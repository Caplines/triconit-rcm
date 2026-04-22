package com.tricon.rcm.api.controller;

import java.util.List;



import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tricon.rcm.dto.AllPendencyReportDto;
import com.tricon.rcm.dto.AssigmentClaimListDto;
import com.tricon.rcm.dto.AssignUnAssignResAsignClaimsDto;
import com.tricon.rcm.dto.CaplineIVFFormDto;
import com.tricon.rcm.dto.ClaimAssignDto;
import com.tricon.rcm.dto.ClaimAssignWithRemarkAndTeam;
import com.tricon.rcm.dto.ClaimAssignmentsOfficeDto;
import com.tricon.rcm.dto.ClaimEditDto;
import com.tricon.rcm.dto.ClaimFromSheet;
import com.tricon.rcm.dto.KeyValueDto;
import com.tricon.rcm.dto.ListOfClaimsCountsDto;
import com.tricon.rcm.dto.ListOfClaimsDto;
import com.tricon.rcm.dto.PartialHeader;
import com.tricon.rcm.dto.RcmArchiveClaimsDto;
import com.tricon.rcm.dto.RcmClaimsServiceRuleValidationDto;
import com.tricon.rcm.dto.RcmIVfDto;
import com.tricon.rcm.dto.RcmIssuClaimPaginationDto;
import com.tricon.rcm.dto.RcmResponseMessageDto;
import com.tricon.rcm.dto.RcmUnarchiveClaimsDto;
import com.tricon.rcm.dto.ReconciliationDto;
import com.tricon.rcm.dto.ReconciliationResponseDto;
import com.tricon.rcm.dto.SearchParamDto;
import com.tricon.rcm.dto.UnArchiveClaimDto;
import com.tricon.rcm.dto.UnArchivedResponseDto;
import com.tricon.rcm.dto.ClaimNotesDto;
import com.tricon.rcm.dto.ClaimProductionLogDto;
import com.tricon.rcm.dto.ClaimRemarkDto;
import com.tricon.rcm.dto.ClaimRuleRemarkDto;
import com.tricon.rcm.dto.ClaimRuleVaidationValueDto;
import com.tricon.rcm.dto.ClaimRuleValidationsDto;
import com.tricon.rcm.dto.ClaimSourceDto;
import com.tricon.rcm.dto.ClaimStatusUpdate;
import com.tricon.rcm.dto.ClaimSubDet;
import com.tricon.rcm.dto.ClaimSubmissionDto;
import com.tricon.rcm.dto.ClientSectionMappingDto;
import com.tricon.rcm.dto.CommonSectionsRequestBodyDto;
import com.tricon.rcm.dto.EobLink;
import com.tricon.rcm.dto.FindRulesDto;
import com.tricon.rcm.dto.FindTLExistDto;
import com.tricon.rcm.dto.FreshClaimDataImplDto;
import com.tricon.rcm.dto.GenericResponse;
import com.tricon.rcm.dto.customquery.AssignFreshClaimLogsImplDto;
import com.tricon.rcm.dto.customquery.ClaimRemarksDto;
import com.tricon.rcm.dto.customquery.ClaimRuleRemarksDto;
import com.tricon.rcm.dto.customquery.ClaimSteps;
import com.tricon.rcm.dto.customquery.ClaimTransferDto;
import com.tricon.rcm.dto.customquery.ClientCustomDto;
import com.tricon.rcm.dto.customquery.CompanyIdAndNameDto;
import com.tricon.rcm.dto.customquery.FreshClaimDataDto;
import com.tricon.rcm.dto.customquery.FreshClaimDetailsDto;
import com.tricon.rcm.dto.customquery.FreshClaimLogDto;
import com.tricon.rcm.dto.customquery.ProductionDto;
import com.tricon.rcm.dto.customquery.RcmClaimSubmissionDto;
import com.tricon.rcm.dto.customquery.RuleEngineClaimDto;
import com.tricon.rcm.dto.customquery.UserClaimsAssignmentResponseDto;
import com.tricon.rcm.enums.RcmTeamEnum;
import com.tricon.rcm.service.impl.ClaimSectionImpl;
import com.tricon.rcm.service.impl.ClaimServiceImpl;
import com.tricon.rcm.service.impl.RcmCommonServiceImpl;
import com.tricon.rcm.service.impl.RuleEngineService;
import com.tricon.rcm.util.MessageConstants;

import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin
public class RcmController extends BaseHeaderController{

	private final Logger logger = LoggerFactory.getLogger(RuleEngineService.class);

	@Autowired
	ClaimServiceImpl claimServiceImpl;

	@Autowired
	Environment ev;

	@Value("${data.listClaims.maxRecordsPerPage:200}")
	private int maxRecordsPerPage;

	@Value("${data.listClaims.defaultRecordsPerPage:50}")
	private int defaultRecordsPerPage;

	@Autowired
	RcmCommonServiceImpl rcmCommonServiceImpl;
	
	@Autowired
	ClaimSectionImpl claimSection;
	@Autowired
	RcmCommonServiceImpl rcmCommonService;


	/**
	 * Fetch Claims From Eagle Soft or Google Sheet
	 * 
	 * @param dto
	 * @return
	 */
	
	@ApiOperation(value = "Api For Fetching Claims From  ES or GSheet", response = String.class, responseContainer = "Map")
	@PostMapping("/api/fetch-claims-from-source")
	@PreAuthorize("hasAnyRole('TL','SUPER_ADMIN','REPORTING','ADMIN','ASSO')")
	public ResponseEntity<Object> fetchClaimsFromSource(@RequestBody ClaimSourceDto dto,
			Model model) {

		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader==null) {
			return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", "not Autorized"));
		}
		
		/*if (!(partialHeader.getTeamId() == RcmTeamEnum.BILLING.getId()
				|| partialHeader.getTeamId() == RcmTeamEnum.INTERNAL_AUDIT.getId() || partialHeader.getRole().equals(RcmTeamEnum.REPORTING.getName()))) {
			
			return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", "not Autorized"));
		}*/
		Object sucess = null;
		dto.setCompanyuuid(partialHeader.getCompany().getUuid());
		sucess = claimServiceImpl.pullClaimFromSource(dto, null, partialHeader);
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", sucess));
	}

	@ApiOperation(value = "Api For Fetching Fresh Claims Logs (Billing Pendency Dashboard)", response = FreshClaimLogDto.class, responseContainer = "List")
	@GetMapping("/api/fetch-fresh-claims-logs/{uuid}")
	@PreAuthorize("hasAnyRole('TL','SUPER_ADMIN','REPORTING','ADMIN','ASSO')")
	public ResponseEntity<Object> fetchFreshClaimLogs(@PathVariable("uuid") String companyUuid
			,Model model) {
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader==null) {
			return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", "not Autorized"));
		}
		
		/*try {
			claimServiceImpl.updateDuplicateActives();
			}catch (Exception e) {
				e.printStackTrace();
			}*/
		return ResponseEntity
				.ok(new GenericResponse(HttpStatus.OK, "", claimServiceImpl.fetchFreshClaimLogs(partialHeader.getCompany().getUuid())));
	}

	// @GetMapping("/api/fetch-remote-lite-rej/{uuid}")
	@ApiOperation(value = "Api For RemoteLiteRejections (Billing Pendency Dashboard)", response = FreshClaimDetailsDto.class, responseContainer = "List")
	@GetMapping("/api/fetch-remote-lite-rej")
	public ResponseEntity<Object> fetchRemoteLiteRejections(Model model) {

		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader==null) {
			return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", "not Autorized"));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", claimServiceImpl.fetchRemoteLiteRejections(partialHeader)));
	}
	
	@ApiOperation(value = "Api For Fetching Billing/Rebilling Claims Details (Billing Pendency Dashboard)", response = FreshClaimDetailsDto.class, responseContainer = "List")
	@GetMapping("/api/fetch-billing-claims/{billType}")
	@PreAuthorize("hasAnyRole('TL','SUPER_ADMIN')")
	public ResponseEntity<Object> fetchBillingClaimDetails(@PathVariable("billType") int billType,Model model) {
		
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader==null) {
			return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", "not Autorized"));
		}
		/*try {
			claimServiceImpl.updateDuplicateActives();
			}catch (Exception e) {
				e.printStackTrace();
			}*/
		if (partialHeader.getTeamId()!=RcmTeamEnum.BILLING.getId()) {
			return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", "not Autorized"));
		}
		return ResponseEntity
				.ok(new GenericResponse(HttpStatus.OK, "", claimServiceImpl.fetchBillingClaimDetails(billType,partialHeader)));
	}

	@ApiOperation(value = "Api For Fetching Fresh Billing Claims Details (Billing Pendency Dashboard)", response = FreshClaimDataDto.class, responseContainer = "List")
	@GetMapping("/api/fetch-fresh-claims-det/{type}/{subType}")
	@PreAuthorize("hasAnyRole('TL','SUPER_ADMIN','ASSO','ADMIN')")
	public ResponseEntity<Object> fetchFreshClaimsDetails(@PathVariable("type") int type,
			@PathVariable("subType") String subType,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(required = false) Integer size,
			@RequestParam(defaultValue = "0") long knownTotalCount,
			@RequestParam(required = false) String sortBy,
			@RequestParam(required = false, defaultValue = "asc") String sortOrder,
			@RequestParam(required = false, defaultValue = "") String officeFilter,
			@RequestParam(required = false, defaultValue = "") String claimTypeFilter,
			@RequestParam(required = false, defaultValue = "") String ageBracketFilter,
			@RequestParam(required = false, defaultValue = "") String insuranceFilter,
			@RequestParam(required = false, defaultValue = "") String insuranceTypeFilter,
			@RequestParam(required = false, defaultValue = "") String currentStatusFilter,
			@RequestParam(required = false, defaultValue = "") String nextActionFilter,
			@RequestParam(required = false, defaultValue = "") String providerSpecialityFilter,
			@RequestParam(required = false, defaultValue = "") String lastTeamFilter,
			@RequestParam(required = false, defaultValue = "") String statusTypeFilter,
			Model model) {

		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader==null) {
			return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", "not Autorized"));
		}
		int effectiveSize = (size == null) ? defaultRecordsPerPage : Math.min(size, maxRecordsPerPage);
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
				claimServiceImpl.fetchFreshClaimDetails(partialHeader.getTeamId(), type, subType, partialHeader, page, effectiveSize, knownTotalCount,
						sortBy, sortOrder, officeFilter, claimTypeFilter, ageBracketFilter,
						insuranceFilter, insuranceTypeFilter, currentStatusFilter, nextActionFilter,
						providerSpecialityFilter, lastTeamFilter, statusTypeFilter)));
	}
	
	@ApiOperation(value = "Api For Fetching Unbilled Claims Details (Admin Ubnilled Claims)", response = FreshClaimDataDto.class, responseContainer = "List")
	@PostMapping("/api/unbilled-claims")
	@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
	public ResponseEntity<Object> fetchUnBilledClaim(@RequestBody AssignUnAssignResAsignClaimsDto dto,Model model) {
		
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader==null) {
			return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", "not Autorized"));
		}
		
		
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
				claimServiceImpl.fetchAllUnBilledClaimClient(dto,partialHeader)));
	}
	
	@ApiOperation(value = "Api For Fetching Fresh Billing Claims Details (Billing Pendency Dashboard)", response = FreshClaimDataDto.class, responseContainer = "List")
	@GetMapping("/api/fetch-fresh-claims-det-lead/{type}/{subType}")
	@PreAuthorize("hasAnyRole('TL','SUPER_ADMIN','ASSO','ADMIN')")
	public ResponseEntity<Object> fetchFreshClaimsDetailsLead(@PathVariable("type") int type,
			@PathVariable("subType") String subType,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(required = false) Integer size,
			@RequestParam(defaultValue = "0") long knownTotalCount,
			@RequestParam(required = false) String sortBy,
			@RequestParam(required = false, defaultValue = "asc") String sortOrder,
			@RequestParam(required = false, defaultValue = "") String officeFilter,
			@RequestParam(required = false, defaultValue = "") String claimTypeFilter,
			@RequestParam(required = false, defaultValue = "") String ageBracketFilter,
			@RequestParam(required = false, defaultValue = "") String insuranceFilter,
			@RequestParam(required = false, defaultValue = "") String insuranceTypeFilter,
			@RequestParam(required = false, defaultValue = "") String currentStatusFilter,
			@RequestParam(required = false, defaultValue = "") String nextActionFilter,
			@RequestParam(required = false, defaultValue = "") String providerSpecialityFilter,
			@RequestParam(required = false, defaultValue = "") String lastTeamFilter,
			@RequestParam(required = false, defaultValue = "") String statusTypeFilter,
			Model model) {

		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader==null) {
			return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", "not Autorized"));
		}
		int effectiveSize = (size == null) ? defaultRecordsPerPage : Math.min(size, maxRecordsPerPage);
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
				claimServiceImpl.fetchFreshClaimDetailsLead(partialHeader.getTeamId(), type, subType, partialHeader, page, effectiveSize, knownTotalCount,
						sortBy, sortOrder, officeFilter, claimTypeFilter, ageBracketFilter,
						insuranceFilter, insuranceTypeFilter, currentStatusFilter, nextActionFilter,
						providerSpecialityFilter, lastTeamFilter, statusTypeFilter)));
	}
	
	@ApiOperation(value = "Api For Saving Remark and Asssigning Claims (Other teams)", response = String.class, responseContainer = "List")
	@PostMapping("/api/assign-claim-with-remark")
	@PreAuthorize("hasAnyRole('TL','SUPER_ADMIN','ASSO')")
	public ResponseEntity<Object> fetchFreshClaimsDetailsLeadWhyThisName(@RequestBody ClaimAssignWithRemarkAndTeam dto,
			Model model) {
		
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader==null) {
			return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", "not Autorized"));
		}
		
		
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
				claimServiceImpl.assignClaimToOtherTeamWithRemark(partialHeader,dto)));
	}
	
	
	

	/*
	 * @ApiOperation(value =
	 * "Api For Fetching Fresh Billing Claims Details (Billing Pendency Dashboard)",
	 * response = FreshClaimDataDto.class, responseContainer = "List")
	 * 
	 * @GetMapping("/api/fetch-fresh-claims-det/other") public
	 * ResponseEntity<Object> fetchFreshClaimsDetailsOther() { return
	 * ResponseEntity.ok(new GenericResponse(HttpStatus.OK,
	 * "",claimServiceImpl.fetchClaimsByTeamNotFrom(RcmTeamEnum.BILLING.getId())));
	 * }
	 */

	@ApiOperation(value = "Api For Fetching Fresh Billing Claims Details (Billing Pendency Dashboard)", response = AssignFreshClaimLogsImplDto.class, responseContainer = "List")
	@PostMapping("/api/fetch-claims-log-assign")
	@PreAuthorize("hasAnyRole('TL','SUPER_ADMIN','ASSO','ADMIN')")
	public ResponseEntity<Object> fetchClaimsForAssignments(@RequestBody AssigmentClaimListDto dto,
			 Model model) {
		
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader ==null) return null;
		/*try {
			claimServiceImpl.updateDuplicateActives();
			}catch (Exception e) {
				e.printStackTrace();
			}*/
		return ResponseEntity
				.ok(new GenericResponse(HttpStatus.OK, "", claimServiceImpl.fetchClaimsForAssignments(dto,partialHeader)));
	}

	@ApiOperation(value = "Api For Fetching Billing TL Prodution Report", response = ProductionDto.class, responseContainer = "List")
	@PostMapping("/api/bill/claim-production")
	@PreAuthorize("hasAnyRole('TL','SUPER_ADMIN','REPORTING','ASSO','ADMIN')")
	public ResponseEntity<Object> claimsProduction(@RequestBody ClaimProductionLogDto dto, Model model) {
		
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader ==null) return null;
		
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
				claimServiceImpl.claimsProductionReportByTeam(dto,partialHeader)));
	}

	//@PreAuthorize("hasAnyRole('TL','ASSO','SUPER_ADMIN')")
	@ApiOperation(value = "Api For Fetching Individual Claim by uuid", response = FreshClaimDataImplDto.class)
	@GetMapping("/api/fetchindclaim/{uuid}")
	public ResponseEntity<Object> fetchIndividualClaim(@PathVariable("uuid") String claimUuid,
			 Model model) {

		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader ==null) return null;
		
		
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
				claimServiceImpl.fetchIndividualClaim(claimUuid, partialHeader,false)));
	}

    @ApiOperation(value = "Api For Fetching Service Code Validation Data by uuid", response = RcmClaimsServiceRuleValidationDto.class , responseContainer = "List")
	@GetMapping("/api/fetchservicecodeval/{uuid}")
	public ResponseEntity<Object> fetchServiceValidationFromGSheet(@PathVariable("uuid") String claimUuid,Model model) {
        //Rule Engine up and running is needed
    	//Only for Smile point
    	PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader ==null) return null;
		Object object= claimServiceImpl.readServiceValidationFromGSheet(null,claimUuid, partialHeader,false);
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
				object));
	}
	
   
	@ApiOperation(value = "Api For Fetching All Client Names and uuid", response = ClientCustomDto.class, responseContainer = "List")
	@GetMapping("/api/allclients")
	public ResponseEntity<Object> getAllClients(Model model) {
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader ==null) return null;
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", rcmCommonServiceImpl.findAllClients(partialHeader)));
	}

	@ApiOperation(value = "Api For Fetching All Client Names and uuid", response = ClientCustomDto.class, responseContainer = "List")
	@GetMapping("/api/issueClaims/{uuid}")
	@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','REPORTING','TL','ASSO')")
	public ResponseEntity<Object> getIssueClaims(@PathVariable("uuid") String companyId,Model model) {
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader ==null) return null;
		
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", claimServiceImpl.getIssueClaims(companyId)));
	}

	@ApiOperation(value = "Api For Fetching Rule Data From Rule Engine DB", response = RuleEngineClaimDto.class, responseContainer = "List")
	@PostMapping("/api/rules-data")
	public ResponseEntity<Object> getRuleEngineClaimReport(@RequestBody FindRulesDto  dto,Model model) {
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader ==null) return null;
		
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",claimServiceImpl.getRuleEngineClaimReport(dto.getOfficeId(),
				partialHeader, dto.getPatientId(), dto.getClaimId())));
	}

	@ApiOperation(value = "Api For Fetching IVF DATA From Rule Engine", response = CaplineIVFFormDto.class)
	@GetMapping("/api/ivfdata/{claimuuid}")
	public ResponseEntity<Object> getIvfDataFromRE(@PathVariable("claimuuid") String claimuuid,Model model) {
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader ==null) return null;
		
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
				claimServiceImpl.getIvfDataFromRE(partialHeader.getCompany().getUuid(), claimuuid)));
	}
	
	
	@ApiOperation(value = "Api For Fetching Claims (Other team )Remarks From Claims", response = ClaimRemarksDto.class ,responseContainer = "List")
	@GetMapping("/api/remarks-other/{claimuuid}")
	public ResponseEntity<Object> fetchClaimRemarksOtherTeam(@PathVariable("claimuuid") String claimuuid, Model model) {
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader ==null) return null;
		
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
				claimServiceImpl.fetchClaimRemarksOtherTeam(partialHeader.getCompany().getUuid(), claimuuid,partialHeader.getTeamId())));
	}
	
	@ApiOperation(value = "Api For Fetching Claims Cycle/Step", response = ClaimSteps.class ,responseContainer = "List")
	@GetMapping("/api/claimstep/{claimuuid}")
	public ResponseEntity<Object> fetchClaimSteps(@PathVariable("claimuuid") String claimuuid, Model model) {
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader ==null) return null;
		
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
				claimServiceImpl.fetchClaimSteps(claimuuid)));
	}
	
	@ApiOperation(value = "Api For Saving Claims Remarks", response = String.class)
	@PostMapping("/api/save-remarks")
	public ResponseEntity<Object> saveRemark(@RequestBody ClaimRemarkDto dto, Model model) {
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		 
		if (partialHeader ==null) return null;
		
			return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
				claimServiceImpl.saveClaimRemark(partialHeader,dto)));
	}
	
	
	@ApiOperation(value = "Api For Fetching Claims Rule Remarks", response = ClaimRuleRemarksDto.class ,responseContainer = "List")
	@GetMapping("/api/fetch-claim-rule-remarks/{claimuuid}")
	//@PreAuthorize("hasAnyRole('BILLING_TL','BILLING_ASSO')")
	public ResponseEntity<Object> fetchClaimRuleRemark(@PathVariable("claimuuid") String claimuuid, Model model) {
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader ==null) return null;
		
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
				claimServiceImpl.fetchClaimRuleRemark(partialHeader,claimuuid)));
	}
	
	
	@ApiOperation(value = "Api For Saving Claims Rule Remarks", response = String.class)
	@PostMapping("/api/save-claim-rule-remarks")
	@PreAuthorize("hasAnyRole('TL','ASSO','SUPER_ADMIN')")
	public ResponseEntity<Object> saveClaimRuleRemark(@RequestBody ClaimRuleRemarkDto dto, Model model) {
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader ==null) return null;
		
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
				claimServiceImpl.saveClaimRuleRemark(partialHeader,dto)));
	}
	
	@ApiOperation(value = "Api For Fetching Claims Automated and Manual Rules Data", response = ClaimRuleVaidationValueDto.class ,responseContainer = "List")
	@GetMapping("/api/fetch-claim-rule-val-data/{claimuuid}")
	//@PreAuthorize("hasAnyRole('BILLING_TL','BILLING_ASSO')")
	public ResponseEntity<Object> fetchClaimAutoRules(@PathVariable("claimuuid") String claimuuid, Model model) {
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader ==null) return null;
		
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
				claimServiceImpl.fetchClaimAllRulesData(partialHeader,claimuuid)));
	}
	
	@ApiOperation(value = "Api For Saving Claims Manual Rules", response = String.class)
	@PostMapping("/api/save-claim-rule-val-datas")
	@PreAuthorize("hasAnyRole('TL','ASSO','SUPER_ADMIN')")
	public ResponseEntity<Object> saveClaimManualRules(@RequestBody ClaimRuleValidationsDto dto, Model model) {
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader ==null) return null;
		
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
				claimServiceImpl.saveClaimManualRules(partialHeader,dto)));
	}
	
	@ApiOperation(value = "Api For Fetching Claims Submission detail", response = RcmClaimSubmissionDto.class)
	@GetMapping("/api/fetch-claim-sub-det/{claimuuid}")
	//@PreAuthorize("hasAnyRole('BILLING_TL','BILLING_ASSO')")
	public ResponseEntity<Object> fetchClaimSubDetails(@PathVariable("claimuuid") String claimuuid, Model model) {
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader ==null) return null;
		
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
				claimServiceImpl.fetchClaimSubmissionDetails(partialHeader,claimuuid)));
	}
	
	@ApiOperation(value = "Api For Saving Claims Submission  Details", response = String.class)
	@PostMapping("/api/save-claim-sub-det")
	@PreAuthorize("hasAnyRole('TL','ASSO','SUPER_ADMIN')")
	public ResponseEntity<Object> saveClaimSubDetails(@RequestBody ClaimSubmissionDto dto, Model model) {
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader ==null) return null;
		
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
				claimServiceImpl.saveClaimSubmissionDetails(partialHeader,dto)));
	}
	
	@ApiOperation(value = "Api For Fetching Claims Notes", response = KeyValueDto.class,responseContainer = "List")
	@GetMapping("/api/fetch-claim-notes/{claimuuid}")
	//@PreAuthorize("hasAnyRole('BILLING_TL','BILLING_ASSO')")
	public ResponseEntity<Object> fetchClaimNotes(@PathVariable("claimuuid") String claimuuid, Model model) {
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader ==null) return null;
		
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
				claimServiceImpl.fetchClaimNotes(partialHeader,claimuuid)));
	}
	
	@ApiOperation(value = "Api For Saving Claims Notes", response = String.class)
	@PostMapping("/api/save-claim-notes")
	@PreAuthorize("hasAnyRole('TL','ASSO','SUPER_ADMIN')")
	public ResponseEntity<Object> saveClaimNotes(@RequestBody ClaimNotesDto dto, Model model) {
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader ==null) return null;
		
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
				claimServiceImpl.saveClaimNotes(partialHeader,dto)));
	}
	
	@ApiOperation(value = "Api For Fetching Claims Remark (Only 1 for now)", response = String.class)
	@GetMapping("/api/fetch-claim-remark/{claimuuid}")
	//@PreAuthorize("hasAnyRole('BILLING_TL','BILLING_ASSO')")
	public ResponseEntity<Object> fetchClaimRemark(@PathVariable("claimuuid") String claimuuid, Model model) {
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader ==null) return null;
		
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
				claimServiceImpl.fetchClaimRemark(partialHeader,claimuuid)));
	}
	
	@ApiOperation(value = "Api For Saving Full Claim", response = String.class)
	@PostMapping("/api/save-full-claim")
	@PreAuthorize("hasAnyRole('TL','ASSO','SUPER_ADMIN')")
	public ResponseEntity<Object> saveFullClaim(@RequestBody ClaimEditDto dto, Model model) {
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader ==null) return null;
		
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
				claimServiceImpl.saveFullClaim(partialHeader,dto)));
	}
	
	/*
	@ApiOperation(value = "Api For Assigning Claim to other Team", response = String.class)
	@PostMapping("/api/assign-other-team")
	@PreAuthorize("hasAnyRole('TL','ASSO','SUPER_ADMIN')")
	public ResponseEntity<Object> assignToOtherOrTeamLead(@RequestBody ClaimAssignDto dto, Model model) {
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader ==null) return null;
		
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
				claimServiceImpl.assignToOtherTeam(partialHeader,dto)));
	}
	*/
	
	@ApiOperation(value = "Api For Assigning Claim to TL", response = KeyValueDto.class,responseContainer = "List")
	@PostMapping("/api/assign_to_tl")
	@PreAuthorize("hasAnyRole('TL','ASSO','SUPER_ADMIN')")
	public ResponseEntity<Object> assignClaimToTL(@RequestBody ClaimAssignDto dto, Model model) {
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader ==null) return null;
		
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
				claimServiceImpl.assignClaimToTL(partialHeader,dto,partialHeader.getTeamId())));
	}
	
	/*
	 * used used API
	 * 
	 * @ApiOperation(value =
	 * "Api For Assigning Un Assigned Claim to Assocaited Users", response =
	 * String.class)
	 * 
	 * @GetMapping("/api/assign-unsassigned_claims")
	 * 
	 * @PreAuthorize("hasAnyRole('TL','SUPER_ADMIN')") public ResponseEntity<Object>
	 * assignUnsAsignedClaims(Model model) { PartialHeader partialHeader =
	 * (PartialHeader) model.getAttribute("headerInfo"); if (partialHeader ==null)
	 * return null;
	 * 
	 * return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
	 * claimServiceImpl.assignedUnsAssignedClaims(partialHeader))); }
	 */
	

	@ApiOperation(value = "Api For Running Automated  rules on Claims", response = String.class)
	@GetMapping("/api/run-auto-rules/{claimuuid}/{reRun}")
	@PreAuthorize("hasAnyRole('TL','ASSO','SUPER_ADMIN')")
	public ResponseEntity<Object> runAutomatedRules(@PathVariable("claimuuid") String claimuuid,
			@PathVariable("reRun") boolean reRun, Model model) {
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader ==null) return null;
		
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
				claimServiceImpl.runAutomatedRules(null,partialHeader,claimuuid,reRun,false)));
	}
	
	@ApiOperation(value = "Api For Fetching pendency Report Data (All Billing Pendency Dashboard)", response = AllPendencyReportDto.class, responseContainer = "List")
	@GetMapping("/api/allpendency/{companyUuid}")
	@PreAuthorize("hasAnyRole('TL','SUPER_ADMIN','REPORTING','ASSO','ADMIN')")
	public ResponseEntity<Object> fetchAllPendencyData(@PathVariable("companyUuid") String companyUuid,Model model) {
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader==null) {
			return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", "not Autorized"));
		}
				
		return ResponseEntity
				.ok(new GenericResponse(HttpStatus.OK, "", claimServiceImpl.getAllPendencyReport(companyUuid,partialHeader.getTeamId(),partialHeader)));
	}
	

	/*private Object[] checkForSimplePointUser() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		((UserDetails) principal).getUsername();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
		JwtUser jwtUser = (JwtUser) userDetails;
		// only SmilePoint can do this
		if (jwtUser.isSmilePoint()) {
			return new Object[] { jwtUser, true };
		} else {
			return new Object[] { jwtUser, false };
		}
	}*/
//	@ApiOperation(value = "Api For Fetching All Client Names and uuid", response = ClientCustomDto.class, responseContainer = "List")
//	@GetMapping("/api/issueClaims/{uuid}/{pageNumber}")
//	@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','REPORTING','TL','ASSO')")
//	public ResponseEntity<Object> getIssueClaimss(@PathVariable("uuid") String companyId,
//			@PathVariable("pageNumber") int pageNumber, Model model) {
//		List<RcmIssuClaimPaginationDto> response = null;
//		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
//		if (partialHeader == null)
//			return null;
//		if(pageNumber==-1) {
//			return ResponseEntity.ok().body(new GenericResponse(HttpStatus.BAD_REQUEST,"", null));
//		}
//		try {
//			response = claimServiceImpl.getIssueClaimsByPagination(pageNumber, companyId);
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error(e.getMessage());
//			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
//		}
//		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
//	}
	
	@PostMapping("/api/updateivfid")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','REPORTING','TL','ASSO')")
    public ResponseEntity<Object> updateIvfId(@RequestBody RcmIVfDto dto, Model model) {
		ClaimSubDet response = null;
        PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
        if (partialHeader == null)
            return null;
        if (dto.getClaimUuid() == null || dto.getClaimUuid().trim().equals("") ) {
            return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.EMPTY_RESOURCE,null));
        }
        try {
        	response = claimServiceImpl.updateAutoIvIdAndTpId(dto,partialHeader);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
        }
        return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
    }
	
	@PostMapping("/api/updateivfid/delete")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','REPORTING','TL','ASSO')")
    public ResponseEntity<Object> removeIvIdAndTpId(@RequestBody RcmIVfDto dto, Model model) {
		ClaimSubDet response = null;
        PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
        if (partialHeader == null)
            return null;
        if (dto.getClaimUuid() == null || dto.getClaimUuid().trim().equals("") ) {
            return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.EMPTY_RESOURCE,null));
        }
        try {
        	response = claimServiceImpl.removeIvIdAndTpId(dto,partialHeader);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
        }
        return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
    }
	
	/*@PostMapping("/api/search-claims")
	@PreAuthorize("hasAnyRole('SUPER_ADMIN','REPORTING','TL','ASSO')")
	public ResponseEntity<Object> submittedClaims(@RequestBody ClaimSubmittedDto dto, Model model) {
		ClaimSubDet response = null;
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader == null)
			return null;
		if (dto.getOfficeUuid() == null || dto.getOfficeUuid().trim().equals("")) {
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.EMPTY_RESOURCE, null));
		}
		try {
			// response = claimServiceImpl.getSubmittedClaims(dto,partialHeader);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}*/
	
	@PostMapping("api/save-archive-claims")
	@PreAuthorize("hasAnyRole('TL','SUPER_ADMIN')")
	public ResponseEntity<?> archiveClaims(@RequestBody RcmArchiveClaimsDto dto, Model model) {
		String response = null;
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader == null)
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null));

		if (dto.getArchiveClaims() == null || dto.getArchiveClaims().stream().anyMatch(x -> x.getId() == null)
				||dto.getArchiveClaims().stream().anyMatch(x -> x.getArchiveStatus() == null)) {
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.EMPTY_RESOURCE, null));
		}
		try {
			response = claimServiceImpl.saveArchiveClaims(dto, partialHeader.getJwtUser());
			if (response == null) {
				return ResponseEntity
						.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.UPDATION_FAIL, null));

			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}
	
	@GetMapping("api/archiveClaims/{uuid}/{pageNumber}")
	@PreAuthorize("hasAnyRole('SUPER_ADMIN','TL','ASSO')")
	public ResponseEntity<Object> getArchiveClaims(@PathVariable("uuid") String companyId,
			@PathVariable("pageNumber") int pageNumber, Model model) {
		List<RcmIssuClaimPaginationDto> response = null;
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader == null)
			return null;
		if(pageNumber==-1) {
			return ResponseEntity.ok().body(new GenericResponse(HttpStatus.BAD_REQUEST,"", null));
		}
		try {
			response = claimServiceImpl.getArchiveClaimsByPagination(pageNumber, companyId);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}
	
	@GetMapping("api/alluserclients")
	@PreAuthorize("hasAnyRole('SUPER_ADMIN','TL','ASSO','ADMIN')")
	public ResponseEntity<Object> getAllclients(Model model) {
		List<CompanyIdAndNameDto> response = null;
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader == null)
			return null;
		try {
			response = claimServiceImpl.findAssociatedCompanyWithNameByUserUuid(partialHeader);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}
	
	@ApiOperation(value = "Api for updating status of Claim to Archive", response = String.class, responseContainer = "Map")
	@PostMapping("api/archiveunsub")
	@PreAuthorize("hasAnyRole('SUPER_ADMIN','TL','ASSO')")
	public ResponseEntity<Object> archiveActiveClaim(@RequestBody ClaimStatusUpdate dto,Model model) {
		String response = null;
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader == null)
			return null;
		try {
			response = claimServiceImpl.archiveActiveClaim(dto,partialHeader);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}
	
	@ApiOperation(value = "Api for updating status of Claim to UNArchive", response = String.class, responseContainer = "Map")
	@PostMapping("api/unarchivesub")
	@PreAuthorize("hasAnyRole('SUPER_ADMIN','TL','ASSO','ADMIN')")
	public ResponseEntity<Object> unArchiveClaim(@RequestBody ClaimStatusUpdate dto,Model model) {
		String response = null;
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader == null)
			return null;
		try {
			response = claimServiceImpl.UnArchiveActiveClaim(dto,partialHeader);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}
	
	@PostMapping("api/save-unarchive-claims")
	@PreAuthorize("hasAnyRole('TL','SUPER_ADMIN')")
	public ResponseEntity<?> unArchiveClaims(@RequestBody UnArchiveClaimDto dto, Model model) {
		UnArchivedResponseDto response = null;
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader == null)
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null));

		//if all claims has to be unarchive then no need to check below parameters
		if (!dto.isUnArchiveAll()) {
			if (dto.getId() == null || dto.getClaimId() == null || dto.getClaimId().isEmpty()) {
				return ResponseEntity
						.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.EMPTY_RESOURCE, null));
			}
		}
		try {
			response = claimServiceImpl.saveUnArchivedClaims(dto, partialHeader.getJwtUser(),partialHeader.getCompany());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}
	
	
	@GetMapping("api/searchparams")
	public ResponseEntity<Object> getSearchParams(Model model) {
		SearchParamDto response = null;
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader == null)
			return null;
		try {
			response = claimServiceImpl.getSearchParams();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}
	
	@PostMapping("api/unarchive-claims")
	@PreAuthorize("hasAnyRole('TL','SUPER_ADMIN')")
	public ResponseEntity<?> unArchiveClaimsByIds(@RequestBody RcmUnarchiveClaimsDto dto, Model model) {
		UnArchivedResponseDto response = null;
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader == null)
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null));

		if (dto.getUnarchiveClaims().stream().anyMatch(x -> x.getClaimId() == null || x.getClaimId().isEmpty())
				|| dto.getUnarchiveClaims().stream().anyMatch(x -> x.getId() == null)) {
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.EMPTY_RESOURCE, null));
		}

		try {
			response = claimServiceImpl.unArchiveAllClaimsByIds(dto, partialHeader.getJwtUser());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}
	
	@PostMapping("api/others-teams-tl-exit")
	@PreAuthorize("hasAnyRole('TL','SUPER_ADMIN','ASSO')")
	public ResponseEntity<?> findteamLeadExistForOtherTeams(@RequestBody FindTLExistDto dto, Model model) {
		RcmResponseMessageDto response = new RcmResponseMessageDto();
		int validateTeamId = 0;
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader == null) {
	        response.setResponseStatus(false);
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, response));}

		// if buttonType 1(claim send back to team who assigned the claim) then no need
		// to check TL exist or not
		if (dto.getAssignToTeamId() == null) {
			response.setResponseStatus(true);
			return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
		}

		validateTeamId = RcmTeamEnum.validateTeamIdWithRoleVisible(dto.getAssignToTeamId());

		if ((dto.getClaimUuid() == null || dto.getClaimUuid().isEmpty()) || validateTeamId == 0) {
			response.setResponseStatus(false);
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.EMPTY_RESOURCE, response));
		}

		try {
			response = claimServiceImpl.findTeamLeadExistForOtherTeams(dto, partialHeader.getJwtUser(),null);
		} catch (Exception e) {
		    response.setResponseStatus(false);
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", response));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}
	
	@PostMapping(value = "api/claim/pendency")
	@PreAuthorize("hasAnyRole('SUPER_ADMIN','TL','ASSO')")
	public ResponseEntity<?> getClaimsDataAccordingToCounts(@RequestBody ListOfClaimsCountsDto requestDto,
			Model model) {
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader == null)
			return ResponseEntity.badRequest()
					.body(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null));
		String response = null;
		try {
			response = claimServiceImpl.claimsDataAccordingToCountsType(requestDto);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}
	
	@GetMapping(value = "api/claim/user-section-permission")
	@PreAuthorize("hasAnyRole('SUPER_ADMIN','TL','ASSO','ADMIN')")
	public ResponseEntity<?> getSectionDetailsOfUserAndClient(Model model) {
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader == null)
			return ResponseEntity.badRequest()
					.body(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null));
		List<ClientSectionMappingDto> response = null;
		try {
			response = claimSection.sectionsPermissionOfUser(partialHeader.getJwtUser().getUuid(),partialHeader.getCompany().getUuid(),partialHeader.getTeamId());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}
	
//	@ApiOperation(value = "Api For Fetching Submitted Claims", response = FreshClaimDataDto.class, responseContainer = "List")
//	@GetMapping("/api/fetch-submitted-claims")
//	@PreAuthorize("hasAnyRole('TL','SUPER_ADMIN','ASSO')")
//	public ResponseEntity<Object> fetchSubmitClaimsDetails(Model model) {
//		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
//		if (partialHeader == null) {
//			return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", "not Autorized"));
//		}
//		List<FreshClaimDataViewDto> response = null;
//		try {
//			response = claimServiceImpl.fetchSubmittedClaimDetails(partialHeader);
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error(e.getMessage());
//			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
//		}
//		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
//	}

	
	@PostMapping(value = "api/save-section-info")
	@PreAuthorize("hasAnyRole('TL','ASSO','SUPER_ADMIN','ADMIN')")
	public ResponseEntity<?> saveSectionsData(@RequestBody CommonSectionsRequestBodyDto sectionRequestBody,
			Model model) {
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader == null)
			return ResponseEntity.badRequest()
					.body(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null));
		Object response = false;
		if (!StringUtils.isNoneBlank(sectionRequestBody.getClaimUuid())) {
			return ResponseEntity.badRequest()
					.body(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.EMPTY_RESOURCE, null));
		}
		try {
			response = claimServiceImpl.saveClaimSectionDataAfterSubmission(sectionRequestBody,partialHeader);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}
	
	@ApiOperation(value = "Api for Saving EOB -Pdf Link of Claim", response = String.class, responseContainer = "Map")
	@PostMapping("api/saveeoblink")
	@PreAuthorize("hasAnyRole('SUPER_ADMIN','TL','ASSO')")
	public ResponseEntity<Object> saveEobLink(@RequestBody EobLink dto,Model model) {
		EobLink response = null;
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader == null)
			return null;
		try {
			response = claimServiceImpl.saveEobLink(dto,partialHeader);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}
	
	@ApiOperation(value = "Api for validate secondary claim for recreation of claim", response = String.class, responseContainer = "List")
	@PostMapping(value = "/api/validate-secondary-claim-creation/{claimUuid}")
	@PreAuthorize("hasAnyRole('SUPER_ADMIN','TL','ASSO')")
	public ResponseEntity<?> validateSecondaryClaim(@PathVariable("claimUuid")String claimUuid, @RequestBody ClaimFromSheet dto, Model model) {
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader == null)
			return ResponseEntity.badRequest()
					.body(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null));
		List<String> response = null;
		try {
			response = claimServiceImpl.validSecondaryClaimDataFromRecreateSection(dto,
					partialHeader.getCompany().getUuid(),claimUuid);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}
	
	
	@PostMapping(value = "/api/reconciliation")
	@PreAuthorize("hasAnyRole('SUPER_ADMIN','TL','ASSO','REPORTING','ADMIN')")
	public ResponseEntity<GenericResponse> reconcillationData(
        @RequestBody ReconciliationDto dto,
        Model model) {
		   long startTime = System.currentTimeMillis();
		   // 1. Fetch contextual header info
			PartialHeader headerInfo = (PartialHeader) model.getAttribute("headerInfo");
			if (headerInfo == null) {
				logger.error("[RECON][CONTROLLER] Missing headerInfo in request. dto={}", dto);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new GenericResponse(
							HttpStatus.INTERNAL_SERVER_ERROR,
							"Request context missing. Please re-login.",
							null
					));
			}
		   // 2. Basic request validation
			if (dto == null || dto.getOfficeUuid() == null) {
				logger.warn("[RECON][CONTROLLER] Invalid request payload. dto={}", dto);
				return ResponseEntity.badRequest()
					.body(new GenericResponse(
							HttpStatus.BAD_REQUEST,
							"Office is required for reconciliation.",
							null
					));
			}
			// Date range validation (important per requirement)
			if (dto.getStartDate() == null || dto.getEndDate() == null) {
				logger.warn("[RECON][CONTROLLER] Date range missing. office={}, dto={}",
						dto.getOfficeUuid(), dto);
				return ResponseEntity.badRequest()
					.body(new GenericResponse(
							HttpStatus.BAD_REQUEST,
							"Start date and end date are required.",
							null
					));
			}else if (dto.getStartDate().after(dto.getEndDate())) {
				logger.warn("[RECON][CONTROLLER] Invalid date range. startDate={}, endDate={}",
						dto.getStartDate(), dto.getEndDate());
				return ResponseEntity.badRequest()
					.body(new GenericResponse(
						HttpStatus.BAD_REQUEST,
						"Start date cannot be after end date.",
						null
					));
			}

		    try {
				logger.info(
						"[RECON][START] office={}, startDate={}, endDate={}",
						dto.getOfficeUuid(),
						dto.getStartDate(),
						dto.getEndDate()
				);

        		List<ReconciliationResponseDto> response =
                claimServiceImpl.fetchReconciliationData(dto, headerInfo);

				logger.info(
						"[RECON][SUCCESS] office={}, records={}, timeTakenMs={}",
						dto.getOfficeUuid(),
						response != null ? response.size() : 0,
						(System.currentTimeMillis() - startTime)
				);

				return ResponseEntity.ok(
					new GenericResponse(
							HttpStatus.OK,
							"Reconciliation data fetched successfully",
							response
					)
				);

			} catch (IllegalArgumentException ex) {
				// Known validation / business errors
				logger.error(
						"[RECON][VALIDATION_ERROR] office={}, message={}",
						dto.getOfficeUuid(),
						ex.getMessage(),
						ex
				);
				 return ResponseEntity.badRequest()
					.body(new GenericResponse(
							HttpStatus.BAD_REQUEST,
							ex.getMessage(),
							null
					));

			} catch (Exception ex) {
			// Unexpected failures
				logger.error(
						"[RECON][ERROR] office={}, dto={}",
						dto.getOfficeUuid(),
						dto,
						ex
				);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new GenericResponse(
							HttpStatus.INTERNAL_SERVER_ERROR,
							"Unable to fetch reconciliation data. Please try again later.",
							null
					));
			}
		}	
	
	@PostMapping(value = "/api/claim-transfer-to-aging")
	@PreAuthorize("hasAnyRole('SUPER_ADMIN','TL','ASSO')")
	public ResponseEntity<?> claimTransferFromPostingToAging(@RequestBody ClaimTransferDto dto, Model model) {
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader == null)
			return ResponseEntity.badRequest()
					.body(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null));
		Object response = null;
		if (dto.getClaimUuid() == null || dto.getClaimUuid().isEmpty() || !StringUtils.isNoneBlank(dto.getRemarks())) {
			return ResponseEntity.badRequest()
					.body(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.EMPTY_RESOURCE, null));
		}
		
		if(partialHeader.getTeamId()!=RcmTeamEnum.PAYMENT_POSTING.getId()) {
			return ResponseEntity.badRequest()
					.body(new GenericResponse(HttpStatus.BAD_REQUEST, "Only Payment Posting user will be allow", null));
		}
		try {
			response = claimServiceImpl.transferClaimPostingToAging(dto, partialHeader);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}
	
	
	@PostMapping(value = "/api/tl-exist-by-claims")
	@PreAuthorize("hasAnyRole('SUPER_ADMIN','TL','ASSO')")
	public ResponseEntity<?> checkAnyTLOrAssoExist(@RequestBody ListOfClaimsDto dto, Model model) {
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader == null)
			return ResponseEntity.badRequest()
					.body(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null));
		RcmResponseMessageDto response = null;
		if (dto == null || dto.getClaimUuids().isEmpty() || dto.getTeamId() == null) {
			return ResponseEntity.badRequest()
					.body(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.EMPTY_RESOURCE, null));
		}
		try {
			response = claimServiceImpl.checkAnyTLOrAssoExist(dto, partialHeader);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}
	
	@ApiOperation(value = "Api For Multiple Claim Assigmentment/UnAssigmentment by TL (Same Team /Other Team/Same team Other member)", response = String.class)
	@PostMapping("/api/assign_unassign_reassign_claimbytl")
	@PreAuthorize("hasAnyRole('TL','ADMIN','SUPER_ADMIN')")
	public ResponseEntity<Object> assignResAssignClaimByTL(
			@RequestBody AssignUnAssignResAsignClaimsDto dto, Model model) {
		
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader==null) {
			return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", "not Autorized"));
		}
		try {
			return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
					claimServiceImpl.assignReAssignClaimByTL(dto,partialHeader)));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
	}
	
	@ApiOperation(value = "Api For Multiple Unbilled Claim Assgingment by ADMIN", response = String.class)
	@PostMapping("/api/assign_unbilled_claim_admin")
	@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
	public ResponseEntity<Object> assignUnBilledClaimsByAdmin(
			@RequestBody AssignUnAssignResAsignClaimsDto dto, Model model) {
		
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader==null) {
			return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", "not Autorized"));
		}
		try {
			return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
					claimServiceImpl.assignUnBilledClaimsByAdmin(dto,partialHeader)));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
	}
	
	@ApiOperation(value = "Api For List for user for claim Assgignments", response = UserClaimsAssignmentResponseDto.class, responseContainer = "List")
	@PostMapping("/api/usersbyTeam")
	@PreAuthorize("hasAnyRole('TL','ADMIN','SUPER_ADMIN')")
	public ResponseEntity<Object> getUsersByTeamForClaimAssignment(
			@RequestBody ClaimAssignmentsOfficeDto dto, Model model) {
		
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader==null) {
			return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", "not Autorized"));
		}
		try {
			return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
					claimServiceImpl.getUsersForClaimAssignment(dto,partialHeader)));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
	}
	
	@ApiOperation(value = "Api For Fetching DueBalanceResponsibleParty ", response = String.class)
	@GetMapping("/api/fetchdueBalresponpar/{uuid}")
	public ResponseEntity<Object> fetchDueBalance(@PathVariable("uuid") String claimUuid,Model model) {
        //Rule Engine up and running is needed
    	//Only for Smile point
    	PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader ==null) return null;
		Object object= claimServiceImpl.fetchDueBalanceRespart(claimUuid, partialHeader);
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
				object));
	}
	
	
	
	
}
