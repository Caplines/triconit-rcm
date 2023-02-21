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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tricon.rcm.db.entity.RcmUser;
import com.tricon.rcm.dto.AssigmentClaimListDto;
import com.tricon.rcm.dto.CaplineIVFFormDto;
import com.tricon.rcm.dto.ClaimProductionLogDto;
import com.tricon.rcm.dto.ClaimRemarkDto;
import com.tricon.rcm.dto.ClaimSourceDto;
import com.tricon.rcm.dto.FindRulesDto;
import com.tricon.rcm.dto.FreshClaimDataImplDto;
import com.tricon.rcm.dto.customquery.FreshClaimLogDto;
import com.tricon.rcm.dto.customquery.ProductionDto;
import com.tricon.rcm.dto.customquery.RcmClaimDetailDto;
import com.tricon.rcm.dto.customquery.RuleEngineClaimDto;
import com.tricon.rcm.enums.RcmTeamEnum;
import com.tricon.rcm.security.JwtUser;
import com.tricon.rcm.dto.GenericResponse;
import com.tricon.rcm.dto.customquery.AssignFreshClaimLogsDto;
import com.tricon.rcm.dto.customquery.AssignFreshClaimLogsImplDto;
import com.tricon.rcm.dto.customquery.ClaimRemarksDto;
import com.tricon.rcm.dto.customquery.ClientCustomDto;
import com.tricon.rcm.dto.customquery.FreshClaimDataDto;
import com.tricon.rcm.dto.customquery.FreshClaimDetailsDto;
import com.tricon.rcm.service.impl.ClaimServiceImpl;
import com.tricon.rcm.service.impl.RcmCommonServiceImpl;
import com.tricon.rcm.service.impl.RuleEngineService;
import com.tricon.rcm.util.Constants;

import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin
public class RcmController {

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
	@PreAuthorize("hasRole('BILLING_TL')")
	public ResponseEntity<Object> fetchClaimsFromSource(@RequestBody ClaimSourceDto dto) {

		Object[] obj = checkForSimplePointUser();
		// only SmilePoint can do this
		if (!((boolean) obj[1])) {
			return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", "not Autorized"));
		}

		Object sucess = null;
		sucess = claimServiceImpl.pullClaimFromSource(dto, null, (JwtUser) obj[0]);
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", sucess));
	}

	@ApiOperation(value = "Api For Fetching Fresh Claims Logs (Billing Pendency Dashboard)", response = FreshClaimLogDto.class, responseContainer = "List")
	@GetMapping("/api/fetch-fresh-claims-logs/{uuid}")
	public ResponseEntity<Object> fetchFreshClaimLogs(@PathVariable("uuid") String companyUuid) {
		// only SmilePoint can do this
		Object[] obj = checkForSimplePointUser();
		if (!((boolean) obj[1])) {
			return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", "not Autorized"));
		}
		return ResponseEntity
				.ok(new GenericResponse(HttpStatus.OK, "", claimServiceImpl.fetchFreshClaimLogs(companyUuid)));
	}

	// @GetMapping("/api/fetch-remote-lite-rej/{uuid}")
	@ApiOperation(value = "Api For RemoteLiteRejections (Billing Pendency Dashboard)", response = FreshClaimDetailsDto.class, responseContainer = "List")
	@GetMapping("/api/fetch-remote-lite-rej")
	public ResponseEntity<Object> fetchRemoteLiteRejections() {

		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", claimServiceImpl.fetchRemoteLiteRejections()));
	}

	@ApiOperation(value = "Api For Fetching Billing/Rebilling Claims Details (Billing Pendency Dashboard)", response = FreshClaimDetailsDto.class, responseContainer = "List")
	@GetMapping("/api/fetch-billing-claims/{billType}")
	public ResponseEntity<Object> fetchBillingClaimDetails(@PathVariable("billType") int billType) {
		return ResponseEntity
				.ok(new GenericResponse(HttpStatus.OK, "", claimServiceImpl.fetchBillingClaimDetails(billType)));
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
	@PreAuthorize("hasAnyRole('BILLING_TL','BILLING_ASSO')")
	public ResponseEntity<Object> fetchClaimsForAssignments(@RequestBody AssigmentClaimListDto dto) {
		return ResponseEntity
				.ok(new GenericResponse(HttpStatus.OK, "", claimServiceImpl.fetchClaimsForAssignments(dto)));
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
				claimServiceImpl.fetchIndividualClaim(claimUuid, jwtUser.getCompany())));
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
	
	
	@ApiOperation(value = "Api For Fetching Claims Remarks", response = ClaimRemarksDto.class ,responseContainer = "List")
	@GetMapping("/api/remarks/{claimuuid}")
	public ResponseEntity<Object> fetchClaimRemarks(@PathVariable("claimuuid") String claimuuid) {
		Object[] obj = checkForSimplePointUser();
		JwtUser jwtUser = (JwtUser) obj[0];

		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
				claimServiceImpl.fetchClaimRemarks(jwtUser.getCompany().getUuid(), claimuuid,jwtUser.getTeamId())));
	}
	
	@ApiOperation(value = "Api For Saving Claims Remarks", response = ClaimRemarksDto.class ,responseContainer = "List")
	@PostMapping("/api/save-remarks")
	public ResponseEntity<Object> saveRemark(@RequestBody ClaimRemarkDto dto) {
		Object[] obj = checkForSimplePointUser();
		JwtUser jwtUser = (JwtUser) obj[0];

		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
				claimServiceImpl.saveClaimRemark(jwtUser,dto)));
	}
	

	private Object[] checkForSimplePointUser() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		((UserDetails) principal).getUsername();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
		JwtUser jwtUser = (JwtUser) userDetails;
		// only SmilePoint can do this
		if (jwtUser.getCompany().getName().equals(Constants.COMPANY_NAME)) {
			return new Object[] { jwtUser, true };
		} else {
			return new Object[] { jwtUser, false };
		}
	}
}
