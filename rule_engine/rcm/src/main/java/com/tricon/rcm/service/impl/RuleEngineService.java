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
import com.tricon.rcm.jpa.repository.RCMUserRepository;
import com.tricon.rcm.jpa.repository.RcmEagleSoftDBDetailsRepo;
import com.tricon.rcm.jpa.repository.RcmInsuranceRepo;
import com.tricon.rcm.jpa.repository.RcmInsuranceTypeRepo;
import com.tricon.rcm.jpa.repository.RcmMappingTableRepo;
import com.tricon.rcm.jpa.repository.RcmOfficeRepository;
import com.tricon.rcm.jpa.repository.RcmRemoteLiteRepo;
import com.tricon.rcm.util.ConnectAndReadSheets;
import com.tricon.rcm.util.Constants;
import com.google.common.collect.Collections2;
import com.tricon.rcm.db.entity.RcmInsurance;
import com.tricon.rcm.db.entity.RcmMappingTable;
import com.tricon.rcm.db.entity.RcmOffice;
import com.tricon.rcm.db.entity.RcmRemoteStatusCount;
import com.tricon.rcm.db.entity.RcmUser;
import com.tricon.rcm.dto.ClaimSourceDto;
import com.tricon.rcm.dto.ClaimsFromRuleEngine;
import com.tricon.rcm.dto.InsuranceFromRuleEngine;
import com.tricon.rcm.dto.InsuranceNameTypeDto;
import com.tricon.rcm.dto.RcmClaimDataDto;

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
	RcmMappingTableRepo  rcmMappingTableRepo;

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

	public RcmClaimMainRootDto pullClaimFromRE(ClaimSourceDto dto,RcmUser user) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication!=null) {
			user=userRepo.findByUserName(authentication.getName());
		}
		logger.info(" In pullClaimFromRE");
		RcmClaimMainRootDto mainRoot=null;
		dto.setPassword(eagleSoftDBDetailsRepo.findByOffice(officeRepo.findByUuid(dto.getOfficeuuid())).getPassword());
		try {
			HttpEntity<String> entity = new HttpEntity<String>(headers);
			String param = "?password=" + dto.getPassword();
			if (dto.getOfficeuuid() != null)
				param = param + "&office=" + dto.getOfficeuuid();

			// Call Rule Engine API..
			ResponseEntity<RcmClaimMainRootDto> result = restTemplate.exchange(
					ev.getProperty("rmc.pullclaimurl") + param, HttpMethod.GET, entity, RcmClaimMainRootDto.class);

			mainRoot = result.getBody();
			/*
			for (RcmClaimDataDto datas : rootDto.getData().getDatas()) {
				String OfficeUuid = datas.getOfficeName();
				try {
					for (ClaimsFromRuleEngine re : datas.getData()) {
						insurance = new RcmInsurance();
						insurance.setInsuranceId(re.getInsuranceCompanyId());
						insurance.setName(re.getName());
						insurance.setOffice(officeRepo.findByUuid(OfficeUuid));
						insuranceOld = insuranceRepo.findByInsuranceId(re.getInsuranceCompanyId());
						if (insuranceOld == null)
							insuranceRepo.save(insurance);
						else {
							insuranceOld.setName(insurance.getName());
							insuranceRepo.save(insuranceOld);
						}
					}
				} catch (Exception n) {

				}
				
			}*/
		} catch (Exception n) {
			logger.error("Error in " + dto.getOfficeuuid());
			logger.error(n.getMessage());
		}
		return mainRoot;
	}

	/**
	 * Pull Insurance From Eagle Soft and Store in "rcm_insurance" table
	 * @param dto
	 * @param user
	 * @return
	 */
	public boolean pullIAndSaveInsuranceFromRE(ClaimSourceDto dto,RcmUser user) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		List<InsuranceNameTypeDto> insuranceTypeDto= pullnsuranceMappingFromSheet();
		if (authentication!=null) {
			user=userRepo.findByUserName(authentication.getName());
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
						if (insuranceType!=null) {
							insurance.setInsuranceType(rcmInsuranceTypeRepo.findByName(insuranceType));
						}
						insuranceOld = insuranceRepo.findByInsuranceIdAndOffice(re.getInsuranceCompanyId(),insurance.getOffice());
						if (insuranceOld == null)
							insuranceRepo.save(insurance);
						else {
							insuranceType = getInsuranceTypeFromSheetList(insuranceTypeDto, insurance.getName());
							if (insuranceType!=null) {
								insuranceOld.setInsuranceType(rcmInsuranceTypeRepo.findByName(insuranceType));
							}
							insuranceOld.setUpdatedBy(user);
							//insuranceOld.setOffice(officeRepo.findByUuid(OfficeUuid));
							insuranceOld.setName(insurance.getName());
							insuranceOld.setUpdatedDate(new Date());
							try {
							insuranceRepo.save(insuranceOld);
							}catch(Exception s) {
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
	 * Pull Data From Remote Lite Google sheet and Count the data of rejected/duplicate/Printed/Accepted
	 * and store in "rcm_remote_lite_count" Table
	 * @param dto
	 * @param user
	 * @return
	 */
	public HashMap<String, RemoteLiteDataDto> pullAndSaveRemoteLiteDate(ClaimSourceDto dto,RcmUser user) {

		logger.info(" In pullRemoteLiteDate");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication!=null) {
			user=userRepo.findByUserName(authentication.getName());
		}
		HashMap<String, RemoteLiteDataDto> map = new HashMap<>();
		try {
			HttpEntity<String> entity = new HttpEntity<String>(headers);

			String param = "";//"?password=" + dto.getPassword();
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
					RemoteLiteDataDto dataDtao=	ConnectAndReadSheets.readRemoteLiteSheet(dto1.getGoogleSheetIdDb(),
							dto1.getPassword(), CLIENT_SECRET_DIR, CREDENTIALS_FOLDER);
					map.put(dto1.getOfficeId(), dataDtao);
					
					RcmOffice office= officeRepo.findByUuid(dto1.getOfficeId());
					RcmRemoteStatusCount ct= new RcmRemoteStatusCount();
					BeanUtils.copyProperties(dataDtao.getStatusCount(), ct);
					//ct.setCreatedDate(new Date());
					ct.setOffice(office);
					ct.setCreatedBy(user);
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
	
	public List<InsuranceNameTypeDto> pullnsuranceMappingFromSheet() {

		logger.info(" In pullnsuranceMappingFromSheet");
		RcmMappingTable table =rcmMappingTableRepo.findByName(Constants.RCM_MAPPING_INSURANCE);
		List<InsuranceNameTypeDto> li = null;
		try {
			li = ConnectAndReadSheets.readInsuranceMappingSheet(table.getGoogleSheetId(),
					table.getGoogleSheetSubName(), CLIENT_SECRET_DIR, CREDENTIALS_FOLDER);
			
		} catch (Exception n) {
			logger.error("Error in Fetching Insurance ");
			logger.error(n.getMessage());
		}
		
		return li;
		}
	
	
	private String getInsuranceTypeFromSheetList(List<InsuranceNameTypeDto> sheetData, String name) {
		String insuranceType = null;
		if (sheetData==null) {
			logger.error("Data From Mapping sheet not found");
			return null;
		}
		Collection<InsuranceNameTypeDto> ruleGen = Collections2.filter(sheetData, sh -> sh.getInsuranceName().equals(name));
		for (InsuranceNameTypeDto gs : ruleGen) {
			insuranceType = gs.getInsuranceType();
			break;
		}
		if (insuranceType==null) {
			logger.error(name + " Not found in  Google sheet");

		}
		return insuranceType;
	}

}
