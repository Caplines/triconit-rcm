package com.tricon.rcm.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class CommonRedirectionController {

	@RequestMapping(value = { "/login", "/tool-update", "/fetch-claims", "/users-status", "/register", "/manage-office",
			"/manage-client", "/user-setting", "/claim-assignment","/list-of-claims","/update-pass", "/billing-claims/*" }, method = RequestMethod.GET)
	public void forward(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("IP ---> " + request.getServerName());
		System.out.println("Scheme-->" + request.getScheme());
		forwardRedirect(request, response);
	}
	
	@RequestMapping(value = { "/billing-claims/{cl}/ivf" }, method = RequestMethod.GET)
	public void forwardC(@PathVariable("claimuuid") String cl,HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("IP ---> " + request.getServerName());
		System.out.println("Scheme-->" + request.getScheme());
		forwardRedirect(request, response);
	}

	private void forwardRedirect(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.getRequestDispatcher("/").forward(request, response);
	}

}
