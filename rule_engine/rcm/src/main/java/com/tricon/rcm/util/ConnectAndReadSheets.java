package com.tricon.rcm.util;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.tricon.rcm.dto.ClaimFromSheet;
import com.tricon.rcm.dto.ClaimServiceValidationGSheet;
import com.tricon.rcm.dto.ClaimServiceValidationGSheetData;
import com.tricon.rcm.dto.CredentialData;
import com.tricon.rcm.dto.CredentialDataAnesthesia;
import com.tricon.rcm.dto.InsuranceNameTypeDto;
import com.tricon.rcm.dto.ProivderHelpingSheetDto;
import com.tricon.rcm.dto.ProviderCodeWithOffice;
import com.tricon.rcm.dto.ProviderCodeWithSpecialty;
import com.tricon.rcm.dto.RemoteLietStatusCount;
import com.tricon.rcm.dto.RemoteLiteDataDto;
import com.tricon.rcm.dto.RemoteLiteDto;
import com.tricon.rcm.dto.TimelyFilingLimitDto;
import com.tricon.rcm.service.impl.RuleEngineService;

@Configuration
public class ConnectAndReadSheets {

	static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";

	private final static Logger logger = LoggerFactory.getLogger(ConnectAndReadSheets.class);

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

	/**
	 * Authenticates using service-account credentials from environment variables.
	 * G_SHEET_CLIENT_EMAIL  - service account email
	 * G_SHEET_CLIENT_PRIVATE_KEY - RSA private key (PEM, with or without headers, \n or real newlines)
	 * Both clientDir and clientFolder are kept for call-site compatibility but are not used.
	 */
	public static HttpRequestInitializer getCredentials(String clientDir, String clientFolder) throws IOException {
		String clientEmail = System.getenv("G_SHEET_CLIENT_EMAIL");
		String rawKey = System.getenv("G_SHEET_CLIENT_PRIVATE_KEY");

		if (clientEmail == null || clientEmail.isEmpty()) {
			throw new IOException("G_SHEET_CLIENT_EMAIL environment variable is not set");
		}
		if (rawKey == null || rawKey.isEmpty()) {
			throw new IOException("G_SHEET_CLIENT_PRIVATE_KEY environment variable is not set");
		}

		// Normalise: replace literal \n with real newlines, strip PEM headers and whitespace
		String pemBody = rawKey
				.replace("\\n", "\n")
				.replace("-----BEGIN PRIVATE KEY-----", "")
				.replace("-----END PRIVATE KEY-----", "")
				.replaceAll("\\s+", "");

		try {
			byte[] keyBytes = Base64.getDecoder().decode(pemBody);
			PrivateKey privateKey = KeyFactory.getInstance("RSA")
					.generatePrivate(new PKCS8EncodedKeySpec(keyBytes));

			ServiceAccountCredentials credentials = ServiceAccountCredentials.newBuilder()
					.setClientEmail(clientEmail)
					.setPrivateKey(privateKey)
					.setScopes(SCOPES)
					.build();

			return new HttpCredentialsAdapter(credentials);
		} catch (GeneralSecurityException e) {
			throw new IOException("Failed to build service account credentials from env vars", e);
		}
	}

	public static HashMap<String, RemoteLietStatusCount> readRemoteLiteSheet(String spreadsheetId, String sheetName,
			String clientDir, String clientFolder) throws IOException {
		Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(clientDir, clientFolder))
				.setApplicationName(APPLICATION_NAME).build();
		ValueRange response = service.spreadsheets().values().get(spreadsheetId, sheetName).execute();
		return readFullRemoteLiteSheet(response);
	}

	private static HashMap<String, RemoteLietStatusCount> readFullRemoteLiteSheet(ValueRange range) {

		List<List<Object>> values = range.getValues();

		HashMap<String, RemoteLietStatusCount> map = new HashMap<>();
		// RemoteLiteDataDto fulldto = new RemoteLiteDataDto();
		// List<RemoteLiteDto> list = new ArrayList<>();
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
						obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x));

				String officeName = dto.getOffice();

				if (map.get(officeName) == null) {
					RemoteLietStatusCount xt = new RemoteLietStatusCount();
					xt.setAcceptedCount(0);
					xt.setDuplicateCount(0);
					xt.setPrintedCount(0);
					xt.setRejectedCount(0);
					map.put(officeName, xt);
				}

				RemoteLietStatusCount statusCount = map.get(officeName);

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

		// fulldto.setDataList(list);
		// fulldto.setStatusCount(statusCount);
		return map;

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
			int x = -1;
			if (ctr < 2)
				continue;
			try {
				
				dto = new InsuranceNameTypeDto(obj.get(++x),obj.get(++x), obj.get(++x));
				

			} catch (Exception ex) {
				continue;
			}
			try {
				
				dto.setInsuranceCode(obj.get(++x));
			} catch (Exception m) {
			}
			 if (dto.getInsuranceCode()==null){
            	 dto.setInsuranceCode("");
             }
             try {
				
				dto.setPreferredModeOfSubmission(obj.get(++x));
			} catch (Exception m) {
			}
             if (dto.getPreferredModeOfSubmission()==null){
            	 dto.setPreferredModeOfSubmission("");
             }
			list.add(dto);
		}
		logger.info("readInsuranceMappingSheetFull");
		if (list != null) {
			logger.info("Size-->" + list.size());
		}
		return list;

	}

	public static List<TimelyFilingLimitDto> readTimelyFilingLimitMappingSheet(String spreadsheetId, String sheetName,
			String clientDir, String clientFolder) throws IOException {
		Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(clientDir, clientFolder))
				.setApplicationName(APPLICATION_NAME).build();
		ValueRange response = service.spreadsheets().values().get(spreadsheetId, sheetName).execute();
		return readTimelyFilingLimitMappingSheetFull(response);
	}

	private static List<TimelyFilingLimitDto> readTimelyFilingLimitMappingSheetFull(ValueRange range) {

		List<List<Object>> values = range.getValues();
		List<TimelyFilingLimitDto> list = new ArrayList<>();
		TimelyFilingLimitDto dto = null;
		ListIterator li = values.listIterator();
		int ctr = 0;
		while (li.hasNext()) {
			ArrayList<String> obj = (ArrayList<String>) li.next();
			ctr++;
			if (ctr < 2)
				continue;
			try {
				int x = -1;
				dto = new TimelyFilingLimitDto(obj.get(++x),obj.get(++x), obj.get(++x));
				list.add(dto);

			} catch (Exception ex) {
				continue;
			}

		}
		logger.info("readTimelyFilingLimitMappingSheetFull");
		if (list != null) {
			logger.info("Size-->" + list.size());
		}
		return list;

	}

	public static List<ClaimFromSheet> readClaimsFromGSheet(String spreadsheetId, String sheetName, String clientDir,
			String clientFolder, String clientName, List<String> officeNames, List<String> officeNamesWithKey)
			throws IOException {
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
				/*
				 * dto = new ClaimFromSheet(obj.get(++x),
				 * obj.get(++x),obj.get(++x),obj.get(++x),obj.get(++x),obj.get(++x),obj.get(++x)
				 * ,obj.get(++x),obj.get(++x),
				 * obj.get(++x),obj.get(++x),obj.get(++x),obj.get(++x),obj.get(++x),obj.get(++x)
				 * ,obj.get(++x),obj.get(++x)
				 * ,obj.get(++x),obj.get(++x),obj.get(++x),obj.get(++x),obj.get(++x),obj.get(++x
				 * ),obj.get(++x),obj.get(++x)
				 * ,obj.get(++x),obj.get(++x),obj.get(++x),obj.get(++x),obj.get(++x),obj.get(++x
				 * ),obj.get(++x),obj.get(++x));
				 */
				dto = new ClaimFromSheet();
				try {
					dto.setClientName(obj.get(++x));//A
				} catch (Exception m) {
				}

				if (!clientName.equals(dto.getClientName()))
					continue;
				try {
					dto.setOfficeName(obj.get(++x));//B
				} catch (Exception m) {
				}

				if (officeNames != null && officeNames.size() > 0) {
					String g = dto.getOfficeName();
					String n = officeNames.stream().filter(xx -> g.equals(xx)).findAny().orElse(null);
					if (n == null)
						continue;
				}

				try {
					dto.setOfficeKey("0");//C
					dto.setOfficeKey(obj.get(++x));
				} catch (Exception m) {
				}
				if (officeNamesWithKey != null && officeNamesWithKey.size() > 0) {
					String g = dto.getOfficeName() + dto.getOfficeKey();
					String n = officeNamesWithKey.stream().filter(xx -> g.equals(xx)).findAny().orElse(null);
					if (n == null)
						continue;
				}
				try {
					dto.setClaimId(obj.get(++x));//D
				} catch (Exception m) {
					dto.setClaimId("");
				}
				try {
					dto.setAccountId(obj.get(++x));//E
				} catch (Exception m) {
					dto.setAccountId("");
				}
				try {
					dto.setPatientName(obj.get(++x));//F
				} catch (Exception m) {
					dto.setPatientName("");
				}
				try {
					dto.setPatientContactNo(obj.get(++x));//G
				} catch (Exception m) {
					dto.setPatientContactNo("");
				}
				try {
					dto.setPaitentDob(obj.get(++x));//H
				} catch (Exception m) {
					dto.setPaitentDob("");
				}
				try {
					dto.setDos(obj.get(++x));//I
				} catch (Exception m) {
					dto.setDos("");
				}
				try {
					dto.setPrimaryBilledAmount(obj.get(++x));//J
				} catch (Exception m) {
					dto.setPrimaryBilledAmount("");
				}
				try {
					dto.setClaimTypeP(obj.get(++x));//K
				} catch (Exception m) {
					dto.setClaimTypeP("");
				}
				try {
					dto.setPrimaryClaimStatus(obj.get(++x));//L
				} catch (Exception m) {
					dto.setPrimaryClaimStatus("");
				}
				
				try {
					dto.setTreatingProviderName(obj.get(++x));//M
				} catch (Exception m) {
					dto.setTreatingProviderName("");
				}
				try {
					dto.setProviderIdProviderName(obj.get(++x));//N
				} catch (Exception m) {
					dto.setProviderIdProviderName("");
				}
				try {
					dto.setPrimaryEstAmount(obj.get(++x));//O
				} catch (Exception m) {
					dto.setPrimaryEstAmount("");
				}
				try {
					dto.setPrimaryInsuranceCompany(obj.get(++x));//P
				} catch (Exception m) {
					dto.setPrimaryInsuranceCompany("");
				}
				
				try {
					dto.setPrimaryInsuranceContactNo(obj.get(++x));//Q
				} catch (Exception m) {
					dto.setPrimaryInsuranceContactNo("");
				}
				try {
					dto.setInsuranceName(obj.get(++x));//R
				} catch (Exception m) {
					dto.setInsuranceName("");
				}
				try {
					dto.setPrimaryMemberId(obj.get(++x));//S
				} catch (Exception m) {
					dto.setPrimaryMemberId("");
				}
				try {
					dto.setPrimaryInsuranceAddress(obj.get(++x));//T
				} catch (Exception m) {
					dto.setPrimaryInsuranceAddress("");
				}
				try {
					dto.setPrimaryGroupNumber(obj.get(++x));//U
				} catch (Exception m) {
					dto.setPrimaryGroupNumber("");
				}
				try {
					dto.setPrimaryPolicyHolderName(obj.get(++x));//V
				} catch (Exception m) {
					dto.setPrimaryPolicyHolderName("");
				}
				try {
					dto.setPrimaryPolicyHolderDob(obj.get(++x));//W
				} catch (Exception m) {
					dto.setPrimaryPolicyHolderDob("");
				}
				try {
					dto.setSecondaryBIlledAmount(obj.get(++x));//X
				} catch (Exception m) {
					dto.setSecondaryBIlledAmount("");
				}
				try {
					dto.setSecondaryClaimSubmissionDate(obj.get(++x));//Y
				} catch (Exception m) {
					dto.setSecondaryClaimSubmissionDate("");
				}
				try {
					dto.setPrimaryPaid(obj.get(++x));//Z
				} catch (Exception m) {
					dto.setPrimaryPaid("");
				}
				try {
					dto.setClaimTypeS(obj.get(++x));//AA
				} catch (Exception m) {
					dto.setClaimTypeS("");
				}
				try {
					dto.setSecondaryClaimStatus(obj.get(++x));//AB
				} catch (Exception m) {
					dto.setSecondaryClaimStatus("");
				}
				try {
					dto.setProviderIdReport(obj.get(++x));//AC
				} catch (Exception m) {
					dto.setProviderIdReport("");
				}
				try {
					dto.setSecondaryEstAmount(obj.get(++x));//AD
				} catch (Exception m) {
					dto.setSecondaryEstAmount("");
				}
				try {
					dto.setSecondaryInsuranceCompany(obj.get(++x));//AE
				} catch (Exception m) {
					dto.setSecondaryInsuranceCompany("");
				}
				try {
					dto.setSecondaryInsuranceContactNo(obj.get(++x));//AF
				} catch (Exception m) {
					dto.setSecondaryInsuranceContactNo("");
				}
				try {
					dto.setSecondaryInsuranceName(obj.get(++x));//AG
				} catch (Exception m) {
					dto.setSecondaryInsuranceName("");
				}
				try {
					dto.setSecondaryMemberId(obj.get(++x));//AH
				} catch (Exception m) {
					dto.setSecondaryMemberId("");
				}
				try {
					dto.setSecondaryInsuranceAddress(obj.get(++x));//AI
				} catch (Exception m) {
					dto.setSecondaryInsuranceAddress("");
				}
				try {
					dto.setSecondaryGroupNumber(obj.get(++x));//AJ
				} catch (Exception m) {
					dto.setSecondaryGroupNumber("");
				}
				try {
					dto.setSecondaryPolicyHolder(obj.get(++x));//AK
				} catch (Exception m) {
					dto.setSecondaryPolicyHolder("");
				}
				try {
					dto.setSecondaryPolicyHolderDob(obj.get(++x));//AL
				} catch (Exception m) {
					dto.setSecondaryPolicyHolderDob("");
				}
				
				
				try {
					
					dto.setServiceCodes(Arrays.asList(obj.get(++x).split(",")));//AM
					
				} catch (Exception m) {
					List<String> d=new ArrayList<>();
					dto.setServiceCodes(d);
				}
				
				try {
					
					dto.setToothAndSurfaces(Arrays.asList(obj.get(++x).split(",")));//AN
					
				} catch (Exception m) {
					List<String> tooth=new ArrayList<>();
					dto.setToothAndSurfaces(tooth);
				}

				list.add(dto);

			} catch (Exception ex) {
				ex.printStackTrace();
				continue;
			}

		}

		return list;
	}
	
	private static HttpRequestInitializer setTimeout(final HttpRequestInitializer initializer, final int timeout) {
	    return request -> {
	        initializer.initialize(request);
	        request.setReadTimeout(timeout);
	    };
	}
	
	

	/**
	 * Service Code based validations
	 * @param spreadsheetId
	 * @param sheetName
	 * @param clientDir
	 * @param clientFolder
	 * @return
	 * @throws IOException
	 */
	
	public static HashMap<String, List<ClaimServiceValidationGSheetData>> readServiceValidationFromGSheet(
			String spreadsheetId, String sheetName, String clientDir, String clientFolder) throws IOException {
		Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, setTimeout(getCredentials(clientDir, clientFolder),0))
				.setApplicationName(APPLICATION_NAME).build();
		ValueRange response = service.spreadsheets().values().get(spreadsheetId, sheetName).execute();
		List<List<Object>> values = response.getValues();

		HashMap<String, List<ClaimServiceValidationGSheetData>> map = new LinkedHashMap<>();
		ClaimServiceValidationGSheet dto = null;
		ListIterator li = values.listIterator();
		List cache = new ArrayList<>();
		int ctr = 0;
		// Store Service code in Map Key
		while (li.hasNext()) {
			ArrayList<String> obj = (ArrayList<String>) li.next();
			cache.add(obj);
			ctr++;
			int x = -1;
			if (ctr < 6)
				continue;
			else {

				map.put(obj.get(++x), new ArrayList<>());
			}
		}
		ctr = 0;
		for (Object n : cache) {
			int x = -1;// Ignore Service Code.
			ctr++;
			ArrayList<String> obj = (ArrayList<String>) n;
			if (ctr == 1 || ctr == 2 || ctr == 3 || ctr == 4 || ctr == 5) {// For Headings
				
				//ClaimServiceValidationGSheetData gs = new ClaimServiceValidationGSheetData();
				try {
				//	gs.setobj.get(l++);
				}catch(Exception p) {
					
				}
				
				
					int x1 = ++x;
					//if (x1 == 0)
					//	continue;
					for (int p=1;p<obj.size();p++) {
					for (Map.Entry<String, List<ClaimServiceValidationGSheetData>> entry : map.entrySet()) {
						String code = entry.getKey();
						//int l=0;
						
						List<ClaimServiceValidationGSheetData> sqL = entry.getValue();
						//if (sqL.size()==0) {
							//ClaimServiceValidationGSheet one = new ClaimServiceValidationGSheet();
						if (ctr ==1) {
							ClaimServiceValidationGSheetData gs = new ClaimServiceValidationGSheetData();
							gs.setInsuranceTypes(obj.get(p));
							//if (l==1)gs.setNameOfService(obj.get(p));
							//if (l==2)gs.setAutoOrManual(obj.get(p));
							//if (l==3)gs.setValues(obj.get(p));
							//if (l==4)gs.setDescription(obj.get(p));
							//List<ClaimServiceValidationGSheetData> qq = new ArrayList<>();
							//qq.add(gs);
							//one.setData(qq);
							sqL.add(gs);
						}else {
							ClaimServiceValidationGSheetData gs= sqL.get(p-1);
							if (ctr==2)gs.setNameOfService(obj.get(p));
							if (ctr==3)gs.setAutoOrManual(obj.get(p));
							if (ctr==4)gs.setValues(obj.get(p));
							if (ctr==5)gs.setDescription(obj.get(p));
						}
							// String head=obj.get(x1);
						//}
						/*else {
							ClaimServiceValidationGSheet old =sqL.get(l-1);
							List<ClaimServiceValidationGSheetData> qq =old.getData();
							ClaimServiceValidationGSheetData gs =qq.get(0);
							if (l==1)gs.setNameOfService(obj.get(p));
							if (l==2)gs.setAutoOrManual(obj.get(p));
							if (l==3)gs.setValues(obj.get(p));
							if (l==4)gs.setDescription(obj.get(p));
							
						    }*/
							//l++;
						 }//map
						/*ClaimServiceValidationGSheet one = new ClaimServiceValidationGSheet();
						ClaimServiceValidationGSheetData gs = new ClaimServiceValidationGSheetData();
						// String head=obj.get(x1);

						//gs.setHeading(obj11);
						List<ClaimServiceValidationGSheetData> qq = new ArrayList<>();
						qq.add(gs);
						one.setData(qq);
						sqL.add(one);*/
						//l++;
					}//obj
				

			/*} else if (ctr == 2) {// For Description

				try {

				} catch (Exception ex) {

				}*/

			} else {// other
					// for (String obj11 : obj) {
					// ++x;
				int x1 = ++x;
				// if (x1==0) continue;

				String code = obj.get(x1);
				List<ClaimServiceValidationGSheetData> sqL = map.get(code);
				// for (Map.Entry<String,List<ClaimServiceValidationGSheet>> entry :
				// map.entrySet()) {
				// String code= entry.getKey();

				// List<ClaimServiceValidationGSheet> sqL=entry.getValue();
				for (ClaimServiceValidationGSheetData sq : sqL) {

					//List<ClaimServiceValidationGSheetData> gss = sq.getData();
					//for (ClaimServiceValidationGSheetData gs : gss) {

						// for (String obj11 : obj) {
						sq.setValue(obj.get(++x1));
						// }
					//}
				}
				// }
				// }
			}

		}

		for (Map.Entry<String, List<ClaimServiceValidationGSheetData>> entry : map.entrySet()) {
			logger.info("Key = " + entry.getKey() + ", Value = " + entry.getValue());
			//for (ClaimServiceValidationGSheet d : entry.getValue()) {
				//for (ClaimServiceValidationGSheetData qq : d.getData()) {
				//	logger.info("HEAD: " + qq.getHeading() + "   VALUE: " + qq.getValue());
				//}
			//}
			logger.info("");

		}

		return map;
	}

	public static HashMap<String, String> readProviderScheduleGSheet(String spreadsheetId, String sheetName,
			String clientDir, String clientFolder) throws IOException {
		Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(clientDir, clientFolder))
				.setApplicationName(APPLICATION_NAME).build();
		ValueRange response = service.spreadsheets().values().get(spreadsheetId, sheetName).execute();
		List<List<Object>> values = response.getValues();

		HashMap<String, Object[]> map = new LinkedHashMap<>();
		HashMap<String, String> map1 = new LinkedHashMap<>();
		// ClaimServiceValidationGSheet dto = null;
		ListIterator li = values.listIterator();
		// List cache = new ArrayList<>();
		int ctr = 0;
		// Store Office name as Map Key
		while (li.hasNext()) {
			ArrayList<String> obj = (ArrayList<String>) li.next();

			ctr++;

			if (ctr == 1) {
				int x = 1;
				for (;;) {
					try {
						String off = obj.get(++x);
						if (!off.equals(""))
							map.put(off, null);
					} catch (Exception d) {
						break;
					}
				}
			} else if (ctr == 2) {
				int x = 1;
				for (;;) {
					try {
						String doc = obj.get(++x);
						int ind = x;
						if (doc.equals("Doc - 1")) {
							for (Map.Entry<String, Object[]> entry : map.entrySet()) {
								String k = entry.getKey();
								if (entry.getValue() == null) {
									Object[] ar = new Object[3];
									ar[0] = ind;
									map.put(k, ar);
									break;
								}
							}

						}
					} catch (Exception d) {
						break;
					}
				}

			} else {

				// cache.add(obj);

				for (Map.Entry<String, Object[]> entry : map.entrySet()) {
					String k = entry.getKey();
					if (entry.getValue() != null) {
						Object[] d = entry.getValue();
						d[1] = obj.get((int) d[0]);

						map1.put(k + "->" + obj.get(0), d[1].toString());
						if (k.equals("Jasper")) {
							System.out.println(k + "->" + obj.get(0) + "-->" + d[1]);
						}
					}
				}

			}

		}

		for (Map.Entry<String, String> entry : map1.entrySet()) {
			logger.info("Key = " + entry.getKey() + ", Value = " + entry.getValue());
			// System.out.println();
			logger.info("");

		}

		return map1;
	}
	
	public static List<ProivderHelpingSheetDto> readProviderScheduleHelpingGSheet(String spreadsheetId, String sheetName,
			String clientDir, String clientFolder) throws IOException {
		Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(clientDir, clientFolder))
				.setApplicationName(APPLICATION_NAME).build();
		ValueRange response = service.spreadsheets().values().get(spreadsheetId, sheetName).execute();
		List<List<Object>> values = response.getValues();

		List<ProivderHelpingSheetDto> l= new ArrayList<>();
		ProivderHelpingSheetDto dto = null;
		ListIterator li = values.listIterator();
		// List cache = new ArrayList<>();
		//int ctr = 0;
		
		
		while (li.hasNext()) {
			ArrayList<String> obj = (ArrayList<String>) li.next();
			int x=-1;
			//ctr++;
			try {
			dto= new ProivderHelpingSheetDto(obj.get(++x),obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x));
			l.add(dto);
			}catch(Exception n) {
				n.printStackTrace();
				//break;
			}

		}


		return l;
	}

	/**
	 * 
	 * @param spreadsheetId
	 * @param sheetName
	 * @param clientDir
	 * @param clientFolder
	 * @return Object[] with 2 List form the Sheet
	 * @throws IOException
	 */
	public static Object[] readProviderGSheet(String spreadsheetId, String sheetName, String clientDir,
			String clientFolder) throws IOException {
		Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(clientDir, clientFolder))
				.setApplicationName(APPLICATION_NAME).build();
		ValueRange response = service.spreadsheets().values().get(spreadsheetId, sheetName).execute();
		List<List<Object>> values = response.getValues();

		ListIterator li = values.listIterator();
		Object[] objArray = new Object[2];
		List<ProviderCodeWithSpecialty> listSpe = new ArrayList<>();
		List<ProviderCodeWithOffice> listOff = new ArrayList<>();
		ProviderCodeWithSpecialty spe = null;
		ProviderCodeWithOffice off = null;

		// List cache = new ArrayList<>();
		int ctr = 0;
		// Store Office name as Map Key
		while (li.hasNext()) {
			ArrayList<String> obj = (ArrayList<String>) li.next();
			int x = -1;
			ctr++;

			if (ctr == 1) {

			} else {

				spe = new ProviderCodeWithSpecialty();
				try {
					spe.setClientName(obj.get(++x));

				} catch (Exception e) {
					// TODO: handle exception
				}
				try {
					spe.setProviderNames(obj.get(++x));

				} catch (Exception e) {
					// TODO: handle exception
				}
				try {
					spe.setProviderCode(obj.get(++x));

				} catch (Exception e) {
					// TODO: handle exception
				}
				try {
					spe.setStatus(obj.get(++x));

				} catch (Exception e) {
					// TODO: handle exception
				}
				try {
					spe.setSpecialty(obj.get(++x));

				} catch (Exception e) {
					// TODO: handle exception
				}

				++x;// E is blank
				off = new ProviderCodeWithOffice();

				try {
					off.setClientName(obj.get(++x));

				} catch (Exception e) {
					// TODO: handle exception
				}
				try {
					off.setProviderCode(obj.get(++x));

				} catch (Exception e) {
					// TODO: handle exception
				}
				try {
					off.setOffice(obj.get(++x));

				} catch (Exception e) {
					// TODO: handle exception
				}
				try {
					off.setEsCode(obj.get(++x));

				} catch (Exception e) {
					// TODO: handle exception
				}

				listSpe.add(spe);
				listOff.add(off);
			}

		}

		objArray[0] = listSpe;
		objArray[1] = listOff;

		return objArray;
	}

	public static List<CredentialData> readCredentialGSheet(String spreadsheetId, String sheetName, String clientDir,
			String clientFolder) throws IOException {
		logger.info("readCredentialGSheet: spreadsheetId={} tab={}", spreadsheetId, sheetName);
		Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(clientDir, clientFolder))
				.setApplicationName(APPLICATION_NAME).build();

		ValueRange range;
		try {
			range = service.spreadsheets().values().get(spreadsheetId, sheetName).execute();
		} catch (IOException e) {
			logger.error(
					"readCredentialGSheet: Google Sheets API read failed (check tab name, sharing for service account, and API errors) spreadsheetId={} tab={}",
					spreadsheetId, sheetName, e);
			throw e;
		} catch (RuntimeException e) {
			logger.error(
					"readCredentialGSheet: unexpected error during Sheets API execute spreadsheetId={} tab={}",
					spreadsheetId, sheetName, e);
			throw new IOException("Credential sheet read failed: " + e.getClass().getSimpleName(), e);
		}

		List<List<Object>> values = range.getValues();
		if (values == null || values.isEmpty()) {
			String msg = String.format(
					"Credential tracker sheet returned no rows (empty sheet, wrong tab name, or restricted range) spreadsheetId=%s tab=%s",
					spreadsheetId, sheetName);
			logger.warn("readCredentialGSheet: {}", msg);
			throw new IOException(msg);
		}

		List<CredentialData> list = new ArrayList<>();
		ListIterator li = values.listIterator();
		CredentialData dto = null;
		// IVFHistorySheet vifH = null;

		int ctr = 0;
		while (li.hasNext()) {
			ArrayList<String> obj = (ArrayList<String>) li.next();
			ctr++;
			if (ctr ==1 || ctr ==2)
				continue;
			try {
				int x = -1;
					dto = new CredentialData(obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
						obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
						obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
						obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
						obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
						obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
						obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
						obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
						obj.get(++x),obj.get(++x));
				        list.add(dto);
			} catch (Exception ex) {
				continue;
			}

		}

		// fulldto.setDataList(list);
		// fulldto.setStatusCount(statusCount);
		return list;

	}
	
	public static List<CredentialDataAnesthesia> readCredentialTrackerAnesthesiaGSheet(String spreadsheetId, String sheetName, String clientDir,
			String clientFolder) throws IOException {
		Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(clientDir, clientFolder))
				.setApplicationName(APPLICATION_NAME).build();

		ValueRange range = service.spreadsheets().values().get(spreadsheetId, sheetName).execute();

		List<List<Object>> values = range.getValues();

		List<CredentialDataAnesthesia> list = new ArrayList<>();
		ListIterator li = values.listIterator();
		CredentialDataAnesthesia dto = null;
		// IVFHistorySheet vifH = null;

		int ctr = 0;
		while (li.hasNext()) {
			ArrayList<String> obj = (ArrayList<String>) li.next();
			ctr++;
			if (ctr ==1)
				continue;
			try {
				int x = -1;
				dto = new CredentialDataAnesthesia();
					/*dto = new CredentialDataAnesthesia(obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
						obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
						obj.get(++x), obj.get(++x));
                     */
					try {
    					dto.setSrNo(obj.get(++x));//A
					} catch (Exception e) { dto.setSrNo(""); }

					try {
    					dto.setProviders(obj.get(++x));//B
					} catch (Exception e) { dto.setProviders(""); }

					try {
    					dto.setProviderCodes(obj.get(++x));//C
					} catch (Exception e) { dto.setProviderCodes(""); }

					try {
    					dto.setProvideStatus(obj.get(++x));//D
					} catch (Exception e) { dto.setProvideStatus(""); }

					try {
					    dto.setD9230Nirtrous(obj.get(++x));//E
					} catch (Exception e) { dto.setD9230Nirtrous(""); }

					try {
 					   dto.setD9248Anesthesia(obj.get(++x));//F
					} catch (Exception e) { dto.setD9248Anesthesia(""); }

					try {
					    dto.setD9245Sedation(obj.get(++x));//G - NEW COLUMN
					} catch (Exception e) { dto.setD9245Sedation(""); }

					try {
 					   dto.setEmpty(obj.get(++x));//H - empty
					} catch (Exception e) { dto.setEmpty(""); }

					try {
					    dto.setFirstHomeD0145(obj.get(++x));//I - was H
					} catch (Exception e) { dto.setFirstHomeD0145(""); }

					try {
					    dto.setFDHEffectiveDate(obj.get(++x));//J - was I
					} catch (Exception e) { dto.setFDHEffectiveDate(""); }

					try {
					    dto.setUpdatedWithMCNA(obj.get(++x));//K - was J
					} catch (Exception e) { dto.setUpdatedWithMCNA(""); }

					try {
					    dto.setUpdatedWithDQ(obj.get(++x));//L - was K
					} catch (Exception e) { dto.setUpdatedWithDQ(""); }

					try {
 					   dto.setUpdatedWithUHC(obj.get(++x));//M - was L
					} catch (Exception e) { dto.setUpdatedWithUHC(""); }

					try {
 					   dto.setRemark(obj.get(++x));//N - was M
					} catch (Exception e) { dto.setRemark(""); }
					
				list.add(dto);
			} catch (Exception ex) {
				continue;
			}

		}

		// fulldto.setDataList(list);
		// fulldto.setStatusCount(statusCount);
		return list;

	}

}
