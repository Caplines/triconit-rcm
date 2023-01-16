package com.tricon.rcm.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.tricon.rcm.dto.RcmClaimMainRootDto;
import com.tricon.rcm.dto.RcmInsuranceDatas;
import com.tricon.rcm.dto.RcmInsuranceMainRootDto;
import com.tricon.rcm.dto.RcmRemoteLiteMainRootDto;
import com.tricon.rcm.dto.RcmRemoteLiteSiteDetailsDto;
import com.tricon.rcm.dto.RemoteLiteDataDto;
import com.tricon.rcm.dto.RemoteLiteDto;
import com.tricon.rcm.enums.ClaimSourceEnum;
import com.tricon.rcm.enums.ClaimTypeEnum;
import com.tricon.rcm.enums.RcmTeamEnum;
import com.tricon.rcm.jpa.repository.RCMUserRepository;
import com.tricon.rcm.jpa.repository.RcmClaimAssignmentRepo;
import com.tricon.rcm.jpa.repository.RcmClaimLogRepo;
import com.tricon.rcm.jpa.repository.RcmClaimRepository;
import com.tricon.rcm.jpa.repository.RcmClaimStatusTypeRepo;
import com.tricon.rcm.jpa.repository.RcmEagleSoftDBDetailsRepo;
import com.tricon.rcm.jpa.repository.RcmInsuranceRepo;
import com.tricon.rcm.jpa.repository.RcmInsuranceTypeRepo;
import com.tricon.rcm.jpa.repository.RcmMappingTableRepo;
import com.tricon.rcm.jpa.repository.RcmOfficeRepository;
import com.tricon.rcm.jpa.repository.RcmRemoteLiteRepo;
import com.tricon.rcm.jpa.repository.RcmTeamRepo;
import com.tricon.rcm.util.ClaimUtil;
import com.tricon.rcm.util.ConnectAndReadSheets;
import com.tricon.rcm.util.Constants;
import com.google.common.collect.Collections2;
import com.tricon.rcm.db.entity.RcmClaimAssignment;
import com.tricon.rcm.db.entity.RcmClaimLog;
import com.tricon.rcm.db.entity.RcmClaimStatusType;
import com.tricon.rcm.db.entity.RcmClaims;
import com.tricon.rcm.db.entity.RcmInsurance;
import com.tricon.rcm.db.entity.RcmMappingTable;
import com.tricon.rcm.db.entity.RcmOffice;
import com.tricon.rcm.db.entity.RcmRemoteStatusCount;
import com.tricon.rcm.db.entity.RcmTeam;
import com.tricon.rcm.db.entity.RcmUser;
import com.tricon.rcm.dto.ClaimSourceDto;
import com.tricon.rcm.dto.ClaimsFromRuleEngine;
import com.tricon.rcm.dto.InsuranceFromRuleEngine;
import com.tricon.rcm.dto.InsuranceNameTypeDto;
import com.tricon.rcm.dto.RcmClaimDataDto;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

@Service
public class RuleEngineService {

	private final Logger logger = LoggerFactory.getLogger(RuleEngineService.class);

	@Autowired
	Environment ev;

	@Value("${google.credential.folder}")
	private String CREDENTIALS_FOLDER;

	@Value("${google.client.secret}")
	private String CLIENT_SECRET_DIR;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	RCMUserRepository userRepo;

	@Autowired
	RcmInsuranceRepo insuranceRepo;

	@Autowired
	RcmOfficeRepository officeRepo;

	@Autowired
	RcmRemoteLiteRepo rcmRemoteLiteRepo;

	@Autowired
	RcmEagleSoftDBDetailsRepo eagleSoftDBDetailsRepo;

	@Autowired
	RcmInsuranceTypeRepo rcmInsuranceTypeRepo;

	@Autowired
	RcmMappingTableRepo rcmMappingTableRepo;

	@Autowired
	RcmClaimRepository rcmClaimRepository;

	@Autowired
	RcmTeamRepo rcmTeamRepo;

	@Autowired
	RcmClaimStatusTypeRepo rcmClaimStatusTypeRepo;

	@Autowired
	CommonClaimServiceImpl commonClaimServiceImpl;
	
	@Autowired
	RcmClaimLogRepo rcmClaimLogRepo;
	
	@Autowired
	RcmClaimAssignmentRepo rcmClaimAssignmentRepo;

	HttpHeaders headers = null;

	@PostConstruct
	public void init() {

		headers = new HttpHeaders() {
			{
				// set("Accept-Language", "en");
				// set("Content-Type", "application/json");
				set("x-api-key", ev.getProperty("rmc.auth.token"));

			}
		};

	}

	/**
	 * Pull and Save Claims From Rule Engine
	 * 
	 * @param dto
	 * @param user
	 * @return
	 */
	public int pullAndSaveClaimFromRE(ClaimSourceDto dto, RcmUser user) {

		String success = Constants.ClAIM_PULLED_SUCCESS;
		int logId=-1;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			user = userRepo.findByEmail(authentication.getName());
		}
		logger.info(" In pullClaimFromRE");
		RcmClaimMainRootDto mainRoot = null;
		RcmOffice off = officeRepo.findByUuid(dto.getOfficeuuid());
		RcmUser assingedOfficeUser =null;//Once Api is Done implement it.
		RcmClaimAssignment rcmAssigment=null;
		int newClaimCt = 0;
		int newClaimPCt = 0;
		int newClaimSCt = 0;
		int logStatus=0;
		try {
			List<RcmTeam> team = rcmTeamRepo.findAll();
			RcmClaimStatusType systemStatus = rcmClaimStatusTypeRepo.findByStatus(Constants.billingClaim);
			dto.setPassword(eagleSoftDBDetailsRepo.findByOffice(off).getPassword());
			HttpEntity<String> entity = new HttpEntity<String>(headers);
			String param = "?password=" + dto.getPassword();
			if (dto.getOfficeuuid() != null)
				param = param + "&office=" + dto.getOfficeuuid();

			// Call Rule Engine API..
			
			ResponseEntity<String> diagnostic = restTemplate.exchange(ev.getProperty("rcm.diagnosticurl") + param,
					HttpMethod.GET, entity, String.class);
			
			if (diagnostic.getBody()!=null) {
				String diag= diagnostic.getBody();
				if (diag.equals(Constants.socketworkingFine)) {
					logStatus=1;
					ResponseEntity<RcmClaimMainRootDto> result = restTemplate.exchange(
							ev.getProperty("rmc.pullclaimurl") + param, HttpMethod.GET, entity, RcmClaimMainRootDto.class);

					mainRoot = result.getBody();
					RcmClaims claim = null;
					for (RcmClaimDataDto datas : mainRoot.getData().getDatas()) {
						//String officeUuid = datas.getOfficeName();
						// RcmOffice off = officeRepo.findByUuid(officeUuid);
						try {
							for (ClaimsFromRuleEngine re : datas.getData()) {
		                        System.out.println(re.getClaimId()+"--<ID");
		                        List<String> allCl=Arrays.asList(re.getClaimId()+ClaimTypeEnum.P.getSuffix(),re.getClaimId()+ClaimTypeEnum.S.getSuffix());
		    					List<RcmClaims> claims = rcmClaimRepository.findByClaimIdInAndOffice(allCl, off);
								
								if (claims == null || claims.size()==0) {
									//Fresh Claims
									newClaimCt++;
									newClaimPCt++;
									claim = new RcmClaims();
									claim = ClaimUtil.createClaimFromESData(claim, off, re,
											filterTeamByNameId(team, RcmTeamEnum.SYSYEM.toString()), user,
											insuranceRepo.findByInsuranceIdAndOffice(re.getPrimInsuranceCompanyId(), off),
											insuranceRepo.findByInsuranceIdAndOffice(re.getSecInsuranceCompanyId(), off),
											systemStatus,ClaimTypeEnum.P.getSuffix());
									rcmClaimRepository.save(claim);
									///createAssginmentData
									rcmAssigment = new RcmClaimAssignment();
									
									rcmClaimAssignmentRepo.save(rcmAssigment);
									if (re.getSecStatus().equalsIgnoreCase(Constants.secondaryClaimTypeES)) {
									newClaimCt++;	
									newClaimSCt++;
									claim = new RcmClaims();
									claim = ClaimUtil.createClaimFromESData(claim, off, re,
											filterTeamByNameId(team, RcmTeamEnum.SYSYEM.toString()), user,
											insuranceRepo.findByInsuranceIdAndOffice(re.getPrimInsuranceCompanyId(), off),
											insuranceRepo.findByInsuranceIdAndOffice(re.getSecInsuranceCompanyId(), off),
											systemStatus,ClaimTypeEnum.S.getSuffix());
									rcmClaimRepository.save(claim);
									}
								}else {
									//OLD Claims Exists
									boolean secondaryPresent=false;
									boolean primaryPresent=false;
									for(RcmClaims oldClaims:claims) {
										
										if (oldClaims.getClaimId().equalsIgnoreCase(re.getClaimId()+ClaimTypeEnum.P.getSuffix())) {
											primaryPresent=true;
										}
										if (oldClaims.getClaimId().equalsIgnoreCase(re.getClaimId()+ClaimTypeEnum.S.getSuffix())) {
											secondaryPresent=true;
										}
									}
									
									if (!primaryPresent) {
										newClaimCt++;
										newClaimPCt++;
										claim = new RcmClaims();
										claim = ClaimUtil.createClaimFromESData(claim, off, re,
												filterTeamByNameId(team, RcmTeamEnum.SYSYEM.toString()), null,
												insuranceRepo.findByInsuranceIdAndOffice(re.getPrimInsuranceCompanyId(), off),
												insuranceRepo.findByInsuranceIdAndOffice(re.getSecInsuranceCompanyId(), off),
												systemStatus,ClaimTypeEnum.P.getSuffix());
										rcmClaimRepository.save(claim);
										
									}
									if (!secondaryPresent) {
										newClaimCt++;
										newClaimSCt++;
										claim = new RcmClaims();
										claim = ClaimUtil.createClaimFromESData(claim, off, re,
												filterTeamByNameId(team, RcmTeamEnum.SYSYEM.toString()), null,
												insuranceRepo.findByInsuranceIdAndOffice(re.getPrimInsuranceCompanyId(), off),
												insuranceRepo.findByInsuranceIdAndOffice(re.getSecInsuranceCompanyId(), off),
												systemStatus,ClaimTypeEnum.S.getSuffix());
										rcmClaimRepository.save(claim);
									}
						
								
								}
								
							}

						} catch (Exception n1) {
							success = n1.getMessage();
							n1.printStackTrace();
						}

					}
				}
			}	
			//assign Claims left..
			RcmClaimLog log = new RcmClaimLog();
			logId =commonClaimServiceImpl.saveClaimLog(log, user, off, ClaimSourceEnum.EAGLESOFT.toString(), logStatus, newClaimCt,
					newClaimPCt,newClaimSCt);
			
		} catch (Exception n) {
			logger.error("Error in " + dto.getOfficeuuid());
			logger.error(n.getMessage());
			success = n.getMessage();
			n.printStackTrace();
		}
		return logId;
	}

	/**
	 * Pull Insurance From Eagle Soft and Store in "rcm_insurance" table
	 * 
	 * @param dto
	 * @param user
	 * @return
	 */
	public boolean pullAndSaveInsuranceFromRE(ClaimSourceDto dto, RcmUser user) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		List<InsuranceNameTypeDto> insuranceTypeDto = pullInsuranceMappingFromSheet();
		if (authentication != null) {
			user = userRepo.findByEmail(authentication.getName());
		}

		logger.info(" In pull Insurance From RE");
		dto.setPassword(eagleSoftDBDetailsRepo.findByOffice(officeRepo.findByUuid(dto.getOfficeuuid())).getPassword());
		insuranceRepo.inActiveAllInsuranceByOffice(dto.getOfficeuuid());
		try {
			HttpEntity<String> entity = new HttpEntity<String>(headers);
			String param = "?password=" + dto.getPassword();
			if (dto.getOfficeuuid() != null)
				param = param + "&office=" + dto.getOfficeuuid();
			// Call Rule Engine API..
			ResponseEntity<RcmInsuranceMainRootDto> result = restTemplate.exchange(
					ev.getProperty("rmc.pullInsuranceUrl") + param, HttpMethod.GET, entity,
					RcmInsuranceMainRootDto.class);

			RcmInsuranceMainRootDto rootDto = result.getBody();
			RcmInsurance insurance = null;
			RcmInsurance insuranceOld = null;

			for (RcmInsuranceDatas datas : rootDto.getData().getDatas()) {
				String OfficeUuid = datas.getOfficeName();
				try {
					for (InsuranceFromRuleEngine re : datas.getData()) {
						insurance = new RcmInsurance();
						insurance.setCreatedBy(user);

						insurance.setInsuranceId(re.getInsuranceCompanyId());
						insurance.setName(re.getName());
						insurance.setOffice(officeRepo.findByUuid(OfficeUuid));

						String insuranceType = getInsuranceTypeFromSheetList(insuranceTypeDto, re.getName());
						if (insuranceType != null) {
							insurance.setInsuranceType(rcmInsuranceTypeRepo.findByName(insuranceType));
						}
						insuranceOld = insuranceRepo.findByInsuranceIdAndOffice(re.getInsuranceCompanyId(),
								insurance.getOffice());
						insurance.setActive(true);
						if (insuranceOld == null)
							insuranceRepo.save(insurance);
						else {
							insuranceType = getInsuranceTypeFromSheetList(insuranceTypeDto, insurance.getName());
							if (insuranceType != null) {
								insuranceOld.setInsuranceType(rcmInsuranceTypeRepo.findByName(insuranceType));
							}
							insuranceOld.setUpdatedBy(user);
							// insuranceOld.setOffice(officeRepo.findByUuid(OfficeUuid));
							insuranceOld.setName(insurance.getName());
							insuranceOld.setUpdatedDate(new Date());
							insuranceOld.setActive(true);
							try {
								insuranceRepo.save(insuranceOld);
							} catch (Exception s) {
								s.printStackTrace();
							}
						}
					}
				} catch (Exception n) {

				}
			}

		} catch (Exception n) {
			logger.error("Error in " + dto.getOfficeuuid());
			logger.error(n.getMessage());
		}

		// System.out.println(rootDto.getData().getDatas().get(0).getName());

		return true;
	}

	/**
	 * Pull Data From Remote Lite Google sheet and Count the data of
	 * rejected/duplicate/Printed/Accepted and store in "rcm_remote_lite_count"
	 * Table
	 * 
	 * @param dto
	 * @param user
	 * @return
	 */
	public HashMap<String, RemoteLiteDataDto> pullAndSaveRemoteLiteData(ClaimSourceDto dto, RcmUser user,int rcmClaimLogId) {

		logger.info(" In pullRemoteLiteDate");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			user = userRepo.findByEmail(authentication.getName());
		}
		RcmClaimLog log=null;
		try {
		log=rcmClaimLogRepo.findById(Integer.valueOf(rcmClaimLogId)).get();
		}catch(Exception n) {
			logger.info("RcmClaimLog nort found for id -"+rcmClaimLogId);
		}
		
		HashMap<String, RemoteLiteDataDto> map = new HashMap<>();
		try {
			HttpEntity<String> entity = new HttpEntity<String>(headers);

			String param = "";// "?password=" + dto.getPassword();
			if (dto.getOfficeuuid() != null)
				param = param + "?office=" + dto.getOfficeuuid();

			// Call Rule Engine API..
			ResponseEntity<RcmRemoteLiteMainRootDto> result = restTemplate.exchange(
					ev.getProperty("rcm.pullremoteliteurl") + param, HttpMethod.GET, entity,
					RcmRemoteLiteMainRootDto.class);

			System.out.println(result.getBody());
			RcmRemoteLiteMainRootDto rootDto = result.getBody();
			try {
				List<RcmRemoteLiteSiteDetailsDto> data = rootDto.getData();
				for (RcmRemoteLiteSiteDetailsDto dto1 : data) {
					RemoteLiteDataDto dataDtao = ConnectAndReadSheets.readRemoteLiteSheet(dto1.getGoogleSheetIdDb(),
							dto1.getPassword(), CLIENT_SECRET_DIR, CREDENTIALS_FOLDER);
					map.put(dto1.getOfficeId(), dataDtao);

					RcmOffice office = officeRepo.findByUuid(dto1.getOfficeId());
					RcmRemoteStatusCount ct = new RcmRemoteStatusCount();
					BeanUtils.copyProperties(dataDtao.getStatusCount(), ct);
					// ct.setCreatedDate(new Date());
					ct.setOffice(office);
					ct.setCreatedBy(user);
					ct.setRcmClaimLog(log);
					rcmRemoteLiteRepo.save(ct);

				}

			} catch (Exception n) {
				n.printStackTrace();

			}
		} catch (Exception n) {
			logger.error("Error in " + dto.getOfficeuuid());
			logger.error(n.getMessage());
		}

		return map;
	}

	/**
	 * Pull Insurance Mapping Data From google Sheet
	 * 
	 * @return
	 */
	private List<InsuranceNameTypeDto> pullInsuranceMappingFromSheet() {

		logger.info(" In pullInsuranceMappingFromSheet");
		RcmMappingTable table = rcmMappingTableRepo.findByName(Constants.RCM_MAPPING_INSURANCE);
		List<InsuranceNameTypeDto> li = null;
		try {
			li = ConnectAndReadSheets.readInsuranceMappingSheet(table.getGoogleSheetId(), table.getGoogleSheetSubName(),
					CLIENT_SECRET_DIR, CREDENTIALS_FOLDER);

		} catch (Exception n) {
			logger.error("Error in Fetching Insurance ");
			logger.error(n.getMessage());
		}

		return li;
	}

	/**
	 * Filter Insurance Type from Google Sheet based on Insurance Name
	 * 
	 * @param sheetData
	 * @param name
	 * @return
	 */
	private String getInsuranceTypeFromSheetList(List<InsuranceNameTypeDto> sheetData, String name) {
		String insuranceType = null;
		if (sheetData == null) {
			logger.error("Data From Mapping sheet not found");
			return null;
		}
		Collection<InsuranceNameTypeDto> ruleGen = Collections2.filter(sheetData,
				sh -> sh.getInsuranceName().equals(name));
		for (InsuranceNameTypeDto gs : ruleGen) {
			insuranceType = gs.getInsuranceType();
			break;
		}
		if (insuranceType == null) {
			logger.error(name + " Not found in  Google sheet");

		}
		return insuranceType;
	}

	private RcmTeam filterTeamByNameId(List<RcmTeam> rcmTeamList, String teamNameId) {

		Collection<RcmTeam> ruleGen = Collections2.filter(rcmTeamList, sh -> sh.getNameId().equals(teamNameId));
		return ruleGen.iterator().next();

	}

}
