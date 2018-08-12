package com.tricon.ruleengine.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.google.common.collect.Collections2;
import com.tricon.ruleengine.dto.TreatmentPlanValidationDto;
import com.tricon.ruleengine.model.sheet.IVFTableSheet;

@Configuration
public class ConnectAndReadSheets {

	static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";

	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);

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

	/**
	 * 
	 * @param spreadsheetId
	 * @param sheetName
	 * @param id
	 *            Can be null
	 * @param sheetType
	 * @param clientDir
	 * @param clientFolder
	 * @return
	 * @throws IOException
	 */
	public static Map<String, List<Object>> readSheet(String spreadsheetId, String sheetName, String[] id,
			String clientDir, String clientFolder, String officeName) throws IOException {
		Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(clientDir, clientFolder))
				.setApplicationName(APPLICATION_NAME).build();
		ValueRange response = service.spreadsheets().values().get(spreadsheetId, sheetName).execute();
		// if (sheetType==Constants.treatmentPlanSheetID) return
		// readTPSheetData(response, id);
		// if (sheetType==Constants.ivTableDataSheetID)
		return readIVFSheet(response, id, officeName);
		// if (sheetType==Constants.mappingSheetID_CM) return
		// readMappingDataCM(response);
		// if (sheetType==Constants.mappingSheetID_FEE) return
		// readMappingDataFEE(response);
		// if (sheetType==Constants.eagleSoftFSANDFEESheetID) return
		// readEagleSoftFSFee(response);
		// if (sheetType==Constants.eagleSoftCoverageSheetID) return
		// readEagleSoftESCoverage(response);
		// if (sheetType==Constants.eagleSoftFSNAMESheetID) return
		// readEagleSoftFSName(response);
		// if (sheetType==Constants.eagleSoftRemDedBalSheetID) return
		// readEagleSoftRemDedMax(response);
		// return null;
	}

	/*
	 * Not used Now public static List<Object> readTPSheetData(ValueRange
	 * range,String treatmentPlanId) {
	 * 
	 * List<List<Object>> values = range.getValues(); ListIterator li =
	 * values.listIterator(values.size()); TreatmentPlan tp =null; List<Object>
	 * tpList=null; while(li.hasPrevious()) { ArrayList<String>
	 * obj=(ArrayList<String>) li.previous(); if
	 * (obj.get(0).toLowerCase().startsWith("unique_id")) break;
	 * //System.out.println(obj); if (!obj.get(1).equals(treatmentPlanId)) continue;
	 * //System.out.println("obj.get(8)--"+obj.get(8)); TreatmentPlanDetails tpd=new
	 * TreatmentPlanDetails(obj.get(8), obj.get(12)); TreatmentPlanPatient tpdp=new
	 * TreatmentPlanPatient(obj.get(2),obj.get(3), obj.get(4) ); tp=new
	 * TreatmentPlan(obj.get(0), obj.get(1), tpdp, tpd, obj.get(5), obj.get(6),
	 * obj.get(7), obj.get(9), obj.get(10), obj.get(11), obj.get(13), obj.get(14),
	 * obj.get(15));
	 * 
	 * if (tpList ==null) tpList= new ArrayList<>(); tpList.add(tp);
	 * 
	 * }
	 * 
	 * return tpList;
	 * 
	 * }
	 */
	public static Map<String, List<Object>> readIVFSheet(ValueRange range, String[] uniqueIds, String officeName) {
		System.out.println("KKKKKKKKKKKKKKKK");

		List<List<Object>> values = range.getValues();
		Map<String, List<Object>> map = null;
		List<String> ivds = Arrays.asList(uniqueIds);
		ListIterator li = values.listIterator(values.size());
		IVFTableSheet vif = null;
		List<Object> ivList = null;
		// int maxlength= values.size();
		// int maxlengthT= values.size();
		// System.out.println("maxlengthT30::"+maxlengthT);

		while (li.hasPrevious()) {
			ArrayList<String> obj = (ArrayList<String>) li.previous();
			System.out.println(obj.size());
			System.out.println(obj);
			String uniqueId = "";
			// System.out.println("maxlengthT"+maxlengthT);
			// for(String uniqueId:uniqueIds) {
			try {
				if (obj.get(156).toLowerCase().startsWith("Unique_ID"))
					break;
				System.out.println("id---" + ivds.get(0));
				System.out.println("id---" + officeName + "_" + ivds.get(0));
				System.out.println("888888:;" + (obj.get(156)));
				Collection<String> ruleGen = Collections2.filter(ivds,
						id -> (officeName + "_" + id).equals(obj.get(156)));
				if (ruleGen != null && ruleGen.size() > 0) {
					//uniqueId = ruleGen.get(0);//obj.get(156);
                    for(String i:ruleGen) {
                    	uniqueId=i;	
                    }
					System.out.println("ADDEDDDDD");
					// if (!obj.get(156).equals(uniqueId)) continue;
					int x = -1;
					vif = new IVFTableSheet(obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
							obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
							obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
							obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
							obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
							obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
							obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
							obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
							obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
							obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
							obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
							obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
							obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
							obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
							obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
							obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
							obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
							obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
							obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
							obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
							obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
							obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
							obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
							obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
							obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
							obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
							obj.get(++x), obj.get(156));
				}else {
					continue;
				}
			} catch (Exception ex) {
				continue;
			}
			System.out.println(uniqueId);

			if (map == null)
				map = new HashMap<>();

			if (map.containsKey(uniqueId)) {
				// if the key has already been used,
				// we'll just grab the array list and add the value to it
				ivList = map.get(uniqueId);
				ivList.add(vif);
			} else {
				// if the key hasn't been used yet,
				// we'll create a new ArrayList<String> object, add the value
				// and put it in the array list with the new key
				ivList = new ArrayList<>();
				ivList.add(vif);
				map.put(uniqueId, ivList);
			}

			// if (ivList ==null) ivList= new ArrayList<>();
			// ivList.add(vif);
			if (map != null && uniqueIds.length == map.size())
				break;// Because
			// }//For loop
		} // While Loop - 1

		return map;

	}

	/*
	 * public static List<Object> readMappingDataFEE(ValueRange range) {
	 * 
	 * List<List<Object>> values = range.getValues(); ListIterator li =
	 * values.listIterator(values.size()); MappingTableFeeSN vif =null; List<Object>
	 * ivList=null; while(li.hasPrevious()) { ArrayList<String>
	 * obj=(ArrayList<String>) li.previous(); int x=0; vif=new
	 * MappingTableFeeSN(obj.get(++x), obj.get(++x)); if (ivList ==null) ivList= new
	 * ArrayList<>(); ivList.add(vif); }
	 * 
	 * return ivList;
	 * 
	 * }
	 * 
	 * public static List<Object> readMappingDataCM(ValueRange range) {
	 * 
	 * List<List<Object>> values = range.getValues(); ListIterator li =
	 * values.listIterator(values.size()); MappingTableCodeMaster vif =null;
	 * List<Object> ivList=null; while(li.hasPrevious()) { ArrayList<String>
	 * obj=(ArrayList<String>) li.previous(); int x=0; vif=new
	 * MappingTableCodeMaster(obj.get(++x), obj.get(++x), obj.get(++x),
	 * obj.get(++x), obj.get(++x), obj.get(++x) ,obj.get(++x), obj.get(++x),
	 * obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x) ); if (ivList ==null)
	 * ivList= new ArrayList<>(); ivList.add(vif); }
	 * 
	 * return ivList;
	 * 
	 * } public static List<Object> readEagleSoftFSFee(ValueRange range) {
	 * List<List<Object>> values = range.getValues(); ListIterator li =
	 * values.listIterator(values.size()); EagleSoftFeeShedule vif =null;
	 * List<Object> ivList=null; while(li.hasPrevious()) { ArrayList<String>
	 * obj=(ArrayList<String>) li.previous(); int x=-1; vif=new
	 * EagleSoftFeeShedule(obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x) );
	 * if (ivList ==null) ivList= new ArrayList<>(); ivList.add(vif); }
	 * 
	 * return ivList;
	 * 
	 * } public static List<Object> readEagleSoftESCoverage(ValueRange range) {
	 * List<List<Object>> values = range.getValues(); ListIterator li =
	 * values.listIterator(values.size()); EagleSoftCB vif =null; List<Object>
	 * ivList=null; while(li.hasPrevious()) { ArrayList<String>
	 * obj=(ArrayList<String>) li.previous(); int x=-1; vif=new
	 * EagleSoftCB(obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
	 * obj.get(++x), obj.get(++x) ,obj.get(++x), obj.get(++x), obj.get(++x),
	 * obj.get(++x), obj.get(++x) ); if (ivList ==null) ivList= new ArrayList<>();
	 * ivList.add(vif); } return ivList;
	 * 
	 * } public static List<Object> readEagleSoftFSName(ValueRange range) {
	 * List<List<Object>> values = range.getValues(); ListIterator li =
	 * values.listIterator(values.size()); EagleSoftFSName vif =null; List<Object>
	 * ivList=null; while(li.hasPrevious()) { ArrayList<String>
	 * obj=(ArrayList<String>) li.previous(); int x=-1; vif=new
	 * EagleSoftFSName(obj.get(++x), obj.get(++x) ); if (ivList ==null) ivList= new
	 * ArrayList<>(); ivList.add(vif); } return ivList; } public static List<Object>
	 * readEagleSoftRemDedMax(ValueRange range) {
	 * 
	 * List<List<Object>> values = range.getValues(); ListIterator li =
	 * values.listIterator(values.size()); EagleSoftRemBalDedMax vif =null;
	 * List<Object> ivList=null; while(li.hasPrevious()) { ArrayList<String>
	 * obj=(ArrayList<String>) li.previous(); System.out.println(obj); int x=-1;
	 * String m1="0"; String m2="0";
	 * 
	 * if (obj.size()==10) m1=obj.get(9); if (obj.size()==9) m2=obj.get(8); vif=new
	 * EagleSoftRemBalDedMax(obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
	 * obj.get(++x), obj.get(++x) ,obj.get(++x), obj.get(++x), obj.get(++x), m2, m1
	 * ); if (ivList ==null) ivList= new ArrayList<>(); ivList.add(vif); }
	 * 
	 * return ivList;
	 * 
	 * }
	 */
}
