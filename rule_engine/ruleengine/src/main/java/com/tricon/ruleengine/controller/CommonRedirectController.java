package com.tricon.ruleengine.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tricon.ruleengine.service.UserService;

/**
 * @author Deepak.Dogra
 *
 */
@Controller
public class CommonRedirectController {

	//public static final Logger logger = LoggerFactory.getLogger(CommonRedirectController.class);

	// request method Forward Login Page
	@CrossOrigin
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public void loginForward(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		forwardRedirect(request, response);
	}
	// request method Forward Register Page
	@CrossOrigin
	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public void registerForward(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		forwardRedirect(request, response);
	}

	// request method Forward Register Page
	@CrossOrigin
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public void logoutForward(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		forwardRedirect(request, response);
	}

	// request method Forward IVF Page
	@CrossOrigin
	@RequestMapping(value = "/ivf", method = RequestMethod.GET)
	public void ivfForward(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		forwardRedirect(request, response);
	}
	// request method Forward IVF Page
	@CrossOrigin
	@RequestMapping(value = "/profile", method = RequestMethod.GET)
	public void profileForward(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		forwardRedirect(request, response);
	}
	
	@CrossOrigin
	@RequestMapping(value = "/ivfbatch", method = RequestMethod.GET)
	public void ivfBatchForward(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		forwardRedirect(request, response);
	}
	
	@CrossOrigin
	@RequestMapping(value = "/ivfbatchpre", method = RequestMethod.GET)
	public void ivfBatchPreForward(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		forwardRedirect(request, response);
	}
	
	@CrossOrigin
	@RequestMapping(value = "/report", method = RequestMethod.GET)
	public void reportForward(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		forwardRedirect(request, response);
	}

	@CrossOrigin
	@RequestMapping(value = "/ivftreatmentplan", method = RequestMethod.GET)
	public void treatmentForward(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		forwardRedirect(request, response);
	}
	
	@CrossOrigin
	@RequestMapping(value = "/diagnosticcheck", method = RequestMethod.GET)

 void diagnosticcheckForward(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		forwardRedirect(request, response);
	}

	@CrossOrigin
	@RequestMapping(value = "/userinput", method = RequestMethod.GET)
	public void userinputForward(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		forwardRedirect(request, response);
	}
	
	@CrossOrigin
	@RequestMapping(value = "/enreport", method = RequestMethod.GET)
	public void enreportsForward(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		forwardRedirect(request, response);
	}
	
	private void forwardRedirect(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		
		request.getRequestDispatcher("/").forward(request, response);
	}
	//
}
