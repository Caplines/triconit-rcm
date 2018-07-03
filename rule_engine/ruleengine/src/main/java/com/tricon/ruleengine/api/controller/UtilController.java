package com.tricon.ruleengine.api.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tricon.ruleengine.dto.GenericResponse;
import com.tricon.ruleengine.dto.OfficeDto;
import com.tricon.ruleengine.service.UserService;

@Controller
public class UtilController {

	@Autowired
	private UserService userService;

	@CrossOrigin
	@RequestMapping(value = "/getoffices", method = RequestMethod.GET)
	public ResponseEntity<?> registerUser() {

		Optional<List<OfficeDto>> offices = userService.getAllOffices();
		if (offices.isPresent() && offices.get() != null) {
			return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", offices.get()));
		}

		else
			return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", ""));

	}
}
