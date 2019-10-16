package com.tricon.ruleengine.api.controller;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tricon.ruleengine.dto.CaplineIVFFormDto;
import com.tricon.ruleengine.dto.CaplineIVFQueryFormDto;
import com.tricon.ruleengine.dto.GenericResponse;
import com.tricon.ruleengine.dto.UserRegistrationDto;
import com.tricon.ruleengine.model.db.Patient;
import com.tricon.ruleengine.service.CaplineIVFGoogleFormService;

@CrossOrigin
@RestController
public class CaplineIVFGoogleFormController {


	@Autowired
	CaplineIVFGoogleFormService civf;
	
	
	@CrossOrigin
	@PostMapping
	@RequestMapping(value = "/savedatatore")
	public ResponseEntity<Object> saveIVFFromGoogleForm(@RequestBody CaplineIVFFormDto dto,
			HttpServletRequest request) {
		//
		
		Integer i=0;
		try {
			i = civf.saveIVFFormData(dto);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String mess="Data saved sucessfully";
		if (i==0) mess="Data not saved sucessfully";
		
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", mess));

	}

	@PostMapping
	@RequestMapping(value = "/queryivdatafromdb")
	public ResponseEntity<Object> queryDataFromDB(@RequestBody CaplineIVFQueryFormDto dto,
			HttpServletRequest request) {
		//
		
		Integer i=0;
		List<CaplineIVFFormDto> cap=null;
		try {
			cap = (List<CaplineIVFFormDto>) civf.searchIVFData(dto);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", cap));

	}

	
}
