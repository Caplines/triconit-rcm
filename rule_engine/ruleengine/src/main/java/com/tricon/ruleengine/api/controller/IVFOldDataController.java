package com.tricon.ruleengine.api.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tricon.ruleengine.dto.GenericResponse;
import com.tricon.ruleengine.dto.IVFDumpDto;
import com.tricon.ruleengine.service.IVFOldDataService;

/**
 * @author Deepak.Dogra
 *
 */

@RestController
public class IVFOldDataController {

	static Class<?> clazz = IVFOldDataController.class;

	@Autowired
	IVFOldDataService iVFOldDataService;

	@CrossOrigin
	@PostMapping
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@RequestMapping(value = "/dumpOldIVFData", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> dumpIOLData(@RequestBody IVFDumpDto dto) {

		//Map<String, List<TPValidationResponseDto>> map = null;
		String data = iVFOldDataService.dumpOldData(dto);
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", data));

	}

}
