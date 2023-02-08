package com.tricon.rcm.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.tricon.rcm.dto.RcmClaimMainRootDto;
import com.tricon.rcm.dto.RcmInsuranceDatas;
import com.tricon.rcm.dto.RcmInsuranceMainRootDto;
import com.tricon.rcm.dto.RemoteLietStatusCount;
import com.tricon.rcm.dto.TimelyFilingLimitDto;
import com.tricon.rcm.enums.ClaimSourceEnum;
import com.tricon.rcm.enums.ClaimStatusEnum;
import com.tricon.rcm.enums.ClaimTypeEnum;
import com.tricon.rcm.enums.RcmTeamEnum;
import com.tricon.rcm.jpa.repository.RCMUserRepository;
import com.tricon.rcm.jpa.repository.RcmClaimAssignmentRepo;
import com.tricon.rcm.jpa.repository.RcmClaimLogRepo;
import com.tricon.rcm.jpa.repository.RcmClaimRepository;
import com.tricon.rcm.jpa.repository.RcmClaimStatusTypeRepo;
import com.tricon.rcm.jpa.repository.RcmCompanyRepo;
import com.tricon.rcm.jpa.repository.RcmEagleSoftDBDetailsRepo;
import com.tricon.rcm.jpa.repository.RcmInsuranceRepo;
import com.tricon.rcm.jpa.repository.RcmInsuranceTypeRepo;
import com.tricon.rcm.jpa.repository.RcmIssueClaimsRepo;
import com.tricon.rcm.jpa.repository.RcmMappingTableRepo;
import com.tricon.rcm.jpa.repository.RcmOfficeRepository;
import com.tricon.rcm.jpa.repository.RcmRemoteLiteRepo;
import com.tricon.rcm.jpa.repository.UserAssignOfficeRepo;
import com.tricon.rcm.jpa.repository.RcmTeamRepo;
import com.tricon.rcm.util.ClaimUtil;
import com.tricon.rcm.util.ConnectAndReadSheets;
import com.tricon.rcm.util.Constants;
import com.google.common.collect.Collections2;
import com.tricon.rcm.db.entity.RcmClaimAssignment;
import com.tricon.rcm.db.entity.RcmClaimLog;
import com.tricon.rcm.db.entity.RcmClaimStatusType;
import com.tricon.rcm.db.entity.RcmClaims;
import com.tricon.rcm.db.entity.RcmCompany;
import com.tricon.rcm.db.entity.RcmInsurance;
import com.tricon.rcm.db.entity.RcmInsuranceType;
import com.tricon.rcm.db.entity.RcmIssueClaims;
import com.tricon.rcm.db.entity.RcmMappingTable;
import com.tricon.rcm.db.entity.RcmOffice;

import com.tricon.rcm.db.entity.RcmTeam;
import com.tricon.rcm.db.entity.RcmUser;
import com.tricon.rcm.db.entity.UserAssignOffice;
import com.tricon.rcm.dto.ClaimSourceDto;
import com.tricon.rcm.dto.ClaimsFromRuleEngine;
import com.tricon.rcm.dto.InsuranceFromRuleEngine;
import com.tricon.rcm.dto.InsuranceNameTypeDto;
import com.tricon.rcm.dto.RcmClaimDataDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

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
	UserAssignOfficeRepo userAssignOfficeRepo;

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

	@Autowired
	RcmCompanyRepo compRepo;
	
	@Autowired
	RcmIssueClaimsRepo rcmIssueClaimsRepo;
	
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
	public String pullAndSaveClaimFromRE(ClaimSourceDto dto, RcmUser user,
			List<TimelyFilingLimitDto> timelyFilingLimits,ClaimTypeEnum claimTypeEnum,RcmCompany comp,int logId) {

		String success = "";
		//int logId = -1;
		String source=ClaimSourceEnum.EAGLESOFT.toString();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		RcmInsurance ins=null;
		if (authentication != null) {
			user = userRepo.findByEmail(authentication.getName());
		}
		logger.info(" In pullClaimFrom RE");
		RcmClaimMainRootDto mainRoot = null;
		RcmOffice off = officeRepo.findByUuid(dto.getOfficeuuid());
		// RcmUser assingedOfficeUser =null;//Once Api is Done implement it.
		RcmClaimAssignment rcmAssigment = null;
		int newClaimCt = 0;
		int newClaimPCt = 0;
		int newClaimSCt = 0;
		int logStatus = 0;
		try {
			
			List<InsuranceNameTypeDto> insuranceTypeDto = pullInsuranceMappingFromSheet(comp);
			
			List<RcmTeam> allTeams = rcmTeamRepo.findAll();
			RcmClaimStatusType systemStatusBilling = rcmClaimStatusTypeRepo.findByStatus(ClaimStatusEnum.Billing.getType());
			dto.setPassword(eagleSoftDBDetailsRepo.findByOffice(off).getPassword());
			HttpEntity<String> entity = new HttpEntity<String>(headers);
			String param = "?password=" + dto.getPassword()+"&primarySecondary="+claimTypeEnum.getType();
			if (dto.getOfficeuuid() != null)
				param = param + "&office=" + dto.getOfficeuuid();

			// Call Rule Engine API..

			ResponseEntity<String> diagnostic = restTemplate.exchange(ev.getProperty("rcm.diagnosticurl") + param,
					HttpMethod.GET, entity, String.class);

			if (diagnostic.getBody() != null) {
				String diag = diagnostic.getBody();
				if (diag.equals(Constants.socketworkingFine)) {
					logStatus = 1;
					ResponseEntity<RcmClaimMainRootDto> result = restTemplate.exchange(
							ev.getProperty("rmc.pullclaimurl") + param, HttpMethod.GET, entity,
							RcmClaimMainRootDto.class);

					mainRoot = result.getBody();
					RcmClaims claim = null;
					for (RcmClaimDataDto datas : mainRoot.getData().getDatas()) {
						// String officeUuid = datas.getOfficeName();
						// RcmOffice off = officeRepo.findByUuid(officeUuid);
						try {
							for (ClaimsFromRuleEngine re : datas.getData()) {
								try {
									System.out.println(re.getClaimId() + "--<ID");
									List<String> allCl = Arrays.asList(re.getClaimId() + claimTypeEnum.getSuffix());
									List<RcmClaims> claims = rcmClaimRepository.findByClaimIdInAndOffice(allCl, off);
									UserAssignOffice assignedUser = userAssignOfficeRepo
											.findByOfficeUuidAndTeamId(off.getUuid(), RcmTeamEnum.BILLING.getId());

									RcmInsuranceType rcmInsuranceType = null;
									if (claims == null || claims.size() == 0) {
										List<String> buildError=null;
										// Fresh Claims
									
										claim = new RcmClaims();
										List<String> error= new ArrayList<>();
										System.out.println(re.getPrimSecInsuranceCompanyId());
										ins = insuranceRepo
												.findByInsuranceIdAndOffice(re.getPrimSecInsuranceCompanyId(), off);
										
										if (ins==null) {
											ins=  new RcmInsurance();
											ins.setActive(true);
											ins.setAddress(re.getInsuranceCompanyFullAddress());
											ins.setInsuranceId(re.getPrimSecInsuranceCompanyId());
											String insuranceType = getInsuranceTypeFromSheetList(insuranceTypeDto, re.getInsuranceCompanyName());
											
											ins.setInsuranceType(rcmInsuranceTypeRepo.findByName(insuranceType));
											ins.setName(re.getInsuranceCompanyName());
											ins.setOffice(off);
											if (insuranceType==null) {
												error.add( "Insurance Type Missing For for Name:"+re.getInsuranceCompanyName() +" in G-Sheet");
											}else {
											ins.setId(insuranceRepo.save(ins).getId());
											}
										    //error.add( "Primary Insurance Missing for id:"+re.getPrimInsuranceCompanyId());
										}
										//if (primaryIns.getInsuranceType()==null) {
										   // error.add("Primary Insurance Type Missing for id:"+re.getPrimInsuranceCompanyId()+" and Name: "+primaryIns.getName());
										//}
										
										String timely =ClaimUtil.getTimelyLimitFromSheetList(timelyFilingLimits, re.getInsuranceCompanyName());
										if (timely==null) {
											error.add("Timely Limit Type Missing for Primary Ins. :"+ins.getName());		    
													    
										}
										
										//int insuranceId = primaryIns.getInsuranceType().getId();
										//rcmInsuranceType = rcmInsuranceTypeRepo.findById(insuranceId);
									//	if (rcmInsuranceType==null) {
										//	error.add("Primary Ins. missing For:"+re.getPrimInsuranceCompanyId());		    
													    
										//}
										if (error.size()>0) {
											 saveRcmIssueClaim(re, buildError, off,user,
													 String.join("\n", error),source,claimTypeEnum);
											 continue;
										}
										newClaimCt++;
										if (claimTypeEnum.getType().equals(Constants.insuranceTypePrimary))newClaimPCt++;
										if (claimTypeEnum.getType().equals(Constants.insuranceTypeSecondary))newClaimSCt++;
										rcmInsuranceType = rcmInsuranceTypeRepo.findById(ins.getInsuranceType().getId());
										claim = ClaimUtil.createClaimFromESData(claim, off, re,
												ClaimUtil.filterTeamByNameId(allTeams, RcmTeamEnum.BILLING.toString()), user,
												ins,ins,
												systemStatusBilling, claimTypeEnum.getSuffix(), rcmInsuranceType,
												timely,claimTypeEnum);
										String claimUUid = rcmClaimRepository.save(claim).getClaimUuid();
										RcmIssueClaims isC=rcmIssueClaimsRepo.findByClaimIdAndOfficeAndSource(re.getClaimId(), off,source);
									    if (isC!=null){
											isC.setResolved(true);
											rcmIssueClaimsRepo.save(isC);
										}
										/// createAssginmentData
										if (assignedUser != null) {
											rcmAssigment = new RcmClaimAssignment();
											//
											rcmAssigment = ClaimUtil.createAssginmentData(rcmAssigment, user,
													assignedUser.getUser(), claimUUid, claim,
													Constants.SYSTEM_INITIAL_COMMENT, systemStatusBilling);

											rcmClaimAssignmentRepo.save(rcmAssigment);
										}
										/*
										if (re.getSecStatus().equalsIgnoreCase(Constants.secondaryClaimTypeES)) {
											newClaimCt++;
											newClaimSCt++;
											claim = new RcmClaims();
											error= new ArrayList<>();
											RcmInsurance secondarySec = insuranceRepo
													.findByInsuranceIdAndOffice(re.getSecInsuranceCompanyId(), off);
											
											if (secondarySec==null && !re.getSecInsuranceCompanyId().equals(Constants.NO_DATA)) {
												error.add("Secondary Insurance Missing for id:"+re.getSecInsuranceCompanyId());
											}
											if (secondarySec.getInsuranceType()==null && !re.getSecInsuranceCompanyId().equals(Constants.NO_DATA)) {
												error.add("Secondary Insurance Type Missing for id:"+re.getSecInsuranceCompanyId()+" and Name: "+secondarySec.getName());
											}
											
											timely =ClaimUtil.getTimelyLimitFromSheetList(timelyFilingLimits, secondarySec.getName() );
											if (timely==null) {
												error.add("Timely Limit Type Missing for Secondary Ins. :"+secondarySec.getName());
											}
											
											
											insuranceId = secondarySec.getInsuranceType().getId();
											rcmInsuranceType = rcmInsuranceTypeRepo.findById(insuranceId);
											
											if (rcmInsuranceType==null) {
												error.add("Secondary Ins. missing For:"+re.getSecInsuranceCompanyId());		    
														    
											}
											if (error.size()>0) {
												 saveRcmIssueClaim(re, buildError, off,user,
														 String.join("\n", error),source);
												 continue;
											}
											
											
											claim = ClaimUtil.createClaimFromESData(claim, off, re,
													ClaimUtil.filterTeamByNameId(allTeams, RcmTeamEnum.BILLING.toString()), user,
													insuranceRepo.findByInsuranceIdAndOffice(
															re.getPrimInsuranceCompanyId(), off),
													secondarySec, systemStatusBilling, ClaimTypeEnum.S.getSuffix(),
													rcmInsuranceType,timely);
											claimUUid = rcmClaimRepository.save(claim).getClaimUuid();
											if (assignedUser != null) {
												rcmAssigment = new RcmClaimAssignment();
												rcmAssigment = ClaimUtil.createAssginmentData(rcmAssigment, user,
														assignedUser.getUser(), claimUUid, claim,
														Constants.SYSTEM_INITIAL_COMMENT, systemStatusBilling);
												rcmClaimAssignmentRepo.save(rcmAssigment);
											}
										}
										*/
										if (isC!=null){
											isC.setResolved(true);
											rcmIssueClaimsRepo.save(isC);
										}
									} else {
										// OLD Claims Exists
										/*
										boolean secondaryPresent = false;
										boolean primaryPresent = false;
										List<String> buildError=null;
										for (RcmClaims oldClaims : claims) {

											if (oldClaims.getClaimId()
													.equalsIgnoreCase(re.getClaimId() + ClaimTypeEnum.P.getSuffix())) {
												primaryPresent = true;
											}
											if (oldClaims.getClaimId()
													.equalsIgnoreCase(re.getClaimId() + ClaimTypeEnum.S.getSuffix())) {
												secondaryPresent = true;
											}
										}

										if (!primaryPresent) {
											List<String> error= new ArrayList<>();
											
											newClaimCt++;
											newClaimPCt++;
											claim = new RcmClaims();
											RcmInsurance primarySec = insuranceRepo
													.findByInsuranceIdAndOffice(re.getPrimInsuranceCompanyId(), off);
											if (primarySec==null) {
												error.add("Primary Insurance Missing for id:"+re.getPrimInsuranceCompanyId());
											}
											
											if (primarySec.getInsuranceType()==null) {
												error.add("Primary Insurance Type Missing for id:"+re.getPrimInsuranceCompanyId()+" and Name: "+primarySec.getName());
											}
											String timely =ClaimUtil.getTimelyLimitFromSheetList(timelyFilingLimits, primarySec.getName());
											
											if (timely==null) {
												error.add("Timely Limit Type Missing for Primary Ins. :"+primarySec.getName());
											}
											int insuranceId = primarySec.getInsuranceType().getId();
											rcmInsuranceType = rcmInsuranceTypeRepo.findById(insuranceId);
											
											if (rcmInsuranceType==null) {
												error.add("Primary Ins. missing For:"+re.getPrimInsuranceCompanyId());		    
														    
											}
											if (error.size()>0) {
												 saveRcmIssueClaim(re, buildError, off,user,
														 String.join("\n", error),source);
												 continue;
											}
											claim = ClaimUtil.createClaimFromESData(claim, off, re,
													ClaimUtil.filterTeamByNameId(allTeams, RcmTeamEnum.BILLING.toString()), user,
													insuranceRepo.findByInsuranceIdAndOffice(
															re.getPrimInsuranceCompanyId(), off),
													insuranceRepo.findByInsuranceIdAndOffice(
															re.getSecInsuranceCompanyId(), off),
													systemStatusBilling, ClaimTypeEnum.P.getSuffix(), rcmInsuranceType,timely);
											String claimUUid = rcmClaimRepository.save(claim).getClaimUuid();
											RcmIssueClaims isC=rcmIssueClaimsRepo.findByClaimIdAndOfficeAndSource(re.getClaimId(), off,source);
										    if (isC!=null){
												isC.setResolved(true);
												rcmIssueClaimsRepo.save(isC);
											}
											if (assignedUser != null) {
												rcmAssigment = new RcmClaimAssignment();
												rcmAssigment = ClaimUtil.createAssginmentData(rcmAssigment, user,
														assignedUser.getUser(), claimUUid, claim,
														Constants.SYSTEM_INITIAL_COMMENT, systemStatusBilling);
												rcmClaimAssignmentRepo.save(rcmAssigment);
											}
										}
										if (!secondaryPresent) {
											newClaimCt++;
											newClaimSCt++;
											claim = new RcmClaims();
											List<String> error= new ArrayList<>();
											RcmInsurance secondarySec = insuranceRepo
													.findByInsuranceIdAndOffice(re.getSecInsuranceCompanyId(), off);
											if (secondarySec==null && !re.getSecInsuranceCompanyId().equals(Constants.NO_DATA)) {
											    error.add("Secondary Insurance Missing for id:"+re.getSecInsuranceCompanyId());
											}
											
											if (secondarySec!=null && (secondarySec.getInsuranceType()==null || !re.getSecInsuranceCompanyId().equals(Constants.NO_DATA))) {
												error.add("Secondary Insurance Type Missing for id:"+re.getSecInsuranceCompanyId()+" and Name: "+secondarySec.getName());
											}
											
											String timely =null;
											if (secondarySec!=null) ClaimUtil.getTimelyLimitFromSheetList(timelyFilingLimits, secondarySec.getName() );
											if (timely==null && secondarySec!=null) {
												error.add("Timely Limit Type Missing for Secondary Ins. :"+secondarySec.getName());
											}
											
											int insuranceId =secondarySec!=null?secondarySec.getInsuranceType().getId():-1;
											rcmInsuranceType = rcmInsuranceTypeRepo.findById(insuranceId);
											
											if (rcmInsuranceType==null) {
												error.add("Secondary Ins. missing For:"+re.getSecInsuranceCompanyId());		    
														    
											}
											if (error.size()>0) {
												 saveRcmIssueClaim(re, buildError, off,user,
														 String.join("\n", error),source);
												 continue;
											}
											
											claim = ClaimUtil.createClaimFromESData(claim, off, re,
													ClaimUtil.filterTeamByNameId(allTeams, RcmTeamEnum.BILLING.toString()), user,
													insuranceRepo.findByInsuranceIdAndOffice(
															re.getPrimInsuranceCompanyId(), off),
													insuranceRepo.findByInsuranceIdAndOffice(
															re.getSecInsuranceCompanyId(), off),
													systemStatusBilling, ClaimTypeEnum.S.getSuffix(), rcmInsuranceType,
													timely);
											String claimUUid = rcmClaimRepository.save(claim).getClaimId();
											RcmIssueClaims isC=rcmIssueClaimsRepo.findByClaimIdAndOfficeAndSource(re.getClaimId(), off,source);
										    if (isC!=null){
												isC.setResolved(true);
												rcmIssueClaimsRepo.save(isC);
											}
											if (assignedUser != null) {
												rcmAssigment = new RcmClaimAssignment();
												rcmAssigment = ClaimUtil.createAssginmentData(rcmAssigment, user,
														assignedUser.getUser(), claimUUid, claim,
														Constants.SYSTEM_INITIAL_COMMENT, systemStatusBilling);
												rcmClaimAssignmentRepo.save(rcmAssigment);
											}
										}
                                          */
									}

								} catch (Exception clai) {
									// success = n1.getMessage();
									clai.printStackTrace();
									success = success + "," + re.getClaimId();
									logger.error(clai.getMessage());
								}
							}

						} catch (Exception n1) {
							// success = n1.getMessage();
							n1.printStackTrace();
							logger.error(n1.getMessage());
						}

					}
				}else {
					//Office Not working ...
					
				}
			}

			if (success.equals(""))
				success = Constants.ClAIM_PULLED_SUCCESS;
			RcmClaimLog log = null;///new RcmClaimLog();
			
			if (logId !=-1 ) {
				Optional<RcmClaimLog> opt= rcmClaimLogRepo.findById(logId);
				if (opt.isPresent()) log =opt.get();
			}
			if (log==null) { 
				log= new RcmClaimLog();
			     logId = commonClaimServiceImpl.saveClaimLog(log, user, off, ClaimSourceEnum.EAGLESOFT.toString(), logStatus,
					newClaimCt, newClaimPCt, newClaimSCt, success);
			}else {
				log.setNewClaimsCount(newClaimCt+log.getNewClaimsCount());
				log.setNewClaimsPrimaryCount(newClaimPCt+log.getNewClaimsPrimaryCount());
				log.setNewClaimsSecodaryCount(newClaimSCt+log.getNewClaimsSecodaryCount());
				
				rcmClaimLogRepo.save(log);
			}

		} catch (Exception n) {
			logger.error("Error in " + dto.getOfficeuuid());
			logger.error(n.getMessage());
			// success = n.getMessage();
			n.printStackTrace();
		}
		return success + "___" + logId;
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
		List<InsuranceNameTypeDto> insuranceTypeDto = pullInsuranceMappingFromSheet(
				compRepo.findByUuid(dto.getCompanyuuid()));
		if (authentication != null) {
			user = userRepo.findByEmail(authentication.getName());
		}

		logger.info(" In pull Insurance From RE");
		dto.setPassword(eagleSoftDBDetailsRepo.findByOffice(officeRepo.findByUuid(dto.getOfficeuuid())).getPassword());
		
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
			if (rootDto.getData().getDatas()!=null) {
			insuranceRepo.inActiveAllInsuranceByOffice(dto.getOfficeuuid()); 
			}
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
	public HashMap<String,RemoteLietStatusCount> pullAndSaveRemoteLiteData() {

		logger.info(" In pullRemoteLiteDate");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			//user = userRepo.findByEmail(authentication.getName());
		}
		//RcmClaimLog log = null;
		//RcmRemoteStatusCount ct = new RcmRemoteStatusCount();
		//RcmRemoteStatusCountDto ctDto = new RcmRemoteStatusCountDto();
		//if (rcmClaimLogId != -1) {
	//		try {
		//		log = rcmClaimLogRepo.findById(Integer.valueOf(rcmClaimLogId)).get();
		//	} catch (Exception n) {
			//	logger.info("RcmClaimLog not found for id -" + rcmClaimLogId);
			//}
		//}
		HashMap<String,RemoteLietStatusCount> map = new HashMap<>();
		try {
			/*
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
			*/
			try {
				/*List<RcmRemoteLiteSiteDetailsDto> data = rootDto.getData();
				for (RcmRemoteLiteSiteDetailsDto dto1 : data) {
					RemoteLiteDataDto dataDtao = ConnectAndReadSheets.readRemoteLiteSheet(dto1.getGoogleSheetIdDb(),
							dto1.getPassword(), CLIENT_SECRET_DIR, CREDENTIALS_FOLDER);
					map.put(dto1.getOfficeId(), dataDtao);

					RcmOffice office = officeRepo.findByUuid(dto1.getOfficeId());

					BeanUtils.copyProperties(dataDtao.getStatusCount(), ct);
					BeanUtils.copyProperties(dataDtao.getStatusCount(), ctDto);
					// ct.setCreatedDate(new Date());
					ct.setOffice(office);
					ct.setCreatedBy(user);
					ct.setRcmClaimLog(log);
					rcmRemoteLiteRepo.save(ct);

				}
				*/
				 map= ConnectAndReadSheets.readRemoteLiteSheet("1KVaZbAfaYOGMbYRZuGH-4VVxeZQPIvML0YThPsPNTnw",
						"Combined", CLIENT_SECRET_DIR, CREDENTIALS_FOLDER);
				//map.put(dto1.getOfficeId(), dataDtao);
				

			} catch (Exception n) {
				n.printStackTrace();

			}
		} catch (Exception n) {
			//logger.error("Error in " + dto.getOfficeuuid());
			//logger.error(n.getMessage());
		}

		return map;
	}
	
	
	public List<TimelyFilingLimitDto> pullTimelyFilingLmtMappingFromSheet(RcmCompany company) {

		logger.info(" In TimelyFilingLimitDto");
		RcmMappingTable table = rcmMappingTableRepo.findByNameAndCompany(Constants.RCM_MAPPING_TIMELY_LIMIT, company);
		List<TimelyFilingLimitDto> li = null;
		try {
			li = ConnectAndReadSheets.readTimelyFilingLimitMappingSheet(table.getGoogleSheetId(), table.getGoogleSheetSubName(),
					CLIENT_SECRET_DIR, CREDENTIALS_FOLDER);

		} catch (Exception n) {
			logger.error("Error in Fetching TimelyFilingLimi ");
			logger.error(n.getMessage());
		}

		return li;
	}
	
	private void saveRcmIssueClaim(ClaimsFromRuleEngine re,List<String> buildError,RcmOffice off,
			RcmUser user,String error,String source,ClaimTypeEnum claimTypeEnum) {
		
		RcmIssueClaims isC=rcmIssueClaimsRepo.findByClaimIdAndOfficeAndSource(re.getClaimId()+claimTypeEnum.getSuffix(), off,source);
	      if (buildError==null) buildError=new ArrayList<>();
		    buildError.add(error);
		    if (isC==null){
		    	isC = new RcmIssueClaims();
		    	isC.setClaimId(re.getClaimId()+claimTypeEnum.getSuffix());
		    	isC.setOffice(off);
		    	isC.setSource(source);
		    	isC.setResolved(false);
		    	isC.setIssue(String.join(",", buildError));
		    	isC.setCreatedBy(user);
		    	rcmIssueClaimsRepo.save(isC);
		    }
		    else {
		    	isC.setIssue(String.join(",", buildError));
		    	isC.setResolved(false);
		    	isC.setUpdatedBy(user);
		    	rcmIssueClaimsRepo.save(isC);
		    }
	}

	/**
	 * Pull Insurance Mapping Data From google Sheet
	 * 
	 * @return
	 */
	private List<InsuranceNameTypeDto> pullInsuranceMappingFromSheet(RcmCompany company) {

		logger.info(" In pullInsuranceMappingFromSheet");
		RcmMappingTable table = rcmMappingTableRepo.findByNameAndCompany(Constants.RCM_MAPPING_INSURANCE, company);
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
				sh -> sh.getInsuranceName().equalsIgnoreCase(name));
		for (InsuranceNameTypeDto gs : ruleGen) {
			insuranceType = gs.getInsuranceType();
			break;
		}
		if (insuranceType == null) {
			logger.error(name + " Not found in  Google sheet");

		}
		return insuranceType;
	}
	
	

	

}
