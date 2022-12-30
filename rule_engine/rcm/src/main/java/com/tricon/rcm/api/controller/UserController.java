package com.tricon.rcm.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tricon.rcm.dto.GenericResponse;
import com.tricon.rcm.dto.PasswordResetDto;
import com.tricon.rcm.service.impl.UserServiceImpl;
import com.tricon.rcm.util.MessageConstants;

@RestController
@CrossOrigin
public class UserController {

	private final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserServiceImpl userService;

	@Autowired
	@Qualifier("jwtUserDetailsService")
	private UserDetailsService userDetailsService;

	@RequestMapping(value = "/updatepassword", method = RequestMethod.POST)
	public ResponseEntity<?> updatePasswordOfUserOrAdmin(@RequestBody PasswordResetDto dto) {
		GenericResponse response = null;
		if (dto.getPassword().trim().equals("")) {
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.PASSWORD_EMPTY, null));
		}
		try {
			response = userService.updatePassword(dto.getPassword());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.ok(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(response);
	}

	@RequestMapping(value = "/users/{role}", method = RequestMethod.GET)
	public ResponseEntity<?> getUsersOfParticularTeam(@PathVariable("role") String role) {
		GenericResponse response = null;
		try {
			response = userService.getUsersByRole(role);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.ok(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(response);
	}
}
