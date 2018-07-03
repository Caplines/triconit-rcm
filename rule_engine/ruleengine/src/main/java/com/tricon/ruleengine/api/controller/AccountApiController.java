package com.tricon.ruleengine.api.controller;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tricon.ruleengine.dto.GenericResponse;
import com.tricon.ruleengine.dto.UserRegistrationDto;
import com.tricon.ruleengine.model.db.User;
import com.tricon.ruleengine.security.service.UserDetailsServiceImpl;
import com.tricon.ruleengine.service.UserService;


/**
 * @author Deepak.Dogra
 *
 */
@RestController
@RequestMapping("account")
public class AccountApiController {

	public static final Logger logger = LoggerFactory.getLogger(AccountApiController.class);

	@Autowired
	private UserService userService;

	// request method to create a new account by a guest
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ResponseEntity<?> registerUser(@RequestBody UserRegistrationDto dto) {
		
		
		userService.registerUser(dto);
		//return new ResponseEntity<User>(userService.save(newUser), HttpStatus.CREATED);
		return null;
	}

	// this is the login api/service
	@RequestMapping("/login")
	public Principal user(Principal principal) {
		logger.info("user logged "+principal);
		return principal;
	}
}