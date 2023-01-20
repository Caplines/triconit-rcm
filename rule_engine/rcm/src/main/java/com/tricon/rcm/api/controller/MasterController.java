package com.tricon.rcm.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tricon.rcm.dto.GenericResponse;
import com.tricon.rcm.dto.RcmOfficeDto;
import com.tricon.rcm.service.impl.MasterServiceImpl;

@RestController
@CrossOrigin
@RequestMapping("master")
public class MasterController {

	@Autowired
	MasterServiceImpl masterDataService;

	@RequestMapping(value = "/getoffices", method = RequestMethod.GET)
	public ResponseEntity<?> getAlloffifceCap() {

		List<RcmOfficeDto> offices = masterDataService.getSmilePointOffice();

		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", offices));

	}

	@RequestMapping(value = "/getteams/{isSmilePoint}", method = RequestMethod.GET)
	public ResponseEntity<?> getTeams(@PathVariable("isSmilePoint")boolean isSmilePoint){

		GenericResponse teams = masterDataService.getTeams(isSmilePoint);
		return ResponseEntity.ok(teams);

	}

	@RequestMapping(value = "/getroles/{isSmilePoint}", method = RequestMethod.GET)
	public ResponseEntity<?> getRoles(@PathVariable("isSmilePoint")boolean isSmilePoint){

		GenericResponse roles = masterDataService.getRoles(isSmilePoint);

		return ResponseEntity.ok(roles);

	}
}
