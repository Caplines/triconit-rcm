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

import com.tricon.ruleengine.dao.IVformTypeDao;
import com.tricon.ruleengine.dto.GenericResponse;
import com.tricon.ruleengine.dto.IVFDumpDto;
import com.tricon.ruleengine.service.IVFOldDataService;
import com.tricon.ruleengine.utils.Constants;

/**
 * @author Deepak.Dogra
 *
 */

@RestController
public class IVFOldDataController {
	
	/*
	 * in google sheet add Function 
	 * function gfk() {
        return SpreadsheetApp.getActiveSpreadsheet().getActiveSheet().getSheetId();
}


	*/

	static Class<?> clazz = IVFOldDataController.class;

	@Autowired
	IVFOldDataService iVFOldDataService;
	
	@Autowired
	IVformTypeDao iVformTypeDao;
	

	@CrossOrigin
	@PostMapping
	@PreAuthorize("hasAnyRole('ADMIN','DUMP')")
	@RequestMapping(value = "/dumpOldIVFData", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> dumpIOLData(@RequestBody IVFDumpDto dto) {

		//Map<String, List<TPValidationResponseDto>> map = null;
		String data = iVFOldDataService.dumpOldData(dto,iVformTypeDao.getIVFormTypeByName(Constants.IV_GENERAL_FORM_NAME));
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", data));

	}

}
