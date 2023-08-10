package com.tricon.rcm.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tricon.rcm.dto.GenericResponse;
import com.tricon.rcm.dto.RcmClaimsServiceRuleValidationDto;
import com.tricon.rcm.service.impl.ClaimServiceImpl;

import io.swagger.annotations.ApiOperation;

@Controller
public class CommonRedirectionController {

	@Autowired
	ClaimServiceImpl claimServiceImpl;
	
	@RequestMapping(value = { "/login", "/tool-update", "/fetch-claims", "/users-status", "/register", "/manage-office",
			"/manage-client", "/user-setting", "/claim-assignment","/list-of-claims","/update-pass", "/billing-claims/*",
			"/all-pendency","/production","/tool-update/issue-claims"}, method = RequestMethod.GET)
	public void forward(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("IP ---> " + request.getServerName());
		System.out.println("Scheme-->" + request.getScheme());
		forwardRedirect(request, response);
	}
	
	@RequestMapping(value = { "/billing-claims/{cl}/ivf" }, method = RequestMethod.GET)
	public void forwardIVF(@PathVariable("cl") String cl,HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("IP ---> " + request.getServerName());
		System.out.println("Scheme-->" + request.getScheme());
		forwardRedirect(request, response);
	}
	
	@RequestMapping(value = { "/billing-claims/{cl}/tp" }, method = RequestMethod.GET)
	public void forwardTP(@PathVariable("cl") String cl,HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("IP ---> " + request.getServerName());
		System.out.println("Scheme-->" + request.getScheme());
		forwardRedirect(request, response);
	}

	private void forwardRedirect(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.getRequestDispatcher("/").forward(request, response);
	}
	
	
	@GetMapping("/api/testSVSheet")
	public ResponseEntity<Object> aaa() {
        
    Object dd=	claimServiceImpl.readServiceValidationFromGSheet();
		
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "",
				dd));
	}
	

}
