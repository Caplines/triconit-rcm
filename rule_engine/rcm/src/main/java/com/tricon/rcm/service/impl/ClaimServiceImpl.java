package com.tricon.rcm.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import java.util.stream.Collectors;

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
import com.tricon.rcm.dto.AssigmentClaimListDto;
import com.tricon.rcm.dto.ClaimFromSheet;
import com.tricon.rcm.dto.ClaimLogDto;
import com.tricon.rcm.dto.ClaimProductionLogDto;
import com.tricon.rcm.dto.ClaimSourceDto;

import com.tricon.rcm.dto.customquery.FreshClaimLogDto;
import com.tricon.rcm.dto.customquery.ProductionDto;
import com.tricon.rcm.dto.RcmOfficeDto;
import com.tricon.rcm.dto.RemoteLietStatusCount;
import com.tricon.rcm.dto.TimelyFilingLimitDto;
import com.tricon.rcm.dto.customquery.AssignFreshClaimLogsDto;
import com.tricon.rcm.dto.customquery.AssignFreshClaimLogsImplDto;
import com.tricon.rcm.dto.customquery.FreshClaimDataDto;
import com.tricon.rcm.dto.customquery.FreshClaimDetailsDto;
import com.tricon.rcm.dto.customquery.FreshClaimDetailsImplDto;
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
import com.tricon.rcm.jpa.repository.RcmInsuranceRepo;
import com.tricon.rcm.jpa.repository.RcmInsuranceTypeRepo;
import com.tricon.rcm.jpa.repository.RcmIssueClaimsRepo;
import com.tricon.rcm.jpa.repository.RcmMappingTableRepo;
import com.tricon.rcm.jpa.repository.RcmOfficeRepository;
import com.tricon.rcm.jpa.repository.RcmTeamRepo;
import com.tricon.rcm.jpa.repository.UserAssignOfficeRepo;
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
	
	@Autowired
	UserAssignOfficeRepo userAssignOfficeRepo;
	
	@Autowired
	RcmIssueClaimsRepo rcmIssueClaimsRepo;
	
	@Autowired
	RcmClaimAssignmentRepo rcmClaimAssignmentRepo;
	
	private final Logger logger = LoggerFactory.getLogger(ClaimServiceImpl.class);

	@Transactional(rollbackFor = Exception.class)
	public Object pullClaimFromSource(ClaimSourceDto dto, RcmUser user) {
		// go to Rule Engine.
		Object status = null;
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
		JwtUser jwtUser = (JwtUser) userDetails;
		
		if (dto.getSource().equals(ClaimSourceEnum.GOOGLESHEET.toString())) {
			try {
				status = pullAndSaveClaimFromSheet(dto, user);
			} catch (Exception error) {

			}
		} else {
			List<ClaimLogDto> mapcountNew = new ArrayList<>();
			ClaimLogDto claimLogDto =null;
			if (dto.getOfficeuuids()==null) {
				List<String> of= new ArrayList<>(); 
				officeRepo.findByCompany(jwtUser.getCompany()).stream().map(RcmOfficeDto::getUuid).forEach(of::add);
				dto.setOfficeuuids(of);
			}
			for (String dtoOff : dto.getOfficeuuids()) {
				ClaimSourceDto d = new ClaimSourceDto();
				
				BeanUtils.copyProperties(dto, d);
				d.setOfficeuuid(dtoOff);
				// logId=-1;
				String data="";
				//ruleEngineService.pullAndSaveInsuranceFromRE(d, user);
				
				List<TimelyFilingLimitDto> li= ruleEngineService.pullTimelyFilingLmtMappingFromSheet(jwtUser.getCompany());
				try {
					data = ruleEngineService.pullAndSaveClaimFromRE(d, user, li, ClaimTypeEnum.P, jwtUser.getCompany(),-1);    //(d, user,li);
					String[] logsP=data.split("___");
					if (logsP.length==2) {
						
						Optional<RcmClaimLog> optLog= rcmClaimLogRepo.findById(Integer.parseInt(logsP[1]));
						if (optLog.isPresent()) {
							RcmClaimLog log =	optLog.get();
							claimLogDto=	new ClaimLogDto(ClaimSourceEnum.EAGLESOFT.toString(), dtoOff, log.getStatus(), log.getNewClaimsCount(), new Date(),
									log.getOffice().getName());
							mapcountNew.add(claimLogDto);
							//mapcountNew.put(log.getOffice().getName()+"___"+log.getOffice().getUuid(), new Object[] {log.getNewClaimsCount(),
							//		log.getNewClaimsPrimaryCount(),log.getNewClaimsSecodaryCount(),log.getStatus()});
						}
					}
					String[] statusPri = data.split("___");
					int logId=-1;
					try {
						if (statusPri.length==2) logId=Integer.parseInt(statusPri[1]);
						}catch(Exception p) {
							
						}
					data = ruleEngineService.pullAndSaveClaimFromRE(d, user, li, ClaimTypeEnum.S, jwtUser.getCompany(),logId);    //(d, user,li);
					String[] logsS=data.split("___");
					if (logsS.length==2) {
						
						Optional<RcmClaimLog> optLog= rcmClaimLogRepo.findById(Integer.parseInt(logsS[1]));
						if (optLog.isPresent()) {
							RcmClaimLog log =	optLog.get();
							claimLogDto=	new ClaimLogDto(ClaimSourceEnum.EAGLESOFT.toString(), dtoOff, log.getStatus(), log.getNewClaimsCount(), new Date(),
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
	 * Service For Billing Pendency Dashboard (Get Fresh Claims/Rebill Count Details)
	 * 
	 * @return
	 */
	public List<FreshClaimDetailsImplDto> fetchBillingClaimDetails(int billType) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
		JwtUser jwtUser = (JwtUser) userDetails;
		List<FreshClaimDetailsDto> dtoList=rcmClaimRepository.fetchBillingOrReBillingClaimDetails(jwtUser.getCompany().getUuid(),billType);
		 List<FreshClaimDetailsImplDto> finalList = new ArrayList<>();
		HashMap<String,RemoteLietStatusCount> remoteLiteMap= ruleEngineService.pullAndSaveRemoteLiteData();
		RemoteLietStatusCount counts=null;
		if (dtoList!=null) {
			for(FreshClaimDetailsDto d:dtoList) {
				FreshClaimDetailsImplDto dF= new FreshClaimDetailsImplDto();
				BeanUtils.copyProperties(d, dF);
				counts=remoteLiteMap.get(d.getOfficeName());
				if (counts!=null) dF.setRemoteLiteRejections( counts.getRejectedCount());
				finalList.add(dF);
				
			}
		}
		return finalList;
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
		Map<String,RcmClaimLog> mapcountNew = new HashMap<>();
		RcmClaimLog rcmClaimLog=null;
		String source=ClaimSourceEnum.GOOGLESHEET.toString();
		RcmClaimAssignment rcmAssigment = null;
		//Map<String, Object[]> mapcountNew = new HashMap<>();
		// Map<String,String> messages= new HashMap<>();
		// Map<String,int[]> mapcountNewP= new HashMap<>();
		// Map<String,int[]>mapcountNewS= new HashMap<>();
		RcmCompany company = rcmCompanyRepo.findByUuid(dto.getCompanyuuid());
		List<RcmTeam> allTeams = rcmTeamRepo.findAll();
		List<TimelyFilingLimitDto> timelyFilingLimits= ruleEngineService.pullTimelyFilingLmtMappingFromSheet(company);
		RcmClaimStatusType systemStatusBilling = rcmClaimStatusTypeRepo.findByStatus(ClaimStatusEnum.Billing.getType());
		RcmClaimStatusType systemStatusReBilling = rcmClaimStatusTypeRepo.findByStatus(ClaimStatusEnum.ReBilling.getType());
		// int logId=-1;
		RcmMappingTable table = rcmMappingTableRepo.findByNameAndCompany(Constants.RCM_MAPPING_RCM_DATABASE, company);
		List<ClaimFromSheet> li = null;
		try {
			li = ConnectAndReadSheets.readClaimsFromGSheet(table.getGoogleSheetId(), table.getGoogleSheetSubName(),
					CLIENT_SECRET_DIR, CREDENTIALS_FOLDER);
			if (li != null) {

				///RcmClaimStatusType billingStatus = rcmClaimStatusTypeRepo.findByStatus(ClaimStatusEnum.Billing.getType());
				for (ClaimFromSheet re : li) {
					
					RcmOffice office = officeRepo.findByCompanyAndName(company, re.getOfficeName());
					//RcmInsuranceType insType = rcmInsuranceTypeRepo.findByName(re.getInsuranceType());
					if (office == null) {
						logger.error("Office Not Found For Claim :" + re.getClaimId());
						//claimLogDto= new ClaimLogDto(ClaimSourceEnum.GOOGLESHEET.toString(), null, 1, newClaimsCount, cd, ins.getOfficeName());
						//mapcountNew.put(ins.getOfficeName() + "___", new Object[] { 0, 0, 0, "Office Not Found" });
						
						continue;
					}


					try {
						System.out.println(re.getClaimId() + "--<ID");
						List<String> allCl = Arrays.asList(re.getClaimId() + ClaimTypeEnum.P.getSuffix(),
								re.getClaimId() + ClaimTypeEnum.S.getSuffix());
						List<RcmClaims> claims = rcmClaimRepository.findByClaimIdInAndOffice(allCl, office);
						UserAssignOffice assignedUser = userAssignOfficeRepo
								.findByOfficeUuidAndTeamId(office.getUuid(), RcmTeamEnum.BILLING.getId());

						RcmInsuranceType rcmInsuranceType = null;
						if (claims == null || claims.size() == 0) {
							List<String> buildError=null;
							// Fresh Claims
							
							claim = new RcmClaims();
							List<String> error= new ArrayList<>();
							System.out.println(re.getInsuranceCompanyName());
							RcmInsurance ins = insuranceRepo
									.findByNameAndOffice(re.getInsuranceCompanyName(), office);
							if (ins==null) {
							    error.add( "Insurance Missing for name:"+re.getInsuranceCompanyName());
							}
							RcmInsuranceType iType =rcmInsuranceTypeRepo.findByName(re.getInsuranceType());
							if (iType==null) {
								 error.add( "Insurance Type is wrong:"+re.getInsuranceType());
							}
							String timely =ClaimUtil.getTimelyLimitFromSheetList(timelyFilingLimits,re.getInsuranceCompanyName());
							if (timely==null) {
								error.add("Timely Limit Type Missing for  Ins. :"+re.getInsuranceCompanyName());		    
										    
							}
							
							/*int insuranceId = ins.getInsuranceType().getId();
							rcmInsuranceType = rcmInsuranceTypeRepo.findById(insuranceId);
							if (rcmInsuranceType==null) {
								error.add("Primary Ins. missing For:"+re.getPrimInsuranceCompanyId());		    
										    
							}*/
							if (error.size()>0) {
								 saveRcmIssueClaim(re, buildError, office,user,
										 String.join("\n", error),source);
								 continue;
							}
							rcmClaimLog = mapcountNew.get(office.getUuid());
							if (rcmClaimLog==null) {
								rcmClaimLog = new RcmClaimLog();//(source, office.getUuid(), 1, 0, new Date(), office.getName());
								rcmClaimLog.setOffice(office);
								rcmClaimLog.setNewClaimsCount(1);
								rcmClaimLog.setNewClaimsPrimaryCount(1);
								rcmClaimLog.setNewClaimsSecodaryCount(0);
								mapcountNew.put(office.getUuid(), rcmClaimLog);
							}else {
								rcmClaimLog.setNewClaimsCount(rcmClaimLog.getNewClaimsCount()+1);
								rcmClaimLog.setNewClaimsPrimaryCount(rcmClaimLog.getNewClaimsPrimaryCount()+1);
							}
							claim = ClaimUtil.createClaimFromSheetData(claim, office, re,
									ClaimUtil.filterTeamByNameId(allTeams, RcmTeamEnum.BILLING.toString()), user,
									ins,
									ins,
									re.getActionRequired().equals("Billing")?systemStatusBilling:systemStatusReBilling, ClaimTypeEnum.P.getSuffix(), rcmInsuranceType,
									timely);
							String claimUUid = rcmClaimRepository.save(claim).getClaimUuid();
							RcmIssueClaims isC=rcmIssueClaimsRepo.findByClaimIdAndOfficeAndSource(re.getClaimId(), office,source);
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
										Constants.SYSTEM_INITIAL_COMMENT, re.getActionRequired().equals("Billing")?systemStatusBilling:systemStatusReBilling);

								rcmClaimAssignmentRepo.save(rcmAssigment);
							}
							if (re.getClaimType().equalsIgnoreCase(Constants.insuranceTypeSecondary)) {
								
								claim = new RcmClaims();
								error= new ArrayList<>();
								RcmInsurance secondarySec = insuranceRepo
										.findByNameAndOffice(re.getInsuranceCompanyName(), office);
								
								if (secondarySec==null) {
									error.add("Secondary Insurance Missing for name:"+re.getInsuranceCompanyName());
								}
								
								
								timely =ClaimUtil.getTimelyLimitFromSheetList(timelyFilingLimits, secondarySec.getName() );
								if (timely==null) {
									error.add("Timely Limit Type Missing for Secondary Ins. :"+secondarySec.getName());
								}
								
								
								
								if (error.size()>0) {
									 saveRcmIssueClaim(re, buildError, office,user,
											 String.join("\n", error),source);
									 continue;
								}
								
								
								rcmClaimLog = mapcountNew.get(office.getUuid());
								if (rcmClaimLog==null) {
									rcmClaimLog = new RcmClaimLog();//(source, office.getUuid(), 1, 0, new Date(), office.getName());
									rcmClaimLog.setOffice(office);
									rcmClaimLog.setNewClaimsCount(1);
									rcmClaimLog.setNewClaimsPrimaryCount(0);
									rcmClaimLog.setNewClaimsSecodaryCount(1);
									mapcountNew.put(office.getUuid(), rcmClaimLog);
								}else {
									rcmClaimLog.setNewClaimsCount(rcmClaimLog.getNewClaimsCount()+1);
									rcmClaimLog.setNewClaimsSecodaryCount(rcmClaimLog.getNewClaimsSecodaryCount()+1);
								}
								claim = ClaimUtil.createClaimFromSheetData(claim, office, re,
										ClaimUtil.filterTeamByNameId(allTeams, RcmTeamEnum.BILLING.toString()), user,
										secondarySec,
										secondarySec, re.getActionRequired().equals("Billing")?systemStatusBilling:systemStatusReBilling, ClaimTypeEnum.S.getSuffix(),
										rcmInsuranceType,timely);
								claimUUid = rcmClaimRepository.save(claim).getClaimUuid();
								if (assignedUser != null) {
									rcmAssigment = new RcmClaimAssignment();
									rcmAssigment = ClaimUtil.createAssginmentData(rcmAssigment, user,
											assignedUser.getUser(), claimUUid, claim,
											Constants.SYSTEM_INITIAL_COMMENT, re.getActionRequired().equals("Billing")?systemStatusBilling:systemStatusReBilling);
									rcmClaimAssignmentRepo.save(rcmAssigment);
								}
							}
							if (isC!=null){
								isC.setResolved(true);
								rcmIssueClaimsRepo.save(isC);
							}
						} else {
							// OLD Claims Exists
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
								
								
								claim = new RcmClaims();
								RcmInsurance primarySec = insuranceRepo
										.findByInsuranceIdAndOffice(re.getInsuranceCompanyName(), office);
								if (primarySec==null) {
									error.add("Primary Insurance Missing for name:"+re.getInsuranceCompanyName());
								}
								
								
								String timely =ClaimUtil.getTimelyLimitFromSheetList(timelyFilingLimits, primarySec.getName());
								
								if (timely==null) {
									error.add("Timely Limit Type Missing for Primary Ins. :"+primarySec.getName());
								}
								int insuranceId = primarySec.getInsuranceType().getId();
								rcmInsuranceType = rcmInsuranceTypeRepo.findById(insuranceId);
								
								
								if (error.size()>0) {
									 saveRcmIssueClaim(re, buildError, office,user,
											 String.join("\n", error),source);
									 continue;
								}
								rcmClaimLog = mapcountNew.get(office.getUuid());
								if (rcmClaimLog==null) {
									rcmClaimLog = new RcmClaimLog();//(source, office.getUuid(), 1, 0, new Date(), office.getName());
									rcmClaimLog.setOffice(office);
									rcmClaimLog.setNewClaimsCount(1);
									rcmClaimLog.setNewClaimsPrimaryCount(1);
									rcmClaimLog.setNewClaimsSecodaryCount(0);
									mapcountNew.put(office.getUuid(), rcmClaimLog);
								}else {
									rcmClaimLog.setNewClaimsCount(rcmClaimLog.getNewClaimsCount()+1);
									rcmClaimLog.setNewClaimsPrimaryCount(rcmClaimLog.getNewClaimsPrimaryCount()+1);
								}
								claim = ClaimUtil.createClaimFromSheetData(claim, office, re,
										ClaimUtil.filterTeamByNameId(allTeams, RcmTeamEnum.BILLING.toString()), user,
										primarySec,
										primarySec,
										 re.getActionRequired().equals("Billing")?systemStatusBilling:systemStatusReBilling, ClaimTypeEnum.P.getSuffix(), rcmInsuranceType,timely);
								String claimUUid = rcmClaimRepository.save(claim).getClaimUuid();
								RcmIssueClaims isC=rcmIssueClaimsRepo.findByClaimIdAndOfficeAndSource(re.getClaimId(), office,source);
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
							
								claim = new RcmClaims();
								List<String> error= new ArrayList<>();
								RcmInsurance secondarySec = insuranceRepo
										.findByNameAndOffice(re.getInsuranceCompanyName(), office);
								if (secondarySec==null  ) {
								    error.add("Secondary Insurance Missing for name:"+re.getInsuranceCompanyName());
								}
								
								
								
								String timely =null;
								if (secondarySec!=null) ClaimUtil.getTimelyLimitFromSheetList(timelyFilingLimits, secondarySec.getName() );
								if (timely==null && secondarySec!=null) {
									error.add("Timely Limit Type Missing for Secondary Ins. :"+secondarySec.getName());
								}
								
								if (error.size()>0) {
									 saveRcmIssueClaim(re, buildError, office,user,
											 String.join("\n", error),source);
									 continue;
								}
								
								rcmClaimLog = mapcountNew.get(office.getUuid());
								if (rcmClaimLog==null) {
									rcmClaimLog = new RcmClaimLog();//(source, office.getUuid(), 1, 0, new Date(), office.getName());
									rcmClaimLog.setOffice(office);
									rcmClaimLog.setNewClaimsCount(1);
									rcmClaimLog.setNewClaimsPrimaryCount(0);
									rcmClaimLog.setNewClaimsSecodaryCount(1);
									mapcountNew.put(office.getUuid(), rcmClaimLog);
								}else {
									rcmClaimLog.setNewClaimsCount(rcmClaimLog.getNewClaimsCount()+1);
									rcmClaimLog.setNewClaimsSecodaryCount(rcmClaimLog.getNewClaimsSecodaryCount()+1);
								}
								
								claim = ClaimUtil.createClaimFromSheetData(claim, office, re,
										ClaimUtil.filterTeamByNameId(allTeams, RcmTeamEnum.BILLING.toString()), user,
										secondarySec,
										secondarySec,
										 re.getActionRequired().equals("Billing")?systemStatusBilling:systemStatusReBilling, ClaimTypeEnum.S.getSuffix(), rcmInsuranceType,
										timely);
								String claimUUid = rcmClaimRepository.save(claim).getClaimId();
								RcmIssueClaims isC=rcmIssueClaimsRepo.findByClaimIdAndOfficeAndSource(re.getClaimId(), office,source);
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

						}

					} catch (Exception clai) {
						// success = n1.getMessage();
						clai.printStackTrace();
						success = success + "," + re.getClaimId();
						logger.error(clai.getMessage());
					}
				

				}

				Set<String> offices = mapcountNew.keySet();
				for (String of : offices) {
					RcmClaimLog log= mapcountNew.get(of);
					
						commonClaimServiceImpl.saveClaimLog(log, user, log.getOffice(),
								ClaimSourceEnum.GOOGLESHEET.toString(), 0, log.getNewClaimsCount(),
								log.getNewClaimsPrimaryCount(),log.getNewClaimsSecodaryCount(), success);
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
	
	public Object fetchRemoteLiteRejections() {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
		JwtUser jwtUser = (JwtUser) userDetails;
		
		ClaimSourceDto dto= new ClaimSourceDto();
		//dto.setOfficeuuid(officeUUid);
		dto.setCompanyuuid(jwtUser.getCompany().getUuid());
		//only for Simple Point
		if (jwtUser.getCompany().getName().equals(Constants.COMPANY_NAME)) {
			
			//RcmUser user= userRepo.findByEmail(((UserDetails) principal).getUsername());
			Object obj=ruleEngineService.pullAndSaveRemoteLiteData();
			return obj;
		}
		else return null;
	}
	
	
	 public List<FreshClaimDataDto> fetchFreshClaimDetails(int teamId,int billingORRebill,String sub) {
			
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
		JwtUser jwtUser = (JwtUser) userDetails;
			
		if (sub.equals("Fresh")) return rcmClaimRepository.fetchFreshClaimDetails(jwtUser.getCompany().getUuid(), teamId);
		else                     return rcmClaimRepository.fetchClaimDetailsWorkedByTeam(jwtUser.getCompany().getUuid(), teamId);
			
		}
	 
	 public List<FreshClaimDataDto> fetchClaimsByTeamNotFrom(int teamId) {
			
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			Object principal = authentication.getPrincipal();
			final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
			JwtUser jwtUser = (JwtUser) userDetails;
			
			return rcmClaimRepository.fetchFreshClaimDetailsOtherTeam(jwtUser.getCompany().getUuid(),teamId);
		}
	 
	 
	 
	 public List<AssignFreshClaimLogsImplDto> fetchClaimsForAssignments(AssigmentClaimListDto dto) {
			
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			Object principal = authentication.getPrincipal();
			final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
			JwtUser jwtUser = (JwtUser) userDetails;
			List<Integer> ct=dto.getClaimType();
			List<String> inst=dto.getInsuranceType();
			if (dto.getClaimType()==null) {
				ct= new ArrayList<>();
				ct.add(ClaimStatusEnum.Billing.getId());
				ct.add(ClaimStatusEnum.ReBilling.getId());
			}
			List<AssignFreshClaimLogsImplDto> finalList = new ArrayList<>();
			 Set<Integer> instDB=new HashSet<>();
			 List<RcmInsuranceType>  insList=rcmInsuranceTypeRepo.findAll();
			if (inst==null) {
			  insList.stream().map(RcmInsuranceType::getId).forEach(instDB::add);
			}else {
				
				for(String i:inst) {
					
					List<RcmInsuranceType> x=	insList.stream()
							.filter(p -> p.getName().contains(i)).collect(Collectors.toList());
					if (x!=null) {
						x.stream().map(RcmInsuranceType::getId).forEach(instDB::add);
					}
					
				}
				
			}
			List<AssignFreshClaimLogsDto> l=null;
			try {
			 l=rcmClaimRepository.fetchClaimsForAssignments(jwtUser.getCompany().getUuid(),ct,instDB);
			 HashMap<String,RemoteLietStatusCount> remoteLiteMap= ruleEngineService.pullAndSaveRemoteLiteData();
			 RemoteLietStatusCount counts=null;
			 
			 AssignFreshClaimLogsImplDto dF=null;
			  if (l!=null) {
				  for (AssignFreshClaimLogsDto logD:l) {
					  dF= new AssignFreshClaimLogsImplDto();
					  BeanUtils.copyProperties(logD, dF);
					  counts= remoteLiteMap.get(logD.getOfficeName());
					  if (counts!=null) {
						  dF.setRemoteLiteRejections(counts.getRejectedCount()); 
					  }
					  finalList.add(dF);
				  }
				  
			  }
	        }catch(Exception n) {
	        	n.printStackTrace();
	        }
			return finalList;
		}
	 
	 public List<ProductionDto> claimsProductionReportByTeam(int teamId,ClaimProductionLogDto dto) {
		 
		 Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			Object principal = authentication.getPrincipal();
			final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
			JwtUser jwtUser = (JwtUser) userDetails;
			
			
		 rcmClaimRepository.claimProductionByTeamMember(jwtUser.getCompany().getUuid(), teamId,dto.getStartDate(),dto.getEndDate());
		 return null;
		 
	 }
	 
	 
	 private void saveRcmIssueClaim(ClaimFromSheet re,List<String> buildError,RcmOffice off,
				RcmUser user,String error,String source) {
			
			RcmIssueClaims isC=rcmIssueClaimsRepo.findByClaimIdAndOfficeAndSource(re.getClaimId(), off,source);
		      if (buildError==null) buildError=new ArrayList<>();
			    buildError.add(error);
			    if (isC==null){
			    	isC = new RcmIssueClaims();
			    	isC.setClaimId(re.getClaimId());
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
}
