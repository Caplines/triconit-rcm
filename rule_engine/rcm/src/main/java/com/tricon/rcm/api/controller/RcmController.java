package com.tricon.rcm.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tricon.rcm.db.entity.RcmUser;
import com.tricon.rcm.dto.AssigmentClaimListDto;
import com.tricon.rcm.dto.ClaimSourceDto;
import com.tricon.rcm.dto.customquery.FreshClaimLogDto;
import com.tricon.rcm.enums.RcmTeamEnum;
import com.tricon.rcm.security.JwtUser;
import com.tricon.rcm.dto.GenericResponse;
import com.tricon.rcm.dto.customquery.AssignFreshClaimLogsDto;
import com.tricon.rcm.dto.customquery.FreshClaimDataDto;
import com.tricon.rcm.dto.customquery.FreshClaimDetailsDto;
import com.tricon.rcm.service.impl.ClaimServiceImpl;
import com.tricon.rcm.service.impl.RuleEngineService;

import io.swagger.annotations.ApiOperation;

@RestController
public class RcmController {

	private final Logger logger = LoggerFactory.getLogger(RuleEngineService.class);

	@Autowired
	ClaimServiceImpl claimServiceImpl;

	@Autowired
	Environment ev;
	
	@Autowired
	@Qualifier("jwtUserDetailsService")
	private UserDetailsService userDetailsService;

	/**
	 * Fetch Claims From Eagle Soft or Google Sheet
	 * 
	 * @param dto
	 * @return
	 */
	@ApiOperation(value = "Api For Fetching Claims From  ES or GSheet", response = String.class, responseContainer = "Map")
	@PostMapping("/api/fetch-claims-from-source")
	public ResponseEntity<Object> fetchClaimsFromSource(@RequestBody ClaimSourceDto dto) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		((UserDetails) principal).getUsername();
		RcmUser user = null;
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
		JwtUser jwtUser = (JwtUser) userDetails;
		dto.setCompanyuuid(jwtUser.getCompany().getUuid());
		Object sucess = null;
		sucess = claimServiceImpl.pullClaimFromSource(dto, user);
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", sucess));
	}

	@ApiOperation(value = "Api For Fetching Fresh Claims Logs (Billing Pendency Dashboard)", response = FreshClaimLogDto.class, responseContainer = "List")
	@GetMapping("/api/fetch-fresh-claims-logs")
	public ResponseEntity<Object> fetchFreshClaimLogs() {
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",claimServiceImpl.fetchFreshClaimLogs()));
	}

	
	@ApiOperation(value = "Api For RemoteLiteRejections (Billing Pendency Dashboard)", response = FreshClaimDetailsDto.class, responseContainer = "List")
	@GetMapping("/api/fetch-remote-lite-rej/{uuid}")
	public ResponseEntity<Object> fetchRemoteLiteRejections(@PathVariable("uuid") String officeUUid) {
		
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",claimServiceImpl.fetchRemoteLiteRejections(officeUUid)));
	}
	
	@ApiOperation(value = "Api For Fetching Billing Claims Details (Billing Pendency Dashboard)", response = FreshClaimDetailsDto.class, responseContainer = "List")
	@GetMapping("/api/fetch-billing-claims")
	public ResponseEntity<Object> fetchBillingClaimDetails() {
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",claimServiceImpl.fetchBillingClaimDetails() ));
	}
	
	@ApiOperation(value = "Api For Fetching Re- Billing Claims Details (Billing Pendency Dashboard)", response = FreshClaimDetailsDto.class, responseContainer = "List")
	@GetMapping("/api/fetch-re-billing-claims")
	public ResponseEntity<Object> fetchReBillingClaimDetails() {
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",claimServiceImpl.fetchReBillingClaimDetails() ));
	}
	
	
	
	@ApiOperation(value = "Api For Fetching Fresh Billing Claims Details (Billing Pendency Dashboard)", response = FreshClaimDataDto.class, responseContainer = "List")
	@GetMapping("/api/fetch-fresh-claims-det")
	public ResponseEntity<Object> fetchFreshClaimsDetials() {
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",claimServiceImpl.fetchFreshClaimDetails(RcmTeamEnum.BILLING.getId()) ));
	}
	
	@ApiOperation(value = "Api For Fetching Fresh Billing Claims Details (Billing Pendency Dashboard)", response = FreshClaimDataDto.class, responseContainer = "List")
	@GetMapping("/api/fetch-fresh-claims-det/other")
	public ResponseEntity<Object> fetchFreshClaimsDetialsOther() {
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",claimServiceImpl.fetchClaimsByTeamNotFrom(RcmTeamEnum.BILLING.getId())));
	}
	
	@ApiOperation(value = "Api For Fetching Fresh Billing Claims Details (Billing Pendency Dashboard)", response = AssignFreshClaimLogsDto.class, responseContainer = "List")
	@PostMapping("/api/fetch-claims-log-assign")
	public ResponseEntity<Object> fetchClaimsForAssignments(@RequestBody AssigmentClaimListDto dto ) {
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",claimServiceImpl.fetchClaimsForAssignments(dto)));
	}
	
	
	
}

