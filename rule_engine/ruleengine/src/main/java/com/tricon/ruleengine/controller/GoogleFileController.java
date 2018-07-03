package com.tricon.ruleengine.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

/**
 * @author Deepak.Dogra
 *
 */
@Controller
public class GoogleFileController {

	@Autowired
	private Environment environment;
	
    private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String CREDENTIALS_FOLDER = "D:\\Project\\Tricon\\TimeEstimates\\IV rule engine\\Code Related\\2\\"; // Directory to store user credentials.
    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved credentials/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
    private static final String CLIENT_SECRET_DIR = "D:\\Project\\Tricon\\TimeEstimates\\IV rule engine\\Code Related\\2\\client_secret.json";

    /*
     * https://accounts.google.com/o/oauth2/v2/auth?scope=https://www.googleapis.com/auth/drive.file&access_type=offline&include_granted_scopes=true&state=state_parameter_passthrough_value&redirect_uri=http://localhost:8080/sss&response_type=code&client_id=125735930803-sj8kb5ofpr0kem4ldvqs95ce99d3sv2m.apps.googleusercontent.com
     */

	@RequestMapping(value = "/getIVF/{officeId}/{fileid}", method = RequestMethod.GET)
	public void fetchIVFData(@PathVariable String officeId,@PathVariable String fileid) throws GeneralSecurityException, IOException {
		//check for office Id is its same as logged in user then continue
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		final String spreadsheetId = "1HYEYlEtOxXeCN0bJ7Q9c6d5i3c5W2o7sWAzQUdrLveM";
		Credential cd= getCredentials(HTTP_TRANSPORT);
		System.err.println( cd.getExpiresInSeconds());
		final String range = "Rules - OM/TP";// Name of Sheet
		Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY,cd )
				.setApplicationName(APPLICATION_NAME).build();
		ValueRange response = service.spreadsheets().values().get(spreadsheetId, range).execute();
		List<List<Object>> values = response.getValues();
		if (values == null || values.isEmpty()) {
			System.out.println("No data found.");
		} else {
			System.out.println("Name, Major");
			for (List row : values) {
				// Print columns A and E, which correspond to indices 0 and 4.
				// System.out.printf("%s, %s\n", row.get(0), row.get(1));
				System.out.println(row.size());
				if (row.size() > 0) {
					System.out.printf("%s, %s\n", row.get(0), row.get(1));
				}
			}
		}

	}

	private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
		// Load client secrets.
		File initialFile = new File("d:/client_secret1.json");
		InputStream targetStream = new FileInputStream(initialFile);
		//InputStream in = GoogleFileController.class.getResourceAsStream(CLIENT_SECRET_DIR);
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(targetStream));

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, SCOPES)
						.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(CREDENTIALS_FOLDER)))
						.setAccessType("offline").build();
		return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
	}
	
	private  Credential test() throws GeneralSecurityException, IOException {
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		 Credential credential =
			        new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken("token");
			    HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(credential);
			    return credential;

	}

}
