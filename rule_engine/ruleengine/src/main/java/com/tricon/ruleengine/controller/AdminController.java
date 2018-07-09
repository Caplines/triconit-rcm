package com.tricon.ruleengine.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tricon.ruleengine.dto.UserRegistrationDto;
import com.tricon.ruleengine.service.UserService;

@RestController
@RequestMapping("admin")
public class AdminController {

	@CrossOrigin
    @PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public void createUser(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		
		request.getRequestDispatcher("/").forward(request, response);
	}


}