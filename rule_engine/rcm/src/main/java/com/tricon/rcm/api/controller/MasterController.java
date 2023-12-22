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

import com.tricon.rcm.db.entity.RcmCompany;
import com.tricon.rcm.dto.GenericResponse;
import com.tricon.rcm.dto.RcmOfficeDto;
import com.tricon.rcm.dto.RcmRoleDto;
import com.tricon.rcm.dto.RcmTeamDto;
import com.tricon.rcm.dto.SectionDto;
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
	public ResponseEntity<?> getTeams(){

		List<RcmTeamDto> teams = masterDataService.getTeams();
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", teams));

	}
	
	@RequestMapping(value = "/getroles", method = RequestMethod.GET)
	public ResponseEntity<?> getRoles(){

		List<RcmRoleDto> roles = masterDataService.getRoles();
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", roles));

	}

//	@RequestMapping(value = "/getroles/{companyName}", method = RequestMethod.GET)
//	public ResponseEntity<?> getRoles(@PathVariable("companyName")String companyName){
//
//		List<RcmRoleDto> roles = masterDataService.getRoles(companyName);
//		if(roles==null) {
//			return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", null));
//		}
//		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", roles));
//
//	}
	
//	@RequestMapping(value = "/rolesByTeamId/{teamId}", method = RequestMethod.GET)
//	public ResponseEntity<?> rolesByTeamId(@PathVariable("teamId")int teamId){
//
//		List<RcmRoleDto> roles = masterDataService.getRolesByTeamId(teamId);
//		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", roles));
//
//	}
//	
//	@RequestMapping(value = "/defaultRolesByCname/{companyName}", method = RequestMethod.GET)
//	public ResponseEntity<?> defaultRolesByCname(@PathVariable("companyName")String companyName){
//
//		List<RcmRoleDto> defaultRoles = masterDataService.defaultRolesByCompanyName(companyName);
//		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", defaultRoles));
//
//
//	}
	
	@RequestMapping(value = "/getClients", method = RequestMethod.GET)
	public ResponseEntity<?> getClients() {
		List<RcmCompany> clients = null;
		try {
		  clients = masterDataService.getClients();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", clients));
	}
	
	@RequestMapping(value = "/get-sections", method = RequestMethod.GET)
	public ResponseEntity<?> getSections() {
		List<SectionDto> sectionList = null;
		try {
			sectionList = masterDataService.getSections();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", sectionList));
	}

}
