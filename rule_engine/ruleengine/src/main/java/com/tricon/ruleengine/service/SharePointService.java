package com.tricon.ruleengine.service;

import java.io.IOException;

import com.tricon.ruleengine.dto.MicroSoftGraphToken;
import com.tricon.ruleengine.model.db.OneDriveApp;
import com.tricon.ruleengine.model.db.OneDriveFile;

public interface SharePointService {
	
	public String checkForAccessTokens(String officeId);
	
	public String generateAuthTokenUrl(String officeId);

	public String[] generateAccAndRefreshTokensUrlandParams(String code);
	
	public void saveAuthToken(String officeId,String token);

	/**
	 * 
	 * @param oneD This can be null
	 * @param officeId
	 * @param Token pass this as null for now
	 * @param accessToken
	 * @param refreshToken
	 */
	public void saveAccessAndRefreshTokenByAuthToken(OneDriveApp oneD,String officeId,String Token,String accessToken,String refreshToken);

	public MicroSoftGraphToken reGenerateAccAndRefreshTokens(OneDriveApp oneDA, String refreshCode)  throws IOException;


	public String webUrlForMicrosoftSheets(OneDriveFile oneDF,OneDriveApp app);
}
