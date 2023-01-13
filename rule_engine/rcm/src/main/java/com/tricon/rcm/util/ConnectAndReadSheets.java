package com.tricon.rcm.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.springframework.context.annotation.Configuration;

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
import com.tricon.rcm.dto.ClaimFromSheet;
import com.tricon.rcm.dto.InsuranceNameTypeDto;
import com.tricon.rcm.dto.RemoteLietStatusCount;
import com.tricon.rcm.dto.RemoteLiteDataDto;
import com.tricon.rcm.dto.RemoteLiteDto;

@Configuration
public class ConnectAndReadSheets {

	static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";

	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);

	private static NetHttpTransport HTTP_TRANSPORT = null;

	static {
		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		} catch (GeneralSecurityException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		;
	}

	public static Credential getCredentials(String clientDir, String clientFolder) throws IOException {
		// Load client secrets.
		File initialFile = new File(clientDir);
		InputStream targetStream = new FileInputStream(initialFile);
		// InputStream in =
		// GoogleFileController.class.getResourceAsStream(CLIENT_SECRET_DIR);
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(targetStream));

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, SCOPES).setDataStoreFactory(new FileDataStoreFactory(new java.io.File(clientFolder)))
						.setAccessType("offline").build();
		return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
	}

	public static RemoteLiteDataDto readRemoteLiteSheet(String spreadsheetId, String sheetName, String clientDir,
			String clientFolder) throws IOException {
		Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(clientDir, clientFolder))
				.setApplicationName(APPLICATION_NAME).build();
		ValueRange response = service.spreadsheets().values().get(spreadsheetId, sheetName).execute();
		return readFullRemoteLiteSheet(response);
	}

	private static RemoteLiteDataDto readFullRemoteLiteSheet(ValueRange range) {

		List<List<Object>> values = range.getValues();

		RemoteLietStatusCount statusCount = new RemoteLietStatusCount();
		statusCount.setAcceptedCount(0);
		statusCount.setDuplicateCount(0);
		statusCount.setPrintedCount(0);
		statusCount.setRejectedCount(0);

		RemoteLiteDataDto fulldto = new RemoteLiteDataDto();
		List<RemoteLiteDto> list = new ArrayList<>();
		ListIterator li = values.listIterator();
		RemoteLiteDto dto = null;
		// IVFHistorySheet vifH = null;

		int ctr = 0;
		while (li.hasNext()) {
			ArrayList<String> obj = (ArrayList<String>) li.next();
			ctr++;
			if (ctr < 3)
				continue;
			try {
				int x = -1;
				dto = new RemoteLiteDto(obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
						obj.get(++x), obj.get(++x), obj.get(++x));
				list.add(dto);
				if (dto.getStatus().equalsIgnoreCase("Accepted")) {
					statusCount.setAcceptedCount(statusCount.getAcceptedCount() + 1);
				} else if (dto.getStatus().equalsIgnoreCase("Rejected")) {
					statusCount.setRejectedCount(statusCount.getRejectedCount() + 1);
				} else if (dto.getStatus().equalsIgnoreCase("Duplicate")) {
					statusCount.setDuplicateCount(statusCount.getDuplicateCount() + 1);
				} else if (dto.getStatus().equalsIgnoreCase("Printed")) {
					statusCount.setPrintedCount(statusCount.getPrintedCount() + 1);
				}

			} catch (Exception ex) {
				continue;
			}

		}

		fulldto.setDataList(list);
		fulldto.setStatusCount(statusCount);
		return fulldto;

	}

	public static List<InsuranceNameTypeDto> readInsuranceMappingSheet(String spreadsheetId, String sheetName,
			String clientDir, String clientFolder) throws IOException {
		Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(clientDir, clientFolder))
				.setApplicationName(APPLICATION_NAME).build();
		ValueRange response = service.spreadsheets().values().get(spreadsheetId, sheetName).execute();
		return readInsuranceMappingSheetFull(response);
	}

	private static List<InsuranceNameTypeDto> readInsuranceMappingSheetFull(ValueRange range) {

		List<List<Object>> values = range.getValues();
		List<InsuranceNameTypeDto> list = new ArrayList<>();
		InsuranceNameTypeDto dto = null;
		ListIterator li = values.listIterator();
		int ctr = 0;
		while (li.hasNext()) {
			ArrayList<String> obj = (ArrayList<String>) li.next();
			ctr++;
			if (ctr < 2)
				continue;
			try {
				int x = -1;
				dto = new InsuranceNameTypeDto(obj.get(++x), obj.get(++x));
				list.add(dto);

			} catch (Exception ex) {
				continue;
			}

		}

		return list;

	}
	
	
	public static List<ClaimFromSheet> readClaimsFromGSheet(String spreadsheetId, String sheetName,
			String clientDir, String clientFolder) throws IOException {
		Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(clientDir, clientFolder))
				.setApplicationName(APPLICATION_NAME).build();
		ValueRange response = service.spreadsheets().values().get(spreadsheetId, sheetName).execute();
		List<List<Object>> values = response.getValues();
		List<ClaimFromSheet> list = new ArrayList<>();
		ClaimFromSheet dto = null;
		ListIterator li = values.listIterator();
		int ctr = 0;
		while (li.hasNext()) {
			ArrayList<String> obj = (ArrayList<String>) li.next();
			ctr++;
			if (ctr < 2)
				continue;
			try {
				int x = -1;
				dto = new ClaimFromSheet(obj.get(++x), obj.get(++x),obj.get(++x),obj.get(++x),obj.get(++x),obj.get(++x),obj.get(++x),obj.get(++x),obj.get(++x),
						obj.get(++x),obj.get(++x),obj.get(++x),obj.get(++x),obj.get(++x),obj.get(++x),obj.get(++x),obj.get(++x));
				list.add(dto);

			} catch (Exception ex) {
				continue;
			}

		}

		return list;
	}

	

}
