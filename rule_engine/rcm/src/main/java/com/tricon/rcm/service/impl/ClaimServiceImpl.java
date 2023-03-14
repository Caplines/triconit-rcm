package com.tricon.rcm.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
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

import com.google.common.collect.Collections2;
import com.tricon.rcm.db.entity.RcmClaimAssignment;
import com.tricon.rcm.db.entity.RcmClaimComment;
import com.tricon.rcm.db.entity.RcmClaimDetail;
import com.tricon.rcm.db.entity.RcmClaimLog;
import com.tricon.rcm.db.entity.RcmClaimNoteType;
import com.tricon.rcm.db.entity.RcmClaimNotes;
import com.tricon.rcm.db.entity.RcmClaimRuleRemark;
import com.tricon.rcm.db.entity.RcmClaimRuleValidation;
import com.tricon.rcm.db.entity.RcmClaimStatusType;
import com.tricon.rcm.db.entity.RcmClaimSubmissionDetails;
import com.tricon.rcm.db.entity.RcmClaims;
import com.tricon.rcm.db.entity.RcmClaimsServiceRuleValidation;
import com.tricon.rcm.db.entity.RcmCompany;
import com.tricon.rcm.db.entity.RcmInsurance;
import com.tricon.rcm.db.entity.RcmInsuranceType;
import com.tricon.rcm.db.entity.RcmIssueClaims;

import com.tricon.rcm.db.entity.RcmMappingTable;
import com.tricon.rcm.db.entity.RcmOffice;
import com.tricon.rcm.db.entity.RcmRules;
import com.tricon.rcm.db.entity.RcmTeam;
import com.tricon.rcm.db.entity.RcmUser;
import com.tricon.rcm.db.entity.UserAssignOffice;
import com.tricon.rcm.dto.AssigmentClaimListDto;
import com.tricon.rcm.dto.CaplineIVFFormDto;
import com.tricon.rcm.dto.ClaimAssignDto;
import com.tricon.rcm.dto.ClaimDataDetails;
import com.tricon.rcm.dto.ClaimDetailDto;
import com.tricon.rcm.dto.ClaimEditDto;
import com.tricon.rcm.dto.KeyValueDto;
import com.tricon.rcm.dto.RcmClaimsServiceRuleValidationDto;
import com.tricon.rcm.dto.ClaimFromSheet;
import com.tricon.rcm.dto.ClaimLogDto;
import com.tricon.rcm.dto.ClaimNoteDto;
import com.tricon.rcm.dto.ClaimNotesDto;
import com.tricon.rcm.dto.ClaimProductionLogDto;
import com.tricon.rcm.dto.ClaimRemarkDto;
import com.tricon.rcm.dto.ClaimRuleRemarkDto;
import com.tricon.rcm.dto.ClaimRuleVaidationValueDto;
import com.tricon.rcm.dto.ClaimRuleValidationsDto;
import com.tricon.rcm.dto.ClaimServiceDto;
import com.tricon.rcm.dto.ClaimServiceValidationGSheet;
import com.tricon.rcm.dto.ClaimServiceValidationGSheetData;
import com.tricon.rcm.dto.ClaimSourceDto;
import com.tricon.rcm.dto.ClaimSubmissionDto;
import com.tricon.rcm.dto.CredentialData;
import com.tricon.rcm.dto.FreshClaimDataImplDto;
import com.tricon.rcm.dto.customquery.FreshClaimLogDto;
import com.tricon.rcm.dto.customquery.IVFDto;
import com.tricon.rcm.dto.customquery.IssueClaimDto;
import com.tricon.rcm.dto.customquery.ProductionDto;
import com.tricon.rcm.dto.customquery.RcmClaimDetailDto;
import com.tricon.rcm.dto.customquery.RcmClaimNoteDto;
import com.tricon.rcm.dto.customquery.RcmClaimSubmissionDto;
import com.tricon.rcm.dto.customquery.RuleEngineClaimDto;
import com.tricon.rcm.dto.RcmOfficeDto;
import com.tricon.rcm.dto.RemoteLietStatusCount;
import com.tricon.rcm.dto.RuleRemarkDto;
import com.tricon.rcm.dto.ServiceValidationDataDto;
import com.tricon.rcm.dto.TPValidationResponseDto;
import com.tricon.rcm.dto.TimelyFilingLimitDto;
import com.tricon.rcm.dto.customquery.AssignFreshClaimLogsDto;
import com.tricon.rcm.dto.customquery.AssignFreshClaimLogsImplDto;
import com.tricon.rcm.dto.customquery.ClaimRemarksDto;
import com.tricon.rcm.dto.customquery.ClaimRuleRemarksDto;
import com.tricon.rcm.dto.customquery.ClaimRuleValidationDto;
import com.tricon.rcm.dto.customquery.FreshClaimDataDto;
import com.tricon.rcm.dto.customquery.FreshClaimDetailsDto;
import com.tricon.rcm.dto.customquery.FreshClaimDetailsImplDto;
import com.tricon.rcm.enums.ClaimSourceEnum;
import com.tricon.rcm.enums.ClaimStatusEnum;
import com.tricon.rcm.enums.ClaimTypeEnum;
import com.tricon.rcm.enums.RcmTeamEnum;
import com.tricon.rcm.jpa.repository.RCMUserRepository;
import com.tricon.rcm.jpa.repository.RcmClaimAssignmentRepo;
import com.tricon.rcm.jpa.repository.RcmClaimCommentRepo;
import com.tricon.rcm.jpa.repository.RcmClaimDetailRepo;
import com.tricon.rcm.jpa.repository.RcmClaimLogRepo;
import com.tricon.rcm.jpa.repository.RcmClaimNotesRepo;
import com.tricon.rcm.jpa.repository.RcmClaimRepository;
import com.tricon.rcm.jpa.repository.RcmClaimRuleRemarkRepo;
import com.tricon.rcm.jpa.repository.RcmClaimRuleValidationRepo;
import com.tricon.rcm.jpa.repository.RcmClaimStatusTypeRepo;
import com.tricon.rcm.jpa.repository.RcmClaimSubmissionDetailsRepo;
import com.tricon.rcm.jpa.repository.RcmClaimsServiceRuleValidationRepo;
import com.tricon.rcm.jpa.repository.RcmCompanyRepo;
import com.tricon.rcm.jpa.repository.RcmInsuranceRepo;
import com.tricon.rcm.jpa.repository.RcmInsuranceTypeRepo;
import com.tricon.rcm.jpa.repository.RcmIssueClaimsRepo;
import com.tricon.rcm.jpa.repository.RcmLinkedClaimsRepo;
import com.tricon.rcm.jpa.repository.RcmMappingTableRepo;
import com.tricon.rcm.jpa.repository.RcmOfficeRepository;
import com.tricon.rcm.jpa.repository.RcmClaimNoteTypeRepo;
import com.tricon.rcm.jpa.repository.RcmRuleRepo;
import com.tricon.rcm.jpa.repository.RcmTeamRepo;
import com.tricon.rcm.jpa.repository.UserAssignOfficeRepo;
import com.tricon.rcm.security.JwtUser;
import com.tricon.rcm.util.ClaimUtil;
import com.tricon.rcm.util.ConnectAndReadSheets;
import com.tricon.rcm.util.Constants;
import com.tricon.rcm.util.RuleConstants;
import com.tricon.rcm.util.MessageUtil;

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
	RcmClaimCommentRepo rcmClaimCommentRepo;

	@Autowired
	RcmClaimRuleRemarkRepo rcmClaimRuleRemarkRepo;

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

	@Autowired
	RcmRuleRepo rcmRuleRepo;

	@Autowired
	RcmClaimSubmissionDetailsRepo rcmClaimSubmissionDetailsRepo;

	@Autowired
	RcmClaimNotesRepo rcmClaimNotesRepo;

	@Autowired
	RcmClaimNoteTypeRepo rcmClaimNoteTypeRepo;

	@Autowired
	RcmClaimRuleValidationRepo rcmClaimRuleValidationRepo;

	@Autowired
	RcmClaimsServiceRuleValidationRepo rcmClaimsServiceRuleValidationRepo;

	@Autowired
	RuleBookServiceImpl ruleBookService;
	
	@Autowired
	RcmClaimDetailRepo rcmClaimDetailRepo;

	private final Logger logger = LoggerFactory.getLogger(ClaimServiceImpl.class);

	@Transactional(rollbackFor = Exception.class)
	public Object pullClaimFromSource(ClaimSourceDto dto, RcmUser user, JwtUser jwtUser) {
		// go to Rule Engine.
		Object status = null;
		if (dto.getSource().equals(ClaimSourceEnum.GOOGLESHEET.toString())) {
			try {
				if (user == null) {
					user = userRepo.findByUuid(jwtUser.getUuid());
				}
				status = pullAndSaveClaimFromSheet(dto, user);
			} catch (Exception error) {

			}
		} else {
			List<ClaimLogDto> mapcountNew = new ArrayList<>();
			ClaimLogDto claimLogDto = null;
			if (dto.getOfficeuuids() == null) {
				List<String> of = new ArrayList<>();
				officeRepo.findByCompany(jwtUser.getCompany()).stream().map(RcmOfficeDto::getUuid).forEach(of::add);
				dto.setOfficeuuids(of);
			}
			for (String dtoOff : dto.getOfficeuuids()) {
				ClaimSourceDto d = new ClaimSourceDto();

				BeanUtils.copyProperties(dto, d);
				d.setOfficeuuid(dtoOff);
				// logId=-1;
				String data = "";
				// ruleEngineService.pullAndSaveInsuranceFromRE(d, user);

				List<TimelyFilingLimitDto> li = ruleEngineService
						.pullTimelyFilingLmtMappingFromSheet(jwtUser.getCompany());
				try {
					data = ruleEngineService.pullAndSaveClaimFromRE(d, user, li, ClaimTypeEnum.P, jwtUser.getCompany(),
							-1); // (d, user,li);
					String[] logsP = data.split("___");
					if (logsP.length == 2) {

						Optional<RcmClaimLog> optLog = rcmClaimLogRepo.findById(Integer.parseInt(logsP[1]));
						if (optLog.isPresent()) {
							RcmClaimLog log = optLog.get();
							claimLogDto = new ClaimLogDto(ClaimSourceEnum.EAGLESOFT.toString(), dtoOff, log.getStatus(),
									log.getNewClaimsCount(), new Date(), log.getOffice().getName());
							mapcountNew.add(claimLogDto);
							// mapcountNew.put(log.getOffice().getName()+"___"+log.getOffice().getUuid(),
							// new Object[] {log.getNewClaimsCount(),
							// log.getNewClaimsPrimaryCount(),log.getNewClaimsSecodaryCount(),log.getStatus()});
						}
					}
					String[] statusPri = data.split("___");
					int logId = -1;
					try {
						if (statusPri.length == 2)
							logId = Integer.parseInt(statusPri[1]);
					} catch (Exception p) {

					}
					data = ruleEngineService.pullAndSaveClaimFromRE(d, user, li, ClaimTypeEnum.S, jwtUser.getCompany(),
							logId); // (d, user,li);
					String[] logsS = data.split("___");
					if (logsS.length == 2) {

						Optional<RcmClaimLog> optLog = rcmClaimLogRepo.findById(Integer.parseInt(logsS[1]));
						if (optLog.isPresent()) {
							RcmClaimLog log = optLog.get();
							claimLogDto = new ClaimLogDto(ClaimSourceEnum.EAGLESOFT.toString(), dtoOff, log.getStatus(),
									log.getNewClaimsCount(), new Date(), log.getOffice().getName());
							mapcountNew.add(claimLogDto);
							// mapcountNew.put(log.getOffice().getName()+"___"+log.getOffice().getUuid(),
							// new Object[] {log.getNewClaimsCount(),
							// log.getNewClaimsPrimaryCount(),log.getNewClaimsSecodaryCount(),log.getStatus()});
						}
					}
				} catch (Exception error) {
				}

			}

			status = mapcountNew;

		}
		return status;
	}

	/**
	 * Service For Billing Pendency Dashboard (Get Fresh Claims/Rebill Count
	 * Details)
	 * 
	 * @return
	 */
	public List<FreshClaimDetailsImplDto> fetchBillingClaimDetails(int billType) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
		JwtUser jwtUser = (JwtUser) userDetails;
		List<FreshClaimDetailsDto> dtoList = null;
		if (jwtUser.isTeamLead()) {// && jwtUser.isAssociate()) || jwtUser.isTeamLead()
			dtoList = rcmClaimRepository.fetchBillingOrReBillingClaimDetails(jwtUser.getCompany().getUuid(), billType,
					jwtUser.getTeamId());
		} else if (jwtUser.isAssociate()) {
			dtoList = rcmClaimRepository.fetchBillingOrReBillingClaimDetails(jwtUser.getCompany().getUuid(), billType,
					jwtUser.getTeamId(), jwtUser.getUuid());
		}
		List<FreshClaimDetailsImplDto> finalList = new ArrayList<>();
		HashMap<String, RemoteLietStatusCount> remoteLiteMap = ruleEngineService.pullAndSaveRemoteLiteData();
		RemoteLietStatusCount counts = null;
		if (dtoList != null) {
			for (FreshClaimDetailsDto d : dtoList) {
				FreshClaimDetailsImplDto dF = new FreshClaimDetailsImplDto();
				// BeanUtils.copyProperties(d, dF);
				dF.setCount(d.getCount());
				dF.setOfficeName(d.getOfficeName());
				dF.setOfficeUuid(d.getOfficeUuid());
				try {
					if (d.getOpdos() != null) {
						// Simple 2023-01-12
						// Constants.SDF_UI.format(Constants.SDF_ES_DATE.parse((d.getOpdos())
						// System.out.println(d.getOpdos());
						dF.setOpdos(Constants.SDF_ES_DATE.parse(d.getOpdos()));
					}
				} catch (Exception dt) {

				}
				try {
					if (d.getOpdt() != null) {
						// Simple 2023-01-12
						// Constants.SDF_UI.format(Constants.SDF_ES_DATE.parse((d.getOpdos())
						// System.out.println(d.getOpdos());
						dF.setOpdt(Constants.SDF_ES_DATE.parse(d.getOpdt()));
					}
				} catch (Exception dt) {

				}

				dF.setRemoteLiteRejections(d.getRemoteLiteRejections());

				counts = remoteLiteMap.get(d.getOfficeName());
				if (counts != null)
					dF.setRemoteLiteRejections(counts.getRejectedCount());
				finalList.add(dF);

			}
		}
		return finalList;
	}

	/**
	 * 
	 * Fetch Claim Logs To Show "Billing Lead - Tool to update database"
	 * 
	 * @return
	 */
	public List<FreshClaimLogDto> fetchFreshClaimLogs(String companyUuid) {

		return rcmClaimRepository.fetchFreshClaimLogs(companyUuid);
	}

	public Object pullAndSaveClaimFromSheet(ClaimSourceDto dto, RcmUser user) {

		logger.info(" In pullClaimFromSheet");
		String success = "";
		RcmClaims claim = null;
		List<ClaimLogDto> logClaimDtos = new ArrayList<>();
		Map<String, ClaimLogDto> mapcountNew = new HashMap<>();
		// RcmClaimLog rcmClaimLog=null;
		String source = ClaimSourceEnum.GOOGLESHEET.toString();
		RcmClaimAssignment rcmAssigment = null;
		HashMap<String, RcmClaimLog> logMap = new HashMap<>();
		// Map<String, Object[]> mapcountNew = new HashMap<>();
		// Map<String,String> messages= new HashMap<>();
		// Map<String,int[]> mapcountNewP= new HashMap<>();
		// Map<String,int[]>mapcountNewS= new HashMap<>();
		RcmCompany company = rcmCompanyRepo.findByUuid(dto.getCompanyuuid());
		List<RcmTeam> allTeams = rcmTeamRepo.findAll();
		List<TimelyFilingLimitDto> timelyFilingLimits = ruleEngineService.pullTimelyFilingLmtMappingFromSheet(company);
		RcmClaimStatusType systemStatusBilling = rcmClaimStatusTypeRepo.findByStatus(ClaimStatusEnum.Billing.getType());
		RcmClaimStatusType systemStatusReBilling = rcmClaimStatusTypeRepo
				.findByStatus(ClaimStatusEnum.ReBilling.getType());

		// List<InsuranceNameTypeDto> insuranceTypeDto =
		// ruleEngineService.pullInsuranceMappingFromSheet(company);
		// int logId=-1;
		RcmMappingTable table = rcmMappingTableRepo.findByNameAndCompany(Constants.RCM_MAPPING_RCM_DATABASE, company);
		List<ClaimFromSheet> li = null;
		HashMap<String, RcmCompany> companies = new HashMap<>();
		HashMap<String, RcmInsuranceType> rcmInsuranceTypes = new HashMap<>();
		RcmInsuranceType rcmInsuranceType = null;
		RcmCompany clCompany = null;
		RcmInsurance ins = null;
		List<String> offNames = null;
		List<String> offNameKeys = null;

		List<RcmOffice> rcmOffices = null;
		if (dto.getOfficeuuids() != null && dto.getOfficeuuids().size() > 0) {
			offNames = new ArrayList<>();
			offNameKeys = new ArrayList<>();
			rcmOffices = officeRepo.findByUuidInAndCompanyUuid(dto.getOfficeuuids(), company.getUuid());
			// rcmOffices.stream().map(RcmOffice::getName).forEach(offNames::add);
			for (RcmOffice s : rcmOffices) {
				offNames.add(s.getName());
				offNameKeys.add(s.getName() + s.getKey());
			}

		}
		try {
			li = ConnectAndReadSheets.readClaimsFromGSheet(table.getGoogleSheetId(), table.getGoogleSheetSubName(),
					CLIENT_SECRET_DIR, CREDENTIALS_FOLDER, company.getName(), offNames, offNameKeys);
			if (li != null) {

				List<ClaimFromSheet> primaryList = li.stream()
						.filter(e -> e.getPrimaryClaimStatus().trim().equalsIgnoreCase(ClaimTypeEnum.P.getValue())
								|| e.getPrimaryClaimStatus().trim().equalsIgnoreCase(ClaimTypeEnum.E.getValue()))
						.collect(Collectors.toList());

				List<ClaimFromSheet> secondaryList = li.stream()
						.filter(e -> e.getSecondaryClaimStatus().trim().equalsIgnoreCase(ClaimTypeEnum.PP.getValue())
								|| e.getSecondaryClaimStatus().trim().equalsIgnoreCase(ClaimTypeEnum.EE.getValue())
								|| e.getSecondaryClaimStatus().trim().equalsIgnoreCase(ClaimTypeEnum.UU.getValue()))
						.collect(Collectors.toList());

				int newClaimCt = 0;
				int newClaimPCt = 0;
				int newClaimSCt = 0;
				int logStatus = 0;
				for (ClaimFromSheet re : primaryList) {
					try {
						System.out.println(re.getClaimId() + "--<ID");
						ClaimTypeEnum claimTypeEnum = ClaimTypeEnum.P;
						RcmClaimStatusType claimStatusType = null;

						if (companies.get(re.getClientName()) == null) {
							clCompany = rcmCompanyRepo.findByName(re.getClientName());
							if (clCompany != null) {
								companies.put(re.getClientName(), clCompany);
							} else {
								ruleEngineService.saveRcmIssueClaim(re.getClaimId(), null, user,
										"Wrong Client Name-" + re.getClientName(), source, claimTypeEnum);
								continue;
							}
						}
						int key = 0;
						try {
							key = Integer.parseInt(re.getOfficeKey());
						} catch (Exception p) {

						}
						RcmOffice off = officeRepo.findByCompanyAndKeyAndName(companies.get(re.getClientName()), key,
								re.getOfficeName());
						if (off == null) {
							ruleEngineService.saveRcmIssueClaim(re.getClaimId(), null, user,
									"Wrong Office Name/Key -" + re.getOfficeName() + "For " + re.getClientName(),
									source, claimTypeEnum);
							continue;
						}
						List<String> allCl = Arrays.asList(re.getClaimId() + claimTypeEnum.getSuffix());
						List<RcmClaims> claims = rcmClaimRepository.findByClaimIdInAndOffice(allCl, off);
						UserAssignOffice assignedUser = userAssignOfficeRepo.findByOfficeUuidAndTeamId(off.getUuid(),
								RcmTeamEnum.BILLING.getId());

						// RcmInsuranceType rcmInsuranceType = null;
						if (claims == null || claims.size() == 0) {
							// List<String> buildError1=null;
							// Fresh Claims
							/*
							 * if (claimTypeEnum.getType().equals(Constants.insuranceTypeSecondary)) {
							 * //Check for Corresponding Primary Claim in Case of secondary RcmClaims
							 * primaryClaim=
							 * rcmClaimRepository.findByClaimIdAndOffice(re.getClaimId()+ClaimTypeEnum.P.
							 * getSuffix(), off); if (primaryClaim==null) { //no need to save this as
							 * primary is not present.. ruleEngineService.saveRcmIssueClaim(re.getClaimId(),
							 * off, user,"Primary not Present", source, claimTypeEnum); continue;
							 * 
							 * } }
							 */
							claim = new RcmClaims();
							List<String> error = new ArrayList<>();
							System.out.println(re.getPrimaryInsuranceCompany());
							List<RcmInsurance> insL = insuranceRepo
									.findByNameAndOfficeAndActive(re.getPrimaryInsuranceCompany(), off, true);
							// In Google sheet we do not have InsuranceId so we need to generate. Prefix
							// ==GEN_
							if (insL.size() == 2) {
								boolean p = false;
								if (insL.get(0).getInsuranceId().startsWith("GEN_"))
									p = true;
								if (p)
									ins = insL.get(1);
								else
									ins = insL.get(0);

								if (ins.getInsuranceId().startsWith("GEN_")) {
									// De-activate this
									ins.setActive(false);
									insuranceRepo.save(ins);
								}

							} else if (insL.size() == 1) {
								ins = insL.get(0);
							} else {
								ins = new RcmInsurance();
								ins.setActive(true);
								ins.setAddress(re.getPrimaryInsuranceAddress());
								ins.setInsuranceId("GEN_" + new Date().getTime());

								rcmInsuranceType = rcmInsuranceTypes.get(re.getInsuranceName());
								if (rcmInsuranceType == null) {
									rcmInsuranceType = rcmInsuranceTypeRepo.findByName(re.getInsuranceName());
									if (rcmInsuranceType == null) {
										rcmInsuranceType = new RcmInsuranceType();
										rcmInsuranceType.setName(re.getInsuranceName());
										rcmInsuranceType.setId(rcmInsuranceTypeRepo.save(rcmInsuranceType).getId());
										rcmInsuranceTypes.put(re.getInsuranceName(), rcmInsuranceType);
									} else {
										rcmInsuranceTypes.put(re.getInsuranceName(), rcmInsuranceType);
									}

								}
								ins.setInsuranceType(rcmInsuranceTypes.get(re.getInsuranceName()));
								ins.setName(re.getPrimaryInsuranceCompany());
								ins.setOffice(off);
								ins.setId(insuranceRepo.save(ins).getId());
								// if (insuranceType==null) {
								// error.add( "Insurance Type Missing For for
								// Name:"+re.getPrimaryInsuranceCompany() +" in G-Sheet");
								// }else {
								// ins.setId(insuranceRepo.save(ins).getId());
								// }
							}

							String timely = ClaimUtil.getTimelyLimitFromSheetList(timelyFilingLimits,
									re.getPrimaryInsuranceCompany());
							if (timely == null) {
								// error.add("Timely Limit Type Missing for Primary Ins. :"+ins.getName());

							}

							if (systemStatusBilling.getStatus().equalsIgnoreCase(re.getClaimTypeP())) {
								claimStatusType = systemStatusBilling;
							}
							if (systemStatusReBilling.getStatus().equalsIgnoreCase(re.getClaimTypeP())) {
								claimStatusType = systemStatusReBilling;
							}
							if (claimStatusType == null) {
								error.add("Wrong Claim Type :" + re.getClaimTypeP());
							}
							if (error.size() > 0) {
								ruleEngineService.saveRcmIssueClaim(re.getClaimId(), off, user,
										String.join("\n", error), source, claimTypeEnum);
								continue;
							}
							newClaimCt++;
							if (claimTypeEnum.getType().equals(Constants.insuranceTypePrimary))
								newClaimPCt++;
							if (claimTypeEnum.getType().equals(Constants.insuranceTypeSecondary))
								newClaimSCt++;
							rcmInsuranceType = rcmInsuranceTypeRepo.findById(ins.getInsuranceType().getId());

							claim = ClaimUtil.createClaimFromSheetData(claim, off, re,
									ClaimUtil.filterTeamByNameId(allTeams, RcmTeamEnum.BILLING.toString()), user, ins,
									ins, claimStatusType, claimTypeEnum.getSuffix(), rcmInsuranceType, timely,
									claimTypeEnum);
							String claimUUid = rcmClaimRepository.save(claim).getClaimUuid();
							RcmClaimLog l = logMap.get(off.getName());
							if (l == null) {
								l = new RcmClaimLog();
								l.setOffice(off);
								l.setNewClaimsCount(1);
								l.setNewClaimsPrimaryCount(1);
								l.setNewClaimsSecodaryCount(0);
								logMap.put(off.getName(), l);
							} else {
								l.setNewClaimsCount(1 + l.getNewClaimsCount());
								l.setNewClaimsPrimaryCount(1 + l.getNewClaimsPrimaryCount());

							}
							RcmIssueClaims isC = rcmIssueClaimsRepo.findByClaimIdAndOfficeAndSource(
									re.getClaimId() + claimTypeEnum.getSuffix(), off, source);
							if (isC != null) {
								isC.setResolved(true);
								rcmIssueClaimsRepo.save(isC);
							}
							/// createAssginmentData
							if (assignedUser != null) {
								rcmAssigment = new RcmClaimAssignment();
								//
								rcmAssigment = ClaimUtil.createAssginmentData(rcmAssigment, user,
										assignedUser.getUser(), claimUUid, claim, Constants.SYSTEM_INITIAL_COMMENT,
										systemStatusBilling);

								rcmClaimAssignmentRepo.save(rcmAssigment);
							}

							if (isC != null) {
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

				for (ClaimFromSheet re : secondaryList) {

					try {
						System.out.println(re.getClaimId() + "--<ID");
						ClaimTypeEnum claimTypeEnum = ClaimTypeEnum.S;
						RcmClaimStatusType claimStatusType = null;

						if (companies.get(re.getClientName()) == null) {
							clCompany = rcmCompanyRepo.findByName(re.getClientName());
							if (clCompany != null) {
								companies.put(re.getClientName(), clCompany);
							} else {
								ruleEngineService.saveRcmIssueClaim(re.getClaimId(), null, user,
										"Wrong Client Name-" + re.getClientName(), source, claimTypeEnum);
								continue;
							}
						}

						RcmOffice off = officeRepo.findByCompanyAndName(companies.get(re.getClientName()),
								re.getOfficeName());
						if (off == null) {
							ruleEngineService.saveRcmIssueClaim(re.getClaimId(), null, user,
									"Wrong Office Name-" + re.getOfficeName() + "For " + re.getClientName(), source,
									claimTypeEnum);
							continue;
						}

						List<String> allCl = Arrays.asList(re.getClaimId() + claimTypeEnum.getSuffix());
						List<RcmClaims> claims = rcmClaimRepository.findByClaimIdInAndOffice(allCl, off);
						UserAssignOffice assignedUser = userAssignOfficeRepo.findByOfficeUuidAndTeamId(off.getUuid(),
								RcmTeamEnum.BILLING.getId());

						// RcmInsuranceType rcmInsuranceType = null;
						if (claims == null || claims.size() == 0) {
							// List<String> buildError1=null;
							// Fresh Claims

							if (claimTypeEnum.getType().equals(Constants.insuranceTypeSecondary)) {
								// Check for Corresponding Primary Claim in Case of secondary
								RcmClaims primaryClaim = rcmClaimRepository
										.findByClaimIdAndOffice(re.getClaimId() + ClaimTypeEnum.P.getSuffix(), off);
								if (primaryClaim == null) {
									// no need to save this as primary is not present..
									ruleEngineService.saveRcmIssueClaim(re.getClaimId(), off, user,
											"Primary not Present", source, claimTypeEnum);
									continue;

								}
							}

							claim = new RcmClaims();
							List<String> error = new ArrayList<>();
							System.out.println(re.getSecondaryInsuranceCompany());
							List<RcmInsurance> insL = insuranceRepo
									.findByNameAndOfficeAndActive(re.getSecondaryInsuranceCompany(), off, true);
							// In Google sheet we do not have InsuranceId so we need to generate. Prefix
							// ==GEN_
							if (insL.size() == 2) {
								boolean p = false;
								if (insL.get(0).getInsuranceId().startsWith("GEN_"))
									p = true;
								if (p)
									ins = insL.get(1);
								else
									ins = insL.get(0);

								if (ins.getInsuranceId().startsWith("GEN_")) {
									// De-activate this
									ins.setActive(false);
									insuranceRepo.save(ins);
								}

							} else if (insL.size() == 1) {
								ins = insL.get(0);
							} else {
								ins = new RcmInsurance();
								ins.setActive(true);
								ins.setAddress(re.getSecondaryInsuranceAddress());
								ins.setInsuranceId("GEN_" + new Date().getTime());

								rcmInsuranceType = rcmInsuranceTypes.get(re.getSecondaryInsuranceName());
								if (rcmInsuranceType == null) {
									rcmInsuranceType = rcmInsuranceTypeRepo.findByName(re.getSecondaryInsuranceName());
									if (rcmInsuranceType == null) {
										rcmInsuranceType = new RcmInsuranceType();
										rcmInsuranceType.setName(re.getSecondaryInsuranceName());
										rcmInsuranceType.setId(rcmInsuranceTypeRepo.save(rcmInsuranceType).getId());
										rcmInsuranceTypes.put(re.getSecondaryInsuranceName(), rcmInsuranceType);
									} else {
										rcmInsuranceTypes.put(re.getSecondaryInsuranceName(), rcmInsuranceType);
									}
								}

								ins.setInsuranceType(rcmInsuranceTypes.get(re.getSecondaryInsuranceName()));
								ins.setName(re.getSecondaryInsuranceCompany());
								ins.setOffice(off);
								ins.setId(insuranceRepo.save(ins).getId());
								// if (insuranceType==null) {
								// error.add( "Insurance Type Missing For for
								// Name:"+re.getSecondaryInsuranceCompany() +" in G-Sheet");
								// }else {
								// ins.setId(insuranceRepo.save(ins).getId());
								// }
							}

							String timely = ClaimUtil.getTimelyLimitFromSheetList(timelyFilingLimits,
									re.getSecondaryInsuranceCompany());
							if (timely == null) {
								// error.add("Timely Limit Type Missing for Primary Ins. :"+ins.getName());

							}

							if (systemStatusBilling.getStatus().equalsIgnoreCase(re.getClaimTypeS())) {
								claimStatusType = systemStatusBilling;
							}
							if (systemStatusReBilling.getStatus().equalsIgnoreCase(re.getClaimTypeS())) {
								claimStatusType = systemStatusReBilling;
							}
							if (claimStatusType == null) {
								error.add("Wrong Claim Type :" + re.getClaimTypeS());
							}
							if (error.size() > 0) {
								ruleEngineService.saveRcmIssueClaim(re.getClaimId(), off, user,
										String.join("\n", error), source, claimTypeEnum);
								continue;
							}
							newClaimCt++;
							if (claimTypeEnum.getType().equals(Constants.insuranceTypePrimary))
								newClaimPCt++;
							if (claimTypeEnum.getType().equals(Constants.insuranceTypeSecondary))
								newClaimSCt++;
							rcmInsuranceType = rcmInsuranceTypeRepo.findById(ins.getInsuranceType().getId());
							claim = ClaimUtil.createClaimFromSheetData(claim, off, re,
									ClaimUtil.filterTeamByNameId(allTeams, RcmTeamEnum.BILLING.toString()), user, ins,
									ins, claimStatusType, claimTypeEnum.getSuffix(), rcmInsuranceType, timely,
									claimTypeEnum);
							String claimUUid = rcmClaimRepository.save(claim).getClaimUuid();
							RcmClaimLog l = logMap.get(off.getName());
							if (l == null) {
								l = new RcmClaimLog();
								l.setOffice(off);
								l.setNewClaimsCount(1);
								l.setNewClaimsPrimaryCount(0);
								l.setNewClaimsSecodaryCount(1);
								logMap.put(off.getName(), l);
							} else {
								l.setNewClaimsCount(1 + l.getNewClaimsCount());
								l.setNewClaimsSecodaryCount(1 + l.getNewClaimsSecodaryCount());

							}
							RcmIssueClaims isC = rcmIssueClaimsRepo.findByClaimIdAndOfficeAndSource(
									re.getClaimId() + claimTypeEnum.getSuffix(), off, source);
							if (isC != null) {
								isC.setResolved(true);
								rcmIssueClaimsRepo.save(isC);
							}
							/// createAssginmentData
							if (assignedUser != null) {
								rcmAssigment = new RcmClaimAssignment();
								//
								rcmAssigment = ClaimUtil.createAssginmentData(rcmAssigment, user,
										assignedUser.getUser(), claimUUid, claim, Constants.SYSTEM_INITIAL_COMMENT,
										systemStatusBilling);

								rcmClaimAssignmentRepo.save(rcmAssigment);
							}

							if (isC != null) {
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

				} // For Secondary
				ClaimLogDto claimLogDto = null;
				for (Map.Entry<String, RcmClaimLog> entry : logMap.entrySet()) {

					claimLogDto = new ClaimLogDto(source, entry.getValue().getOffice().getUuid(), 1, newClaimCt,
							new Date(), entry.getValue().getOffice().getName());
					System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
					commonClaimServiceImpl.saveClaimLog(entry.getValue(), user, entry.getValue().getOffice(),
							ClaimSourceEnum.GOOGLESHEET.toString(), logStatus, newClaimCt, newClaimPCt, newClaimSCt,
							success);
					mapcountNew.put(entry.getKey(), claimLogDto);

				}

				if (rcmOffices != null && rcmOffices.size() > 0) {
					for (RcmOffice p : rcmOffices) {
						if (mapcountNew.get(p.getUuid()) == null) {

							RcmClaimLog l = new RcmClaimLog();
							l.setOffice(p);
							l.setNewClaimsCount(0);
							l.setNewClaimsPrimaryCount(0);
							l.setNewClaimsSecodaryCount(0);

							claimLogDto = new ClaimLogDto(source, p.getUuid(), 1, 0, new Date(), p.getName());
							commonClaimServiceImpl.saveClaimLog(l, user, p, ClaimSourceEnum.GOOGLESHEET.toString(), 1,
									0, 0, 0, "success");
							mapcountNew.put(p.getUuid(), claimLogDto);
						}
					}

				}
				if (rcmOffices == null) {
					// need to Store logs look for all the offices
					officeRepo.findByCompany(clCompany);
					// ClaimLogDto claimLogDto=null;
					for (Map.Entry<String, RcmCompany> entry : companies.entrySet()) {

						List<RcmOffice> allOffices = officeRepo.getByCompany(entry.getValue());
						for (RcmOffice exo : allOffices) {

							if (mapcountNew.get(exo.getUuid()) == null) {

								RcmClaimLog l = new RcmClaimLog();
								l.setOffice(exo);
								l.setNewClaimsCount(0);
								l.setNewClaimsPrimaryCount(0);
								l.setNewClaimsSecodaryCount(0);
								claimLogDto = new ClaimLogDto(source, exo.getUuid(), 1, 0, new Date(), exo.getName());
								commonClaimServiceImpl.saveClaimLog(l, user, exo,
										ClaimSourceEnum.GOOGLESHEET.toString(), 1, 0, 0, 0, "success");
								mapcountNew.put(exo.getUuid(), claimLogDto);

							}

						}

					}

				}

			}

			for (Map.Entry<String, ClaimLogDto> entry : mapcountNew.entrySet()) {

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

		ClaimSourceDto dto = new ClaimSourceDto();
		// dto.setOfficeuuid(officeUUid);
		dto.setCompanyuuid(jwtUser.getCompany().getUuid());
		// only for Simple Point
		if (jwtUser.getCompany().getName().equals(Constants.COMPANY_NAME)) {

			// RcmUser user= userRepo.findByEmail(((UserDetails) principal).getUsername());
			Object obj = ruleEngineService.pullAndSaveRemoteLiteData();
			return obj;
		} else
			return null;
	}

	public List<FreshClaimDataDto> fetchFreshClaimDetails(int teamId, int billingORRebill, String sub) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
		JwtUser jwtUser = (JwtUser) userDetails;

		if (sub.equals("Fresh"))
			return rcmClaimRepository.fetchFreshClaimDetails(jwtUser.getCompany().getUuid(), teamId);
		else
			return rcmClaimRepository.fetchClaimDetailsWorkedByTeam(jwtUser.getCompany().getUuid(), teamId);

	}

	public List<FreshClaimDataDto> fetchClaimsByTeamNotFrom(int teamId) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
		JwtUser jwtUser = (JwtUser) userDetails;

		return rcmClaimRepository.fetchFreshClaimDetailsOtherTeam(jwtUser.getCompany().getUuid(), teamId);
	}

	public List<AssignFreshClaimLogsImplDto> fetchClaimsForAssignments(AssigmentClaimListDto dto) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
		JwtUser jwtUser = (JwtUser) userDetails;
		List<Integer> ct = dto.getClaimType();
		List<String> inst = dto.getInsuranceType();
		if (dto.getClaimType() == null) {
			ct = new ArrayList<>();
			ct.add(ClaimStatusEnum.Billing.getId());
			ct.add(ClaimStatusEnum.ReBilling.getId());
		}
		List<AssignFreshClaimLogsImplDto> finalList = new ArrayList<>();
		Set<Integer> instDB = new HashSet<>();
		List<RcmInsuranceType> insList = rcmInsuranceTypeRepo.findAll();
		if (inst == null) {
			insList.stream().map(RcmInsuranceType::getId).forEach(instDB::add);
		} else {

			for (String i : inst) {

				List<RcmInsuranceType> x = insList.stream().filter(p -> p.getName().contains(i))
						.collect(Collectors.toList());
				if (x != null) {
					x.stream().map(RcmInsuranceType::getId).forEach(instDB::add);
				}

			}

		}
		List<AssignFreshClaimLogsDto> l = null;
		try {
			l = rcmClaimRepository.fetchClaimsForAssignments(jwtUser.getCompany().getUuid(), ct, instDB);
			HashMap<String, RemoteLietStatusCount> remoteLiteMap = ruleEngineService.pullAndSaveRemoteLiteData();
			RemoteLietStatusCount counts = null;

			AssignFreshClaimLogsImplDto dF = null;
			if (l != null) {
				for (AssignFreshClaimLogsDto logD : l) {
					dF = new AssignFreshClaimLogsImplDto();
					BeanUtils.copyProperties(logD, dF);
					counts = remoteLiteMap.get(logD.getOfficeName());
					if (counts != null) {
						dF.setRemoteLiteRejections(counts.getRejectedCount());
					}
					finalList.add(dF);
				}

			}
		} catch (Exception n) {
			n.printStackTrace();
		}
		return finalList;
	}

	public FreshClaimDataImplDto fetchIndividualClaim(String claimUuid, JwtUser jwtUser) {
		FreshClaimDataImplDto implDto = null;
		RcmClaimDetailDto dto = rcmClaimRepository.fetchIndividualClaim(jwtUser.getCompany().getUuid(), claimUuid);
		// RcmUser user = userRepo.findByUuid(jwtUser.getUuid());
		if (dto != null) {

			implDto = new FreshClaimDataImplDto();

			// RcmClaims claim = rcmClaimRepository.findByClaimUuid(claimUuid);
			// Fetch Data from RCM Tool Checks and Validations Sheets //141479965

			BeanUtils.copyProperties(dto, implDto);
			List<String> linkedClaims = rcmLinkedClaimsRepo.getLinkedClaims(dto.getUuid());
			String ivfId = "", tpId = "",ivDos="",tpDos="";
			String[] clT = implDto.getClaimId().split("_");
			String claimSubTy = Constants.insuranceTypeSecondary;// May be needed latter

			if (("_" + clT[1]).equals(ClaimTypeEnum.P.getSuffix())) {
				claimSubTy = Constants.insuranceTypePrimary;
				implDto.setPrimary(true);
			} else {
				implDto.setPrimary(false);
			}
			try {
				Object ivd = rcmClaimRepository.getIVIdOfClaim(clT[0], implDto.getOfficeUuid(), implDto.getPatientId());
				Object ivDet[] = null;
				if (ivd != null)
					ivDet = (Object[]) ivd;
				if (ivDet != null && ivDet.length == 2) {
					ivDet = (Object[]) ivd;
					ivfId = (String) ivDet[0];
					ivDos = (String) ivDet[1];
					
					Object tp  = rcmClaimRepository.getLatestTPIdForPatientDosAndIV(implDto.getOfficeUuid(),
							implDto.getPatientId(), (String) ivDet[1]);
					if (tp != null) {
						Object[] tpDet = (Object[]) tp;
						if (tpDet != null && tpDet.length == 2) {
							tpId = (String) tpDet[0];
							tpDos = (String) tpDet[1];
						}
					}
					
					
				}
			} catch (Exception issueIV) {
				issueIV.printStackTrace();
			}
			implDto.setIvfId(ivfId);
			implDto.setTpId(tpId);
			implDto.setIvDos(ivDos);
			implDto.setTpDos(tpDos);
			//
			implDto.setAllowEdit(false);// Allow Edit only if assigned to login user.
			RcmClaimAssignment assign = rcmClaimAssignmentRepo.findByClaimsClaimUuidAndActive(claimUuid, true);
			if (assign != null) {
				RcmUser assBy = assign.getAssignedBy();
				assBy = userRepo.findByUuid(assBy.getUuid());
				implDto.setAssignedByName(assBy.getFirstName() + " " + assBy.getLastName());
				implDto.setAssignedByUuid(assBy.getUuid());
				implDto.setAssignedByTeam(assBy.getTeam().getId());
				implDto.setAssignedByRemark(assign.getCommentAssignedBy());
				RcmUser assTo = assign.getAssignedTo();
				assTo = userRepo.findByUuid(assTo.getUuid());
				implDto.setAssignedToName(assTo.getFirstName() + " " + assTo.getLastName());
				implDto.setAssignedToUuid(assTo.getUuid());
				implDto.setAssignedToTeam(assTo.getTeam().getId());
				implDto.setAllowEdit(jwtUser.getUuid().equals(assTo.getUuid()));

			}
			RcmClaimComment comment = rcmClaimCommentRepo.findByClaimsClaimUuid(claimUuid);
			if (comment != null) {
				implDto.setClaimRemarks(comment.getComments());
			}
			if (linkedClaims != null)
				implDto.setLinkedClaims(linkedClaims);
			//
			// If Secondary
			if (implDto != null && implDto.getClaimId().endsWith(ClaimTypeEnum.S.getSuffix())) {
				Object sec = rcmClaimRepository.getClaimsUuidClaimId(
						implDto.getClaimId().split(ClaimTypeEnum.S.getSuffix())[0] + ClaimTypeEnum.P.getSuffix(),
						implDto.getOfficeUuid());
				if (sec != null) {
					Object s[] = (Object[]) sec;
					implDto.setAssoicatedClaimUuid(s[0].toString());
					implDto.setAssoicatedClaimStatus((boolean) s[1]);
				}

			} else {
				Object sec = rcmClaimRepository.getClaimsUuidClaimId(
						implDto.getClaimId().split(ClaimTypeEnum.P.getSuffix())[0] + ClaimTypeEnum.S.getSuffix(),
						implDto.getOfficeUuid());
				if (sec != null) {
					Object s[] = (Object[]) sec;
					implDto.setAssoicatedClaimUuid(s[0].toString());
					implDto.setAssoicatedClaimStatus((boolean) s[1]);
				}
			}
			// Run Auto Rules
			//runAutomatedRules(jwtUser, claimUuid);//not from here 
		
		} else {
			// Wrong claimId;
		}
		return implDto;

	}

	@Transactional(rollbackFor = Exception.class)
	public ServiceValidationDataDto readServiceValidationFromGSheet(String claimUuid, JwtUser jwtUser) {

		RcmUser user = userRepo.findByUuid(jwtUser.getUuid());
		ServiceValidationDataDto dto = new ServiceValidationDataDto();
		List<RcmClaimsServiceRuleValidationDto> list = new ArrayList<>();
		RcmClaimsServiceRuleValidationDto one = null;
		RcmClaims claim = rcmClaimRepository.findByClaimUuid(claimUuid);
		RcmOffice off = claim.getOffice();
		List<RcmClaimDetail> cddList= new ArrayList<>();
		if (officeRepo.findByUuid(off.getUuid()).getCompany().getUuid().equals(user.getCompany().getUuid())) {
			if (claim.isPending()) {

			
			String[] clT = claim.getClaimId().split("_");
			long ct= rcmClaimDetailRepo.countByClaimClaimUuid(claimUuid);
			if (ct==0) {
				//Pull && Save Data
				clT[0] = "2115";// For testing
				List<ClaimDetailDto> cdList = ruleEngineService.pullClaimDetailFromFromRE(clT[0], off.getCompany().getUuid(),
						off.getUuid());
				
				if (cdList == null) {
					dto.setClaimFound(false);
					return dto;
				} 
				
				RcmClaimDetail rcmClaimDetail=null;
				
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
						dto.setEsDate(cdd.getDateLastUpdated());
						
					}
					tmp++;
					
					
					/*
					rcmClaimDetail.set cdt.getApptId();
					rcmClaimDetail.set cdt.getDescription();
					rcmClaimDetail.set cdt.getEstInsurance();
					rcmClaimDetail.set cdt.getEstPrimary();
					rcmClaimDetail.set cdt.getFee();
					rcmClaimDetail.set cdt.getId();
					rcmClaimDetail.set cdt.getLineItem();
					rcmClaimDetail.set cdt.getPatientPortion();
					rcmClaimDetail.set cdt.getPatientPortionSec();
					//cd.getPd();
					rcmClaimDetail.set cdt.getProviderLastName();
					cdt.getServiceCode();
					cdt.getStatus();
					cdt.getSurface();
					cdt.getTooth();
					
					*/
				}
				dto.setClaimFound(true);
				rcmClaimRepository.save(claim);
			}else {
				dto.setClaimFound(true);
			}
			
			
			
			}
			

			
			
			boolean pullSuccess = false;

			List<RcmClaimsServiceRuleValidation> serviceData = rcmClaimsServiceRuleValidationRepo
					.findByClaimClaimUuid(claimUuid);
			// RcmTeamEnum.BILLING.getId()
			if (serviceData != null && serviceData.size() == 0) {

				HashMap<String, List<ClaimServiceValidationGSheet>> sheetServiceData = readServiceValidationFromGSheet();
				if (sheetServiceData != null) {
					// Save Data in Table First Time.
					RcmClaimsServiceRuleValidation v = null;
					for (Map.Entry<String, List<ClaimServiceValidationGSheet>> entry : sheetServiceData.entrySet()) {
						logger.info("Key = " + entry.getKey() + ", Value = " + entry.getValue());
						for (ClaimServiceValidationGSheet d : entry.getValue()) {
							for (ClaimServiceValidationGSheetData qq : d.getData()) {
								String codeFromES = "";
								List<RcmClaimDetail> cdFilter = cddList.stream()
										.filter(p -> entry.getKey().equals(p.getServiceCode()))
										.collect(Collectors.toList());
								if (cdFilter != null && cdFilter.size() > 0) {
									codeFromES = cdFilter.get(0).getServiceCode();
								}
								if (!codeFromES.equals(entry.getKey()))
									continue;
								if (qq.getValue().trim().equalsIgnoreCase("Not Required"))
									continue;
								if (qq.getValue().trim().equalsIgnoreCase("Rule Engine Check"))
									continue;
								if (qq.getValue().trim()
										.equalsIgnoreCase("As per code sheet -This is not a valid CDT any longer."))
									continue;

								logger.info("HEAD: " + qq.getHeading() + "   VALUE: " + qq.getValue());
								v = new RcmClaimsServiceRuleValidation();
								v.setActive(true);
								v.setDescription(qq.getDescription());
								v.setClaim(claim);
								v.setName(qq.getHeading());
								v.setServiceCode(entry.getKey());
								v.setValue(qq.getValue());
								String uu = rcmClaimsServiceRuleValidationRepo.save(v).getRemarkUuid();
								one = new RcmClaimsServiceRuleValidationDto();
								one.setDescription(qq.getDescription());
								one.setRemarkUuid(uu);
								one.setServiceCode(entry.getKey());
								one.setValue(qq.getValue());
								one.setName(qq.getHeading());
								list.add(one);
							}
						}

						pullSuccess = true;
					}
					if (pullSuccess) {
						claim.setPulledClaimsServiceDataFromEs(pullSuccess);
						rcmClaimRepository.save(claim);
					}
				}
			} else {
				for (RcmClaimsServiceRuleValidation s : serviceData) {
					one = new RcmClaimsServiceRuleValidationDto();
					one.setDescription(s.getDescription());
					one.setRemarkUuid(s.getRemarkUuid());
					one.setServiceCode(s.getServiceCode());
					one.setValue(s.getValue());
					one.setName(s.getName());
					one.setMessageType(s.getMessageType());
					one.setRemark(s.getRemark());
					list.add(one);
				}

				// findByClaimClaimUuidAndNameAndServiceCode
				/*
				 * RcmClaimsServiceRuleValidation v = null;
				 * rcmClaimsServiceRuleValidationRepo.deactivateOldClaimData(claimUuid); for
				 * (Map.Entry<String, List<ClaimServiceValidationGSheet>> entry :
				 * sheetServiceData.entrySet()) { logger.info("Key = " + entry.getKey() +
				 * ", Value = " + entry.getValue()); for (ClaimServiceValidationGSheet d :
				 * entry.getValue()) { for (ClaimServiceValidationGSheetData qq : d.getData()) {
				 * v =
				 * rcmClaimsServiceRuleValidationRepo.findByClaimClaimUuidAndNameAndServiceCode(
				 * claimUuid, qq.getHeading(), entry.getKey()); logger.info("HEAD: " +
				 * qq.getHeading() + "   VALUE: " + qq.getValue()); String codeFromES = "";
				 * List<ClaimDetailDto> cdFilter = cd.stream() .filter(p ->
				 * entry.getKey().equals(p.getServiceCode())) .collect(Collectors.toList()); if
				 * (cdFilter != null && cdFilter.size() > 0) { codeFromES =
				 * cdFilter.get(0).getServiceCode(); } if (!codeFromES.equals(entry.getKey()))
				 * continue; if (qq.getValue().trim().equalsIgnoreCase("Not Required"))
				 * continue; if (qq.getValue().trim().equalsIgnoreCase("Rule Engine Check"))
				 * continue; if (qq.getValue().trim()
				 * .equalsIgnoreCase("As per code sheet -This is not a valid CDT any longer."))
				 * continue;
				 * 
				 * if (v == null) { v = new RcmClaimsServiceRuleValidation();
				 * 
				 * } else {
				 * 
				 * v.setUpdatedDate(new Date()); v.setUpdatedBy(user); } v.setActive(true);
				 * v.setDescription(qq.getDescription()); v.setClaim(claim);
				 * v.setName(qq.getHeading()); v.setServiceCode(entry.getKey());
				 * v.setValue(qq.getValue()); rcmClaimsServiceRuleValidationRepo.save(v); } } }
				 */
			}

		} else {
			logger.error("Wrong Client");
		}
		dto.setDto(list);
		return dto;
	}

	private HashMap<String, List<ClaimServiceValidationGSheet>> readServiceValidationFromGSheet() {

		HashMap<String, List<ClaimServiceValidationGSheet>> data = null;
		try {
			data = ConnectAndReadSheets.readServiceValidationFromGSheet("1Ba44zmvoNzNBPNaxGPUNVxUu_O41r7HAWq2hyZuCDJc",
					"Service-Code based Validations", CLIENT_SECRET_DIR, CREDENTIALS_FOLDER);
		} catch (Exception v) {
			v.printStackTrace();
			logger.error("Error in  RCM Tool Checks and Validations Sheets");
			logger.error(v.getMessage());
		}
		return data;
	}

	public List<ProductionDto> claimsProductionReportByTeam(int teamId, ClaimProductionLogDto dto) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
		JwtUser jwtUser = (JwtUser) userDetails;

		rcmClaimRepository.claimProductionByTeamMember(jwtUser.getCompany().getUuid(), teamId, dto.getStartDate(),
				dto.getEndDate());
		return null;

	}

	/**
	 * get all issue in Claims That are fetched from Source
	 * 
	 * @param companyId
	 * @return
	 */
	public List<IssueClaimDto> getIssueClaims(String companyId) {

		return rcmClaimRepository.getIssueClaims(companyId);
	}

	public CaplineIVFFormDto getIvfDataFromRE(String companyId, String claimuuid) {

		RcmClaims claims = rcmClaimRepository.findByClaimUuid(claimuuid);

		if (claims != null) {
			RcmOffice off = claims.getOffice();
			IVFDto iVFDto = rcmClaimRepository.getLatestIvfNumberForClaim(off.getUuid(), claims.getPatientId(),
					claimuuid);
			if (iVFDto != null) {
				return ruleEngineService.pullIVFDataFromRE(iVFDto.getIvId(), claims.getPatientId(), companyId,
						off.getUuid());
			} else {
				// For testing..
				return ruleEngineService.pullIVFDataFromRE("196041", "7431", companyId,
						"f015515d-7df2-11e8-8432-8c16451459cd");
			}
		} else {
			// For testing..
			return ruleEngineService.pullIVFDataFromRE("196041", "7431", companyId,
					"f015515d-7df2-11e8-8432-8c16451459cd");
		}
		// return null;
	}

	public List<ClaimRemarksDto> fetchClaimRemarksOtherTeam(String companyId, String claimuuid, int teamId) {

		List<ClaimRemarksDto> list = rcmClaimCommentRepo.fetchClaimRemarksOtherTeam(claimuuid, teamId);

		return list;
	}

	public List<ClaimRuleRemarksDto> fetchClaimRuleRemark(JwtUser jwtUser, String claimuuid) {

		RcmUser user = userRepo.findByUuid(jwtUser.getUuid());
		List<ClaimRuleRemarksDto> list = null;
		RcmClaims claim = rcmClaimRepository.findByClaimUuid(claimuuid);
		if (officeRepo.findByUuid(claim.getOffice().getUuid()).getCompany().getUuid()
				.equals(user.getCompany().getUuid())) {
			list = rcmClaimRuleRemarkRepo.fetchClaimRuleRemarks(claimuuid, RcmTeamEnum.BILLING.getId());
		} else {
			logger.error("Wrong Client");
		}
		return list;
	}

	public String saveClaimRemark(JwtUser jwtUser, ClaimRemarkDto dto) {

		RcmUser user = userRepo.findByUuid(jwtUser.getUuid());
		RcmClaims claim = rcmClaimRepository.findByClaimUuid(dto.getClaimUuid());

		if (officeRepo.findByUuid(claim.getOffice().getUuid()).getCompany().getUuid()
				.equals(user.getCompany().getUuid())) {
			if (!claim.isPending())
				return "Claim Already Submitted";
			return saveClaimRemark(dto.getRemark(), claim, user, jwtUser);
		}

		else
			return "wrong Client Name";

	}

	private String saveClaimRemark(String remark, RcmClaims claim, RcmUser user, JwtUser jwtUser) {

		if (remark != null && !remark.trim().equals("")) {

			RcmClaimComment comment = rcmClaimCommentRepo.findByCommentedByUuidAndClaimsClaimUuid(jwtUser.getUuid(),
					claim.getClaimUuid());
			if (comment == null) {

				comment = new RcmClaimComment();
				comment.setCommentedBy(user);
				comment.setCreatedBy(user);
				comment.setClaims(claim);
				comment.setComments(remark);
				comment.setActive(true);
				comment.setTeamId(rcmTeamRepo.findById(jwtUser.getTeamId()));
			} else {
				comment.setComments(remark);
			}

			return rcmClaimCommentRepo.save(comment).getUuid();
		} else {
			return "";
		}

	}

	public String saveClaimRuleRemark(JwtUser jwtUser, ClaimRuleRemarkDto dto) {

		RcmUser user = userRepo.findByUuid(jwtUser.getUuid());
		RcmClaims claim = rcmClaimRepository.findByClaimUuid(dto.getClaimUuid());

		if (officeRepo.findByUuid(claim.getOffice().getUuid()).getCompany().getUuid()
				.equals(user.getCompany().getUuid())) {
			if (!claim.isPending())
				return "Claim Already Submitted";
			saveClaimRuleRemark(dto.getData(), user, claim, jwtUser);

			return "Remarks Saved";
		}

		else
			return "wrong Client Name";

	}

	private void saveClaimRuleRemark(List<RuleRemarkDto> data, RcmUser user, RcmClaims claim, JwtUser jwtUser) {
		data.forEach(s -> {

			RcmRules rule = rcmRuleRepo.findById(s.getRuleId()).get();
			RcmClaimRuleRemark remark = rcmClaimRuleRemarkRepo.findByRuleAndClaim(rule, claim);

			if (remark == null) {
				remark = new RcmClaimRuleRemark();
				remark.setCreatedBy(user);
			} else {
				remark.setUpdatedBy(user);
				remark.setUpdatedDate(new Date());
			}
			remark.setCommentedBy(user);
			remark.setClaim(claim);
			remark.setRemarks(s.getRemark());
			remark.setRule(rule);
			remark.setActive(true);
			remark.setTeamId(rcmTeamRepo.findById(jwtUser.getTeamId()));
			rcmClaimRuleRemarkRepo.save(remark);
		});
	}

	private void saveClaimRuleManualMessageType(List<RuleRemarkDto> data, RcmUser user, RcmClaims claim, JwtUser jwtUser) {
		data.forEach(s -> {

			
			RcmRules rule = rcmRuleRepo.findById(s.getRuleId()).get();
			if (!rule.getManualAuto().equals(Constants.RULE_TYPE_MANUAL)) return ;
			RcmClaimRuleValidation val = rcmClaimRuleValidationRepo.findByRuleAndClaim(rule, claim);

			
			if (val == null) {
				val = new RcmClaimRuleValidation();
				val.setCreatedBy(user);
				val.setRunBy(user);
			} else {
				val.setUpdatedBy(user);
				val.setUpdatedDate(new Date());
				val.setRunBy(user);
			}
			val.setMessageType(s.getMessageType());
			val.setClaim(claim);
			val.setRule(rule);
			val.setActive(true);
			val.setMessage("BY System");
			val.setTeamId(rcmTeamRepo.findById(jwtUser.getTeamId()));
			rcmClaimRuleValidationRepo.save(val);
		});
	}
	private void saveClaimServiceCodeValidation(List<ClaimServiceDto> data, RcmUser user, RcmClaims claim,
			JwtUser jwtUser) {
		data.forEach(s -> {

			RcmClaimsServiceRuleValidation val = rcmClaimsServiceRuleValidationRepo
					.findByClaimClaimUuidAndRemarkUuid(claim.getClaimUuid(), s.getRemarkUuid());

			val.setUpdatedBy(user);
			val.setUpdatedDate(new Date());
			val.setRemark(s.getRemark());
			val.setMessageType(s.getMessageType());
			val.setActive(true);
			rcmClaimsServiceRuleValidationRepo.save(val);
		});
	}

	public List<RuleEngineClaimDto> getRuleEngineClaimReport(String officeId, String companyId, String patientId,
			String claimId) {

		return rcmClaimRepository.getRuleEngineClaimReport(officeId, companyId, patientId, claimId);
	}

	public RcmClaimSubmissionDto fetchClaimSubmissionDetails(JwtUser jwtUser, String claimuuid) {

		RcmClaimSubmissionDto d = rcmClaimSubmissionDetailsRepo.findByClaimUuidAndCompanyId(claimuuid,
				jwtUser.getCompany().getUuid());

		return d;
	}

	public String saveClaimSubmissionDetails(JwtUser jwtUser, ClaimSubmissionDto dto) {

		RcmUser user = userRepo.findByUuid(jwtUser.getUuid());
		RcmClaims claim = rcmClaimRepository.findByClaimUuid(dto.getClaimUuid());

		if (officeRepo.findByUuid(claim.getOffice().getUuid()).getCompany().getUuid()
				.equals(user.getCompany().getUuid())) {
			if (!claim.isPending())
				return "Claim Already Submitted";
			if (!claim.isPulledClaimsServiceDataFromEs()) {
				return "Service Code validation not Done";
			}
			return saveClaimSubmissionDetails(user, claim, dto);
		}

		else
			return "wrong Client Name";

	}

	private String saveClaimSubmissionDetails(RcmUser user, RcmClaims claim, ClaimSubmissionDto dto) {
		RcmClaimSubmissionDetails det = rcmClaimSubmissionDetailsRepo.findByClaim(claim);
		if (dto == null)
			return "";
		if (det != null) {
			det.setSubmittedBy(user);
			det.setUpdatedBy(user);
			det.setUpdatedDate(new Date());
		} else {
			det = new RcmClaimSubmissionDetails();
			det.setSubmittedBy(user);
		}

		det.setCreatedBy(user);
		det.setClaim(claim);
		det.setEsDate(dto.getEsDate());
		det.setPreauth(dto.isPreauth());
		det.setPreauthNo(dto.getProviderRefNo());
		det.setChannel(dto.getChannel());
		det.setClaimNumber(dto.getClaimNumber());
		det.setProviderRefNo(dto.getProviderRefNo());
		det.setRefferalLetter(dto.isRefferalLetter());
		det.setAttachmentSend(dto.isAttachmentSend());
		det.setEsTime(dto.getEsTime());

		return rcmClaimSubmissionDetailsRepo.save(det).getId();
	}

	public List<KeyValueDto> fetchClaimNotes(JwtUser jwtUser, String claimuuid) {

		List<KeyValueDto> list = new ArrayList<>();
		List<RcmClaimNoteDto> d = rcmClaimNotesRepo.fetchClaimNotes(claimuuid, RcmTeamEnum.BILLING.getId());

		List<RcmClaimNoteDto> all = rcmClaimNoteTypeRepo.fetchAllNotes(true);

		all.forEach(s -> {
			KeyValueDto dto = new KeyValueDto();
			dto.setId(s.getNoteId());
			dto.setKey(s.getNote());

			// s.getNoteId()
			List<RcmClaimNoteDto> fil = d.stream().filter(c -> c.getNoteId() == s.getNoteId())
					.collect(Collectors.toList());
			if (fil != null && fil.size() > 0) {
				dto.setValue(fil.get(0).getNote());
			}

			list.add(dto);
		});
		return list;
	}

	public String saveClaimNotes(JwtUser jwtUser, ClaimNotesDto dto) {

		// RcmClaimNotes notes = null;
		RcmUser user = userRepo.findByUuid(jwtUser.getUuid());

		RcmClaims claim = rcmClaimRepository.findByClaimUuid(dto.getClaimUuid());
		RcmClaimAssignment assign = rcmClaimAssignmentRepo
				.findByAssignedToUuidAndClaimsClaimUuidAndActive(jwtUser.getUuid(), claim.getClaimUuid(), true);
		// claim.getC
		if (assign == null) {
			return "Not assigned to user:" + jwtUser.getEmail();
		}
		if (officeRepo.findByUuid(claim.getOffice().getUuid()).getCompany().getUuid()
				.equals(user.getCompany().getUuid())) {

			if (!claim.isPending())
				return "Claim Already Submitted";
			saveClaimNotes(dto.getData(), user, claim, jwtUser);
			return "Done";
		}

		else
			return "wrong Client Name";

	}

	private boolean saveClaimNotes(List<ClaimNoteDto> data, RcmUser user, RcmClaims claim, JwtUser jwtUser) {
        boolean saveAll=true;
        if (data==null) return false;
        if (data.size()==0) return false;
        for(ClaimNoteDto s:data){
		//data.forEach(s -> {
			RcmClaimNoteType noteType = rcmClaimNoteTypeRepo.findById(s.getId()).get();
			RcmClaimNotes notes = rcmClaimNotesRepo.findByClaimAndNoteType(claim, noteType);
			if (s.getValue() != null && !s.getValue().trim().equals("")) {
				if (notes != null) {
					notes.setNotesBy(user);
					notes.setUpdatedBy(user);
					notes.setUpdatedDate(new Date());
				} else {
					notes = new RcmClaimNotes();
					notes.setNotesBy(user);
					notes.setNoteType(noteType);
					notes.setCreatedBy(user);
				}
				notes.setClaim(claim);
				notes.setNote(s.getValue());
				notes.setTeamId(rcmTeamRepo.findById(RcmTeamEnum.BILLING.getId()));

				rcmClaimNotesRepo.save(notes);
				
			}else {
				saveAll=false;
			}
		}
       return saveAll;
	}

	public List<ClaimRuleVaidationValueDto> fetchClaimAllRulesData(JwtUser jwtUser, String claimuuid) {

		List<ClaimRuleVaidationValueDto> list = new ArrayList<>();
		List<ClaimRuleValidationDto> d = rcmClaimRuleValidationRepo.fetchClaimRuleValidation(claimuuid,
				RcmTeamEnum.BILLING.getId());

		List<RcmRules> all = rcmRuleRepo.findByRuleTypeInAndActive(
				Arrays.asList(new String[] { Constants.RULE_TYPE_RCM, Constants.RULE_TYPE_RULE_ENGINE_AND_RCM }), 1);

		all.forEach(s -> {
			ClaimRuleVaidationValueDto dto = new ClaimRuleVaidationValueDto();
			dto.setRuleId(s.getId());
			dto.setRuleName(s.getName());
			dto.setRuleDesc(s.getDescription());
			dto.setManualAuto(s.getManualAuto());
			// s.getNoteId()
			List<ClaimRuleValidationDto> fil = d.stream().filter(c -> c.getRuleId() == s.getId())
					.collect(Collectors.toList());
			if (fil != null && fil.size() > 0) {
				ClaimRuleValidationDto fi = fil.get(0);
				dto.setMessage(fi.getMessage());
				dto.setMessageType(fi.getMessageType());

			}

			list.add(dto);
		});
		return list;
	}

	public String saveClaimManualRules(JwtUser jwtUser, ClaimRuleValidationsDto dto) {

		// RcmClaimNotes notes = null;
		RcmUser user = userRepo.findByUuid(jwtUser.getUuid());

		RcmClaims claim = rcmClaimRepository.findByClaimUuid(dto.getClaimUuid());
		RcmClaimAssignment assign = rcmClaimAssignmentRepo
				.findByAssignedToUuidAndClaimsClaimUuidAndActive(jwtUser.getUuid(), claim.getClaimUuid(), true);
		// claim.getC
		if (assign == null) {
			return "Not assigned to user:" + jwtUser.getEmail();
		}

		if (officeRepo.findByUuid(claim.getOffice().getUuid()).getCompany().getUuid()
				.equals(user.getCompany().getUuid())) {

			if (!claim.isPending())
				return "Claim Already Submitted";
			saveClaimManualRules(dto.getData(), user, claim, jwtUser);

			return "Done";
		}

		else
			return "wrong Client Name";

	}

	private void saveClaimManualRules(List<com.tricon.rcm.dto.ClaimRuleValidationDto> data, RcmUser user,
			RcmClaims claim, JwtUser jwtUser) {
		data.forEach(s -> {
			RcmRules rule = rcmRuleRepo.findById(s.getRuleId()).get();
			if (rule.getManualAuto().equals(Constants.RULE_TYPE_MANUAL)) {
				RcmClaimRuleValidation val = rcmClaimRuleValidationRepo.findByRuleAndClaim(rule, claim);
				if (s.getMessage() != null && !s.getMessage().trim().equals("")) {

					if (val != null) {
						val.setRunBy(user);
						val.setUpdatedBy(user);
						val.setUpdatedDate(new Date());
					} else {
						val = new RcmClaimRuleValidation();
						val.setRunBy(user);
						val.setCreatedBy(user);
					}
					val.setClaim(claim);
					val.setMessage(s.getMessage());
					val.setMessageType(s.getMessageType());
					val.setActive(true);
					val.setRule(rule);

					val.setTeamId(rcmTeamRepo.findById(jwtUser.getTeamId()));
					rcmClaimRuleValidationRepo.save(val);

				}
			}
		});

	}

	public String fetchClaimRemark(JwtUser jwtUser, String claimuuid) {

		RcmClaimComment d = rcmClaimCommentRepo.findByClaimsClaimUuid(claimuuid);
		if (d != null) {
			return d.getComments();
		}
		return "";
	}

	public String saveFullClaim(JwtUser jwtUser, ClaimEditDto dto) {

		RcmClaims claim = rcmClaimRepository.findByClaimUuid(dto.getClaimUuid());
		RcmUser user = userRepo.findByUuid(jwtUser.getUuid());
		RcmClaimAssignment assign = rcmClaimAssignmentRepo
				.findByAssignedToUuidAndClaimsClaimUuidAndActive(jwtUser.getUuid(), claim.getClaimUuid(), true);
		// claim.getC
		if (assign == null) {
			return "Not assigned to user:" + jwtUser.getEmail();
		}
		boolean notesSaved=false;
		if (officeRepo.findByUuid(claim.getOffice().getUuid()).getCompany().getUuid()
				.equals(user.getCompany().getUuid())) {

			if (!claim.isPending()) {
				return "Claim Already Submitted";
			} else {

				if (dto.getClaimManualRuleValidationList() != null)
					saveClaimManualRules(dto.getClaimManualRuleValidationList(), user, claim, jwtUser);
				if (dto.getClaimNoteDtoList() != null) {
					notesSaved = saveClaimNotes(dto.getClaimNoteDtoList(), user, claim, jwtUser);
				}
				if (dto.getRuleRemarkDto() != null) {
					saveClaimRuleRemark(dto.getRuleRemarkDto(), user, claim, jwtUser);
					saveClaimRuleManualMessageType(dto.getRuleRemarkDto(), user, claim, jwtUser);

				}
				
				if (dto.getClaimRemark() != null) {
					saveClaimRemark(dto.getClaimRemark(), claim, user, jwtUser);
				}

				if (dto.getSubmissionDto() != null)
					saveClaimSubmissionDetails(user, claim, dto.getSubmissionDto());
				if (dto.getSerCVDto() != null)
					saveClaimServiceCodeValidation(dto.getSerCVDto(), user, claim, jwtUser);

			}

			if (dto.isSubmission()) {
				claim.setPending(false);
				claim.setUpdatedBy(user);
				claim.setUpdatedDate(new Date());

				rcmClaimRepository.save(claim);
			}

		} else {
			return "wrong Client Name";
		}

		return "success";
	}

	public String assignToOtherOrTeamLead(JwtUser jwtUser, ClaimAssignDto dto) {

		RcmClaims claim = rcmClaimRepository.findByClaimUuid(dto.getClaimUuid());
		RcmUser user = userRepo.findByUuid(jwtUser.getUuid());
		RcmClaimAssignment assign = rcmClaimAssignmentRepo
				.findByAssignedToUuidAndClaimsClaimUuidAndActive(jwtUser.getUuid(), claim.getClaimUuid(), true);
		// claim.getC
		if (assign == null) {
			return "Not assigned to user:" + jwtUser.getEmail();
		}
		if (officeRepo.findByUuid(claim.getOffice().getUuid()).getCompany().getUuid()
				.equals(user.getCompany().getUuid())) {

			if (!claim.isPending()) {
				return "Claim Already Submitted";
			}
			if (!claim.isPulledClaimsServiceDataFromEs()) {
				return "Service Code validation not Done";
			}

			if (dto.isToLead()) {
				// Assign to lead
				RcmTeam lastTeam = rcmTeamRepo.findById(jwtUser.getTeamId());
				RcmUser rcmLeadUser = userRepo.findByUuid(dto.getTeamLeadUuid());
				// Check For Role on do for TEAM Lead
				claim.setLastWorkTeamId(lastTeam);
				claim.setUpdatedBy(user);
				claim.setUpdatedDate(new Date());
				rcmClaimRepository.save(claim);

				RcmClaimAssignment newAssign = new RcmClaimAssignment();
				newAssign.setActive(true);
				newAssign.setAssignedBy(user);
				newAssign.setAssignedTo(rcmLeadUser);// need to find
				newAssign.setClaims(claim);
				newAssign.setCommentAssignedBy(dto.getRemark());
				newAssign.setCurrentTeamId(claim.getCurrentTeamId());
				newAssign.setRcmClaimStatus(claim.getClaimStatusType());
				newAssign.setTakenBack(false);

				rcmClaimAssignmentRepo.save(newAssign);

				assign.setActive(false);
				assign.setUpdatedBy(user);
				assign.setUpdatedDate(new Date());
				rcmClaimAssignmentRepo.save(assign);
			} else {
				// Assign to other Team.

				RcmTeam team = rcmTeamRepo.findById(dto.getOtherTeamId());
				RcmTeam lastTeam = rcmTeamRepo.findById(jwtUser.getTeamId());
				if (team != null) {
					// Cannot assign to Same Team
					if (jwtUser.getTeamId() != dto.getOtherTeamId()) {

						claim.setCurrentTeamId(team);
						claim.setLastWorkTeamId(lastTeam);
						claim.setUpdatedBy(user);
						claim.setUpdatedDate(new Date());
						rcmClaimRepository.save(claim);

						RcmClaimAssignment newAssign = new RcmClaimAssignment();

						newAssign.setActive(true);
						newAssign.setAssignedBy(user);
						newAssign.setAssignedTo(null);// need to find
						newAssign.setClaims(claim);
						newAssign.setCommentAssignedBy(dto.getRemark());
						newAssign.setCurrentTeamId(team);
						newAssign.setRcmClaimStatus(claim.getClaimStatusType());
						newAssign.setTakenBack(false);

						rcmClaimAssignmentRepo.save(newAssign);

						assign.setActive(false);
						assign.setUpdatedBy(user);
						assign.setUpdatedDate(new Date());
						rcmClaimAssignmentRepo.save(assign);

					}
				}

			}

		} else {
			return "wrong Client Name";
		}

		return "success";
	}

	public String runAutomatedRules(JwtUser jwtUser, String claimuuid,boolean reRrun) {

		RcmClaims claim = rcmClaimRepository.findByClaimUuid(claimuuid);
		
		
		if (claim.isAutoRuleRun() && !reRrun) {
			return "Already Run";
		}
		RcmUser user = userRepo.findByUuid(jwtUser.getUuid());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		System.out.println(calendar.get(Calendar.YEAR));
		if (officeRepo.findByUuid(claim.getOffice().getUuid()).getCompany().getUuid()
				.equals(user.getCompany().getUuid())) {

			if (!claim.isPending()) {
				return "Already Submitted";
				
			}
			
			RcmClaimAssignment assign = rcmClaimAssignmentRepo.findByClaimsClaimUuidAndActive(claimuuid, true);
			//Only Assigned User Can Run
			if (assign != null && jwtUser.getUuid().equals(assign.getAssignedTo().getUuid())) {
				try {

					List<String> types = Arrays
							.asList(new String[] { Constants.RULE_TYPE_RCM, Constants.RULE_TYPE_RULE_ENGINE_AND_RCM })
							.stream().collect(Collectors.toList());
					List<RcmRules> rules = rcmRuleRepo.findByRuleTypeInAndActiveAndManualAuto(types, 1,
							Constants.RULE_TYPE_AUTO);

					List<TPValidationResponseDto> allLIst = new ArrayList<>();

					CaplineIVFFormDto ivData = getIvfDataFromRE(jwtUser.getCompany().getUuid(), claimuuid);

					RcmRules rule = getRulesFromList(rules, RuleConstants.RULE_ID_301);
					allLIst.addAll(ruleBookService.rule301(rule, ivData, claim.getSecMemberId()));

					rule = getRulesFromList(rules, RuleConstants.RULE_ID_302);

					allLIst.addAll(ruleBookService.rule302(rule, ivData, claim.getGroupNumber()));

					rule = getRulesFromList(rules, RuleConstants.RULE_ID_303);
					// allLIst.addAll(ruleBookService.rule303(rule, ivData, claim));

					rule = getRulesFromList(rules, RuleConstants.RULE_ID_304);
					Object providerSheetData[] = ConnectAndReadSheets.readProviderGSheet(
							"1g9VtQVT5T0-Fp_beLSYhRIbUn-KBqP4TGmYuteMbsd4", "Provider", CLIENT_SECRET_DIR,
							CREDENTIALS_FOLDER);
					HashMap<String, String> doc1NameMap = ConnectAndReadSheets.readProviderScheduleGSheet(
							"1GK8lWBc3rXgtnm6hzxcFS_ueS0QGb5tBGaKdskSFzuA",
							calendar.get(Calendar.YEAR) + " Provider Schedule", CLIENT_SECRET_DIR, CREDENTIALS_FOLDER);
	                String officeName=officeRepo.findById(claim.getOffice().getUuid()).get().getName();
					allLIst.addAll(ruleBookService.rule304(rule, doc1NameMap, claim, officeName, providerSheetData));

					rule = getRulesFromList(rules, RuleConstants.RULE_ID_305);
					List<CredentialData> creList = ConnectAndReadSheets.readCredentialGSheet(
							"1QPfa8aEA5pcSWhGJQSX9R7CC-ZxVA-8_nvagjGAA4s4", "Master Data", CLIENT_SECRET_DIR,
							CREDENTIALS_FOLDER);
					claim.setRcmInsuranceType(rcmInsuranceTypeRepo.findById(claim.getRcmInsuranceType().getId()));
					
					allLIst.addAll(
							ruleBookService.rule305(rule, creList, claim, officeName, providerSheetData));

					// Save Data here RcmClaimRuleValidation
					saveAutoRuleReport(allLIst, user, claim, rules);
					claim.setAutoRuleRun(true);
					rcmClaimRepository.save(claim);
				} catch (Exception sheet) {
					sheet.printStackTrace();
				}
			}
			
			

		} else {
			logger.error("Wrong Client Name");
			return null;
		}

		return "success";
	}

	private void saveAutoRuleReport(List<TPValidationResponseDto> ruleDataList, RcmUser user, RcmClaims claim,
			List<RcmRules> rules) {

		RcmClaimRuleValidation val = null;
		for (TPValidationResponseDto ruleData : ruleDataList) {

			RcmRules rule = getRulesFromListById(rules, ruleData.getRuleId());
			val = rcmClaimRuleValidationRepo.findByRuleAndClaim(rule, claim);
			if (val == null) {
				val = new RcmClaimRuleValidation();
				val.setCreatedBy(user);
				val.setRule(rule);
				val.setClaim(claim);
			} else {

				val.setUpdatedBy(user);
				val.setUpdatedDate(new Date());
			}

			val.setMessage(ruleData.getMessage());
			val.setMessageType(MessageUtil.getReportMessageType(ruleData.getMessage()));
			val.setActive(true);
			val.setRunBy(user);
			val.setTeamId(user.getTeam());
			rcmClaimRuleValidationRepo.save(val);
		}
	}

	public RcmRules getRulesFromList(List<RcmRules> rules, String name) {
		RcmRules r = null;
		Collection<RcmRules> ruleGen = Collections2.filter(rules, rule -> rule.getShortName().equals(name));
		for (RcmRules rule : ruleGen) {
			r = rule;
		}

		return r;
	}

	public RcmRules getRulesFromListById(List<RcmRules> rules, int id) {
		RcmRules r = null;
		Collection<RcmRules> ruleGen = Collections2.filter(rules, rule -> rule.getId() == id);
		for (RcmRules rule : ruleGen) {
			r = rule;
		}

		return r;
	}

}
