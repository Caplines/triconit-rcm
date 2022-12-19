package com.tricon.ruleengine.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.tricon.ruleengine.dto.scrapping.RosterDetails;
import com.tricon.ruleengine.utils.ConnectAndReadSheets;

@Controller
public class TestContoller {
	
	@Value("${google.credential.folder}")
	private String CREDENTIALS_FOLDER;

	@Value("${google.client.secret}")
	private String CLIENT_SECRET_DIR;


	@RequestMapping(value = "/writeData", method = RequestMethod.GET)
	public void fetchIVFData() throws GeneralSecurityException, IOException {
		System.out.println(CREDENTIALS_FOLDER);
		System.out.println(CLIENT_SECRET_DIR);
		List<RosterDetails> rList= new ArrayList<>();
		RosterDetails r=new RosterDetails();
		r.setAddress1("address1");
		r.setAddress2("address2");
		r.setAssignedDentistF("assignedDentistF");
		r.setCity("city");
		r.setDob("dob");
		r.setPatFName("patFName");
		r.setSubscriberId("subscriberId");
		r.setTelephone("telephone");
		rList.add(r);
		ConnectAndReadSheets.updateSheetRoster("1WCa3Ux8hFvaWBLSz8qFosfpRqUmP220GJE9frzuqGqk",
				"Sheet1", CLIENT_SECRET_DIR, CREDENTIALS_FOLDER,rList,6,6,"NO");

	}
}
