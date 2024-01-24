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
import com.google.api.services.sheets.v4.model.BatchClearValuesRequest;
import com.google.api.services.sheets.v4.model.BatchClearValuesResponse;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetResponse;
import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.CellFormat;
import com.google.api.services.sheets.v4.model.Color;
import com.google.api.services.sheets.v4.model.ExtendedValue;
import com.google.api.services.sheets.v4.model.GridCoordinate;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.RowData;
import com.google.api.services.sheets.v4.model.TextFormat;
import com.google.api.services.sheets.v4.model.UpdateCellsRequest;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.common.collect.Collections2;
import com.tricon.ruleengine.api.enums.HighLevelReportMessageStatusEnum;
import com.tricon.ruleengine.dto.CaplineIVFFormDto;
import com.tricon.ruleengine.dto.DigitizationRuleEngineResult;
import com.tricon.ruleengine.dto.ExceptionDataDto;
import com.tricon.ruleengine.dto.OrthoGoogleSheetDto;
import com.tricon.ruleengine.dto.RemoteLiteData;
import com.tricon.ruleengine.dto.ReportDto;
import com.tricon.ruleengine.dto.ReportResponseDto;
import com.tricon.ruleengine.dto.ToothHistoryDto;
import com.tricon.ruleengine.dto.scrapping.EligibilityDto;
import com.tricon.ruleengine.dto.scrapping.FullWebsiteScrapDto;
import com.tricon.ruleengine.dto.scrapping.HistoryDto;
import com.tricon.ruleengine.dto.scrapping.RosterDetails;
import com.tricon.ruleengine.model.db.PatientTemp;
import com.tricon.ruleengine.model.sheet.CRAReqMappingDto;
import com.tricon.ruleengine.model.sheet.FullWebsiteDataParsingSheet;
import com.tricon.ruleengine.model.sheet.IVFHistorySheet;
import com.tricon.ruleengine.model.sheet.IVFTableSheet;
import com.tricon.ruleengine.model.sheet.InsuranceMappingDto;
import com.tricon.ruleengine.model.sheet.MCNADentaSheet;
import com.tricon.ruleengine.model.sheet.OrthoOfficeMappingDto;

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
		final int COLUMN_STATUS=208;
		try {
		if (ivfMap!=null) {
		for (Map.Entry<String, List<Object>> entry : ivfMap.entrySet()) {

			//String key = entry.getKey();
			List<Object> obL = entry.getValue();
			for (Object obj : obL) {
				sh = (IVFTableSheet) obj;
				List<CellData> values = new ArrayList<>();//347 old  353
				values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(sh.getStatusDump())));
				requests.add(new Request()
						.setUpdateCells(new UpdateCellsRequest().setStart(new GridCoordinate().setSheetId(Integer.parseInt(sheetSubID)).setRowIndex(sh.getRowCounter()+1)
								.setColumnIndex(COLUMN_STATUS))
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
				//if (medicaltype.equals("D")) {
					values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(rd.getComment())));
				//}
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

	
	private static String convertRowToRange(int n) {
		String r="";
		if (n==0) r= "A";     //26 52
		else if (n==1) r= "B";//27 53
		else if (n==2) r= "C";//28 54
		else if (n==3) r= "D";//29 55
		else if (n==4) r= "E";//30 56
		else if (n==5) r= "F";//31 57
		else if (n==6) r= "G";//32 58
		else if (n==7) r= "H";//33 59
		else if (n==8) r= "I";//34 60
		else if (n==9) r= "J";//35 61
		else if (n==10) r= "K";//36 62
		else if (n==11) r= "L";//37 63
		else if (n==12) r= "M";//38 64
		else if (n==13) r= "N";//39 65
		else if (n==14) r= "O";//40 66
		else if (n==15) r= "P";//41 67
		else if (n==16) r= "Q";//42 68
		else if (n==17) r= "R";//43 69 
		else if (n==18) r= "S";//44 70 
		else if (n==19) r= "T";//45 71
		else if (n==20) r= "U";//46 72
		else if (n==21) r= "V";//47 73
		else if (n==22) r= "W";//48 74
		else if (n==23) r= "X";//49 75
		else if (n==24) r= "Y";//50 76
		else if (n==25) r= "Z";//51 77
		else if (n>=26 && n<=51) {
			
			
			r=r+convertRowToRange(0);
			//r=r+convertRowToRange(n-26); //51-26 =>25
			r=r+convertRowToRange(n-(26*(0+1)));
		} else if (n>=52 && n<=77) {
			
			r=r+convertRowToRange(1); //77 BZ  77-26-26=>25
			r=r+convertRowToRange(n-(26*(1+1)));
		}else if (n>=78 && n<=103) {
			
			r=r+convertRowToRange(2); //77 BZ  77-26-26=>25
			//r=r+convertRowToRange(n-26-26-26);
			r=r+convertRowToRange(n-(26*(2+1)));
		}else if (n>=78 && n<=103) {
			
			r=r+convertRowToRange(3); //77 BZ  77-26-26=>25
			//r=r+convertRowToRange(n-26-26-26-26);
			r=r+convertRowToRange(n-(26*(3+1)));
		}	
		return r;
		
	}
	
	public static void main(String [] a) throws IOException {
		
		
		updateGoogleReportsDigitationSheet("1PSzfq1J7ajKWwM9Y7uUsLQ2hPWB0_f8mMs16IF9R69Q", "Patient ID Wise Search",2006499654, "E:/Project/Tricon/files/client_secret.json", "E:/Project/Tricon/files", null);
	}
	
	private static Color getSheetColorByType(int mType) {
		
		
		if (mType==HighLevelReportMessageStatusEnum.FAIL.getStatus()) return new Color().setRed(1f).setGreen(0f).setBlue(0f);
		else if (mType==HighLevelReportMessageStatusEnum.PASS.getStatus()) return new Color().setRed(0f).setGreen(1f).setBlue(0f);
		else if (mType==HighLevelReportMessageStatusEnum.ALERT.getStatus()) return new Color().setRed(0f).setGreen(0f).setBlue(1f);
		else if (mType==4) return new Color().setRed(0f).setGreen(0f).setBlue(102f);//Blue Heading..
		else if (mType==5) return new Color().setRed(1f).setGreen(1f).setBlue(1f);//white
		else if (mType==6) return new Color().setRed(0f).setGreen(102f).setBlue(0f);//Green Heading..
		else if (mType==7) return new Color().setRed(0f).setGreen(102f).setBlue(0f);//Not run/found..
		
		
		
		else return new Color().setRed(0f).setGreen(0f).setBlue(0f);
	}
	
	public static ReportDto readGoogleReportsDigitationSheet(String spreadsheetId, String sheetName,int sheetSubId, 
			String clientDir, String clientFolder,ReportDto dto) throws IOException {
		
		Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(clientDir, clientFolder))
				.setApplicationName(APPLICATION_NAME).build();

		//List<Request> requests = new ArrayList<>();
		ValueRange response = service.spreadsheets().values().get(spreadsheetId, sheetName).execute();
		
		List<List<Object>> values = response.getValues();
		//Map<String, List<Object>> map = null;
		ListIterator li = values.listIterator();
		//IVFTableSheet vif = null;
		
		
       
       while (li.hasNext()) {
			ArrayList<String> obj =  (ArrayList<String>) li.next();
			
			int x = -1;
			
			if (sheetSubId==Constants.Digitization_of_RE_Results_TP|| sheetSubId==Constants.Digitization_of_RE_Results_Cl) {
				obj.get(++x);//Office
				dto.setOffficeName(obj.get(++x));
				obj.get(++x);//From
				dto.setReportField1(obj.get(++x));
				obj.get(++x);//To
				dto.setReportField2(obj.get(++x));//F
					
			}else {
				obj.get(++x);//Type of Validation
				String q=obj.get(++x);
				if (q.equalsIgnoreCase("Claim Validation")) dto.setmType("c");
				else dto.setmType("v");
				obj.get(++x);
				dto.setOffficeName(obj.get(++x));//D
				obj.get(++x);//Patient Id
				dto.setPatientId(obj.get(++x));//Patient Id F
				
				
			}
			break;
		}

		
	     return dto;
	}

	public static List<ExceptionDataDto> updateGoogleReportsDigitationSheet(String spreadsheetId, String sheetName,int sheetSubId, 
			String clientDir, String clientFolder,List<DigitizationRuleEngineResult> dataList) throws IOException {
		updateGoogleReportSheet(spreadsheetId,sheetName,sheetSubId, 
				clientDir, clientFolder,dataList);
		
	     return null;
	}

	private static List<ExceptionDataDto> updateGoogleReportSheet(String spreadsheetId, String sheetName,int sheetSubId, 
			String clientDir, String clientFolder,List<DigitizationRuleEngineResult> dataList) throws IOException {
		Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(clientDir, clientFolder))
				.setApplicationName(APPLICATION_NAME).build();
         try {
		//List<Request> requests = new ArrayList<>();
		ValueRange response = service.spreadsheets().values().get(spreadsheetId, sheetName).execute();
		
		List<List<Object>> values = response.getValues();
		ListIterator li = values.listIterator();
        int rowCounter=-1;
       
        List<String> rangesClear = new ArrayList<>();
		while (li.hasNext()) {
			ArrayList<String> obj1 = (ArrayList<String>) li.next();
			rowCounter++;
			if(rowCounter<=2) {
				continue;
			}
			ListIterator obj = obj1.listIterator();
			int c=0;
			while(obj.hasNext()) {
				obj.next();
				String p=convertRowToRange(c)+(rowCounter+1);
				///System.out.println("-----------------------------"+"'"+sheetName+"'!"+p);
				rangesClear.add("'"+sheetName+"'!"+p);
				c++;
                  				
			}
			
			
			
		}
		
		if (rangesClear.size()>0) {
			
		BatchClearValuesRequest requestBody = new BatchClearValuesRequest();
	    requestBody.setRanges(rangesClear);
	    Sheets.Spreadsheets.Values.BatchClear request =
	    		service.spreadsheets().values().batchClear(spreadsheetId, requestBody);
	     BatchClearValuesResponse response1 = request.execute();
		}
	     int initRow=2;
	     int initCol=0;
	     int max =100;
	     int ctMax =0;
	     
	     List<Request> requestsT = new ArrayList<>();
	    for(DigitizationRuleEngineResult res:dataList) {
    		List<CellData> cellValues = new ArrayList<>();
	    	/*res.getId();
	    	res.getName();
	    	res.getRunDate();
	    	res.getOfficeName();*/
    		CellFormat f=null;
    		if (initRow==2) {
    			
    			 f=	new CellFormat().setBackgroundColor(getSheetColorByType(4));
    			 //new TextFormat().setForegroundColor(foregroundColor)Red(1f).setGreen(0f).setBlue(0f);
    			
    		}else {
    			f=	new CellFormat().setBackgroundColor(getSheetColorByType(5));
    		}
    		cellValues.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(res.getPatientId())).setUserEnteredFormat(f));
    		
    		cellValues.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(res.getDocumentId())).setUserEnteredFormat(f));
    	    
    		cellValues.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(res.getDos())).setUserEnteredFormat(f));

    		cellValues.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(res.getRunDate())).setUserEnteredFormat(f));
    		
    		cellValues.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(res.getId())).setUserEnteredFormat(f));
    		//System.out.println("dataList.size()dataList.size()----->>"+res.getPatientId());
    		
    		Map<Integer,List<String[]>>  m=res.getRuleMap();
    		if (m!=null) {
	    	for (Map.Entry<Integer, List<String[]>> entry : m.entrySet()) {
	    		//System.out.println("9999999----->>"+res.getPatientId());
	    		List<String[]> eL=entry.getValue();
	    		String x="";
	    		int ms=0; 
	    		for (String[] e:eL) {
	    			//x=x+"\n"+e[1];
	    			x=x+e[1];
	    			//System.out.println("9999999----->>"+x);
	    			//System.out.println("9999999----->>"+e[0]);
	    			
	    			ms=Integer.parseInt(e[0]);
		    			
	    		}
	    		
	    		cellValues.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(x)).
        	    		setUserEnteredFormat(new CellFormat().setBackgroundColor(getSheetColorByType(ms))));
	    		//initCol++;
	     	 }
    		}	  
    		
    		requestsT.add(new Request()
					.setUpdateCells(new UpdateCellsRequest().setStart(new GridCoordinate().setSheetId(sheetSubId).setRowIndex(initRow)//)
							 .setColumnIndex(initCol))
							.setRows(Arrays.asList(new RowData().setValues(cellValues)))
							.setFields("userEnteredValue,userEnteredFormat.backgroundColor")));
    		
    		initRow++;
    		ctMax++;
    		if (ctMax==max) {
    			ctMax=0;
    			BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(requestsT);
    		    BatchUpdateSpreadsheetResponse r= service.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute();
    		    requestsT.clear();
    		}
    		
	    }
	    
	    //System.out.println("dataList.size()dataList.size()-----------"+dataList.size());
	    if (dataList.size()==1) {
	    	List<CellData> cellValues = new ArrayList<>();
	    	cellValues.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue("No data found..")));
	    	requestsT.add(new Request()
					.setUpdateCells(new UpdateCellsRequest().setStart(new GridCoordinate().setSheetId(sheetSubId).setRowIndex(3)//)
							 .setColumnIndex(initCol))
							.setRows(Arrays.asList(new RowData().setValues(cellValues)))
							.setFields("userEnteredValue,userEnteredFormat.backgroundColor")));
	    }
	    
	    BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(requestsT);
	    BatchUpdateSpreadsheetResponse r= service.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute();
		System.out.println(r.getSpreadsheetId());
	
         }catch(Exception e) {
        	 e.printStackTrace();
         }
	        return null;
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
				if (obj.get(1).toLowerCase().startsWith("PatientName".toLowerCase()))
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
							null,
							/*new IVFHistorySheet(obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
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
									obj.get(++x))*/
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
						vif.setD0160Freq(obj.get(++x));//MI d0160Freq
					}catch (Exception e) {
						continue;
					}
					try {
						vif.setD2391Freq(obj.get(++x));//MJ d2391Freq
					}catch (Exception e) {
						continue;
					}
					try {
						vif.setD0330Freq(obj.get(++x));//MK d0330Freq
					}catch (Exception e) {
						continue;
					}
					try {
						vif.setD4381Freq(obj.get(++x));//ML d4381Freq
					}catch (Exception e) {
						continue;
					}
					try {
						vif.setD3330(obj.get(++x));//MM d3330
					}catch (Exception e) {
						continue;
					}
					try {
						vif.setD3330Freq(obj.get(++x));//MN d3330Freq
					}catch (Exception e) {
						continue;
					}
					try {
						vif.setFreqD2934(obj.get(++x));//Freq D2934
					}catch (Exception e) {
						continue;
					}
					try {
						vif.setD0350(obj.get(++x));//D0350 Percent
					}catch (Exception e) {
						continue;
					}
					try {
						vif.setD1330(obj.get(++x));//D1330 Percent
					}catch (Exception e) {
						continue;
					}
					try {
						vif.setD2930(obj.get(++x));//D2930 Percent
					}catch (Exception e) {
						continue;
					}
					try {
						vif.setD7250(obj.get(++x));//d7250  
					}catch (Exception e) {
						continue;
					}
					try {
						vif.setFmxSubjectToDed(obj.get(++x));//fmxSubjectToDed
					}catch (Exception e) {
						continue;
					}
					try {
						vif.setD1510(obj.get(++x));//d1510
					}catch (Exception e) {
						continue;
					}
					try {
						vif.setD1510Freq(obj.get(++x));//d1510Freq
					}catch (Exception e) {
						continue;
					}
					try {
						vif.setD1516(obj.get(++x));//d1516
					}catch (Exception e) {
						continue;
					}
					try {
						vif.setD1516Freq(obj.get(++x));//d1516Freq
					}catch (Exception e) {
						continue;
					}
					try {
						vif.setD1517(obj.get(++x));//d1517
					}catch (Exception e) {
						continue;
					}
					try {
						vif.setD1517Freq(obj.get(++x));//d1517Freq
					}catch (Exception e) {
						continue;
					}
					try {
						vif.setD3220(obj.get(++x));//d3220
					}catch (Exception e) {
						continue;
					}
					try {
						vif.setD3220Freq(obj.get(++x));//d3220Freq
					}catch (Exception e) {
						continue;
					}
					try {
						vif.setOutNetworkMessage(obj.get(++x));//OutnetworkMessage
					}catch (Exception e) {
						continue;
					}
					try {
						vif.setSmAgeLimit(obj.get(++x));//space maintance smAgeLimit  
					}catch (Exception e) {
						continue;
					}
					try {
						vif.setPerioD4921(obj.get(++x));//perioD4921
					}catch (Exception e) {
						continue;
					}
					try {
						vif.setD4921Frequency(obj.get(++x));//D4921Frequency
					}catch (Exception e) {
						continue;
					}
					try {
						vif.setPerioD4266(obj.get(++x));//perioD4266  
					}catch (Exception e) {
						continue;
					}
					try {
						vif.setD4266Frequency(obj.get(++x));//D4266Frequency
					}catch (Exception e) {
						continue;
					}
					try {
						vif.setPerioD9910(obj.get(++x));//perioD9910 
					}catch (Exception e) {
						continue;
					}
					try {
						vif.setD9910Frequency(obj.get(++x));//D9910Frequency 
					}catch (Exception e) {
						continue;
					}
					try {
						vif.setOonbenfits(obj.get(++x));//oonbenfits 
					}catch (Exception e) {
						continue;
					}
					try {
						vif.setD9630(obj.get(++x));//d9630 
					}catch (Exception e) {
						continue;
					}
					try {
						vif.setD9630fr(obj.get(++x));//d9630fr 
					}catch (Exception e) {
						continue;
					}
					try {
						vif.setD0431(obj.get(++x));//d0431
					}catch (Exception e) {
						continue;
					}
					try {
						vif.setD0431fr(obj.get(++x));//d0431fr
					}catch (Exception e) {
						continue;
					}
					try {
						vif.setD4999(obj.get(++x));//d4999
					}catch (Exception e) {
						continue;
					}
					try {
						vif.setD4999fr(obj.get(++x));//d4999fr
					}catch (Exception e) {
						continue;
					}
					try {
						vif.setD2962(obj.get(++x));//d2962
					}catch (Exception e) {
						continue;
					}
					try {
						vif.setD2962fr(obj.get(++x));//d2962fr
					}catch (Exception e) {
						continue;
					}
					//
					try {
						vif.setMajord72101(obj.get(++x));//majord72101
					}catch (Exception e) {
						continue;
					}
					try {
						vif.setSrpd4341(obj.get(++x));//srpd4341
					}catch (Exception e) {
						continue;
					}
					try {
						vif.setD7953(obj.get(++x));//d7953
					}catch (Exception e) {
						continue;
					}
					try {
						vif.setPlanPreDMandatory(obj.get(++x));//policy12 
					}catch (Exception e) {
						continue;
					}
					try {
						vif.setSheetSubId(obj.get(++x));//GZ sheetsubid
					}catch (Exception e) {
						continue;
					}
					vif.setRowCounter(++rowCounter);
					vif.setStatusDump("OK");//MP
					vif.setDollarInToothHistory(false);
					x++;
					boolean add=false;
					List<IVFHistorySheet> lh= new ArrayList<>();
					while(true) {
						IVFHistorySheet s= new IVFHistorySheet();
						s.setHistoryCode("");
						s.setHistoryTooth("");
						s.setHistorySurface("");
						s.setHistoryDOS("");
						
						try {
							add=false;
							s.setHistoryCode(obj.get(++x));
							s.setHistoryTooth(obj.get(++x));
							if (s.getHistoryTooth().contains("$")) {
								vif.setDollarInToothHistory(true);
							}
							s.setHistoryDOS(DateUtils.correctDateformat(obj.get(++x)));
							//s.setHistorySurface(obj.get(++x));
							lh.add(s);
							 add=true;
						}catch (Exception e) {
							if (!add && s.getHistoryCode()!=null  && !s.getHistoryCode().equals("")) lh.add(s);
							break;
						}	
					
					}
					
					vif.setiVFHistorySheetList(lh);
					IVFHistorySheet hs = new IVFHistorySheet();
					vif.setHs(hs);

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
	
	public static void updateRemoteLiteScrapSheetGoogleSheet(String spreadsheetId, String sheetSubID, String clientDir, String clientFolder,
			List<RemoteLiteData> li,String status,int row) throws IOException {
		Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(clientDir, clientFolder))
				.setApplicationName(APPLICATION_NAME).build();

		List<Request> requests = new ArrayList<>();
		//rowCount=rowCount+3;
		//100 original -- 90 now
		//List<CellData> values = new ArrayList<>();
		System.out.println(sheetSubID);
		System.out.println(spreadsheetId);
		//int row=2;
		if (li != null) {
			//int hiscMax = 200;
			//int his = 1;
			for(RemoteLiteData rd:li) {
				List<CellData> values = new ArrayList<>();
				values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(rd.getProcessedDate())));
				values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(rd.getSendDate())));
				values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(rd.getName())));
				values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(rd.getSubscriberName())));
				values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(rd.getCarrier())));
				values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(rd.getStatus())));
				values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(rd.getDescription())));
				values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(rd.getServiceDate())));
				values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(rd.getTreatingSignature())));
				values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(rd.getHiddenClaims())));
				values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(rd.getLastUpdate())));
				
				requests.add(new Request()
						.setUpdateCells(new UpdateCellsRequest().setStart(new GridCoordinate().setSheetId(Integer.parseInt(sheetSubID)).setRowIndex(row)//)
								 .setColumnIndex(0))
								.setRows(Arrays.asList(new RowData().setValues(values)))
								.setFields("userEnteredValue,userEnteredFormat.backgroundColor")));
				row++;
				//break;
			}
				

			
		}
		
		if (!status.equals("")) {
			List<CellData> values = new ArrayList<>();
			values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(status)));
			requests.add(new Request()
					.setUpdateCells(new UpdateCellsRequest().setStart(new GridCoordinate().setSheetId(Integer.parseInt(sheetSubID)).setRowIndex(1)//)
							 .setColumnIndex(0))
							.setRows(Arrays.asList(new RowData().setValues(values)))
							.setFields("userEnteredValue,userEnteredFormat.backgroundColor")));
		}
        
		BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);
		service.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute();
	}
	
	
	
	public static List<InsuranceMappingDto> readSheetInsuranceMapping(String spreadsheetId, String sheetName,
			String clientDir, String clientFolder) throws IOException {
		Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(clientDir, clientFolder))
				.setApplicationName(APPLICATION_NAME).build();
		ValueRange response = service.spreadsheets().values().get(spreadsheetId, sheetName).execute();
		List<List<Object>> values = response.getValues();
		ListIterator li = values.listIterator();
		InsuranceMappingDto dto = null;
		List<InsuranceMappingDto> list = new ArrayList<>();
		int heading_rows = 2;
        int ct=-1;
		while (li.hasNext()) {
			ArrayList<String> obj = (ArrayList<String>) li.next();
			try {
				ct++;
				if (ct<=heading_rows)
				continue;
				int x = -1;
				dto = new InsuranceMappingDto(obj.get(++x),obj.get(++x),obj.get(++x),obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x));
					
				list.add(dto);
			} catch (Exception ex) {
				ex.printStackTrace();
				continue;
			}
			
		}
		return list;
	  }
	
	public static List<OrthoOfficeMappingDto> readSheetOrthoMapping(String spreadsheetId, String sheetName,
			String clientDir, String clientFolder) throws IOException {
		Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(clientDir, clientFolder))
				.setApplicationName(APPLICATION_NAME).build();
		ValueRange response = service.spreadsheets().values().get(spreadsheetId, sheetName).execute();
		List<List<Object>> values = response.getValues();
		ListIterator li = values.listIterator();
		OrthoOfficeMappingDto dto = null;
		List<OrthoOfficeMappingDto> list = new ArrayList<>();
		int heading_rows = 2;
        int ct=-1;
		while (li.hasNext()) {
			ArrayList<String> obj = (ArrayList<String>) li.next();
			try {
				ct++;
				if (ct<=heading_rows)
				continue;
				int x = -1;
				dto = new OrthoOfficeMappingDto(obj.get(++x),obj.get(++x));
					
				list.add(dto);
			} catch (Exception ex) {
				ex.printStackTrace();
				continue;
			}
			
		}
		return list;
	 }
	
	public static List<CRAReqMappingDto> readSheetCRAReqMapping(String spreadsheetId, String sheetName,
			String clientDir, String clientFolder) throws IOException {
		Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(clientDir, clientFolder))
				.setApplicationName(APPLICATION_NAME).build();
		ValueRange response = service.spreadsheets().values().get(spreadsheetId, sheetName).execute();
		List<List<Object>> values = response.getValues();
		ListIterator li = values.listIterator();
		CRAReqMappingDto dto = null;
		List<CRAReqMappingDto> list = new ArrayList<>();
		int heading_rows = 0;
        int ct=-1;
		while (li.hasNext()) {
			ArrayList<String> obj = (ArrayList<String>) li.next();
			try {
				ct++;
				if (ct<=heading_rows)
				continue;
				int x = -1;
				dto = new CRAReqMappingDto(obj.get(++x),obj.get(++x),obj.get(++x),
						obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x));
					
				list.add(dto);
			} catch (Exception ex) {
				ex.printStackTrace();
				continue;
			}
			
		}
		return list;
	  }
	
	public static Map<String, List<Object>> readSheetIvOrtho(String spreadsheetId, String sheetName,
			String clientDir, String clientFolder) throws IOException {
		Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(clientDir, clientFolder))
				.setApplicationName(APPLICATION_NAME).build();
		ValueRange response = service.spreadsheets().values().get(spreadsheetId, sheetName).execute();
		List<List<Object>> values = response.getValues();
		Map<String, List<Object>> map = null;
		ListIterator li = values.listIterator();
		OrthoGoogleSheetDto vif = null;
		
		
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
				if (obj.get(1).toLowerCase().startsWith("Office".toLowerCase()))
					continue;
				// System.out.println("id---" + ivds.get(0));
				// System.out.println("id---" + officeName + "_" + ivds.get(0));
				// System.out.println("888888:;" + (obj.get(157)));
				//Collection<String> ruleGen = null;
				int x = -1;
					vif = new OrthoGoogleSheetDto(obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
							obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
							obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
							obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
							obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
							obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
							obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x), obj.get(++x),
							obj.get(++x), obj.get(++x), obj.get(++x)
					       );
					
					vif.setRowCounter(++rowCounter);
					vif.setStatusDump("OK");
					
					x++;
					boolean add=false;
					
					
					if (vif.getEsid().trim().equals(""))continue;
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

			
		}
		
		return map;
	}

}
