package com.tricon.ruleengine.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


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
	@RequestMapping(value = "/profile", method = RequestMethod.GET)
	public void profileForward(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		forwardRedirect(request, response);
	}
	
	@CrossOrigin
	@RequestMapping(value = {"/ivf", "/ivfbatch","/ivfbatchpre","/report","/ivftreatmentplan"}, method = RequestMethod.GET)
	public void ivfBatchForward(HttpServletRequest request, HttpServletResponse response)
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

	@CrossOrigin
	@RequestMapping(value={ "/ivfclaimid", "/ivfcl","/ivfclbatch","/reportcl","/enreportscl","/usersettings","/dumpOldIVFData" }, method = RequestMethod.GET)
	public void ivfClaimIdForward(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		forwardRedirect(request, response);
	}
	private void forwardRedirect(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		
		request.getRequestDispatcher("/").forward(request, response);
	}
	
	@CrossOrigin
	//@ResponseBody
	@RequestMapping(value = "/.well-known/acme-challenge/8h0NwOQLZreL70OMOZtKMYcM5W2Fme1JUatgJFuTElA", method = RequestMethod.GET
			)//,produces="text/plain")
	public void sslForward(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {       
			response.setContentType("text/plain");
	        PrintWriter out = response.getWriter();
	        out.println("8h0NwOQLZreL70OMOZtKMYcM5W2Fme1JUatgJFuTElA.Pw-9dWQdIvPVoOCxvLb3MlkzQPiQo3jjoi3DPhotZwQ");
	        out.close();
	    } catch (IOException ex) { 
	        //...
	    }
		
		//return "jZDbjQShlYmH4D3kq0tcgndIwf_1ioeegQEH4hbLyjg.XpbGCykpKh__XhBBrdH5ePZE_iXrGYFq_1_IdIuBlXM";
	}
}
