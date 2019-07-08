package com.tricon.ruleengine.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


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
	@RequestMapping(value = "/enreports", method = RequestMethod.GET)
	public void enreportsForward(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		forwardRedirect(request, response);
	}

	@CrossOrigin
	@RequestMapping(value = "/scrap", method = RequestMethod.GET)
	public void scrapForward(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		forwardRedirect(request, response);
	}

	/*
	@CrossOrigin
	@RequestMapping(value={ "/ivfclaimid", "/ivfcl","/ivfclbatch","/reportcl","/enreportscl" }, method = RequestMethod.GET)
	public void ivfClaimIdForward(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		forwardRedirect(request, response);
	}
  */
	@CrossOrigin
	@RequestMapping(value = "/ivfclaimid", method = RequestMethod.GET)
	public void ivfClaimIdForward(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		forwardRedirect(request, response);
	}

	@CrossOrigin
	@RequestMapping(value = "/ivfcl", method = RequestMethod.GET)
	public void ivfClForward(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		forwardRedirect(request, response);
	}

	@CrossOrigin
	@RequestMapping(value = "/ivfclbatch", method = RequestMethod.GET)
	public void ivfclbatchForward(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {

			forwardRedirect(request, response);
	}

	@CrossOrigin
	@RequestMapping(value = "/reportcl", method = RequestMethod.GET)
	public void reportclForward(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		forwardRedirect(request, response);
	}

	@CrossOrigin
	@RequestMapping(value = "/enreportscl", method = RequestMethod.GET)
	public void enreportsclForward(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		forwardRedirect(request, response);
	}

	private void forwardRedirect(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		
		request.getRequestDispatcher("/").forward(request, response);
	}
	
}
