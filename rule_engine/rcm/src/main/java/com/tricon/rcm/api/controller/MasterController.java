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

	@RequestMapping(value = "/getteams/{companyName}", method = RequestMethod.GET)
	public ResponseEntity<?> getTeams(@PathVariable("companyName")String companyName){

		GenericResponse teams = masterDataService.getTeams(companyName);
		return ResponseEntity.ok(teams);

	}

	@RequestMapping(value = "/getroles/{companyName}", method = RequestMethod.GET)
	public ResponseEntity<?> getRoles(@PathVariable("companyName")String companyName){

		GenericResponse roles = masterDataService.getRoles(companyName);

		return ResponseEntity.ok(roles);

	}
	
	@RequestMapping(value = "/rolesByTeamId/{teamId}", method = RequestMethod.GET)
	public ResponseEntity<?> rolesByTeamId(@PathVariable("teamId")int teamId){

		GenericResponse roles = masterDataService.getRolesByTeamId(teamId);

		return ResponseEntity.ok(roles);

	}
	
	@RequestMapping(value = "/defaultRolesByCname/{companyName}", method = RequestMethod.GET)
	public ResponseEntity<?> defaultRolesByCname(@PathVariable("companyName")String companyName){

		GenericResponse defaultRoles = masterDataService.defaultRolesByCompanyName(companyName);

		return ResponseEntity.ok(defaultRoles);

	}
}
