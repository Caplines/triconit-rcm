package com.tricon.rcm.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.tricon.rcm.dto.RcmClaimMainRootDto;
import com.tricon.rcm.dto.RcmInsuranceDatas;
import com.tricon.rcm.dto.RcmInsuranceMainRootDto;
import com.tricon.rcm.dto.RcmIvFMainRootDto;
import com.tricon.rcm.dto.RemoteLietStatusCount;
import com.tricon.rcm.dto.TimelyFilingLimitDto;
import com.tricon.rcm.dto.customquery.ClaimXDaysDto;
import com.tricon.rcm.dto.customquery.PendingClaimToReAssignDto;
import com.tricon.rcm.dto.customquery.RuleEngineClaimDto;
import com.tricon.rcm.enums.ClaimSourceEnum;
import com.tricon.rcm.enums.ClaimStatusEnum;
import com.tricon.rcm.enums.ClaimTypeEnum;
import com.tricon.rcm.enums.RcmTeamEnum;
import com.tricon.rcm.jpa.repository.RCMUserRepository;
import com.tricon.rcm.jpa.repository.RcmClaimAssignmentRepo;
import com.tricon.rcm.jpa.repository.RcmClaimDetailRepo;
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
import com.tricon.rcm.security.JwtUser;
import com.tricon.rcm.jpa.repository.RcmTeamRepo;
import com.tricon.rcm.util.ClaimUtil;
import com.tricon.rcm.util.ConnectAndReadSheets;
import com.tricon.rcm.util.Constants;
import com.google.common.collect.Collections2;
import com.tricon.rcm.db.entity.RcmClaimAssignment;
import com.tricon.rcm.db.entity.RcmClaimDetail;
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
import com.tricon.rcm.dto.AssignOfficesToBillingUserDto;
import com.tricon.rcm.dto.AssignUserOfficeDto;
import com.tricon.rcm.dto.CaplineIVFFormDto;
import com.tricon.rcm.dto.ClaimAppointmentDto;
import com.tricon.rcm.dto.ClaimDataDetails;
import com.tricon.rcm.dto.ClaimDetailDto;
import com.tricon.rcm.dto.ClaimSourceDto;
import com.tricon.rcm.dto.ClaimsFromRuleEngine;
import com.tricon.rcm.dto.InsuranceFromRuleEngine;
import com.tricon.rcm.dto.InsuranceNameTypeDto;
import com.tricon.rcm.dto.RcmClaimAppointmentDatas;
import com.tricon.rcm.dto.RcmClaimAppointmentMainRootDto;
import com.tricon.rcm.dto.RcmClaimDataDto;
import com.tricon.rcm.dto.RcmClaimDetMainRootDto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.data.repository.query.Param;
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
	
	@Autowired
	RcmClaimDetailRepo rcmClaimDetailRepo;

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
			List<TimelyFilingLimitDto> timelyFilingLimits, ClaimTypeEnum claimTypeEnum, RcmCompany comp, int logId) {

		String success = "";
		// int logId = -1;
		String source = ClaimSourceEnum.EAGLESOFT.toString();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		RcmInsurance ins = null;
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
			RcmClaimStatusType systemStatusBilling = rcmClaimStatusTypeRepo
					.findByStatus(ClaimStatusEnum.Billing.getType());
			dto.setPassword(eagleSoftDBDetailsRepo.findByOffice(off).getPassword());
			HttpEntity<String> entity = new HttpEntity<String>(headers);
			String param = "?password=" + dto.getPassword() + "&primarySecondary=" + claimTypeEnum.getType();
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
					RcmTeam assignedTeamBilling =null;
					RcmTeam assignedTeamInternalAudit =null;
					
					for (RcmClaimDataDto datas : mainRoot.getData().getDatas()) {
						// String officeUuid = datas.getOfficeName();
						// RcmOffice off = officeRepo.findByUuid(officeUuid);
						try {
							for (ClaimsFromRuleEngine re : datas.getData()) {
								try {
									System.out.println(re.getClaimId() + "--<ID");
									InsuranceNameTypeDto insuranceNameTypeDto=null;
									//if (re.getClaimId().equals("5622") || re.getClaimId().equals("3018") || re.getClaimId().equals("32950")) {
									//	System.out.println("k");
									//}
									List<String> allCl = Arrays.asList(re.getClaimId() + claimTypeEnum.getSuffix());
									List<RcmClaims> claims = rcmClaimRepository.findByClaimIdInAndOffice(allCl, off);
									
									UserAssignOffice assignedUserBilling = userAssignOfficeRepo
											.findByOfficeUuidAndTeamId(off.getUuid(), RcmTeamEnum.BILLING.getId());
									UserAssignOffice assignedUserInternalAudit = userAssignOfficeRepo
											.findByOfficeUuidAndTeamId(off.getUuid(), RcmTeamEnum.INTERNAL_AUDIT.getId());
									
									assignedTeamBilling = rcmTeamRepo.findById(RcmTeamEnum.BILLING.getId());
									assignedTeamInternalAudit= rcmTeamRepo.findById(RcmTeamEnum.INTERNAL_AUDIT.getId());
									RcmInsuranceType rcmInsuranceType = null;
									if (claims == null || claims.size() == 0) {
										// List<String> buildError1=null;
										// Fresh Claims

										if (claimTypeEnum.getType().equals(Constants.insuranceTypeSecondary)) {
											// Check for Corresponding Primary Claim in Case of secondary
											RcmClaims primaryClaim = rcmClaimRepository.findByClaimIdAndOffice(
													re.getClaimId() + ClaimTypeEnum.P.getSuffix(), off);
											if (primaryClaim == null) {
												// no need to save this as primary is not present..
												saveRcmIssueClaim(re.getClaimId(), off, user, "Primary not Present",
														source, claimTypeEnum);
												continue;

											}
										}

										claim = new RcmClaims();
										List<String> error = new ArrayList<>();
										//System.out.println(re.getPrimSecInsuranceCompanyId());
										ins = insuranceRepo
												.findByInsuranceIdAndOffice(re.getPrimSecInsuranceCompanyId(), off);
										if (ins == null) {
											ins = new RcmInsurance();
											ins.setActive(true);
											ins.setAddress(re.getInsuranceCompanyFullAddress());
											//ins.setInsuranceCode(re.getInsuranceCompanyFullAddress());
											ins.setInsuranceId(re.getPrimSecInsuranceCompanyId());
											insuranceNameTypeDto= getInsuranceTypeFromSheetListByNameAndClient(insuranceTypeDto, re.getInsuranceCompanyName().trim(),
													Constants.COMPANY_NAME);
											//String insuranceType = getInsuranceTypeFromSheetList(insuranceTypeDto,
											//		re.getInsuranceCompanyName());
											String insuranceType =insuranceNameTypeDto==null?null: insuranceNameTypeDto.getInsuranceType();
											
											if (insuranceType == null) {
												error.add("Insurance Type Missing For for Name:"
														+ re.getInsuranceCompanyName() + " in G-Sheet");
											} else {
												ins.setInsuranceType(rcmInsuranceTypeRepo.findByCode(insuranceType));
												ins.setName(re.getInsuranceCompanyName());
												ins.setOffice(off);
												ins.setId(insuranceRepo.save(ins).getId());
											}
											// error.add( "Primary Insurance Missing for
											// id:"+re.getPrimInsuranceCompanyId());
										}else {
											if (ins.getInsuranceCode()==null) {
												insuranceNameTypeDto= getInsuranceTypeFromSheetListByNameAndClient(insuranceTypeDto, re.getInsuranceCompanyName().trim(),
														Constants.COMPANY_NAME);
												if (insuranceNameTypeDto!=null) {
													ins.setInsuranceCode(insuranceNameTypeDto.getInsuranceCode());
													insuranceRepo.save(ins);
												}
												
											}
										}
										// if (primaryIns.getInsuranceType()==null) {
										// error.add("Primary Insurance Type Missing for
										// id:"+re.getPrimInsuranceCompanyId()+" and Name: "+primaryIns.getName());
										// }
										TimelyFilingLimitDto timely=null;
                                        if (insuranceNameTypeDto!=null) {
                                        	timely = ClaimUtil.getTimelyLimitFromSheetListByCode(timelyFilingLimits,
                                        			insuranceNameTypeDto.getInsuranceCode().trim());
                                        }else if(ins.getInsuranceCode()!=null){
                                        	insuranceNameTypeDto= getInsuranceTypeFromSheetListByNameAndClient(insuranceTypeDto, re.getInsuranceCompanyName().trim(),Constants.COMPANY_NAME);
											if (insuranceNameTypeDto!=null) {
                                        	timely = ClaimUtil.getTimelyLimitFromSheetListByCode(timelyFilingLimits,
                                        			ins.getInsuranceCode().trim());
											}
                                        }
										
										if (timely == null) {
											error.add("Timely Limit Type Missing for  Ins. :" + re.getInsuranceCompanyName().trim());

										}

										// int insuranceId = primaryIns.getInsuranceType().getId();
										// rcmInsuranceType = rcmInsuranceTypeRepo.findById(insuranceId);
										// if (rcmInsuranceType==null) {
										// error.add("Primary Ins. missing For:"+re.getPrimInsuranceCompanyId());

										// }
										if (error.size() > 0) {
											saveRcmIssueClaim(re.getClaimId(), off, user, String.join("\n", error),
													source, claimTypeEnum);
											continue;
										}
										newClaimCt++;
										if (claimTypeEnum.getType().equals(Constants.insuranceTypePrimary))
											newClaimPCt++;
										if (claimTypeEnum.getType().equals(Constants.insuranceTypeSecondary))
											newClaimSCt++;
										rcmInsuranceType = rcmInsuranceTypeRepo
												.findById(ins.getInsuranceType().getId());
										
										boolean isBilling=ClaimUtil.isBillingClaimByInsuranceName(ins.getInsuranceType().getName());
										boolean isMedicaid=ClaimUtil.isMedcaidClaimByInsuranceName(ins.getInsuranceType().getName());
										boolean isMedicare=ClaimUtil.isMedicareClaimByInsuranceName(ins.getInsuranceType().getName());
										boolean isChip=ClaimUtil.isChipClaimByInsuranceName(ins.getInsuranceType().getName());
										boolean missing=true;
										try {
										if (insuranceNameTypeDto!=null && insuranceNameTypeDto.getPreferredModeOfSubmission()==null) {
											InsuranceNameTypeDto temp = getInsuranceTypeFromSheetListByNameAndClient(insuranceTypeDto, re.getInsuranceCompanyName().trim(),Constants.COMPANY_NAME);
											insuranceNameTypeDto.setPreferredModeOfSubmission(temp.getPreferredModeOfSubmission());
										}
										}catch(Exception s) {
											logger.error(s.getMessage());
										}
										if (isBilling) {
										claim = ClaimUtil.createClaimFromESData(claim, off, re,
												ClaimUtil.filterTeamByNameId(allTeams, RcmTeamEnum.BILLING.toString()),
												user, ins, ins, systemStatusBilling, claimTypeEnum.getSuffix(),
												rcmInsuranceType, timely.getTimelyFilingLimit(),insuranceNameTypeDto.getPreferredModeOfSubmission(), claimTypeEnum);
										missing=false;
										}
										if (isMedicaid || isMedicare || isChip) {
											claim = ClaimUtil.createClaimFromESData(claim, off, re,
													ClaimUtil.filterTeamByNameId(allTeams, RcmTeamEnum.INTERNAL_AUDIT.toString()),
													user, ins, ins, systemStatusBilling, claimTypeEnum.getSuffix(),
													rcmInsuranceType, timely.getTimelyFilingLimit(),insuranceNameTypeDto.getPreferredModeOfSubmission(), claimTypeEnum);
											missing=false;
										}
										
										if(missing) {//no Billing or Medicaid
											//put in billing 
											claim = ClaimUtil.createClaimFromESData(claim, off, re,
													ClaimUtil.filterTeamByNameId(allTeams, RcmTeamEnum.BILLING.toString()),
													user, ins, ins, systemStatusBilling, claimTypeEnum.getSuffix(),
													rcmInsuranceType, timely.getTimelyFilingLimit(),insuranceNameTypeDto.getPreferredModeOfSubmission(), claimTypeEnum);
											isBilling=true;
										}
										String claimUUid = rcmClaimRepository.save(claim).getClaimUuid();
										RcmIssueClaims isC = rcmIssueClaimsRepo.findByClaimIdAndOfficeAndSource(
												re.getClaimId() + claimTypeEnum.getSuffix(), off, source);
										if (isC != null) {
											isC.setResolved(true);
											rcmIssueClaimsRepo.save(isC);
										}
										/// createAssginmentData
										if (assignedUserBilling != null && isBilling) {
											rcmAssigment = new RcmClaimAssignment();
											//
											rcmAssigment = ClaimUtil.createAssginmentData(rcmAssigment, user,
													assignedUserBilling.getUser(), claimUUid, claim,
													"", systemStatusBilling,assignedTeamBilling,Constants.SYSTEM_INITIAL_COMMENT);

											rcmClaimAssignmentRepo.save(rcmAssigment);
										}
										if (assignedUserInternalAudit != null && (isMedicaid|| isMedicare || isChip)) {
											rcmAssigment = new RcmClaimAssignment();
											//
											rcmAssigment = ClaimUtil.createAssginmentData(rcmAssigment, user,
													assignedUserInternalAudit.getUser(), claimUUid, claim,
													"", systemStatusBilling,assignedTeamInternalAudit,Constants.SYSTEM_INITIAL_COMMENT);

											rcmClaimAssignmentRepo.save(rcmAssigment);
										}
										/*
										 * if (re.getSecStatus().equalsIgnoreCase(Constants.secondaryClaimTypeES)) {
										 * newClaimCt++; newClaimSCt++; claim = new RcmClaims(); error= new
										 * ArrayList<>(); RcmInsurance secondarySec = insuranceRepo
										 * .findByInsuranceIdAndOffice(re.getSecInsuranceCompanyId(), off);
										 * 
										 * if (secondarySec==null &&
										 * !re.getSecInsuranceCompanyId().equals(Constants.NO_DATA)) {
										 * error.add("Secondary Insurance Missing for id:"+re.getSecInsuranceCompanyId()
										 * ); } if (secondarySec.getInsuranceType()==null &&
										 * !re.getSecInsuranceCompanyId().equals(Constants.NO_DATA)) {
										 * error.add("Secondary Insurance Type Missing for id:"+re.
										 * getSecInsuranceCompanyId()+" and Name: "+secondarySec.getName()); }
										 * 
										 * timely =ClaimUtil.getTimelyLimitFromSheetList(timelyFilingLimits,
										 * secondarySec.getName() ); if (timely==null) {
										 * error.add("Timely Limit Type Missing for Secondary Ins. :"+secondarySec.
										 * getName()); }
										 * 
										 * 
										 * insuranceId = secondarySec.getInsuranceType().getId(); rcmInsuranceType =
										 * rcmInsuranceTypeRepo.findById(insuranceId);
										 * 
										 * if (rcmInsuranceType==null) {
										 * error.add("Secondary Ins. missing For:"+re.getSecInsuranceCompanyId());
										 * 
										 * } if (error.size()>0) { saveRcmIssueClaim(re, buildError, off,user,
										 * String.join("\n", error),source); continue; }
										 * 
										 * 
										 * claim = ClaimUtil.createClaimFromESData(claim, off, re,
										 * ClaimUtil.filterTeamByNameId(allTeams, RcmTeamEnum.BILLING.toString()), user,
										 * insuranceRepo.findByInsuranceIdAndOffice( re.getPrimInsuranceCompanyId(),
										 * off), secondarySec, systemStatusBilling, ClaimTypeEnum.S.getSuffix(),
										 * rcmInsuranceType,timely); claimUUid =
										 * rcmClaimRepository.save(claim).getClaimUuid(); if (assignedUser != null) {
										 * rcmAssigment = new RcmClaimAssignment(); rcmAssigment =
										 * ClaimUtil.createAssginmentData(rcmAssigment, user, assignedUser.getUser(),
										 * claimUUid, claim, Constants.SYSTEM_INITIAL_COMMENT, systemStatusBilling);
										 * rcmClaimAssignmentRepo.save(rcmAssigment); } }
										 */
										if (isC != null) {
											isC.setResolved(true);
											rcmIssueClaimsRepo.save(isC);
										}
									} else {
										// OLD Claims Exists
										/*
										 * boolean secondaryPresent = false; boolean primaryPresent = false;
										 * List<String> buildError=null; for (RcmClaims oldClaims : claims) {
										 * 
										 * if (oldClaims.getClaimId() .equalsIgnoreCase(re.getClaimId() +
										 * ClaimTypeEnum.P.getSuffix())) { primaryPresent = true; } if
										 * (oldClaims.getClaimId() .equalsIgnoreCase(re.getClaimId() +
										 * ClaimTypeEnum.S.getSuffix())) { secondaryPresent = true; } }
										 * 
										 * if (!primaryPresent) { List<String> error= new ArrayList<>();
										 * 
										 * newClaimCt++; newClaimPCt++; claim = new RcmClaims(); RcmInsurance primarySec
										 * = insuranceRepo .findByInsuranceIdAndOffice(re.getPrimInsuranceCompanyId(),
										 * off); if (primarySec==null) {
										 * error.add("Primary Insurance Missing for id:"+re.getPrimInsuranceCompanyId())
										 * ; }
										 * 
										 * if (primarySec.getInsuranceType()==null) {
										 * error.add("Primary Insurance Type Missing for id:"+re.
										 * getPrimInsuranceCompanyId()+" and Name: "+primarySec.getName()); } String
										 * timely =ClaimUtil.getTimelyLimitFromSheetList(timelyFilingLimits,
										 * primarySec.getName());
										 * 
										 * if (timely==null) {
										 * error.add("Timely Limit Type Missing for Primary Ins. :"+primarySec.getName()
										 * ); } int insuranceId = primarySec.getInsuranceType().getId();
										 * rcmInsuranceType = rcmInsuranceTypeRepo.findById(insuranceId);
										 * 
										 * if (rcmInsuranceType==null) {
										 * error.add("Primary Ins. missing For:"+re.getPrimInsuranceCompanyId());
										 * 
										 * } if (error.size()>0) { saveRcmIssueClaim(re, buildError, off,user,
										 * String.join("\n", error),source); continue; } claim =
										 * ClaimUtil.createClaimFromESData(claim, off, re,
										 * ClaimUtil.filterTeamByNameId(allTeams, RcmTeamEnum.BILLING.toString()), user,
										 * insuranceRepo.findByInsuranceIdAndOffice( re.getPrimInsuranceCompanyId(),
										 * off), insuranceRepo.findByInsuranceIdAndOffice(
										 * re.getSecInsuranceCompanyId(), off), systemStatusBilling,
										 * ClaimTypeEnum.P.getSuffix(), rcmInsuranceType,timely); String claimUUid =
										 * rcmClaimRepository.save(claim).getClaimUuid(); RcmIssueClaims
										 * isC=rcmIssueClaimsRepo.findByClaimIdAndOfficeAndSource(re.getClaimId(),
										 * off,source); if (isC!=null){ isC.setResolved(true);
										 * rcmIssueClaimsRepo.save(isC); } if (assignedUser != null) { rcmAssigment =
										 * new RcmClaimAssignment(); rcmAssigment =
										 * ClaimUtil.createAssginmentData(rcmAssigment, user, assignedUser.getUser(),
										 * claimUUid, claim, Constants.SYSTEM_INITIAL_COMMENT, systemStatusBilling);
										 * rcmClaimAssignmentRepo.save(rcmAssigment); } } if (!secondaryPresent) {
										 * newClaimCt++; newClaimSCt++; claim = new RcmClaims(); List<String> error= new
										 * ArrayList<>(); RcmInsurance secondarySec = insuranceRepo
										 * .findByInsuranceIdAndOffice(re.getSecInsuranceCompanyId(), off); if
										 * (secondarySec==null &&
										 * !re.getSecInsuranceCompanyId().equals(Constants.NO_DATA)) {
										 * error.add("Secondary Insurance Missing for id:"+re.getSecInsuranceCompanyId()
										 * ); }
										 * 
										 * if (secondarySec!=null && (secondarySec.getInsuranceType()==null ||
										 * !re.getSecInsuranceCompanyId().equals(Constants.NO_DATA))) {
										 * error.add("Secondary Insurance Type Missing for id:"+re.
										 * getSecInsuranceCompanyId()+" and Name: "+secondarySec.getName()); }
										 * 
										 * String timely =null; if (secondarySec!=null)
										 * ClaimUtil.getTimelyLimitFromSheetList(timelyFilingLimits,
										 * secondarySec.getName() ); if (timely==null && secondarySec!=null) {
										 * error.add("Timely Limit Type Missing for Secondary Ins. :"+secondarySec.
										 * getName()); }
										 * 
										 * int insuranceId
										 * =secondarySec!=null?secondarySec.getInsuranceType().getId():-1;
										 * rcmInsuranceType = rcmInsuranceTypeRepo.findById(insuranceId);
										 * 
										 * if (rcmInsuranceType==null) {
										 * error.add("Secondary Ins. missing For:"+re.getSecInsuranceCompanyId());
										 * 
										 * } if (error.size()>0) { saveRcmIssueClaim(re, buildError, off,user,
										 * String.join("\n", error),source); continue; }
										 * 
										 * claim = ClaimUtil.createClaimFromESData(claim, off, re,
										 * ClaimUtil.filterTeamByNameId(allTeams, RcmTeamEnum.BILLING.toString()), user,
										 * insuranceRepo.findByInsuranceIdAndOffice( re.getPrimInsuranceCompanyId(),
										 * off), insuranceRepo.findByInsuranceIdAndOffice(
										 * re.getSecInsuranceCompanyId(), off), systemStatusBilling,
										 * ClaimTypeEnum.S.getSuffix(), rcmInsuranceType, timely); String claimUUid =
										 * rcmClaimRepository.save(claim).getClaimId(); RcmIssueClaims
										 * isC=rcmIssueClaimsRepo.findByClaimIdAndOfficeAndSource(re.getClaimId(),
										 * off,source); if (isC!=null){ isC.setResolved(true);
										 * rcmIssueClaimsRepo.save(isC); } if (assignedUser != null) { rcmAssigment =
										 * new RcmClaimAssignment(); rcmAssigment =
										 * ClaimUtil.createAssginmentData(rcmAssigment, user, assignedUser.getUser(),
										 * claimUUid, claim, Constants.SYSTEM_INITIAL_COMMENT, systemStatusBilling);
										 * rcmClaimAssignmentRepo.save(rcmAssigment); } }
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
				} else {
					// Office Not working ...

				}
			}

			if (success.equals(""))
				success = Constants.ClAIM_PULLED_SUCCESS;
			RcmClaimLog log = null;/// new RcmClaimLog();

			if (logId != -1) {
				Optional<RcmClaimLog> opt = rcmClaimLogRepo.findById(logId);
				if (opt.isPresent())
					log = opt.get();
			}
			if (log == null) {
				log = new RcmClaimLog();
				logId = commonClaimServiceImpl.saveClaimLog(log, user, off, ClaimSourceEnum.EAGLESOFT.toString(),
						logStatus, newClaimCt, newClaimPCt, newClaimSCt, success);
			} else {
				log.setNewClaimsCount(newClaimCt + log.getNewClaimsCount());
				log.setNewClaimsPrimaryCount(newClaimPCt + log.getNewClaimsPrimaryCount());
				log.setNewClaimsSecodaryCount(newClaimSCt + log.getNewClaimsSecodaryCount());

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
			if (rootDto.getData().getDatas() != null) {
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
						InsuranceNameTypeDto insuranceNameTypeDto= getInsuranceTypeFromSheetListByNameAndClient(insuranceTypeDto, re.getName().trim(),
								Constants.COMPANY_NAME);
						String insuranceType =insuranceNameTypeDto==null?null: insuranceNameTypeDto.getInsuranceType();
						if (insuranceType != null) {
							insurance.setInsuranceType(rcmInsuranceTypeRepo.findByName(insuranceType));
						}
						insuranceOld = insuranceRepo.findByInsuranceIdAndOffice(re.getInsuranceCompanyId(),
								insurance.getOffice());
						insurance.setActive(true);
						if (insuranceOld == null)
							insuranceRepo.save(insurance);
						else {
							insuranceNameTypeDto= getInsuranceTypeFromSheetListByNameAndClient(insuranceTypeDto, insurance.getName().trim()
									,Constants.COMPANY_NAME);
							//insuranceType = getInsuranceTypeFromSheetList(insuranceTypeDto, insurance.getName());
							insuranceType =insuranceNameTypeDto==null?null: insuranceNameTypeDto.getInsuranceType();
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
	public HashMap<String, RemoteLietStatusCount> pullAndSaveRemoteLiteData() {

		logger.info(" In pullRemoteLiteDate");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			// user = userRepo.findByEmail(authentication.getName());
		}
		// RcmClaimLog log = null;
		// RcmRemoteStatusCount ct = new RcmRemoteStatusCount();
		// RcmRemoteStatusCountDto ctDto = new RcmRemoteStatusCountDto();
		// if (rcmClaimLogId != -1) {
		// try {
		// log = rcmClaimLogRepo.findById(Integer.valueOf(rcmClaimLogId)).get();
		// } catch (Exception n) {
		// logger.info("RcmClaimLog not found for id -" + rcmClaimLogId);
		// }
		// }
		HashMap<String, RemoteLietStatusCount> map = new HashMap<>();
		try {
			/*
			 * HttpEntity<String> entity = new HttpEntity<String>(headers);
			 * 
			 * String param = "";// "?password=" + dto.getPassword(); if
			 * (dto.getOfficeuuid() != null) param = param + "?office=" +
			 * dto.getOfficeuuid();
			 * 
			 * // Call Rule Engine API.. ResponseEntity<RcmRemoteLiteMainRootDto> result =
			 * restTemplate.exchange( ev.getProperty("rcm.pullremoteliteurl") + param,
			 * HttpMethod.GET, entity, RcmRemoteLiteMainRootDto.class);
			 * 
			 * System.out.println(result.getBody()); RcmRemoteLiteMainRootDto rootDto =
			 * result.getBody();
			 */
			try {
				/*
				 * List<RcmRemoteLiteSiteDetailsDto> data = rootDto.getData(); for
				 * (RcmRemoteLiteSiteDetailsDto dto1 : data) { RemoteLiteDataDto dataDtao =
				 * ConnectAndReadSheets.readRemoteLiteSheet(dto1.getGoogleSheetIdDb(),
				 * dto1.getPassword(), CLIENT_SECRET_DIR, CREDENTIALS_FOLDER);
				 * map.put(dto1.getOfficeId(), dataDtao);
				 * 
				 * RcmOffice office = officeRepo.findByUuid(dto1.getOfficeId());
				 * 
				 * BeanUtils.copyProperties(dataDtao.getStatusCount(), ct);
				 * BeanUtils.copyProperties(dataDtao.getStatusCount(), ctDto); //
				 * ct.setCreatedDate(new Date()); ct.setOffice(office); ct.setCreatedBy(user);
				 * ct.setRcmClaimLog(log); rcmRemoteLiteRepo.save(ct);
				 * 
				 * }
				 */
				map = ConnectAndReadSheets.readRemoteLiteSheet("1KVaZbAfaYOGMbYRZuGH-4VVxeZQPIvML0YThPsPNTnw",
						"Combined", CLIENT_SECRET_DIR, CREDENTIALS_FOLDER);
				// map.put(dto1.getOfficeId(), dataDtao);

			} catch (Exception n) {
				n.printStackTrace();

			}
		} catch (Exception n) {
			// logger.error("Error in " + dto.getOfficeuuid());
			// logger.error(n.getMessage());
		}

		return map;
	}

	public List<TimelyFilingLimitDto> pullTimelyFilingLmtMappingFromSheet(RcmCompany company) {

		logger.info(" In TimelyFilingLimitDto");
		RcmMappingTable table = rcmMappingTableRepo.findByNameAndCompany(Constants.RCM_MAPPING_TIMELY_LIMIT, company);
		List<TimelyFilingLimitDto> li = null;
		try {
			li = ConnectAndReadSheets.readTimelyFilingLimitMappingSheet(table.getGoogleSheetId(),
					table.getGoogleSheetSubName(), CLIENT_SECRET_DIR, CREDENTIALS_FOLDER);

		} catch (Exception n) {
			logger.error("Error in Fetching TimelyFilingLimit ");
			logger.error(n.getMessage());
		}

		return li;
	}

	
	public CaplineIVFFormDto pullIVFDataFromRE(String ivfId, String patientId, String companyId, String officeId) {

		logger.info(" In pull IVF DATA From RE");
		CaplineIVFFormDto dto = null;
		try {
			HttpEntity<String> entity = new HttpEntity<String>(headers);
			String param = "?cmpId=" + companyId;
			param = param + "&office=" + officeId;
			param = param + "&ivId=" + ivfId;
			param = param + "&patientId=" + patientId;
			// Call Rule Engine API..
			ResponseEntity<RcmIvFMainRootDto> result = restTemplate.exchange(ev.getProperty("rcm.ivfdataurl") + param,
					HttpMethod.GET, entity, RcmIvFMainRootDto.class);

			RcmIvFMainRootDto rootDto = result.getBody();
			if (rootDto.getData() != null && rootDto.getData().size() > 0) {
				dto = rootDto.getData().get(0);
			}

		} catch (Exception n) {
			logger.error("Error in " + ivfId);
			logger.error(n.getMessage());
		}

		// System.out.println(rootDto.getData().getDatas().get(0).getName());

		return dto;
	}
	
	public List<ClaimDetailDto> pullClaimDetailFromFromRE(String claimId, String companyId, String officeId) {

		logger.info(" In pull ClaimDetail From RE");
		List<ClaimDetailDto> dto = null;
		try {
			HttpEntity<String> entity = new HttpEntity<String>(headers);
			String param = "?cmpId=" + companyId;
			param = param + "&office=" + officeId;
			param = param + "&claimId=" + claimId;
			// Call Rule Engine API..
			ResponseEntity<RcmClaimDetMainRootDto> result = restTemplate.exchange(ev.getProperty("rcm.claimESdataurl") + param,
					HttpMethod.GET, entity, RcmClaimDetMainRootDto.class);

			RcmClaimDetMainRootDto rootDto = result.getBody();
			if (rootDto.getData() != null && rootDto.getData().size() > 0) {
				dto = rootDto.getData().get(claimId);
			}

		} catch (Exception n) {
			logger.error("Error in " + claimId);
			logger.error(n.getMessage());
		}

		return dto;
	}
	
	public List<ClaimDetailDto> pullTPDetailFromFromRE(String tpId, String companyId, String officeId) {

		logger.info(" In pull TPDetail From RE");
		List<ClaimDetailDto> dto = null;
		try {
			HttpEntity<String> entity = new HttpEntity<String>(headers);
			String param = "?cmpId=" + companyId;
			param = param + "&office=" + officeId;
			param = param + "&tpId=" + tpId;
			// Call Rule Engine API..
			ResponseEntity<RcmClaimDetMainRootDto> result = restTemplate.exchange(ev.getProperty("rcm.tpESdataurl") + param,
					HttpMethod.GET, entity, RcmClaimDetMainRootDto.class);

			RcmClaimDetMainRootDto rootDto = result.getBody();
			if (rootDto.getData() != null && rootDto.getData().size() > 0) {
				dto = rootDto.getData().get(tpId);
			}

		} catch (Exception n) {
			logger.error("Error in " + tpId);
			logger.error(n.getMessage());
		}

		return dto;
	}
	

	public void saveRcmIssueClaim(String claimId, RcmOffice off, RcmUser user, String error, String source,
			ClaimTypeEnum claimTypeEnum) {

		RcmIssueClaims isC = rcmIssueClaimsRepo.findByClaimIdAndOfficeAndSource(claimId + claimTypeEnum.getSuffix(),
				off, source);
		if (isC == null) {
			isC = new RcmIssueClaims();
			isC.setClaimId(claimId + claimTypeEnum.getSuffix());
			isC.setOffice(off);
			isC.setSource(source);
			isC.setResolved(false);
			isC.setIssue(error);
			isC.setCreatedBy(user);
			rcmIssueClaimsRepo.save(isC);
		} else {
			isC.setIssue(error);
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
	public List<InsuranceNameTypeDto> pullInsuranceMappingFromSheet(RcmCompany company) {

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
	public InsuranceNameTypeDto getInsuranceTypeFromSheetListByNameAndClient(List<InsuranceNameTypeDto> sheetData,
			String name, String clientName) {
		//String insuranceType = null;
		InsuranceNameTypeDto dto= null;
		if (sheetData == null) {
			logger.error("Data From Mapping sheet not found");
			return null;
		}
		Collection<InsuranceNameTypeDto> ruleGen = Collections2.filter(sheetData,
				sh -> sh.getInsuranceName().trim().equalsIgnoreCase(name.trim())
				     && sh.getClientName().trim().equalsIgnoreCase(clientName)
				     );
		for (InsuranceNameTypeDto gs : ruleGen) {
			dto  = gs;
			break;
		}
		if (dto == null) {
			logger.error(name + " Not found in  Google sheet");

		}
		return dto;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public boolean assignedUnsAssignedClaimsByTeam(String companyId,RcmUser assignedBy,int teamId)  {
		
		//Assign Unassigned Claims
		try {
		List<String> claims =rcmClaimRepository.getUnAsignedClaims(companyId);
		RcmClaimStatusType systemStatusBilling = rcmClaimStatusTypeRepo
				.findByStatus(ClaimStatusEnum.Billing.getType());
		RcmTeam assignedTeam  = rcmTeamRepo.findById(teamId);
		logger.info(claims.size()+"");
		int ct=0;
		for(String claimUUid:claims) {
			logger.info("MY COUNT--"+ (++ct));
			RcmClaimAssignment rcmAssigment = new RcmClaimAssignment();
			//
			RcmClaims claim = rcmClaimRepository.findByClaimUuid(claimUUid);
			//if (claim.getFirstWorkedTeamId().getId() == teamId) {
			UserAssignOffice assignedUser = userAssignOfficeRepo
					.findByOfficeUuidAndTeamId(claim.getOffice().getUuid(), teamId);
			 
			if (assignedUser != null) {
				List<Integer> assignmentId = rcmClaimAssignmentRepo.findIssueAssingments(claimUUid);
				if (assignmentId.size() < 1) {
					rcmAssigment = ClaimUtil.createAssginmentData(rcmAssigment, assignedBy, assignedUser.getUser(),
							claimUUid, claim, "", systemStatusBilling, assignedTeam, Constants.SYSTEM_INITIAL_COMMENT);
					rcmClaimAssignmentRepo.save(rcmAssigment);
				} else {
					logger.error("Unassigned claims(" + claimUUid + ") is already assigned to a user whose team id is: "
							+ teamId + " ");
				}
			}
			//}
			
		}
		}catch(Exception ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Pull Claim Detail from Eagle Soft(Called from Rule Engine).
	 * @param rcmCompany
	 * @param officeUUid =null mean for all offices
	 */
	public void pullClaimDetailsFromES(RcmCompany rcmCompany,String officeUUid) {
	    //Only For Smile Point	
		if (rcmCompany.getName().equals(Constants.COMPANY_NAME)) {
		List<ClaimXDaysDto> claimIds=null;
		if (officeUUid==null)claimIds= rcmClaimRepository.getClaimIdsdWithNoDetailForGivenLastDays(rcmCompany.getName(),Constants.LAST_X_DAYS_TO_CHECK_DET);
		else  claimIds= rcmClaimRepository.getClaimIdsdWithNoDetailForGivenLastDayForOffice(rcmCompany.getName(),Constants.LAST_X_DAYS_TO_CHECK_DET,officeUUid);
			for(ClaimXDaysDto data:claimIds) {
			RcmClaims claim = rcmClaimRepository.findByClaimUuid(data.getClaimUUid());
			List<ClaimDetailDto> cdList = pullClaimDetailFromFromRE(data.getClaimId().split("_")[0], rcmCompany.getUuid(),
					data.getOfficeId());
			if (cdList!=null) {
			RcmClaimDetail rcmClaimDetail=null;
			List<RcmClaimDetail> cddList= new ArrayList<>();
			int tmp=0;
			for(ClaimDetailDto cdt:cdList) {
				
				rcmClaimDetail = new RcmClaimDetail();
				BeanUtils.copyProperties(cdt, rcmClaimDetail, "id");
				rcmClaimDetail.setIdEs(cdt.getId()); 
				rcmClaimDetail.setClaim(claim);
				rcmClaimDetailRepo.save(rcmClaimDetail);
				cddList.add(rcmClaimDetail);
				if (tmp==0) {
					
					ClaimDataDetails cdd= cdt.getDetails();
					claim.setDateLastUpdatedES(cdd.getDateLastUpdated());
					claim.setDescriptionES(cdd.getDescription());
					claim.setEstSecondaryES(cdd.getEstSecondary());
					claim.setStatusES(cdd.getStatus());
					//dto.setEsDate(cdd.getDateLastUpdated());
					
				}
				//dto.setClaimFound(true);
			  }
			rcmClaimRepository.save(claim);	
			}
		}
		
		}
	}
	
	/**
	 * From Pendency Page( Claim Assignment)
	 * This Api Reassigns the The Pending claims to new Users. that are selected 
	 * @param dto 
	 * @param company
	 */
	@Transactional(rollbackFor = Exception.class)
	public void reAssignClaimToUserByOffices(RcmCompany company,
			int teamId,JwtUser jwtUser) {
		
		List<PendingClaimToReAssignDto> claimList= rcmClaimRepository.fetchAllPendingClaimsAssignedToSomeOneByCompanyIdAndTeamId(company.getUuid(),teamId);
		List<UserAssignOffice> userAssignOffices = new ArrayList<>();
		RcmUser assignedBy= userRepo.findByUuid(jwtUser.getUuid()) ;
		for(PendingClaimToReAssignDto cl:claimList) {
			 
			   //Logic added so that we do minimum Data base call to rcm_user_assign_office table
			  // if assigned to Same User and Team Then do not do anything else reassign
				UserAssignOffice exists = userAssignOffices.stream().filter(s -> {
					   return s.getOffice().getUuid().equals(cl.getOfficeId()) && s.getTeam().getId()==teamId ;
					   }).findAny().orElse(null);
				if (exists==null) {
					UserAssignOffice uo =userAssignOfficeRepo.findByOfficeUuidAndTeamId(cl.getOfficeId(),teamId);
					if (uo!=null) {
					userAssignOffices.add(uo);
					}
				}
				
				exists = userAssignOffices.stream().filter(s -> {
					   return s.getOffice().getUuid().equals(cl.getOfficeId())  && s.getTeam().getId()==teamId ;
					   }).findAny().orElse(null);
				
				if (exists != null) {
					
					if (!exists.getUser().getUuid().equals(cl.getClaimAssignedTo())) {
						//do reassignment
						Optional<RcmClaimAssignment> rcaOpt = rcmClaimAssignmentRepo.findById(cl.getClaimAssignmentId());
						if (rcaOpt.isPresent()) {
							RcmClaimAssignment rca = rcaOpt.get();
							rca.setActive(false);
							rca.setSystemComment("Claim taken back from Pendency Screen by :"+assignedBy.getEmail());
							rcmClaimAssignmentRepo.save(rca);
							
//							rcmClaimAssignmentRepo.assignClaimToUser(assignedBy.getUuid(), 
//									exists.getUser().getUuid(), teamId, rca.getRcmClaimStatus().getId(), Constants.SYSTEM_INITIAL_COMMENT, cl.getClaimUuid());
						  
								    
							
							
						}
					}
					
				}
				
				
			
		}
		this.assignedUnsAssignedClaimsByTeam(company.getUuid(),assignedBy,teamId);   
		
		
	}

	/**
	 * Fetch Appointment Date From Rule engine
	 * @param claim
	 * @param off
	 * @return
	 */
	public String fetchAppointmentDate(RcmClaims claim, RcmOffice off) {

		String officeUuid = claim.getOffice().getUuid();
		String appointmentDate = "";
		String startDate="";
		try{
			SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
			//sdf.format(claim.getDos());
			String[] dt=sdf.format(claim.getDos()).split("-");//2023-09-20 2023-07-13
			startDate=(dt[1].startsWith("0")?dt[1].replaceFirst("0", ""):dt[1])+"/"+(dt[2].startsWith("0")?dt[2].replaceFirst("0", ""):dt[2])+"/"+dt[0];
		}catch(Exception n){
			
		}
		try {
			HttpEntity<String> entity = new HttpEntity<String>(headers);
			String param = "?password=" + eagleSoftDBDetailsRepo.findByOffice(off).getPassword() + "&patientId="
					+ claim.getPatientId() + "&startDate="+startDate;
			param = param + "&office=" + officeUuid;
			
			//TEST DATA
			/*param = "?password=" + "134568" + "&patientId="
					+ "24734" + "&startDate=8/31/2023";
			param = param + "&office=" + "c04a2dbe-9bc5-11e8-9f0b-8c16451459cd";*/
			

			ResponseEntity<RcmClaimAppointmentMainRootDto> result = restTemplate.exchange(
					ev.getProperty("rcm.claimAppointmenturl") + param, HttpMethod.GET, entity,
					RcmClaimAppointmentMainRootDto.class);

			RcmClaimAppointmentMainRootDto rootDto = result.getBody();

			for (RcmClaimAppointmentDatas datas : rootDto.getData().getDatas()) {
				try {
					for (ClaimAppointmentDto re : datas.getData()) {
						appointmentDate = re.getStartDate();
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return appointmentDate;
	}
		
}
