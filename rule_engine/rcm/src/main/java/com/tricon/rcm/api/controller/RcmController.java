package com.tricon.rcm.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tricon.rcm.db.entity.RcmUser;
import com.tricon.rcm.dto.ClaimSourceDto;
import com.tricon.rcm.dto.GenericResponse;
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

	/**
	 * Fetch Claims From Eagle Soft or Google Sheet
	 * 
	 * @param dto
	 * @return
	 */
	@ApiOperation(value = "Api For Fetching Claims For ES", response = String.class, responseContainer = "Map")
	@GetMapping("/api/fetch-claims")
	public ResponseEntity<Object> fetchClaims(@RequestBody ClaimSourceDto dto) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		((UserDetails) principal).getUsername();
		RcmUser user = null;
		Object sucess = null;
		sucess = claimServiceImpl.pullClaimFromSource(dto, user);
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", sucess));
	}

	@ApiOperation(value = "Api For Fetching Fresh Claims Details (Billing Pendency Dashboard)", response = FreshClaimDetailsDto.class, responseContainer = "List")
	@GetMapping("/api/fetch-fresh-claims")
	public ResponseEntity<Object> fetchFreshClaimDetails() {
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", claimServiceImpl.fetchFreshClaimDetails()));
	}

}

