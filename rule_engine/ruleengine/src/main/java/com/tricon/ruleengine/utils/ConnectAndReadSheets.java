package com.tricon.ruleengine.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import org.springframework.beans.factory.annotation.Value;
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
import com.tricon.ruleengine.model.sheet.EagleSoftCB;
import com.tricon.ruleengine.model.sheet.EagleSoftFSName;
import com.tricon.ruleengine.model.sheet.EagleSoftFeeShedule;
import com.tricon.ruleengine.model.sheet.IVFTableSheet;
import com.tricon.ruleengine.model.sheet.MappingTableCodeMaster;
import com.tricon.ruleengine.model.sheet.MappingTableFeeSN;
import com.tricon.ruleengine.model.sheet.TreatmentPlan;
import com.tricon.ruleengine.model.sheet.TreatmentPlanDetails;
import com.tricon.ruleengine.model.sheet.TreatmentPlanPatient;

@Configuration
public class ConnectAndReadSheets {

	
	 static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
	   
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	
	private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
	
	private static NetHttpTransport HTTP_TRANSPORT=null;
	 
	 static {
		try {
			HTTP_TRANSPORT= GoogleNetHttpTransport.newTrustedTransport();
		} catch (GeneralSecurityException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
	}
	 
	public static Credential getCredentials(String clientDir,String clientFolder) throws IOException {
		// Load client secrets.
		System.out.println(clientDir);
		System.out.println("9999999999999999999999999999999");
		
		File initialFile = new File(clientDir);
		InputStream targetStream = new FileInputStream(initialFile);
		// InputStream in =
		// GoogleFileController.class.getResourceAsStream(CLIENT_SECRET_DIR);
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(targetStream));

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, SCOPES)
						.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(clientFolder)))
						.setAccessType("offline").build();
		return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
	}
	/**
	 * 
	 * @param spreadsheetId
	 * @param sheetName
	 * @param id Can be null
	 * @param sheetType
	 * @param clientDir
	 * @param clientFolder
	 * @return
	 * @throws IOException
	 */
	public static List<Object> readSheet(String spreadsheetId,String sheetName, String id,String sheetType,String clientDir,String clientFolder ) throws IOException {
		Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY,getCredentials(clientDir,clientFolder) )
				.setApplicationName(APPLICATION_NAME).build();
		System.out.println("ididid-"+id);
		ValueRange response = service.spreadsheets().values().get(spreadsheetId, sheetName).execute();
		if (sheetType.equals(Constants.SHEET_TYPE_TP)) return  readTPSheetData(response, id);
		if (sheetType.equals(Constants.SHEET_TYPE_IVF_DATA)) return  readIVFSheet(response, id);
		if (sheetType.equals(Constants.SHEET_TYPE_MappingTable_CM)) return  readMappingDataCM(response);
		if (sheetType.equals(Constants.SHEET_TYPE_MappingTable_FEESN)) return  readMappingDataFEE(response);
		if (sheetType.equals(Constants.SHEET_TYPE_FS_FEE)) return  readEagleSoftFSFee(response);
		if (sheetType.equals(Constants.SHEET_TYPE_ES_COVERAGE)) return  readEagleSoftESCoverage(response);
		if (sheetType.equals(Constants.SHEET_TYPE_FS_NAME)) return  readEagleSoftFSName(response);
       return  null;
	}
	
	public static List<Object> readTPSheetData(ValueRange range,String treatmentPlanId) {
		
		List<List<Object>> values = range.getValues();
		ListIterator li = values.listIterator(values.size());
		TreatmentPlan tp =null;
		List<Object> tpList=null;
        while(li.hasPrevious()) {
        	ArrayList<String> obj=(ArrayList<String>) li.previous();
        	if (obj.get(0).toLowerCase().startsWith("unique_id")) break;
        	System.out.println(obj);
        	if (!obj.get(1).equals(treatmentPlanId)) continue;
        	System.out.println("obj.get(8)--"+obj.get(8));
            TreatmentPlanDetails tpd=new TreatmentPlanDetails(obj.get(8), obj.get(12));
            TreatmentPlanPatient tpdp=new TreatmentPlanPatient(obj.get(2),obj.get(3), obj.get(4) );
            tp=new TreatmentPlan(obj.get(0), obj.get(1), tpdp, tpd, obj.get(5), obj.get(6), obj.get(7), obj.get(9), obj.get(10), obj.get(11),
            		obj.get(13), obj.get(14), obj.get(15));
            
            if (tpList ==null) tpList= new ArrayList<>();
            tpList.add(tp);            

        }
		
		return tpList;
		
	}

	public static List<Object> readIVFSheet(ValueRange range,String uniqueId) {
		
		List<List<Object>> values = range.getValues();
		ListIterator li = values.listIterator(values.size());
		IVFTableSheet vif =null;
		List<Object> ivList=null;
        while(li.hasPrevious()) {
        	ArrayList<String> obj=(ArrayList<String>) li.previous();
        	System.out.println(obj);
        	System.out.println(obj.size());;
            	System.out.println(obj.get(151));
            	System.out.println("uniqueIduniqueId::"+uniqueId);
        	if (obj.get(151).toLowerCase().startsWith("unique_id")) break;
        	if (!obj.get(151).equals(uniqueId)) continue;
        	System.out.println(obj.get(151));
        	System.out.println(obj.get(0));
        	int x=-1;
        	vif=new IVFTableSheet(obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x)
        		,obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x)
        		,obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x)
        		,obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x)
        		,obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x)
        		,obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x)
        		,obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x)
        		,obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x)
        		,obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x)
        		,obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x)
        		,obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x)
        		,obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x)
        		,obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x)
        		,obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x)
        		,obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x)
        		,obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x)
        		,obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x)
        		,obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x)
        		,obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x)
        		,obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x)
        		,obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x)
        		,obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x)
        		,obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x)
        		,obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x)
        		,obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x)
        		,obj.get(++x), obj.get(++x)
        		);            
        	System.out.println("xxxxxxxx:"+x);
            if (ivList ==null) ivList= new ArrayList<>();
        	ivList.add(vif);            
            break;//Because only one row will be there
        }
		
		return ivList;
		
	}
	
	public static List<Object> readMappingDataFEE(ValueRange range) {
		
		List<List<Object>> values = range.getValues();
		ListIterator li = values.listIterator(values.size());
		MappingTableFeeSN vif =null;
		List<Object> ivList=null;
        while(li.hasPrevious()) {
        	ArrayList<String> obj=(ArrayList<String>) li.previous();
        	int x=0;
        	vif=new MappingTableFeeSN(obj.get(++x), obj.get(++x));            
        	System.out.println("xxxxxxxx:"+x);
            if (ivList ==null) ivList= new ArrayList<>();
        	ivList.add(vif);            
        }
		
		return ivList;
		
	}

	public static List<Object> readMappingDataCM(ValueRange range) {
		
		List<List<Object>> values = range.getValues();
		ListIterator li = values.listIterator(values.size());
		MappingTableCodeMaster vif =null;
		List<Object> ivList=null;
        while(li.hasPrevious()) {
        	ArrayList<String> obj=(ArrayList<String>) li.previous();
        	int x=0;
        	vif=new MappingTableCodeMaster(obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x)
        		,obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x)
        		);            
        	System.out.println("xxxxxxxx:"+x);
            if (ivList ==null) ivList= new ArrayList<>();
        	ivList.add(vif);            
        }
		
		return ivList;
		
	}
	public static List<Object> readEagleSoftFSFee(ValueRange range) {
		List<List<Object>> values = range.getValues();
		ListIterator li = values.listIterator(values.size());
		EagleSoftFeeShedule vif =null;
		List<Object> ivList=null;
        while(li.hasPrevious()) {
        	ArrayList<String> obj=(ArrayList<String>) li.previous();
        	int x=-1;
        	vif=new EagleSoftFeeShedule(obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x)
        		);            
        	System.out.println("xxxxxxxx:"+x);
            if (ivList ==null) ivList= new ArrayList<>();
        	ivList.add(vif);            
        }
		
		return ivList;
		
	}
	public static List<Object> readEagleSoftESCoverage(ValueRange range) {
		List<List<Object>> values = range.getValues();
		ListIterator li = values.listIterator(values.size());
		EagleSoftCB vif =null;
		List<Object> ivList=null;
        while(li.hasPrevious()) {
        	ArrayList<String> obj=(ArrayList<String>) li.previous();
        	int x=-1;
        	vif=new EagleSoftCB(obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x)
        		,obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x)
        		);            
        	System.out.println("xxxxxxxx:"+x);
            if (ivList ==null) ivList= new ArrayList<>();
        	ivList.add(vif);            
        }
        return ivList;
		
	}
	public static List<Object> readEagleSoftFSName(ValueRange range) {
		List<List<Object>> values = range.getValues();
		ListIterator li = values.listIterator(values.size());
		EagleSoftFSName vif =null;
		List<Object> ivList=null;
        while(li.hasPrevious()) {
        	ArrayList<String> obj=(ArrayList<String>) li.previous();
        	int x=-1;
        	vif=new EagleSoftFSName(obj.get(++x), obj.get(++x)
        		);            
        	System.out.println("xxxxxxxx:"+x);
            if (ivList ==null) ivList= new ArrayList<>();
        	ivList.add(vif);            
        }
        return ivList;
	}
	
}
