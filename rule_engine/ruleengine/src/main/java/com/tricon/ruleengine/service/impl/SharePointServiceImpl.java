package com.tricon.ruleengine.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.tricon.ruleengine.dao.OfficeDao;
import com.tricon.ruleengine.dao.SharePointDao;
import com.tricon.ruleengine.dao.UserDao;
import com.tricon.ruleengine.dto.MicroSoftGraphToken;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.OneDriveApp;
import com.tricon.ruleengine.model.db.OneDriveFile;
import com.tricon.ruleengine.model.db.User;
import com.tricon.ruleengine.service.SharePointService;
import com.tricon.ruleengine.utils.Constants;
import com.tricon.ruleengine.utils.SendPostAndReadJSon;

@Transactional
@Service
public class SharePointServiceImpl implements SharePointService {


	@Autowired
	OfficeDao od;

	@Autowired
	SharePointDao spd;
	
	@Autowired UserDao userDao;

	@Override
	public String checkForAccessTokens(String officeId) {

		OneDriveApp oneD= spd.getOneDriveAppDetailsByOfficeId(officeId);
		return oneD.getAccessToken();
		// TODO Auto-generated method stub

	}
	
	@Override
	public String generateAuthTokenUrl(String officeId) {
		
		//Office off=od.getOfficeByUuid(officeId);
		OneDriveApp oneD= spd.getOneDriveAppDetailsByOfficeId(officeId);
		return getLoginUrlForAuth(oneD, Constants.APP_AccessToken);
		// TODO Auto-generated method stub

	}

	@Override
	public void saveAuthToken(String officeId,String token) {
		
		//Office off=od.getOfficeByUuid(officeId);
		User user =getUser(SecurityContextHolder.getContext().getAuthentication());
		
		OneDriveApp oneD= spd.getOneDriveAppDetailsByOfficeId(officeId);
		//Office off=od.getOfficeByUuid(officeId);
		oneD.setUpdatedBy(user);
		//oneD.setOffice(off);
		oneD.setAuthToken(token);
		oneD.setRefreshToken(null);
		oneD.setAccessToken(null);
		
		spd.updateOneDrive(oneD);
		// TODO Auto-generated method stub

	}

	@Override
	public String[] generateAccAndRefreshTokensUrlandParams(String token) {
		
	 OneDriveApp oneD=  spd.getOneDriveAppDetailsByAuthCode(token);
	 return 	new String[] {Constants.authorizeUrlToken,getParamsForToken(oneD, token,"authorization_code")};
		// TODO Auto-generated method stub

	}

	private static String getLoginUrlForAuth(OneDriveApp oneD, String responseType) {

		//UUID state = UUID.randomUUID();
		UUID nonce = UUID.randomUUID();
		UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(Constants.authorizeUrl);
		urlBuilder.queryParam("client_id", oneD.getAppLicationId());
		urlBuilder.queryParam("redirect_uri", oneD.getRdUrl());
		urlBuilder.queryParam("response_type", responseType);
		urlBuilder.queryParam("scope", getScopesForAuth());
		urlBuilder.queryParam("state", oneD.getOffice().getUuid());
		urlBuilder.queryParam("nonce", nonce);
		//urlBuilder.queryParam("officeI", oneD.getOffice().getUuid());
		//urlBuilder.queryParam("response_mode", "form_post");

		return urlBuilder.toUriString();
	}
	
	private static String getParamsForToken(OneDriveApp oneD,String code,String grantType) {

		//UUID state = UUID.randomUUID();
		//UUID nonce = UUID.randomUUID();
		UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(Constants.authorizeUrlToken);
		//String params="";
		try {
			return "".concat("client_id=").concat(oneD.getAppLicationId()).//, oneD.getAppLicationId());
			concat("&redirect_uri=").concat(URLEncoder.encode(oneD.getRdUrl(),"UTF-8")).//, oneD.getAppLicationId());
			concat("&grant_type=").concat(grantType).//, oneD.getAppLicationId());
			concat("&scope=").concat(URLEncoder.encode(getScopesForToken(),"UTF-8")).//, oneD.getAppLicationId());
			concat("&code=").concat(code).//, oneD.getAppLicationId());
			concat("&client_secret=").concat(URLEncoder.encode(oneD.getClientSecret(),"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//, oneD.getAppLicationId());
       /*
		urlBuilder.queryParam("client_id", oneD.getAppLicationId());
		urlBuilder.queryParam("redirect_uri", oneD.getRdUrl());
		urlBuilder.queryParam("grant_type", "authorization_code");
		urlBuilder.queryParam("scope", getScopesForToken());
		urlBuilder.queryParam("code", code);
		urlBuilder.queryParam("client_secret", oneD.getClientSecret());
		//urlBuilder.queryParam("officeI", oneD.getOffice().getUuid());
		//urlBuilder.queryParam("response_mode", "form_post");
        */
		return "";
	}
	private static String getParamsForTokenRefresh(OneDriveApp oneD,String code,String grantType) {

		try {
			return "".concat("client_id=").concat(oneD.getAppLicationId()).//, oneD.getAppLicationId());
			concat("&redirect_uri=").concat(URLEncoder.encode(oneD.getRdUrl(),"UTF-8")).//, oneD.getAppLicationId());
			concat("&grant_type=").concat(grantType).//, oneD.getAppLicationId());
			concat("&scope=").concat(URLEncoder.encode(getScopesForToken(),"UTF-8")).//, oneD.getAppLicationId());
			concat("&refresh_token=").concat(code).//, oneD.getAppLicationId());
			concat("&client_secret=").concat(URLEncoder.encode(oneD.getClientSecret(),"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}//, oneD.getAppLicationId());
		return "";
	}

	private static String getScopesForToken() {
		    StringBuilder sb = new StringBuilder();
		    for (String scope: Constants.scopes) {
		      sb.append(scope + " ");
		    }
		    return sb.toString().trim();
		  }

	  private static String getScopesForAuth() {
		    StringBuilder sb = new StringBuilder();
		    for (String scope: Constants.scopes) {
		      sb.append(scope + " ");
		    }
		    return sb.toString().trim();
		  }
	  
	 private User getUser(Authentication authentication) {
		  String email="admin@admin.com";
			User user=null;
			boolean update=false;
			if (authentication!=null) {
				user= userDao.findUserByEmail(authentication.getName());
				//Hibernate.initialize(user.getOffice());
			}else {
				user= userDao.findUserByEmail(email);
			}
			return user;
	  }

	@Override
	public void saveAccessAndRefreshTokenByAuthToken(OneDriveApp oneD,String officeId,String token, String accessToken, String refreshToken) {

	
	User user =getUser(SecurityContextHolder.getContext().getAuthentication());
	
	//OneDriveApp oneD= spd.getOneFileDetailsByAuthCode(token);
	if (oneD==null) oneD= spd.getOneDriveAppDetailsByOfficeId(officeId);
	oneD.setUpdatedBy(user);
	oneD.setRefreshToken(refreshToken);
	oneD.setAccessToken(accessToken);
	
	spd.updateOneDrive(oneD);

	//
		
	}

	@Override
	public MicroSoftGraphToken reGenerateAccAndRefreshTokens(OneDriveApp oneDA,String refreshCode) throws IOException {
		
		String params =getParamsForTokenRefresh(oneDA, refreshCode,"refresh_token");
		System.out.println("params");
		System.out.println(params);
		
		MicroSoftGraphToken graph =(MicroSoftGraphToken) SendPostAndReadJSon.sendPostRequest(params,Constants.authorizeUrlToken,MicroSoftGraphToken.class);
		
		// TODO Auto-generated method stub
		return graph;
	}

	@Override
	public String webUrlForMicrosoftSheets(OneDriveFile oneDF,OneDriveApp app) {
		
		return  SendPostAndReadJSon.sendGetDataGraphDownloadUrl(app.getAccessToken(),
				Constants.grapworkUrlDownload.replace("item-id", oneDF.getSheetId()));

	}

}
