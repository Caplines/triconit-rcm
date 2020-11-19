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
import com.google.api.services.sheets.v4.model.AppendDimensionRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetResponse;
import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.ExtendedValue;
import com.google.api.services.sheets.v4.model.GridCoordinate;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.RowData;
import com.google.api.services.sheets.v4.model.UpdateCellsRequest;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.common.collect.Collections2;
import com.tricon.ruleengine.dto.CaplineIVFFormDto;
import com.tricon.ruleengine.dto.ExceptionDataDto;
import com.tricon.ruleengine.dto.ToothHistoryDto;
import com.tricon.ruleengine.dto.scrapping.EligibilityDto;
import com.tricon.ruleengine.dto.scrapping.FullWebsiteScrapDto;
import com.tricon.ruleengine.dto.scrapping.HistoryDto;
import com.tricon.ruleengine.dto.scrapping.RosterDetails;
import com.tricon.ruleengine.model.db.PatientTemp;
import com.tricon.ruleengine.model.sheet.FullWebsiteDataParsingSheet;
import com.tricon.ruleengine.model.sheet.IVFHistorySheet;
import com.tricon.ruleengine.model.sheet.IVFTableSheet;
import com.tricon.ruleengine.model.sheet.MCNADentaSheet;

@Configuration
public class ConnectAndReadSheets {

	static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";

	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);

	private static NetHttpTransport HTTP_TRANSPORT = null;
	
	
	final static int Column_NO_UNIQUE = 312;
	final static int Column_NO_PATIENT = 129;


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
			String clientDir, String clientFolder, String officeName, boolean idsPatient,boolean breakLoop) throws IOException {
		Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(clientDir, clientFolder))
				.setApplicationName(APPLICATION_NAME).build();
		ValueRange response = service.spreadsheets().values().get(spreadsheetId, sheetName).execute();
		// if (sheetType==Constants.treatmentPlanSheetID) return
		// readTPSheetData(response, id);
		// if (sheetType==Constants.ivTableDataSheetID)
		if (id!=null)
		return readIVFSheet(response, id, officeName, idsPatient, breakLoop);
		else return readIVFWholeSheet(response, officeName);
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

	//ONly for Dumping Data
	public static Map<String, List<Object>> readSheetNewDump(String spreadsheetId, String sheetName, String[] id,
			String clientDir, String clientFolder, String officeName, boolean idsPatient,boolean breakLoop) throws IOException {
		Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(clientDir, clientFolder))
				.setApplicationName(APPLICATION_NAME).build();
		ValueRange response = service.spreadsheets().values().get(spreadsheetId, sheetName).execute();
		return readIVFWholeSheetWithNewDump(response, officeName);
	}

	public static void updateSheetRoster(String spreadsheetId, String sheetSubID, String clientDir, String clientFolder,
			List<RosterDetails> rList, int rowCount,int initRow,String status) throws IOException {
		Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(clientDir, clientFolder))
				.setApplicationName(APPLICATION_NAME).build();

		List<Request> requests = new ArrayList<>();
		if (rList != null) {
			//int x=1;	
			for(RosterDetails rd:rList) {
				List<CellData> values = new ArrayList<>();
				values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(rd.getPatFName())));
				values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(rd.getSubscriberId())));
				values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(rd.getAddress1()+" "+rd.getAddress2()+" "+rd.getCity()) ));
				//values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(rd.getAddress2())));
				//values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(rd.getCity())));
				values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(rd.getDob())));
				values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(rd.getTelephone())));
				//values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(rd.getAssignedDentistF())));
				requests.add(new Request()
						.setUpdateCells(new UpdateCellsRequest().setStart(new GridCoordinate().setSheetId(Integer.parseInt(sheetSubID)).setRowIndex(initRow))
								// .setColumnIndex(3))
								.setRows(Arrays.asList(new RowData().setValues(values)))
								.setFields("userEnteredValue,userEnteredFormat.backgroundColor")));
				initRow++;
			}
				
			
			List<CellData> values = new ArrayList<>();
			values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(status)));
			requests.add(new Request()
					.setUpdateCells(new UpdateCellsRequest().setStart(new GridCoordinate().setSheetId(Integer.parseInt(sheetSubID)).setRowIndex(ConstantsScrapping.MCNA_ROSTER_ROW_INDEX_STATUS)//)
							 .setColumnIndex(ConstantsScrapping.MCNA_ROSTER_COLUMN_INDEX_STATUS))
							.setRows(Arrays.asList(new RowData().setValues(values)))
							.setFields("userEnteredValue,userEnteredFormat.backgroundColor")));

	        
		}
        
		BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);
		service.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute();
	}

	public static void updateDumpSheet(String spreadsheetId, String sheetSubID, String clientDir, String clientFolder,
			Map<String, List<Object>> ivfMap) throws IOException {
		Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(clientDir, clientFolder))
				.setApplicationName(APPLICATION_NAME).build();

		List<Request> requests = new ArrayList<>();
		IVFTableSheet sh = null;
		try {
		if (ivfMap!=null) {
		for (Map.Entry<String, List<Object>> entry : ivfMap.entrySet()) {

			//String key = entry.getKey();
			List<Object> obL = entry.getValue();
			for (Object obj : obL) {
				sh = (IVFTableSheet) obj;
				List<CellData> values = new ArrayList<>();
				values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(sh.getStatusDump())));
				requests.add(new Request()
						.setUpdateCells(new UpdateCellsRequest().setStart(new GridCoordinate().setSheetId(Integer.parseInt(sheetSubID)).setRowIndex(sh.getRowCounter()+1)
								.setColumnIndex(347))
								.setRows(Arrays.asList(new RowData().setValues(values)))
								.setFields("userEnteredValue,userEnteredFormat.backgroundColor")));
			}
		}	
		
		BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);
		service.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute();
		}
		}catch (Exception e) {
			System.out.println("Issue in updating sheet...");
			e.printStackTrace();
			// TODO: handle exception
		}
	}

	public static void updateSheetMCNADentaRunStatus(String spreadsheetId, String sheetSubID, String clientDir, String clientFolder,
			String status,int rowNumber,int columnIndex) throws IOException {
		Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(clientDir, clientFolder))
				.setApplicationName(APPLICATION_NAME).build();

		List<Request> requests = new ArrayList<>();
		//100 original -- 90 now
		//List<CellData> values = new ArrayList<>();
		List<CellData> values = new ArrayList<>();
		values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(status)));
		requests.add(new Request()
				.setUpdateCells(new UpdateCellsRequest().setStart(new GridCoordinate().setSheetId(Integer.parseInt(sheetSubID)).setRowIndex(rowNumber)//)
						 .setColumnIndex(columnIndex))
						.setRows(Arrays.asList(new RowData().setValues(values)))
						.setFields("userEnteredValue,userEnteredFormat.backgroundColor")));

        
		//values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue("Hello World! update 99")));
		//values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue("Hello World! update 22")));
		
		BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);
		service.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute();
	}


	public static void appendCelltoSheet(String spreadsheetId, String sheetSubID, String clientDir, String clientFolder,
			int columnCount) throws IOException {
		Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(clientDir, clientFolder))
				.setApplicationName(APPLICATION_NAME).build();

		List<Request> requests = new ArrayList<>();
		Request request = new Request();
		AppendDimensionRequest appendRequest = new AppendDimensionRequest();
		appendRequest.setDimension("COLUMNS");
		appendRequest.setLength(columnCount);
		request .setAppendDimension(appendRequest );
		requests.add(request);
		BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);
		service.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute();

	}
	public static void updateSheetMCNADenta(String spreadsheetId, String sheetSubID, String clientDir, String clientFolder,
			List<EligibilityDto> rList, int rowCount,String status,String medicaltype) throws IOException {
		Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(clientDir, clientFolder))
				.setApplicationName(APPLICATION_NAME).build();

		List<Request> requests = new ArrayList<>();
		rowCount=rowCount+3;
		//100 original -- 90 now
		//List<CellData> values = new ArrayList<>();
		if (rList != null) {
			//List<CellData> valuesDel = new ArrayList<>();
			/*
			if (rList.size() < rowCount) {
				// now we have to delete Extra Rows
				//int deleteCount = rowCount - rList.size();
				valuesDel = new ArrayList<>();
				for (int x = 0; x <= 7; x++) {
					//valuesDel.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue("")));
				}
				for (int x = rList.size()+1; x <= rowCount+1; x++) {
				//	requests.add(new Request()
					//		.setUpdateCells(new UpdateCellsRequest().setStart(new GridCoordinate().setSheetId(0).setRowIndex(x))
						//			// .setColumnIndex(3))
							//		.setRows(Arrays.asList(new RowData().setValues(valuesDel)))
								//	.setFields("userEnteredValue,userEnteredFormat.backgroundColor")));
					
				}

			}
			*/
			
			//int x = 4;	
			//int hiscMax = 200;
			//int his = 1;
			for(EligibilityDto rd:rList) {
				List<CellData> values = new ArrayList<>();
				
				values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(rd.getFirstName())));
				values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(rd.getLastName())));
				values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(rd.getSubscriberId())));
				values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(rd.getDob())));
				values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(rd.getInsuranceName())));
				
				
				
				values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(rd.getEligible())));
				values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(rd.getEmployerName())));
				if (medicaltype.equals("M")) {
					values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(rd.getProviderChange())));
					values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(rd.getProviderName())));
				}
				if (medicaltype.equals("D")) {
					values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(rd.getProviderName())));
				}
				values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(rd.getBenefitRemaining())));
				values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(rd.getCopay())));
				if (medicaltype.equals("D")) {
					values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(rd.getComment())));
				}
				for(HistoryDto d:rd.getHistoryList()) {
					
					values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(d.getCode())));
					values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(d.getTooth())));
					values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(d.getDos())));
					//his++;
					//if (his > hiscMax) break; 
				}
				//setStart(new GridCoordinate().setSheetId(0)
				requests.add(new Request()
						.setUpdateCells(new UpdateCellsRequest().setStart(new GridCoordinate().setSheetId(Integer.parseInt(sheetSubID)).setRowIndex(Integer.parseInt(rd.getMcnaSheet().getRowNumber()))//)
								 .setColumnIndex(3))
								.setRows(Arrays.asList(new RowData().setValues(values)))
								.setFields("userEnteredValue,userEnteredFormat.backgroundColor")));
				//x++;
				//break;
			}
				

			
			List<CellData> values = new ArrayList<>();
			values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(status)));
			requests.add(new Request()
					.setUpdateCells(new UpdateCellsRequest().setStart(new GridCoordinate().setSheetId(Integer.parseInt(sheetSubID)).setRowIndex(ConstantsScrapping.ELE_ROW_INDEX_STATUS)//)
							 .setColumnIndex(ConstantsScrapping.ELE_COLUMN_INDEX_STATUS))
							.setRows(Arrays.asList(new RowData().setValues(values)))
							.setFields("userEnteredValue,userEnteredFormat.backgroundColor")));

    	
	        
		}
        
		//values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue("Hello World! update 99")));
		//values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue("Hello World! update 22")));
		
		BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);
		service.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute();
	}

	public static void updateFullScrapSheet(String spreadsheetId, String sheetSubID, String clientDir, String clientFolder,
			List<PatientTemp> rList,String status) throws IOException {
		Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(clientDir, clientFolder))
				.setApplicationName(APPLICATION_NAME).build();

		List<Request> requests = new ArrayList<>();
		//rowCount=rowCount+3;
		if (rList != null) {
			for(PatientTemp rd:rList) {
				List<CellData> values = new ArrayList<>();
				values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(rd.getStatus())));
				//setStart(new GridCoordinate().setSheetId(0)
				requests.add(new Request()
						.setUpdateCells(new UpdateCellsRequest().setStart(new GridCoordinate().setSheetId(Integer.parseInt(sheetSubID)).setRowIndex(Integer.parseInt(rd.getRowNumber()))//)
								 .setColumnIndex(10))
								.setRows(Arrays.asList(new RowData().setValues(values)))
								.setFields("userEnteredValue,userEnteredFormat.backgroundColor")));
				//x++;
				//break;
			}
				

			if (!status.equals(ConstantsScrapping.NO_WRITE)) {
			List<CellData> values = new ArrayList<>();
			values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(status)));
			requests.add(new Request()
					.setUpdateCells(new UpdateCellsRequest().setStart(new GridCoordinate().setSheetId(Integer.parseInt(sheetSubID)).setRowIndex(ConstantsScrapping.ELE_ROW_INDEX_STATUS)//)
							 .setColumnIndex(ConstantsScrapping.ELE_COLUMN_INDEX_STATUS))
							.setRows(Arrays.asList(new RowData().setValues(values)))
							.setFields("userEnteredValue,userEnteredFormat.backgroundColor")));
			}
    	
	        
		}
        
		//values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue("Hello World! update 99")));
		//values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue("Hello World! update 22")));
		
		BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);
		service.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute();
	}

	public static Map<String, List<Object>> readSheetMcnaDenta(String spreadsheetId, String sheetName,
			String clientDir, String clientFolder) throws IOException {
		Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(clientDir, clientFolder))
				.setApplicationName(APPLICATION_NAME).build();
		ValueRange response = service.spreadsheets().values().get(spreadsheetId, sheetName).execute();
		return readMCNADentaSheet(response);
	}


	public static List<ExceptionDataDto> readSheetExceptionRulesheet(String spreadsheetId, String sheetName,
			String clientDir, String clientFolder) throws IOException {
		Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(clientDir, clientFolder))
				.setApplicationName(APPLICATION_NAME).build();
		ValueRange response = service.spreadsheets().values().get(spreadsheetId, sheetName).execute();
		return readExceptionRulesheet(response);
	}

	public static Map<String, List<Object>> readSheeFullWebsiteParsing(String spreadsheetId, String sheetName,
			String clientDir, String clientFolder) throws IOException {
		Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(clientDir, clientFolder))
				.setApplicationName(APPLICATION_NAME).build();
		ValueRange response = service.spreadsheets().values().get(spreadsheetId, sheetName).execute();
		return readFullWebsiteParsingSheet(response);
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
	public static Map<String, List<Object>> readIVFSheet(ValueRange range, String[] uniqueIds, String officeName,
			boolean idsPatient,boolean breakLoop) {

		List<List<Object>> values = range.getValues();
		Map<String, List<Object>> map = null;
		List<String> ivds = Arrays.asList(uniqueIds);
		ListIterator li = values.listIterator(values.size());
		IVFTableSheet vif = null;
		// IVFHistorySheet vifH = null;
		List<Object> ivList = null;
		// int maxlength= values.size();
		// int maxlengthT= values.size();
		// System.out.println("maxlengthT30::"+maxlengthT);
		//int Column_NO_UNIQUE = 312;
		//int Column_NO_PATIENT = 129;

		while (li.hasPrevious()) {
			ArrayList<String> obj = (ArrayList<String>) li.previous();
			String uniqueId = "";
			try {
				if (obj.get(Column_NO_UNIQUE).toLowerCase().startsWith("Unique_ID"))
					break;
				// System.out.println("id---" + ivds.get(0));
				// System.out.println("id---" + officeName + "_" + ivds.get(0));
				// System.out.println("888888:;" + (obj.get(157)));
				Collection<String> ruleGen = null;
				if (idsPatient) {
					ruleGen = Collections2.filter(ivds, id -> id.equals(obj.get(Column_NO_PATIENT)));
				} else {
					ruleGen = Collections2.filter(ivds,
							id -> (officeName + "_" + id).equals(obj.get(Column_NO_UNIQUE)));
				}
				if (ruleGen != null && ruleGen.size() > 0) {
					// uniqueId = ruleGen.get(0);//obj.get(157);
					if (idsPatient) {
						uniqueId = obj.get(Column_NO_UNIQUE).split("_")[1];
						/*
						 * for(String i:ruleGen) { uniqueId=i; }
						 */
					} else {
						for (String i : ruleGen) {
							uniqueId = i;
						}
					}
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
							obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), // 20*6
																												// +5
							obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), // 21*6 +5 =131
							new IVFHistorySheet(obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
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
									obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
									obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
									obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
									obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
									obj.get(++x)),
							obj.get(++x), obj.get(++x), obj.get(++x)

					);

				} else {
					continue;
				}
			} catch (Exception ex) {
				continue;
			}
			// System.out.println(uniqueId);

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
			if (breakLoop && map != null && uniqueIds.length == map.size())
				break;// Because
			// }//For loop
		} // While Loop - 1

		return map;

	}

	public static Map<String, List<Object>> readIVFWholeSheet(ValueRange range, String officeName
			) {

		List<List<Object>> values = range.getValues();
		Map<String, List<Object>> map = null;
		ListIterator li = values.listIterator(values.size());
		IVFTableSheet vif = null;
		// IVFHistorySheet vifH = null;
		List<Object> ivList = null;
		// int maxlength= values.size();
		// int maxlengthT= values.size();
		// System.out.println("maxlengthT30::"+maxlengthT);
		//int Column_NO_UNIQUE = 312;
		//int Column_NO_PATIENT = 129;

		while (li.hasPrevious()) {
			ArrayList<String> obj = (ArrayList<String>) li.previous();
			String uniqueId = "";
			try {
				if (obj.get(Column_NO_UNIQUE).toLowerCase().startsWith("Unique_ID".toLowerCase()))
					continue;
				// System.out.println("id---" + ivds.get(0));
				// System.out.println("id---" + officeName + "_" + ivds.get(0));
				// System.out.println("888888:;" + (obj.get(157)));
				Collection<String> ruleGen = null;
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
							obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), // 20*6
																												// +5
							obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), // 21*6 +5 =131
							new IVFHistorySheet(obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
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
									obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
									obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
									obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
									obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
									obj.get(++x)),
							obj.get(++x), obj.get(++x), obj.get(++x)

					       );
				//Done because some sheets have LB an LC and some not...	
				try {
					vif.setD0120(obj.get(++x));	
				}catch (Exception e) {
					// TODO: handle exception
				}
				try {
					vif.setD2391(obj.get(++x));	
				}catch (Exception e) {
					// TODO: handle exception
				}

				
			} catch (Exception ex) {
				continue;
			}
			// System.out.println(uniqueId);

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

			
		} // While Loop - 1

		return map;

	}
   /**
    * For new IVF sheet with new Columns added..
    * @param range
    * @param officeName
    * @return
    */
	public static Map<String, List<Object>> readIVFWholeSheetWithNewDump(ValueRange range, String officeName
			) {

		List<List<Object>> values = range.getValues();
		Map<String, List<Object>> map = null;
		ListIterator li = values.listIterator();
		IVFTableSheet vif = null;
		
		
		// IVFHistorySheet vifH = null;
		List<Object> ivList = null;
		// int maxlength= values.size();
		// int maxlengthT= values.size();
		// System.out.println("maxlengthT30::"+maxlengthT);
		//int Column_NO_UNIQUE = 312;
		//int Column_NO_PATIENT = 129;
        int rowCounter=-1;
		while (li.hasNext()) {
			ArrayList<String> obj = (ArrayList<String>) li.next();
			String uniqueId = "";
			try {
				if (obj.get(Column_NO_UNIQUE).toLowerCase().startsWith("Unique_ID".toLowerCase()))
					continue;
				// System.out.println("id---" + ivds.get(0));
				// System.out.println("id---" + officeName + "_" + ivds.get(0));
				// System.out.println("888888:;" + (obj.get(157)));
				//Collection<String> ruleGen = null;
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
							obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), // 20*6
																												// +5
							obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), // 21*6 +5 =131
							new IVFHistorySheet(obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
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
									obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
									obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
									obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
									obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
									obj.get(++x)),
							obj.get(++x), obj.get(++x), obj.get(++x)

					       );
					try {
						vif.setD0120(obj.get(++x));	
					}catch (Exception e) {
						// TODO: handle exception
					}
					try {
						vif.setD2391(obj.get(++x));	
					}catch (Exception e) {
						// TODO: handle exception
					}
					try {
						vif.setBridges1(obj.get(++x)); //LD
					}catch (Exception e) {
						// TODO: handle exception
					}
					try {
						vif.setBridges2(obj.get(++x)); //LE
					}catch (Exception e) {
						// TODO: handle exception
					}
					try {
						vif.setCrowngradeCode(obj.get(++x));//LF 
					}catch (Exception e) {
						// TODO: handle exception
					}
					try {
						vif.setDen5225Per(obj.get(++x));//LG
					}catch (Exception e) {
						// TODO: handle exception
					}
					try {
						vif.setDen5226Per(obj.get(++x));//LH
					}catch (Exception e) {
						// TODO: handle exception
					}
					try {
						vif.setDenf5225FR(obj.get(++x));//LI
					}catch (Exception e) {
						// TODO: handle exception
					}
					try {
						vif.setDenf5226Fr(obj.get(++x));//LJ
					}catch (Exception e) {
						// TODO: handle exception
					}
				
					try {
						vif.setDiagnosticSubDed(obj.get(++x));//LK
					}catch (Exception e) {
						// TODO: handle exception
					}
					try {
						vif.setImplantsFrD6010(obj.get(++x));//LL
					}catch (Exception e) {
						// TODO: handle exception
					}
					try {
						vif.setImplantsFrD6057(obj.get(++x));//LM
					}catch (Exception e) {
						// TODO: handle exception
					}
					try {
						vif.setImplantsFrD6065(obj.get(++x));//LN
					}catch (Exception e) {
						// TODO: handle exception
					}
					try {
						vif.setImplantsFrD6190(obj.get(++x));//LO
					}catch (Exception e) {
						// TODO: handle exception
					}
					try {
						vif.setNightGuardsD9945Percentage(obj.get(++x));//LP
					}catch (Exception e) {
						// TODO: handle exception
					}
					try {
						vif.setOrthoRemaining(obj.get(++x));//LQ
					}catch (Exception e) {
						// TODO: handle exception
					}
					try {
						vif.setOrthoWaitingPeriod(obj.get(++x));//LR
					}catch (Exception e) {
						// TODO: handle exception
					}
					try {
						vif.setpAXRaysSubDed(obj.get(++x));//LS
					}catch (Exception e) {
						// TODO: handle exception
					}
					try {
						vif.setPreventiveSubDed(obj.get(++x));//LT
					}catch (Exception e) {
						// TODO: handle exception
					}
					try {
						vif.setWillDowngradeApplicable(obj.get(++x));//LU 
					}catch (Exception e) {
						// TODO: handle exception
					}
					try {
						vif.setFmxPer(obj.get(++x)); //LV --
					}catch (Exception e) {
						// TODO: handle exception
					}
					try {
						vif.setNightGuardsD9944Fr(obj.get(++x));//LW
					}catch (Exception e) {
						// TODO: handle exception
					}
					try {
						vif.setNightGuardsD9945Fr(obj.get(++x));//LX T21
					}catch (Exception e) {
						// TODO: handle exception
					}
					try {
						vif.setCrownsInYear(obj.get(++x));//LY T22
					}catch (Exception e) {
						// TODO: handle exception
					}
					try {
						vif.setExtractionsInYear(obj.get(++x));//LZ T23
					}catch (Exception e) {
						// TODO: handle exception
					}
					try {
						vif.setFillingsInYear(obj.get(++x));//MA T24
					}catch (Exception e) {
						// TODO: handle exception
					}
					try {
						vif.setWaitingPeriod4(obj.get(++x));//MB WaitingPeriod4
					}catch (Exception e) {
						continue;
					}
					try {
						vif.setShareFr(obj.get(++x));//MC ShareFr
					}catch (Exception e) {
						continue;
					}
					try {
						vif.setPedo1(obj.get(++x));//MD pedo1
					}catch (Exception e) {
						continue;
					}
					try {
						vif.setPedo2(obj.get(++x));//ME pedo2
					}catch (Exception e) {
						continue;
					}
					try {
						vif.setPano1(obj.get(++x));//MF pano1
					}catch (Exception e) {
						continue;
					}
					try {
						vif.setPano2(obj.get(++x));//MG pano2
					}catch (Exception e) {
						continue;
					}
					try {
						vif.setD4381(obj.get(++x));//MH d4381
					}catch (Exception e) {
						continue;
					}
					try {
						vif.setSheetSubId(obj.get(++x));//MH sheetsubid
					}catch (Exception e) {
						continue;
					}
					vif.setRowCounter(++rowCounter);
					vif.setStatusDump("OK");
					if (vif.getPatientId().trim().equals(""))continue;
				 //For new Added Columns
				
				

				
			} catch (Exception ex) {
				continue;
			}
			// System.out.println(uniqueId);

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

			
		} // While Loop - 1

		return map;

	}
	public static Map<String, List<Object>> readMCNADentaSheet(ValueRange range) {

		List<List<Object>> values = range.getValues();
		Map<String, List<Object>> map = null;
		ListIterator li = values.listIterator();
		MCNADentaSheet mcna = null;
		// IVFHistorySheet vifH = null;
		List<Object> mcnaList = null;
		// int maxlength= values.size();
		// int maxlengthT= values.size();
		// System.out.println("maxlengthT30::"+maxlengthT);
		int heading_rows = 2;
		int subscriberIdCT = 0;
        int ct=-1;
		while (li.hasNext()) {
			ArrayList<String> obj = (ArrayList<String>) li.next();
			//String subscriber = "";
			try {
				ct++;
				if (ct<=heading_rows)
				continue;
				// System.out.println("id---" + ivds.get(0));
				// System.out.println("id---" + officeName + "_" + ivds.get(0));
				// System.out.println("888888:;" + (obj.get(157)));
				//Collection<String> ruleGen = null;
				
				int x = -1;
				//subscriber = obj.get(5);
				mcna = new MCNADentaSheet(obj.get(++x),obj.get(++x),obj.get(++x),obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),(ct)+"");
					
				
			} catch (Exception ex) {
				ex.printStackTrace();
				continue;
			}
			// System.out.println(uniqueId);

			if (map == null)
				map = new HashMap<>();

			if (map.containsKey(subscriberIdCT+"")) {
				// if the key has already been used,
				// we'll just grab the array list and add the value to it
				mcnaList = map.get(subscriberIdCT+"");
				mcnaList.add(mcna);
			} else {
				// if the key hasn't been used yet,
				// we'll create a new ArrayList<String> object, add the value
				// and put it in the array list with the new key
				mcnaList = new ArrayList<>();
				mcnaList.add(mcna);
				map.put(subscriberIdCT+"", mcnaList);
			}
			subscriberIdCT++;
			// if (ivList ==null) ivList= new ArrayList<>();
			// ivList.add(vif);
			// }//For loop
		} // While Loop - 1

		return map;

	}

	private static List<ExceptionDataDto> readExceptionRulesheet(ValueRange range) {

		List<List<Object>> values = range.getValues();
		ListIterator li = values.listIterator();
		ExceptionDataDto sh = null;
		// IVFHistorySheet vifH = null;
		List<ExceptionDataDto> shList = null;
		// int maxlength= values.size();
		// int maxlengthT= values.size();
		// System.out.println("maxlengthT30::"+maxlengthT);
		int heading_rows = 0;
        int ct=-1;
		while (li.hasNext()) {
			ArrayList<String> obj = (ArrayList<String>) li.next();
			//String subscriber = "";
			try {
				ct++;
				if (ct<=heading_rows)
				continue;
				int x = -1;
				//subscriber = obj.get(5);
				sh = new ExceptionDataDto();
				sh.setEmpolyerName("");
				sh.setGroup("");
				sh.setCode("");
				sh.setMessage("");
				sh.setResultType("");
				
				
				try {
					//sh.setEmpolyerName(obj.get(++x));
					sh.setGroup(obj.get(++x));
					
				}catch (Exception e) {
					// TODO: handle exception
				}
				try {
					sh.setCode(obj.get(++x));
				}catch (Exception e) {
					// TODO: handle exception
				}
				try {
					sh.setMessage(obj.get(++x));
				}catch (Exception e) {
					// TODO: handle exception
				}
				try {
					sh.setResultType(obj.get(++x));
				}catch (Exception e) {
					// TODO: handle exception
				}
				
				if (shList==null) shList= new ArrayList<>();
				shList.add(sh);
				
			} catch (Exception ex) {
				ex.printStackTrace();
				continue;
			}

		} // While Loop - 1

		return shList;

	}

	private static Map<String, List<Object>> readFullWebsiteParsingSheet(ValueRange range) {

		List<List<Object>> values = range.getValues();
		Map<String, List<Object>> map = null;
		ListIterator li = values.listIterator();
		FullWebsiteDataParsingSheet sheet = null;
		List<Object> dataList = null;
		int heading_rows = 2;
		int subscriberIdCT = 0;
        int ct=-1;
		while (li.hasNext()) {
			ArrayList<String> obj = (ArrayList<String>) li.next();
			//String uni = "";
			try {
				ct++;
				if (ct<=heading_rows)
				continue;
				int x = 1;
				//uni = obj.get(8);
				sheet = new FullWebsiteDataParsingSheet(obj.get(++x),obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),obj.get(++x),
						obj.get(++x),ct+"");
					
				
			} catch (Exception ex) {
				ex.printStackTrace();
				continue;
			}
		
			if (map == null)
				map = new HashMap<>();

			dataList = new ArrayList<>();
			dataList.add(sheet);
			map.put(subscriberIdCT+"", dataList);
			subscriberIdCT++;	
			}
			
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
	
	
	public static void updateIVFGoogleSheet(String spreadsheetId, String sheetSubID, String clientDir, String clientFolder,
			List<CaplineIVFFormDto> li) throws IOException {
		Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(clientDir, clientFolder))
				.setApplicationName(APPLICATION_NAME).build();

		List<Request> requests = new ArrayList<>();
		//rowCount=rowCount+3;
		//100 original -- 90 now
		//List<CellData> values = new ArrayList<>();
		if (li != null) {
			//int hiscMax = 200;
			//int his = 1;
			for(CaplineIVFFormDto rd:li) {
				List<CellData> values = new ArrayList<>();
				values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(rd.getBasicInfo1())));
				values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(rd.getBasicInfo2())));
				values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(rd.getBasicInfo3())));
				values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(rd.getBasicInfo4())));
				values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(rd.getBasicInfo5())));
				values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(rd.getBasicInfo6())));
				values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(rd.getBasicInfo7())));
				
				
				for(ToothHistoryDto d:rd.getHdto()) {
					String th=d.getHistoryTooth();
					if (d.getSurfaceTooth()!=null && !d.getSurfaceTooth().equals("")) th=th+"-"+d.getSurfaceTooth();
					values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(d.getHistoryCode())));
					values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(d.getHistoryTooth())));
					values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(d.getHistoryDos())));
					//his++;
					//if (his > hiscMax) break; 
				}
				//setStart(new GridCoordinate().setSheetId(0)
				requests.add(new Request()
						.setUpdateCells(new UpdateCellsRequest().setStart(new GridCoordinate().setSheetId(Integer.parseInt(sheetSubID)).setRowIndex(Integer.parseInt("2"))//)
								 .setColumnIndex(0))
								.setRows(Arrays.asList(new RowData().setValues(values)))
								.setFields("userEnteredValue,userEnteredFormat.backgroundColor")));
				//x++;
				//break;
			}
				

			
		}
        
		BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);
		service.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute();
	}

}
