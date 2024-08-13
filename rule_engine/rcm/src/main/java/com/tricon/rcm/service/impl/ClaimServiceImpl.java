package com.tricon.rcm.service.impl;


import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import org.apache.commons.collections4.CollectionUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.bind.annotation.RequestBody;

import com.google.common.collect.Collections2;
import com.tricon.rcm.db.entity.ClaimCycle;
import com.tricon.rcm.db.entity.CurrentClaimStatusAndNextAction;
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
import com.tricon.rcm.db.entity.RcmInsuranceTypeDateMapping;
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
import com.tricon.rcm.dto.AssignOfficeResponseDto;
import com.tricon.rcm.dto.AutoRunClaimReponseDto;
import com.tricon.rcm.dto.CaplineIVFFormDto;
import com.tricon.rcm.dto.ClaimAssignDto;
import com.tricon.rcm.dto.ClaimAssignWithRemarkAndTeam;
import com.tricon.rcm.dto.ClaimDataDetails;
import com.tricon.rcm.dto.ClaimDetailDto;
import com.tricon.rcm.dto.ClaimEditDetailDto;
import com.tricon.rcm.dto.ClaimEditDto;
import com.tricon.rcm.dto.KeyValueDto;
import com.tricon.rcm.dto.LinkedClaimResponseDto;
import com.tricon.rcm.dto.ListOfClaimsCountsDto;
import com.tricon.rcm.dto.ListOfClaimsDto;
import com.tricon.rcm.dto.PartialHeader;
import com.tricon.rcm.dto.PendencyDataCountDto;
import com.tricon.rcm.dto.PendencyKeyValDto;
import com.tricon.rcm.dto.PendencyWithOfficeOnlyDto;
import com.tricon.rcm.dto.ProductionAgeWiseDto;
import com.tricon.rcm.dto.ProductionCurrentStatusWiseDto;
import com.tricon.rcm.dto.ProductionDispositionWiseDto;
import com.tricon.rcm.dto.ProductionDtoFrorAging;
import com.tricon.rcm.dto.ProductionForCDP;
import com.tricon.rcm.dto.ProductionStatementTypeWiseDto;
import com.tricon.rcm.dto.ProductionStatementTypeWiseDto.StatementType;
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
import com.tricon.rcm.dto.ClaimReconcillationDto;
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
import com.tricon.rcm.dto.CurrentStatusAndNextActionDto;
import com.tricon.rcm.dto.Discrepancy;
import com.tricon.rcm.dto.EobLink;
import com.tricon.rcm.dto.FindTLExistDto;
import com.tricon.rcm.dto.FreshClaimDataImplDto;
import com.tricon.rcm.dto.FreshClaimDataViewDto;
import com.tricon.rcm.dto.InsuranceNameTypeDto;
import com.tricon.rcm.dto.customquery.FreshClaimLogDto;
import com.tricon.rcm.dto.customquery.IVFDto;
import com.tricon.rcm.dto.customquery.IssueClaimDto;
import com.tricon.rcm.dto.customquery.LinkedClaimDto;
import com.tricon.rcm.dto.customquery.ProductionDto;
import com.tricon.rcm.dto.customquery.ProductionForAging;
import com.tricon.rcm.dto.customquery.ProductionForPatientCalling;
import com.tricon.rcm.dto.customquery.ProductionForPatientStatement;
import com.tricon.rcm.dto.customquery.RcmClaimDetailDto;
import com.tricon.rcm.dto.customquery.RcmClaimNoteDto;
import com.tricon.rcm.dto.customquery.RcmClaimSubmissionDto;
import com.tricon.rcm.dto.customquery.ReconcillationClaimDto;
import com.tricon.rcm.dto.customquery.RuleEngineClaimDto;
import com.tricon.rcm.dto.RcmOfficeDto;
import com.tricon.rcm.dto.RcmResponseMessageDto;
import com.tricon.rcm.dto.RcmTeamDto;
import com.tricon.rcm.dto.RcmUnarchiveClaimsDto;
import com.tricon.rcm.dto.ReconciliationDto;
import com.tricon.rcm.dto.ReconciliationResponseDto;
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
import com.tricon.rcm.dto.customquery.AssignOfficeDto;
import com.tricon.rcm.dto.customquery.ClaimRemarksDto;
import com.tricon.rcm.dto.customquery.ClaimRuleRemarksDto;
import com.tricon.rcm.dto.customquery.ClaimRuleValidationDto;
import com.tricon.rcm.dto.customquery.ClaimSteps;
import com.tricon.rcm.dto.customquery.ClaimTransferDto;
import com.tricon.rcm.dto.customquery.CompanyIdAndNameDto;
import com.tricon.rcm.dto.customquery.DataPatientRuleDto;
import com.tricon.rcm.dto.customquery.FreshClaimDataDto;
import com.tricon.rcm.dto.customquery.FreshClaimDetailsDto;
import com.tricon.rcm.dto.customquery.FreshClaimDetailsImplDto;
import com.tricon.rcm.enums.ClaimSourceEnum;
import com.tricon.rcm.enums.ClaimStatusEnum;
import com.tricon.rcm.enums.ClaimStatusSearchEnum;
import com.tricon.rcm.enums.ClaimTypeEnum;
import com.tricon.rcm.enums.DispositionEnumForProduction;
import com.tricon.rcm.enums.InsuranceTypeEnum;
import com.tricon.rcm.enums.RcmRoleEnum;
import com.tricon.rcm.enums.RcmTeamEnum;
import com.tricon.rcm.jpa.repository.ClaimCycleRepo;
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
import com.tricon.rcm.jpa.repository.RcmCurrentClaimStatusRepo;
import com.tricon.rcm.jpa.repository.RcmInsuranceRepo;
import com.tricon.rcm.jpa.repository.RcmInsuranceTypeDateMappingRepo;
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
import com.tricon.rcm.util.ClaimMovementUtil;
import com.tricon.rcm.util.ClaimUtil;
import com.tricon.rcm.util.ConnectAndReadSheets;
import com.tricon.rcm.util.Constants;
import com.tricon.rcm.util.MessageConstants;
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
	RcmCompanyRepo companyRepo;
	
	@Autowired
	MasterServiceImpl masterService;
	
	@Autowired
	ClaimCycleServiceImpl claimCycleService;
	
	@Autowired
	RcmClaimLogServiceImpl rcmClaimLogServiceImpl;
	
	@Autowired
	UserAssignOfficeRepo userAssignRepo;
	
	@Autowired
	RcmInsuranceTypeDateMappingRepo insuranceTypeDateMappingRepo;
	
	@Lazy
	@Autowired
	ClaimSectionImpl claimSectionImpl;
	
	@Autowired
	RcmCurrentClaimStatusRepo currentStatusAndNextActionRepo;
	
	@Autowired
	ClaimCycleRepo clamCycleRepo;
	

	private final Logger logger = LoggerFactory.getLogger(ClaimServiceImpl.class);

	@Transactional(rollbackFor = Exception.class)
	public Object pullClaimFromSource(ClaimSourceDto dto, RcmUser user, PartialHeader partialHeader) {
		// go to Rule Engine.
		Object status = null;
		RcmTeam currentTeam = rcmTeamRepo.findById(partialHeader.getTeamId());
		if (dto.getSource().equals(ClaimSourceEnum.GOOGLESHEET.toString())) {
			try {
				if (user == null) {
					user = userRepo.findByUuid(partialHeader.getJwtUser().getUuid());
				}
				status = pullAndSaveClaimFromSheet(dto, user,currentTeam);
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
					data = ruleEngineService.pullAndSaveClaimFromRE(d, user, li, ClaimTypeEnum.P, partialHeader.getCompany(),currentTeam,
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
							currentTeam,logId); // (d, user,li);
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

	public Object pullAndSaveClaimFromSheet(ClaimSourceDto dto, RcmUser user,RcmTeam currentTeam) {

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
							if (ins!=null && ins.getInsuranceType() ==null) {
								rcmInsuranceType = rcmInsuranceTypeRepo.findByName(re.getInsuranceName());
								ins.setInsuranceType(rcmInsuranceType);
								insuranceRepo.save(ins);
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
							boolean isFCL= false;
							if (clCompany.getName().equals(Constants.COMPANY_NAME)) {
								isBilling = ClaimUtil.isBillingClaimByInsuranceName(rcmInsuranceType.getName());
								isMedicaid=ClaimUtil.isMedcaidClaimByInsuranceName(rcmInsuranceType.getName());
								isMedicare=ClaimUtil.isMedicareClaimByInsuranceName(ins.getInsuranceType().getName());
								isChip=ClaimUtil.isChipClaimByInsuranceName(ins.getInsuranceType().getName());
								isFCL=ClaimUtil.isFCLClaimByInsuranceName(ins.getInsuranceType().getName(),ins.getName());
							}
							boolean missing=true;
							if (isBilling || isMedicare ) {
							claim = ClaimUtil.createClaimFromSheetData(claim, off, re,
									ClaimUtil.filterTeamByNameId(allTeams, RcmTeamEnum.BILLING.toString()), user, ins,
									ins, claimStatusType, claimTypeEnum.getSuffix(), rcmInsuranceType, timely.getTimelyFilingLimit(),""
									,claimTypeEnum,ClaimSourceEnum.GOOGLESHEET.toString());
							missing=false;
							}
							if (  isMedicaid || isChip || isFCL) {
								claim = ClaimUtil.createClaimFromSheetData(claim, off, re,
										ClaimUtil.filterTeamByNameId(allTeams, RcmTeamEnum.INTERNAL_AUDIT.toString()), user, ins,
										ins, claimStatusType, claimTypeEnum.getSuffix(), rcmInsuranceType, timely.getTimelyFilingLimit(),
										"",claimTypeEnum,ClaimSourceEnum.GOOGLESHEET.toString());
								missing=false;
							}
							if(missing) {//no Billing or Medicaid
								//put in billing 
								claim = ClaimUtil.createClaimFromSheetData(claim, off, re,
										ClaimUtil.filterTeamByNameId(allTeams, RcmTeamEnum.BILLING.toString()), user, ins,
										ins, claimStatusType, claimTypeEnum.getSuffix(), rcmInsuranceType, timely.getTimelyFilingLimit(),
										"",claimTypeEnum,ClaimSourceEnum.GOOGLESHEET.toString());
								missing=false;
								isBilling=true;
							}
							claim.setCurrentStatus(ClaimStatusEnum.Claim_Uploaded.getId());	
							String claimUUid = rcmClaimRepository.save(claim).getClaimUuid();
							RcmTeam systemTeam=rcmTeamRepo.findByNameId(RcmTeamEnum.SYSTEM.getName());
							claimCycleService.createNewClaimCycle(claim,ClaimStatusEnum.Claim_Uploaded.getType(),null,systemTeam, user);
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
									det.setEstInsurance("0");
									det.setEstPrimary("0");
									det.setFee("0");
									det.setIdEs("-1");
									det.setLineItem("-1");
									det.setPatientPortion("0");
									det.setPatientPortionSec("0");
									det.setProviderLastName("");
									det.setServiceCode(cds.trim());
									det.setStatus("");
									
									det.setSurface("");
									det.setTooth("");
									det.setActive(true);
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
							if (assignedUserBilling != null && (isBilling || isMedicare )) {
								rcmAssigment = new RcmClaimAssignment();
								//
								rcmAssigment = ClaimUtil.createAssginmentData(rcmAssigment, user,
										assignedUserBilling.getUser(), claimUUid, claim, "",
										systemStatusBilling,assignedTeamBilling,Constants.SYSTEM_INITIAL_COMMENT);

								rcmClaimAssignmentRepo.save(rcmAssigment);
								claim.setCurrentStatus(ClaimStatusEnum.Pending_For_Billing.getId());
								claim.setNextAction(ClaimStatusEnum.Need_to_Bill.getId());
								rcmClaimRepository.updateClaimCurrentStatusWithAction(ClaimStatusEnum.Pending_For_Billing.getId(),ClaimStatusEnum.Need_to_Bill.getId(),claim.getClaimUuid());
								
								claimCycleService.createNewClaimCycle(claim, ClaimStatusEnum.Pending_For_Billing.getType(),ClaimStatusEnum.Need_to_Bill.getType(), assignedTeamBilling, user);
							}
							if (assignedUserInternalAudit != null && (isMedicaid  || isChip || isFCL)) {
								rcmAssigment = new RcmClaimAssignment();
								//
								rcmAssigment = ClaimUtil.createAssginmentData(rcmAssigment, user,
										assignedUserInternalAudit.getUser(), claimUUid, claim, "",
										systemStatusBilling,assignedTeamInternalAudit,Constants.SYSTEM_INITIAL_COMMENT);

								rcmClaimAssignmentRepo.save(rcmAssigment);
								claim.setCurrentStatus(ClaimStatusEnum.Pending_For_Review.getId());
								claim.setNextAction(ClaimStatusEnum.Need_to_Audit.getId());
								rcmClaimRepository.updateClaimCurrentStatusWithAction(ClaimStatusEnum.Pending_For_Review.getId(),ClaimStatusEnum.Need_to_Audit.getId(),claim.getClaimUuid());
								
								claimCycleService.createNewClaimCycle(claim, ClaimStatusEnum.Pending_For_Review.getType(),ClaimStatusEnum.Need_to_Audit.getType(), assignedTeamInternalAudit, user);
								
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
							if (ins!=null && ins.getInsuranceType() ==null) {
								rcmInsuranceType = rcmInsuranceTypeRepo.findByName(re.getInsuranceName());
								ins.setInsuranceType(rcmInsuranceType);
								insuranceRepo.save(ins);
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
							boolean isFCL= false;
							//For External we always have claims in Billing 
							if (clCompany.getName().equals(Constants.COMPANY_NAME)) {
								isBilling = ClaimUtil.isBillingClaimByInsuranceName(rcmInsuranceType.getName());
								isMedicaid=ClaimUtil.isMedcaidClaimByInsuranceName(rcmInsuranceType.getName());
								isMedicare=ClaimUtil.isMedicareClaimByInsuranceName(ins.getInsuranceType().getName());
								isChip=ClaimUtil.isChipClaimByInsuranceName(ins.getInsuranceType().getName());
								isFCL=ClaimUtil.isFCLClaimByInsuranceName(ins.getInsuranceType().getName(),ins.getName());
							}
							
							//boolean isBilling=ClaimUtil.isBillingClaimByInsuranceName(rcmInsuranceType.getName());
							//boolean isMedicaid=ClaimUtil.isMedcaidClaimByInsuranceName(rcmInsuranceType.getName());
							//boolean isMedicare=ClaimUtil.isMedicareClaimByInsuranceName(ins.getInsuranceType().getName());
							boolean missing=true;
							if (isBilling || isMedicare) {
							
							claim = ClaimUtil.createClaimFromSheetData(claim, off, re,
									ClaimUtil.filterTeamByNameId(allTeams, RcmTeamEnum.BILLING.toString()), user, ins,
									ins, claimStatusType, claimTypeEnum.getSuffix(), rcmInsuranceType, timely.getTimelyFilingLimit(),"",
									claimTypeEnum,ClaimSourceEnum.GOOGLESHEET.toString());
							missing=false;
							}
							if (isMedicaid  || isChip || isFCL) {
								claim = ClaimUtil.createClaimFromSheetData(claim, off, re,
										ClaimUtil.filterTeamByNameId(allTeams, RcmTeamEnum.INTERNAL_AUDIT.toString()), user, ins,
										ins, claimStatusType, claimTypeEnum.getSuffix(), rcmInsuranceType, timely.getTimelyFilingLimit(),
										"",claimTypeEnum,ClaimSourceEnum.GOOGLESHEET.toString());
								missing=false;
							}
							if(missing) {//no Billing or Medicaid
								//put in billing 
								claim = ClaimUtil.createClaimFromSheetData(claim, off, re,
										ClaimUtil.filterTeamByNameId(allTeams, RcmTeamEnum.BILLING.toString()), user, ins,
										ins, claimStatusType, claimTypeEnum.getSuffix(), rcmInsuranceType, timely.getTimelyFilingLimit(),
										"",claimTypeEnum,ClaimSourceEnum.GOOGLESHEET.toString());
								missing=false;
								isBilling=true;
							}
							
							
							
							claim.setCurrentStatus(ClaimStatusEnum.Claim_Uploaded.getId());
							String claimUUid = rcmClaimRepository.save(claim).getClaimUuid();
							RcmTeam systemTeam=rcmTeamRepo.findByNameId(RcmTeamEnum.SYSTEM.getName());
							claimCycleService.createNewClaimCycle(claim,ClaimStatusEnum.Claim_Uploaded.getType(),null, systemTeam, user);
							//Save Data in rcm_claim_detail (new Enhancement)
							if (re.getServiceCodes().size()>0) {
								RcmClaimDetail det=null;
								int serviceCount= 0;
								for(String cds:re.getServiceCodes()) {
									det =new RcmClaimDetail();
									List<String> toothSurfaces=re.getToothAndSurfaces();
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
									det.setActive(true);
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
							if (assignedUserBilling != null && (isBilling || isMedicare)) {
								rcmAssigment = new RcmClaimAssignment();
								//
								rcmAssigment = ClaimUtil.createAssginmentData(rcmAssigment, user,
										assignedUserBilling.getUser(), claimUUid, claim, "",
										systemStatusBilling,assignedTeamBilling,Constants.SYSTEM_INITIAL_COMMENT);

								rcmClaimAssignmentRepo.save(rcmAssigment);
								claim.setCurrentStatus(ClaimStatusEnum.Pending_For_Billing.getId());
								claim.setNextAction(ClaimStatusEnum.Need_to_Bill.getId());
								rcmClaimRepository.updateClaimCurrentStatusWithAction(ClaimStatusEnum.Pending_For_Billing.getId(),ClaimStatusEnum.Need_to_Bill.getId(),claim.getClaimUuid());
								
								claimCycleService.createNewClaimCycle(claim, ClaimStatusEnum.Pending_For_Billing.getType(), ClaimStatusEnum.Need_to_Bill.getType(), assignedTeamBilling, user);
								
							}
							if (assignedUserInternalAudit != null && ( isMedicaid || isChip || isFCL)) {
								rcmAssigment = new RcmClaimAssignment();
								//
								rcmAssigment = ClaimUtil.createAssginmentData(rcmAssigment, user,
										assignedUserInternalAudit.getUser(), claimUUid, claim, "",
										systemStatusBilling,assignedTeamInternalAudit,Constants.SYSTEM_INITIAL_COMMENT);

								rcmClaimAssignmentRepo.save(rcmAssigment);
								claim.setCurrentStatus( ClaimStatusEnum.Pending_For_Review.getId());
								claim.setNextAction(ClaimStatusEnum.Need_to_Audit.getId());
								rcmClaimRepository.updateClaimCurrentStatusWithAction(ClaimStatusEnum.Pending_For_Review.getId(),ClaimStatusEnum.Need_to_Audit.getId(),claim.getClaimUuid());
								
								claimCycleService.createNewClaimCycle(claim, ClaimStatusEnum.Pending_For_Review.getType(),ClaimStatusEnum.Need_to_Audit.getType(), assignedTeamInternalAudit, user);
								
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
	
	public List<String> validSecondaryClaimDataFromRecreateSection(ClaimFromSheet re,
			String companyuuid,String claimUuid) {


		logger.info(" In Validate Secondary Claim From Recreate Claim section");
		RcmClaims currentClaim = rcmClaimRepository.findByClaimUuid(claimUuid);
		String claim[]=currentClaim.getClaimId().split(ClaimTypeEnum.P.getSuffix());
		re.setClaimId(claim[0]);
		List<String> error = new ArrayList<>();
		// Map<String, Object[]> mapcountNew = new HashMap<>();
		// Map<String,String> messages= new HashMap<>();
		// Map<String,int[]> mapcountNewP= new HashMap<>();
		// Map<String,int[]>mapcountNewS= new HashMap<>();
		RcmCompany company = rcmCompanyRepo.findByUuid(companyuuid);
		re.setClientName(company.getName());
		List<RcmTeam> allTeams = rcmTeamRepo.findAll();
		InsuranceNameTypeDto insuranceNameTypeDto=null;
		RcmCompany companyIns = rcmCompanyRepo.findByName(Constants.COMPANY_NAME);//Always
		Map<String,List<InsuranceNameTypeDto>> insuranceTypeDtos =new HashMap();// ruleEngineService.pullInsuranceMappingFromSheet(companyIns);
		List<TimelyFilingLimitDto> timelyFilingLimits = ruleEngineService.pullTimelyFilingLmtMappingFromSheet(company);
		RcmClaimStatusType systemStatusBilling = rcmClaimStatusTypeRepo.findByStatus(ClaimStatusEnum.Billing.getType());
		RcmClaimStatusType systemStatusReBilling = rcmClaimStatusTypeRepo
				.findByStatus(ClaimStatusEnum.ReBilling.getType());

		List<InsuranceNameTypeDto> insuranceTypeDto =null;
		// ruleEngineService.pullInsuranceMappingFromSheet(company);
		// int logId=-1;
		//RcmMappingTable table = rcmMappingTableRepo.findByNameAndCompany(Constants.RCM_MAPPING_RCM_DATABASE, company);
		HashMap<String, RcmCompany> companies = new HashMap<>();
		HashMap<String, RcmInsuranceType> rcmInsuranceTypes = new HashMap<>();
		RcmInsuranceType rcmInsuranceType = null;
		RcmCompany clCompany = null;
		RcmInsurance ins = null;
		//List<String> offNames = null;
		//List<String> offNameKeys = null;

		//offNames = new ArrayList<>();
		//offNameKeys = new ArrayList<>();
		RcmOffice  rcmOffice = officeRepo.findByUuid(currentClaim.getOffice().getUuid());
		re.setOfficeName(rcmOffice.getName());
		// rcmOffices.stream().map(RcmOffice::getName).forEach(offNames::add);
		//offNames.add(rcmOffice.getName());
		//offNameKeys.add(rcmOffice.getName() + rcmOffice.getKey());
	    try {
			
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
								error.add(
										"Wrong Client Name-" + re.getClientName());
								return error;
							}
						}

						RcmOffice off = officeRepo.findByCompanyAndName(companies.get(re.getClientName()),
								re.getOfficeName());
						if (off == null) {
							error.add("Wrong Office Name-" + re.getOfficeName() + "For " + re.getClientName());
							
							return error;
						}

						List<String> allCl = Arrays.asList(re.getClaimId() + claimTypeEnum.getSuffix());
						List<RcmClaims> claims = rcmClaimRepository.findByClaimIdInAndOffice(allCl, off);
						
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
									error.add("Primary not Present");
                                    return error;
								}
							}

							
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
								
						}

					} catch (Exception clai) {
						// success = n1.getMessage();
						clai.printStackTrace();
						logger.error(clai.getMessage());
						error.add(clai.getMessage());
					}

		} catch (Exception n) {
			n.printStackTrace();
			logger.error("Error in Fetching/Creating  Claims From Recreate section.. ");
			logger.error(n.getMessage());
			error.add(n.getMessage());
		}
      
		return error;
	
	}
	
	public List<ClaimLogDto> createSecondaryClaimDataFromRecreateSection(ClaimFromSheet re,
			String companyuuid, String officeuuids,RcmUser user,RcmTeam currentTeam,String currentClaimStatusEs) {


		logger.info(" In Save Secondary Claim From Recreate Claim section");
		String success = "";
		RcmClaims claim = null;
		List<ClaimLogDto> logClaimDtos = new ArrayList<>();
		Map<String, ClaimLogDto> mapcountNew = new HashMap<>();
		// RcmClaimLog rcmClaimLog=null;
		String source = ClaimSourceEnum.RECREATECLAIMSECTION.toString();
		RcmClaimAssignment rcmAssigment = null;
		HashMap<String, RcmClaimLog> logMap = new HashMap<>();
		// Map<String, Object[]> mapcountNew = new HashMap<>();
		// Map<String,String> messages= new HashMap<>();
		// Map<String,int[]> mapcountNewP= new HashMap<>();
		// Map<String,int[]>mapcountNewS= new HashMap<>();
		RcmCompany company = rcmCompanyRepo.findByUuid(companyuuid);
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
		HashMap<String, RcmCompany> companies = new HashMap<>();
		HashMap<String, RcmInsuranceType> rcmInsuranceTypes = new HashMap<>();
		RcmInsuranceType rcmInsuranceType = null;
		RcmCompany clCompany = null;
		RcmInsurance ins = null;
		//List<String> offNames = null;
		//List<String> offNameKeys = null;

		RcmOffice rcmOffice = null;
		//offNames = new ArrayList<>();
		//offNameKeys = new ArrayList<>();
		rcmOffice = officeRepo.findByNameAndCompanyUuid(re.getOfficeName(), company.getUuid());
		// rcmOffices.stream().map(RcmOffice::getName).forEach(offNames::add);
		//offNames.add(rcmOffice.getName());
		//offNameKeys.add(rcmOffice.getName() + rcmOffice.getKey());
	    try {
			
				int newClaimCt = 0;
				int newClaimPCt = 0;
				int newClaimSCt = 0;
				int logStatus = 0;
				
				
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
								return null;
							}
						}

						RcmOffice off = officeRepo.findByCompanyAndName(companies.get(re.getClientName()),
								re.getOfficeName());
						if (off == null) {
							ruleEngineService.saveRcmIssueClaim(re.getClaimId(), null, user,
									"Wrong Office Name-" + re.getOfficeName() + "For " + re.getClientName(), source,
									claimTypeEnum);
							return null;
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
									return null;

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
								return null;
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
							boolean isFCL= false;
							//For External we always have claims in Billing 
							if (clCompany.getName().equals(Constants.COMPANY_NAME)) {
								isBilling = ClaimUtil.isBillingClaimByInsuranceName(rcmInsuranceType.getName());
								isMedicaid=ClaimUtil.isMedcaidClaimByInsuranceName(rcmInsuranceType.getName());
								isMedicare=ClaimUtil.isMedicareClaimByInsuranceName(ins.getInsuranceType().getName());
								isChip=ClaimUtil.isChipClaimByInsuranceName(ins.getInsuranceType().getName());
								isFCL=ClaimUtil.isFCLClaimByInsuranceName(ins.getInsuranceType().getName(),ins.getName());
							}
							
							//boolean isBilling=ClaimUtil.isBillingClaimByInsuranceName(rcmInsuranceType.getName());
							//boolean isMedicaid=ClaimUtil.isMedcaidClaimByInsuranceName(rcmInsuranceType.getName());
							//boolean isMedicare=ClaimUtil.isMedicareClaimByInsuranceName(ins.getInsuranceType().getName());
							boolean missing=true;
							if (isBilling || isMedicare) {
							
							claim = ClaimUtil.createClaimFromSheetData(claim, off, re,
									ClaimUtil.filterTeamByNameId(allTeams, RcmTeamEnum.BILLING.toString()), user, ins,
									ins, claimStatusType, claimTypeEnum.getSuffix(), rcmInsuranceType, timely.getTimelyFilingLimit(),"",
									claimTypeEnum,ClaimSourceEnum.RECREATECLAIMSECTION.toString());
							missing=false;
							}
							if (isMedicaid  || isChip || isFCL ) {
								claim = ClaimUtil.createClaimFromSheetData(claim, off, re,
										ClaimUtil.filterTeamByNameId(allTeams, RcmTeamEnum.INTERNAL_AUDIT.toString()), user, ins,
										ins, claimStatusType, claimTypeEnum.getSuffix(), rcmInsuranceType, timely.getTimelyFilingLimit(),
										"",claimTypeEnum,ClaimSourceEnum.RECREATECLAIMSECTION.toString());
								missing=false;
							}
							if(missing) {//no Billing or Medicaid
								//put in billing 
								claim = ClaimUtil.createClaimFromSheetData(claim, off, re,
										ClaimUtil.filterTeamByNameId(allTeams, RcmTeamEnum.BILLING.toString()), user, ins,
										ins, claimStatusType, claimTypeEnum.getSuffix(), rcmInsuranceType, timely.getTimelyFilingLimit(),
										"",claimTypeEnum,ClaimSourceEnum.RECREATECLAIMSECTION.toString());
								missing=false;
								isBilling=true;
							}
							
							
							
							claim.setCurrentStatus(ClaimStatusEnum.Claim_Uploaded.getId());
							claim.setStatusES(currentClaimStatusEs);
							String claimUUid = rcmClaimRepository.save(claim).getClaimUuid();
							RcmTeam systemTeam=rcmTeamRepo.findByNameId(RcmTeamEnum.SYSTEM.getName());
							claimCycleService.createNewClaimCycle(claim,ClaimStatusEnum.Claim_Uploaded.getType(),null, systemTeam, user);
							//Save Data in rcm_claim_detail (new Enhancement)
							int serviceCount=0;
							if (re.getServiceCodes().size()>0) {
								RcmClaimDetail det=null;
								for(String cds:re.getServiceCodes()) {
									det =new RcmClaimDetail();
									List<String> toothSurfaces=re.getToothAndSurfaces();
									det.setApptId("");
									det.setClaim(claim);
									det.setDescription("");
									det.setEstInsurance("0");
									det.setEstPrimary("0");
									det.setFee("0");
									det.setIdEs("-1");
									det.setLineItem("-1");
									det.setPatientPortion("0");
									det.setPatientPortionSec("0");
									det.setProviderLastName("");
									det.setServiceCode(cds.trim());
									det.setStatus(currentClaimStatusEs);
									det.setSurface("");
									det.setTooth("");
									det.setActive(true);
									try {
										String[] ths = toothSurfaces.get(serviceCount++).split("#");
										det.setTooth(ths[0]);
										if (ths.length > 1) {
											det.setSurface(ths[1]);

										}
									} catch (Exception b) {
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
							if (assignedUserBilling != null && (isBilling || isMedicare)) {
								rcmAssigment = new RcmClaimAssignment();
								//
								rcmAssigment = ClaimUtil.createAssginmentData(rcmAssigment, user,
										assignedUserBilling.getUser(), claimUUid, claim, "",
										systemStatusBilling,assignedTeamBilling,Constants.SYSTEM_INITIAL_COMMENT);

								rcmClaimAssignmentRepo.save(rcmAssigment);
								claim.setCurrentStatus(ClaimStatusEnum.Pending_For_Billing.getId());
								claim.setNextAction(ClaimStatusEnum.Need_to_Bill.getId());
								rcmClaimRepository.updateClaimCurrentStatusWithAction(ClaimStatusEnum.Pending_For_Billing.getId(),ClaimStatusEnum.Need_to_Bill.getId(),claim.getClaimUuid());
								
								claimCycleService.createNewClaimCycle(claim, ClaimStatusEnum.Pending_For_Billing.getType(), ClaimStatusEnum.Need_to_Bill.getType(), assignedTeamBilling, user);
								
							}
							if (assignedUserInternalAudit != null && ( isMedicaid || isChip || isFCL)) {
								rcmAssigment = new RcmClaimAssignment();
								//
								rcmAssigment = ClaimUtil.createAssginmentData(rcmAssigment, user,
										assignedUserInternalAudit.getUser(), claimUUid, claim, "",
										systemStatusBilling,assignedTeamInternalAudit,Constants.SYSTEM_INITIAL_COMMENT);

								rcmClaimAssignmentRepo.save(rcmAssigment);
								claim.setCurrentStatus( ClaimStatusEnum.Pending_For_Review.getId());
								claim.setNextAction(ClaimStatusEnum.Need_to_Audit.getId());
								rcmClaimRepository.updateClaimCurrentStatusWithAction(ClaimStatusEnum.Pending_For_Review.getId(),ClaimStatusEnum.Need_to_Audit.getId(),claim.getClaimUuid());
								
								claimCycleService.createNewClaimCycle(claim, ClaimStatusEnum.Pending_For_Review.getType(),ClaimStatusEnum.Need_to_Audit.getType(), assignedTeamInternalAudit, user);
								
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

				
				
				ClaimLogDto claimLogDto = null;
				for (Map.Entry<String, RcmClaimLog> entry : logMap.entrySet()) {

					RcmClaimLog l= entry.getValue();
					claimLogDto = new ClaimLogDto(source, l.getOffice().getUuid(), 1, newClaimCt,
							new Date(), entry.getValue().getOffice().getName());
					System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
					commonClaimServiceImpl.saveClaimLog(entry.getValue(), user, entry.getValue().getOffice(),
							ClaimSourceEnum.RECREATECLAIMSECTION.toString(), 1,claimLogDto.getNewClaimsCount(),l.getNewClaimsPrimaryCount(),l.getNewClaimsSecodaryCount(),
							success);
					mapcountNew.put(entry.getKey(), claimLogDto);

				}
				RcmClaimLog l=null;
				RcmOffice fName =rcmOffice;
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
									ClaimSourceEnum.RECREATECLAIMSECTION.toString(), 1,0,0,0,
									success);
					 }
	               //}

			

			for (Map.Entry<String, ClaimLogDto> entry : mapcountNew.entrySet()) {

				logClaimDtos.add(entry.getValue());

			}
		} catch (Exception n) {
			n.printStackTrace();
			logger.error("Error in Fetching/Creating  Claims From Recreate section.. ");
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
						dataView.setNextAction(ClaimStatusEnum.getById(data.getNextAction())!=null?ClaimStatusEnum.getById(data.getNextAction()).getType():"N/A");
						listView.add(dataView);
			});
			 if (teamId != RcmTeamEnum.BILLING.getId() && teamId != RcmTeamEnum.INTERNAL_AUDIT.getId()) {
				 //Need to get Claim remark in of non billing and internal audit
				
				  //List<String> claimUuid = listView.stream().map(x -> x.getUuid()).collect(Collectors.toList());

				  this.populateClaimListWithComments(listView, teamId);
					
////			  int endIndex = Math.min(i + maxCommentDataPerPage, claimUuid.size());
////		      List<String> claimUuidChunk = claimUuid.subList(i, endIndex);
////			  commentsList.addAll(this.fetchLatestCommentsByClaimUuid(claimUuidChunk, teamId));
////					}
	
//				 listView.forEach(data->{
//					 
//					 
//					 data.setLastTeamRemark(rcmClaimAssignmentRepo.findLatestClaimCommentByOtherTeam(data.getUuid(), teamId));
//				 });
			 
			
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
				dataView.setNextAction(ClaimStatusEnum.getById(data.getNextAction())!=null?ClaimStatusEnum.getById(data.getNextAction()).getType():"N/A");
				//set es_status updated
//				if (data.getStatusESUpdated() == null) {
//					dataView.setStatusESUpdated(data.getStatusES());
//				}
				listView.add(dataView);
			});
			 if (teamId != RcmTeamEnum.BILLING.getId() && teamId != RcmTeamEnum.INTERNAL_AUDIT.getId()) {
				 //Need to get Claim remark in of non billing and internal audit
				
				 listView.forEach(data->{
					 data.setLastTeamRemark(rcmClaimAssignmentRepo.findLatestClaimCommentByOtherTeam(data.getUuid(), teamId));
				 });
				 
				 this.populateClaimListWithComments(listView, teamId);
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
				dataView.setNextAction(ClaimStatusEnum.getById(data.getNextAction())!=null?ClaimStatusEnum.getById(data.getNextAction()).getType():"N/A");
				//set es_status updated
//				if (data.getStatusESUpdated() == null) {
//					dataView.setStatusESUpdated(data.getStatusES());
//				}
				listView.add(dataView);
			});
			 if (teamId != RcmTeamEnum.BILLING.getId() && teamId != RcmTeamEnum.INTERNAL_AUDIT.getId()) {
				 //Need to get Claim remark in of non billing and internal audit
				
				 listView.forEach(data->{
					 data.setLastTeamRemark(rcmClaimAssignmentRepo.findLatestClaimCommentByOtherTeam(data.getUuid(), teamId));
				 });
				 this.populateClaimListWithComments(listView, teamId);
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
				dataView.setNextAction(ClaimStatusEnum.getById(data.getNextAction())!=null?ClaimStatusEnum.getById(data.getNextAction()).getType():"N/A");
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
		HashMap<String, RemoteLietStatusCount> remoteLiteMap = ruleEngineService.pullAndSaveRemoteLiteData();
		
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
		List<AssignFreshClaimLogsDto> ll = null;
		List<String> companies = findAssociatedCompanyIdByUserUuid(partialHeader);
		try {
			if (partialHeader.getRole().equals(Constants.ASSOCIATE)) {
				ll = rcmClaimRepository.fetchClaimsForAssignmentsByTeamAndUserType(companies, ct, instDB,partialHeader.getTeamId());
				
			}else {
				ll = rcmClaimRepository.fetchClaimsForAssignmentsByTeamType(companies, ct, instDB,partialHeader.getTeamId());
					
			}
			List<AssignFreshClaimLogsDto> primaries=new ArrayList<>();
			List<AssignFreshClaimLogsDto> secondaries=new ArrayList<>();
			Set<String> offices = new HashSet<>();
			if (ll!=null) {
				Set<String> primaryClaims = new HashSet<>();
				((List<AssignFreshClaimLogsDto>) ll).stream().map(AssignFreshClaimLogsDto::getOfficeUuid).forEach(offices::add);
				for(String off:offices) {
					List<AssignFreshClaimLogsDto> l2= ll.stream().filter(e -> e.getOfficeUuid().equals(off)
   							&&  e.getPrimaryC() ==1).collect(Collectors.toList());
					if (l2!=null && l2.size()>0) primaries.addAll(l2);
					l2 =ll.stream().filter(e -> e.getOfficeUuid().equals(off)
   							&&  e.getPrimaryC() ==0).collect(Collectors.toList());
					if (l2!=null && l2.size()>0) secondaries.addAll(l2);
					
				}
				//Remove Primary claims from secondary
				primaries.forEach( pr->{
					if (pr.getClaimId().split("_P").length>0) {
					primaryClaims.add(pr.getClaimId().split("_P")[0]+"_S");
					secondaries.removeIf(n -> (n.getOfficeUuid().equals(pr.getOfficeUuid()) && n.getClaimId().equals(pr.getClaimId().split("_P")[0]+"_S")));
					}
				});
				
				 
			}
			
			if (secondaries.size()>0)primaries.addAll(secondaries);
			RemoteLietStatusCount counts = null;
			AssignFreshClaimLogsImplDto dFA = null;
			Map<String, RcmOffice> officeCache = new HashMap<>();
			Map<String, RcmCompany> companyCache = new HashMap<>();
			Map<String, UserAssignOffice> userAssignCache = new HashMap<>();
			Map<String, RcmUser> rcmUserCache = new HashMap<>();
			for(String off:offices) {
				
			List<AssignFreshClaimLogsDto> x =	primaries.stream().filter(e -> e.getOfficeUuid().equals(off))
					.collect(Collectors.toList());
			if (x.isEmpty()) continue;
			AssignFreshClaimLogsDto minValue1=null;
			AssignFreshClaimLogsDto minValue2=null;
			Optional<AssignFreshClaimLogsDto> minValue1Opt = x.stream().filter(e -> e.getOpdt()!=null).max(Comparator.comparing(v -> v.getOpdt()));
			Optional<AssignFreshClaimLogsDto> minValue2Opt = x.stream().filter(e -> e.getOpdos()!=null).max(Comparator.comparing(v -> v.getOpdos()));
			if (minValue1Opt.isPresent()) {
				 minValue1 = minValue1Opt.get();
				 minValue2 = minValue2Opt.get();
			}
			dFA = new AssignFreshClaimLogsImplDto();
			RcmOffice office = officeCache.computeIfAbsent(off, id -> officeRepo.findByUuid(id));
			RcmCompany company = companyCache.computeIfAbsent(office.getCompany().getUuid(), id -> rcmCompanyRepo.findByUuid(id));
			dFA.setOfficeName(office.getName());
			if (minValue1!=null) {
				dFA.setAssignedUser(minValue1.getAssignedUser());
				dFA.setFName(minValue1.getFName());
				dFA.setLName(minValue1.getLName());
			}
			else {
				if (userAssignCache.get(office.getUuid())== null) {
					UserAssignOffice  uaf=userAssignOfficeRepo.findByOfficeUuidAndTeamId(office.getUuid(),partialHeader.getTeamId());
					userAssignCache.put(office.getUuid(),uaf);
				}
				UserAssignOffice uaf=userAssignCache.get(office.getUuid());
				if (uaf!= null) {
					dFA.setAssignedUser(uaf.getUser().getUuid());
					RcmUser user = rcmUserCache.computeIfAbsent(uaf.getUser().getUuid(), id -> userRepo.findByUuid(id));
					dFA.setFName(user.getFirstName());
					dFA.setLName(user.getLastName());
				}
				
			}
			if (minValue1!=null)dFA.setCount(x.size());
			else dFA.setCount(0);
			
			dFA.setCompanyName(company.getName());
			dFA.setOfficeUuid(office.getUuid());

			if (minValue1!=null)dFA.setOpdtd(minValue1.getOpdt()==null?"0":minValue1.getOpdt());
			if (minValue2!=null)dFA.setOpdosd(minValue2.getOpdos()==null?"0":minValue2.getOpdos());
			//BeanUtils.copyProperties(logD, dF);
			if (minValue1!=null)counts = remoteLiteMap.get(office.getName());
			if (counts != null) {
				dFA.setRemoteLiteRejections(counts.getRejectedCount());
			}
			finalList.add(dFA);
			}
			

		} catch (Exception n) {
			n.printStackTrace();
		}
		
		Collections.sort(finalList, (o1, o2) -> ( o1.getCompanyName().compareTo(o2.getCompanyName())));
		
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
			assignedClaims= rcmClaimAssignmentRepo.findIssueAssignments(claimUuid);
		 if (assignedClaims.size()>1) {
			 rcmClaimAssignmentRepo.updateClaimIssueAssignment(assignedClaims.get(0),claimUuid);
		 }
		}catch(Exception dup) {
			dup.printStackTrace();
		}
	

		RcmClaimDetailDto dto = rcmClaimRepository.fetchIndividualClaim(claimUuid);
		
		
		if (dto != null) {
			
			if (!partialHeader.getRole().equals(Constants.SUPER_ADMIN)) {
				validateClaimRight=dto.getCompanyId().equals(partialHeader.getCompany().getUuid());
				if (!validateClaimRight) {
					return null;
				}
			}
			
			implDto = new FreshClaimDataImplDto();

			//implDto.setSecInsurance("N/A");
			// RcmClaims claim = rcmClaimRepository.findByClaimUuid(claimUuid);
			// Fetch Data from RCM Tool Checks and Validations Sheets //141479965

			BeanUtils.copyProperties(dto, implDto);
			
			// fetch user data from user_assign table by teams
			 List<AssignOfficeResponseDto>teamsFromUserAssign=this.fetchTeamsDataFromUserAssignOffice(dto.getOfficeUuid());
			 
			 implDto.setUserAssignOfficeData(teamsFromUserAssign);		
			
			//set es_status updated
			if (dto.getStatusESUpdated() == null) {
				implDto.setStatusESUpdated(dto.getStatusES());
			}
			
			if (dto.getCurrentStatus()!=0) {
				ClaimStatusEnum oldStatus = ClaimStatusEnum.getById(dto.getCurrentStatus());
				if (oldStatus!=null) {
					implDto.setCurrentStatusName(oldStatus.getType());
				}
			}
            if (dto.getNextAction()!=0) {
            	ClaimStatusEnum oldAction = ClaimStatusEnum.getById(dto.getNextAction());
				if (oldAction!=null) {
					implDto.setNextActionName(oldAction.getType());
				}
			}
			if (implDto.getPatientContactNo() ==null) implDto.setPatientContactNo("");
			if (implDto.getInsuranceContactNo() ==null) implDto.setInsuranceContactNo("");
			
			if (dto.getCompanyId().equals(partialHeader.getCompany().getUuid())) {
				implDto.setClaimCmpMatchesLoggedCmp(true);
			}else {
				implDto.setClaimCmpMatchesLoggedCmp(false);
			}
			implDto.setIvfId(dto.getIvId());
			//for linked claims
			List<LinkedClaimDto> linkedClaims = rcmLinkedClaimsRepo.getLinkedClaimsByClaimUuuid(dto.getUuid());
			List<LinkedClaimResponseDto> linkedClaimsList = new ArrayList<>();
			if (linkedClaims != null && linkedClaims.size() > 0) {
				linkedClaims.forEach(x -> {
					LinkedClaimResponseDto linkedClaimdto = new LinkedClaimResponseDto();
					if (x.getClaimId().contains(Constants.ARCHIVE_PREFIX)) {
						String removeArchivePrefix = x.getClaimId()
								.split(Constants.HYPHEN + Constants.ARCHIVE_PREFIX)[1];
						linkedClaimdto.setClaimId(removeArchivePrefix);
						linkedClaimdto.setClaimUuid(x.getClaimUuid());
						linkedClaimsList.add(linkedClaimdto);
					} else {
						linkedClaimdto.setClaimId(x.getClaimId());
						linkedClaimdto.setClaimUuid(x.getClaimUuid());
						linkedClaimsList.add(linkedClaimdto);
					}
				});
			}
			implDto.setLinkedClaims(linkedClaimsList);
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
			
//			// set automated field nextFollowUpDate inside follow_up_section
//			String insuranceType = implDto.isPrimary() ? dto.getPrimaryInsType() : dto.getSecondaryInsType();
//			Calendar calendarForNextFollowUpDate = Calendar.getInstance();
//			Date date = dto.getNextFollowUpDate() != null ? dto.getNextFollowUpDate() : new Date();
//			calendarForNextFollowUpDate.setTime(date);
//			int days = InsuranceTypeEnum.getDaysByType(insuranceType);
//			calendarForNextFollowUpDate.add(Calendar.DAY_OF_YEAR, days);
//			implDto.setNextFollowUpDate(Constants.SDF_MYSL_DATE.format(calendarForNextFollowUpDate.getTime()));
			
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
					implDto.setInsuranceContactNo(ivfDto.getInsuranceContact());
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
				
				} else {
					ivfId = implDto.getIvfId();
					
				}
				
				if (implDto.getInsuranceContactNo() == null || (implDto.getInsuranceContactNo() != null
						&& implDto.getInsuranceContactNo().trim().equals(""))) {

					// get insurance contactNumber
					Set<String> insTypes = new HashSet<>();
					insTypes.add(claimSubTy);
					if (claimSubTy.equalsIgnoreCase(Constants.insuranceTypePrimary)) {
						insTypes.add("");
						insTypes.add("No Information");
					}
					ivfDto = rcmClaimRepository.getIVIdOfClaimByDos(Constants.SDF_MYSL_DATE.format(implDto.getDos()),
							implDto.getOfficeUuid(), implDto.getPatientId(), insTypes);
					if (ivfDto != null) {
						implDto.setInsuranceContactNo(ivfDto.getInsuranceContact());
					if (claim==null)  claim = rcmClaimRepository.findByClaimUuid(claimUuid);
					   claim.setInsuranceContactNo(ivfDto.getInsuranceContact());
					   
					}
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
			RcmClaimAssignment assign = rcmClaimAssignmentRepo.findByClaimsClaimUuidAndActiveAndAssignedToNotNull(claimUuid, true);
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
			RcmClaimComment comment = rcmClaimCommentRepo.findByCommentedByUuidAndClaimsClaimUuid(partialHeader.getJwtUser().getUuid(),claimUuid);
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
					/*
					 * "For now, the secondary claims becomes active to workupon by Internal Audit and billing team as soon as billing team submits the primary claims.
						But, as per our phase#2 requiments, this is how it should be
						When any user selects the ""Next Action Required"" as ""Need to Bill to Secondary"", then the secondary claim of that claim should become active to be worked upon by Billing or internal audt team (depending on the insurance type) 
						For, Internal Audit team, they should be able to work on secondary even if primary is open"
					 */
					implDto.setAssoicatedClaimStatus(ClaimStatusEnum.Need_to_Bill_Secondary_Insurance.getId()!=(int) s[1]);
					implDto.setPrimInsurance(s[2]==null?"":s[2].toString());
					implDto.setAssoicatedClaimCurrentStatus(s[3]==null?0:Integer.parseInt(s[3].toString()));
					if (implDto.getCurrentTeamId() == RcmTeamEnum.INTERNAL_AUDIT.getId()) {
						implDto.setAssoicatedClaimStatus(false);
					}
				}

			} else {
				Object sec = rcmClaimRepository.getClaimsUuidClaimIdSec(
						implDto.getClaimId().split(ClaimTypeEnum.P.getSuffix())[0] + ClaimTypeEnum.S.getSuffix(),
						implDto.getOfficeUuid());
				if (sec != null) {
					Object s[] = (Object[]) sec;
					implDto.setAssoicatedClaimUuid(s[0].toString());
					implDto.setAssoicatedClaimStatus(ClaimStatusEnum.Need_to_Bill_Secondary_Insurance.getId()!=(int) s[1]);
					implDto.setSecInsurance(s[2].toString());//For Primary see if we have Primary
					implDto.setAssoicatedClaimCurrentStatus(s[3]==null?0:Integer.parseInt(s[3].toString()));
					if (implDto.getCurrentTeamId() == RcmTeamEnum.INTERNAL_AUDIT.getId()) {
						implDto.setAssoicatedClaimStatus(false);
					}
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
			
			// set allowEdit false when office is active=false
			if (!dto.getOfficeActive()) {
				implDto.setAllowEdit(false);
			}
			
			// Run Auto Rules
			//runAutomatedRules(jwtUser, claimUuid);//not from here 
		
		} else {
			// Wrong claimId;
		}
		//if (implDto.getSecInsurance()==null) implDto.setSecInsurance("N/A");
		
		
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
				det.setInsuranceContact(ivfDto.getInsuranceContact());
				claim.setInsuranceContactNo(ivfDto.getInsuranceContact());
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
			 claim.setInsuranceContactNo("");
			 det.setInsuranceContact("");
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
			if (claim.isPending() ) {

			
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
					rcmClaimDetail.setActive(true);
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
				cddList = rcmClaimDetailRepo.findByClaimClaimUuidAndActiveTrue(claimUuid);
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
				
				cddList = rcmClaimDetailRepo.findByClaimClaimUuidAndActiveTrue(claimUuid);
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
								List<RcmClaimDetail> cList= rcmClaimDetailRepo.findByClaimClaimUuidAndServiceCode(claim.getClaimUuid(),entry.getKey());
								one.setTooth(getToothOrSurfaceFromClaimDetails(cList, true));
								one.setSurface(getToothOrSurfaceFromClaimDetails(cList, false));
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
					List<RcmClaimDetail> cList= rcmClaimDetailRepo.findByClaimClaimUuidAndServiceCode(claim.getClaimUuid(),s.getServiceCode());
					one.setTooth(getToothOrSurfaceFromClaimDetails(cList, true));
					one.setSurface(getToothOrSurfaceFromClaimDetails(cList, false));
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

	public List<?> claimsProductionReportByTeam(ClaimProductionLogDto dto,PartialHeader partialHeader) {

		List<String> companies =null;
		if (partialHeader.getCompany()==null || partialHeader.getCompany().getUuid()==null) {
	          companies = findAssociatedCompanyIdByUserUuid(partialHeader);
        }else {
        	companies = new ArrayList<>();
        	companies.add(rcmCompanyRepo.findByUuid(partialHeader.getCompany().getUuid()).getUuid());
	    }
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
		}
		
		else if (partialHeader.getTeamId() == RcmTeamEnum.AGING.getId()) {
			ProductionDtoFrorAging data = new ProductionDtoFrorAging();
			List<ProductionForAging> agingData = null;
			List<ProductionAgeWiseDto> listOfAgeWiseData = new ArrayList<>();
			List<ProductionCurrentStatusWiseDto> listOfCurrentStatusWiseData = new ArrayList<>();
			List<RcmOfficeDto> offices = officeRepo.findByCompanyUuidInAndActiveTrue(companies);
			if (partialHeader.getRole().equals(Constants.ASSOCIATE)) {
				agingData = rcmClaimRepository.claimProductionForAgingAssoicate(companies, partialHeader.getTeamId(),
						dto.getStartDate(), dto.getEndDate(), partialHeader.getJwtUser().getUuid());

			} else {
				agingData = rcmClaimRepository.claimProductionForAging(companies, partialHeader.getTeamId(),
						dto.getStartDate(), dto.getEndDate());
			}

			
			for (RcmOfficeDto off : offices) {
				ProductionAgeWiseDto agingDto = new ProductionAgeWiseDto(off.getName(), "-", 0, 0, 0, 0);
				if (partialHeader.getRole().equals(Constants.ASSOCIATE)) {
				List<ProductionForAging> filterAge = agingData.stream()
					.filter(x -> x.getOfficeUuid().equals(off.getUuid())).collect(Collectors.toList());
				if (filterAge.size()>0) listOfAgeWiseData.add(agingDto);
				 }else {
					 listOfAgeWiseData.add(agingDto);
				 }
			}

			for (ProductionAgeWiseDto pd : listOfAgeWiseData) {
				List<ProductionForAging> filterAge = agingData.stream()
						.filter(x -> x.getOfficeName().equals(pd.getOfficeName())).collect(Collectors.toList());
				for (ProductionForAging age : filterAge) {
					pd.setAssociateName(age.getFName()+" "+age.getLName());
					if (age.getClaimAge() <= 30) {
						pd.setCountForAgeRange1(pd.getCountForAgeRange1() + 1);
					} else if (age.getClaimAge() > 31 && age.getClaimAge() <= 60) {
						pd.setCountForAgeRange2(pd.getCountForAgeRange2() + 1);
					} else if (age.getClaimAge() > 61 && age.getClaimAge() <= 90) {
						pd.setCountForAgeRange3(pd.getCountForAgeRange3() + 1);
					} else {
						pd.setCountForAgeRange4(pd.getCountForAgeRange4() + 1);
					}
				}
			}

			// status wise
			for (RcmOfficeDto off : offices) {
				ProductionCurrentStatusWiseDto currentStausDto = new ProductionCurrentStatusWiseDto(off.getName(), "-",
						0, 0, 0, 0, 0, 0, 0, 0);
				if (partialHeader.getRole().equals(Constants.ASSOCIATE)) {
				List<ProductionForAging> filterAge = agingData.stream()
						.filter(x -> x.getOfficeUuid().equals(off.getUuid())).collect(Collectors.toList());
					if (filterAge.size()>0) listOfCurrentStatusWiseData.add(currentStausDto);
				}else {
					 listOfCurrentStatusWiseData.add(currentStausDto);
				}
				
			}

			for (ProductionCurrentStatusWiseDto pd : listOfCurrentStatusWiseData) {
				List<ProductionForAging> filterStatus = agingData.stream()
						.filter(x -> x.getOfficeName().equals(pd.getOfficeName())).collect(Collectors.toList());
				for (ProductionForAging status : filterStatus) {
					pd.setAssociateName(status.getFName()+" "+status.getLName());
					if (status.getCurrentClaimStatus() == ClaimStatusEnum.Pending_For_Review.getId()) {
						pd.setPendingForReviewCount(pd.getPendingForReviewCount());
					} else if (status.getCurrentClaimStatus() == ClaimStatusEnum.Pending_For_Billing.getId()) {
						pd.setPendingForBillingCount(pd.getPendingForBillingCount() + 1);
					} else if (status.getCurrentClaimStatus() == ClaimStatusEnum.Billed.getId()) {
						pd.setBilledCount(pd.getBilledCount() + 1);
					} else if (status.getCurrentClaimStatus() == ClaimStatusEnum.IN_PROCESS.getId()) {
						pd.setBilledCount(pd.getBilledCount() + 1);
					} else if (status.getCurrentClaimStatus() == ClaimStatusEnum.Case_Closed.getId()) {
						pd.setClosedCount(pd.getClosedCount() + 1);
					} else if (status.getCurrentClaimStatus() == ClaimStatusEnum.Voided.getId()) {
						pd.setVoidedCount(pd.getVoidedCount() + 1);
					} else if (status.getCurrentClaimStatus() == ClaimStatusEnum.ReBilling.getId()) {
						pd.setReBillingCount(pd.getReBillingCount() + 1);
					} else if (status.getCurrentClaimStatus() == ClaimStatusEnum.Reviewed.getId()) {
						pd.setReviewedCount(pd.getReviewedCount() + 1);
					} else if (status.getCurrentClaimStatus() == ClaimStatusEnum.Submitted.getId()) {
						pd.setSubmittedCount(pd.getSubmittedCount() + 1);
					}
				}
			}
			data.setListOfAgeWiseData(listOfAgeWiseData);
			data.setListOfCurrentStatusWiseData(listOfCurrentStatusWiseData);
			return Arrays.asList(data);
		}

		else if (partialHeader.getTeamId() == RcmTeamEnum.PATIENT_CALLING.getId()) {
			List<ProductionForPatientCalling> patientCallingData = null;
			List<RcmOfficeDto> offices = officeRepo.findByCompanyUuidIn(companies);
			List<ProductionDispositionWiseDto> data = new ArrayList<>();
			if (partialHeader.getRole().equals(Constants.ASSOCIATE)) {
				patientCallingData = rcmClaimRepository.claimProductionForPatientCallingAssoicate(companies,
						partialHeader.getTeamId(), dto.getStartDate(), dto.getEndDate(),
						partialHeader.getJwtUser().getUuid());
			} else {
				patientCallingData = rcmClaimRepository.claimProductionForPatientCalling(companies,
						partialHeader.getTeamId(), dto.getStartDate(), dto.getEndDate());
			}

			for (RcmOfficeDto off : offices) {

				ProductionDispositionWiseDto dispositionDto = new ProductionDispositionWiseDto(off.getName(), 0, 0, 0,
						0, 0, 0);
				if (partialHeader.getRole().equals(Constants.ASSOCIATE)) {
					List<ProductionForPatientCalling> filterAge = patientCallingData.stream()
							.filter(x -> x.getOfficeUuid().equals(off.getUuid())).collect(Collectors.toList());
						if (filterAge.size()>0) data.add(dispositionDto);
					}else {
						data.add(dispositionDto);
					}
				
				//data.add(dispositionDto);
			}

			for (ProductionDispositionWiseDto pd : data) {
				List<ProductionForPatientCalling> filterData = patientCallingData.stream()
						.filter(x -> x.getOfficeName().equals(pd.getOfficeName())).collect(Collectors.toList());
				for (ProductionForPatientCalling pc : filterData) {
					pd.setOfficeName(pc.getOfficeName());
					if (pc.getDesposition()==null) {
					}else if (pc.getDesposition().equals(DispositionEnumForProduction.VOICE_MAIL_LEFT.getName())) {
						pd.setVoiceMailLeft(pd.getVoiceMailLeft() + 1);
					} else if (pc.getDesposition().equals(DispositionEnumForProduction.PAYMENT_PROMISED.getName())) {
						pd.setPaymentPromised(pd.getPaymentPromised() + 1);
					} else if (pc.getDesposition().equals(DispositionEnumForProduction.PAYMENT_MADE.getName())) {
						pd.setPaymentMade(pd.getPaymentMade() + 1);
					} else if (pc.getDesposition().equals(DispositionEnumForProduction.WRONG_NO.getName())) {
						pd.setWrongNo(pd.getWrongNo() + 1);
					} else if (pc.getDesposition().equals(DispositionEnumForProduction.NOT_READY_TO_PAY.getName())) {
						pd.setNotReadyToPay(pd.getNotReadyToPay() + 1);
					} else if (pc.getDesposition().equals(DispositionEnumForProduction.STATEMENT_REQUESTED.getName())) {
						pd.setStatementRequested(pd.getStatementRequested() + 1);
					}
				}
			}
			return data;
		}

		else if (partialHeader.getTeamId() == RcmTeamEnum.PATIENT_STATEMENT.getId()) {
			List<ProductionForPatientStatement> patientStatementData = null;
			List<ProductionStatementTypeWiseDto> data = new ArrayList<>();
			if (partialHeader.getRole().equals(Constants.ASSOCIATE)) {
				patientStatementData = rcmClaimRepository.claimProductionForPatientStatementAssoicate(companies,
						partialHeader.getTeamId(), dto.getStartDate(), dto.getEndDate(),
						partialHeader.getJwtUser().getUuid());
			} else {
				patientStatementData = rcmClaimRepository.claimProductionForPatientStatement(companies,
						partialHeader.getTeamId(), dto.getStartDate(), dto.getEndDate());
			}

			List<String> userUuids = patientStatementData.stream().map(x -> x.getUuid()).distinct()
					.collect(Collectors.toList());

			for (String userUuid : userUuids) {
				ProductionStatementTypeWiseDto statementTypeDto = new ProductionStatementTypeWiseDto(userUuid, 0, 0,
						" ", " "," ", new ProductionStatementTypeWiseDto().new StatementType());
				data.add(statementTypeDto);
			}

			for (ProductionStatementTypeWiseDto pd : data) {
				List<ProductionForPatientStatement> filterData = patientStatementData.stream()
						.filter(x -> x.getUuid().equals(pd.getUserUuid())).collect(Collectors.toList());
				for (ProductionForPatientStatement ps : filterData) {
					pd.setClientName(ps.getCompanyName());
					pd.setFname(ps.getFName());
					pd.setLname(ps.getLName());
					pd.setTotal(ps.getTotal());
					pd.setDays(ps.getDays());
					if (ps.getStatementType() == 1) {
						pd.getStatementType().setStatementType1(pd.getStatementType().getStatementType1() + 1);
					} else if (ps.getStatementType() == 2) {
						pd.getStatementType().setStatementType2(pd.getStatementType().getStatementType2() + 1);
					} else if (ps.getStatementType() == 3) {
						pd.getStatementType().setStatementType3(pd.getStatementType().getStatementType3() + 1);
					}
				}
			}
			return data;
		}

		else if (partialHeader.getTeamId() == RcmTeamEnum.CDP.getId()) {
			ProductionForCDP data = new ProductionForCDP();
			List<ProductionDto> productionDataForInsuranceFollowUp = null;
			List<ProductionDto> productionDataForAppeal = null;
			// fetch data with the help of cdp team in Insurance follow up section
			if (partialHeader.getRole().equals(Constants.ASSOCIATE)) {
				productionDataForInsuranceFollowUp = rcmClaimRepository
						.claimProductionForCDPByInsuranceFollowUpAssoicate(companies, partialHeader.getTeamId(),
								dto.getStartDate(), dto.getEndDate(), partialHeader.getJwtUser().getUuid());
			} else {
				productionDataForInsuranceFollowUp = rcmClaimRepository.claimProductionForCDPByInsuranceFollowUp(
						companies, partialHeader.getTeamId(), dto.getStartDate(), dto.getEndDate());
			}

			// fetch data with the help of cdp team in appeal section
			if (partialHeader.getRole().equals(Constants.ASSOCIATE)) {
				productionDataForAppeal = rcmClaimRepository.claimProductionForCDPByAppealAssoicate(companies,
						partialHeader.getTeamId(), dto.getStartDate(), dto.getEndDate(),
						partialHeader.getJwtUser().getUuid());
			} else {
				productionDataForAppeal = rcmClaimRepository.claimProductionForCDPByAppeal(companies,
						partialHeader.getTeamId(), dto.getStartDate(), dto.getEndDate());
			}

			data.setCdpForInsuranceFollowUp(productionDataForInsuranceFollowUp);
			data.setCdpForAppeal(productionDataForAppeal);
			return Arrays.asList(data);
		}

		else if (partialHeader.getTeamId() == RcmTeamEnum.LC3.getId()
				|| partialHeader.getTeamId() == RcmTeamEnum.MEDICAID_IV.getId()
				|| partialHeader.getTeamId() == RcmTeamEnum.OFFICE.getId()
				|| partialHeader.getTeamId() == RcmTeamEnum.ORTHO.getId()
				|| partialHeader.getTeamId() == RcmTeamEnum.PPO_IV.getId()
				|| partialHeader.getTeamId() == RcmTeamEnum.CREDENTIALING.getId()) {
			if (partialHeader.getRole().equals(Constants.ASSOCIATE)) {
				return rcmClaimRepository.claimProductionForAssignToOtherTeamsAssoicate(companies,
						partialHeader.getTeamId(), dto.getStartDate(), dto.getEndDate(),
						partialHeader.getJwtUser().getUuid());
			} else {
				return rcmClaimRepository.claimProductionForAssignToOtherTeams(companies, partialHeader.getTeamId(),
						dto.getStartDate(), dto.getEndDate());
			}
		}
		
		else if (partialHeader.getTeamId() == RcmTeamEnum.PAYMENT_POSTING.getId()) {

			//String claimStatus = ClaimStatusEnum.POSTED.getType();
			if (partialHeader.getRole().equals(Constants.ASSOCIATE)) {
				return rcmClaimRepository.claimProductionForPaymentPostingAssoicate(companies,
						partialHeader.getTeamId(), dto.getStartDate(), dto.getEndDate(),
						partialHeader.getJwtUser().getUuid());
			} else {
				return rcmClaimRepository.claimProductionForPaymentPosting(companies, partialHeader.getTeamId(),
						dto.getStartDate(), dto.getEndDate());
			}
		}

		else {
			if (partialHeader.getRole().equals(Constants.ASSOCIATE)) {
				return rcmClaimRepository.claimProductionForOtherTeamAssoicate(companies, partialHeader.getTeamId(),
						dto.getStartDate(), dto.getEndDate(), partialHeader.getJwtUser().getUuid());
			} else {
				return rcmClaimRepository.claimProductionForOtherTeam(companies, partialHeader.getTeamId(),
						dto.getStartDate(), dto.getEndDate());
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
	
	public List<ClaimSteps> fetchClaimSteps(String claimuuid) {

		List<ClaimSteps> list = claimCycleService.getClaimCycle(claimuuid);

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
		Date date=new Date();
		if (dto == null)
			return "";
		if (det != null) {
			det.setSubmittedBy(user);
			det.setUpdatedBy(user);
			det.setUpdatedDate(date);
		} else {
			det = new RcmClaimSubmissionDetails();
			det.setSubmittedBy(user);
		}

		det.setCreatedBy(user);
		det.setClaim(claim);
		if(dto.getEsDate()==null) {
			dto.setEsDate(new java.sql.Date(date.getTime()));
		}
		det.setEsDate(dto.getEsDate());
		det.setPreauth(dto.isPreauth());
		det.setPreauthNo(dto.getPreauthNo());
		det.setChannel(dto.getChannel());
		det.setClaimNumber(dto.getClaimNumber());
		det.setProviderRefNo(dto.getProviderRefNo());
		det.setRefferalLetter(dto.isRefferalLetter());
		det.setAttachmentSend(dto.isAttachmentSend());
		det.setCleanClaim(dto.isCleanClaim());
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
			
			RcmClaimAssignment assign = rcmClaimAssignmentRepo.findByClaimsClaimUuidAndActiveAndAssignedToNotNull(claim.getClaimUuid(),true);
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
			if (newAssign.isActive()) newAssign.setActionName(ClaimStatusEnum.Claim_Assign_TO_TL.getType());
			rcmClaimAssignmentRepo.save(newAssign);
			String newCycleStatus =null;
			
			CurrentStatusAndNextActionDto nextActionRequiredInfoModel = new CurrentStatusAndNextActionDto();
			if (StringUtils.isAllBlank(dto.getNextAction())) {
				ClaimStatusEnum nextAction = ClaimStatusEnum.getById(claim.getNextAction());
				nextActionRequiredInfoModel.setNextAction(nextAction != null ? nextAction.getType() : null);
			} else {
				ClaimStatusEnum nextAction = ClaimStatusEnum.getByType(dto.getNextAction());
				nextActionRequiredInfoModel.setNextAction(dto.getNextAction());
				claim.setNextAction(nextAction.getId());
			}
			if (StringUtils.isAllBlank(dto.getCurrentClaimStatusRcm())) {
				ClaimStatusEnum type = ClaimStatusEnum.getById(claim.getCurrentStatus());
				nextActionRequiredInfoModel.setCurrentClaimStatusRcm(type != null ? type.getType() : null);
			} else {
				ClaimStatusEnum rcmStatus = ClaimStatusEnum.getByType(dto.getCurrentClaimStatusRcm());
				nextActionRequiredInfoModel.setCurrentClaimStatusRcm(dto.getCurrentClaimStatusRcm());
				claim.setCurrentStatus(rcmStatus.getId());
			}
			nextActionRequiredInfoModel.setAssignToTeamId(teamId);
			nextActionRequiredInfoModel.setRemarks(dto.getRemark());
			nextActionRequiredInfoModel.setCurrentClaimStatusEs(
					StringUtils.isAllBlank(dto.getCurrentClaimStatusEs()) ? claim.getStatusESUpdated()
							: dto.getCurrentClaimStatusEs());
			// save next action data
			RcmTeam currentTeam = rcmTeamRepo.findById(teamId);

			claimSectionImpl.saveNextActionRequiredAndCurrentClaimStatusSection(nextActionRequiredInfoModel, claim,
					user, currentTeam, true, "");

			if (dto.getWithNextActionData() == null) {
				// update old status
				ClaimCycle previousCycleData = clamCycleRepo.findFirstByClaimAndCurrentTeamIdOrderByCreatedDateDescIdDesc(claim,
						claim.getCurrentTeamId());
				if (previousCycleData!=null) {
					previousCycleData.setStatusUpdated(previousCycleData.getStatus());
					clamCycleRepo.save(previousCycleData);
				} else
					claimCycleService.createNewClaimCycleWithOldStatus(claim, claim.getCurrentTeamId(), rcmLeadUser,
							newCycleStatus, null);
			}
			else {
				newCycleStatus = StringUtils.isAllBlank(dto.getCurrentClaimStatusRcm()) ? null
						: dto.getCurrentClaimStatusRcm();
				ClaimStatusEnum nextAction = ClaimStatusEnum.getByType(dto.getNextAction());
				String nextActionType = null;
				if (nextAction != null) {
					nextActionType = nextAction.getType();
				}
				
				// update old status
				ClaimCycle previousCycleData = clamCycleRepo.findFirstByClaimAndCurrentTeamIdOrderByCreatedDateDescIdDesc(claim,claim.getCurrentTeamId());
				if (previousCycleData != null) {
					if (previousCycleData.getStatusUpdated()!=null && !previousCycleData.getStatusUpdated().isEmpty()) {
						previousCycleData.setStatusUpdated(newCycleStatus);
					} else {
						previousCycleData.setStatusUpdated(previousCycleData.getStatus());
					}
					clamCycleRepo.save(previousCycleData);
				}
				
				claimCycleService.createNewClaimCycleWithOldStatus(claim, currentTeam, rcmLeadUser, newCycleStatus,
						nextActionType);
			}

			return "success";
		} else {
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
			if (s.getValue() != null && s.getValue().trim().equals("")) {
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

		RcmClaimComment d = rcmClaimCommentRepo.findByCommentedByUuidAndClaimsClaimUuid(partialHeader.getJwtUser().getUuid(),claimuuid);
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
		boolean originalClaimPendingStatus =claim.isPending();
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
			if (originalClaimPendingStatus &&  dto.isSubmission() && partialHeader.getTeamId()==RcmTeamEnum.BILLING.getId()) {

				// update old status
				ClaimCycle previousCycleData = clamCycleRepo.findFirstByClaimAndCurrentTeamIdOrderByCreatedDateDescIdDesc(claim,
						claim.getCurrentTeamId());
				if (previousCycleData!=null) {
	
					 previousCycleData.setStatusUpdated(ClaimStatusEnum.Billed.getType());
			
					clamCycleRepo.save(previousCycleData);
				}

				//only billing can submit
				//ClaimStatusEnum.Billing.getType();//Once claim is submitted and its being reworked upon the maintain the current status.
				message= rcmClaimLogServiceImpl.assignClaimToOtherTeamWithRemarkCommon(partialHeader,dto.getClaimUuid(),
						Constants.FROMBILLINGTOPOSTING,"",claim,assign,user,office,null,
						originalClaimPendingStatus? ClaimStatusEnum.Billed.getType() : null,originalClaimPendingStatus? ClaimStatusEnum.Need_to_Post.getType() : null,dto.getActionName());
			    
				
				rcmClaimAssignmentRepo.save(assign);
				claim.setUpdatedBy(user);
				claim.setPending(false);
				claim.setUpdatedDate(new Date());
				 long millis=System.currentTimeMillis();  
			     java.sql.Date date=new java.sql.Date(millis);  
				if (claim.getFirstPostingDate() ==null)claim.setFirstPostingDate(date);
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
				
				
			}else if(originalClaimPendingStatus  && dto.isAssignToOtherTeam()){
				String createStatus =null;//&& partialHeader.getTeamId()==RcmTeamEnum.INTERNAL_AUDIT.getId()
				String nextAction =null;
				if (originalClaimPendingStatus) {
					// update old status
					ClaimCycle previousCycleData = clamCycleRepo.findFirstByClaimAndCurrentTeamIdOrderByCreatedDateDescIdDesc(claim,
							claim.getCurrentTeamId());
					if (previousCycleData != null) {
						String status = null;
						if (claim.getCurrentTeamId().getId() == RcmTeamEnum.BILLING.getId()) {
							status = ClaimStatusEnum.Billed.getType();
						} else if (claim.getCurrentTeamId().getId() == RcmTeamEnum.INTERNAL_AUDIT.getId()) {
							status = ClaimStatusEnum.Reviewed.getType();
						}
						previousCycleData.setStatusUpdated(status);

						clamCycleRepo.save(previousCycleData);
					}
					
					 if (dto.getAssignToTeam()==RcmTeamEnum.BILLING.getId()) {
						 createStatus = ClaimStatusEnum.Pending_For_Billing.getType(); 
						 nextAction =ClaimStatusEnum.Need_to_Bill.getType();
					 }
					 // As per phase 2 this will never happen
					 else if ( dto.getAssignToTeam()==RcmTeamEnum.INTERNAL_AUDIT.getId()) {
						 createStatus = ClaimStatusEnum.Need_to_Audit.getType(); 
						 nextAction= ClaimStatusEnum.Need_to_Audit.getType(); 
					 }
					 else {
						 createStatus = ClaimStatusEnum.Need_Additional_Information_For_Claim.getType(); 
						 nextAction= ClaimStatusEnum.Need_Additional_Information_For_Claim.getType(); 
					 }
				}
				
				    claim.setUpdatedBy(user);
					claim.setUpdatedDate(new Date());
					//claim.setCurrentStatus(Constants.CLAIM_POSTING_STATE);
					rcmClaimRepository.save(claim);
					
				message= rcmClaimLogServiceImpl.assignClaimToOtherTeamWithRemarkCommon(partialHeader,dto.getClaimUuid(),
						dto.getAssignToTeam(),dto.getAssignToComment(),claim,assign,user,office,null,
						createStatus,nextAction,dto.getActionName());
				rcmClaimAssignmentRepo.save(assign);
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
		Optional<RcmTeam>team = rcmTeamRepo.findById(assignToTeamId);
		if (!team.isPresent()) {
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
				String newCycleStatus = null;
				if (assignToTeamId.intValue() == RcmTeamEnum.BILLING.getId()) {

					newCycleStatus = ClaimStatusEnum.Pending_For_Billing.getType();
				}
				
				if(assignToTeamId.intValue() == RcmTeamEnum.INTERNAL_AUDIT.getId()) {
					newCycleStatus = ClaimStatusEnum.Pending_For_Review.getType();
				}
				
				message =rcmClaimLogServiceImpl.assignClaimToOtherTeamWithRemarkCommon(partialHeader, dto.getClaimUuid(),
						assignToTeamId, dto.getRemark(), claim,
						 assign, user, office,dto.getAttachmentsWithRemarks(),newCycleStatus,null,null);
				rcmClaimAssignmentRepo.save(assign);
				if (message!=null && message.equals("OtherTeam")) message="done";
				return message;
			
		      }
			}
		return null;
		
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
			
			RcmClaimAssignment assign1 = rcmClaimAssignmentRepo.findByClaimsClaimUuidAndActiveAndAssignedToNotNull(claimuuid, true);
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
		
		int currentStatusClosed=ClaimStatusEnum.Case_Closed.getId();
		int currentStatusVoided=ClaimStatusEnum.Voided.getId();
		
		if (partialHeader.getRole().equals(Constants.ASSOCIATE)) {
            count =rcmClaimRepository.allPendencyCountForUser(company.getUuid(),teamId,partialHeader.getJwtUser().getUuid(),currentStatusClosed);
			date =rcmClaimRepository.allPendencyDateCountForUser(company.getUuid(),teamId,partialHeader.getJwtUser().getUuid(),currentStatusClosed);
		}else {
			count =rcmClaimRepository.allPendencyCount(company.getUuid(),currentStatusClosed);
			date =rcmClaimRepository.allPendencyDateCount(company.getUuid(),currentStatusClosed);
		}
		//dto.setCount(count);
		//dto.setDateCount(date);
		dto.setOffices(officeRepo.findByCompanyAndActiveTrueOrderByNameAsc(company));
		List<RcmOfficeDto> offices=officeRepo.findByCompanyAndActiveTrueOrderByNameAsc(company);
		
		//Remove Office in case of associate 
		if (partialHeader.getRole().equals(Constants.ASSOCIATE)) {
			List<RcmOfficeDto> fil =new ArrayList<>();
			List<UserAssignOffice> assignoffices=userAssignOfficeRepo.findByUserUuidAndTeamId(partialHeader.getJwtUser().getUuid(),teamId);
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
    public String archiveActiveClaim(@RequestBody ClaimStatusUpdate dto,PartialHeader partialHeader,String... archiveClaimId) {
    	
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
			history.setClaim(claim);
			history.setCurrentState(Constants.CLAIM_ARCHIVE_PREFIX_CANNOT_SUBMITED);
			rcmClaimArchiveHistoryRepo.save(history);
			String claimId=archiveClaimId!=null && archiveClaimId.length>0?archiveClaimId[0]:date.getTime()+Constants.HYPHEN+Constants.ARCHIVE_PREFIX+claim.getClaimId();
			claim.setClaimId(claimId);
			claim.setCurrentState(Constants.CLAIM_ARCHIVE_PREFIX_CANNOT_SUBMITED);
			RcmUser updatedBy= userRepo.findByUuid(partialHeader.getJwtUser().getUuid()) ;
			claim.setUpdatedBy(updatedBy);
			rcmClaimRepository.save(claim);
			
			claimCycleService.createNewClaimCycle(claim,ClaimStatusEnum.Claim_Archived.getType(),
					ClaimStatusEnum.getById(claim.getCurrentStatus()).getType(),claim.getCurrentTeamId(),updatedBy);
			
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
			history.setClaim(claim);
			history.setCurrentState(Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED);
			rcmClaimArchiveHistoryRepo.save(history);
			claim.setCurrentState(Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED);
			
			claim.setClaimId(claimId);
			RcmUser updatedBy= userRepo.findByUuid(partialHeader.getJwtUser().getUuid()) ;
			claim.setUpdatedBy(updatedBy);
			rcmClaimRepository.save(claim);
			claimCycleService.createNewClaimCycle(claim,ClaimStatusEnum.Claim_UnArchived.getType(),
					ClaimStatusEnum.getById(claim.getNextAction()).getType(),claim.getCurrentTeamId(),updatedBy);
			
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
		RcmClaims claim = rcmClaimRepository.findByClaimUuid(dto.getClaimUuid());
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
	
	
	public Object saveClaimSectionDataAfterSubmission(CommonSectionsRequestBodyDto sectionRequestBody,
			PartialHeader partialHeader) throws Exception {

		Object response =null;
		boolean allSectionAccess = true;
		boolean allCheckValidation = true;
		boolean validateClaimRight = checkifCompanyIdMatchesList(partialHeader.getJwtUser().getUuid(),
				partialHeader.getCompany().getUuid());
		
		if ((sectionRequestBody.getNextActionRequiredInfoModel() != null
				&& sectionRequestBody.getNextActionRequiredInfoModel().getButtonType() != null)
				&& (sectionRequestBody.getNextActionRequiredInfoModel().getButtonType()
						.equals(Constants.BUTTON_TYPE_ARCHIVE)
						|| sectionRequestBody.getNextActionRequiredInfoModel().getButtonType()
								.equals(Constants.BUTTON_TYPE_ASSIGN_TO_TL))) {
			logger.error("Invalid button type");
			return false;
		}
		if (!validateClaimRight) {
			logger.error("claim not valid");
			return false;
		}
		RcmClaims claim = rcmClaimRepository.findByClaimUuid(sectionRequestBody.getClaimUuid());
		
		String[] claimSplit = claim.getClaimId().split("_");
		boolean isPrimary = false;
		int assoicatedClaimCurrentStatus = 0;
		if (("_" + claimSplit[1]).equals(ClaimTypeEnum.P.getSuffix())) {
			isPrimary = true;
		} else {
			isPrimary = false;
			Object sec = rcmClaimRepository.getClaimsUuidClaimIdSec(
					claim.getClaimId().split(ClaimTypeEnum.S.getSuffix())[0] + ClaimTypeEnum.P.getSuffix(),
					claim.getOffice().getUuid());
			if (sec != null) {
				Object s[] = (Object[]) sec;
				assoicatedClaimCurrentStatus = s[3] == null ? 0 : Integer.parseInt(s[3].toString());
			}
		}

		RcmClaimAssignment assign = rcmClaimAssignmentRepo.findByAssignedToUuidAndClaimsClaimUuidAndActive(
				partialHeader.getJwtUser().getUuid(), claim.getClaimUuid(), true);
		if (assign == null) {
			logger.error("claim not assigned to this user");
			// Not assigned to user
			return false;
		}
 
		if (claim.getCurrentState() == Constants.CLAIM_ARCHIVE_PREFIX_CANNOT_SUBMITED) {
			logger.error("claim already archived");
			// Not assigned to user
			return false;
		}
		
		if (isPrimary && claim.getCurrentStatus() == ClaimStatusEnum.Case_Closed.getId()) {
			logger.error("Primary is open but current status is closed");
			return false;
		}

		if (!isPrimary && (claim.getCurrentStatus() == ClaimStatusEnum.Case_Closed.getId()
				|| assoicatedClaimCurrentStatus != ClaimStatusEnum.Case_Closed.getId())) {
			logger.error("Primary is closed or secondary is also closed");
			return false;
		}
	
		List<SectionDto> sectionsData = masterService.getSections();

		RcmUser createdBy = userRepo.findByUuid(partialHeader.getJwtUser().getUuid());

		RcmTeam team = rcmTeamRepo.findById(partialHeader.getTeamId());

		for (Field field : CommonSectionsRequestBodyDto.class.getDeclaredFields()) {
			// add ignore fields in if condition
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
						if (response==null) break;
					} else {
						/*Will add validation latter if needed
						boolean checkValidation = rcmCommonServiceImpl.commonSectionInformationsForAllWithValidation(
								sectionRequestBody, partialHeader, sectionId, sectionsData);
						if (!checkValidation) {
							logger.error("validation fail!");
							response=false;
							allCheckValidation = false;
							break;
						}*/
					}
				}
			}
		}

		if (sectionRequestBody.isFinalSubmit() && allCheckValidation && allSectionAccess) {
			response = null;
			for (Field field : CommonSectionsRequestBodyDto.class.getDeclaredFields()) {
				// add ignore fields in if condition
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
						if (response==null)
							break;
					}
				}
			}
			if (response!=null && sectionRequestBody.isFinalSubmit() && sectionRequestBody.isMoveToNextTeam() ) {
				CurrentStatusAndNextActionDto currentDto= sectionRequestBody.getNextActionRequiredInfoModel();
				RcmOffice office = officeRepo.findByUuid(claim.getOffice().getUuid());
				String[] clT = claim.getClaimId().split("_");
				RcmClaims primaryCl=null;
				RcmClaims secondaryCl=null;
				boolean checkForPrimary=false;
				if (("_" + clT[1]).equals(ClaimTypeEnum.P.getSuffix())) {
					checkForPrimary =true;
					primaryCl=claim;
					//secondaryCl= rcmClaimRepository.findByClaimIdAndOffice(clT[0]+ClaimTypeEnum.S.getSuffix(), office);
					
				}else {
					secondaryCl=claim;
					//primaryCl=rcmClaimRepository.findByClaimIdAndOffice(clT[0]+ClaimTypeEnum.P.getSuffix(), office);
				}
				
				String newCycleStatus =currentDto.getCurrentClaimStatusRcm();//  ClaimMovementUtil.getNextStatus(assign.getCurrentTeamId().getId(), primaryCl, secondaryCl, checkForPrimary);
				int nextTeam =currentDto.getAssignToTeamId();// ClaimMovementUtil.getNextTeam(assign.getCurrentTeamId().getId(), primaryCl, secondaryCl, checkForPrimary);
				ClaimStatusEnum nextAction =ClaimStatusEnum.getByType(currentDto.getNextAction());// ClaimMovementUtil.getNextAction(assign.getCurrentTeamId().getId(), primaryCl, secondaryCl, checkForPrimary);
				// update old status
				ClaimCycle previousCycleData = clamCycleRepo.findFirstByClaimAndCurrentTeamIdOrderByCreatedDateDescIdDesc(claim,team);
				if (previousCycleData != null) {
					if (previousCycleData.getStatusUpdated()!=null && !previousCycleData.getStatusUpdated().isEmpty()) {
						previousCycleData.setStatusUpdated(newCycleStatus);
					} else {
						previousCycleData.setStatusUpdated(previousCycleData.getStatus());
					}
				}
				clamCycleRepo.save(previousCycleData);
				
				if (nextAction!=null) {
					/*
					   claim.setCurrentStatus(nextAction.getId());
				       rcmClaimRepository.save(claim);
				   if (checkForPrimary == false) {
					   //Mean we need to update Primary claim Status if not closed and move to next team.
					   primaryCl.setCurrentStatus(nextAction.getId());
				       rcmClaimRepository.save(primaryCl);
				   }*/
				   String assignActionName="Assign To Team";////Same we have in ui if change needed update that also (billing-claims.component.ts)
				   String claimTransfer=rcmClaimLogServiceImpl.assignClaimToOtherTeamWithRemarkCommon(partialHeader, sectionRequestBody.getClaimUuid(),
							nextTeam, currentDto.getRemarks(), claim, assign, createdBy, office, null,newCycleStatus,nextAction.getType(),assignActionName);
				   rcmClaimAssignmentRepo.save(assign);
				}else {
					logger.info("claim transfer response-> Wrong claim Status:"+currentDto.getNextAction()+" send for claim :"+claim.getClaimUuid() );
				}
				/*
				if (nextTeam!= -1) {
					String claimTransfer=rcmClaimLogServiceImpl.assignClaimToOtherTeamWithRemarkCommon(partialHeader, sectionRequestBody.getClaimUuid(),
							nextTeam, "Please Work on Claim", claim, assign, createdBy, office, null,newCycleStatus);
					logger.info("claim transfer response->" + claimTransfer);
					 if (checkForPrimary == false) {
						   //Mean we need to update Primary claim Status if not closed and move to next team.
						 claimTransfer=rcmClaimLogServiceImpl.assignClaimToOtherTeamWithRemarkCommon(partialHeader, sectionRequestBody.getClaimUuid(),
									nextTeam, "Please Work on Claim", primaryCl, assign, createdBy, office, null,newCycleStatus);
						 logger.info("claim transfer response->" + claimTransfer);
							
					   }
					
				}else {
					claimCycleService.createNewClaimCycle(claim, newCycleStatus, null, createdBy);
				}*/
				
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
	  File initialFile = new File(eobLinkFolder+File.separator+fileName);
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
    
    public List<ReconciliationResponseDto> fetchReconciliationData(ReconciliationDto dto,
			PartialHeader partialHeader) {
		
		RcmOffice office= officeRepo.findByUuid(dto.getOfficeUuid());
		//Call Rule Engine 
		String date1= dto.getStartDate()!=null?Constants.SDF_CredentialSheetAnes.format(dto.getStartDate()):"";
		String date2= dto.getEndDate()!=null?Constants.SDF_CredentialSheetAnes.format(dto.getEndDate()):"";
				
		List<ClaimReconcillationDto> primaryUnbilledClaims =ruleEngineService.fetchReconcillationDataFromES(office,"PrimaryUnbilled",date1,date2);//B23
		List<ClaimReconcillationDto> secondaryUnbilledClaims =ruleEngineService.fetchReconcillationDataFromES(office,"SecondaryUnsubmitted",date1,date2);//c25
		List<ClaimReconcillationDto> primaryOpenClaims =ruleEngineService.fetchReconcillationDataFromES(office,"PrimaryOpen",date1,date2);//B24
		List<ClaimReconcillationDto> secondaryOpenClaims =ruleEngineService.fetchReconcillationDataFromES(office,"SecondaryOpen",date1,date2);//C24
		List<ClaimReconcillationDto> secondaryUnsubmittedClaims =ruleEngineService.fetchReconcillationDataFromES(office,"SecondaryUnbilled",date1,date2);//c23
		
		List<ClaimReconcillationDto> primaryCloseClaims =ruleEngineService.fetchReconcillationDataFromES(office,"PrimaryClose",date1,date2);//B26
		List<ClaimReconcillationDto> secondaryCloseClaims =ruleEngineService.fetchReconcillationDataFromES(office,"SecondaryClose",date1,date2);//C26
		
		
		List<ReconciliationResponseDto> dataList= new ArrayList<>();
		ReconciliationResponseDto resposeDto = null;
		
		///Compare Data from RCM Tool.
		//Primary Unbilled
		resposeDto = new ReconciliationResponseDto();
		resposeDto= prepaireReconcillationData("Primary Unbilled",resposeDto, office, primaryUnbilledClaims, true);
		dataList.add(resposeDto);
		//Secondary Unbilled - (Primary Unbilled or Primary Open)
		resposeDto = new ReconciliationResponseDto();
		resposeDto=  prepaireReconcillationData("Secondary Unbilled (Primary Unbilled/Open)", resposeDto, office, secondaryUnbilledClaims,false);
		dataList.add(resposeDto);
		
		resposeDto = new ReconciliationResponseDto();
		resposeDto= prepaireReconcillationData("Primary Open",resposeDto, office, primaryOpenClaims, true);
		dataList.add(resposeDto);
		
		resposeDto = new ReconciliationResponseDto();
		resposeDto= prepaireReconcillationData("Secondary Open",resposeDto, office, secondaryOpenClaims, false);
		dataList.add(resposeDto);
		
		resposeDto = new ReconciliationResponseDto();
		resposeDto= prepaireReconcillationData("Primary Closed",resposeDto, office, primaryCloseClaims, true);
		dataList.add(resposeDto);
		
		resposeDto = new ReconciliationResponseDto();
		resposeDto= prepaireReconcillationData("Secondary Closed",resposeDto, office, secondaryCloseClaims, false);
		dataList.add(resposeDto);
		
		resposeDto = new ReconciliationResponseDto();
		resposeDto=  prepaireReconcillationData("Secondary Unbilled (Primary Closed)", resposeDto, office, secondaryUnsubmittedClaims,false);
		dataList.add(resposeDto);
		
	  return dataList;
	}
	
	/*private ReconciliationResponseDto prepaireReconcillationDataPrimarySecondaryStatuses(String title,ReconciliationResponseDto resposeDto,RcmOffice office,List<ClaimReconcillationDto> datas,
			int typeQuery){
		// _13767_P|P
		//Secondary Unbilled  and primary Open+ unBilled
		String passP="",pipe="",passS="";
		resposeDto.setTitle(title);
		List<String> claimsP= new ArrayList<>();
		List<String> claimsS= new ArrayList<>();
		List<String> claimsOrignal= new ArrayList<>();
		for(ClaimReconcillationDto x:datas) {
			passP=passP+pipe+"_"+x.getClaimId()+"_"+"P";
			passS=passP+pipe+"_"+x.getClaimId()+"_"+"S";
	        pipe="|";
	        claimsP.add(x.getClaimId()+"_"+"P");
	        claimsS.add(x.getClaimId()+"_"+"S");
	        claimsOrignal.add(x.getClaimId());
		}
		passS = "'"+passS+"'";
		passP = "'"+passP+"'";
		System.out.println(passP);
		
		Set<Discrepancy> foundClaims = new HashSet<>();
		//Set<String> commonClaims = new HashSet<>();
		List<ReconcillationClaimDto> unArchsPUB=null;
		List<ReconcillationClaimDto> unArchsSUB=null;
		
		if (typeQuery ==1) {
			unArchsPUB=rcmClaimRepository.getClaimbyOfficeAndClaimIdsUnarchivedAndWithStatusEsUpdatedOPenUnbilled(office.getUuid(), claimsP);
			unArchsSUB=rcmClaimRepository.getClaimbyOfficeAndClaimIdsUnarchivedAndWithStatusEsUpdated(office.getUuid(), claimsS,"Unbilled");
		}else if (typeQuery ==2) {
			unArchsPUB=rcmClaimRepository.getClaimbyOfficeAndClaimIdsUnarchivedAndWithStatusEsUpdated(office.getUuid(), claimsP,"Closed");
			unArchsSUB=rcmClaimRepository.getClaimbyOfficeAndClaimIdsUnarchivedAndWithStatusEsUpdated(office.getUuid(), claimsS,"Unbilled");
		}
		final List<ReconcillationClaimDto> unArchsPUBFINAL=unArchsPUB;
		final List<ReconcillationClaimDto> unArchsSUBFINAL=unArchsSUB;
		List<ReconcillationClaimDto> archs = rcmClaimRepository.getClaimbyOfficeAndClaimIdsArchived(office.getUuid(), passS);
		List<ReconcillationClaimDto> issueunArchs=rcmClaimRepository.getClaimInIssueClaimByClaimIdAndOfficeUnarchived(office.getUuid(), claimsS);
		List<ReconcillationClaimDto> issuearchs= rcmClaimRepository.getClaimInIssueClaimByClaimIdAndOfficeArchived(office.getUuid(), passS);
		int inEs= datas.size();
        resposeDto.setClaimsES(inEs);
        //
        Set<Discrepancy> unArchsPUB1= new HashSet<>();
        Set<Discrepancy> unArchsSUB1= new HashSet<>();
        Discrepancy discrepancy=null;
        for(ReconcillationClaimDto x:unArchsPUB) {
        	discrepancy =new Discrepancy();
			discrepancy.setClaimUUid(x.getClaimUuid());
			discrepancy.setPatientId(x.getPatientId());
			discrepancy.setArchived(false);
			if (x.getClaimId().endsWith("_P")) {
				discrepancy.setPrimary(true);
			}else {
				discrepancy.setPrimary(false);
			}
			discrepancy.setPatientName(x.getPatientName());
			discrepancy.setClaimId(x.getClaimId().split("_")[0]);
        	unArchsPUB1.add(discrepancy);
		}
        for(ReconcillationClaimDto x:unArchsSUB) {
        	discrepancy =new Discrepancy();
			discrepancy.setClaimUUid(x.getClaimUuid());
			discrepancy.setPatientId(x.getPatientId());
			discrepancy.setArchived(false);
			if (x.getClaimId().endsWith("_P")) {
				discrepancy.setPrimary(true);
			}else {
				discrepancy.setPrimary(false);
			}
			discrepancy.setPatientName(x.getPatientName());
			discrepancy.setClaimId(x.getClaimId().split("_")[0]);
        	unArchsSUB1.add(discrepancy);
		}
        
        //Set<Discrepancy> commonClaimsinES=CollectionUtils.intersection(unArchsPUB1, unArchsSUB1).stream().collect(Collectors.toSet());
        //Set<Discrepancy> differences = new HashSet<>((CollectionUtils.removeAll(unArchsPUB1, unArchsSUB1)));
        Set<Discrepancy> commonClaimsinES=new HashSet<>();
        Set<Discrepancy> differences = new HashSet<>();
        resposeDto.setClaimsRCM(commonClaimsinES.size());
        //Create Common Claims
        for(Discrepancy dis:unArchsPUB1 ) {
        	Optional<Discrepancy> comon=unArchsSUB1.stream().filter(x->{
        		return x.getClaimId().split("_")[0].equals(dis.getClaimId().split("_")[0]);
        	}).findFirst();
        	if (comon.isPresent()) {
        		commonClaimsinES.add(dis);
        	}else {
        		differences.add(dis);
        	}
        }
        for(Discrepancy dis:unArchsSUB1 ) {
        	Optional<Discrepancy> comon=unArchsPUB1.stream().filter(x->{
        		return x.getClaimId().split("_")[0].equals(dis.getClaimId().split("_")[0]);
        	}).findFirst();
        	if (comon.isPresent()) {
        		
        	}else {
        		differences.add(dis);
        	}
        }
        
        Set<Discrepancy> discrepancies=new HashSet<>();
        differences.forEach(data -> {
            Optional<ReconcillationClaimDto> dx =unArchsPUBFINAL.stream().filter(x->{
            		return x.getClaimId().split("_")[0].equals(data.getClaimId().split("_")[0]);
            	}).findFirst();
            if (dx.isPresent()) {
            	if (!claimUUidExistInList(discrepancies, data.getClaimUUid())) {
            		Discrepancy ds= new Discrepancy();
            		ds.setClaimUUid(data.getClaimUUid());
            		ds.setPatientId(data.getPatientId());
            		ds.setArchived(data.isArchived());
            		ds.setPrimary(data.isPrimary());
        			ds.setPatientName(data.getPatientName());
            		ds.setClaimId(data.getClaimId().split("_")[0]);
                	discrepancies.add(ds);
            	}
            }
            	
            
            Optional<ReconcillationClaimDto> dx1 =unArchsSUBFINAL.stream().filter(x->{
        		return x.getClaimId().split("_")[0].equals(data.getClaimId().split("_")[0]);
        	}).findFirst();
            if (dx1.isPresent()) {
            	Discrepancy ds= new Discrepancy();
        		ds.setClaimUUid(dx1.get().getClaimUuid());
        		ds.setPatientId(dx1.get().getPatientId());
        		ds.setPatientName(dx1.get().getPatientName());
        		ds.setClaimId(dx1.get().getClaimId().split("_")[0]);
        		ds.setArchived(false);
    			if (dx1.get().getClaimId().endsWith("_P")) {
    				ds.setPrimary(true);
    			}else {
    				ds.setPrimary(false);
    			}
        		
            	discrepancies.add(ds);
            	//discrepancies.add(dx1.get().getClaimId().split("_")[0]);
            	}
            
           
        });
       
        for(ReconcillationClaimDto x:unArchsPUB) {
        	Discrepancy ds= new Discrepancy();
    		ds.setClaimUUid(x.getClaimUuid());
    		ds.setPatientId(x.getPatientId());
    		ds.setPatientName(x.getPatientName());
    		ds.setClaimId(x.getClaimId().split("_")[0]);
    		ds.setArchived(false);
			if (x.getClaimId().endsWith("_P")) {
				ds.setPrimary(true);
			}else {
				ds.setPrimary(false);
			}
    		unArchsPUB1.add(ds);
        	
		}
       	
		Set<Discrepancy> uploadError=new HashSet<>();
		for(ReconcillationClaimDto x:issueunArchs) {
			Discrepancy ds= new Discrepancy();
    		ds.setClaimUUid(x.getClaimUuid());
    		ds.setPatientId(x.getPatientId());
    		ds.setPatientName(x.getPatientName());
    		ds.setClaimId(x.getClaimId().split("_")[0]);
			uploadError.add(ds);
			foundClaims.add(ds);
		}
		
		
		
		for(ReconcillationClaimDto x:archs) {
			
			
			Discrepancy ds= new Discrepancy();
    		ds.setClaimUUid(x.getClaimUuid());
    		ds.setPatientId(x.getPatientId());
    		ds.setPatientName(x.getPatientName());
    		ds.setArchived(true);
			if (x.getClaimId().endsWith("_P")) {
				ds.setPrimary(true);
			}else {
				ds.setPrimary(false);
			}
			if (x.getClaimId().indexOf("_arc_")>=0)ds.setClaimId(x.getClaimId().split("_arc_")[1].split("_")[0]);
			else ds.setClaimId(x.getClaimId().split("_")[0]);
			discrepancies.add(ds);
			foundClaims.add(ds);
			
		}
		
		
		for(ReconcillationClaimDto x:issuearchs) {
			Discrepancy ds= new Discrepancy();
    		ds.setClaimUUid(x.getClaimUuid());
    		ds.setPatientId(x.getPatientId());
    		ds.setPatientName(x.getPatientName());
    		
			if (x.getClaimId().indexOf("_arc_")>=0) {
				ds.setClaimId(x.getClaimId().split("_arc_")[1].split("_")[0]);
			}
			else {
				ds.setClaimId(x.getClaimId().split("_")[0]);
			}
			uploadError.add(ds);
			
		}
		foundClaims.addAll(uploadError);
		resposeDto.setClaimInUploadErrors(uploadError);
		
		resposeDto.setDiscrepancies(discrepancies);
		resposeDto.setOffice(office.getName());
		
		Set<com.tricon.rcm.dto.ReconcillationClaimDto> discrepanciesAll= new HashSet<>();
		differences.forEach(data -> {
			
            Optional<ReconcillationClaimDto> dx =unArchsPUBFINAL.stream().filter(x->{
            		return x.getClaimId().split("_")[0].equals(data.getClaimId().split("_")[0]);
            	}).findFirst();
            if (dx.isPresent()) {
            	com.tricon.rcm.dto.ReconcillationClaimDto q= new com.tricon.rcm.dto.ReconcillationClaimDto();
            	BeanUtils.copyProperties(dx.get(), q);
            	discrepanciesAll.add(q);
            	
            }
            
            Optional<ReconcillationClaimDto> dx1 =unArchsSUBFINAL.stream().filter(x->{
        		return x.getClaimId().split("_")[0].equals(data.getClaimId().split("_")[0]);
        	}).findFirst();
            if (dx1.isPresent()) {
        	   com.tricon.rcm.dto.ReconcillationClaimDto q= new com.tricon.rcm.dto.ReconcillationClaimDto();
            	BeanUtils.copyProperties(dx1.get(), q);
            	discrepanciesAll.add(q);
           }
            
            
           Optional<com.tricon.rcm.dto.ReconcillationClaimDto> dx2 = discrepanciesAll.stream().filter(x->{
       		return x.getClaimId().split("_")[0].equals(data);
       	    }).findFirst();
           if (!dx2.isPresent()) {
        	   com.tricon.rcm.dto.ReconcillationClaimDto q= new com.tricon.rcm.dto.ReconcillationClaimDto();
            	q.setClaimId(data.getClaimId());
            	discrepanciesAll.add(q);
           }
        });
		
		//Set<String> notFound=new HashSet<>();
		claimsOrignal.removeAll(foundClaims);
		
		Set<String> tSet = new TreeSet<String>(claimsOrignal);
		resposeDto.setDiscrepanciesAll(discrepanciesAll);
		resposeDto.setClaimsNotFoundRCM(tSet);
		return resposeDto;
	}*/
	
	
	
	private ReconciliationResponseDto prepaireReconcillationData(String title,ReconciliationResponseDto resposeDto,RcmOffice office,List<ClaimReconcillationDto> datas,boolean primary ){
		// _13767_P|P
		String pass="",pipe="";
		resposeDto.setTitle(title);
		List<String> claimsES= new ArrayList<>();
		List<String> claimsOrignalES= new ArrayList<>();
		resposeDto.setOffice(office.getName());
		
		List<String> claimsRcm= new ArrayList<>();
		//List<String> claimsOrignalRcm= new ArrayList<>();
		
		for(ClaimReconcillationDto x:datas) {
			pass=pass+pipe+"_"+x.getClaimId()+"_"+(primary?"P":"S");
	        pipe="|";
	        claimsES.add(x.getClaimId()+"_"+(primary?"P":"S"));
	        claimsOrignalES.add(x.getClaimId());
		}
		
		pass = "'"+pass+"'";
		System.out.println(pass);
		
		//Set<Discrepancy> foundClaims = new HashSet<>();
		String type="";
		boolean pend=true;
		boolean isPrimary=true;
		if (title.equals("Primary Unbilled")) {
			type="%_P";
			pend=true;//1. Billing Team has not submitted Primary Claim yet
		}
		if (title.equals("Secondary Unbilled (Primary Closed)")) {
			type="%_S";
			pend=true;//1. Billing Team has not submitted Secondary Claim yet.
			
		}
		if (title.equals("Secondary Unbilled (Primary Unbilled/Open)")) {
			type="%_S";
			pend=true;//1. Billing Team has not submitted Secondary Claim yet.
			isPrimary =false;
		}
		if (title.equals("Primary Open") || title.equals("Primary Closed")) {
			type="%_P";
			pend=false;//1. Billing Team has submitted the claim.
		}
		if (title.equals("Secondary Open") || title.equals("Secondary Closed")) {
			type="%_S";
			pend=false;//1. Billing Team has submitted the claim.
		}
		List<ReconcillationClaimDto> rcmClaims = null;
		if (title.equals("Primary Open") | title.equals("Secondary Open")) {
			rcmClaims = rcmClaimRepository.getClaimbyOfficeAndNotArchivedPrimaryorSecondarySubmitedorNotEsUpdatedStatus(office.getUuid(), type, pend,ClaimStatusSearchEnum.STATUS_OPEN.getStatus());
		}else if (title.equals("Primary Closed") | title.equals("Secondary Closed")) {
			rcmClaims = rcmClaimRepository.getClaimbyOfficeAndNotArchivedPrimaryorSecondarySubmitedorNotEsUpdatedStatus(office.getUuid(), type, pend,ClaimStatusSearchEnum.STATUS_CLOSED.getStatus());
					
		}else {
			rcmClaims = rcmClaimRepository.getClaimbyOfficeAndNotArchivedPrimaryorSecondarySubmitedorNot(office.getUuid(), type, pend);
					
		}
		rcmClaims.stream().map(ReconcillationClaimDto::getClaimId).forEach(claimsRcm::add);
		if (claimsRcm.size()>0) {
			List<ReconcillationClaimDto> issueunArchsRcm=rcmClaimRepository.getClaimInIssueClaimByClaimIdAndOfficeUnarchived(office.getUuid(), claimsRcm);
			for(ReconcillationClaimDto x: issueunArchsRcm) {
				rcmClaims.removeIf(y->
					y.getClaimId().equals(x.getClaimId()));
			}
		}
		if (title.equals("Secondary Unbilled (Primary Unbilled/Open)")) {
		//Claim Status in PMS is "Open" for Primary claim or Billing Team has not submitted the primary claim yet
			List<String> primaryClaimsforSecondayClaims = new ArrayList<>();
			for(ReconcillationClaimDto x: rcmClaims) {
				primaryClaimsforSecondayClaims.add(x.getClaimId().replace("_S","_P"));
				
			  }
			if (primaryClaimsforSecondayClaims.size()>0) {
				List<ReconcillationClaimDto> rcmClaimsPrimary = rcmClaimRepository.getClaimbyOfficeAndClaimIdsEsUpdatedStatusOrNotSubmittedByBilling(office.getUuid(), type, ClaimStatusSearchEnum.STATUS_OPEN.getStatus());
				for(ReconcillationClaimDto x: rcmClaimsPrimary) {
					rcmClaims.removeIf(y->
					y.getClaimId().equals(x.getClaimId().replace("_P","_S")));
				}
			}
		}
		if (title.equals("Secondary Unbilled (Primary Closed)")) {
		//Claim Status in PMS is "Open" for Primary claim or Billing Team has not submitted the primary claim yet
			List<String> primaryClaimsforSecondayClaims = new ArrayList<>();
			for(ReconcillationClaimDto x: rcmClaims) {
				primaryClaimsforSecondayClaims.add(x.getClaimId().replace("_S","_P"));
				
			  }
			if (primaryClaimsforSecondayClaims.size()>0) {
				List<ReconcillationClaimDto> rcmClaimsPrimary = rcmClaimRepository.getClaimbyOfficeAndClaimIdsEsUpdatedStatus(office.getUuid(), type, ClaimStatusSearchEnum.STATUS_CLOSED.getStatus());
				for(ReconcillationClaimDto x: rcmClaimsPrimary) {
					rcmClaims.removeIf(y->
					y.getClaimId().equals(x.getClaimId().replace("_P","_S")));
				}
			}
		}		
		
		resposeDto.setClaimsRCM(rcmClaims.size());
		List<com.tricon.rcm.dto.ReconcillationClaimDto> allRcmClaims=  new ArrayList<> ();
		for(ReconcillationClaimDto x: rcmClaims) {
			com.tricon.rcm.dto.ReconcillationClaimDto n = new com.tricon.rcm.dto.ReconcillationClaimDto();
			n.setClaimId(x.getClaimId());
			n.setClaimUuid(x.getClaimUuid());
			n.setPatientId(x.getPatientId());
			n.setPatientName(x.getPatientName());
			allRcmClaims.add(n);
		}
		
		int inEs= datas.size();
		resposeDto.setClaimsES(inEs);
		
		Set<com.tricon.rcm.dto.ReconcillationClaimDto> claimsNotFoundPMS = new HashSet<>();
		for(com.tricon.rcm.dto.ReconcillationClaimDto d: allRcmClaims) {
        List<?> l= datas.stream().filter(x->(x.getClaimId()+"_"+(primary?"P":"S"))
        		.equals(d.getClaimId())).collect(Collectors.toList());
        if (l.size()==0) claimsNotFoundPMS.add(d);
		}
		
		Set<com.tricon.rcm.dto.ReconcillationClaimDto> claimsNotFoundRCM = new HashSet<>();
		for(ClaimReconcillationDto d: datas) {
	        List<?> l= allRcmClaims.stream().filter(x->x.getClaimId()
	        		.equals(d.getClaimId()+"_"+(primary?"P":"S"))).collect(Collectors.toList());
	        if (l.size()==0) {
	        	com.tricon.rcm.dto.ReconcillationClaimDto s= new com.tricon.rcm.dto.ReconcillationClaimDto();
	        	s.setClaimId(d.getClaimId()+"_"+(primary?"P":"S"));
	        	s.setStatusEsUpdated(d.getStatus());
	        	s.setPatientId(d.getPatientId());
	        	claimsNotFoundRCM.add(s);
	        }
		}
		
		
		resposeDto.setClaimsNotFoundPMS(claimsNotFoundPMS);
		resposeDto.setClaimsNotFoundRCM(claimsNotFoundRCM);
		
		//List<ReconcillationClaimDto> unArchs=rcmClaimRepository.getClaimbyOfficeAndClaimIdsUnarchived(office.getUuid(), claims);
		List<ReconcillationClaimDto> archs = rcmClaimRepository.getClaimbyOfficeAndClaimIdsArchived(office.getUuid(), pass);
		List<ReconcillationClaimDto> issueunArchs=rcmClaimRepository.getClaimInIssueClaimByClaimIdAndOfficeUnarchived(office.getUuid(), claimsES);
		//List<ReconcillationClaimDto> issuearchs= rcmClaimRepository.getClaimInIssueClaimByClaimIdAndOfficeArchived(office.getUuid(), pass);
		
  	Set<Discrepancy> uploadError=new HashSet<>();
		Discrepancy discrepancy=null;
		for(ReconcillationClaimDto x:issueunArchs) {
			discrepancy =new Discrepancy();
			discrepancy.setClaimUUid(x.getClaimUuid());
			discrepancy.setPatientId(x.getPatientId());
			discrepancy.setPatientName(x.getPatientName());
			discrepancy.setClaimId(x.getClaimId().split("_")[0]);
			uploadError.add(discrepancy);
			//foundClaims.add(discrepancy);
		}
		resposeDto.setClaimInUploadErrors(uploadError);
		
		Set<Discrepancy> archivedClaims=new HashSet<>();
		
		for(ReconcillationClaimDto x:archs) {
			discrepancy =new Discrepancy();
			discrepancy.setClaimUUid(x.getClaimUuid());
			discrepancy.setPatientId(x.getPatientId());
			discrepancy.setPatientName(x.getPatientName());
			discrepancy.setClaimId(x.getClaimId());
			discrepancy.setArchived(true);
					archivedClaims.add(discrepancy);
		}
		resposeDto.setClaimArchived(archivedClaims);
		
		return resposeDto;
	}
	
	private List<com.tricon.rcm.dto.customquery.KeyValueDto> fetchLatestCommentsByClaimUuid(List<String> claimUuid,
			int teamId) {
		List<com.tricon.rcm.dto.customquery.KeyValueDto> comments = rcmClaimAssignmentRepo
				.findLatestClaimCommentByOtherTeam(claimUuid, teamId);
		return comments;
	}
	
	private void populateClaimListWithComments(List<FreshClaimDataViewDto> listView, int teamId) {
		int ctr = 0;
		while (true) {
			List<String> claimUuids = listView.stream().skip(ctr).limit(Constants.MAX_COMMENT_DATA_PER_PAGE)
					.map(x -> x.getUuid()).collect(Collectors.toList());
			List<com.tricon.rcm.dto.customquery.KeyValueDto> comments = this.fetchLatestCommentsByClaimUuid(claimUuids,
					teamId);
			listView.forEach(data -> {
				com.tricon.rcm.dto.customquery.KeyValueDto dto = comments.stream()
						.filter(x -> x.getKeyy().equals(data.getUuid())).findAny().orElse(null);
				if (dto != null) {
					data.setLastTeamRemark(dto.getValue());
				}
			});
			ctr = ctr + Constants.MAX_COMMENT_DATA_PER_PAGE;
			if (claimUuids.size() == 0)
				break;
		}
	}
	
	private List<AssignOfficeResponseDto> fetchTeamsDataFromUserAssignOffice(String officeUuid) {
		List<AssignOfficeResponseDto> dataList = null;
		List<AssignOfficeDto> data = userAssignRepo.findTeamsByOffice(officeUuid);
		if (!data.isEmpty()) {
			dataList = new ArrayList<>();
			for (AssignOfficeDto x : data) {
				AssignOfficeResponseDto dto = new AssignOfficeResponseDto();
				if (!StringUtils.isNoneBlank(x.getUserUuid())) {
					dto.setOfficeId(x.getOfficeUuid());
					dto.setTeamId(x.getTeamId());
					dto.setTeamName(x.getTeamName());
					dto.setUserExist(false);
					dto.setUserId(x.getUserUuid());
					dataList.add(dto);
				} else {
					dto.setOfficeId(x.getOfficeUuid());
					dto.setTeamId(x.getTeamId());
					dto.setTeamName(x.getTeamName());
					dto.setUserExist(true);
					dto.setUserId(x.getUserUuid());
					dataList.add(dto);
				}
			}
		}
		return dataList;
	}
	
	private boolean claimUUidExistInList(Set<Discrepancy> discrepancies, String claimUUid) {
		
		Optional<Discrepancy> matchingObject = discrepancies.stream().
			    filter(p -> p.getClaimUUid().equals(claimUUid)).
			    findFirst();
		if (matchingObject.isPresent()) return true;
		else return false;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public Object transferClaimPostingToAging(ClaimTransferDto dto, PartialHeader partialHeader) throws Exception {
		String message = "";
		boolean validateClaimRight = checkifCompanyIdMatchesList(partialHeader.getJwtUser().getUuid(),
				partialHeader.getCompany().getUuid());
		if (!validateClaimRight) {
			return null;
		}
		List<String> claimUuids = dto.getClaimUuid();
		Map<String, RcmOffice> officeCache = new HashMap<>();
		for (String claimId : claimUuids) {
			RcmClaims claim = rcmClaimRepository.findByClaimUuid(claimId);
			RcmUser user = userRepo.findByUuid(partialHeader.getJwtUser().getUuid());
			RcmClaimAssignment assign = rcmClaimAssignmentRepo.findByAssignedToUuidAndClaimsClaimUuidAndActive(
					partialHeader.getJwtUser().getUuid(), claimId, true);
			if (assign == null) {
				logger.error("Not assigned to user:" + partialHeader.getJwtUser().getEmail() + "");
				continue;
			}
			String officeUuid = claim.getOffice().getUuid();
			RcmOffice office = officeCache.computeIfAbsent(officeUuid, id -> officeRepo.findByUuid(id));
			if (validateClaimRight) {
				if (claim.getCurrentStatus() == ClaimStatusEnum.Case_Closed.getId()) {
					logger.error("Claim Already Closed:"+claimId);
				} else if (claim.getCurrentState() == Constants.CLAIM_ARCHIVE_PREFIX_CANNOT_SUBMITED) {
					logger.error("Claim is Archived:"+claimId);
				}

				else if (assign.getCurrentTeamId().getId() == Constants.FROMPOSTINGTOAGING) {
					logger.error("Claim is Already in Aging:"+claimId);
				} else {
					message = rcmClaimLogServiceImpl.assignClaimToOtherTeamWithRemarkCommon(partialHeader,
							claim.getClaimUuid(), Constants.FROMPOSTINGTOAGING,dto.getRemarks(), claim, assign, user,
							office, null, ClaimStatusEnum.Billed.getType(),
							ClaimStatusEnum.Need_to_Follow_Up.getType(), ClaimStatusEnum.Need_to_Follow_Up.getType());
					
					rcmClaimRepository.save(claim);
				}
			}
		}
		return message;
	}

	public RcmResponseMessageDto checkAnyTLOrAssoExist(ListOfClaimsDto dto, PartialHeader partialHeader)
			throws Exception {
		List<String> claims = dto.getClaimUuids();
		RcmResponseMessageDto response = null;
		for (String claim : claims) {
			response = this.findTeamLeadExistForOtherTeams(new FindTLExistDto(dto.getTeamId(), claim),
					partialHeader.getJwtUser());
			if (!response.isResponseStatus())
				break;
		}
		return response;
	}
	
	private String getToothOrSurfaceFromClaimDetails(List<RcmClaimDetail> cList,boolean byTooth) {
		String data="NA";
		if (cList!=null) {
			List<String> tooths = cList.stream()
					.filter(i -> i.getTooth()!=null)
					.map(RcmClaimDetail::getTooth).collect(Collectors.toList());
			List<String> surfaces = cList.stream()
					.filter(i -> i.getSurface()!=null)
					.map(RcmClaimDetail::getSurface).collect(Collectors.toList());
			if (tooths.size()>0 && byTooth)data=String.join(",", tooths);
			if (surfaces.size()>0 && !byTooth)data=String.join(",", surfaces);
		}
		return data;
	}
}
