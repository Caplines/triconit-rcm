package com.tricon.rcm.service.impl;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
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
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.bind.annotation.RequestBody;

import com.google.common.collect.Collections2;
import com.tricon.rcm.db.entity.RcmClaimArchiveHistory;
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
import com.tricon.rcm.db.entity.RcmTPDetail;
import com.tricon.rcm.db.entity.RcmTeam;
import com.tricon.rcm.db.entity.RcmUser;
import com.tricon.rcm.db.entity.RcmUserCompany;
import com.tricon.rcm.db.entity.RcmUserRole;
import com.tricon.rcm.db.entity.UserAssignOffice;
import com.tricon.rcm.dto.AllPendencyReportDto;
import com.tricon.rcm.dto.ArchiveClaimsPayloadDto;
import com.tricon.rcm.dto.AssigmentClaimListDto;
import com.tricon.rcm.dto.AutoRunClaimReponseDto;
import com.tricon.rcm.dto.CaplineIVFFormDto;
import com.tricon.rcm.dto.ClaimAssignDto;
import com.tricon.rcm.dto.ClaimAssignWithRemarkAndTeam;
import com.tricon.rcm.dto.ClaimDataDetails;
import com.tricon.rcm.dto.ClaimDetailDto;
import com.tricon.rcm.dto.ClaimEditDetailDto;
import com.tricon.rcm.dto.ClaimEditDto;
import com.tricon.rcm.dto.KeyValueDto;
import com.tricon.rcm.dto.ListOfClaimsCountsDto;
import com.tricon.rcm.dto.PartialHeader;
import com.tricon.rcm.dto.PendencyDataCountDto;
import com.tricon.rcm.dto.PendencyKeyValDto;
import com.tricon.rcm.dto.PendencyWithOfficeOnlyDto;
import com.tricon.rcm.dto.ProivderHelpingSheetDto;
import com.tricon.rcm.dto.ProviderCodeWithOffice;
import com.tricon.rcm.dto.ProviderCodeWithSpecialty;
import com.tricon.rcm.dto.RcmArchiveClaimsDto;
import com.tricon.rcm.dto.RcmClaimDetailViewDto;
import com.tricon.rcm.dto.RcmClaimsServiceRuleValidationDto;
import com.tricon.rcm.dto.RcmIVfDto;
import com.tricon.rcm.dto.RcmIssuClaimPaginationDto;
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
import com.tricon.rcm.dto.ClaimServiceValidationGSheetData;
import com.tricon.rcm.dto.ClaimSourceDto;
import com.tricon.rcm.dto.ClaimStatusUpdate;
import com.tricon.rcm.dto.ClaimSubDet;
import com.tricon.rcm.dto.ClaimSubmissionDto;
import com.tricon.rcm.dto.CommonSectionsRequestBodyDto;
import com.tricon.rcm.dto.CredentialData;
import com.tricon.rcm.dto.CredentialDataAnesthesia;
import com.tricon.rcm.dto.EobLink;
import com.tricon.rcm.dto.FindTLExistDto;
import com.tricon.rcm.dto.FreshClaimDataImplDto;
import com.tricon.rcm.dto.FreshClaimDataViewDto;
import com.tricon.rcm.dto.InsuranceNameTypeDto;
import com.tricon.rcm.dto.customquery.FreshClaimLogDto;
import com.tricon.rcm.dto.customquery.IVFDto;
import com.tricon.rcm.dto.customquery.IssueClaimDto;
import com.tricon.rcm.dto.customquery.ProductionDto;
import com.tricon.rcm.dto.customquery.RcmClaimDetailDto;
import com.tricon.rcm.dto.customquery.RcmClaimNoteDto;
import com.tricon.rcm.dto.customquery.RcmClaimSubmissionDto;
import com.tricon.rcm.dto.customquery.RuleEngineClaimDto;
import com.tricon.rcm.dto.RcmOfficeDto;
import com.tricon.rcm.dto.RcmResponseMessageDto;
import com.tricon.rcm.dto.RcmTeamDto;
import com.tricon.rcm.dto.RcmUnarchiveClaimsDto;
import com.tricon.rcm.dto.RemoteLietStatusCount;
import com.tricon.rcm.dto.RuleRemarkDto;
import com.tricon.rcm.dto.SearchParamDto;
import com.tricon.rcm.dto.SectionDto;
import com.tricon.rcm.dto.ServiceValidationDataDto;
import com.tricon.rcm.dto.TPValidationResponseDto;
import com.tricon.rcm.dto.TimelyFilingLimitDto;
import com.tricon.rcm.dto.UnArchiveClaimDto;
import com.tricon.rcm.dto.UnArchivedResponseDto;
import com.tricon.rcm.dto.UnarchiveClaimsPayloadDto;
import com.tricon.rcm.dto.customquery.AllPendencyDateDto;
import com.tricon.rcm.dto.customquery.AllPendencyDto;
import com.tricon.rcm.dto.customquery.AssignFreshClaimLogsDto;
import com.tricon.rcm.dto.customquery.AssignFreshClaimLogsImplDto;
import com.tricon.rcm.dto.customquery.ClaimRemarksDto;
import com.tricon.rcm.dto.customquery.ClaimRuleRemarksDto;
import com.tricon.rcm.dto.customquery.ClaimRuleValidationDto;
import com.tricon.rcm.dto.customquery.CompanyIdAndNameDto;
import com.tricon.rcm.dto.customquery.DataPatientRuleDto;
import com.tricon.rcm.dto.customquery.FreshClaimDataDto;
import com.tricon.rcm.dto.customquery.FreshClaimDetailsDto;
import com.tricon.rcm.dto.customquery.FreshClaimDetailsImplDto;
import com.tricon.rcm.enums.ClaimSourceEnum;
import com.tricon.rcm.enums.ClaimStatusEnum;
import com.tricon.rcm.enums.ClaimTypeEnum;
import com.tricon.rcm.enums.RcmRoleEnum;
import com.tricon.rcm.enums.RcmTeamEnum;
import com.tricon.rcm.jpa.repository.RCMUserRepository;
import com.tricon.rcm.jpa.repository.RcmClaimArchiveHistoryRepo;
import com.tricon.rcm.jpa.repository.RcmClaimAssignmentRepo;
import com.tricon.rcm.jpa.repository.RcmClaimAttachmentRepo;
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
import com.tricon.rcm.jpa.repository.RcmTPDetailRepo;
import com.tricon.rcm.jpa.repository.RcmTeamRepo;
import com.tricon.rcm.jpa.repository.RcmUserCompanyRepo;
import com.tricon.rcm.jpa.repository.UserAssignOfficeRepo;
import com.tricon.rcm.security.JwtUser;
import com.tricon.rcm.util.ClaimUtil;
import com.tricon.rcm.util.ConnectAndReadSheets;
import com.tricon.rcm.util.Constants;
import com.tricon.rcm.util.MessageConstants;
import com.tricon.rcm.util.RuleConstants;
import com.tricon.rcm.util.MessageUtil;
import com.tricon.rcm.util.NextTeamClaimTransferUtil;

@Service
public class ClaimServiceImpl {

	@Autowired
	RuleEngineService ruleEngineService;

	@Autowired
	RcmMappingTableRepo rcmMappingTableRepo;

	@Autowired
	RcmCompanyRepo rcmCompanyRepo;
	
	@Autowired
	RcmUserCompanyRepo rcmUserCompanyRepo;

	@Autowired
	RCMUserRepository userRepo;

	@Autowired
	RcmTeamRepo rcmTeamRepo;

	@Value("${google.credential.folder}")
	private String CREDENTIALS_FOLDER;

	@Value("${google.client.secret}")
	private String CLIENT_SECRET_DIR;
	
	@Value("${eoblink.folder}")
	private String eobLinkFolder;

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
	
	@Autowired
	RcmCommonServiceImpl rcmCommonServiceImpl;
	
	@Autowired
	RcmTPDetailRepo rcmTPDetailRepo;
	
	@Value("${data.archiveClaims.totalRecordperPage}")
	private int totalRecordsperPage;
	
	@Autowired
	RcmClaimArchiveHistoryRepo rcmClaimArchiveHistoryRepo;

	@Autowired
	RcmIssueClaimsRepo issueClaimRepo;
	
	@Autowired
	 RcmClaimAttachmentRepo attachmentRepo;
	
	@Autowired
	RcmUserCompanyRepo userCompanyRepo;
	
	@Autowired
	RcmClaimRepository claimRepo;
	
	@Autowired
	RcmCompanyRepo companyRepo;
	
	@Autowired
	MasterServiceImpl masterService;
	

	private final Logger logger = LoggerFactory.getLogger(ClaimServiceImpl.class);

	@Transactional(rollbackFor = Exception.class)
	public Object pullClaimFromSource(ClaimSourceDto dto, RcmUser user, PartialHeader partialHeader) {
		// go to Rule Engine.
		Object status = null;
		if (dto.getSource().equals(ClaimSourceEnum.GOOGLESHEET.toString())) {
			try {
				if (user == null) {
					user = userRepo.findByUuid(partialHeader.getJwtUser().getUuid());
				}
				status = pullAndSaveClaimFromSheet(dto, user);
			} catch (Exception error) {

			}
		} else {
			List<ClaimLogDto> mapcountNew = new ArrayList<>();
			ClaimLogDto claimLogDto = null;
			if (dto.getOfficeuuids() == null) {
				List<String> of = new ArrayList<>();
				officeRepo.findByCompany(partialHeader.getCompany()).stream().map(RcmOfficeDto::getUuid).forEach(of::add);
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
						.pullTimelyFilingLmtMappingFromSheet(partialHeader.getCompany());
				try {
					data = ruleEngineService.pullAndSaveClaimFromRE(d, user, li, ClaimTypeEnum.P, partialHeader.getCompany(),
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
					data = ruleEngineService.pullAndSaveClaimFromRE(d, user, li, ClaimTypeEnum.S, partialHeader.getCompany(),
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
		
		ruleEngineService.pullClaimDetailsFromES(partialHeader.getCompany(), dto.getOfficeuuid());
		return status;
	}

	/**
	 * Service For Billing Pendency Dashboard (Get Fresh Claims/Rebill Count
	 * Details)
	 * 
	 * @return
	 */
	public List<FreshClaimDetailsImplDto> fetchBillingClaimDetails(int billType,PartialHeader partialHeader) {

		List<FreshClaimDetailsDto> dtoList = null;
		/*if (jwtUser.isTeamLead()) {// && jwtUser.isAssociate()) || jwtUser.isTeamLead()
			dtoList = rcmClaimRepository.fetchBillingOrReBillingClaimDetails(partialHeader.getCompany().getUuid(), billType,
					partialHeader.getTeamId());
		} else if (jwtUser.isAssociate()) {
			dtoList = rcmClaimRepository.fetchBillingOrReBillingClaimDetails(partialHeader.getCompany().getUuid(), billType,
					partialHeader.getTeamId(), partialHeader.getJwtUser().getUuid());
		}*/
		dtoList = rcmClaimRepository.fetchBillingOrReBillingClaimDetails(partialHeader.getCompany().getUuid(), billType,
				partialHeader.getTeamId());
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
						dF.setOpdos(Constants.SDF_MYSL_DATE.parse(d.getOpdos()));
					}
				} catch (Exception dt) {

				}
				try {
					if (d.getOpdt() != null) {
						// Simple 2023-01-12
						// Constants.SDF_UI.format(Constants.SDF_ES_DATE.parse((d.getOpdos())
						// System.out.println(d.getOpdos());
						dF.setOpdt(Constants.SDF_MYSL_DATE.parse(d.getOpdt()));
					}
				} catch (Exception dt) {

				}

				dF.setRemoteLiteRejections(d.getRemoteLiteRejections());

				counts = remoteLiteMap.get(d.getOfficeName());
				//Rejected=Rejected +Printed 
				if (counts != null)
					dF.setRemoteLiteRejections(counts.getRejectedCount() + counts.getPrintedCount());
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
		InsuranceNameTypeDto insuranceNameTypeDto=null;
		RcmCompany companyIns = rcmCompanyRepo.findByName(Constants.COMPANY_NAME);//Always
		Map<String,List<InsuranceNameTypeDto>> insuranceTypeDtos =new HashMap();// ruleEngineService.pullInsuranceMappingFromSheet(companyIns);
		List<TimelyFilingLimitDto> timelyFilingLimits = ruleEngineService.pullTimelyFilingLmtMappingFromSheet(company);
		RcmClaimStatusType systemStatusBilling = rcmClaimStatusTypeRepo.findByStatus(ClaimStatusEnum.Billing.getType());
		RcmClaimStatusType systemStatusReBilling = rcmClaimStatusTypeRepo
				.findByStatus(ClaimStatusEnum.ReBilling.getType());

		RcmTeam assignedTeamBilling  = rcmTeamRepo.findById(RcmTeamEnum.BILLING.getId());
		RcmTeam assignedTeamInternalAudit  = rcmTeamRepo.findById(RcmTeamEnum.INTERNAL_AUDIT.getId());
		
		 List<InsuranceNameTypeDto> insuranceTypeDto =null;
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
						insuranceNameTypeDto= null;
						System.out.println(re.getClaimId() + "--<ID");
						ClaimTypeEnum claimTypeEnum = ClaimTypeEnum.P;
						RcmClaimStatusType claimStatusType = null;

						if (companies.get(re.getClientName()) == null) {
							clCompany = rcmCompanyRepo.findByName(re.getClientName());
							
							if (clCompany != null) {
								companies.put(re.getClientName(), clCompany);
								insuranceTypeDto=insuranceTypeDtos.get(re.getClientName());
								if (insuranceTypeDto == null) {
									insuranceTypeDtos.put(re.getClientName(), ruleEngineService.pullInsuranceMappingFromSheet(companyIns));
									insuranceTypeDto = insuranceTypeDtos.get(re.getClientName());
								}
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
						UserAssignOffice assignedUserBilling = userAssignOfficeRepo.findByOfficeUuidAndTeamId(off.getUuid(),
								RcmTeamEnum.BILLING.getId());
						UserAssignOffice assignedUserInternalAudit = userAssignOfficeRepo.findByOfficeUuidAndTeamId(off.getUuid(),
								RcmTeamEnum.INTERNAL_AUDIT.getId());

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
										//rcmInsuranceType = new RcmInsuranceType();
										//rcmInsuranceType.setName(re.getInsuranceName());
										//rcmInsuranceType.setId(rcmInsuranceTypeRepo.save(rcmInsuranceType).getId());
										//rcmInsuranceTypes.put(re.getInsuranceName(), rcmInsuranceType);
									} else {
										rcmInsuranceTypes.put(re.getInsuranceName(), rcmInsuranceType);
									}

								}
								
								ins.setName(re.getPrimaryInsuranceCompany());
								ins.setOffice(off);
								//ins.setId(insuranceRepo.save(ins).getId());
								 if (rcmInsuranceType==null) {
								 error.add( "Insurance Type Missing For Name:"+re.getInsuranceName() +" in G-Sheet");
								 }else {
							     ins.setInsuranceType(rcmInsuranceTypes.get(re.getInsuranceName()));
								 ins.setId(insuranceRepo.save(ins).getId());
								 }
								 
								 insuranceNameTypeDto= ruleEngineService.getInsuranceTypeFromSheetListByNameAndClient(insuranceTypeDto, re.getPrimaryInsuranceCompany().trim(),re.getClientName());
							}
							
							if (ins.getInsuranceCode()==null || (ins.getInsuranceCode()!=null && ins.getInsuranceCode().trim().equals(""))) {
								insuranceNameTypeDto= ruleEngineService.getInsuranceTypeFromSheetListByNameAndClient(insuranceTypeDto, re.getPrimaryInsuranceCompany().trim(),re.getClientName());
								if (insuranceNameTypeDto!=null) {
									ins.setInsuranceCode(insuranceNameTypeDto.getInsuranceCode());
									insuranceRepo.save(ins);
								}
								
							}
							TimelyFilingLimitDto timely=null;
							if (insuranceNameTypeDto!=null) {
								timely = ClaimUtil.getTimelyLimitFromSheetListByCode(timelyFilingLimits,
										insuranceNameTypeDto.getInsuranceCode().trim());
								
							}else if(ins.getInsuranceCode()!=null){
                            	insuranceNameTypeDto= ruleEngineService.getInsuranceTypeFromSheetListByNameAndClient(insuranceTypeDto, re.getPrimaryInsuranceCompany().trim(),re.getClientName());
								if (insuranceNameTypeDto!=null) {
									ins.setInsuranceCode(insuranceNameTypeDto.getInsuranceCode());
									insuranceRepo.save(ins);
                            	timely = ClaimUtil.getTimelyLimitFromSheetListByCode(timelyFilingLimits,
                            			ins.getInsuranceCode().trim());
                            	
								}
                            }
							if (timely == null) {
								 error.add("Timely Limit Type Missing for Primary Ins. :"+re.getPrimaryInsuranceCompany());

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

								
							boolean isBilling= true;
							boolean isMedicaid= false;
							boolean isMedicare =false;
							boolean isChip =false;
							
							if (clCompany.getName().equals(Constants.COMPANY_NAME)) {
								isBilling = ClaimUtil.isBillingClaimByInsuranceName(rcmInsuranceType.getName());
								isMedicaid=ClaimUtil.isMedcaidClaimByInsuranceName(rcmInsuranceType.getName());
								isMedicare=ClaimUtil.isMedicareClaimByInsuranceName(ins.getInsuranceType().getName());
								isChip=ClaimUtil.isChipClaimByInsuranceName(ins.getInsuranceType().getName());
							}
							boolean missing=true;
							if (isBilling) {
							claim = ClaimUtil.createClaimFromSheetData(claim, off, re,
									ClaimUtil.filterTeamByNameId(allTeams, RcmTeamEnum.BILLING.toString()), user, ins,
									ins, claimStatusType, claimTypeEnum.getSuffix(), rcmInsuranceType, timely.getTimelyFilingLimit(),""
									,claimTypeEnum);
							missing=false;
							}
							if (isMedicaid || isMedicare || isChip) {
								claim = ClaimUtil.createClaimFromSheetData(claim, off, re,
										ClaimUtil.filterTeamByNameId(allTeams, RcmTeamEnum.INTERNAL_AUDIT.toString()), user, ins,
										ins, claimStatusType, claimTypeEnum.getSuffix(), rcmInsuranceType, timely.getTimelyFilingLimit(),
										"",claimTypeEnum);
								missing=false;
							}
							if(missing) {//no Billing or Medicaid
								//put in billing 
								claim = ClaimUtil.createClaimFromSheetData(claim, off, re,
										ClaimUtil.filterTeamByNameId(allTeams, RcmTeamEnum.BILLING.toString()), user, ins,
										ins, claimStatusType, claimTypeEnum.getSuffix(), rcmInsuranceType, timely.getTimelyFilingLimit(),
										"",claimTypeEnum);
								missing=false;
								isBilling=true;
							}
								
							String claimUUid = rcmClaimRepository.save(claim).getClaimUuid();
							//Save Data in rcm_claim_detail (new Enhancement)
							if (re.getServiceCodes().size()>0) {
								RcmClaimDetail det=null;
								int serviceCount= 0;
								List<String> toothSurfaces=re.getToothAndSurfaces();
								for(String cds:re.getServiceCodes()) {
									det =new RcmClaimDetail();
									det.setApptId("");
									det.setClaim(claim);
									det.setDescription("");
									det.setEstInsurance("-1");
									det.setEstPrimary("-1");
									det.setFee("-1");
									det.setIdEs("-1");
									det.setLineItem("-1");
									det.setPatientPortion("-1");
									det.setPatientPortionSec("-1");
									det.setProviderLastName("");
									det.setServiceCode(cds.trim());
									det.setStatus("");
									
									det.setSurface("");
									det.setTooth("");
									try {
										 String[] ths = toothSurfaces.get(serviceCount++).split("#");
										 det.setTooth(ths[0]);
										 if (ths.length>1) {
											 det.setSurface(ths[1]);
										 }
										 
										}catch(Exception b ) {
										b.printStackTrace();
									  }
									rcmClaimDetailRepo.save(det);
									
								}
									
							}
							
							
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
							if (assignedUserBilling != null && isBilling) {
								rcmAssigment = new RcmClaimAssignment();
								//
								rcmAssigment = ClaimUtil.createAssginmentData(rcmAssigment, user,
										assignedUserBilling.getUser(), claimUUid, claim, "",
										systemStatusBilling,assignedTeamBilling,Constants.SYSTEM_INITIAL_COMMENT);

								rcmClaimAssignmentRepo.save(rcmAssigment);
							}
							if (assignedUserInternalAudit != null && (isMedicaid|| isMedicare || isChip)) {
								rcmAssigment = new RcmClaimAssignment();
								//
								rcmAssigment = ClaimUtil.createAssginmentData(rcmAssigment, user,
										assignedUserInternalAudit.getUser(), claimUUid, claim, "",
										systemStatusBilling,assignedTeamInternalAudit,Constants.SYSTEM_INITIAL_COMMENT);

								rcmClaimAssignmentRepo.save(rcmAssigment);
							}

							if (isC != null) {
								isC.setResolved(true);
								rcmIssueClaimsRepo.save(isC);
							}
						//}
						}else {
							//no claim Added
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
						insuranceNameTypeDto= null;
						System.out.println(re.getClaimId() + "--<ID");
						ClaimTypeEnum claimTypeEnum = ClaimTypeEnum.S;
						RcmClaimStatusType claimStatusType = null;

						if (companies.get(re.getClientName()) == null) {
							clCompany = rcmCompanyRepo.findByName(re.getClientName());
							if (clCompany != null) {
								companies.put(re.getClientName(), clCompany);
								insuranceTypeDto=insuranceTypeDtos.get(re.getClientName());
								if (insuranceTypeDto == null) {
									insuranceTypeDtos.put(re.getClientName(), ruleEngineService.pullInsuranceMappingFromSheet(companyIns));
									insuranceTypeDto = insuranceTypeDtos.get(re.getClientName());
								}
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
						UserAssignOffice assignedUserBilling = userAssignOfficeRepo.findByOfficeUuidAndTeamId(off.getUuid(),
								RcmTeamEnum.BILLING.getId());
						UserAssignOffice assignedUserInternalAudit = userAssignOfficeRepo.findByOfficeUuidAndTeamId(off.getUuid(),
								RcmTeamEnum.INTERNAL_AUDIT.getId());

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
										//rcmInsuranceType = new RcmInsuranceType();
										//rcmInsuranceType.setName(re.getSecondaryInsuranceName());
										//rcmInsuranceType.setId(rcmInsuranceTypeRepo.save(rcmInsuranceType).getId());
										//rcmInsuranceTypes.put(re.getSecondaryInsuranceName(), rcmInsuranceType);
									} else {
										rcmInsuranceTypes.put(re.getSecondaryInsuranceName(), rcmInsuranceType);
									}
								}

								ins.setName(re.getSecondaryInsuranceCompany());
								ins.setOffice(off);
								//ins.setId(insuranceRepo.save(ins).getId());
								 if (rcmInsuranceType==null) {
								 error.add( "Insurance Type Missing For for Name:"+re.getSecondaryInsuranceName() +" in G-Sheet");
								 }else {
								    ins.setInsuranceType(rcmInsuranceTypes.get(re.getSecondaryInsuranceName()));
									ins.setId(insuranceRepo.save(ins).getId());
								 }
								 insuranceNameTypeDto= ruleEngineService.getInsuranceTypeFromSheetListByNameAndClient(insuranceTypeDto, re.getSecondaryInsuranceCompany().trim(),re.getClientName());
							}
							TimelyFilingLimitDto timely = null;
							if (ins.getInsuranceCode()==null || (ins.getInsuranceCode()!=null && ins.getInsuranceCode().trim().equals(""))) {
							//if (ins.getInsuranceCode()==null) {
								insuranceNameTypeDto= ruleEngineService.getInsuranceTypeFromSheetListByNameAndClient(insuranceTypeDto, re.getSecondaryInsuranceCompany().trim(),re.getClientName());
								if (insuranceNameTypeDto!=null) {
									ins.setInsuranceCode(insuranceNameTypeDto.getInsuranceCode());
									insuranceRepo.save(ins);
								}
								
							}else if(ins.getInsuranceCode()!=null){
                            	insuranceNameTypeDto= ruleEngineService.getInsuranceTypeFromSheetListByNameAndClient(insuranceTypeDto, re.getSecondaryInsuranceCompany().trim(),re.getClientName());
								if (insuranceNameTypeDto!=null) {
									ins.setInsuranceCode(insuranceNameTypeDto.getInsuranceCode());
									insuranceRepo.save(ins);
                            	timely = ClaimUtil.getTimelyLimitFromSheetListByCode(timelyFilingLimits,
                            			ins.getInsuranceCode().trim());
								}
                            }
							
							if (insuranceNameTypeDto!=null) timely = ClaimUtil.getTimelyLimitFromSheetListByCode(timelyFilingLimits,
									insuranceNameTypeDto.getInsuranceCode());
							if (timely == null) {
								 error.add("Timely Limit Type Missing for Secondary Ins. :"+re.getSecondaryInsuranceCompany());

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
							
							boolean isBilling= true;
							boolean isMedicaid= false;
							boolean isMedicare= false;
							boolean isChip= false;
							//For External we always have claims in Billing 
							if (clCompany.getName().equals(Constants.COMPANY_NAME)) {
								isBilling = ClaimUtil.isBillingClaimByInsuranceName(rcmInsuranceType.getName());
								isMedicaid=ClaimUtil.isMedcaidClaimByInsuranceName(rcmInsuranceType.getName());
								isMedicare=ClaimUtil.isMedicareClaimByInsuranceName(ins.getInsuranceType().getName());
								isChip=ClaimUtil.isChipClaimByInsuranceName(ins.getInsuranceType().getName());
							}
							
							//boolean isBilling=ClaimUtil.isBillingClaimByInsuranceName(rcmInsuranceType.getName());
							//boolean isMedicaid=ClaimUtil.isMedcaidClaimByInsuranceName(rcmInsuranceType.getName());
							//boolean isMedicare=ClaimUtil.isMedicareClaimByInsuranceName(ins.getInsuranceType().getName());
							boolean missing=true;
							if (isBilling) {
							
							claim = ClaimUtil.createClaimFromSheetData(claim, off, re,
									ClaimUtil.filterTeamByNameId(allTeams, RcmTeamEnum.BILLING.toString()), user, ins,
									ins, claimStatusType, claimTypeEnum.getSuffix(), rcmInsuranceType, timely.getTimelyFilingLimit(),"",
									claimTypeEnum);
							missing=false;
							}
							if (isMedicaid || isMedicare || isChip) {
								claim = ClaimUtil.createClaimFromSheetData(claim, off, re,
										ClaimUtil.filterTeamByNameId(allTeams, RcmTeamEnum.INTERNAL_AUDIT.toString()), user, ins,
										ins, claimStatusType, claimTypeEnum.getSuffix(), rcmInsuranceType, timely.getTimelyFilingLimit(),
										"",claimTypeEnum);
								missing=false;
							}
							if(missing) {//no Billing or Medicaid
								//put in billing 
								claim = ClaimUtil.createClaimFromSheetData(claim, off, re,
										ClaimUtil.filterTeamByNameId(allTeams, RcmTeamEnum.BILLING.toString()), user, ins,
										ins, claimStatusType, claimTypeEnum.getSuffix(), rcmInsuranceType, timely.getTimelyFilingLimit(),
										"",claimTypeEnum);
								missing=false;
								isBilling=true;
							}
							
							
							String claimUUid = rcmClaimRepository.save(claim).getClaimUuid();
							//Save Data in rcm_claim_detail (new Enhancement)
							if (re.getServiceCodes().size()>0) {
								RcmClaimDetail det=null;
								for(String cds:re.getServiceCodes()) {
									det =new RcmClaimDetail();
									det.setApptId("");
									det.setClaim(claim);
									det.setDescription("");
									det.setEstInsurance("-1");
									det.setEstPrimary("-1");
									det.setFee("-1");
									det.setIdEs("-1");
									det.setLineItem("-1");
									det.setPatientPortion("-1");
									det.setPatientPortionSec("-1");
									det.setProviderLastName("");
									det.setServiceCode(cds.trim());
									det.setStatus("");
									det.setSurface("");
									det.setTooth("");
									rcmClaimDetailRepo.save(det);
								}
									
							}
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
							if (assignedUserBilling != null && isBilling) {
								rcmAssigment = new RcmClaimAssignment();
								//
								rcmAssigment = ClaimUtil.createAssginmentData(rcmAssigment, user,
										assignedUserBilling.getUser(), claimUUid, claim, "",
										systemStatusBilling,assignedTeamBilling,Constants.SYSTEM_INITIAL_COMMENT);

								rcmClaimAssignmentRepo.save(rcmAssigment);
							}
							if (assignedUserInternalAudit != null && (isMedicaid|| isMedicare || isChip)) {
								rcmAssigment = new RcmClaimAssignment();
								//
								rcmAssigment = ClaimUtil.createAssginmentData(rcmAssigment, user,
										assignedUserInternalAudit.getUser(), claimUUid, claim, "",
										systemStatusBilling,assignedTeamInternalAudit,Constants.SYSTEM_INITIAL_COMMENT);

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

					RcmClaimLog l= entry.getValue();
					claimLogDto = new ClaimLogDto(source, l.getOffice().getUuid(), 1, newClaimCt,
							new Date(), entry.getValue().getOffice().getName());
					System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
					commonClaimServiceImpl.saveClaimLog(entry.getValue(), user, entry.getValue().getOffice(),
							ClaimSourceEnum.GOOGLESHEET.toString(), 1,claimLogDto.getNewClaimsCount(),l.getNewClaimsPrimaryCount(),l.getNewClaimsSecodaryCount(),
							success);
					mapcountNew.put(entry.getKey(), claimLogDto);

				}
				RcmClaimLog l=null;
				 for(RcmOffice fName: rcmOffices) {
					 if (logMap.get(fName.getName())==null) {
						 claimLogDto = new ClaimLogDto(source, fName.getUuid(), 1, 0,
									new Date(), fName.getName());
						 mapcountNew.put(fName.getName(), claimLogDto); 
						 
						    l = new RcmClaimLog();
							l.setOffice(fName);
							l.setNewClaimsCount(0);
							l.setNewClaimsPrimaryCount(0);
							l.setNewClaimsSecodaryCount(0);
						 commonClaimServiceImpl.saveClaimLog(l, user, fName,
									ClaimSourceEnum.GOOGLESHEET.toString(), 1,0,0,0,
									success);
					 }
	               }

			}

			for (Map.Entry<String, ClaimLogDto> entry : mapcountNew.entrySet()) {

				logClaimDtos.add(entry.getValue());

			}
		} catch (Exception n) {
			n.printStackTrace();
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

	public Object fetchRemoteLiteRejections(PartialHeader partialHeader) {

			ClaimSourceDto dto = new ClaimSourceDto();
		// dto.setOfficeuuid(officeUUid);
		dto.setCompanyuuid(partialHeader.getCompany().getUuid());
		// only for Simple Point
		if (partialHeader.getCompany().getName().equals(Constants.COMPANY_NAME)) {

			// RcmUser user= userRepo.findByEmail(((UserDetails) principal).getUsername());
			Object obj = ruleEngineService.pullAndSaveRemoteLiteData();
			return obj;
		} else
			return null;
	}

	public List<FreshClaimDataViewDto> fetchFreshClaimDetails(int teamId, int billingORRebill, String sub,
			PartialHeader partialHeader) {
        ///Add more logic
		List<FreshClaimDataDto> list=null;
		List<FreshClaimDataViewDto> listView=new ArrayList<>();
		
		
		if (sub.equals("Fresh")) {
			if (partialHeader.getRole().equals(Constants.ASSOCIATE)) {
				if (teamId != RcmTeamEnum.BILLING.getId() && teamId != RcmTeamEnum.INTERNAL_AUDIT.getId())  {
					list = rcmClaimRepository.fetchFreshClaimDetailsOtherTeamInd(partialHeader.getCompany().getUuid(), teamId,partialHeader.getJwtUser().getUuid());
				}else {
					list = rcmClaimRepository.fetchFreshClaimDetailsInd(partialHeader.getCompany().getUuid(), teamId, partialHeader.getJwtUser().getUuid());
						
				}
			}
 			else {
 				if (teamId != RcmTeamEnum.BILLING.getId() && teamId != RcmTeamEnum.INTERNAL_AUDIT.getId()) {
 					list = rcmClaimRepository.fetchFreshClaimDetailsOtherTeam(partialHeader.getCompany().getUuid(), teamId);	
 				}else {
 					list = rcmClaimRepository.fetchFreshClaimDetails(partialHeader.getCompany().getUuid(), teamId);
 	 					
 				}
 			}
			 if (teamId == RcmTeamEnum.BILLING.getId()){
			     if (list==null) list= new ArrayList<>();
			     // add Claims Send From Internal Audit Team
			     
			 }
			 
			 list.forEach(data->{
					final FreshClaimDataViewDto	dataView = new FreshClaimDataViewDto();
						BeanUtils.copyProperties(data, dataView);
						listView.add(dataView);
			});
			 if (teamId != RcmTeamEnum.BILLING.getId() && teamId != RcmTeamEnum.INTERNAL_AUDIT.getId()) {
				 //Need to get Claim remark in of non billing and internal audit
				
				 listView.forEach(data->{
					 data.setLastTeamRemark(rcmClaimAssignmentRepo.findLatestClaimCommentByOtherTeam(data.getUuid(), teamId));
				 });
			 }
			
		}
		// submitted and unsubmitted claims except billing and internal audit teams
		else if (sub.equals(Constants.SUBMITTED_CLAIMS)) {
			
			if (partialHeader.getRole().equals(Constants.ASSOCIATE)) {
				if (teamId != RcmTeamEnum.BILLING.getId() && teamId != RcmTeamEnum.INTERNAL_AUDIT.getId())  {
					list = rcmClaimRepository.fetchSubmittedClaimDetailsOtherTeamInd(partialHeader.getCompany().getUuid(), teamId,partialHeader.getJwtUser().getUuid());
				}
			}
 			else {
 				if (teamId != RcmTeamEnum.BILLING.getId() && teamId != RcmTeamEnum.INTERNAL_AUDIT.getId()) {
 					list = rcmClaimRepository.fetchSubmittedClaimDetailsOtherTeam(partialHeader.getCompany().getUuid(), teamId);	
 				}
 			}
			
			list.forEach(data -> {
				final FreshClaimDataViewDto dataView = new FreshClaimDataViewDto();
				BeanUtils.copyProperties(data, dataView);
				listView.add(dataView);
			});
			 if (teamId != RcmTeamEnum.BILLING.getId() && teamId != RcmTeamEnum.INTERNAL_AUDIT.getId()) {
				 //Need to get Claim remark in of non billing and internal audit
				
				 listView.forEach(data->{
					 data.setLastTeamRemark(rcmClaimAssignmentRepo.findLatestClaimCommentByOtherTeam(data.getUuid(), teamId));
				 });
			 }
		} else if (sub.equals(Constants.UNSUBMITTED_CLAIMS)) {
			//SAME AS FRESH
			if (partialHeader.getRole().equals(Constants.ASSOCIATE)) {
				if (teamId != RcmTeamEnum.BILLING.getId() && teamId != RcmTeamEnum.INTERNAL_AUDIT.getId())  {
					list = rcmClaimRepository.fetchFreshClaimDetailsOtherTeamInd(partialHeader.getCompany().getUuid(), teamId,partialHeader.getJwtUser().getUuid());
				}
			}
 			else {
 				if (teamId != RcmTeamEnum.BILLING.getId() && teamId != RcmTeamEnum.INTERNAL_AUDIT.getId()) {
 					list = rcmClaimRepository.fetchFreshClaimDetailsOtherTeam(partialHeader.getCompany().getUuid(), teamId);	
 				}
 			}
			list.forEach(data -> {
				final FreshClaimDataViewDto dataView = new FreshClaimDataViewDto();
				BeanUtils.copyProperties(data, dataView);
				listView.add(dataView);
			});
			 if (teamId != RcmTeamEnum.BILLING.getId() && teamId != RcmTeamEnum.INTERNAL_AUDIT.getId()) {
				 //Need to get Claim remark in of non billing and internal audit
				
				 listView.forEach(data->{
					 data.setLastTeamRemark(rcmClaimAssignmentRepo.findLatestClaimCommentByOtherTeam(data.getUuid(), teamId));
				 });
			 }
		}
		else {
			//SEND BACK OPTION
			//boolean otherTeam =false;
			if (teamId == RcmTeamEnum.BILLING.getId()) {
			list = rcmClaimRepository.fetchClaimDetailsWorkedByTeamBilling(partialHeader.getCompany().getUuid(), teamId);
			}else if (teamId == RcmTeamEnum.INTERNAL_AUDIT.getId()) { 
				list = rcmClaimRepository.fetchClaimDetailsWorkedByTeamInternalAudit(partialHeader.getCompany().getUuid(), teamId);
			
		    }else {
		    	//otherTeam =true;
		    }
			list.forEach(data->{
				final FreshClaimDataViewDto	dataView = new FreshClaimDataViewDto();
				BeanUtils.copyProperties(data, dataView);
				listView.add(dataView);
				});
		}
          return listView;
	}
	
	public List<FreshClaimDataDto> fetchFreshClaimDetailsLead(int teamId, int billingORRebill, String sub,
			PartialHeader partialHeader) {

		return rcmClaimRepository.fetchFreshClaimDetailsInd(partialHeader.getCompany().getUuid(), teamId, partialHeader.getJwtUser().getUuid());
	
	}

	public List<FreshClaimDataDto> fetchClaimsByTeamNotFrom(int teamId,PartialHeader partialHeader) {

		
		return rcmClaimRepository.fetchFreshClaimDetailsOtherTeam(partialHeader.getCompany().getUuid(), teamId);
	}

	public List<AssignFreshClaimLogsImplDto> fetchClaimsForAssignments(AssigmentClaimListDto dto,PartialHeader partialHeader) {

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
		List<String> companies = findAssociatedCompanyIdByUserUuid(partialHeader);
		try {
			if (partialHeader.getRole().equals(Constants.ASSOCIATE)) {
				l = rcmClaimRepository.fetchClaimsForAssignmentsByTeamAndUser(companies,ct, 
						instDB,partialHeader.getTeamId(),partialHeader.getJwtUser().getUuid());
				
			}else {
				l = rcmClaimRepository.fetchClaimsForAssignmentsByTeam(companies, ct, instDB,partialHeader.getTeamId());
					
			}
			
			
			HashMap<String, RemoteLietStatusCount> remoteLiteMap = ruleEngineService.pullAndSaveRemoteLiteData();
			RemoteLietStatusCount counts = null;

			AssignFreshClaimLogsImplDto dF = null;
			if (l != null) {
				for (AssignFreshClaimLogsDto logD : l) {
					dF = new AssignFreshClaimLogsImplDto();
					dF.setOfficeName(logD.getOfficeName());
					dF.setAssignedUser(logD.getAssignedUser());
					dF.setCount(logD.getCount());
					dF.setFName(logD.getFName());
					dF.setLName(logD.getLName());
					dF.setCompanyName(logD.getCompanyName());
					dF.setOfficeUuid(logD.getOfficeUuid());
//					if (logD.getOpdos()!=null) {
//						//2022-10-12
//						try {
//						Date date=Constants.SDF_MYSL_DATE.parse(logD.getOpdos());  
//						dF.setOpdosd(date);
//						}catch(Exception c) {
//							c.printStackTrace();
//						}
//					}//2023-04-13 03:30:03
//					if (logD.getOpdt()!=null) {
//						try {
//						Date date=Constants.SDF_MYSL_DATE_TIME.parse(logD.getOpdt());  
//						dF.setOpdtd(date);
//						}catch(Exception c) {
//							c.printStackTrace();			
//						}
//					}
					dF.setOpdtd(logD.getOpdt()==null?"0":logD.getOpdt());
					dF.setOpdosd(logD.getOpdos()==null?"0":logD.getOpdos());
					//BeanUtils.copyProperties(logD, dF);
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

	@Transactional
	public FreshClaimDataImplDto fetchIndividualClaim(String claimUuid, PartialHeader partialHeader,boolean pdf) {
		FreshClaimDataImplDto implDto = null;
		
		
		boolean validateClaimRight=checkifCompanyIdMatchesList(partialHeader.getJwtUser().getUuid(),partialHeader.getCompany().getUuid());
		
		if (!validateClaimRight) {
			return null;
		}
		//Check for Duplicate Active Assignment
		List<Integer> assignedClaims =null;
		try {
			assignedClaims= rcmClaimAssignmentRepo.findIssueAssingments(claimUuid);
		 if (assignedClaims.size()>1) {
			 rcmClaimAssignmentRepo.updateClaimIssueAssignment(assignedClaims.get(0),claimUuid);
		 }
		}catch(Exception dup) {
			dup.printStackTrace();
		}
	

		RcmClaimDetailDto dto = rcmClaimRepository.fetchIndividualClaim(claimUuid);
		
		
		if (dto != null) {

			implDto = new FreshClaimDataImplDto();
			//implDto.setSecInsurance("N/A");
			// RcmClaims claim = rcmClaimRepository.findByClaimUuid(claimUuid);
			// Fetch Data from RCM Tool Checks and Validations Sheets //141479965

			BeanUtils.copyProperties(dto, implDto);
			if (implDto.getPatientContactNo() ==null) implDto.setPatientContactNo("");
			if (implDto.getInsuranceContactNo() ==null) implDto.setInsuranceContactNo("");
			
			if (dto.getCompanyId().equals(partialHeader.getCompany().getUuid())) {
				implDto.setClaimCmpMatchesLoggedCmp(true);
			}else {
				implDto.setClaimCmpMatchesLoggedCmp(false);
			}
			implDto.setIvfId(dto.getIvId());
			List<String> linkedClaims = rcmLinkedClaimsRepo.getLinkedClaims(dto.getUuid());
			if (linkedClaims!=null && linkedClaims.size()==0) {
				linkedClaims.add("N/A");
			}else if (linkedClaims ==null) {
				linkedClaims = new ArrayList<>();
				linkedClaims.add("N/A");
			}
			implDto.setLinkedClaims(linkedClaims);
			String ivfId = "", tpId = "",ivDos="",tpDos="";
			String[] clT = implDto.getClaimId().split("_");
			String claimSubTy = Constants.insuranceTypeSecondary;// May be needed latter

			if (("_" + clT[1]).equals(ClaimTypeEnum.P.getSuffix())) {
				claimSubTy = Constants.insuranceTypePrimary;
				implDto.setPrimary(true);
				implDto.setSecMemberId(dto.getSecMemberId());
				
			} else {
				implDto.setPrimary(false);
			}
			if (dto.getSecMemberId()==null) {
				implDto.setSecMemberId("N/A");
			}
			RcmClaims claim = null;
			IVFDto ivfDto =null;
			try {
				
				if (!implDto.isPrimary()) {
					
					if (dto.getSecPolicyHolderDob()!=null) {
						java.sql.Date sqlPackageDate
						= new java.sql.Date(Constants.SDF_MYSL_DATE.parse(dto.getSecPolicyHolderDob()).getTime());
						implDto.setSecPolicyHolderDob(sqlPackageDate);
					}
				}else {
					if (dto.getPrimePolicyHolderDob()!=null) {
						java.sql.Date sqlPackageDate
						= new java.sql.Date(Constants.SDF_MYSL_DATE.parse(dto.getPrimePolicyHolderDob()).getTime());
						implDto.setPrimePolicyHolderDob(sqlPackageDate);
					}
				}
				if (implDto.getIvfId()==null || implDto.getIvfId().equals("")) {
					
				Set<String> insTypes = new HashSet<>();
				insTypes.add(claimSubTy);
				if (claimSubTy.equalsIgnoreCase(Constants.insuranceTypePrimary)) {
					insTypes.add("");
					insTypes.add("No Information");
				}
				ivfDto = rcmClaimRepository.getIVIdOfClaimByDos(Constants.SDF_MYSL_DATE.format(implDto.getDos()), implDto.getOfficeUuid(), implDto.getPatientId(),insTypes);
				//Object ivDet[] = null;
				if (ivfDto != null) {
					claim = rcmClaimRepository.findByClaimUuid(claimUuid);
					ivfId = ivfDto.getIvId();
					ivDos = ivfDto.getDos();
					implDto.setIvfId(ivfId);
					implDto.setIvDos(ivDos);
					claim.setIvfId(ivfId);
					claim.setIvfIdSystem(ivfId);
					claim.setIvDos(ivDos);	
					claim.setSsn(ivfDto.getSsn());
					implDto.setSsn(ivfDto.getSsn());
					if (!implDto.isPrimary()) {
						
						
						if (claim.getSecPolicyHolderDob()==null && Constants.COMPANY_NAME.equals(partialHeader.getCompany().getName())) {
						try {
						//Since we don't have any way to pull the Subscriber's DOB from ES using Query, let's pull that from IV	
						java.sql.Date sqlPackageDate
			            = new java.sql.Date(Constants.SDF_MYSL_DATE.parse(ivfDto.getPdob()).getTime());
						claim.setSecPolicyHolderDob(sqlPackageDate);
						implDto.setSecPolicyHolderDob(sqlPackageDate);
						claim.setSecPolicyHolder(ivfDto.getPdName());
						implDto.setSecPolicyHolder(ivfDto.getPdName());
						}catch(Exception g) {
							
						}
						}
					}else {
						
						
						
						if (claim.getPrimePolicyHolderDob()==null && Constants.COMPANY_NAME.equals(partialHeader.getCompany().getName()))  {
						try {
							//Since we don't have any way to pull the Subscriber's DOB from ES using Query, let's pull that from IV	
							java.sql.Date sqlPackageDate
				            = new java.sql.Date(Constants.SDF_MYSL_DATE.parse(ivfDto.getPdob()).getTime());
							claim.setPrimePolicyHolderDob(sqlPackageDate);
							implDto.setPrimePolicyHolderDob(sqlPackageDate);
							claim.setPrimePolicyHolder(ivfDto.getPdName());
							implDto.setPrimePolicyHolder(ivfDto.getPdName());
							}catch(Exception g) {
								
							}
						}
					}
						
				 }
				
				}else {
					ivfId = implDto.getIvfId();
					
				}
				if (implDto.getTpId() == null) implDto.setTpId("");
				if (!ivfId.equals("") && implDto.getTpId().equals("")) {
					
					
					
					Object tp  = rcmClaimRepository.getLatestTPIdForPatientByIVId(implDto.getOfficeUuid(),
							implDto.getPatientId(), ivfId);
					if (tp != null) {
						Object[] tpDet = (Object[]) tp;
						if (tpDet != null && tpDet.length == 2) {
							tpId = (String) tpDet[0];
							tpDos = (String) tpDet[1];
							if (claim==null)  claim = rcmClaimRepository.findByClaimUuid(claimUuid);
								claim.setTpId(tpId);
								claim.setTpDos(tpDos);
								implDto.setTpId(tpId);
								implDto.setTpDos(tpDos);
								try {
									pullAndSaveTpDataFromRE(claim,claimUuid, tpId, implDto.getOfficeUuid(), partialHeader);
									}catch(Exception o) {
										
									}
							
						}
					}
				
				}	
				
			} catch (Exception issueIV) {
				issueIV.printStackTrace();
			}
			
			
			//if 
			
		 //Check for PolicyHolderDob once more :Go to IV and pull Data
			if (Constants.COMPANY_NAME.equals(partialHeader.getCompany().getName())) {
				
			
				if (!implDto.isPrimary()) {
					if (implDto.getSecPolicyHolderDob()==null) {
						if (claim==null) claim = rcmClaimRepository.findByClaimUuid(claimUuid);
					try {
						Set<String> insTypes = new HashSet<>();
						insTypes.add(claimSubTy);
						if (claimSubTy.equalsIgnoreCase(Constants.insuranceTypePrimary)) {
							insTypes.add("");
							insTypes.add("No Information");
						}
					ivfDto = rcmClaimRepository.getIVIdOfClaimByDos(Constants.SDF_MYSL_DATE.format(implDto.getDos()), implDto.getOfficeUuid(), implDto.getPatientId(),insTypes);
						//Since we don't have any way to pull the Subscriber's DOB from ES using Query, let's pull that from IV	
					java.sql.Date sqlPackageDate
		            = new java.sql.Date(Constants.SDF_MYSL_DATE.parse(ivfDto.getPdob()).getTime());
					claim.setSecPolicyHolderDob(sqlPackageDate);
					implDto.setSecPolicyHolderDob(sqlPackageDate);
					claim.setSecPolicyHolder(ivfDto.getPdName());
					implDto.setSecPolicyHolder(ivfDto.getPdName());
					
					}catch(Exception g) {
						
					}
					}
				}else {
					if (implDto.getPrimePolicyHolderDob()==null)  {
						if (claim==null) claim = rcmClaimRepository.findByClaimUuid(claimUuid);
						Set<String> insTypes = new HashSet<>();
						insTypes.add(claimSubTy);
						if (claimSubTy.equalsIgnoreCase(Constants.insuranceTypePrimary)) {
							insTypes.add("");
							insTypes.add("No Information");
						}
					try {
						ivfDto = rcmClaimRepository.getIVIdOfClaimByDos(Constants.SDF_MYSL_DATE.format(implDto.getDos()), implDto.getOfficeUuid(), implDto.getPatientId(),insTypes);
						//Since we don't have any way to pull the Subscriber's DOB from ES using Query, let's pull that from IV	
						java.sql.Date sqlPackageDate
			            = new java.sql.Date(Constants.SDF_MYSL_DATE.parse(ivfDto.getPdob()).getTime());
						claim.setPrimePolicyHolderDob(sqlPackageDate);
						implDto.setPrimePolicyHolderDob(sqlPackageDate);
						claim.setPrimePolicyHolder(ivfDto.getPdName());
						implDto.setPrimePolicyHolder(ivfDto.getPdName());
						}catch(Exception g) {
							
						}
					}
				}
				
		}
			
			
			
			
			
			//
			implDto.setAllowEdit(false);// Allow Edit only if assigned to login user.
			RcmClaimAssignment assign = rcmClaimAssignmentRepo.findByClaimsClaimUuidAndActive(claimUuid, true);
			if (assign != null) {
				RcmUser assBy = assign.getAssignedBy();
				assBy = userRepo.findByUuid(assBy.getUuid());
				
				implDto.setAssignedByName(assBy.getFirstName() + " " + assBy.getLastName());
				implDto.setAssignedByUuid(assBy.getUuid());
				//implDto.setAssignedByTeam(assign.getCurrentTeamId().getId());
				implDto.setAssignedByRemark(assign.getCommentAssignedBy());
				RcmUser assTo = assign.getAssignedTo();
				if (assTo!=null) {
				assTo = userRepo.findByUuid(assTo.getUuid());
				implDto.setAssignedToName(assTo.getFirstName() + " " + assTo.getLastName());
				implDto.setAssignedToUuid(assTo.getUuid());
				implDto.setAllowEdit(partialHeader.getJwtUser().getUuid().equals(assTo.getUuid()) &&
						assign.getCurrentTeamId().getId()==partialHeader.getTeamId());
				}
				implDto.setAssignedToTeam(assign.getCurrentTeamId().getId());
				
				implDto.setAssignedToTeamName(rcmTeamRepo.findById(assign.getCurrentTeamId().getId()).getName());
				if (assTo!=null) {
					List<String> rls= new ArrayList<>();
					for(RcmUserRole rl:assTo.getRoles()) {
						rls.add(RcmRoleEnum.getRoleFullName(rl.getRole()));
					}
					implDto.setAssignedToRoleName(String.join(", ", rls));
				}
				
			}
			RcmClaimComment comment = rcmClaimCommentRepo.findByClaimsClaimUuid(claimUuid);
			if (comment != null) {
				implDto.setClaimRemarks(comment.getComments());
			}
			
			
			if (implDto != null && implDto.getClaimId().endsWith(ClaimTypeEnum.S.getSuffix())) {
				Object sec = rcmClaimRepository.getClaimsUuidClaimIdPrim(
						implDto.getClaimId().split(ClaimTypeEnum.S.getSuffix())[0] + ClaimTypeEnum.P.getSuffix(),
						implDto.getOfficeUuid());
				if (sec != null) {
					Object s[] = (Object[]) sec;
					implDto.setAssoicatedClaimUuid(s[0].toString());
					implDto.setAssoicatedClaimStatus((boolean) s[1]);
					implDto.setPrimInsurance(s[2]==null?"":s[2].toString());
				}

			} else {
				Object sec = rcmClaimRepository.getClaimsUuidClaimIdSec(
						implDto.getClaimId().split(ClaimTypeEnum.P.getSuffix())[0] + ClaimTypeEnum.S.getSuffix(),
						implDto.getOfficeUuid());
				if (sec != null) {
					Object s[] = (Object[]) sec;
					implDto.setAssoicatedClaimUuid(s[0].toString());
					implDto.setAssoicatedClaimStatus((boolean) s[1]);
					implDto.setSecInsurance(s[2].toString());//For Primary see if we have Primary
				}else {
					implDto.setSecInsurance("N/A");
				}
			}
			
			if (implDto.getClaimType()==null)  implDto.setClaimType("");
			
			if ((implDto.getTreatingProvider()==null || implDto.getClaimType().equals(""))  && !pdf) {
				
			//Provider Sheet
				String treatingProvider="";
				List<ProivderHelpingSheetDto> providers=null;
				String treatingProviderFromClaim="";
				String providerOnClaim="";
				String providerOnClaimFromSheet="";//From mapping sheet
				String specialty="";
				
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				try {
					if (claim==null) claim = rcmClaimRepository.findByClaimUuid(claimUuid);
					String sheetYear = Constants.SDF_SHEET_PROVIDER_DATE_HELPING_YEAR.format(claim.getDos());
			    Object providerSheetData[] = ConnectAndReadSheets.readProviderGSheet(
					Constants.Mapping_Tables, Constants.Mapping_Tables_Provider, CLIENT_SECRET_DIR,
					CREDENTIALS_FOLDER);
			  	
			   List<ProivderHelpingSheetDto> helpingList= ConnectAndReadSheets.readProviderScheduleHelpingGSheet(
					   Constants.Provider_Schedule_SHEET, sheetYear, CLIENT_SECRET_DIR,
					CREDENTIALS_FOLDER);
               String officeName=implDto.getOfficeName();// officeRepo.findById(claim.getOffice().getUuid()).get().getName();
               //String sheetDate = Constants.SDF_SHEET_PROVIDER_DATE.format(implDto.getDos());
               String sheetDate = Constants.SDF_SHEET_PROVIDER_DATE_HELPING.format(implDto.getDos());
               //String doc1FromProvider = doc1NameMap.get(officeName + "->" + sheetDate);
               if (Constants.COMPANY_NAME.equalsIgnoreCase(implDto.getClientName())) {
            	   providers = getDocsFromHelpingData(helpingList,sheetDate,officeName,implDto.getClientName());
               }else {
            	   providers=new ArrayList<>();
            	   ProivderHelpingSheetDto tempHelp= new ProivderHelpingSheetDto(
            			   implDto.getClientName(), sheetDate,  implDto.getOfficeName(),  implDto.getTreatingProviderFromSheet(),  "Doc - 1");
            	   providers.add(tempHelp);
            	   //tempHelp.setClientName(implDto.getClientName());
            	   //tempHelp.setDate(date);
            	   //tempHelp.setOfficeName(officeName);
            	   //tempHelp.setTreatingProvider(implDto.getTreatingProviderFromSheet());
            	   //tempHelp.setType("Doc - 1");
            	   //providers =tempHelp;// implDto.getTreatingProviderFromSheet();
               }
               
               //final String  treatingProviderF=treatingProvider;
               List<ProviderCodeWithOffice> pro = (List<ProviderCodeWithOffice>) providerSheetData[1];
   			   
               final String proIdFinal=implDto.getProviderId();
               final String clientName=implDto.getClientName();
               List<ProviderCodeWithOffice> pCodeList = pro.stream()
   					.filter(e -> e.getOffice().trim().equalsIgnoreCase(officeName)
   							&&  e.getClientName().trim().equalsIgnoreCase(clientName)
   							&& e.getEsCode().trim().equalsIgnoreCase(proIdFinal))
   					.collect(Collectors.toList());
               
   			if (pCodeList != null && pCodeList.size() > 0) {
   				treatingProviderFromClaim = pCodeList.get(0).getProviderCode();
			 	
   			 List<ProviderCodeWithSpecialty> proEs = (List<ProviderCodeWithSpecialty>) providerSheetData[0];
   			 List<ProviderCodeWithSpecialty> proEsF= proEs.stream().filter(p -> 
   			               p.getProviderCode().equalsIgnoreCase(pCodeList.get(0).getProviderCode())
   			               &&  p.getClientName().equalsIgnoreCase(clientName)
   					        ).collect(Collectors.toList());
   			 if (proEsF!=null && proEsF.size()>0) {
   				providerOnClaim =proEsF.get(0).getProviderNames();
   				specialty = proEsF.get(0).getSpecialty();
   			 }
   				
   			}
   			
   			List<ProviderCodeWithSpecialty> proEs = (List<ProviderCodeWithSpecialty>) providerSheetData[0];
   			for(ProivderHelpingSheetDto provider: providers) {
   				final String  treatingProviderF=provider.getTreatingProvider();
   				treatingProvider=treatingProvider+Constants.ProviderJoinCons+provider.getTreatingProvider();
   				List<ProviderCodeWithSpecialty> proEsF= proEs.stream().filter(p -> 
   				                   p.getProviderCode().equalsIgnoreCase(treatingProviderF) &&
   				                   p.getClientName().equalsIgnoreCase(clientName)
   				                   )
   	   					.collect(Collectors.toList());
   	   			if (proEsF!=null && proEsF.size()>0) {
   	   				providerOnClaimFromSheet =providerOnClaimFromSheet+Constants.ProviderJoinCons+ proEsF.get(0).getProviderNames();
   	   			}
   				
   			}
   			
   			//if (!sheetProvider.equals("") ||  !claimProvider.equals("")) {
   				implDto.setTreatingProvider(treatingProvider.replaceFirst(Constants.ProviderJoinCons, ""));
   				implDto.setTreatingProviderFromClaim(treatingProviderFromClaim);
   				implDto.setClaimType(specialty);
   				implDto.setProviderOnClaim(providerOnClaim);
   				implDto.setProviderOnClaimFromSheet(providerOnClaimFromSheet.replaceFirst(Constants.ProviderJoinCons, ""));
   				
   				if (claim ==null) claim= rcmClaimRepository.findByClaimUuid(claimUuid);
   				claim.setTreatingProvider(treatingProvider);
   				claim.setTreatingProviderFromClaim(treatingProviderFromClaim);
   				claim.setClaimType(specialty);
   				claim.setProviderOnClaim(providerOnClaim);
   				claim.setProviderOnClaimFromSheet(providerOnClaimFromSheet);
   			///}
				}catch(Exception c) {
					
				}
			}
			if (claim!=null  && !pdf) {
				if (!dto.getAutoRuleRun()) {
					AutoRunClaimReponseDto autoResponse = runAutomatedRules(claim, partialHeader, claimUuid,false,true);
					implDto.setAssignmentOfBenefits(autoResponse.getAssignmentOfBenefits());
					claim.setAssignmentOfBenefits(autoResponse.getAssignmentOfBenefits());
					claim.setPreferredModeOfSubmission(autoResponse.getPreferredModeOfSubmission());
					implDto.setPreferredModeOfSubmission(autoResponse.getPreferredModeOfSubmission());
				}
				rcmClaimRepository.save(claim);
			}
			
			// Run Auto Rules
			//runAutomatedRules(jwtUser, claimUuid);//not from here 
		
		} else {
			// Wrong claimId;
		}
		//if (implDto.getSecInsurance()==null) implDto.setSecInsurance("N/A");
		if(implDto != null) {
			implDto.setNextTeam(NextTeamClaimTransferUtil.getNextTeam(implDto.getAssignedToTeam()));
		}
		
		return implDto;

	}

	
	@Transactional(rollbackFor = Exception.class)
	public List<RcmClaimDetail> pullAndSaveTpDataFromRE(RcmClaims claim,String claimuuid,String tpId,String  offUuid,PartialHeader partialHeader) {

		//Delete if record exists.
		if (claim==null) {
			claim = rcmClaimRepository.findByClaimUuid(claimuuid);
		}
		if (!claim.isPending() && claim.getCurrentStatus()==0){
			//Already Submitted
			return null;
		}
		rcmTPDetailRepo.deleteByClaimId(claim.getClaimUuid());
		List<ClaimDetailDto> cdList = ruleEngineService.pullTPDetailFromFromRE(tpId, partialHeader.getCompany().getUuid(),
				offUuid);
		List<RcmClaimDetail> tpList= new ArrayList<>();
		if (cdList!=null && cdList.size()>0) {
			
			
			RcmTPDetail det= null;
			RcmClaimDetail clDet= null;
			for(ClaimDetailDto d:cdList) {
				det= new RcmTPDetail();
				clDet= new RcmClaimDetail();
				BeanUtils.copyProperties(d, det);
				BeanUtils.copyProperties(d, clDet);
				det.setClaim(claim);
				clDet.setClaim(claim);
				tpList.add(clDet);
				rcmTPDetailRepo.save(det);
			}
			
			
		}
		
		return tpList;
	}
	
	
	@Transactional(rollbackFor = Exception.class)
	public ClaimSubDet  removeIvIdAndTpId(RcmIVfDto dto,PartialHeader partialHeader) {
		
        RcmClaims claim = rcmClaimRepository.findByClaimUuid(dto.getClaimUuid());
        ClaimSubDet det = new ClaimSubDet();
		det.setSuccess(false);
		if (!claim.isPending()){
			return det;
		}
		
		if (dto.getRemoveIv()!=null) {
			 claim.setIvfId("-O-");
			 det.setIvfId("");
			 claim.setIvDos("");
			 det.setIvDos("");
			 claim.setSsn("");
			 det.setSsn("");
			   
		}
       if (dto.getRemoveTp()!=null) {
    	   claim.setTpDos("");
    	   claim.setTpId("-O-");
    	   det.setTpDos(claim.getTpDos());
		   det.setTpId("");
		   rcmTPDetailRepo.deleteByClaimId(claim.getClaimUuid());
		} 
        rcmClaimRepository.save(claim);
        det.setSuccess(true);
		return det;
		
	}
	@Transactional(rollbackFor = Exception.class)
	public ClaimSubDet updateAutoIvIdAndTpId(RcmIVfDto dto,PartialHeader partialHeader) {

		RcmClaims claim = rcmClaimRepository.findByClaimUuid(dto.getClaimUuid());
		
		ClaimSubDet det = new ClaimSubDet();
		det.setSuccess(false);
		if (!claim.isPending() && claim.getCurrentStatus()==0){
			//Already Submitted
			return det;
		}
		if (dto.getIvfId() !=null && !dto.getIvfId().equals("")) {
			det.setSuccess(true);
		   claim.setIvfId(dto.getIvfId());
		   det.setIvfId(dto.getIvfId());
		
			IVFDto ivfDto = rcmClaimRepository.getIVDosByIvId(dto.getIvfId(), claim.getOffice().getUuid(), claim.getPatientId());
			
			if (ivfDto!=null) {
				det.setIvDos(ivfDto.getDos());
				claim.setIvDos(ivfDto.getDos());
				claim.setSsn(ivfDto.getSsn());
				det.setSsn(ivfDto.getSsn());
			}
		}else {
			det.setSuccess(true);
			dto.setRemoveIv(true);
			removeIvIdAndTpId(dto,partialHeader);
			 claim.setIvfId("-O-");
			 det.setIvfId("");
			 claim.setIvDos("");
			 det.setIvDos("");
			 claim.setSsn("");
			 det.setSsn("");
		}
		//Fetch Tpid Date based on New Tpid
		if (dto.getTpId() !=null && !dto.getTpId().equals("")) {
			
		
		Object tp  = rcmClaimRepository.getTPIdByTpid(claim.getOffice().getUuid(),
				claim.getPatientId(), dto.getTpId());
		det.setSuccess(true);
		//String tpId = "";
		String tpDos = "";
		claim.setTpId(dto.getTpId());
		det.setTpId(dto.getTpId());
		claim.setTpDos(tpDos);
		if (tp != null) {
			Object[] tpDet = (Object[]) tp;
			if (tpDet != null && tpDet.length == 2) {
				//tpId = (String) tpDet[0];
				tpDos = (String) tpDet[1];
				claim.setTpId(dto.getTpId());
				claim.setTpDos(tpDos);
				det.setTpDos(tpDos);
				//det.setTpId(dto.getTpId());
				
			}
		}
	   }else {
		   det.setSuccess(true);
		   dto.setRemoveTp(true);
		   claim.setTpDos("");
    	   claim.setTpId("-O-");
    	   det.setTpDos(claim.getTpDos());
		   det.setTpId("");
		   rcmTPDetailRepo.deleteByClaimId(claim.getClaimUuid());
		   
	   }
		if (dto.getTpId()!=null && !dto.getTpId().equals("")) {
			pullAndSaveTpDataFromRE(claim, dto.getClaimUuid(), dto.getTpId(), claim.getOffice().getUuid(), partialHeader);
		}
		
		rcmClaimRepository.save(claim);
		return det;
	}
	
	
	
	
	@Transactional(rollbackFor = Exception.class)
	public ServiceValidationDataDto readServiceValidationFromGSheet(RcmClaims claim,String claimUuid,PartialHeader partialHeader,boolean deleteOld) {

		//Rule Engine up and running is needed
        boolean validateClaimRight=checkifCompanyIdMatchesList(partialHeader.getJwtUser().getUuid(),partialHeader.getCompany().getUuid());
		
		if (!validateClaimRight) {
			return null;
		}
		ServiceValidationDataDto dto = new ServiceValidationDataDto();
		List<RcmClaimDetailViewDto> details=new ArrayList<>();
		List<RcmClaimsServiceRuleValidationDto> list = new ArrayList<>();
		RcmClaimsServiceRuleValidationDto one = null;
		if (claim == null) claim = rcmClaimRepository.findByClaimUuid(claimUuid);
		RcmOffice off = claim.getOffice();
		List<RcmClaimDetail> cddList= new ArrayList<>();
		RcmInsuranceType insurancetype=rcmInsuranceTypeRepo.findById(claim.getRcmInsuranceType().getId());
		//RcmCompany rcmCompany = rcmCommonServiceImpl.getCompanyFormParitalHeaderCompanyId(officeRepo.findByUuid(off.getUuid()).getCompany().getUuid(), partialHeader.getCompany());
		Date claimDos=null;
		if (claim!=null) claimDos= claim.getDos();
		if (validateClaimRight) {
			if (claim.isPending()) {

			
			String[] clT = claim.getClaimId().split("_");
			
			long ct= rcmClaimDetailRepo.countByClaimClaimUuid(claimUuid);
			
			if (ct==0) {
				//Pull && Save Data
				//clT[0] = "2115";// For testing
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
				cddList.forEach( det ->{
					RcmClaimDetailViewDto view = new RcmClaimDetailViewDto();
					BeanUtils.copyProperties(det, view); 
					details.add(view);
				});
				dto.setDetails(details);
			}else {
				dto.setClaimFound(true);
				cddList = rcmClaimDetailRepo.findByClaimClaimUuid(claimUuid);
				/*RcmClaimDetail cd= cddList.get(0);
				claim.setDateLastUpdatedES(cd.getDateLastUpdated());
				claim.setDescriptionES(cd.getDescription());
				claim.setEstSecondaryES(cd.getEstSecondary());
				claim.setStatusES(cd.getStatus();*/
				dto.setEsDate(claim.getDateLastUpdatedES());
				cddList.forEach( det ->{
					RcmClaimDetailViewDto view = new RcmClaimDetailViewDto();
					BeanUtils.copyProperties(det, view); 
					details.add(view);
				});
				dto.setDetails(details);
			}
			
			
			
			}else {
				
				cddList = rcmClaimDetailRepo.findByClaimClaimUuid(claimUuid);
				dto.setClaimFound(true);
				dto.setEsDate(claim.getDateLastUpdatedES());
				cddList.forEach( det ->{
					RcmClaimDetailViewDto view = new RcmClaimDetailViewDto();
					BeanUtils.copyProperties(det, view); 
					details.add(view);
				});
				dto.setDetails(details);
			}
			

			
			
			boolean pullSuccess = false;
            Set<String> claimCodes=new HashSet<>();
            if (deleteOld) {
            	rcmClaimsServiceRuleValidationRepo.markOldClaimDataForDeletion(claim.getClaimUuid());
            }
			List<RcmClaimsServiceRuleValidation> serviceData = rcmClaimsServiceRuleValidationRepo
					.findByClaimClaimUuid(claimUuid);
			// RcmTeamEnum.BILLING.getId()
			if (deleteOld || (serviceData != null && serviceData.size() == 0)) {

				HashMap<String, List<ClaimServiceValidationGSheetData>> sheetServiceData = readServiceValidationFromGSheet();
				if (sheetServiceData != null) {
					// Save Data in Table First Time.
					RcmClaimsServiceRuleValidation v = null;
					for (Map.Entry<String, List<ClaimServiceValidationGSheetData>> entry : sheetServiceData.entrySet()) {
						
						logger.info("Key = " + entry.getKey() + ", Value = " + entry.getValue());
						if (entry.getKey().equals("D0431")) {
							
							System.out.println("D0431");
							
						}
						//for (ClaimServiceValidationGSheet d : entry.getValue()) {
							for (ClaimServiceValidationGSheetData qq : entry.getValue()) {
								String codeFromES = "";
								List<RcmClaimDetail> cdFilter = cddList.stream()
										.filter(p -> entry.getKey().equals(p.getServiceCode()))
										.collect(Collectors.toList());
								if (cdFilter != null && cdFilter.size() > 0) {
									codeFromES = cdFilter.get(0).getServiceCode();
								}
								logger.info("codeFromES = " + codeFromES);
								if (!codeFromES.equals(entry.getKey()))
									continue;
								if (!qq.getValue().trim().equalsIgnoreCase("Required"))
									continue;
								/*if (qq.getValue().trim().equalsIgnoreCase("Rule Engine Check"))
									continue;
								if (qq.getValue().trim()
										.equalsIgnoreCase("As per code sheet -This is not a valid CDT any longer."))
									continue;
							    */		
								if (insurancetype==null) {
									continue;
								}
								claimCodes.add(entry.getKey());
								if (!isInsuranceCodeMatch(insurancetype.getCode(), qq.getInsuranceTypes()))  continue;
								//logger.info("HEAD: " + qq.getHeading() + "   VALUE: " + qq.getValue());
								v = new RcmClaimsServiceRuleValidation();
								v.setActive(true);
								v.setDescription(qq.getDescription());
								v.setClaim(claim);
								v.setName(qq.getNameOfService());
								v.setServiceCode(entry.getKey());
								v.setInsuranceTypes(qq.getInsuranceTypes());
								v.setManualAuto(qq.getAutoOrManual());
								v.setDisplayValues(qq.getValues());
								v.setValue(qq.getValue());
								one = new RcmClaimsServiceRuleValidationDto();
								if (qq.getAutoOrManual().equals("Automated")) {
									if (qq.getNameOfService().equals(RuleConstants.RULE_ID_307)) {
									v.setRule(rcmRuleRepo.findById(307));
									one.setRuleId(307);
									}else if (qq.getNameOfService().equals(RuleConstants.RULE_ID_308)) {
										v.setRule(rcmRuleRepo.findById(308));
										one.setRuleId(308);
									}else if (qq.getNameOfService().equals(RuleConstants.RULE_ID_309)) {
										v.setRule(rcmRuleRepo.findById(309));
										one.setRuleId(309);
									}else if (qq.getNameOfService().equals(RuleConstants.RULE_ID_310)) {
										v.setRule(rcmRuleRepo.findById(310));
										one.setRuleId(310);
									}
									
								}else {
									one.setRuleId(-1);
								}
								
								RcmClaimsServiceRuleValidation oldVal =null;
								String uu =null;
								if (deleteOld) {
								    oldVal = rcmClaimsServiceRuleValidationRepo.
								    findByClaimClaimUuidAndNameAndServiceCode(claimUuid, qq.getNameOfService(), entry.getKey());
								  if (oldVal==null) {
									 oldVal = rcmClaimsServiceRuleValidationRepo.save(v);
									uu =  oldVal.getRemarkUuid();
								  }else {
									 uu =  oldVal.getRemarkUuid();
									 oldVal.setMarkDeleted(false);
									 rcmClaimsServiceRuleValidationRepo.save(oldVal);
								  }
								} else {
									oldVal = rcmClaimsServiceRuleValidationRepo.save(v);
									uu =  oldVal.getRemarkUuid();
								}
								
								//String uu = rcmClaimsServiceRuleValidationRepo.save(v).getRemarkUuid();
								
								one.setDescription(qq.getDescription());
								one.setRemarkUuid(uu);
								one.setServiceCode(entry.getKey());
								one.setValue(qq.getValue());
								one.setManualAuto(qq.getAutoOrManual());
								
								one.setDescription(qq.getDescription());
								one.setDisplayValues(qq.getValues());
								one.setInsuranceTypes(qq.getInsuranceTypes());
								
								one.setName(qq.getNameOfService());
								one.setRemark(oldVal.getRemark());
								one.setAnswer(oldVal.getAnswer());
								
								list.add(one);
								//Run The Rule
								
							}
						//}

						pullSuccess = true;
						
						
						
						
					}
					RcmUser user = userRepo.findByUuid(partialHeader.getJwtUser().getUuid());
					List<String> types = Arrays
							.asList(new String[] { Constants.RULE_TYPE_GSHEET })
							.stream().collect(Collectors.toList());
					List<RcmRules> rules = rcmRuleRepo.findByRuleTypeInAndActiveAndManualAuto(types, 1,
							Constants.RULE_TYPE_AUTO);
					List<TPValidationResponseDto> allLIst = new ArrayList<>();
                    //Read Sheet
					List<CredentialDataAnesthesia> sheetData= readCredentialTrackerAnesthesiaGSheet();
					RcmRules rule = getRulesFromList(rules, RuleConstants.RULE_ID_307);
					Object insCodesObj= rcmClaimRepository.fetchInsuranceCodeOfClaim(claim.getClaimUuid());
					Object[] insCodes = (Object[]) insCodesObj;
					String[] clT = claim.getClaimId().split("_");
					String insCode="";
					try {
					if (("_" + clT[1]).equals(ClaimTypeEnum.P.getSuffix())) {
						insCode =insCodes[0].toString();
					}else {
						insCode = insCodes[1].toString();
					}
					}catch(Exception xx) {
						xx.printStackTrace();
					}
					
					allLIst.addAll(ruleBookService.rule307(rule, cddList));
					
					rule = getRulesFromList(rules, RuleConstants.RULE_ID_308);
					allLIst.addAll(ruleBookService.rule308(rule,cddList , claim.getTreatingProviderFromClaim(),sheetData,claimDos,insCode));
					
				    rule = getRulesFromList(rules, RuleConstants.RULE_ID_309);
					allLIst.addAll(ruleBookService.rule309(rule,cddList, claim.getTreatingProviderFromClaim(),sheetData));
					
					rule = getRulesFromList(rules, RuleConstants.RULE_ID_310);
					allLIst.addAll(ruleBookService.rule310(rule,claimCodes, claim.getTreatingProviderFromClaim(),sheetData));
					
					//if (deleteOld) {
					//	rcmClaimRuleValidationRepo.deleteByClaimId(claim.getClaimUuid());
				   //}
					saveAutoRuleReport(allLIst, user, claim, rules,partialHeader);
					if (pullSuccess) {
						claim.setPulledClaimsServiceDataFromEs(pullSuccess);
						rcmClaimRepository.save(claim);
					}
				}
				
				rcmClaimsServiceRuleValidationRepo.deleteByClaimIdMarkeForDeletion(claim.getClaimUuid());
			} else {
				for (RcmClaimsServiceRuleValidation s : serviceData) {
					one = new RcmClaimsServiceRuleValidationDto();
					one.setDescription(s.getDescription());
					one.setRemarkUuid(s.getRemarkUuid());
					one.setServiceCode(s.getServiceCode());
					one.setValue(s.getValue());
					one.setName(s.getName());
					one.setMessageType(s.getMessageType());
					one.setManualAuto(s.getManualAuto());
					one.setAnswer(s.getAnswer());
					if (s.getRule()!=null)one.setRuleId(s.getRule().getId());
					one.setInsuranceTypes(s.getInsuranceTypes());
					one.setDisplayValues(s.getDisplayValues());
					one.setRemark(s.getRemark());
					list.add(one);
				}

				
			}

		    
			
		} else {
			logger.error("Wrong Client");
		}
		
		dto.setDto(list);
		return dto;
	}

	public HashMap<String, List<ClaimServiceValidationGSheetData>> readServiceValidationFromGSheet() {

		HashMap<String, List<ClaimServiceValidationGSheetData>> data = null;
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
	
	private  List<CredentialDataAnesthesia> readCredentialTrackerAnesthesiaGSheet() {

		List<CredentialDataAnesthesia> data = null;
		try {
			data = ConnectAndReadSheets.readCredentialTrackerAnesthesiaGSheet("1QPfa8aEA5pcSWhGJQSX9R7CC-ZxVA-8_nvagjGAA4s4",
					"Anesthesia and First Dental Home", CLIENT_SECRET_DIR, CREDENTIALS_FOLDER);
		} catch (Exception v) {
			v.printStackTrace();
			logger.error("Error in Anesthesia and First Dental Home");
			logger.error(v.getMessage());
		}
		return data;
	}

	public List<ProductionDto> claimsProductionReportByTeam(ClaimProductionLogDto dto,PartialHeader partialHeader) {

		List<String> companies = findAssociatedCompanyIdByUserUuid(partialHeader);
		if (partialHeader.getTeamId() == RcmTeamEnum.BILLING.getId() ) {
			if (partialHeader.getRole().equals(Constants.ASSOCIATE)) {
				return rcmClaimRepository.claimProductionByForBillingAssoicate(companies, partialHeader.getTeamId(), dto.getStartDate(),
						dto.getEndDate(),partialHeader.getJwtUser().getUuid());	
				}else {
				return rcmClaimRepository.claimProductionByForBilling(companies, partialHeader.getTeamId(), dto.getStartDate(),
						dto.getEndDate());
				}
		}else if (partialHeader.getTeamId() == RcmTeamEnum.INTERNAL_AUDIT.getId() ) {
			if (partialHeader.getRole().equals(Constants.ASSOCIATE)) {
				return rcmClaimRepository.claimProductionForInternalAuditAssoicate(companies, partialHeader.getTeamId(), dto.getStartDate(),
						dto.getEndDate(),partialHeader.getJwtUser().getUuid());
			}else {
					return rcmClaimRepository.claimProductionForInternalAudit(companies, partialHeader.getTeamId(), dto.getStartDate(),
					dto.getEndDate());
			}
		}else  {
			if (partialHeader.getRole().equals(Constants.ASSOCIATE)) {
				return rcmClaimRepository.claimProductionForOtherTeamAssoicate(companies, partialHeader.getTeamId(), dto.getStartDate(),
						dto.getEndDate(),partialHeader.getJwtUser().getUuid());
			}else {
					return rcmClaimRepository.claimProductionForOtherTeam(companies, partialHeader.getTeamId(), dto.getStartDate(),
					dto.getEndDate());
			}
		}
		//return null;

	}

	/**
	 * get all issue in Claims That are fetched from Source
	 * 
	 * @param companyId
	 * @return
	 */
	public List<com.tricon.rcm.dto.IssueClaimDto> getIssueClaims(String companyId) {
		List<com.tricon.rcm.dto.IssueClaimDto> issueDto = null;
		List<IssueClaimDto> data = rcmClaimRepository.getIssueClaims(companyId);
		if (!data.isEmpty()) {
			issueDto = new ArrayList<>();
			data.stream().map(x -> new com.tricon.rcm.dto.IssueClaimDto(x.getClaimId(), x.getIssue(), x.getSource(),
					x.getOfficeName(), x.getCreatedDate(), x.getId(), x.getIsArchive())).forEach(issueDto::add);
		}
		return issueDto;

	}

	public CaplineIVFFormDto getIvfDataFromRE(String companyId, String claimuuid) {

		RcmClaims claims = rcmClaimRepository.findByClaimUuid(claimuuid);

		if (claims != null) {
			RcmOffice off = claims.getOffice();
		//	String[] clT = claims.getClaimId().split("_");
			//String claimSubTy = Constants.insuranceTypeSecondary;// May be needed latter
//			Set<String> types= new HashSet<>();
//			if (("_" + clT[1]).equals(ClaimTypeEnum.P.getSuffix())) {
//				types.add("Primary");
//				types.add("No Information");
//				types.add("");
//				
//			} else {
//				types.add("Secondary");
//				
//			}
			
			//IVFDto iVFDto = rcmClaimRepository.getIVIdOfClaimByDos(Constants.SDF_MYSL_DATE.format(claims.getDos()), off.getUuid(), claims.getPatientId(),types);
			//IVFDto iVFDto = rcmClaimRepository.getLatestIvfNumberForClaim(off.getUuid(), claims.getPatientId(),
			//		clT[0],types);
			if (claims.getIvfId() != null) {
				return ruleEngineService.pullIVFDataFromRE(claims.getIvfId(), claims.getPatientId(), companyId,
						off.getUuid());
			} else {
				// For testing..
				//return ruleEngineService.pullIVFDataFromRE("196041", "7431", companyId,
				//		"f015515d-7df2-11e8-8432-8c16451459cd");
			}
		} else {
			// For testing..
			//return ruleEngineService.pullIVFDataFromRE("196041", "7431", companyId,
			//		"f015515d-7df2-11e8-8432-8c16451459cd");
		}
		return null;
	}

	public List<ClaimRemarksDto> fetchClaimRemarksOtherTeam(String companyId, String claimuuid, int teamId) {

		List<ClaimRemarksDto> list = rcmClaimAssignmentRepo.fetchClaimRemarksOtherTeam(claimuuid);

		return list;
	}

	public List<ClaimRuleRemarksDto> fetchClaimRuleRemark(PartialHeader partialHeader, String claimuuid) {


       boolean validateClaimRight=checkifCompanyIdMatchesList(partialHeader.getJwtUser().getUuid(),partialHeader.getCompany().getUuid());
		
		if (!validateClaimRight) {
			return null;
		}
		
		List<ClaimRuleRemarksDto> list = null;
		//RcmClaims claim = rcmClaimRepository.findByClaimUuid(claimuuid);
		//RcmCompany rcmCompany = rcmCommonServiceImpl.getCompanyFormParitalHeaderCompanyId(officeRepo.findByUuid(claim.getOffice().getUuid()).getCompany().getUuid(), partialHeader.getCompany());
		if (validateClaimRight) {
			list = rcmClaimRuleRemarkRepo.fetchClaimRuleRemarksAnyTeam(claimuuid);
		} else {
			logger.error("Wrong Client");
		}
		return list;
	}

	public String saveClaimRemark(PartialHeader partialHeader, ClaimRemarkDto dto) {

		boolean validateClaimRight=checkifCompanyIdMatchesList(partialHeader.getJwtUser().getUuid(),partialHeader.getCompany().getUuid());
			
			if (!validateClaimRight) {
				return null;
		}
		RcmUser user = userRepo.findByUuid(partialHeader.getJwtUser().getUuid());
		RcmClaims claim = rcmClaimRepository.findByClaimUuid(dto.getClaimUuid());
		//RcmCompany rcmCompany = rcmCommonServiceImpl.getCompanyFormParitalHeaderCompanyId(officeRepo.findByUuid(claim.getOffice().getUuid()).getCompany().getUuid(), partialHeader.getCompany());
		if (validateClaimRight) {
			if (!claim.isPending() && claim.getCurrentStatus()==0)
				return "Claim Already Submitted";
			return saveClaimRemark(dto.getRemark(), claim, user, partialHeader);
		}

		else
			return "wrong Client Name";

	}

	private String saveClaimRemark(String remark, RcmClaims claim, RcmUser user, PartialHeader partialHeader) {

		if (remark != null && !remark.trim().equals("")) {

			RcmClaimComment comment = rcmClaimCommentRepo.findByCommentedByUuidAndClaimsClaimUuid(partialHeader.getJwtUser().getUuid(),
					claim.getClaimUuid());
			if (comment == null) {

				comment = new RcmClaimComment();
				comment.setCommentedBy(user);
				comment.setCreatedBy(user);
				comment.setClaims(claim);
				comment.setComments(remark);
				comment.setActive(true);
				comment.setTeamId(rcmTeamRepo.findById(partialHeader.getTeamId()));
			} else {
				comment.setComments(remark);
			}

			return rcmClaimCommentRepo.save(comment).getUuid();
		} else {
			return "";
		}

	}

	public String saveClaimRuleRemark(PartialHeader partialHeader, ClaimRuleRemarkDto dto) {
      
		//RcmUser user = userRepo.findByUuid(partialHeader.getJwtUser().getUuid());
		RcmClaims claim = rcmClaimRepository.findByClaimUuid(dto.getClaimUuid());
		RcmCompany rcmCompany = rcmCommonServiceImpl.getCompanyFormParitalHeaderCompanyId(officeRepo.findByUuid(claim.getOffice().getUuid()).getCompany().getUuid(), partialHeader.getCompany());
		if (rcmCompany!=null) {
			if (!claim.isPending() && claim.getCurrentStatus()==0)
				return "Claim Already Submitted";
			//saveClaimRuleRemark(dto.getData(), user, claim, partialHeader);

			return "Remarks Saved";
		}

		else
			return "wrong Client Name";

	}

	private void saveClaimRuleRemark(List<RuleRemarkDto> data, RcmUser user, RcmClaims claim, PartialHeader partialHeader) {
		//for(RuleRemarkDto s :data) {
		data.forEach(s -> {
           
			RcmRules rule = rcmRuleRepo.findById(s.getRuleId());
			
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
			remark.setTeamId(rcmTeamRepo.findById(partialHeader.getTeamId()));
			rcmClaimRuleRemarkRepo.save(remark);
			
		});
	}

	private void saveClaimRuleManualAutoMessageType(List<RuleRemarkDto> data, RcmUser user, RcmClaims claim, PartialHeader partialHeader) {
		data.forEach(s -> {

			
			RcmRules rule = rcmRuleRepo.findById(s.getRuleId());
			//if (s.getRemark()==null)  return ;
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
			val.setTeamId(rcmTeamRepo.findById(partialHeader.getTeamId()));
			rcmClaimRuleValidationRepo.save(val);
		});
	}
	private void saveClaimServiceCodeValidation(List<ClaimServiceDto> data, RcmUser user, RcmClaims claim,
			PartialHeader partialHeader) {
		data.forEach(s -> {

			RcmClaimsServiceRuleValidation val = rcmClaimsServiceRuleValidationRepo
					.findByClaimClaimUuidAndRemarkUuid(claim.getClaimUuid(), s.getRemarkUuid());

			val.setUpdatedBy(user);
			val.setUpdatedDate(new Date());
			val.setRemark(s.getRemark());
			val.setMessageType(s.getMessageType());
			val.setAnswer(s.getAnswer());
			val.setActive(true);
			rcmClaimsServiceRuleValidationRepo.save(val);
		});
	}

	public List<RuleEngineClaimDto> getRuleEngineClaimReport(String officeId, PartialHeader partialHeader, String patientId,
			String claimId) {

		return rcmClaimRepository.getRuleEngineClaimReport(officeId, partialHeader.getCompany().getUuid(), patientId, claimId);
	}

	public RcmClaimSubmissionDto fetchClaimSubmissionDetails(PartialHeader partialHeader, String claimuuid) {

		RcmClaimSubmissionDto d = rcmClaimSubmissionDetailsRepo.findByClaimUuidAndCompanyId(claimuuid,
				partialHeader.getCompany().getUuid());

		return d;
	}

	public String saveClaimSubmissionDetails(PartialHeader partialHeader, ClaimSubmissionDto dto) {

		RcmUser user = userRepo.findByUuid(partialHeader.getJwtUser().getUuid());
		RcmClaims claim = rcmClaimRepository.findByClaimUuid(dto.getClaimUuid());

		RcmCompany rcmCompany = rcmCommonServiceImpl.getCompanyFormParitalHeaderCompanyId(officeRepo.findByUuid(claim.getOffice().getUuid()).getCompany().getUuid(), partialHeader.getCompany());
		if (rcmCompany!=null) {
			if (!claim.isPending() && claim.getCurrentStatus()==0)
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
		det.setPreauthNo(dto.getPreauthNo());
		det.setChannel(dto.getChannel());
		det.setClaimNumber(dto.getClaimNumber());
		det.setProviderRefNo(dto.getProviderRefNo());
		det.setRefferalLetter(dto.isRefferalLetter());
		det.setAttachmentSend(dto.isAttachmentSend());
		det.setEsTime(dto.getEsTime());

		return rcmClaimSubmissionDetailsRepo.save(det).getId();
	}

	public List<KeyValueDto> fetchClaimNotes(PartialHeader partialHeader, String claimuuid) {

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
	
	@Transactional
	public String  assignClaimToTL(PartialHeader partialHeader, ClaimAssignDto dto,int teamId) {

		boolean validateClaimRight=checkifCompanyIdMatchesList(partialHeader.getJwtUser().getUuid(),partialHeader.getCompany().getUuid());
		
		if (!validateClaimRight) {
			return null;
	     }
		RcmClaims claim = rcmClaimRepository.findByClaimUuid(dto.getClaimUuid());

		//RcmCompany rcmCompany = rcmCommonServiceImpl.getCompanyFormParitalHeaderCompanyId(officeRepo.findByUuid(claim.getOffice().getUuid()).getCompany().getUuid(), partialHeader.getCompany());
		if (validateClaimRight) {
			if (!claim.isPending() && claim.getCurrentStatus()==0)
				return "Claim Already Submitted";
			if (claim.getCurrentState() ==Constants.CLAIM_ARCHIVE_PREFIX_CANNOT_SUBMITED)
				return "Claim is Archived";
			//Assign Logic
			
			RcmUser rcmLeadUser = userRepo.findByUuid(dto.getTeamLeadUuid());
			boolean isLead =rcmCommonServiceImpl.isTeamLead(rcmLeadUser.getRoles());
			RcmUser user = userRepo.findByUuid(partialHeader.getJwtUser().getUuid());
			if (isLead) {
			//check if assign to Other team. ..Let him work
			int asisgnCount=rcmClaimAssignmentRepo.claimAssignedToSomeoneAlreadyofOtherTeam(dto.getClaimUuid(),partialHeader.getTeamId());
			if (asisgnCount>0)
				return "Claim Already Assigned To Other Team";
			//Delete OLd Data... NO deletion required now
			/*
			int oldWorkCount=rcmClaimAssignmentRepo.claimWorkedBySomeEarlierByTLTeam(dto.getClaimUuid(),partialHeader.getTeamId());	
			if (oldWorkCount==0 || partialHeader.getTeamId()==RcmTeamEnum.BILLING.getId()) {
				//int del=0;
				///only Billing user can delete as they only write the data.
				/*if (partialHeader.getTeamId()==RcmTeamEnum.BILLING.getId()) {
					
				del=rcmClaimSubmissionDetailsRepo.deleteByClaimId(dto.getClaimUuid());
				del=del+rcmClaimsServiceRuleValidationRepo.deleteByClaimId(dto.getClaimUuid());
				del=del+rcmClaimRuleRemarkRepo.deleteByClaimId(dto.getClaimUuid());
				del=del+rcmClaimRuleValidationRepo.deleteByClaimId(dto.getClaimUuid());
				del=del+rcmClaimCommentRepo.deleteByClaimId(dto.getClaimUuid());
				del=del+rcmClaimNotesRepo.deleteByClaimId(dto.getClaimUuid());
				}
				//if Already Assigned to Any One on Same Team then Take back
				RcmClaimAssignment assign = rcmClaimAssignmentRepo
						.findByClaimsClaimUuidAndActiveAndCurrentTeamIdId(claim.getClaimUuid(), true,teamId);
				if (assign!=null) {
					assign.setActive(false);
					assign.setUpdatedBy(user);
					//assign.setCommentAssignedTo(dto.getRemark());
					assign.setCommentAssignedTo("Assign back to TL");
					assign.setSystemComment(assign.getCommentAssignedTo());
					assign.setUpdatedDate(new Date());
					assign.setTakenBack(true);
					rcmClaimAssignmentRepo.save(assign);
				}
			}else {
				RcmClaimAssignment assign = rcmClaimAssignmentRepo.findByClaimsClaimUuidAndActive(claim.getClaimUuid(),true);
				if (assign!=null) {
					assign.setActive(false);
					rcmClaimAssignmentRepo.save(assign);
				}
			}
			*/
			
			RcmClaimAssignment assign = rcmClaimAssignmentRepo.findByClaimsClaimUuidAndActive(claim.getClaimUuid(),true);
			if (assign!=null) {
				assign.setActive(false);
				rcmClaimAssignmentRepo.save(assign);
			}
			//assign  to TL.
			RcmClaimAssignment newAssign = new RcmClaimAssignment();
			newAssign.setActive(true);
			newAssign.setAssignedBy(user);
			newAssign.setCreatedBy(user);
			newAssign.setAssignedTo(rcmLeadUser);
			newAssign.setClaims(claim);
			newAssign.setSystemComment("Assign back to TL");
			newAssign.setCommentAssignedBy(dto.getRemark());
			newAssign.setCurrentTeamId(claim.getCurrentTeamId());
			newAssign.setRcmClaimStatus(claim.getClaimStatusType());
			newAssign.setCreatedDate(new Date());
			newAssign.setTakenBack(false);

			rcmClaimAssignmentRepo.save(newAssign);
			
			// Take back from OLd team
			
			
			return "success";
		}else {
			return "assigned user not a lead";
		}
		}

		else
			return "wrong Client Name";

	}

	public String saveClaimNotes(PartialHeader partialHeader, ClaimNotesDto dto) {

		// RcmClaimNotes notes = null;
        boolean validateClaimRight=checkifCompanyIdMatchesList(partialHeader.getJwtUser().getUuid(),partialHeader.getCompany().getUuid());
		
		if (!validateClaimRight) {
			return null;
	     }
		RcmUser user = userRepo.findByUuid(partialHeader.getJwtUser().getUuid());

		RcmClaims claim = rcmClaimRepository.findByClaimUuid(dto.getClaimUuid());
		RcmClaimAssignment assign = rcmClaimAssignmentRepo
				.findByAssignedToUuidAndClaimsClaimUuidAndActive(partialHeader.getJwtUser().getUuid(), claim.getClaimUuid(), true);
		// claim.getC
		if (assign == null) {
			return "Not assigned to user:" + partialHeader.getJwtUser().getEmail();
		}
		//RcmCompany rcmCompany = rcmCommonServiceImpl.getCompanyFormParitalHeaderCompanyId(officeRepo.findByUuid(claim.getOffice().getUuid()).getCompany().getUuid(), partialHeader.getCompany());
		if (validateClaimRight) {

			if (!claim.isPending() && claim.getCurrentStatus()==0)
				return "Claim Already Submitted";
			saveClaimNotes(dto.getData(), user, claim, partialHeader);
			return "Done";
		}

		else
			return "wrong Client Name";

	}

	private boolean saveClaimNotes(List<ClaimNoteDto> data, RcmUser user, RcmClaims claim, PartialHeader partialHeader) {
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

	public List<ClaimRuleVaidationValueDto> fetchClaimAllRulesData(PartialHeader partialHeader, String claimuuid) {

		List<ClaimRuleVaidationValueDto> list = new ArrayList<>();
		List<ClaimRuleValidationDto> d = rcmClaimRuleValidationRepo.fetchClaimRuleValidation(claimuuid);

		List<RcmRules> all = rcmRuleRepo.findByRuleTypeInAndActive(
				Arrays.asList(new String[] { Constants.RULE_TYPE_RCM, Constants.RULE_TYPE_RULE_ENGINE_AND_RCM,Constants.RULE_TYPE_GSHEET }), 1);

		all.forEach(s -> {
			ClaimRuleVaidationValueDto dto = new ClaimRuleVaidationValueDto();
			dto.setRuleId(s.getId());
			dto.setRuleName(s.getName());
			dto.setRuleDesc(s.getDescription());
			dto.setManualAuto(s.getManualAuto());
			dto.setRuleType(s.getRuleType());
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

	public String saveClaimManualRules(PartialHeader partialHeader, ClaimRuleValidationsDto dto) {

		// RcmClaimNotes notes = null;
       boolean validateClaimRight=checkifCompanyIdMatchesList(partialHeader.getJwtUser().getUuid(),partialHeader.getCompany().getUuid());
		
		if (!validateClaimRight) {
			return null;
	     }
		RcmUser user = userRepo.findByUuid(partialHeader.getJwtUser().getUuid());

		RcmClaims claim = rcmClaimRepository.findByClaimUuid(dto.getClaimUuid());
		RcmClaimAssignment assign = rcmClaimAssignmentRepo
				.findByAssignedToUuidAndClaimsClaimUuidAndActive(partialHeader.getJwtUser().getUuid(), claim.getClaimUuid(), true);
		// claim.getC
		if (assign == null) {
			return "Not assigned to user:" + partialHeader.getJwtUser().getEmail();
		}

		//RcmCompany rcmCompany = rcmCommonServiceImpl.getCompanyFormParitalHeaderCompanyId(officeRepo.findByUuid(claim.getOffice().getUuid()).getCompany().getUuid(), partialHeader.getCompany());
		if (validateClaimRight) {
			
			if (!claim.isPending() && claim.getCurrentStatus()==0)
				return "Claim Already Submitted";
			if (claim.getCurrentState() ==Constants.CLAIM_ARCHIVE_PREFIX_CANNOT_SUBMITED)
				return "Claim is Archived";
			saveClaimManualRules(dto.getData(), user, claim, partialHeader);

			return "Done";
		}

		else
			return "wrong Client Name";

	}

	private void saveClaimManualRules(List<com.tricon.rcm.dto.ClaimRuleValidationDto> data, RcmUser user,
			RcmClaims claim, PartialHeader partialHeader) {
		data.forEach(s -> {
			RcmRules rule = rcmRuleRepo.findById(s.getRuleId());
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

					val.setTeamId(rcmTeamRepo.findById(partialHeader.getTeamId()));
					rcmClaimRuleValidationRepo.save(val);

				}
			}
		});

	}

	public String fetchClaimRemark(PartialHeader partialHeader, String claimuuid) {

		RcmClaimComment d = rcmClaimCommentRepo.findByClaimsClaimUuid(claimuuid);
		if (d != null) {
			return d.getComments();
		}
		return "";
	}

	public ClaimEditDetailDto saveFullClaim(PartialHeader partialHeader, ClaimEditDto dto) {

       boolean validateClaimRight=checkifCompanyIdMatchesList(partialHeader.getJwtUser().getUuid(),partialHeader.getCompany().getUuid());
		
		if (!validateClaimRight) {
			return null;
	     }
		
		ClaimEditDetailDto claimEditDetailDto= new ClaimEditDetailDto();
		String message="";
		RcmClaims claim = rcmClaimRepository.findByClaimUuid(dto.getClaimUuid());
		RcmUser user = userRepo.findByUuid(partialHeader.getJwtUser().getUuid());
		RcmClaimAssignment assign = rcmClaimAssignmentRepo
				.findByAssignedToUuidAndClaimsClaimUuidAndActive(partialHeader.getJwtUser().getUuid(), claim.getClaimUuid(), true);
		// claim.getC
		if (assign == null) {
			claimEditDetailDto.setMessage( "Not assigned to user:" + partialHeader.getJwtUser().getEmail());
			return claimEditDetailDto;
			}
		boolean notesSaved=false;
		RcmOffice office =officeRepo.findByUuid(claim.getOffice().getUuid());
		//RcmCompany rcmCompany = rcmCommonServiceImpl.getCompanyFormParitalHeaderCompanyId(officeRepo.findByUuid(claim.getOffice().getUuid()).getCompany().getUuid(), partialHeader.getCompany());
		if (validateClaimRight) {

			if (!claim.isPending() && claim.getCurrentStatus()==0) {
				claimEditDetailDto.setMessage( "Claim Already Submitted");
				return claimEditDetailDto;
		}
		  else if (claim.getCurrentState() ==Constants.CLAIM_ARCHIVE_PREFIX_CANNOT_SUBMITED){
			  claimEditDetailDto.setMessage( "Claim is Archived");
			  return claimEditDetailDto;
				
		} else {

				//if (partialHeader.getTeamId()==RcmTeamEnum.BILLING.getId()) {//only billing can save
				//The claims that will be parked in the Internal Audit Team's bucket first, only sections
				//billing team will be filling will include:	
				//a) Rules Engine Validations  b) Enter Claim Submission Details:	
				
				if ((claim.getFirstWorkedTeamId().getId()==RcmTeamEnum.INTERNAL_AUDIT.getId()) && 
					partialHeader.getTeamId()==RcmTeamEnum.BILLING.getId() ) {
					if (dto.getRuleRemarkDto() != null) {
						//Filter List with section name =="RuleEngine" //This is set from UI 
					    List<RuleRemarkDto> fList= dto.getRuleRemarkDto().stream().filter(re -> re.getSectionName().equals(Constants.UI_RULEENIGNE_SECTION))
					      .collect(Collectors.toList());
						if (fList!=null) saveClaimRuleRemark(fList, user, claim, partialHeader);
						 /*fList= dto.getRuleRemarkDto().stream().filter(re -> re.getSectionName().equals(Constants.UI_CLAIM_VALIDATION_SECTION))
							      .collect(Collectors.toList());
						 if (fList!=null)saveClaimRuleManualAutoMessageType(fList, user, claim, partialHeader);*/

					}
					

					if (dto.getSubmissionDto() != null)
						saveClaimSubmissionDetails(user, claim, dto.getSubmissionDto());
					
					
				}
				else if ((claim.getFirstWorkedTeamId().getId()==RcmTeamEnum.BILLING.getId()) && 
						partialHeader.getTeamId()==RcmTeamEnum.BILLING.getId()) {
					if(dto.getClaimManualRuleValidationList() != null)	saveClaimManualRules(dto.getClaimManualRuleValidationList(), user, claim, partialHeader);
					if (dto.getClaimNoteDtoList() != null)	notesSaved = saveClaimNotes(dto.getClaimNoteDtoList(), user, claim, partialHeader);
					if (dto.getRuleRemarkDto() != null) {
						
							saveClaimRuleRemark(dto.getRuleRemarkDto(), user, claim, partialHeader);
							List<RuleRemarkDto> fList= dto.getRuleRemarkDto().stream().filter(re -> re.getSectionName().equals(Constants.UI_CLAIM_VALIDATION_SECTION))
							      .collect(Collectors.toList());
						 if (fList!=null)saveClaimRuleManualAutoMessageType(fList, user, claim, partialHeader);
					}
					if (dto.getClaimRemark() != null) {
						saveClaimRemark(dto.getClaimRemark(), claim, user, partialHeader);
					}

					if (dto.getSubmissionDto() != null)
						saveClaimSubmissionDetails(user, claim, dto.getSubmissionDto());
					if (dto.getSerCVDto() != null)
						saveClaimServiceCodeValidation(dto.getSerCVDto(), user, claim, partialHeader);
					
				    
				}
				else if ((claim.getFirstWorkedTeamId().getId()==RcmTeamEnum.INTERNAL_AUDIT.getId()) && 
						partialHeader.getTeamId()==RcmTeamEnum.INTERNAL_AUDIT.getId()) {
				    //TO DO
					if(dto.getClaimManualRuleValidationList() != null)	saveClaimManualRules(dto.getClaimManualRuleValidationList(), user, claim, partialHeader);
					
					if (dto.getClaimNoteDtoList() != null)	notesSaved = saveClaimNotes(dto.getClaimNoteDtoList(), user, claim, partialHeader);
					
					if (dto.getRuleRemarkDto() != null) {
						List<RuleRemarkDto> fList= dto.getRuleRemarkDto().stream().filter(re -> re.getSectionName().equals(Constants.UI_RULEENIGNE_SECTION))
							      .collect(Collectors.toList());
						saveClaimRuleRemark(fList, user, claim, partialHeader);
						fList= dto.getRuleRemarkDto().stream().filter(re -> re.getSectionName().equals(Constants.UI_CLAIM_VALIDATION_SECTION))
					      .collect(Collectors.toList());
				        if (fList!=null) {
				        	saveClaimRuleManualAutoMessageType(fList, user, claim, partialHeader);
				        	saveClaimRuleRemark(fList, user, claim, partialHeader);
				        }
				        

					}
					if (dto.getSerCVDto() != null)
						saveClaimServiceCodeValidation(dto.getSerCVDto(), user, claim, partialHeader);
					if (dto.getClaimRemark() != null) {
						saveClaimRemark(dto.getClaimRemark(), claim, user, partialHeader);
					}
					
					if (dto.getSubmissionDto() != null)
						saveClaimSubmissionDetails(user, claim, dto.getSubmissionDto());
				}
				
				
			  //}

			}
			claim.setLastWorkTeamId(rcmTeamRepo.findById(partialHeader.getTeamId()));
			claim.setRuleEngineRunRemark(dto.getRuleEngineRunRemark());
			//only billing can submit.
			if (dto.isSubmission() && partialHeader.getTeamId()==RcmTeamEnum.BILLING.getId()) {
				//only billing can submit
				message= assignClaimToOtherTeamWithRemarkCommon(partialHeader,dto.getClaimUuid(),
						Constants.FROMBILLINGTOPOSTING,"Please work on claim",claim,assign,user,office,null);
				
				claim.setPending(false);
				
				claim.setUpdatedBy(user);
				claim.setUpdatedDate(new Date());
				claim.setCurrentStatus(Constants.CLAIM_POSTING_STATE);
				rcmClaimRepository.save(claim);
				//Check if Primary	then Find any Corresponding Secondary Claim and Mark Primar_status =2
				String[] clT = claim.getClaimId().split("_");

				if (("_" + clT[1]).equals(ClaimTypeEnum.P.getSuffix())) {
					//Primary True
				RcmClaims secondary= rcmClaimRepository.findByClaimIdAndOffice(clT[0]+ClaimTypeEnum.S.getSuffix(), claim.getOffice());
				if (secondary!=null) {
					secondary.setPrimaryStaus(Constants.Primary_Status_Primary_submit);
					rcmClaimRepository.save(secondary);
				 }
				}
				
				message="Submitted";
				
				
			}else if(dto.isAssignToOtherTeam()){
				message= assignClaimToOtherTeamWithRemarkCommon(partialHeader,dto.getClaimUuid(),
						dto.getAssignToTeam(),dto.getAssignToComment(),claim,assign,user,office,null);
			}/*else if(dto.isAssignToTL()){//Separate API
				//RcmUser assignuser = userRepo.findByUuid(jwtUser.getUuid());
				claim.setUpdatedBy(user);
				claim.setUpdatedDate(new Date());
				rcmClaimRepository.save(claim);
				message="TL";
			}*/
			else {
				
				
				claim.setUpdatedBy(user);
				claim.setUpdatedDate(new Date());
				rcmClaimRepository.save(claim);
				message="Latter";
			}
			
			

		} else {
			message = "wrong Client Name";
		}
		claimEditDetailDto.setMessage(message);
		return claimEditDetailDto;
	}
	
	public String assignClaimToOtherTeamWithRemark(PartialHeader partialHeader,ClaimAssignWithRemarkAndTeam dto) {
		String message="";
		RcmClaims claim = rcmClaimRepository.findByClaimUuid(dto.getClaimUuid());
		
		if (claim.getCurrentState() ==Constants.CLAIM_ARCHIVE_PREFIX_CANNOT_SUBMITED) {
			message= "Claim is Archived";
			return message;
		}
		RcmUser user = userRepo.findByUuid(partialHeader.getJwtUser().getUuid());
		Integer assignToTeamId=dto.getAssignToTeamId();
		int currentTeam=claim.getCurrentTeamId().getId();
		if (assignToTeamId == null) {
			//Find the Team Who Assigned the Claim
			assignToTeamId = claim.getLastWorkTeamId().getId();
			
		}
		/*if (assignToTeamId == null) {
			return "no Assigned team";
		}*/;
		RcmTeam team = rcmTeamRepo.findById(assignToTeamId);
		if (team == null) {
			return "Wrong Team";
		}
		if (dto.getRemark() == null) {
			return "no remarks";
		}
		if (assignToTeamId == currentTeam) {
			return "Teams Cannot be same";
		}
		
		dto.setRemark(dto.getRemark().trim());
		RcmClaimAssignment assign =null;
		if (partialHeader.getRole().equals(Constants.ASSOCIATE)) {
			 assign = rcmClaimAssignmentRepo
					.findByAssignedToUuidAndClaimsClaimUuidAndActive(partialHeader.getJwtUser().getUuid(), claim.getClaimUuid(), true);
			// claim.getC
			if (assign == null) {
				message= "Not assigned to user:" + partialHeader.getJwtUser().getEmail();
				return message;
			}
		}
		
		if (partialHeader.getRole().equals(Constants.TEAMLEAD)) {
			 assign = rcmClaimAssignmentRepo
					.findByCurrentTeamIdIdAndClaimsClaimUuidAndActive(partialHeader.getTeamId(), claim.getClaimUuid(), true);
			// claim.getC
			if (assign == null) {
				message= "Not assigned to TEAM:" + partialHeader.getJwtUser().getTeamId();
				return message;
			}
		}
		
		
		//boolean notesSaved=false;
		RcmOffice office =officeRepo.findByUuid(claim.getOffice().getUuid());
		RcmCompany rcmCompany = rcmCommonServiceImpl.getCompanyFormParitalHeaderCompanyId(officeRepo.findByUuid(claim.getOffice().getUuid()).getCompany().getUuid(), partialHeader.getCompany());
		if (rcmCompany!=null) {

			if (!claim.isPending() && claim.getCurrentStatus()==0) {
				message= "Claim Already Submitted";
				return message;
			} else {
				message =assignClaimToOtherTeamWithRemarkCommon(partialHeader, dto.getClaimUuid(),
						assignToTeamId, dto.getRemark(), claim,
						 assign, user, office,dto.getAttachmentsWithRemarks());
				if (message!=null && message.equals("OtherTeam")) message="done";
				return message;
			
		      }
			}
		return null;
		
	}
	/**
	 * Assign Claim To Other Team
	 * @param partialHeader
	 * @param dto
	 * @param claim
	 * @param assign
	 * @param user
	 * @param office
	 * @return
	 */
	private String assignClaimToOtherTeamWithRemarkCommon(PartialHeader partialHeader,String claimUuid,
			int assignToTeam,String assignToComment,RcmClaims claim,
			RcmClaimAssignment assign,RcmUser user,RcmOffice office,String attachmentsWithRemarks) {
          
		if (!claim.isPending() && claim.getCurrentStatus()==0) {
			
			return "Already Submitted";
		}
		
		if (claim.getCurrentState() ==Constants.CLAIM_ARCHIVE_PREFIX_CANNOT_SUBMITED) {
			return "Claim is Archived";
		}
		
		  RcmTeam assignTeam = rcmTeamRepo.findById(assignToTeam);
		  RcmTeam lastTeam = rcmTeamRepo.findById(partialHeader.getTeamId());
		  RcmTeam oldTeam = assign.getCurrentTeamId();
		  RcmUser oldRcmUser = assign.getAssignedTo();
		  
		  claim.setUpdatedBy(user);
		  claim.setCurrentTeamId(assignTeam);
		  claim.setLastWorkTeamId(lastTeam);
		  //Edit Assignment Data
		  assign.setActive(false);
		  //assign.setCommentAssignedBy(Constants.SYSTEM_TRANSFER_TO_TEAM_COMMENT+ ":"+assignTeam.getName());
		  assign.setUpdatedBy(user);
		  assign.setUpdatedDate(new Date());
		  
		  rcmClaimAssignmentRepo.save(assign);
		  //Assignment Table
		  UserAssignOffice assignedUser = userAssignOfficeRepo
					.findByOfficeUuidAndTeamId(office.getUuid(), assignTeam.getId());
		  if (assignedUser!=null) {
			  RcmClaimAssignment rcmAssigment = new RcmClaimAssignment();
			  //make New Entry
			  RcmTeam assignedTeam  = rcmTeamRepo.findById(assignTeam.getId());
			  RcmClaimStatusType systemStatusBilling = rcmClaimStatusTypeRepo
						.findByStatus(ClaimStatusEnum.Billing.getType());
			  
				rcmAssigment = ClaimUtil.createAssginmentData(rcmAssigment, user,
						assignedUser.getUser(), claimUuid, claim,
						assignToComment, systemStatusBilling,assignedTeam,
						Constants.SYSTEM_TRANSFER_TO_TEAM_COMMENT+"( From "+partialHeader.getTeamId()+" to "+assignToTeam +")");
				rcmAssigment.setCurrentTeamId(oldTeam); 
				rcmAssigment.setAssignedTo(oldRcmUser);
				rcmAssigment.setActive(false);
				
				// save attachment-with-remarks(yes/no)
				if (attachmentsWithRemarks!=null && attachmentsWithRemarks.equals(Constants.ATTACH_WITH_REMARKS)){
					int existingAttachmentCounts=attachmentRepo.attachmentCountOfUserUuid(claimUuid, user.getUuid());
					if(existingAttachmentCounts>0) {
					rcmAssigment.setAttachmentWithRemarks(Constants.ATTACHMENT_WITH_REMARKS);}
				}
				
				rcmClaimAssignmentRepo.save(rcmAssigment);
				
				rcmAssigment= new RcmClaimAssignment();
				rcmAssigment = ClaimUtil.createAssginmentData(rcmAssigment, user,
						assignedUser.getUser(), claimUuid, claim,
						"", systemStatusBilling,assignedTeam,
						Constants.SYSTEM_TRANSFER_TO_TEAM_COMMENT+"( From "+partialHeader.getTeamId()+" to "+assignToTeam +")");

				rcmClaimAssignmentRepo.save(rcmAssigment);
				
				
		  }else {
			  RcmTeam assignedTeam  = rcmTeamRepo.findById(assignTeam.getId());
			  RcmClaimStatusType systemStatusBilling = rcmClaimStatusTypeRepo
						.findByStatus(ClaimStatusEnum.Billing.getType());
			  RcmClaimAssignment rcmAssigment = new RcmClaimAssignment();
			  //make New Entry
			  rcmAssigment = ClaimUtil.createAssginmentData(rcmAssigment, user,
						null, claimUuid, claim,
						assignToComment, systemStatusBilling,assignedTeam,
						Constants.SYSTEM_TRANSFER_TO_TEAM_COMMENT+"( From "+partialHeader.getTeamId()+" to "+assignToTeam +")");
			  rcmAssigment.setCurrentTeamId(oldTeam);
			  rcmAssigment.setAssignedTo(oldRcmUser);
			  rcmAssigment.setActive(false);
			  rcmClaimAssignmentRepo.save(rcmAssigment);
				
			rcmAssigment= new RcmClaimAssignment();
			  
			  
			  
				rcmAssigment = ClaimUtil.createAssginmentData(rcmAssigment, user,
						null, claimUuid, claim,
						"", systemStatusBilling,assignedTeam,
						Constants.SYSTEM_TRANSFER_TO_TEAM_COMMENT+"( From "+partialHeader.getTeamId()+" to "+assignToTeam +")");
				rcmClaimAssignmentRepo.save(rcmAssigment);
				
				
		  }

		  //claim.set
			claim.setUpdatedDate(new Date());
			rcmClaimRepository.save(claim);
			return "OtherTeam";
		
	}

	/*
	public String assignToOtherTeam(PartialHeader partialHeader, ClaimAssignDto dto) {

		RcmClaims claim = rcmClaimRepository.findByClaimUuid(dto.getClaimUuid());
		RcmUser user = userRepo.findByUuid(partialHeader.getJwtUser().getUuid());
		RcmClaimAssignment assign = rcmClaimAssignmentRepo
				.findByAssignedToUuidAndClaimsClaimUuidAndActive(partialHeader.getJwtUser().getUuid(), claim.getClaimUuid(), true);
		// claim.getC
		if (assign == null) {
			return "Not assigned to user:" + partialHeader.getJwtUser().getEmail();
		}
		RcmOffice office=officeRepo.findByUuid(claim.getOffice().getUuid());
		RcmCompany rcmCompany = rcmCommonServiceImpl.getCompanyFormParitalHeaderCompanyId(office.getCompany().getUuid(), partialHeader.getCompany());
		if (rcmCompany!=null) {

			if (!claim.isPending()) {
				return "Claim Already Submitted";
			}
			if (partialHeader.getCompany().getName().equals(Constants.COMPANY_NAME) &&  !claim.isPulledClaimsServiceDataFromEs()) {
				return "Service Code validation not Done";
			}

			
				// Assign to other Team.
			;
				RcmTeam team = rcmTeamRepo.findById(dto.getAssignToTeam());
				RcmTeam lastTeam = rcmTeamRepo.findById(partialHeader.getTeamId());
				if (team != null) {
					// Cannot assign to Same Team
					if (partialHeader.getTeamId() != dto.getOtherTeamId()) {

						claim.setCurrentTeamId(team);
						claim.setLastWorkTeamId(lastTeam);
						claim.setUpdatedBy(user);
						claim.setUpdatedDate(new Date());
						rcmClaimRepository.save(claim);

						RcmClaimAssignment newAssign = new RcmClaimAssignment();

						newAssign.setActive(true);
						newAssign.setAssignedBy(user);
						// see if some have been assigned to this office 
						
						UserAssignOffice assignedUserBilling = userAssignOfficeRepo.findByOfficeUuidAndTeamId(office.getUuid(),
								partialHeader.getTeamId());
						if (assignedUserBilling!=null) {
							newAssign.setAssignedTo(assignedUserBilling.getUser());
						}else {
							newAssign.setAssignedTo(null);// only to team but not to any Person.
						}
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

		} else {
			return "wrong Client Name";
		}

		return "success";
	}*/

	public AutoRunClaimReponseDto runAutomatedRules(RcmClaims claim,PartialHeader partialHeader, String claimuuid,boolean reRrun,boolean firstRun) {

		 boolean validateClaimRight=checkifCompanyIdMatchesList(partialHeader.getJwtUser().getUuid(),partialHeader.getCompany().getUuid());
			
			if (!validateClaimRight) {
				return null;
		 }
			
		if (claim==null)claim  = rcmClaimRepository.findByClaimUuid(claimuuid);
		AutoRunClaimReponseDto dto = new AutoRunClaimReponseDto();
		
		if (claim.isAutoRuleRun() && !reRrun) {
			dto.setMessage("Already Run");
			return dto;
		}
		RcmUser user = userRepo.findByUuid(partialHeader.getJwtUser().getUuid());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		System.out.println(calendar.get(Calendar.YEAR));
		RcmOffice off =officeRepo.findByUuid(claim.getOffice().getUuid());
		RcmCompany rcmCompany= rcmCompanyRepo.findByUuid(off.getCompany().getUuid());
		
		//RcmCompany rcmCompany = rcmCommonServiceImpl.getCompanyFormParitalHeaderCompanyId(off.getCompany().getUuid(), partialHeader.getCompany());
		if (validateClaimRight) {

			if (!claim.isPending() && claim.getCurrentStatus()==0) {
				dto.setMessage("Already Submitted");
				return dto;
				
			}
			
			if (claim.getCurrentState() ==Constants.CLAIM_ARCHIVE_PREFIX_CANNOT_SUBMITED) {
				dto.setMessage("Claim is Archived");
				return dto;
			}
			
			RcmClaimAssignment assign1 = rcmClaimAssignmentRepo.findByClaimsClaimUuidAndActive(claimuuid, true);
			//(assign1 != null && partialHeader.getJwtUser().getUuid().equals(assign1.getAssignedTo().getUuid()))
			//Only Assigned User Can Run
			if (firstRun || (assign1 != null && partialHeader.getJwtUser().getUuid().equals(assign1.getAssignedTo().getUuid()))) {
				try {

					dto.setProviderOnClaim(claim.getProviderOnClaim());
	   				dto.setClaimType(claim.getClaimType());
	   				
					if (claim.getProviderOnClaim()==null){
						claim.setProviderOnClaim("");
					}
					if (claim.getTreatingProvider()==null) {
						claim.setTreatingProvider("");
					}
					if (claim.getProviderOnClaim().equals("") || claim.getTreatingProvider().equals("")) {
						
						//Provider Sheet
						String treatingProvider="";
						List<ProivderHelpingSheetDto> providers=null;
						String treatingProviderFromClaim="";
						String providerOnClaim="";
						String providerOnClaimFromSheet="";
						String specialty="";
						
						try {
							String sheetYear = Constants.SDF_SHEET_PROVIDER_DATE_HELPING_YEAR.format(claim.getDos());
					    Object providerSheetData[] = ConnectAndReadSheets.readProviderGSheet(
					    		Constants.Mapping_Tables, Constants.Mapping_Tables_Provider, CLIENT_SECRET_DIR,
							CREDENTIALS_FOLDER);
					   	
					   List<ProivderHelpingSheetDto> helpingList= ConnectAndReadSheets.readProviderScheduleHelpingGSheet(
					    		Constants.Provider_Schedule_SHEET, sheetYear , CLIENT_SECRET_DIR,
							CREDENTIALS_FOLDER);
		               String officeName=off.getName();// officeRepo.findById(claim.getOffice().getUuid()).get().getName();
		               //String sheetDate = Constants.SDF_SHEET_PROVIDER_DATE.format(implDto.getDos());
		               String sheetDate = Constants.SDF_SHEET_PROVIDER_DATE_HELPING.format(claim.getDos());
		               //String doc1FromProvider = doc1NameMap.get(officeName + "->" + sheetDate);
		               if (Constants.COMPANY_NAME.equalsIgnoreCase(rcmCompany.getName())) {
		            	   providers = getDocsFromHelpingData(helpingList,sheetDate,officeName,rcmCompany.getName());
		               }else {
		            	   providers=new ArrayList<>();
		            	   ProivderHelpingSheetDto tempHelp= new ProivderHelpingSheetDto(
		            			   rcmCompany.getName(), sheetDate,  off.getName(),  claim.getTreatingProviderFromClaimOnSheet(),  "Doc - 1");
		            	   providers.add(tempHelp);
		            	   //tempHelp.setClientName(implDto.getClientName());
		            	   //tempHelp.setDate(date);
		            	   //tempHelp.setOfficeName(officeName);
		            	   //tempHelp.setTreatingProvider(implDto.getTreatingProviderFromSheet());
		            	   //tempHelp.setType("Doc - 1");
		            	   //providers =tempHelp;// implDto.getTreatingProviderFromSheet();
		               }
		               
		               
		               
		               //final String  treatingProviderF=treatingProvider;
		               List<ProviderCodeWithOffice> pro = (List<ProviderCodeWithOffice>) providerSheetData[1];
		   			   
		               final String proIdFinal=claim.getProviderId();
		               List<ProviderCodeWithOffice> pCodeList = pro.stream()
		   					.filter(e -> e.getOffice().trim().equalsIgnoreCase(officeName)
		   							&& e.getClientName().trim().equalsIgnoreCase(rcmCompany.getName())
		   							&& e.getEsCode().trim().equalsIgnoreCase(proIdFinal))
		   					.collect(Collectors.toList());
		               
		   			if (pCodeList != null && pCodeList.size() > 0) {
		   				treatingProviderFromClaim = pCodeList.get(0).getProviderCode();
					 	
		   			 List<ProviderCodeWithSpecialty> proEs = (List<ProviderCodeWithSpecialty>) providerSheetData[0];
		   			 List<ProviderCodeWithSpecialty> proEsF= proEs.stream().filter(p -> 
		   			                            p.getProviderCode().equalsIgnoreCase(pCodeList.get(0).getProviderCode())
		   			                          && p.getClientName().equalsIgnoreCase(rcmCompany.getName())
		   			                            )
					.collect(Collectors.toList());
		   			 if (proEsF!=null && proEsF.size()>0) {
		   				providerOnClaim =proEsF.get(0).getProviderNames();
		   				specialty = proEsF.get(0).getSpecialty();
		   			 }
		   				
		   			}
		   			
		   			
		   			List<ProviderCodeWithSpecialty> proEs = (List<ProviderCodeWithSpecialty>) providerSheetData[0];
		   			for(ProivderHelpingSheetDto provider: providers) {
		   				final String  treatingProviderF=provider.getTreatingProvider();
		   				treatingProvider=treatingProvider+Constants.ProviderJoinCons+provider.getTreatingProvider();
		   				List<ProviderCodeWithSpecialty> proEsF= proEs.stream().filter(p -> 
		   				            p.getProviderCode().equalsIgnoreCase(treatingProviderF) &&
		   				            p.getClientName().equalsIgnoreCase(rcmCompany.getName())
		   				            )
		   	   					.collect(Collectors.toList());
		   	   			if (proEsF!=null && proEsF.size()>0) {
		   	   				providerOnClaimFromSheet =providerOnClaimFromSheet+Constants.ProviderJoinCons+ proEsF.get(0).getProviderNames();
		   	   			}
		   				
		   			}
		   			
		   			//if (!sheetProvider.equals("") ||  !claimProvider.equals("")) {
		   				
		   				
		   				claim.setTreatingProvider(treatingProvider.replaceFirst(Constants.ProviderJoinCons, ""));
		   				claim.setTreatingProviderFromClaim(treatingProviderFromClaim);
		   				claim.setClaimType(specialty);
		   				claim.setProviderOnClaim(providerOnClaim);
		   				claim.setProviderOnClaimFromSheet(providerOnClaimFromSheet.replaceFirst(Constants.ProviderJoinCons, ""));
		   				
		   				dto.setProviderOnClaim(providerOnClaim);
		   				dto.setProviderOnClaimFromSheet(providerOnClaimFromSheet.replaceFirst(Constants.ProviderJoinCons, ""));
		   				dto.setClaimType(specialty);
		   				rcmClaimRepository.save(claim);
		   			///}
						}catch(Exception c) {
							c.printStackTrace();
						}
						
						
						
						
						
					}
					DataPatientRuleDto ivData=null;
					if (claim.getIvfId()!=null) {
						ivData =	rcmClaimRepository.getDataForRuleCheckFromIV(claim.getIvfId());
					}
					List<String> types = Arrays
							.asList(new String[] { Constants.RULE_TYPE_RCM, Constants.RULE_TYPE_RULE_ENGINE_AND_RCM })
							.stream().collect(Collectors.toList());
					List<RcmRules> rules = rcmRuleRepo.findByRuleTypeInAndActiveAndManualAuto(types, 1,
							Constants.RULE_TYPE_AUTO);

					List<TPValidationResponseDto> allLIst = new ArrayList<>();

					//Get Iv From Database Report
					//CaplineIVFFormDto ivData1 = getIvfDataFromRE(partialHeader.getCompany().getUuid(), claimuuid);

					RcmRules rule = getRulesFromList(rules, RuleConstants.RULE_ID_301);
					allLIst.addAll(ruleBookService.rule301(rule, ivData, claim.getSecMemberId()));

					rule = getRulesFromList(rules, RuleConstants.RULE_ID_302);
					allLIst.addAll(ruleBookService.rule302(rule, ivData, claim.getGroupNumber()));

					rule = getRulesFromList(rules, RuleConstants.RULE_ID_303);
					allLIst.addAll(ruleBookService.rule303(rule, ivData, claim));

					rule = getRulesFromList(rules, RuleConstants.RULE_ID_304);
					
					
					/*Object providerSheetData[] = ConnectAndReadSheets.readProviderGSheet(
							Constants.Mapping_Tables, Constants.Mapping_Tables_Provider, CLIENT_SECRET_DIR,
							CREDENTIALS_FOLDER);
					*/
					String officeName=officeRepo.findById(claim.getOffice().getUuid()).get().getName();
					allLIst.addAll(ruleBookService.rule304(rule, claim));

					rule = getRulesFromList(rules, RuleConstants.RULE_ID_305);
					List<CredentialData> creList = ConnectAndReadSheets.readCredentialGSheet(
							"1QPfa8aEA5pcSWhGJQSX9R7CC-ZxVA-8_nvagjGAA4s4", "Master Data", CLIENT_SECRET_DIR,
							CREDENTIALS_FOLDER);
					claim.setRcmInsuranceType(rcmInsuranceTypeRepo.findById(claim.getRcmInsuranceType().getId()));
					
					String insName="";
					String insCode="";
					//RcmInsuranceType temp=null;
					String[] clT = claim.getClaimId().split("_");
					if (("_" + clT[1]).equals(ClaimTypeEnum.P.getSuffix())) {
				      Optional<RcmInsurance> nn= insuranceRepo.findById(claim.getPrimInsuranceCompanyId().getId());
				      if (nn.isPresent()) {
				    	  //temp= rcmInsuranceTypeRepo.findById(nn.get().getInsuranceType().getId());
				    	  insName=	nn.get().getName();
				    	  insCode= nn.get().getInsuranceCode();
				      }
					} else {
						 Optional<RcmInsurance> nn= insuranceRepo.findById(claim.getSecInsuranceCompanyId().getId());
					      if (nn.isPresent()) {
					    	 // temp= rcmInsuranceTypeRepo.findById(nn.get().getInsuranceType().getId());
					    	  insName=	nn.get().getName();
					    	  insCode= nn.get().getInsuranceCode();
					      }
					}
					if (insCode==null) insCode ="";
					if (insCode.equals("")) {
						//Reading Sheet Again
						List<InsuranceNameTypeDto> insuranceTypeDto = ruleEngineService.pullInsuranceMappingFromSheet(
								partialHeader.getCompany());
						InsuranceNameTypeDto insuranceNameTypeDto= ruleEngineService.getInsuranceTypeFromSheetListByNameAndClient(insuranceTypeDto, insName,rcmCompany.getName());
						insCode =(insuranceNameTypeDto==null)?"": insuranceNameTypeDto.getInsuranceCode();//point 39
						//TO Do Update the Data in InsuranceNameType
						claim.setPreferredModeOfSubmission(insuranceNameTypeDto.getPreferredModeOfSubmission());
						dto.setPreferredModeOfSubmission(insuranceNameTypeDto.getPreferredModeOfSubmission());
					}
					
					allLIst.addAll(
							ruleBookService.rule305(rule, creList, claim, officeName, insCode,insName));
					
					
					rule = getRulesFromList(rules, RuleConstants.RULE_ID_323);
					String appointmentDate=ruleEngineService.fetchAppointmentDate(claim, off);
					allLIst.addAll(
							ruleBookService.rule323(rule,appointmentDate, claim));
					/*rule = getRulesFromList(rules, RuleConstants.RULE_ID_306);
					allLIst.addAll(
							ruleBookService.rule306(rule, ivData, claim));
                    */
					// Save Data here RcmClaimRuleValidation
					//if (deleteOld) {
					//	rcmClaimRuleValidationRepo.deleteByClaimId(claim.getClaimUuid());
					//}
					if(ivData!=null) claim.setAssignmentOfBenefits(ivData.getPlanAssignmentofBenefits());
					if(ivData!=null) dto.setAssignmentOfBenefits(ivData.getPlanAssignmentofBenefits());
					saveAutoRuleReport(allLIst, user, claim, rules,partialHeader);
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
		dto.setServiceValidationDataDto(readServiceValidationFromGSheet(claim,claim.getClaimUuid(), partialHeader,true));
		dto.setMessage("success");
		return dto;
	}

	public AllPendencyReportDto getAllPendencyReport(String companyUuid,int teamId,
			PartialHeader partialHeader) {
		
		AllPendencyReportDto dto= new AllPendencyReportDto();
		List<AllPendencyDto> count= null;
		List<AllPendencyDateDto> date = null;
		RcmCompany company=rcmCompanyRepo.findByUuid(companyUuid);
		
		if (company== null) {
			return null;
		}
		
		RcmUserCompany rcmUserCompany= rcmUserCompanyRepo.findByCompanyUuidAndUserUuid(companyUuid,partialHeader.getJwtUser().getUuid());
		
		if (rcmUserCompany== null) {
			return null;
		}
		if (partialHeader.getRole().equals(Constants.ASSOCIATE)) {
            count =rcmClaimRepository.allPendencyCountForUser(company.getUuid(),teamId,partialHeader.getJwtUser().getUuid());
			date =rcmClaimRepository.allPendencyDateCountForUser(company.getUuid(),teamId,partialHeader.getJwtUser().getUuid());
		}else {
			count =rcmClaimRepository.allPendencyCount(company.getUuid());
			date =rcmClaimRepository.allPendencyDateCount(company.getUuid());
		}
		//dto.setCount(count);
		//dto.setDateCount(date);
		dto.setOffices(officeRepo.findByCompanyAndActiveTrueOrderByNameAsc(company));
		List<RcmOfficeDto> offices=officeRepo.findByCompanyAndActiveTrueOrderByNameAsc(company);
		
		//Remove Office in case of associate 
		if (partialHeader.getRole().equals(Constants.ASSOCIATE)) {
			List<RcmOfficeDto> fil =new ArrayList<>();
			List<UserAssignOffice> assignoffices=userAssignOfficeRepo.findByUserUuid(partialHeader.getJwtUser().getUuid());
			for (UserAssignOffice assignoffice:assignoffices) {
				fil.addAll(offices.stream().filter(re -> re.getUuid().equals(assignoffice.getOffice().getUuid()) )
			      .collect(Collectors.toList()));
			}
			dto.setOffices(fil);
			offices= fil;
			
			
		}
		List<PendencyDataCountDto> header=  new ArrayList<>();
		//List<PendencyKeyValDto> keyData =new ArrayList<>();
		List<RcmTeamEnum> teams= Arrays.asList(RcmTeamEnum.values());   
		
		//List<PendencyDataCountDto> rowData=  new ArrayList<>();
		//List<PendencyKeyValDto> keyRowData =new ArrayList<>();
       List<PendencyWithOfficeOnlyDto> onlyOffices= new ArrayList<>();
       
		
		boolean headerDone=false;
		for(RcmOfficeDto office: offices) {
			PendencyWithOfficeOnlyDto onlyOffice = new PendencyWithOfficeOnlyDto();
			 HashMap<String, Object> counts1 = new HashMap<>();
			 HashMap<String, Object> dates1 = new HashMap<>();
			 HashMap<String, Object> datesPending = new HashMap<>();
			onlyOffice.setOfficeName(office.getName());
			//List<Integer> c= new ArrayList<>();
			//List<Date> d= new ArrayList<>();
			//onlyOffice.setCounts(c);
			//onlyOffice.setDates(d);
			onlyOffice.setCounts1(counts1);
			onlyOffice.setDates1(dates1);
			onlyOffice.setDatesPending(datesPending);
			
			//PendencyDataCountDto offLi = new PendencyDataCountDto();
			//offLi.setOfficeName(office.getName());
			//offLi.setOfficeUUid(office.getUuid());
			//List<PendencyKeyValDto> keyData= new ArrayList<>();
			for(RcmTeamEnum team:teams) {
				Stream<AllPendencyDto> streamCount= count.stream();
				Stream<AllPendencyDateDto> streamDate= date.stream();
				PendencyKeyValDto xx = new PendencyKeyValDto();
				if (team.isRoleVisible()) {
					//offLi.setTeamId(team.getId());
					//offLi.setTeamName(team.getName());
					if (!headerDone){
						
						  PendencyDataCountDto head= new PendencyDataCountDto();
					      head.setTeamId(team.getId());
				       	  head.setTeamName(team.getName());
				       	 // keyData.add(head);
				       	  header.add(head);
					}
					AllPendencyDto filCt = streamCount.filter(re -> re.getOfficeName().equals(office.getName())
							 && re.getTeamId()==team.getId())
							.findAny() .orElse(null);
					//PendencyKeyValDto xx = new PendencyKeyValDto();
					xx.setTeamId(team.getId());
					xx.setTeamName(team.getName());
					if (filCt!=null) {
						//xx.setCount(filCt.getCount());
						counts1.put(team.getName(), filCt.getCount());
						
						//c.add(filCt.getCount());
						AllPendencyDateDto yyy=	streamDate.filter(re -> re.getOfficeName().equals(office.getName())
								 && re.getTeamId()==team.getId())
								.findAny() .orElse(null);
					if (yyy!=null)	{
						xx.setMinDate(yyy.getMinDate());
						dates1.put(team.getName(),yyy.getMinDate());
						datesPending.put(team.getName(),yyy.getDt());
						
						//d.add(yyy.getMinDate());
					}else {
						//d.add(null);
						dates1.put(team.getName(),null);
						datesPending.put(team.getName(),null);
					}
						
					}else {
						xx.setCount(0);
						//c.add(0);
						counts1.put(team.getName(), 0);
						dates1.put(team.getName(),null);
						datesPending.put(team.getName(),null);
						//d.add(null);
					}
					
					//rowData.add(offLi);
					//keyData.add(xx);
					//offLi.setKeyData(keyData);
				}//if 
				
				
			}//Teams
			headerDone=true;
			//offLi.setKeyData(keyData);
			//rowData.add(offLi);
			onlyOffices.add(onlyOffice);
		}//Office
		
		dto.setHeader(header);
		//dto.setRowData(rowData);
		dto.setOnlyOffice(onlyOffices);
		return dto;
		
		
	}
	private void saveAutoRuleReport(List<TPValidationResponseDto> ruleDataList, RcmUser user, RcmClaims claim,
			List<RcmRules> rules,PartialHeader partialHeader) {

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
			val.setTeamId(rcmTeamRepo.findById(partialHeader.getTeamId()));
			rcmClaimRuleValidationRepo.save(val);
		}
	}

	
	/**
	 * Assign all un-Assigned Claims to corresponding Users 
	 * @param jwtUser
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean assignedUnsAssignedClaims(PartialHeader partialHeader) {
		
		//Assign Unassigned Claims
		try {
			RcmUser assignedBy= userRepo.findByUuid(partialHeader.getJwtUser().getUuid()) ;
			return ruleEngineService.assignedUnsAssignedClaimsByTeam(partialHeader.getCompany().getUuid(),assignedBy,RcmTeamEnum.BILLING.getId());
		}catch(Exception ex) {
			ex.printStackTrace();
			return false;
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
	
	private boolean isInsuranceCodeMatch(String insuranceTypecode, String insuranceNames) {
		
	logger.error("isInsuranceCodeMatch: "+insuranceTypecode );
	logger.error("insuranceNames: "+insuranceNames );
	
	if (insuranceNames==null) return false;
	if (insuranceNames.trim().equalsIgnoreCase("ALL")) return true;
	String[] nArray=insuranceNames.split(",");
	for(String n:nArray ) {
		if (n.trim().equalsIgnoreCase(insuranceTypecode)) {
			return true;
		}
	  }
	 return false;
	}
	
	private List<ProivderHelpingSheetDto> getDocsFromHelpingData(List<ProivderHelpingSheetDto> helpingList,String sheetDate,
			String officeName,String clientName) {
		
		List<ProivderHelpingSheetDto> filteredList = helpingList.stream()
				.filter(re -> re.getClientName().equalsIgnoreCase(clientName) && re.getDate().equals(sheetDate)
						&& re.getOfficeName().equalsIgnoreCase(officeName))
				.collect(Collectors.toList());
	List<ProivderHelpingSheetDto> providers=new ArrayList<>();
	if (filteredList!=null && filteredList.size()>0) {
		for(ProivderHelpingSheetDto d:filteredList) {
			if (d.getType().equals("Doc - 1")){
				
				providers.add(d);//= d.getTreatingProvider();
				
				//break;
			}else if (d.getType().equals("Doc - 2")) {
				
				providers.add(d);// d.getTreatingProvider();
			}
			
		}
		
	}else {
		
	}
		return providers;
	}
	
	public List<RcmIssuClaimPaginationDto> getArchiveClaimsByPagination(int pageNumber, String companyId)
			throws Exception {
		List<RcmIssuClaimPaginationDto> paginationData = null;
		RcmIssuClaimPaginationDto paginationDto = null;
		List<com.tricon.rcm.dto.IssueClaimDto> archiveData = null;
		int totalElements = 0;
		int offset = pageNumber * totalRecordsperPage;
		totalElements = userRepo.findCountsOfArchiveClaims(companyId);
		if (totalElements == 0) {
			paginationData = new ArrayList<>();
			paginationDto = new RcmIssuClaimPaginationDto();
			paginationDto.setTotalElements((long) totalElements);
			paginationData.add(paginationDto);
			return paginationData;
		}
		List<IssueClaimDto> pageableList = rcmClaimRepository.archiveClaimsByPagination(companyId, offset,
				totalRecordsperPage);
		if (pageableList != null && !pageableList.isEmpty()) {
			paginationData = new ArrayList<>();
			archiveData = new ArrayList<>();
			paginationDto = new RcmIssuClaimPaginationDto();
			pageableList
					.stream().map(x -> new com.tricon.rcm.dto.IssueClaimDto(x.getClaimId(), x.getIssue(), x.getSource(),
							x.getOfficeName(), x.getCreatedDate(), x.getId(), x.getIsArchive()))
					.forEach(archiveData::add);
			paginationDto.setData(archiveData);
			paginationDto.setPageNumber(pageNumber);
			paginationDto.setTotalElements((long) totalElements);
			paginationDto.setPageSize(totalRecordsperPage);
			paginationDto.setTotalPages((int) Math.ceil((double) totalElements / totalRecordsperPage));
			paginationData.add(paginationDto);
			return paginationData;
		}
		return null;
	}

	@Transactional(rollbackFor = Exception.class)
	public String saveArchiveClaims(RcmArchiveClaimsDto dto, JwtUser jwtUser) throws Exception {
		String msg = null;
		RcmUser updatedBy = null;
		List<Integer> archiveIdTrue = dto.getArchiveClaims().stream().filter(x -> x.getArchiveStatus()== true)
				.map(ArchiveClaimsPayloadDto::getId).collect(Collectors.toList());
		if (!archiveIdTrue.isEmpty()) {
			updatedBy = userRepo.findByEmail(jwtUser.getUsername());
			int status = rcmClaimRepository.updateIssueClaimsArchiveStatus(archiveIdTrue,true, updatedBy);
			if (status > 0) {
				List<RcmIssueClaims> issueClaimData = issueClaimRepo.findByIdInAndResolvedFalse(archiveIdTrue);
				for (RcmIssueClaims c : issueClaimData) {
					if(c.getClaimId().startsWith(c.getId()+Constants.HYPHEN+Constants.ARCHIVE_PREFIX)){
						continue;
					}
					c.setClaimId(c.getId() + Constants.HYPHEN + Constants.ARCHIVE_PREFIX.concat(c.getClaimId()));
					issueClaimRepo.save(c);
				}
				msg = (status > 0) ? MessageConstants.ARCHIVE_CLAIM_SUBMITTED : null;
			}
		}

		return msg;
	}
	
	public List<String> findAssociatedCompanyIdByUserUuid(PartialHeader partialHeader) {
		
		return rcmUserCompanyRepo.findAssociatedCompanyIdByUserUuid(partialHeader.getJwtUser().getUuid());
		
	}
	
    public List<CompanyIdAndNameDto> findAssociatedCompanyWithNameByUserUuid(PartialHeader partialHeader) {
		
		return rcmUserCompanyRepo.findAssociatedCompanyIdWithNameByUserUuid(partialHeader.getJwtUser().getUuid());
		
	}
    
    
    @Transactional(rollbackFor = Exception.class)
    public String archiveActiveClaim(@RequestBody ClaimStatusUpdate dto,PartialHeader partialHeader) {
    	
		 boolean validateClaimRight=checkifCompanyIdMatchesList(partialHeader.getJwtUser().getUuid(),partialHeader.getCompany().getUuid());
			
			if (!validateClaimRight) {
				return null;
		 }
			
    	RcmClaims claim = rcmClaimRepository.findByClaimUuid(dto.getClaimUuid());
    	
    	if (!claim.isPending() && claim.getCurrentStatus()==0) {
    		return "Claim Already Submitted";
	   }
	    if (claim.getCurrentState() ==Constants.CLAIM_ARCHIVE_PREFIX_CANNOT_SUBMITED){
		  return "Claim is Already Archived";
	    }
	    //RcmOffice off = claim.getOffice();
		//RcmCompany rcmCompany = rcmCommonServiceImpl.getCompanyFormParitalHeaderCompanyId(officeRepo.findByUuid(off.getUuid()).getCompany().getUuid(), partialHeader.getCompany());
		if (validateClaimRight) {
			Date date = new Date();
			RcmClaimArchiveHistory history=new RcmClaimArchiveHistory();
			history.setReason(dto.getReason());
			history.setCurrentState(Constants.CLAIM_ARCHIVE_PREFIX_CANNOT_SUBMITED);
			rcmClaimArchiveHistoryRepo.save(history);
			String claimId=date.getTime()+Constants.HYPHEN+Constants.ARCHIVE_PREFIX+claim.getClaimId();
			claim.setClaimId(claimId);
			claim.setCurrentState(Constants.CLAIM_ARCHIVE_PREFIX_CANNOT_SUBMITED);
			rcmClaimRepository.save(claim);
			
		}else return "Wrong Client";
    	return "Claim Archived";
    }
    
    public String UnArchiveActiveClaim(@RequestBody ClaimStatusUpdate dto,PartialHeader partialHeader) {
    	
	    boolean validateClaimRight=checkifCompanyIdMatchesList(partialHeader.getJwtUser().getUuid(),partialHeader.getCompany().getUuid());
			
			if (!validateClaimRight) {
				return null;
		 }
     RcmClaims claim = rcmClaimRepository.findByClaimUuid(dto.getClaimUuid());
    	
    	if (!claim.isPending() && claim.getCurrentStatus()==0) {
    		return "Claim Already Submitted";
	   }
	    if (claim.getCurrentState() == Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED){
		  return "Claim is Already UnArchived";
	    }
	    RcmOffice off = claim.getOffice();
		RcmCompany rcmCompany = rcmCommonServiceImpl.getCompanyFormParitalHeaderCompanyId(officeRepo.findByUuid(off.getUuid()).getCompany().getUuid(), partialHeader.getCompany());
		if (validateClaimRight) {
			
			String claimId=claim.getClaimId().split(Constants.HYPHEN+Constants.ARCHIVE_PREFIX)[1];
			String existingClaimId= rcmClaimRepository.fetchClaimIdByClaimIdAnCompany(claimId,rcmCompany.getUuid(),off.getUuid());
			if (existingClaimId!=null) {
				
				return "Already Exists with Same Claim Id. Can not be UNARCHIVED.";
			}
			//Date date = new Date();
			RcmClaimArchiveHistory history=new RcmClaimArchiveHistory();
			history.setReason(dto.getReason());
			history.setCurrentState(Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED);
			rcmClaimArchiveHistoryRepo.save(history);
			claim.setCurrentState(Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED);
			
			claim.setClaimId(claimId);
			rcmClaimRepository.save(claim);
			
		}else return "Wrong Client";
    	return "Claim UnArchived";
    }
	
	

	@Transactional(rollbackFor = Exception.class)
	public UnArchivedResponseDto saveUnArchivedClaims(UnArchiveClaimDto dto, JwtUser jwtUser,RcmCompany selectedClients) throws Exception {
		UnArchivedResponseDto response = null;
		RcmUser updatedBy = null;
		updatedBy = userRepo.findByEmail(jwtUser.getUsername());
		
		// if select all unchrive then execute above condition
		if (dto.isUnArchiveAll()) {
			response = this.unArchiveAllClaims(selectedClients, updatedBy);
			return response;

		}else {  //if single claim is unarchived then below code is executed
		String[] removePrefix = dto.getClaimId().split(Constants.ARCHIVE_PREFIX);
		if (removePrefix.length < 2) {
			logger.error("Prefix not match of ClaimId>>>>>>>>>>>>>" + dto.getClaimId());
			return UnArchivedResponseDto.builder().message("Prefix not match").unArchiveStatus(false).build();
		}

		Optional<RcmIssueClaims> archivedClaim = rcmIssueClaimsRepo.findById(dto.getId());
		if (!archivedClaim.isPresent()) {
			logger.error("claim does't exist>>>>>>>>>>>>>");
			return UnArchivedResponseDto.builder().message("claim [" +removePrefix[1]+"] does't exist").unArchiveStatus(false).build();
		}

		if (archivedClaim.isPresent() && !archivedClaim.get().isArchive()) {
			logger.error("Already UNARCHIVED>>>>>>>>>>>>>"+archivedClaim.get().getClaimId());
			return UnArchivedResponseDto.builder().message("Already UNARCHIVED Claim is [" +removePrefix[1]+"]").unArchiveStatus(false).build();
		}

		String existingClaim = rcmIssueClaimsRepo.fetchClaimByClaimIdAndCompany(removePrefix[1],
				archivedClaim.get().getOffice().getCompany().getUuid(),archivedClaim.get().getOffice().getUuid());
		if (existingClaim != null) {
			logger.error(MessageConstants.CLAIM_NOT_UNARCHIVED+">>>>"+archivedClaim.get().getClaimId());
			return UnArchivedResponseDto.builder().message(MessageConstants.CLAIM_NOT_UNARCHIVED+" Claim is [" +removePrefix[1]+"]").unArchiveStatus(false).build();
		}
		int status = rcmClaimRepository.updateIssueClaimsUnArchiveStatus(dto.getId(), updatedBy, removePrefix[1]);
		response = status > 0
				? UnArchivedResponseDto.builder().message(MessageConstants.UNARCHIVE_CLAIM_SUBMITTED).unArchiveStatus(true)
						.build()
				: null;
		return response;
	  }
	}
	
	public SearchParamDto getSearchParams() throws Exception {
		Set<String> insuranceNames=new HashSet<String>();
		Set<String> insuranceTypes=new HashSet<String>();
		Set<String> providerTypes=new HashSet<String>();//Specialty
		Set<String> providerNames=new HashSet<String>();
		SearchParamDto searchParamDto= new SearchParamDto();
		//For pulling Insurance name always use SimlePoint.
		RcmCompany companyIns = rcmCompanyRepo.findByName(Constants.COMPANY_NAME);//Uuid(dto.getCompanyuuid());//Always
		List<InsuranceNameTypeDto> insuranceTypeDtoList = ruleEngineService.pullInsuranceMappingFromSheet(companyIns);
		
		for (InsuranceNameTypeDto dto:insuranceTypeDtoList) {
			if (dto.getInsuranceName()==null) continue;
			if (dto.getInsuranceName().equals("")) continue;
			insuranceNames.add(dto.getInsuranceName());
		}
		
		List<RcmInsuranceType> datas= rcmInsuranceTypeRepo.findAll();
		for(RcmInsuranceType data:datas) {
			if (data.getName()==null) continue;
			if (data.getName().equals("")) continue;
			insuranceTypes.add(data.getName());
		}
		
		Object providerSheetData[] = ConnectAndReadSheets.readProviderGSheet(
				Constants.Mapping_Tables, Constants.Mapping_Tables_Provider, CLIENT_SECRET_DIR,
				CREDENTIALS_FOLDER);
		List<ProviderCodeWithSpecialty> proEs = (List<ProviderCodeWithSpecialty>) providerSheetData[0];
		for(ProviderCodeWithSpecialty pro:proEs) {
			if (!pro.getSpecialty().equals("")) providerTypes.add(pro.getSpecialty());
			if (!pro.getProviderNames().equals("")) providerNames.add(pro.getProviderNames());
		}
		searchParamDto.setInsuranceNames(insuranceNames);
		searchParamDto.setInsuranceTypes(insuranceTypes);
		searchParamDto.setProviderNames(providerNames);
		searchParamDto.setProviderTypes(providerTypes);
		
		return searchParamDto;
	}
	
	
	private  boolean checkifCompanyIdMatchesList(String userid,String userCompanyId) {
		List<String> companies= rcmUserCompanyRepo.findAssociatedCompanyIdByUserUuid(userid);
		return ClaimUtil.checkifCompanyIdMatchesList(userCompanyId, companies);
	}
	
	private UnArchivedResponseDto unArchiveAllClaims(RcmCompany selectedClients, RcmUser updatedBy) throws Exception {
		UnArchivedResponseDto response = null;
		String associatedClientsFromClaims = selectedClients.getUuid();
		List<IssueClaimDto> listOfUnarchiveClaims = rcmIssueClaimsRepo
				.fetchAllUnarchiveClaimAssociatedClient(associatedClientsFromClaims);
		int status = 0;
		if (!listOfUnarchiveClaims.isEmpty()) {
			for (IssueClaimDto data : listOfUnarchiveClaims) {
				status = 0;
				String[] removePrefix = data.getClaimId().split(Constants.ARCHIVE_PREFIX);
				String existingClaim = rcmIssueClaimsRepo.fetchClaimByClaimIdAndCompany(removePrefix[1],
						associatedClientsFromClaims,data.getOfficeUuid());
				if (removePrefix.length < 2) {
					logger.error("Prefix not match of ClaimId>>>>>>>>>>>>>" + data.getClaimId());
					break; // if any condition is unmatch then show error claim in ui for error
				} else if (!data.getIsArchive()) {
					logger.error("Already UNARCHIVED>>>>>>>>>>>>>" + data.getClaimId());
					return UnArchivedResponseDto.builder().message("Already UNARCHIVED Claim is [" +removePrefix[1]+"]").unArchiveStatus(false).build();
				} else if (existingClaim != null) {
					logger.error(MessageConstants.CLAIM_NOT_UNARCHIVED + ">>>>" + data.getClaimId());
					return UnArchivedResponseDto.builder().message(MessageConstants.CLAIM_NOT_UNARCHIVED+" Claim is [" +removePrefix[1]+"]")
							.unArchiveStatus(false).build();
				} else {
					status = rcmClaimRepository.updateIssueClaimsUnArchiveStatus(data.getId(), updatedBy,
							removePrefix[1]);
					if (status == 0) {
						logger.error("Error in ClaimId>>>>>>>>>>>>>" + data.getClaimId());
						break;
					}
				}
			}
			response = status > 0
					? UnArchivedResponseDto.builder().message(MessageConstants.UNARCHIVE_CLAIM_SUBMITTED)
							.unArchiveStatus(true).build()
					: null;
		} else {
			response = UnArchivedResponseDto.builder().message("All Claims Are Unarchived Already")
					.unArchiveStatus(true).build();

		}
		return response;
	}

	public UnArchivedResponseDto unArchiveAllClaimsByIds(RcmUnarchiveClaimsDto dto, JwtUser jwtUser) throws Exception {
		UnArchivedResponseDto response = null;
		UnArchiveClaimDto unarchiveDto = null;
		if (!dto.getUnarchiveClaims().isEmpty()) {
			for (UnarchiveClaimsPayloadDto data : dto.getUnarchiveClaims()) {
				unarchiveDto = new UnArchiveClaimDto();
				unarchiveDto.setClaimId(data.getClaimId());
				unarchiveDto.setId(data.getId());
				unarchiveDto.setUnArchiveAll(false);
				response = this.saveUnArchivedClaims(unarchiveDto, jwtUser, null);
				if (response == null || !response.getUnArchiveStatus()) {
					break;
				}
			}
		}
		return response;
	}

	public RcmResponseMessageDto findTeamLeadExistForOtherTeams(FindTLExistDto dto, JwtUser jwtUser) throws Exception {
		RcmResponseMessageDto responseDto = new RcmResponseMessageDto();
		RcmUser user = userRepo.findByEmail(jwtUser.getUsername());
		RcmClaims claim = claimRepo.findByClaimUuid(dto.getClaimUuid());
		if (!claim.isPending() && claim.getCurrentStatus()==0) {
			responseDto.setResponseStatus(false);
			return responseDto;
		}
        RcmOffice office= officeRepo.findByUuid(claim.getOffice().getUuid());
		String clientUuidAssociatedWithClaims = office.getCompany().getUuid();
		
		// match given client with loggedin user client
		List<String> companies = rcmUserCompanyRepo.findAssociatedCompanyIdByUserUuid(user.getUuid());
		boolean isValidClient = companies.contains(clientUuidAssociatedWithClaims);
		if (isValidClient) {
			int exitingTLUserCounts = rcmUserCompanyRepo.findExistingTLByClientUuidAndTeamAndOffice(clientUuidAssociatedWithClaims,
					dto.getAssignToTeamId(),office.getUuid());
			if (exitingTLUserCounts == 0) {
				logger.info("For Client:" + clientUuidAssociatedWithClaims + ",TL Not exist for team Id:"
						+ RcmTeamEnum.getTeamDescriptionByTeamId(dto.getAssignToTeamId()));
				responseDto.setResponseStatus(false);
				responseDto.setMessage("For this client (" + companyRepo.findByUuid(clientUuidAssociatedWithClaims).getName()
						+ "),and office ("+office.getName()+") no Team Lead or Associate exist for "+RcmTeamEnum.getTeamDescriptionByTeamId(dto.getAssignToTeamId())+". Please make Team Lead or Associate first for missing team and then assign to other team.");
				return responseDto;
			} else {
				responseDto.setResponseStatus(true);
				return responseDto;
			}
		}
		return responseDto;
	}
	
	public String claimsDataAccordingToCountsType(ListOfClaimsCountsDto requestDto) {
		if (!StringUtils.isNoneBlank(requestDto.getPageName())
				&& !Constants.PAGE_NAME.contains(requestDto.getPageName())) {
			return "Page Not match";
		}
		if ((requestDto.getPageName().equals("Pendancy") && requestDto.getCountType() == null)
				|| (requestDto.getPageName().equals("Pendancy")
						&& !Constants.COUNT_TYPE.contains(requestDto.getCountType()))) {
			return "Count Type Not match";
		}
		if (!requestDto.getPageName().equals("Pendancy") && requestDto.getTeamId() == null) {
			return MessageConstants.TEAM_REQUIRED;
		}
		return null;
	}
	
	
	public boolean saveClaimSectionDatAfterSubmission(CommonSectionsRequestBodyDto sectionRequestBody,
			PartialHeader partialHeader) throws Exception {

		Boolean response = false;
		boolean allSectionAccess = true;
		boolean allCheckValidation = true;
		boolean validateClaimRight = checkifCompanyIdMatchesList(partialHeader.getJwtUser().getUuid(),
				partialHeader.getCompany().getUuid());
		if (!validateClaimRight) {
			logger.error("claim not valid");
			return false;
		}
		RcmClaims claim = rcmClaimRepository.findByClaimUuid(sectionRequestBody.getClaimUuid());

		RcmClaimAssignment assign = rcmClaimAssignmentRepo.findByAssignedToUuidAndClaimsClaimUuidAndActive(
				partialHeader.getJwtUser().getUuid(), claim.getClaimUuid(), true);
		if (assign == null) {
			logger.error("claim not assigned to this user");
			// Not assigned to user
			return false;
		}

		List<SectionDto> sectionsData = masterService.getSections();

		RcmUser createdBy = userRepo.findByUuid(partialHeader.getJwtUser().getUuid());

		RcmTeam team = rcmTeamRepo.findById(partialHeader.getTeamId());

		for (Field field : CommonSectionsRequestBodyDto.class.getDeclaredFields()) {
			if (field.getName().equals("claimUuid") || field.getName().equals("finalSubmit") || field.getName().equals("moveToNextTeam")) {
				continue;
			} else {
				field.setAccessible(true);
				int sectionId=0;
				Object value = field.get(sectionRequestBody); // Get the value of the field
				if (value != null) {
					Field f = value.getClass().getDeclaredField("sectionId");
					f.setAccessible(true);
					if (f.get(value) instanceof Integer) {
						sectionId = (int) f.get(value);
					}
					boolean sectionAccess = rcmCommonServiceImpl.validateUserSectionAccess(partialHeader,
							sectionId,createdBy);
					if (!sectionAccess) {
						logger.error("section access denied!");
						allSectionAccess = false;
						response=false;
						break;
					}
					if (!sectionRequestBody.isFinalSubmit() && allSectionAccess) {
						response = rcmCommonServiceImpl.saveCommonSectionInformations(sectionRequestBody, partialHeader,
								sectionId, sectionsData, createdBy, claim, team);
						if (!response) break;
					} else {
						boolean checkValidation = rcmCommonServiceImpl.commonSectionInformationsForAllWithValidation(
								sectionRequestBody, partialHeader, sectionId, sectionsData);
						if (!checkValidation) {
							logger.error("validation fail!");
							response=false;
							allCheckValidation = false;
							break;
						}
					}
				}
			}
		}

		if (sectionRequestBody.isFinalSubmit() && allCheckValidation && allSectionAccess) {
			response = false;
			for (Field field : CommonSectionsRequestBodyDto.class.getDeclaredFields()) {
				if (field.getName().equals("claimUuid") || field.getName().equals("finalSubmit")|| field.getName().equals("moveToNextTeam")) {
					continue;
				} else {
					field.setAccessible(true);
					int sectionId=0;
					Object value = field.get(sectionRequestBody); // Get the value of the field
					if (value != null) {
						Field f = value.getClass().getDeclaredField("sectionId");
						f.setAccessible(true);
						if (f.get(value) instanceof Integer) {
							sectionId = (int) f.get(value);
						}
						response = rcmCommonServiceImpl.saveCommonSectionInformations(sectionRequestBody,
								partialHeader, sectionId, sectionsData, createdBy, claim, team);
						logger.info("save section  response->" + response);
						if (!response)
							break;
					}
				}
			}
			if (response && sectionRequestBody.isMoveToNextTeam() ) {
				RcmOffice office = officeRepo.findByUuid(claim.getOffice().getUuid());
				RcmTeamDto nextTeam = NextTeamClaimTransferUtil.getNextTeam(partialHeader.getTeamId());
				String claimTransfer=assignClaimToOtherTeamWithRemarkCommon(partialHeader, sectionRequestBody.getClaimUuid(),
						nextTeam.getTeamId(), "Please Work on Claim", claim, assign, createdBy, office, null);
				logger.info("claim transfer response->" + claimTransfer);
			}
		}
		return response;

	}
	
     public EobLink saveEobLink(EobLink dto,PartialHeader partialHeader) {
    	
		    boolean validateClaimRight=checkifCompanyIdMatchesList(partialHeader.getJwtUser().getUuid(),partialHeader.getCompany().getUuid());
				if (!validateClaimRight) {
					return null;
			 }
	        RcmClaims claim = rcmClaimRepository.findByClaimUuid(dto.getClaimUuid());
	        if (claim.getCurrentState() == Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED){
		    	dto.setStatus("Claim is Already UnArchived");
		    	dto.setSaved(false);
		    	return dto;
		    }
		    if (validateClaimRight) {
				RcmClaimAssignment assign = rcmClaimAssignmentRepo.findByAssignedToUuidAndClaimsClaimUuidAndActive(
						partialHeader.getJwtUser().getUuid(), claim.getClaimUuid(), true);
				if (assign == null) {
					dto.setStatus("claim not assigned to this user");
					// Not assigned to user
					dto.setSaved(false);
					return dto;
				}else {
					String fileName=dto.getClaimUuid()+new Date().getTime()+"."+dto.getExtension();
					try {
						FileUtils.copyURLToFile(
						new URL(dto.getLink()),  new File(eobLinkFolder+"/"+fileName), 60000, 60000);
						dto.setSaved(true);
						dto.setStatus(fileName);
					}catch(Exception c) {
						c.printStackTrace();
						dto.setSaved(false);
						dto.setStatus(c.getMessage());
					}
				}
			
			}else {
				dto.setSaved(false);
				dto.setStatus("Wrong Client");
				return dto;
			}
			return dto;
    }
     
    public InputStream viewEobLink(String fileName,PartialHeader partialHeader) {
	  File initialFile = new File(eobLinkFolder+File.separator+fileName+".pdf");
      InputStream targetStream=null ;
		try {
			targetStream = new FileInputStream(initialFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		   return targetStream;
	 }
//	public List<FreshClaimDataViewDto> fetchSubmittedClaimDetails(PartialHeader partialHeader) throws Exception {
//		String selectedtRole = partialHeader.getRole();
//		int selectedTeamId = partialHeader.getTeamId();
//		List<FreshClaimDataDto> list = null;
//		List<FreshClaimDataViewDto> listView = new ArrayList<>();
//		if (selectedtRole.equals(Constants.ASSOCIATE)) {
//			if (selectedTeamId != RcmTeamEnum.BILLING.getId() && selectedTeamId != RcmTeamEnum.INTERNAL_AUDIT.getId()) {
//				list = rcmClaimRepository.fetchFreshClaimDetailsOtherTeamInd(partialHeader.getCompany().getUuid(),
//						selectedTeamId, partialHeader.getJwtUser().getUuid());
//			} else {
//				list = rcmClaimRepository.fetchFreshClaimDetailsInd(partialHeader.getCompany().getUuid(),
//						selectedTeamId, partialHeader.getJwtUser().getUuid());
//
//			}
//		} else {
//			if (selectedTeamId != RcmTeamEnum.BILLING.getId() && selectedTeamId != RcmTeamEnum.INTERNAL_AUDIT.getId()) {
//				list = rcmClaimRepository.fetchFreshClaimDetailsOtherTeam(partialHeader.getCompany().getUuid(),
//						selectedTeamId);
//			} else {
//				list = rcmClaimRepository.fetchFreshClaimDetails(partialHeader.getCompany().getUuid(), selectedTeamId);
//
//			}
//		}
//		if (selectedTeamId == RcmTeamEnum.BILLING.getId()) {
//			if (list == null)
//				list = new ArrayList<>();
//			// add Claims Send From Internal Audit Team
//		}
//
//		list.forEach(data -> {
//			final FreshClaimDataViewDto dataView = new FreshClaimDataViewDto();
//			BeanUtils.copyProperties(data, dataView);
//			listView.add(dataView);
//		});
//		if (selectedTeamId != RcmTeamEnum.BILLING.getId() && selectedTeamId != RcmTeamEnum.INTERNAL_AUDIT.getId()) {
//			// Need to get Claim remark in of non billing and internal audit
//
//			listView.forEach(data -> {
//				data.setLastTeamRemark(
//						rcmClaimAssignmentRepo.findLatestClaimCommentByOtherTeam(data.getUuid(), selectedTeamId));
//			});
//		}
//
//		return listView;
//	}
}
