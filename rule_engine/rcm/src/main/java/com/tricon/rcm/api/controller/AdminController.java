package com.tricon.rcm.api.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tricon.rcm.dto.FindUserDto;
import com.tricon.rcm.dto.GenericResponse;
import com.tricon.rcm.dto.PasswordResetDto;
import com.tricon.rcm.dto.ResetStatusDto;
import com.tricon.rcm.dto.UserRegistrationDto;
import com.tricon.rcm.service.impl.AdminServiceImpl;
import com.tricon.rcm.util.MessageConstants;

@RestController
@CrossOrigin
public class AdminController {

	private final Logger logger = LoggerFactory.getLogger(AdminController.class);

	@Autowired
	private AdminServiceImpl serviceImpl;

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationDto dto) {
		GenericResponse response = null;
		try {
			response = serviceImpl.registerUser(dto);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.ok(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(response);
	}

	@RequestMapping(value = "/resetpassword", method = RequestMethod.POST)
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> resetpasswordByAdmin(@RequestBody PasswordResetDto dto) {
		if (dto.getPassword().trim().equals("") || dto.getUuid().trim().equals("")) {
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.EMPTY_RESOURCE, null));
		}
		GenericResponse response = null;
		try {
			response = serviceImpl.passwordUpdation(dto);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.ok(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(response);
	}

	@RequestMapping(value = "/finduser", method = RequestMethod.POST)
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> findUserByUsername(@RequestBody FindUserDto dto) {
		GenericResponse response = null;
		if (dto.getUsername().trim().equals("")) {
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.EMPTY_RESOURCE, null));
		}
		try {
			response = serviceImpl.findUserByUserName(dto);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.ok(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(response);
	}

	@RequestMapping(value = "/getAllUsers/{pageNumber}", method = RequestMethod.GET)
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> getAllUsers(@PathVariable("pageNumber") int pageNumber) {
		GenericResponse response = null;
		try {
			response = serviceImpl.getAllUsers(pageNumber);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.ok(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(response);
	}

	@RequestMapping(value = "/resetstatus", method = RequestMethod.POST)
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> resetstatus(@RequestBody ResetStatusDto dto) {
		GenericResponse response = null;
		if (dto.getEnable().isEmpty() && dto.getDisable().isEmpty()) {
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.EMPTY_RESOURCE, null));
		}
		try {
			response = serviceImpl.resetUserStatus(dto);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.ok(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(response);
	}
	
	@RequestMapping(value = "/finduserbydetail/{query}", method = RequestMethod.GET)
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> findUserByDetail(@PathVariable("query")String searchQuery) {
		GenericResponse response = null;
		try {
			response = serviceImpl.findUserByDetail(searchQuery);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.ok(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(response);
	}
}
