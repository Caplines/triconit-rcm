package com.tricon.rcm.api.controller;

import java.util.List;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tricon.rcm.db.entity.RcmTeam;
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

	@RequestMapping(value = "/getteams", method = RequestMethod.GET)
	public ResponseEntity<?> getTeams() {

		List<RcmTeam> teams = masterDataService.getAllTeams();

		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", teams));

	}

	@RequestMapping(value = "/getroles", method = RequestMethod.GET)
	public ResponseEntity<?> getRoles() {

		List<Entry<String, String>> roles = masterDataService.getRoles();

		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", roles));

	}
}
