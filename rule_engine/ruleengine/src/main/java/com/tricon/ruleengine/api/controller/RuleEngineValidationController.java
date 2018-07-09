package com.tricon.ruleengine.api.controller;

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
import com.tricon.ruleengine.dto.TreatmentPlanValidationDto;

/**
 * @author Deepak.Dogra
 *
 */

@RestController
public class RuleEngineValidationController {

	@CrossOrigin
	@PostMapping
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@RequestMapping(value = "/validateTreatmentPlan", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> validateTreatementPlan(@RequestBody TreatmentPlanValidationDto dto) {

		System.out.println("calledddddddddd-----------");
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", ""));
	}


}
