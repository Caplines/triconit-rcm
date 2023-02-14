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
import com.tricon.rcm.db.entity.RcmLogs;
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
import com.tricon.rcm.dto.FreshClaimDataImplDto;
import com.tricon.rcm.dto.InsuranceNameTypeDto;
import com.tricon.rcm.dto.customquery.FreshClaimLogDto;
import com.tricon.rcm.dto.customquery.ProductionDto;
import com.tricon.rcm.dto.customquery.RcmClaimDetailDto;
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
import com.tricon.rcm.jpa.repository.RcmLinkedClaimsRepo;
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
	
	@Autowired
	RcmLinkedClaimsRepo rcmLinkedClaimsRepo;
	
	private final Logger logger = LoggerFactory.getLogger(ClaimServiceImpl.class);

	@Transactional(rollbackFor = Exception.class)
	public Object pullClaimFromSource(ClaimSourceDto dto, RcmUser user,JwtUser jwtUser) {
		// go to Rule Engine.
		Object status = null;
		if (dto.getSource().equals(ClaimSourceEnum.GOOGLESHEET.toString())) {
			try {
				if (user==null) {
					user =userRepo.findByUuid(jwtUser.getUuid());
				}
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
		List<FreshClaimDetailsDto> dtoList=null;
		if ( jwtUser.isTeamLead()) {// && jwtUser.isAssociate()) ||  jwtUser.isTeamLead()
			dtoList =rcmClaimRepository.fetchBillingOrReBillingClaimDetails(jwtUser.getCompany().getUuid(),billType,jwtUser.getTeamId());
		}
		else if (jwtUser.isAssociate()) {
			dtoList =rcmClaimRepository.fetchBillingOrReBillingClaimDetails(jwtUser.getCompany().getUuid(),billType,jwtUser.getTeamId(),
					jwtUser.getUuid());
		}
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
	public List<FreshClaimLogDto> fetchFreshClaimLogs(String companyUuid) {
		
		return rcmClaimRepository.fetchFreshClaimLogs(companyUuid);
	}
	
	public Object pullAndSaveClaimFromSheet(ClaimSourceDto dto, RcmUser user) {

		logger.info(" In pullClaimFromSheet");
		String success = "";
		RcmClaims claim = null;
		List<ClaimLogDto> logClaimDtos= new ArrayList<>();
		Map<String,ClaimLogDto> mapcountNew = new HashMap<>();
		//RcmClaimLog rcmClaimLog=null;
		String source=ClaimSourceEnum.GOOGLESHEET.toString();
		RcmClaimAssignment rcmAssigment = null;
		HashMap<String,RcmClaimLog> logMap= new HashMap<>();
		//Map<String, Object[]> mapcountNew = new HashMap<>();
		// Map<String,String> messages= new HashMap<>();
		// Map<String,int[]> mapcountNewP= new HashMap<>();
		// Map<String,int[]>mapcountNewS= new HashMap<>();
		RcmCompany company = rcmCompanyRepo.findByUuid(dto.getCompanyuuid());
		List<RcmTeam> allTeams = rcmTeamRepo.findAll();
		List<TimelyFilingLimitDto> timelyFilingLimits= ruleEngineService.pullTimelyFilingLmtMappingFromSheet(company);
		RcmClaimStatusType systemStatusBilling = rcmClaimStatusTypeRepo.findByStatus(ClaimStatusEnum.Billing.getType());
		RcmClaimStatusType systemStatusReBilling = rcmClaimStatusTypeRepo.findByStatus(ClaimStatusEnum.ReBilling.getType());
		
		//List<InsuranceNameTypeDto> insuranceTypeDto = ruleEngineService.pullInsuranceMappingFromSheet(company);
		// int logId=-1;
		RcmMappingTable table = rcmMappingTableRepo.findByNameAndCompany(Constants.RCM_MAPPING_RCM_DATABASE, company);
		List<ClaimFromSheet> li = null;
		HashMap<String,RcmCompany> companies=new HashMap<>();
		HashMap<String,RcmInsuranceType> rcmInsuranceTypes=new HashMap<>();
		RcmInsuranceType rcmInsuranceType=null;
		RcmCompany clCompany=null;
		RcmInsurance ins=null;
		List<String> offNames=null; 
		List<RcmOffice> rcmOffices=null;
		if (dto.getOfficeuuids()!=null && dto.getOfficeuuids().size()>0) {
			offNames= new ArrayList<>();
			rcmOffices =officeRepo.findByUuidInAndCompanyUuid(dto.getOfficeuuids(),
					company.getUuid());
			rcmOffices.stream().map(RcmOffice::getName).forEach(offNames::add);
			
		}
		try {
			li = ConnectAndReadSheets.readClaimsFromGSheet(table.getGoogleSheetId(), table.getGoogleSheetSubName(),
					CLIENT_SECRET_DIR, CREDENTIALS_FOLDER,company.getName(),offNames);
			if (li != null) {
				
			List<ClaimFromSheet>	primaryList = li.stream()
					      .filter(e -> e.getPrimaryClaimStatus().trim().equalsIgnoreCase(ClaimTypeEnum.P.getValue())
					    		   || e.getPrimaryClaimStatus().trim().equalsIgnoreCase(ClaimTypeEnum.E.getValue())
					    		  )
					      .collect(Collectors.toList());
			
			List<ClaimFromSheet>	secondaryList = li.stream()
				      .filter(e -> e.getSecondaryClaimStatus().trim().equalsIgnoreCase(ClaimTypeEnum.PP.getValue())
				    		   || e.getSecondaryClaimStatus().trim().equalsIgnoreCase(ClaimTypeEnum.EE.getValue())
				    		   || e.getSecondaryClaimStatus().trim().equalsIgnoreCase(ClaimTypeEnum.UU.getValue())
				    		  )
				      .collect(Collectors.toList());
			
			
			int newClaimCt = 0;
			int newClaimPCt = 0;
			int newClaimSCt = 0;
			int logStatus = 0;
			for(ClaimFromSheet re: primaryList) {
				try {
					System.out.println(re.getClaimId() + "--<ID");
					ClaimTypeEnum claimTypeEnum=ClaimTypeEnum.P;
					RcmClaimStatusType claimStatusType=null;
					
					if (companies.get(re.getClientName()) ==null)  {
						clCompany=  rcmCompanyRepo.findByName(re.getClientName());
						if (clCompany!=null) {
							companies.put(re.getClientName(), clCompany);
						}else {
							ruleEngineService.saveRcmIssueClaim(re.getClaimId(), null, user, "Wrong Client Name-"+re.getClientName(), source, claimTypeEnum);
							continue;
						}
					}
					
					RcmOffice off= officeRepo.findByCompanyAndName(companies.get(re.getClientName()), re.getOfficeName());
					if (off==null) {
						ruleEngineService.saveRcmIssueClaim(re.getClaimId(), null, user, "Wrong Office Name-"+re.getOfficeName() +"For "+re.getClientName(), source, claimTypeEnum);
						continue;
					}
					List<String> allCl = Arrays.asList(re.getClaimId() + claimTypeEnum.getSuffix());
					List<RcmClaims> claims = rcmClaimRepository.findByClaimIdInAndOffice(allCl, off);
					UserAssignOffice assignedUser = userAssignOfficeRepo
							.findByOfficeUuidAndTeamId(off.getUuid(), RcmTeamEnum.BILLING.getId());
					
					
					
					//RcmInsuranceType rcmInsuranceType = null;
					if (claims == null || claims.size() == 0) {
						//List<String> buildError1=null;
						// Fresh Claims
					/*
					if (claimTypeEnum.getType().equals(Constants.insuranceTypeSecondary)) {
					  //Check for Corresponding Primary Claim in Case of secondary
					  RcmClaims primaryClaim=	rcmClaimRepository.findByClaimIdAndOffice(re.getClaimId()+ClaimTypeEnum.P.getSuffix(), off);
					  if (primaryClaim==null) {
					      //no need to save this as primary is not present..
						  ruleEngineService.saveRcmIssueClaim(re.getClaimId(), off, user,"Primary not Present", source, claimTypeEnum);
						  continue;
						  
					   }
					}
					*/	
						claim = new RcmClaims();
						List<String> error= new ArrayList<>();
						System.out.println(re.getPrimaryInsuranceCompany());
						List<RcmInsurance> insL = insuranceRepo
								.findByNameAndOfficeAndActive(re.getPrimaryInsuranceCompany(), off,true);
						//In Google sheet we do not have InsuranceId so we need to generate. Prefix ==GEN_
							if (insL.size()==2) {
								boolean p=false;
								if (insL.get(0).getInsuranceId().startsWith("GEN_"))p=true;
								if (p) ins=insL.get(1);
								else ins=insL.get(0);
								
								if (ins.getInsuranceId().startsWith("GEN_")){
									//De-activate this
									ins.setActive(false);
									insuranceRepo.save(ins);
								}
								
							}else if (insL.size()==1) {
								ins=insL.get(0);
							}else {
								ins=  new RcmInsurance();
								ins.setActive(true);
								ins.setAddress(re.getPrimaryInsuranceAddress());
								ins.setInsuranceId("GEN_"+new Date().getTime());
								
								rcmInsuranceType = rcmInsuranceTypes.get(re.getInsuranceName());
								if (rcmInsuranceType==null) {
									rcmInsuranceType = rcmInsuranceTypeRepo.findByName(re.getInsuranceName());
									 if (rcmInsuranceType==null) {
										rcmInsuranceType= new RcmInsuranceType();
										rcmInsuranceType.setName(re.getInsuranceName());
										rcmInsuranceType.setId(rcmInsuranceTypeRepo.save(rcmInsuranceType).getId());
										rcmInsuranceTypes.put(re.getInsuranceName(),rcmInsuranceType);
									 }else {
										 rcmInsuranceTypes.put(re.getInsuranceName(),rcmInsuranceType);
									 }
										
								}
								ins.setInsuranceType(rcmInsuranceTypes.get(re.getInsuranceName()));
								ins.setName(re.getPrimaryInsuranceCompany());
								ins.setOffice(off);
								ins.setId(insuranceRepo.save(ins).getId());
//								if (insuranceType==null) {
//									error.add( "Insurance Type Missing For for Name:"+re.getPrimaryInsuranceCompany() +" in G-Sheet");
//								}else {
//								ins.setId(insuranceRepo.save(ins).getId());
//								}
							}
							
						
						
						String timely =ClaimUtil.getTimelyLimitFromSheetList(timelyFilingLimits, re.getPrimaryInsuranceCompany());
						if (timely==null) {
							//error.add("Timely Limit Type Missing for Primary Ins. :"+ins.getName());		    
									    
						}
						
						if (systemStatusBilling.getStatus().equalsIgnoreCase(re.getClaimTypeP())){
							claimStatusType = systemStatusBilling;
						}
						if (systemStatusReBilling.getStatus().equalsIgnoreCase(re.getClaimTypeP())){
							claimStatusType = systemStatusReBilling;
						}
						if (claimStatusType==null) {
							error.add("Wrong Claim Type :"+re.getClaimTypeP());
						}
						if (error.size()>0) {
							ruleEngineService.saveRcmIssueClaim(re.getClaimId(), off, user, String.join("\n", error), source, claimTypeEnum);
							 continue;
						}
						newClaimCt++;
						if (claimTypeEnum.getType().equals(Constants.insuranceTypePrimary))newClaimPCt++;
						if (claimTypeEnum.getType().equals(Constants.insuranceTypeSecondary))newClaimSCt++;
						rcmInsuranceType = rcmInsuranceTypeRepo.findById(ins.getInsuranceType().getId());
						
						claim = ClaimUtil.createClaimFromSheetData(claim, off, re,
								ClaimUtil.filterTeamByNameId(allTeams, RcmTeamEnum.BILLING.toString()), user,
								ins,ins,
								claimStatusType, claimTypeEnum.getSuffix(), rcmInsuranceType,
								timely,claimTypeEnum);
						String claimUUid = rcmClaimRepository.save(claim).getClaimUuid();
						RcmClaimLog l= logMap.get(off.getName());
						if (l==null) {
							l= new RcmClaimLog();
							l.setOffice(off);
							l.setNewClaimsCount(1);
							l.setNewClaimsPrimaryCount(1);
							l.setNewClaimsSecodaryCount(0);
							logMap.put(off.getName(), l);
						}else {
							l.setNewClaimsCount(1+l.getNewClaimsCount());
							l.setNewClaimsPrimaryCount(1+l.getNewClaimsPrimaryCount());
							
						}
						RcmIssueClaims isC=rcmIssueClaimsRepo.findByClaimIdAndOfficeAndSource(re.getClaimId()+claimTypeEnum.getSuffix(), off,source);
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
						
						if (isC!=null){
							isC.setResolved(true);
							rcmIssueClaimsRepo.save(isC);
						}
					}

				} catch (Exception clai) {
					// success = n1.getMessage();
					clai.printStackTrace();
					success = success + "," + re.getClaimId();
					logger.error(clai.getMessage());
				}
			 }
			
			 for(ClaimFromSheet re: secondaryList) {

					try {
						System.out.println(re.getClaimId() + "--<ID");
						ClaimTypeEnum claimTypeEnum=ClaimTypeEnum.S;
						RcmClaimStatusType claimStatusType=null;
						
						if (companies.get(re.getClientName()) ==null)  {
							clCompany=  rcmCompanyRepo.findByName(re.getClientName());
							if (clCompany!=null) {
								companies.put(re.getClientName(), clCompany);
							}else {
								ruleEngineService.saveRcmIssueClaim(re.getClaimId(), null, user, "Wrong Client Name-"+re.getClientName(), source, claimTypeEnum);
								continue;
							}
						}
						
						RcmOffice off= officeRepo.findByCompanyAndName(companies.get(re.getClientName()), re.getOfficeName());
						if (off==null) {
							ruleEngineService.saveRcmIssueClaim(re.getClaimId(), null, user, "Wrong Office Name-"+re.getOfficeName() +"For "+re.getClientName(), source, claimTypeEnum);
							continue;
						}
						
						List<String> allCl = Arrays.asList(re.getClaimId() + claimTypeEnum.getSuffix());
						List<RcmClaims> claims = rcmClaimRepository.findByClaimIdInAndOffice(allCl, off);
						UserAssignOffice assignedUser = userAssignOfficeRepo
								.findByOfficeUuidAndTeamId(off.getUuid(), RcmTeamEnum.BILLING.getId());

						
						//RcmInsuranceType rcmInsuranceType = null;
						if (claims == null || claims.size() == 0) {
							//List<String> buildError1=null;
							// Fresh Claims
						
						if (claimTypeEnum.getType().equals(Constants.insuranceTypeSecondary)) {
						  //Check for Corresponding Primary Claim in Case of secondary
						  RcmClaims primaryClaim=	rcmClaimRepository.findByClaimIdAndOffice(re.getClaimId()+ClaimTypeEnum.P.getSuffix(), off);
						  if (primaryClaim==null) {
						      //no need to save this as primary is not present..
							  ruleEngineService.saveRcmIssueClaim(re.getClaimId(), off, user,"Primary not Present", source, claimTypeEnum);
							  continue;
							  
						   }
						}
							
							claim = new RcmClaims();
							List<String> error= new ArrayList<>();
							System.out.println(re.getSecondaryInsuranceCompany());
							List<RcmInsurance> insL = insuranceRepo
									.findByNameAndOfficeAndActive(re.getSecondaryInsuranceCompany(), off,true);
							//In Google sheet we do not have InsuranceId so we need to generate. Prefix ==GEN_
							if (insL.size()==2) {
									boolean p=false;
									if (insL.get(0).getInsuranceId().startsWith("GEN_"))p=true;
									if (p) ins=insL.get(1);
									else ins=insL.get(0);
									
									if (ins.getInsuranceId().startsWith("GEN_")){
										//De-activate this
										ins.setActive(false);
										insuranceRepo.save(ins);
									}
									
								}else if (insL.size()==1) {
									ins=insL.get(0);
								}else {
									ins=  new RcmInsurance();
									ins.setActive(true);
									ins.setAddress(re.getSecondaryInsuranceAddress());
									ins.setInsuranceId("GEN_"+new Date().getTime());
									
									rcmInsuranceType = rcmInsuranceTypes.get(re.getSecondaryInsuranceName());
									if (rcmInsuranceType==null) {
										rcmInsuranceType = rcmInsuranceTypeRepo.findByName(re.getSecondaryInsuranceName());
										if (rcmInsuranceType==null) {
											rcmInsuranceType= new RcmInsuranceType();
											rcmInsuranceType.setName(re.getSecondaryInsuranceName());
											rcmInsuranceType.setId(rcmInsuranceTypeRepo.save(rcmInsuranceType).getId());
											rcmInsuranceTypes.put(re.getSecondaryInsuranceName(),rcmInsuranceType);
										}else {
											rcmInsuranceTypes.put(re.getSecondaryInsuranceName(),rcmInsuranceType);
										}
									}
									
									ins.setInsuranceType(rcmInsuranceTypes.get(re.getSecondaryInsuranceName()));
									ins.setName(re.getSecondaryInsuranceCompany());
									ins.setOffice(off);
									ins.setId(insuranceRepo.save(ins).getId());
//									if (insuranceType==null) {
//										error.add( "Insurance Type Missing For for Name:"+re.getSecondaryInsuranceCompany() +" in G-Sheet");
//									}else {
//									ins.setId(insuranceRepo.save(ins).getId());
//									}
								}
								
							
							
							String timely =ClaimUtil.getTimelyLimitFromSheetList(timelyFilingLimits, re.getSecondaryInsuranceCompany());
							if (timely==null) {
								//error.add("Timely Limit Type Missing for Primary Ins. :"+ins.getName());		    
										    
							}
							
							if (systemStatusBilling.getStatus().equalsIgnoreCase(re.getClaimTypeS())){
								claimStatusType = systemStatusBilling;
							}
							if (systemStatusReBilling.getStatus().equalsIgnoreCase(re.getClaimTypeS())){
								claimStatusType = systemStatusReBilling;
							}
							if (claimStatusType==null) {
								error.add("Wrong Claim Type :"+re.getClaimTypeS());
							}
							if (error.size()>0) {
								ruleEngineService.saveRcmIssueClaim(re.getClaimId(), off, user, String.join("\n", error), source, claimTypeEnum);
								 continue;
							}
							newClaimCt++;
							if (claimTypeEnum.getType().equals(Constants.insuranceTypePrimary))newClaimPCt++;
							if (claimTypeEnum.getType().equals(Constants.insuranceTypeSecondary))newClaimSCt++;
							rcmInsuranceType = rcmInsuranceTypeRepo.findById(ins.getInsuranceType().getId());
							claim = ClaimUtil.createClaimFromSheetData(claim, off, re,
									ClaimUtil.filterTeamByNameId(allTeams, RcmTeamEnum.BILLING.toString()), user,
									ins,ins,
									claimStatusType, claimTypeEnum.getSuffix(), rcmInsuranceType,
									timely,claimTypeEnum);
							String claimUUid = rcmClaimRepository.save(claim).getClaimUuid();
							RcmClaimLog l= logMap.get(off.getName());
							if (l==null) {
								l= new RcmClaimLog();
								l.setOffice(off);
								l.setNewClaimsCount(1);
								l.setNewClaimsPrimaryCount(0);
								l.setNewClaimsSecodaryCount(1);
								logMap.put(off.getName(), l);
							}else {
								l.setNewClaimsCount(1+l.getNewClaimsCount());
								l.setNewClaimsSecodaryCount(1+l.getNewClaimsSecodaryCount());
								
							}
							RcmIssueClaims isC=rcmIssueClaimsRepo.findByClaimIdAndOfficeAndSource(re.getClaimId()+claimTypeEnum.getSuffix(), off,source);
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
							
							if (isC!=null){
								isC.setResolved(true);
								rcmIssueClaimsRepo.save(isC);
							}
						}

					} catch (Exception clai) {
						// success = n1.getMessage();
						clai.printStackTrace();
						success = success + "," + re.getClaimId();
						logger.error(clai.getMessage());
					}
				 
					
		     }//For Secondary
			 ClaimLogDto claimLogDto=null;
			 for (Map.Entry<String,RcmClaimLog> entry : logMap.entrySet()) 
			 {
				 
				 claimLogDto= new ClaimLogDto(source, entry.getValue().getOffice().getUuid(), 1, newClaimCt, new Date(), entry.getValue().getOffice().getName());
		            System.out.println("Key = " + entry.getKey() +
		                             ", Value = " + entry.getValue());
			    commonClaimServiceImpl.saveClaimLog(entry.getValue(), user, entry.getValue().getOffice(), ClaimSourceEnum.GOOGLESHEET.toString(), logStatus,
				newClaimCt, newClaimPCt, newClaimSCt, success);
			    mapcountNew.put(entry.getKey() , claimLogDto) ;
			 
		    }
			 
			 if (rcmOffices!=null && rcmOffices.size()>0) {
				 for(RcmOffice p:rcmOffices) {
					 if (mapcountNew.get(p.getUuid())==null) {
						 
						 RcmClaimLog l= new RcmClaimLog();
							l.setOffice(p);
							l.setNewClaimsCount(0);
							l.setNewClaimsPrimaryCount(0);
							l.setNewClaimsSecodaryCount(0);
							
						 claimLogDto= new ClaimLogDto(source,  p.getUuid(), 1, 0, new Date(), p.getName());
						 commonClaimServiceImpl.saveClaimLog(l, user, p, ClaimSourceEnum.GOOGLESHEET.toString(), 1, 0, 0, 0, "success");
				  	    mapcountNew.put(p.getUuid() , claimLogDto) ;
					 }
				 }
				
			 }
			 if (rcmOffices==null) {
				 //need to Store logs look for all the offices
				 officeRepo.findByCompany(clCompany);
				 //ClaimLogDto claimLogDto=null;
				 for (Map.Entry<String,RcmCompany> entry : companies.entrySet()) 
				 {
					
					List<RcmOffice> allOffices = officeRepo.getByCompany(entry.getValue());
					 for(RcmOffice exo:allOffices) {
						 
						if (mapcountNew.get(exo.getUuid())==null){
							
							 RcmClaimLog l= new RcmClaimLog();
								l.setOffice(exo);
								l.setNewClaimsCount(0);
								l.setNewClaimsPrimaryCount(0);
								l.setNewClaimsSecodaryCount(0);
							 claimLogDto= new ClaimLogDto(source, exo.getUuid(), 1, 0, new Date(), exo.getName());
							 commonClaimServiceImpl.saveClaimLog(l, user, exo, ClaimSourceEnum.GOOGLESHEET.toString(), 1, 0, 0, 0, "success");
					  	     mapcountNew.put(exo.getUuid() , claimLogDto) ;
					  	    
						}
						 
					 }
					
				 
			    }
				 
				 
			 }
			
			 
			}
			
			for (Map.Entry<String,ClaimLogDto> entry : mapcountNew.entrySet()) {
				
				logClaimDtos.add(entry.getValue());
				
			 }
		} catch (Exception n) {
			logger.error("Error in Fetching Claims From Sheet.. ");
			logger.error(n.getMessage());
			success = n.getMessage();
		}

		return logClaimDtos;
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
	 
	 
	 
     public FreshClaimDataImplDto fetchIndividualClaim(String  claimId,RcmCompany company) {
    	 FreshClaimDataImplDto implDto=null;
    	 RcmClaimDetailDto dto=  rcmClaimRepository.fetchIndividualClaim(company.getUuid(), claimId);
    	 if (dto!=null) {
    		 
    		 implDto= new FreshClaimDataImplDto();
    		 BeanUtils.copyProperties(dto, implDto);
    		List<String> linkedClaims =rcmLinkedClaimsRepo.getLinkedClaims(dto.getUuid());
    		String ivfId="",tpId="";
    		
    		ivfId=rcmClaimRepository.getIVIDofClaim(implDto.getClaimId().split("_")[0], implDto.getOfficeUuid(), implDto.getPatientId());
    		if (ivfId!=null && !ivfId.equals("")) {
    			tpId= rcmClaimRepository.getTreatmentPlanIdIV(ivfId, implDto.getOfficeUuid(),  implDto.getPatientId());
    		}else {
    			ivfId="";
    		}
    		implDto.setIvfId(ivfId); 
    		implDto.setTpId(tpId);
    		
    		if (linkedClaims!=null) implDto.setLinkedClaims(linkedClaims);
    	 }
		 return implDto;
		 
	 }
	 
	 public List<ProductionDto> claimsProductionReportByTeam(int teamId,ClaimProductionLogDto dto) {
		 
		 Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			Object principal = authentication.getPrincipal();
			final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
			JwtUser jwtUser = (JwtUser) userDetails;
			
			
		 rcmClaimRepository.claimProductionByTeamMember(jwtUser.getCompany().getUuid(), teamId,dto.getStartDate(),dto.getEndDate());
		 return null;
		 
	 }
	 
	
}
