package com.tricon.ruleengine.controller;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tricon.ruleengine.dto.GenericResponse;
import com.tricon.ruleengine.dto.TreatmentPlanValidationDto;
import com.tricon.ruleengine.dto.UserRegistrationDto;
import com.tricon.ruleengine.model.db.User;
import com.tricon.ruleengine.security.service.UserDetailsServiceImpl;
import com.tricon.ruleengine.service.UserService;


/**
 * @author Deepak.Dogra
 *
 */
@Controller
public class AccountController {

	public static final Logger logger = LoggerFactory.getLogger(AccountController.class);

	@Autowired
	private UserService userService;

	// request method to create a new account by a guest
	@CrossOrigin
	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public void createUser(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		
		userService.getAllOffices();
		//return new ResponseEntity<User>(userService.save(newUser), HttpStatus.CREATED);
		//return "forward:/register";
		request.getRequestDispatcher("/").forward(request, response);
	}


}