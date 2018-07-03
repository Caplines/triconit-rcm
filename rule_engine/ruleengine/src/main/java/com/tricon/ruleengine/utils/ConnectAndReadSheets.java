package com.tricon.ruleengine.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

public class ConnectAndReadSheets {

	private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static final String CREDENTIALS_FOLDER = "D:\\Project\\Tricon\\TimeEstimates\\IV rule engine\\Code Related\\2\\"; // Directory
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
    private static final String CLIENT_SECRET_DIR = "D:\\Project\\Tricon\\TimeEstimates\\IV rule engine\\Code Related\\2\\client_secret.json";
																															// to
																																// store
																																// user
																																// credentials.

	private static Credential getCredentials( NetHttpTransport HTTP_TRANSPORT) throws IOException {
		// Load client secrets.
		File initialFile = new File("d:/client_secret1.json");
		InputStream targetStream = new FileInputStream(initialFile);
		// InputStream in =
		// GoogleFileController.class.getResourceAsStream(CLIENT_SECRET_DIR);
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(targetStream));

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, SCOPES)
						.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(CREDENTIALS_FOLDER)))
						.setAccessType("offline").build();
		return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
	}
	
	public static ValueRange readSheet(String spreadsheetId,String sheetName,Credential credential  ) {
		//final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		//final String spreadsheetId = "1HYEYlEtOxXeCN0bJ7Q9c6d5i3c5W2o7sWAzQUdrLveM";
		//Credential cd= getCredentials(HTTP_TRANSPORT);
		//System.err.println( cd.getExpiresInSeconds());
		final String range = "Rules - OM/TP";// Name of Sheet
		//Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY,credential )
			//	.setApplicationName(APPLICATION_NAME).build();
		//ValueRange response = service.spreadsheets().values().get(spreadsheetId, range).execute();
       return null;//response;
	}

}
