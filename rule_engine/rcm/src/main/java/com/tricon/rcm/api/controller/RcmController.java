package com.tricon.rcm.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.tricon.rcm.dto.AssigmentClaimListDto;
import com.tricon.rcm.dto.CaplineIVFFormDto;
import com.tricon.rcm.dto.ClaimAssignDto;
import com.tricon.rcm.dto.ClaimEditDto;
import com.tricon.rcm.dto.KeyValueDto;
import com.tricon.rcm.dto.PartialHeader;
import com.tricon.rcm.dto.RcmClaimsServiceRuleValidationDto;
import com.tricon.rcm.dto.ClaimNotesDto;
import com.tricon.rcm.dto.ClaimProductionLogDto;
import com.tricon.rcm.dto.ClaimRemarkDto;
import com.tricon.rcm.dto.ClaimRuleRemarkDto;
import com.tricon.rcm.dto.ClaimRuleVaidationValueDto;
import com.tricon.rcm.dto.ClaimRuleValidationsDto;
import com.tricon.rcm.dto.ClaimSourceDto;
import com.tricon.rcm.dto.ClaimSubmissionDto;
import com.tricon.rcm.dto.FindRulesDto;
import com.tricon.rcm.dto.FreshClaimDataImplDto;
import com.tricon.rcm.dto.GenericResponse;
import com.tricon.rcm.dto.customquery.AssignFreshClaimLogsImplDto;
import com.tricon.rcm.dto.customquery.ClaimRemarksDto;
import com.tricon.rcm.dto.customquery.ClaimRuleRemarksDto;
import com.tricon.rcm.dto.customquery.ClientCustomDto;
import com.tricon.rcm.dto.customquery.FreshClaimDataDto;
import com.tricon.rcm.dto.customquery.FreshClaimDetailsDto;
import com.tricon.rcm.dto.customquery.FreshClaimLogDto;
import com.tricon.rcm.dto.customquery.ProductionDto;
import com.tricon.rcm.dto.customquery.RcmClaimSubmissionDto;
import com.tricon.rcm.dto.customquery.RuleEngineClaimDto;
import com.tricon.rcm.enums.RcmTeamEnum;
import com.tricon.rcm.security.JwtUser;
import com.tricon.rcm.service.impl.ClaimServiceImpl;
import com.tricon.rcm.service.impl.RcmCommonServiceImpl;
import com.tricon.rcm.service.impl.RuleEngineService;
import com.tricon.rcm.util.Constants;

import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin
public class RcmController extends BaseHeaderController{

	private final Logger logger = LoggerFactory.getLogger(RuleEngineService.class);

	@Autowired
	ClaimServiceImpl claimServiceImpl;

	@Autowired
	Environment ev;

	@Autowired
	@Qualifier("jwtUserDetailsService")
	private UserDetailsService userDetailsService;

	@Autowired
	RcmCommonServiceImpl rcmCommonServiceImpl;

	
	
	
	/**
	 * Fetch Claims From Eagle Soft or Google Sheet
	 * 
	 * @param dto
	 * @return
	 */
	
	@ApiOperation(value = "Api For Fetching Claims From  ES or GSheet", response = String.class, responseContainer = "Map")
	@PostMapping("/api/fetch-claims-from-source")
	@PreAuthorize("hasAnyRole('TL','SUPER_ADMIN')")
	public ResponseEntity<Object> fetchClaimsFromSource(@RequestBody ClaimSourceDto dto,
			Model model) {

		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader==null) {
			return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", "not Autorized"));
		}
		
		if (partialHeader.getTeamId()!=RcmTeamEnum.BILLING.getId()) {
			return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", "not Autorized"));
		}
		Object sucess = null;
		dto.setCompanyuuid(partialHeader.getCompany().getUuid());
		sucess = claimServiceImpl.pullClaimFromSource(dto, null, partialHeader.getJwtUser());
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", sucess));
	}

	@ApiOperation(value = "Api For Fetching Fresh Claims Logs (Billing Pendency Dashboard)", response = FreshClaimLogDto.class, responseContainer = "List")
	@GetMapping("/api/fetch-fresh-claims-logs/{uuid}")
	@PreAuthorize("hasAnyRole('TL','SUPER_ADMIN')")
	public ResponseEntity<Object> fetchFreshClaimLogs(@PathVariable("uuid") String companyUuid
			,Model model) {
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader==null) {
			return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", "not Autorized"));
		}
		
		
		return ResponseEntity
				.ok(new GenericResponse(HttpStatus.OK, "", claimServiceImpl.fetchFreshClaimLogs(partialHeader.getCompany().getUuid())));
	}

	// @GetMapping("/api/fetch-remote-lite-rej/{uuid}")
	@ApiOperation(value = "Api For RemoteLiteRejections (Billing Pendency Dashboard)", response = FreshClaimDetailsDto.class, responseContainer = "List")
	@GetMapping("/api/fetch-remote-lite-rej")
	public ResponseEntity<Object> fetchRemoteLiteRejections() {

		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", claimServiceImpl.fetchRemoteLiteRejections()));
	}
	
	@ApiOperation(value = "Api For Fetching Billing/Rebilling Claims Details (Billing Pendency Dashboard)", response = FreshClaimDetailsDto.class, responseContainer = "List")
	@GetMapping("/api/fetch-billing-claims/{billType}")
	@PreAuthorize("hasAnyRole('TL','SUPER_ADMIN')")
	public ResponseEntity<Object> fetchBillingClaimDetails(@PathVariable("billType") int billType,Model model) {
		
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader==null) {
			return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", "not Autorized"));
		}
		
		if (partialHeader.getTeamId()!=RcmTeamEnum.BILLING.getId()) {
			return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", "not Autorized"));
		}
		return ResponseEntity
				.ok(new GenericResponse(HttpStatus.OK, "", claimServiceImpl.fetchBillingClaimDetails(billType,partialHeader)));
	}

	@ApiOperation(value = "Api For Fetching Fresh Billing Claims Details (Billing Pendency Dashboard)", response = FreshClaimDataDto.class, responseContainer = "List")
	@GetMapping("/api/fetch-fresh-claims-det/{type}/{subType}")
	public ResponseEntity<Object> fetchFreshClaimsDetails(@PathVariable("type") int type,
			@PathVariable("subType") String subType) {
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
				claimServiceImpl.fetchFreshClaimDetails(RcmTeamEnum.BILLING.getId(), type, subType)));
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
	@PreAuthorize("hasAnyRole('TL','ASSO','SUPER_ADMIN')")
	public ResponseEntity<Object> fetchClaimsForAssignments(@RequestBody AssigmentClaimListDto dto,
			 Model model) {
		
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader ==null) return null;
		
		return ResponseEntity
				.ok(new GenericResponse(HttpStatus.OK, "", claimServiceImpl.fetchClaimsForAssignments(dto,partialHeader)));
	}

	@ApiOperation(value = "Api For Fetching Billing TL Prodution Report", response = ProductionDto.class, responseContainer = "List")
	@PostMapping("/api/bill/claim-production")
	public ResponseEntity<Object> claimsProduction(@RequestBody ClaimProductionLogDto dto) {
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
				claimServiceImpl.claimsProductionReportByTeam(RcmTeamEnum.BILLING.getId(), dto)));
	}

	@ApiOperation(value = "Api For Fetching Individual Claim by uuid", response = FreshClaimDataImplDto.class)
	@GetMapping("/api/fetchindclaim/{uuid}")
	public ResponseEntity<Object> fetchIndividualClaim(@PathVariable("uuid") String claimUuid) {

		Object[] obj = checkForSimplePointUser();
		JwtUser jwtUser = ((JwtUser) obj[0]);
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
				claimServiceImpl.fetchIndividualClaim(claimUuid, jwtUser)));
	}

    @ApiOperation(value = "Api For Fetching Service Code Validation Data by uuid", response = RcmClaimsServiceRuleValidationDto.class , responseContainer = "List")
	@GetMapping("/api/fetchservicecodeval/{uuid}")
	public ResponseEntity<Object> fetchServiceValidationFromGSheet(@PathVariable("uuid") String claimUuid) {

		Object[] obj = checkForSimplePointUser();
		JwtUser jwtUser = ((JwtUser) obj[0]);
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
				claimServiceImpl.readServiceValidationFromGSheet(claimUuid, jwtUser)));
	}
	
   
	@ApiOperation(value = "Api For Fetching All Client Names and uuid", response = ClientCustomDto.class, responseContainer = "List")
	@GetMapping("/api/allclients")
	public ResponseEntity<Object> getAllClients() {
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", rcmCommonServiceImpl.findAllClients()));
	}

	@ApiOperation(value = "Api For Fetching All Client Names and uuid", response = ClientCustomDto.class, responseContainer = "List")
	@GetMapping("/api/issueClaims/{uuid}")
	@PreAuthorize("hasRole('BILLING_TL')")
	public ResponseEntity<Object> getIssueClaims(@PathVariable("uuid") String companyId) {
		Object[] obj = checkForSimplePointUser();
		if (!((boolean) obj[1])) {
			return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", "not Autorized"));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", claimServiceImpl.getIssueClaims(companyId)));
	}

	@ApiOperation(value = "Api For Fetching Rule Data From Rule Engine DB", response = RuleEngineClaimDto.class, responseContainer = "List")
	@PostMapping("/api/rules-data")
	public ResponseEntity<Object> getRuleEngineClaimReport(@RequestBody FindRulesDto  dto) {
		Object[] obj=checkForSimplePointUser();
		JwtUser jwtUser=(JwtUser) obj[0];
		
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",claimServiceImpl.getRuleEngineClaimReport(dto.getOfficeId(),
				jwtUser.getCompany().getUuid(), dto.getPatientId(), dto.getClaimId())));
	}

	@ApiOperation(value = "Api For Fetching IVF DATA From Rule Engine", response = CaplineIVFFormDto.class)
	@GetMapping("/api/ivfdata/{claimuuid}")
	public ResponseEntity<Object> getIvfDataFromRE(@PathVariable("claimuuid") String claimuuid) {
		Object[] obj = checkForSimplePointUser();
		JwtUser jwtUser = (JwtUser) obj[0];

		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
				claimServiceImpl.getIvfDataFromRE(jwtUser.getCompany().getUuid(), claimuuid)));
	}
	
	
	@ApiOperation(value = "Api For Fetching Claims (Other team )Remarks From Claims", response = ClaimRemarksDto.class ,responseContainer = "List")
	@GetMapping("/api/remarks-other/{claimuuid}")
	public ResponseEntity<Object> fetchClaimRemarksOtherTeam(@PathVariable("claimuuid") String claimuuid) {
		Object[] obj = checkForSimplePointUser();
		JwtUser jwtUser = (JwtUser) obj[0];

		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
				claimServiceImpl.fetchClaimRemarksOtherTeam(jwtUser.getCompany().getUuid(), claimuuid,jwtUser.getTeamId())));
	}
	
	@ApiOperation(value = "Api For Saving Claims Remarks", response = String.class)
	@PostMapping("/api/save-remarks")
	public ResponseEntity<Object> saveRemark(@RequestBody ClaimRemarkDto dto) {
		Object[] obj = checkForSimplePointUser();
		JwtUser jwtUser = (JwtUser) obj[0];

		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
				claimServiceImpl.saveClaimRemark(jwtUser,dto)));
	}
	
	
	@ApiOperation(value = "Api For Fetching Claims Rule Remarks", response = ClaimRuleRemarksDto.class ,responseContainer = "List")
	@GetMapping("/api/fetch-claim-rule-remarks/{claimuuid}")
	//@PreAuthorize("hasAnyRole('BILLING_TL','BILLING_ASSO')")
	public ResponseEntity<Object> fetchClaimRuleRemark(@PathVariable("claimuuid") String claimuuid) {
		Object[] obj = checkForSimplePointUser();
		JwtUser jwtUser = (JwtUser) obj[0];

		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
				claimServiceImpl.fetchClaimRuleRemark(jwtUser,claimuuid)));
	}
	
	
	@ApiOperation(value = "Api For Saving Claims Rule Remarks", response = String.class)
	@PostMapping("/api/save-claim-rule-remarks")
	@PreAuthorize("hasAnyRole('BILLING_TL','BILLING_ASSO')")
	public ResponseEntity<Object> saveClaimRuleRemark(@RequestBody ClaimRuleRemarkDto dto) {
		Object[] obj = checkForSimplePointUser();
		JwtUser jwtUser = (JwtUser) obj[0];

		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
				claimServiceImpl.saveClaimRuleRemark(jwtUser,dto)));
	}
	
	@ApiOperation(value = "Api For Fetching Claims Automated and Manual Rules Data", response = ClaimRuleVaidationValueDto.class ,responseContainer = "List")
	@GetMapping("/api/fetch-claim-rule-val-data/{claimuuid}")
	//@PreAuthorize("hasAnyRole('BILLING_TL','BILLING_ASSO')")
	public ResponseEntity<Object> fetchClaimAutoRules(@PathVariable("claimuuid") String claimuuid) {
		Object[] obj = checkForSimplePointUser();
		JwtUser jwtUser = (JwtUser) obj[0];

		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
				claimServiceImpl.fetchClaimAllRulesData(jwtUser,claimuuid)));
	}
	
	@ApiOperation(value = "Api For Saving Claims Manual Rules", response = String.class)
	@PostMapping("/api/save-claim-rule-val-datas")
	@PreAuthorize("hasAnyRole('BILLING_TL','BILLING_ASSO')")
	public ResponseEntity<Object> saveClaimManualRules(@RequestBody ClaimRuleValidationsDto dto) {
		Object[] obj = checkForSimplePointUser();
		JwtUser jwtUser = (JwtUser) obj[0];

		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
				claimServiceImpl.saveClaimManualRules(jwtUser,dto)));
	}
	
	@ApiOperation(value = "Api For Fetching Claims Submission detail", response = RcmClaimSubmissionDto.class)
	@GetMapping("/api/fetch-claim-sub-det/{claimuuid}")
	//@PreAuthorize("hasAnyRole('BILLING_TL','BILLING_ASSO')")
	public ResponseEntity<Object> fetchClaimSubDetails(@PathVariable("claimuuid") String claimuuid) {
		Object[] obj = checkForSimplePointUser();
		JwtUser jwtUser = (JwtUser) obj[0];

		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
				claimServiceImpl.fetchClaimSubmissionDetails(jwtUser,claimuuid)));
	}
	
	@ApiOperation(value = "Api For Saving Claims Submission  Details", response = String.class)
	@PostMapping("/api/save-claim-sub-det")
	@PreAuthorize("hasAnyRole('BILLING_TL','BILLING_ASSO')")
	public ResponseEntity<Object> saveClaimSubDetails(@RequestBody ClaimSubmissionDto dto) {
		Object[] obj = checkForSimplePointUser();
		JwtUser jwtUser = (JwtUser) obj[0];

		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
				claimServiceImpl.saveClaimSubmissionDetails(jwtUser,dto)));
	}
	
	@ApiOperation(value = "Api For Fetching Claims Notes", response = KeyValueDto.class,responseContainer = "List")
	@GetMapping("/api/fetch-claim-notes/{claimuuid}")
	//@PreAuthorize("hasAnyRole('BILLING_TL','BILLING_ASSO')")
	public ResponseEntity<Object> fetchClaimNotes(@PathVariable("claimuuid") String claimuuid) {
		Object[] obj = checkForSimplePointUser();
		JwtUser jwtUser = (JwtUser) obj[0];

		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
				claimServiceImpl.fetchClaimNotes(jwtUser,claimuuid)));
	}
	
	@ApiOperation(value = "Api For Saving Claims Notes", response = String.class)
	@PostMapping("/api/save-claim-notes")
	@PreAuthorize("hasAnyRole('BILLING_TL','BILLING_ASSO')")
	public ResponseEntity<Object> saveClaimNotes(@RequestBody ClaimNotesDto dto) {
		Object[] obj = checkForSimplePointUser();
		JwtUser jwtUser = (JwtUser) obj[0];

		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
				claimServiceImpl.saveClaimNotes(jwtUser,dto)));
	}
	
	@ApiOperation(value = "Api For Fetching Claims Remark (Only 1 for now)", response = String.class)
	@GetMapping("/api/fetch-claim-remark/{claimuuid}")
	//@PreAuthorize("hasAnyRole('BILLING_TL','BILLING_ASSO')")
	public ResponseEntity<Object> fetchClaimRemark(@PathVariable("claimuuid") String claimuuid) {
		Object[] obj = checkForSimplePointUser();
		JwtUser jwtUser = (JwtUser) obj[0];

		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
				claimServiceImpl.fetchClaimRemark(jwtUser,claimuuid)));
	}
	
	@ApiOperation(value = "Api For Saving Full Claim", response = String.class)
	@PostMapping("/api/save-full-claim")
	@PreAuthorize("hasAnyRole('BILLING_TL','BILLING_ASSO')")
	public ResponseEntity<Object> saveFullClaim(@RequestBody ClaimEditDto dto) {
		Object[] obj = checkForSimplePointUser();
		JwtUser jwtUser = (JwtUser) obj[0];

		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
				claimServiceImpl.saveFullClaim(jwtUser,dto)));
	}
	
	
	@ApiOperation(value = "Api For Assigning Claim to other user/Team for Preview", response = String.class)
	@PostMapping("/api/assign-other-team-or-lead")
	@PreAuthorize("hasAnyRole('BILLING_TL','BILLING_ASSO')")
	public ResponseEntity<Object> assignToOtherOrTeamLead(@RequestBody ClaimAssignDto dto) {
		Object[] obj = checkForSimplePointUser();
		JwtUser jwtUser = (JwtUser) obj[0];

		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
				claimServiceImpl.assignToOtherOrTeamLead(jwtUser,dto)));
	}
	
	
	
	@ApiOperation(value = "Api For Assigning Un Assigned Claim to Users", response = String.class)
	@GetMapping("/api/assign-unsassigned_claims")
	@PreAuthorize("hasAnyRole('BILLING_TL','SUPER_ADMIN')")
	public ResponseEntity<Object> assignUnsAsignedClaims() {
		Object[] obj = checkForSimplePointUser();
		JwtUser jwtUser = (JwtUser) obj[0];

		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
				claimServiceImpl.assignedUnsAssignedClaims(jwtUser)));
	}
	

	@ApiOperation(value = "Api For Running Automated  rules on Claims", response = String.class)
	@GetMapping("/api/run-auto-rules/{claimuuid}/{reRun}")
	@PreAuthorize("hasAnyRole('BILLING_TL','BILLING_ASSO')")
	public ResponseEntity<Object> runAutomatedRules(@PathVariable("claimuuid") String claimuuid,
			@PathVariable("reRun") boolean reRun) {
		Object[] obj = checkForSimplePointUser();
		JwtUser jwtUser = (JwtUser) obj[0];

		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
				claimServiceImpl.runAutomatedRules(jwtUser,claimuuid,reRun)));
	}
	

	private Object[] checkForSimplePointUser() {

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
	}
}
