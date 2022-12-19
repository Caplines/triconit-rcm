package com.tricon.rcm.security.api.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tricon.rcm.dto.ClaimSourceDto;
import com.tricon.rcm.dto.GenericResponse;
import com.tricon.rcm.enums.ClaimSourceEnum;
import com.tricon.rcm.service.impl.RuleEngineService;


import io.swagger.annotations.ApiOperation;

@RestController
public class RcmController {
	
	private final Logger logger = LoggerFactory.getLogger(RuleEngineService.class);
	
	@Autowired
	RuleEngineService rService;
	
	@Autowired
	Environment ev;
	
	@ApiOperation(value = "Api TEST)", response = String.class)
	@PostMapping("ignore1") 
	public String  Test() {

		return "called";
	}

	@ApiOperation(value = "Api TEST)", response = String.class)
	@GetMapping("ignore3") 
	public String  Test1() {

		return "called";
	}

	@ApiOperation(value = "Api For Fetching Claims For ES", response = String.class)
	@GetMapping("/api/fetch-claims") 
	public ResponseEntity<Object>  fetchClaims(@RequestBody ClaimSourceDto dto) {

		
		if (dto.getSource().equals(ClaimSourceEnum.EAGLESOFT.toString())) {
			
			//go to Rule Engine.
			rService.pullClaimFromRE(dto);
			
		}else {
			
			//go to Google Sheet.
			
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", ""));
	}
	
	
	

}
