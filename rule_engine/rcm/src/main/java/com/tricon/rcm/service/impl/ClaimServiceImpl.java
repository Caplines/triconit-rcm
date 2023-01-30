package com.tricon.rcm.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Collections2;
import com.tricon.rcm.db.entity.RcmClaimLog;
import com.tricon.rcm.db.entity.RcmClaimStatusType;
import com.tricon.rcm.db.entity.RcmClaims;
import com.tricon.rcm.db.entity.RcmCompany;
import com.tricon.rcm.db.entity.RcmInsurance;
import com.tricon.rcm.db.entity.RcmInsuranceType;
import com.tricon.rcm.db.entity.RcmMappingTable;
import com.tricon.rcm.db.entity.RcmOffice;
import com.tricon.rcm.db.entity.RcmUser;
import com.tricon.rcm.dto.ClaimFromSheet;
import com.tricon.rcm.dto.ClaimLogDto;
import com.tricon.rcm.dto.ClaimSourceDto;
import com.tricon.rcm.dto.customquery.FreshClaimLogDto;
import com.tricon.rcm.dto.InsuranceNameTypeDto;
import com.tricon.rcm.dto.RcmClaimMainRootDto;
import com.tricon.rcm.dto.RcmOfficeDto;
import com.tricon.rcm.dto.customquery.FreshClaimDetailsDto;
import com.tricon.rcm.enums.ClaimSourceEnum;
import com.tricon.rcm.enums.ClaimTypeEnum;
import com.tricon.rcm.enums.RcmTeamEnum;
import com.tricon.rcm.jpa.repository.RCMUserRepository;
import com.tricon.rcm.jpa.repository.RcmClaimLogRepo;
import com.tricon.rcm.jpa.repository.RcmClaimRepository;
import com.tricon.rcm.jpa.repository.RcmClaimStatusTypeRepo;
import com.tricon.rcm.jpa.repository.RcmCompanyRepo;
import com.tricon.rcm.jpa.repository.RcmInsuranceRepo;
import com.tricon.rcm.jpa.repository.RcmInsuranceTypeRepo;
import com.tricon.rcm.jpa.repository.RcmMappingTableRepo;
import com.tricon.rcm.jpa.repository.RcmOfficeRepository;
import com.tricon.rcm.jpa.repository.RcmTeamRepo;
import com.tricon.rcm.security.JwtUser;
import com.tricon.rcm.util.ClaimUtil;
import com.tricon.rcm.util.ConnectAndReadSheets;
import com.tricon.rcm.util.Constants;

@Service
public class ClaimServiceImpl {

	@Autowired
	RuleEngineService ruleEngineService;

	@Autowired
	RcmMappingTableRepo rcmMappingTableRepo;

	@Autowired
	RcmCompanyRepo rcmCompanyRepo;
	
	@Autowired
	RCMUserRepository userRepo;

	@Autowired
	RcmTeamRepo rcmTeamRepo;

	@Value("${google.credential.folder}")
	private String CREDENTIALS_FOLDER;

	@Value("${google.client.secret}")
	private String CLIENT_SECRET_DIR;

	@Autowired
	RcmOfficeRepository officeRepo;

	@Autowired
	RcmInsuranceRepo insuranceRepo;

	@Autowired
	RcmClaimRepository rcmClaimRepository;

	@Autowired
	RcmClaimStatusTypeRepo rcmClaimStatusTypeRepo;

	@Autowired
	RcmInsuranceTypeRepo rcmInsuranceTypeRepo;

	@Autowired
	CommonClaimServiceImpl commonClaimServiceImpl;
	
	@Autowired
	RcmClaimLogRepo rcmClaimLogRepo;
	
	@Autowired
	@Qualifier("jwtUserDetailsService")
	private UserDetailsService userDetailsService;

	private final Logger logger = LoggerFactory.getLogger(ClaimServiceImpl.class);

	@Transactional(rollbackFor = Exception.class)
	public Object pullClaimFromSource(ClaimSourceDto dto, RcmUser user) {
		// go to Rule Engine.
		Object status = null;
		
		if (dto.getSource().equals(ClaimSourceEnum.GOOGLESHEET.toString())) {
			try {
				status = pullAndSaveClaimFromSheet(dto, user);
			} catch (Exception error) {

			}
		} else {
			List<ClaimLogDto> mapcountNew = new ArrayList<>();
			ClaimLogDto claimLogDto =null;
			for (String dtoOff : dto.getOfficeuuids()) {
				ClaimSourceDto d = new ClaimSourceDto();
				
				BeanUtils.copyProperties(dto, d);
				d.setOfficeuuid(dtoOff);
				// logId=-1;
				String data="";
				ruleEngineService.pullAndSaveInsuranceFromRE(d, user);
				try {
					data = ruleEngineService.pullAndSaveClaimFromRE(d, user);
					String[] logs=data.split("___");
					if (logs.length==2) {
						
						Optional<RcmClaimLog> optLog= rcmClaimLogRepo.findById(Integer.parseInt(logs[1]));
						if (optLog.isPresent()) {
							RcmClaimLog log =	optLog.get();
							claimLogDto=	new ClaimLogDto(ClaimSourceEnum.GOOGLESHEET.toString(), dtoOff, log.getStatus(), log.getNewClaimsCount(), new Date(),
									log.getOffice().getName());
							mapcountNew.add(claimLogDto);
							//mapcountNew.put(log.getOffice().getName()+"___"+log.getOffice().getUuid(), new Object[] {log.getNewClaimsCount(),
							//		log.getNewClaimsPrimaryCount(),log.getNewClaimsSecodaryCount(),log.getStatus()});
						}
					}
				} catch (Exception error) {
				}
				
			}
			
			status =mapcountNew;

		}
		return status;
	}

	/**
	 * Service For Billing Pendency Dashboard (Get Fresh Claims Count Details)
	 * 
	 * @return
	 */
	public List<FreshClaimDetailsDto> fetchFreshClaimDetails() {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
		JwtUser jwtUser = (JwtUser) userDetails;
		
		return rcmClaimRepository.fetchFreshClaimDetails(jwtUser.getCompany().getUuid());
	}

	
	/**
	 * 
	 * Fetch Claim Logs To Show "Billing Lead - Tool to update database"
	 * @return
	 */
	public List<FreshClaimLogDto> fetchFreshClaimLogs() {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
		JwtUser jwtUser = (JwtUser) userDetails;
		
		return rcmClaimRepository.fetchFreshClaimLogs(jwtUser.getCompany().getUuid());
	}
	
	public Object pullAndSaveClaimFromSheet(ClaimSourceDto dto, RcmUser user) {

		logger.info(" In pullClaimFromSheet");
		String success = "";
		RcmClaims claim = null;

		Map<String, Object[]> mapcountNew = new HashMap<>();
		// Map<String,String> messages= new HashMap<>();
		// Map<String,int[]> mapcountNewP= new HashMap<>();
		// Map<String,int[]>mapcountNewS= new HashMap<>();
		RcmCompany company = rcmCompanyRepo.findByUuid(dto.getCompanyuuid());
		// int logId=-1;
		RcmMappingTable table = rcmMappingTableRepo.findByNameAndCompany(Constants.RCM_MAPPING_RCM_DATABASE, company);
		List<ClaimFromSheet> li = null;
		try {
			li = ConnectAndReadSheets.readClaimsFromGSheet(table.getGoogleSheetId(), table.getGoogleSheetSubName(),
					CLIENT_SECRET_DIR, CREDENTIALS_FOLDER);
			if (li != null) {

				RcmClaimStatusType billingStatus = rcmClaimStatusTypeRepo.findByStatus(Constants.billingClaim);
				for (ClaimFromSheet ins : li) {

					RcmOffice office = officeRepo.findByCompanyAndName(company, ins.getOfficeName());
					RcmInsuranceType insType = rcmInsuranceTypeRepo.findByName(ins.getInsuranceType());
					if (office == null) {
						logger.error("Office Not Found For Claim :" + ins.getClaimId());
						mapcountNew.put(ins.getOfficeName() + "___", new Object[] { 0, 0, 0, "Office Not Found" });
						continue;
					}

					if (mapcountNew.get(ins.getOfficeName() + "___" + office.getUuid()) == null) {
						mapcountNew.put(ins.getOfficeName() + "___" + office.getUuid(), new Object[] { 0, 0, 0, "" });
					}

					if (insType == null) {
						logger.error("Insurance Found For Claim :" + ins.getClaimId());
						Object[] f = mapcountNew.get(ins.getOfficeName() + "___" + office.getUuid());
						f[3] = f[3].toString() + " Insurance not Found For Claim Id:" + ins.getClaimId();
						// mapcountNew.put(ins.getOfficeName()+"___"+office.getUuid(),new
						// Object[]{0,0,0,"Insurance Found For Claim\n"});
						continue;

					}

					RcmInsurance insu = insuranceRepo.findByNameAndOfficeAndInsuranceType(ins.getInsuranceCompanyName(),
							office, insType);
					List<String> allCl = Arrays.asList(ins.getClaimId() + ClaimTypeEnum.P.getSuffix(),
							ins.getClaimId() + ClaimTypeEnum.S.getSuffix());
					List<RcmClaims> claims = rcmClaimRepository.findByClaimIdInAndOffice(allCl, office);
					if (claims == null || claims.size() == 0) {
						claim = new RcmClaims();
						ClaimUtil.createClaimFromSheetData(claim, office, ins,
								rcmTeamRepo.findByNameId(RcmTeamEnum.BILLING.toString()), user, insu, null,
								billingStatus, null);
						rcmClaimRepository.save(claim);
						Object[] countN = mapcountNew.get(ins.getOfficeName() + "___" + office.getUuid());

						mapcountNew.put(ins.getOfficeName() + "___" + office.getUuid(),
								new Object[] { Integer.parseInt(countN[0].toString()) + 1,
										Integer.parseInt(countN[1].toString()) + 1, countN[2], countN[3] + "\n" });

						if (ins.getInsuranceType().equals(Constants.insuranceTypeSecondary)) {
							ClaimUtil.createClaimFromSheetData(claim, office, ins,
									rcmTeamRepo.findByNameId(RcmTeamEnum.BILLING.toString()), user, null, insu,
									billingStatus, null);
							rcmClaimRepository.save(claim);

							mapcountNew.put(ins.getOfficeName() + "___" + office.getUuid(), new Object[] { countN[0],
									countN[1], Integer.parseInt(countN[2].toString()) + 1, countN[3] + "\n" });

						}
					} else {
						/*
						 * not needed for(RcmClaims olClaim:claims) {
						 * 
						 * if
						 * ((olClaim.getClaimId().split("_")[1]+"_").equals(ClaimTypeEnum.S.getSuffix())
						 * ){ // do nothing
						 * 
						 * } if
						 * ((olClaim.getClaimId().split("_")[1]+"_").equals(ClaimTypeEnum.P.getSuffix())
						 * ){ // look for Secondary if not then }
						 * 
						 * }
						 */
					}

				}

				Set<String> offices = mapcountNew.keySet();
				for (String of : offices) {
					Object[] cts = mapcountNew.get(of);
					String[] x = of.split("___");
					if (x.length == 2) {
						RcmClaimLog log = new RcmClaimLog();
						/*
						 * log.setNewClaimsCount(Integer.parseInt(cts[0].toString()));
						 * log.setNewClaimsPrimaryCount(Integer.parseInt(cts[1].toString()));
						 * log.setNewClaimsSecodaryCount(Integer.parseInt(cts[2].toString()));
						 * log.setCreatedBy(user); log.setMessage(cts[3].toString()); log.setStatus(1);
						 * log.setSource(ClaimSourceEnum.GOOGLESHEET.toString());
						 * 
						 * log.setOffice(officeRepo.findByUuid(x[1]));
						 */
						commonClaimServiceImpl.saveClaimLog(log, user, officeRepo.findByUuid(x[1]),
								ClaimSourceEnum.EAGLESOFT.toString(), 0, Integer.parseInt(cts[0].toString()),
								Integer.parseInt(cts[1].toString()), Integer.parseInt(cts[2].toString()), success);
					} else {

					}

				}

				// commonClaimServiceImpl.saveClaimLog(log, user, office, dto.getSource(), 1,
				// newClaimCt);
			}
		} catch (Exception n) {
			logger.error("Error in Fetching Claims From Sheet.. ");
			logger.error(n.getMessage());
			success = n.getMessage();
		}

		return mapcountNew;
	}

	/*
	 * public void saveClaimLog(RcmClaimLog log, RcmUser user, RcmOffice off, String
	 * source, int logStatus, int newClaimCt) { log.setCreatedBy(user);
	 * log.setOffice(off); log.setSource(source); log.setStatus(logStatus);
	 * log.setNewClaimsCount(newClaimCt); commonClaimServiceImpl.save(log); }
	 */
	
	public Object fetchRemoteLiteRejections(String officeUUid) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
		JwtUser jwtUser = (JwtUser) userDetails;
		
		ClaimSourceDto dto= new ClaimSourceDto();
		dto.setOfficeuuid(officeUUid);
		dto.setCompanyuuid(jwtUser.getCompany().getUuid());
		//only for Simple Point
		if (jwtUser.getCompany().getName().equals(Constants.COMPANY_NAME)) {
			
			RcmUser user= userRepo.findByEmail(((UserDetails) principal).getUsername());
			Object obj=ruleEngineService.pullAndSaveRemoteLiteData(dto, user, -1);
			return obj;
		}
		else return null;
	}

}
