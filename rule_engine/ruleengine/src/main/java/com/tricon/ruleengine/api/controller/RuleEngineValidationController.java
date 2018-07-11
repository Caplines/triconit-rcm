package com.tricon.ruleengine.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tricon.ruleengine.dto.GenericResponse;
import com.tricon.ruleengine.dto.TPValidationResponseDto;
import com.tricon.ruleengine.dto.TreatmentPlanValidationDto;
import com.tricon.ruleengine.service.TreatmentPlanService;

/**
 * @author Deepak.Dogra
 *
 */

@RestController
public class RuleEngineValidationController {

	
	@Autowired
	TreatmentPlanService tPService;
	
	@CrossOrigin
	@PostMapping
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@RequestMapping(value = "/validateTreatmentPlan", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> validateTreatementPlan(@RequestBody TreatmentPlanValidationDto dto) {

		//dto.setTreatmentPlanId("22095");
		List<TPValidationResponseDto> tpDdtoL=	tPService.validateTreatmentPlan(dto);
		System.out.println("calledddddddddd-----------");
		
		
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", tpDdtoL));
	}

	@CrossOrigin
	@PostMapping
	@RequestMapping(value = "/validateTreatmentPlan2", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> validateTreatementPlan2() {

		TreatmentPlanValidationDto dto=new TreatmentPlanValidationDto();
		dto.setTreatmentPlanId("22095");
		List<TPValidationResponseDto> tpDdtoL=	tPService.validateTreatmentPlan(dto);
		System.out.println("calledddddddddd-----------");
		
		
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", tpDdtoL));
	}

}
