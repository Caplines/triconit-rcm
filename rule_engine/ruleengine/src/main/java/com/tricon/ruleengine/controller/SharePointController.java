package com.tricon.ruleengine.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.tricon.ruleengine.dto.MicroSoftGraphToken;
import com.tricon.ruleengine.service.SharePointService;
import com.tricon.ruleengine.utils.SendPostAndReadJSon;

/**
 * @author Deepak.Dogra
 *
 */
@Controller
public class SharePointController {

	@Autowired
	SharePointService spservice;

//	@Autowired
//	private HttpSession httpSession;

	/*
	 * @RequestMapping(value = "/readDriveSuc", method = RequestMethod.POST) public
	 * String authorize(@RequestParam("code") String code, @RequestParam("id_token")
	 * String idToken,
	 * 
	 * @RequestParam("state") UUID state, HttpServletRequest request) { // Get the
	 * expected state value from the session System.out.println("called....");
	 * HttpSession session = request.getSession(); UUID expectedState = (UUID)
	 * session.getAttribute("expected_state"); UUID expectedNonce = (UUID)
	 * session.getAttribute("expected_nonce");
	 * 
	 * // Make sure that the state query parameter returned matches // the expected
	 * state if (state.equals(expectedState)) { //session.setAttribute("authCode",
	 * code); //session.setAttribute("idToken", idToken); } else {
	 * System.out.println("Error"); session.setAttribute("error",
	 * "Unexpected state returned from authority."); }
	 * 
	 * System.out.println("authCode"+ code); System.out.println("idToken"+ idToken);
	 * return null; }
	 */
	@RequestMapping(value = "/readDriveSuc") //
	public ModelAndView authorize(HttpServletRequest request) {
		// Get the expected state value from the session

		System.out.println("4444----" + request.getParameter("code"));
		System.out.println(request.getParameter("state"));
		System.out.println("called....");
		if (request.getParameter("state") != null) {
			spservice.saveAuthToken(request.getParameter("state"), request.getParameter("code"));
			String[] urlParams = spservice.generateAccAndRefreshTokensUrlandParams(request.getParameter("code"));
			// httpSession.setAttribute("codeOpen", request.getParameter("code"));
			try {
				System.out.println(urlParams[1]);
				System.out.println(urlParams[0]);

				MicroSoftGraphToken graph = (MicroSoftGraphToken) SendPostAndReadJSon.sendPostRequest(urlParams[1],
						urlParams[0], MicroSoftGraphToken.class);
				// spservice.saveAccessAndRefreshTokenByAuthToken((String)httpSession.getAttribute("codeOpen"),
				// graph.getAccess_token(), graph.getRefresh_token());
				spservice.saveAccessAndRefreshTokenByAuthToken(null, request.getParameter("state"),
						request.getParameter("code"), graph.getAccess_token(), graph.getRefresh_token());

				// httpSession.removeAttribute("codeOpen");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// return new ModelAndView("redirect:" +
			// spservice.generateAccaAndRefreshTokensUrl(request.getParameter("code")));

		}

		return new ModelAndView("sharepoint");
	}

	@RequestMapping(value = "/sharePointIni", method = RequestMethod.GET)
	public ModelAndView SpInitiate(@RequestParam("officeId") String officeId, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		System.out.println(spservice.generateAuthTokenUrl(officeId));

		return new ModelAndView("redirect:" + spservice.generateAuthTokenUrl(officeId));
	}

}
