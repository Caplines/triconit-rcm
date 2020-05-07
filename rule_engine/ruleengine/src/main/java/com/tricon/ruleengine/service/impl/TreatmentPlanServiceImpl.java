package com.tricon.ruleengine.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.transaction.Transactional;

import org.apache.poi.ss.usermodel.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.google.common.collect.Collections2;
import com.tricon.ruleengine.api.enums.HighLevelReportTypeEnum;
import com.tricon.ruleengine.api.enums.StatusTypeEnum;
import com.tricon.ruleengine.dao.OfficeDao;
import com.tricon.ruleengine.dao.SharePointDao;
import com.tricon.ruleengine.dao.TreatmentValidationDao;
import com.tricon.ruleengine.dao.UserDao;
import com.tricon.ruleengine.dto.CaplineIVFFormDto;
import com.tricon.ruleengine.dto.CaplineIVFQueryFormDto;
import com.tricon.ruleengine.dto.MicroSoftGraphToken;
import com.tricon.ruleengine.dto.PatientTreamentDto;
import com.tricon.ruleengine.dto.QuestionAnswerDto;
import com.tricon.ruleengine.dto.TPValidationResponseDto;
import com.tricon.ruleengine.dto.TreatmentPlanBatchValidationDto;
import com.tricon.ruleengine.dto.TreatmentPlanDto;
import com.tricon.ruleengine.dto.TreatmentPlanValidationDto;
import com.tricon.ruleengine.dto.UserInputDto;
import com.tricon.ruleengine.logger.RuleEngineLogger;
import com.tricon.ruleengine.model.db.EagleSoftDBDetails;
import com.tricon.ruleengine.model.db.GoogleSheets;
import com.tricon.ruleengine.model.db.MVPandVAP;
import com.tricon.ruleengine.model.db.Mappings;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.OneDriveApp;
import com.tricon.ruleengine.model.db.OneDriveFile;
import com.tricon.ruleengine.model.db.PatientDetail;
import com.tricon.ruleengine.model.db.PatientHistory;
import com.tricon.ruleengine.model.db.ReportClaimDetail;
import com.tricon.ruleengine.model.db.ReportDetail;
import com.tricon.ruleengine.model.db.Reports;
import com.tricon.ruleengine.model.db.ReportsClaim;
import com.tricon.ruleengine.model.db.Rules;
import com.tricon.ruleengine.model.db.User;
import com.tricon.ruleengine.model.db.UserInputRuleQuestionHeader;
import com.tricon.ruleengine.model.sheet.ClaimData;
import com.tricon.ruleengine.model.sheet.ClaimDataDetails;
import com.tricon.ruleengine.model.sheet.ClaimDataPatient;
import com.tricon.ruleengine.model.sheet.CommonDataCheck;
import com.tricon.ruleengine.model.sheet.CommonDataDetails;
import com.tricon.ruleengine.model.sheet.CommonDataPatient;
import com.tricon.ruleengine.model.sheet.EagleSoftEmployerMaster;
import com.tricon.ruleengine.model.sheet.EagleSoftFeeShedule;
import com.tricon.ruleengine.model.sheet.EagleSoftPatient;
import com.tricon.ruleengine.model.sheet.EagleSoftPatientWalkHistory;
import com.tricon.ruleengine.model.sheet.IVFHistorySheet;
import com.tricon.ruleengine.model.sheet.IVFTableSheet;
import com.tricon.ruleengine.model.sheet.TreatmentPlan;
import com.tricon.ruleengine.model.sheet.TreatmentPlanDetails;
import com.tricon.ruleengine.model.sheet.TreatmentPlanPatient;
import com.tricon.ruleengine.security.JwtUser;
import com.tricon.ruleengine.service.CaplineIVFGoogleFormService;
import com.tricon.ruleengine.service.EagleSoftDBAccessService;
import com.tricon.ruleengine.service.EagleSoftDBService;
import com.tricon.ruleengine.service.SharePointService;
import com.tricon.ruleengine.service.TreatmentPlanService;
import com.tricon.ruleengine.service.UserInputService;
import com.tricon.ruleengine.utils.ConnectAndReadSheets;
import com.tricon.ruleengine.utils.Constants;
import com.tricon.ruleengine.utils.DateUtils;
import com.tricon.ruleengine.utils.IVFFormConversionUtil;
import com.tricon.ruleengine.utils.MessageUtil;
import com.tricon.ruleengine.utils.ReadMicrosoftFile;
import com.tricon.ruleengine.utils.RuleBook;
import com.tricon.ruleengine.dao.UserInputQuestionDao;
import static java.util.Comparator.comparing;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

/**
 * @author Deepak.Dogra
 *
 */
@Transactional
@Service
public class TreatmentPlanServiceImpl implements TreatmentPlanService {

	private static Locale locale = new Locale("en");

	@Autowired
	Environment env;
	@Value("${google.credential.folder}")
	private String CREDENTIALS_FOLDER;

	@Value("${google.client.secret}")
	private String CLIENT_SECRET_DIR;

	@Value("${application.url}")
	private String AppUrl;

	@Value("${app.debug.folder}")
	private String appLogFolder;

	@Value("${application.sharepoint.init}")
	private String SharepointInit;

	@Autowired
	TreatmentValidationDao tvd;

	@Autowired
	UserDao userDao;

	@Autowired
	SharePointService spService;

	@Autowired
	MessageSource messageSource;

	@Autowired
	SharePointDao spd;

	@Autowired
	OfficeDao od;

	@Autowired
	EagleSoftDBService esService;

	@Autowired
	EagleSoftDBAccessService dbAccesService;
	
	@Autowired
    UserInputQuestionDao  userInputQuestionDao;
	
	@Autowired
	UserInputService userInputService;
	
	@Autowired
	CaplineIVFGoogleFormService  caplineIVFGoogleFormService;
	
	@Autowired
    @Qualifier("jwtUserDetailsService")
    private UserDetailsService userDetailsService;

	
	
	static Class<?> clazz = TreatmentPlanServiceImpl.class;

	private Object[] logFileToAppendData(String officeName) {

		BufferedWriter bw = null;
		FileWriter fw = null;
		String n = officeName + new Date().toString().trim().replaceAll(" ", "").replaceAll(":", "-") + ".txt";

		try {
			File file = new File(appLogFolder + n);
			if (!file.exists()) {
				file.createNewFile();
			}
			// true = append file
			fw = new FileWriter(file.getAbsoluteFile(), true);
			bw = new BufferedWriter(fw);

		} catch (IOException e) {

			e.printStackTrace();

		}
		return new Object[] { n, bw, fw };
	}

	private void closeBuffer(BufferedWriter bw, FileWriter fw) {

		try {

			if (bw != null)
				bw.close();

			if (fw != null)
				fw.close();

		} catch (IOException ex) {

			ex.printStackTrace();

		}
	}

	@Override
	public Map<String, List<TPValidationResponseDto>> validateTreatmentPlan(TreatmentPlanValidationDto dtod) {
		dbAccesService.setUpSSLCertificates();
		Map<String, List<TPValidationResponseDto>> returnMap = null;
		Date currentDate = new Date();
		String fName = "";
		BufferedWriter bw = null;
		Object[] o = null;
		String tempFname = "";
		Office off = od.getOfficeByUuid(dtod.getOfficeId());
		try {
			o = logFileToAppendData(off.getName().trim());
			fName = (String) o[0];
			bw = (BufferedWriter) o[1];
			GoogleSheets ivsheet = null;

			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			Object principal = authentication.getPrincipal();
			final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails)principal).getUsername());
			JwtUser user = (JwtUser) userDetails;
			int type =user.getUserType();
            //String TRAN_DATE="";
			

			RuleEngineLogger.generateLogs(clazz, "Entering To Validate Treatment Plan (claim)", Constants.rule_log_debug, bw);
			// RuleEngineLogger.generateLogs(clazz,
			// "RuleEngineValidationController", Constants.rule_log_debug,bw);
			List<TPValidationResponseDto> list2 = new ArrayList<TPValidationResponseDto>();
			List<Rules> rules = tvd.getAllActiveRules();
			List<Mappings> mappings = tvd.getAllMappings();
			//MicroSoftGraphToken microsoft = null;

			boolean eagleSoftDBAccessPresent=false;
			List<GoogleSheets> sheets = tvd.getAllGoogleSheetByOffice(off);
			EagleSoftDBDetails esDB = tvd.getESDBDetailsByOffice(off);
			if (esDB!=null) {
				eagleSoftDBAccessPresent=true;
			}
			//List<OneDriveFile> fileOne = spd.getOneDriveFileByOfficeId(dtod.getOfficeId());
			TPValidationResponseDto dtoR = null;
			Map<String, List<Object>> ivfMap = null;
			Rules rule = null;
			//OneDriveApp odriveApp = spd.getOneDriveAppDetailsByOfficeId(dtod.getOfficeId());
			/*
			if (eagleSoftDBAccessPresent ==false && (odriveApp != null && odriveApp.getRefreshToken() != null)) {

				// Generate New Access Token and Refresh Token.
				try {
					microsoft = spService.reGenerateAccAndRefreshTokens(odriveApp, odriveApp.getRefreshToken());
					spService.saveAccessAndRefreshTokenByAuthToken(odriveApp, dtod.getOfficeId(), null,
							microsoft.getAccess_token(), microsoft.getRefresh_token());

				} catch (IOException e) {
					// TODO Auto-generated catch block
					RuleEngineLogger.generateLogs(clazz, "Office 365 sheet not registered with Rule Engine",
							Constants.rule_log_debug, bw);
					dtoR = new TPValidationResponseDto(0, "Generic",
							"Office 365 sheet not registered with Rule Engine.<br>"
									+ "Please contact site Admin. or Click link <a target='_blank' " + "href='" + AppUrl
									+ SharepointInit + "?officeId=" + dtod.getOfficeId()
									+ "'> Register Office With App </a>",
							Constants.FAIL);
					list2.add(dtoR);
					if (returnMap == null)
						returnMap = new HashMap<String, List<TPValidationResponseDto>>();
					returnMap.put("no", list2);

					return returnMap;
				}

			} else if (eagleSoftDBAccessPresent==false){
				RuleEngineLogger.generateLogs(clazz, "Office 365 sheet not registered with Rule Engine",
						Constants.rule_log_debug, bw);
				dtoR = new TPValidationResponseDto(0, "Generic", "Office 365 sheet not registered with Rule Engine.<br>"
						+ "Please contact site Admin. or Click link <a target='_blank' " + "href='" + AppUrl
						+ SharepointInit + "?officeId=" + dtod.getOfficeId() + "'> Register Office With App </a>",
						Constants.FAIL);
				list2.add(dtoR);
				if (returnMap == null)
					returnMap = new HashMap<String, List<TPValidationResponseDto>>();
				returnMap.put("no", list2);

				return returnMap;

			}
			*/
			if (rules != null && rules.size() == 0) {
				RuleEngineLogger.generateLogs(clazz, "Generic- No Active Rules Found", Constants.rule_log_debug, bw);
				dtoR = new TPValidationResponseDto(0, "Generic", "Generic- No Active Rules Found", Constants.FAIL,"","","");
				list2.add(dtoR);
				if (returnMap == null)
					returnMap = new HashMap<String, List<TPValidationResponseDto>>();
				returnMap.put("No Rule Found", list2);

				return returnMap;
			}

			try {
				

				Map<String, List<EagleSoftPatient>> espatients=null;
				Map<String, List<EagleSoftPatientWalkHistory>> espatientsHis=null;
				List<MVPandVAP> mvpVapList=null;
				Map<String, List<Object>> tMap=null;
				Map<String, List<Object>> tMapReduced=null;
				
				
				Map<String, List<EagleSoftEmployerMaster>> esempmaster = null;
				Map<String, List<EagleSoftFeeShedule>> esfeess = null;
				
				
				String ivs[] = null;
				if (dtod.getIvfId()!=null) ivs=dtod.getIvfId().split(",");
				String trids[] = dtod.getTreatmentPlanId().split(",");
				// Read Treatment Plan...
				//OneDriveFile tpsheet =null;
				if (eagleSoftDBAccessPresent== false) {
					/*
					ReadMicrosoftFile rmF = new ReadMicrosoftFile();
					// Read Fee Treatment Plan key id IVF id ..

					RuleEngineLogger.generateLogs(clazz,
							Constants.rule_log_read_fil_start + "-" + Constants.microsoft_treatement_sheet_name,
							Constants.rule_log_debug, bw);
					tMap = (Map<String, List<Object>>) (Map<String, ?>) rmF
							.downloadAndReadUsingStream(spService.webUrlForMicrosoftSheets(tpsheet, odriveApp),
									tpsheet.getSheetName(), Constants.microsoft_treatement_sheet_name, trids, null, null,
									null);

					RuleEngineLogger.generateLogs(clazz,
							Constants.rule_log_read_fil_end + "-" + Constants.microsoft_treatement_sheet_name,
							Constants.rule_log_debug, bw);
					// End
					GoogleSheets ivsheet = null;
					if (sheets != null && sheets.size() > 0) {
						ivsheet = sheets.get(0);
					} else {
						rule = getRulesFromList(rules, Constants.RULE_ID_2);
						RuleEngineLogger.generateLogs(clazz, "No IVF Sheet Found for Selected Office" + off.getName(),
								Constants.rule_log_debug, bw);
						dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(),
								"<b class='error-message-api'>No IVF Sheet Found for Selected Office-.</b>" + off.getName(),
								Constants.FAIL);
						list2.add(dtoR);
						if (returnMap == null)
							returnMap = new HashMap<String, List<TPValidationResponseDto>>();
						returnMap.put(
								"IVF ID(" + dtod.getIvfId() + ") Treatment Plan ID (" + dtod.getTreatmentPlanId() + ")",
								list2);

						return returnMap;
					}
					// Read IVF Sheet From Google key is IVF id

					RuleEngineLogger.generateLogs(clazz,
							Constants.rule_log_read_fil_start + "-" + Constants.google_ivf_sheet, Constants.rule_log_debug,
							bw);
					ivfMap = ConnectAndReadSheets.readSheet(ivsheet.getSheetId(),
							off.getName() + " " + ivsheet.getAppSheetName(), ivs, CLIENT_SECRET_DIR, CREDENTIALS_FOLDER,
							off.getName(),false);

					RuleEngineLogger.generateLogs(clazz, Constants.rule_log_read_fil_end + "-" + Constants.google_ivf_sheet,
							Constants.rule_log_debug, bw);
					// End

					// Read Patient Key is Unique Id from IVF sheet
					OneDriveFile espatient = getOneDriveFileFromList(fileOne, Constants.microsoft_patient);
					// String[] treatmentPlanIds, List<String> codes, Map<String, List<Object>>
					// ivMap,
					// Map<String, List<Object>> patientsMap
					RuleEngineLogger.generateLogs(clazz,
							Constants.rule_log_read_fil_start + "-" + Constants.microsoft_patient, Constants.rule_log_debug,
							bw);
					espatients = (Map<String, List<EagleSoftPatient>>) (Map<String, ?>) rmF
							.downloadAndReadUsingStream(spService.webUrlForMicrosoftSheets(espatient, odriveApp),
									espatient.getSheetName(), Constants.microsoft_patient, null, null, ivfMap, null);
					RuleEngineLogger.generateLogs(clazz,
							Constants.rule_log_read_fil_end + "-" + Constants.microsoft_patient, Constants.rule_log_debug,
							bw);
					// End
					// Read emp_master Key is employee ID
					
					if (espatients != null && espatients.size() > 0) {
						RuleEngineLogger.generateLogs(clazz,
								Constants.rule_log_read_fil_start + "-" + Constants.microsoft_emp_master,
								Constants.rule_log_debug, bw);
						OneDriveFile esfemp = getOneDriveFileFromList(fileOne, Constants.microsoft_emp_master);
						// if (tList != null) {
						esempmaster = (Map<String, List<EagleSoftEmployerMaster>>) (Map<String, ?>) rmF
								.downloadAndReadUsingStream(spService.webUrlForMicrosoftSheets(esfemp, odriveApp),
										esfemp.getSheetName(), Constants.microsoft_emp_master, null, null, null,
										espatients);
						// }

						RuleEngineLogger.generateLogs(clazz,
								Constants.rule_log_read_fil_end + "-" + Constants.microsoft_emp_master,
								Constants.rule_log_debug, bw);

						/// FEE SCHEDULE
						OneDriveFile feeScheduleMaster = getOneDriveFileFromList(fileOne,
								Constants.microsoft_feeSchedule_master);
						RuleEngineLogger.generateLogs(clazz,
								Constants.rule_log_read_fil_start + "-" + Constants.microsoft_feeSchedule_master,
								Constants.rule_log_debug, bw);
						esfeess = (Map<String, List<EagleSoftFeeShedule>>) (Map<String, ?>) rmF.downloadAndReadUsingStream(
								spService.webUrlForMicrosoftSheets(feeScheduleMaster, odriveApp),
								feeScheduleMaster.getSheetName(), Constants.microsoft_feeSchedule_master, null, null, null,
								espatients);

						RuleEngineLogger.generateLogs(clazz,
								Constants.rule_log_read_fil_end + "-" + Constants.microsoft_feeSchedule_master,
								Constants.rule_log_debug, bw);

						// END

					}
	
					tpsheet =getOneDriveFileFromList(fileOne, Constants.microsoft_treatement_sheet_name);
					*/
				}else {
					
					//In New Approach 
					if (eagleSoftDBAccessPresent) {
						if (type==Constants.userType_TR) tMap=(Map<String, List<Object>>) (Map<String, ?>)dbAccesService.getTreatmentPlanData(trids, esDB,bw);
						if (type==Constants.userType_CL) tMap=(Map<String, List<Object>>) (Map<String, ?>)dbAccesService.getClaimData(trids, esDB,bw);
						//Phase 3 add new Query
						tMapReduced=tMap;
						tMap=createCommonDataObject(tMap,authentication,false,null);//create Common Object
						tMapReduced=createCommonDataObject(tMapReduced,authentication,true,dtod.getStatus());//create Common Object
						
						List<String> pid=null;
						Map<String,String> t_cl_pid=new HashMap<>();
						//if (tMap!=null && (type==Constants.userType_CL || && type==Constants.userType_TR) ) {
							pid= new  ArrayList<>();
							if (tMap!=null) {
							for (Map.Entry<String,List<Object>> entry : tMap.entrySet()) {  
								
					            for (Object obj : entry.getValue()) {
									CommonDataCheck tp = (CommonDataCheck) obj;
									//TRAN_DATE =tp.getCdDetails().getDateLastUpdated();
									pid.add(tp.getPatient().getId());
									if (t_cl_pid.get(tp.getPatient().getId())==null)t_cl_pid.put(tp.getPatient().getId(),tp.getId());
									break;
								}
			
							}
							}
						//}
						 //
						// Read IVF Sheet From Google key is IVF id
						String inv=Constants.invalidStr_TP;
						if (type==Constants.userType_CL) inv=Constants.invalidStr_Cl;
						if (sheets != null && sheets.size() > 0) {
							ivsheet = sheets.get(0);
						} else {
							rule = getRulesFromList(rules, Constants.RULE_ID_2);
							RuleEngineLogger.generateLogs(clazz, "No IVF Sheet Found for Selected Office" + off.getName(),
									Constants.rule_log_debug, bw);
							dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(),
									"<b class='error-message-api'>No IVF Sheet Found for Selected Office-.</b>" + off.getName(),
									Constants.FAIL,"","","");
							list2.add(dtoR);
							
							if (returnMap == null)
								returnMap = new HashMap<String, List<TPValidationResponseDto>>();
							returnMap.put(
									"IVF ID(" + dtod.getIvfId() + ") "+inv+" (" + dtod.getTreatmentPlanId() + ")",
									list2);

							return returnMap;
						}

						RuleEngineLogger.generateLogs(clazz,
								Constants.rule_log_read_fil_start + "-" + Constants.google_ivf_sheet, Constants.rule_log_debug,
								bw);
						if (true) {//Earlier this was only for Claim now for all (Claim + Treatment)  
							if (ivs!=null && dtod.getIvfId()!=null && dtod.getIvfId().length()>0) {
							if(esDB.getSheet()!=Constants.VALIDATE_FROM_SHEET) {
								ivfMap = ConnectAndReadSheets.readSheet(ivsheet.getSheetId(),
								off.getName() + " " + ivsheet.getAppSheetName(), ivs, CLIENT_SECRET_DIR, CREDENTIALS_FOLDER,
								off.getName(),false,true);
							}
							else {
								//Write Query
								CaplineIVFQueryFormDto fd= new CaplineIVFQueryFormDto();
								Set<String> set = new HashSet<>(Arrays.asList(ivs));
								fd.setUniqueIDs(set);
								
								List<CaplineIVFFormDto> civfD=(List<CaplineIVFFormDto>) caplineIVFGoogleFormService.searchIVFData(fd, off);
								ivfMap= IVFFormConversionUtil.copyValueToIVFSheet(civfD, off);							
								
								
							}
							//if (type==Constants.userType_CL) return null;
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_read_fil_end + "-" + Constants.google_ivf_sheet,
									Constants.rule_log_debug, bw);

							//
							espatients = (Map<String, List<EagleSoftPatient>>) (Map<String, ?>) dbAccesService.getPatientData(ivfMap, esDB,bw);
							
							espatientsHis= (Map<String, List<EagleSoftPatientWalkHistory>>) (Map<String, ?>) dbAccesService.getPatientHistoryES(ivfMap, esDB,
									new SimpleDateFormat(Constants.dateFormatStringESHis).format(new Date()),Constants.Medicaid_Provider_Limitation_MONTH,bw);
							
							mvpVapList=tvd.getAllMVPVAP();
							if (espatients != null && espatients.size() > 0) {
								esempmaster = (Map<String, List<EagleSoftEmployerMaster>>) (Map<String, ?>) dbAccesService.getEmployeeMaster(espatients, esDB,bw);
								esfeess = (Map<String, List<EagleSoftFeeShedule>>) (Map<String, ?>) dbAccesService.getFeeScheduleData(espatients, esDB,bw);
		                     }

							}
						
						}
					}		
			
				}
				
				
				// End
				List<TPValidationResponseDto> list = null;
				for (int y = 0; y < trids.length; y++) {
					list = new ArrayList<TPValidationResponseDto>();
					String TRAN_DATE="";
					String CLAIM_DOS=""; 
					String trx = trids[y];// off.getName() + "_" +
					String ivx = "";// off.getName() + "_" +
					CommonDataCheck tp = null;
					String empMasterKey = "HYPE100sq";// Some hypo value.
					
					List<Object> tList = null;
					List<Object> tListReduced = null;
					String inv1=Constants.invalidStr_TP;
					if (type==Constants.userType_CL) inv1=Constants.invalidStr_Cl;

					if (tMap == null) {
						rule = getRulesFromList(rules, Constants.RULE_ID_2);
						dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(),
								Constants.errorMessOPen + inv1 + Constants.errorMessClose,
								Constants.FAIL,"","","");
						// saveReport(authentication, rule, dtoR, trx, null,off);
						list.add(dtoR);

					} else if (tMap != null && tMap.get(trx) == null) {
						rule = getRulesFromList(rules, Constants.RULE_ID_2);
						dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(),
								Constants.errorMessOPen + inv1 + Constants.errorMessClose,
								Constants.FAIL,"","","");
						// saveReport(authentication, rule, dtoR, trx, null,off);
						list.add(dtoR);
					} else {
						tList = tMap.get(trx);
						tListReduced = tMapReduced.get(trx);
						
						// Create File Name to rename latter..
						tp = (CommonDataCheck) tList.get(0);
						
						TRAN_DATE =tp.getCdDetails().getDateLastUpdated();
						CLAIM_DOS=tp.getCdDetails().getDateLastUpdated();
						if (y == 0)
							tempFname = tp.getPatient().getId();

					}
					
					if (ivs!=null && dtod.getIvfId()!=null && dtod.getIvfId().length()>0) {
						ivx = ivs[y];// off.getName() + "_" +
						
					}else {
						//ivs=dtod.getIvfId().split(",")
						if (tp!=null) { 
						if (type==Constants.userType_TR) {
							TRAN_DATE= Constants.SIMPLE_DATE_FORMAT.format(new Date());
						}
						String pid =tp.getPatient().getId();
						Map<String, List<Object>> ivfMapPat=null;
						if (esDB.getSheet()==Constants.VALIDATE_FROM_SHEET ) {
							CaplineIVFQueryFormDto fd= new CaplineIVFQueryFormDto();
							fd.setPatientIdDB(pid);
							List<CaplineIVFFormDto> civfD=(List<CaplineIVFFormDto>) caplineIVFGoogleFormService.searchIVFData(fd, off);
							ivfMapPat= IVFFormConversionUtil.copyValueToIVFSheet(civfD, off);	
						}
						Object[] maps= DateUtils.selectOneKeyFromMapWithLatestDate(pid,ivsheet,CLIENT_SECRET_DIR,CREDENTIALS_FOLDER,off,
								          TRAN_DATE,true,type,esDB.getSheet(),ivfMapPat);
						ivfMap = (Map<String, List<Object>>)maps[1];
						if (ivfMap==null && (Map<String, List<Object>>)maps[0]!=null) {//This means we have no result due to date issue.
						if 	(true) {//This means result was there but not valid date..
							String debug = "";
							ivfMap = (Map<String, List<Object>>)maps[0];
							for ( String key : ivfMap.keySet() ) {
								ivx=key;break;//only length one will be there ..
							   }
							
							list.add(new TPValidationResponseDto(1, "The Latest IV Was Not Found", messageSource
									.getMessage("rule.lastest.iv.issue.error", new Object[] { ((IVFTableSheet) ivfMap.get(ivx).get(0)).getGeneralDateIVwasDone() },
											locale),
									Constants.FAIL,"","",""));
							String inv=Constants.TP_ID;
							if (type==Constants.userType_CL) inv=Constants.CL_ID;
							if (returnMap==null ) returnMap = new HashMap<String, List<TPValidationResponseDto>>();
							if (type==Constants.userType_TR)
									returnMap.put(inv+" - " + trx + " IV.id - " + ivx + " Of.Name -  " + off.getName()
											+ " PT.id - " + ((IVFTableSheet) ivfMap.get(ivx).get(0)).getPatientId() + " Pt.Name - "
											+ ((IVFTableSheet) ivfMap.get(ivx).get(0)).getPatientName() + debug, list);
							else returnMap.put(inv+" - " + trx + " IV.id - " + ivx + " Of.Name -  " + off.getName()
									+ " PT.id - " + ((IVFTableSheet) ivfMap.get(ivx).get(0)).getPatientId() + " Pt.Name - "
									+ ((IVFTableSheet) ivfMap.get(ivx).get(0)).getPatientName() + "  Tx plan validation "+ TRAN_DATE + debug, list);	
								
								
							
							continue;

						  }
						//continue;
						}
						if (ivfMap!=null) {
						for ( String key : ivfMap.keySet() ) {
							ivx=key;break;//only length one will be there ..
						   }
						
					    
						}
						
					   if (ivfMap != null && ivfMap.get(ivx) != null && ivfMap.get(ivx).get(0) != null ) {
                       espatients = (Map<String, List<EagleSoftPatient>>) (Map<String, ?>) dbAccesService.getPatientData(ivfMap, esDB,bw);
						
						espatientsHis= (Map<String, List<EagleSoftPatientWalkHistory>>) (Map<String, ?>) dbAccesService.getPatientHistoryES(ivfMap, esDB,
								new SimpleDateFormat(Constants.dateFormatStringESHis).format(new Date()),Constants.Medicaid_Provider_Limitation_MONTH,bw);
						
						mvpVapList=tvd.getAllMVPVAP();
						if (espatients != null && espatients.size() > 0) {
							esempmaster = (Map<String, List<EagleSoftEmployerMaster>>) (Map<String, ?>) dbAccesService.getEmployeeMaster(espatients, esDB,bw);
							esfeess = (Map<String, List<EagleSoftFeeShedule>>) (Map<String, ?>) dbAccesService.getFeeScheduleData(espatients, esDB,bw);
	                     }
						
						}
					  }
						
					}
					
					// IMPORTANT --SEE THIS --WHY DONE in IVF sheet need to match Officename_IVFID
					//String ivx = ivs[y];// off.getName() + "_" +
					String patKey = off.getName() + "_" + ivx;
					// String feeKey = "1";
					if (espatients != null && espatients.get(patKey) != null && espatients.get(patKey).get(0) != null) {
						empMasterKey = espatients.get(patKey).get(0).getEmployerId();
					}

					// Treatment Plan Sheet
					// Get web Url of TP sheet

					if (ivfMap != null && ivfMap.get(ivx) != null && ivfMap.get(ivx).get(0) != null ) {

						//Save User input first
						//Phase 2 User Input Work.
						List<UserInputRuleQuestionHeader> qhList=userInputQuestionDao.getAllUserInputQuestionsDbModel();
						//if (type==Constants.userType_TR && dtod.isDebugMode())saveUserInputs(authentication, rules, tList, ivfMap.get(ivx).get(0), list, off, mappings);
						//
						List<QuestionAnswerDto> ansL=null;
						String oldTp="";
						if (tp!=null && type== Constants.userType_TR) {
						UserInputDto userInputDto=new UserInputDto();
						userInputDto.setOfficeId(off.getUuid());
						userInputDto.setTreatmentPlanId(tp.getId());
						ansL=userInputService.getUserAnswersPermanent(userInputDto);
						if (ansL!=null && ansL.size()==0) {
							//means no ansL
							/*
							list.clear();//
							TPValidationResponseDto d= new TPValidationResponseDto(1, "No User Input Present", 
									messageSource.getMessage("rule.nouser.input", new Object[] {}, locale), Constants.FAIL, "", "", "");
							list.add(d);
							if (returnMap == null)
								returnMap = new HashMap<String, List<TPValidationResponseDto>>();

							returnMap.put(Constants.TP_ID+" - " + trx + " IV.id - " + ivx + " Of.Name -  " + off.getName()
										+ " PT.id - " + ((IVFTableSheet) ivfMap.get(ivx).get(0)).getPatientId() + " Pt.Name - "
										+ ((IVFTableSheet) ivfMap.get(ivx).get(0)).getPatientName() , list);
							return returnMap;
							*/

						}
						}else if(tp!=null && type== Constants.userType_CL) {
							IVFTableSheet ivf = (IVFTableSheet) ivfMap.get(ivx).get(0);
							ansL=userInputService.getUserAnswersPermanent(ivf.getPatientId(), ivf.getUniqueID().split("_")[1],TRAN_DATE, off);
							if (ansL!=null && ansL.size()>0 && ansL.get(0)!=null ) {
								oldTp = ansL.get(0).getTpId();
								TRAN_DATE=ansL.get(0).getTxPlanValidationDate().toString();
							}
						}
						// RULE_ID_1 (Eligibility of the patient)

						rule = getRulesFromList(rules, Constants.RULE_ID_1);
						RuleBook rb = new RuleBook();
						List<TPValidationResponseDto> dtoRL1 = new ArrayList<>();
						dtoRL1 = rb.Rule1(ivfMap.get(ivx).get(0), messageSource, rule, false,tListReduced ,tList, bw,type,dtod.getStatus());
						RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_1,
								Constants.rule_log_debug, bw);
						boolean exit=false;
						if (dtoRL1 != null) {
							list.addAll(dtoRL1);
							for (TPValidationResponseDto t : dtoRL1) {
								dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
										t.getResultType(),"","","");
								if (t.getResultType().equals(Constants.EXTI_ENGINE)) exit=true;
							}
						}
						//list.add(dtoR);
						//dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), dtoR.getMessage(),
						//		dtoR.getResultType());

						if (!exit) {

							/*
							 * List<String> serviceCode = getCodesfromTreamentPlanList(tList); OneDriveFile
							 * feeScheduleMaster = getOneDriveFileFromList(fileOne,
							 * Constants.microsoft_feeSchedule_master);
							 * 
							 * List<EagleSoftFeeShedule> esfeess = null; if (tList != null) { esfeess =
							 * (List<EagleSoftFeeShedule>) (List<?>) rmF.downloadAndReadUsingStream(
							 * spService.webUrlForMicrosoftSheets(feeScheduleMaster, odriveApp),
							 * feeScheduleMaster.getSheetName(), Constants.microsoft_feeSchedule_master,
							 * trx, serviceCode, (IVFTableSheet) ivfList.get(0), null); }
							 */
							// System.out.println("----------77-------------------");
							/*
							 * OneDriveFile espatient = getOneDriveFileFromList(fileOne,
							 * Constants.microsoft_patient); List<EagleSoftPatient> espatients =
							 * (List<EagleSoftPatient>) (List<?>) rmF
							 * .downloadAndReadUsingStream(spService.webUrlForMicrosoftSheets(espatient,
							 * odriveApp), espatient.getSheetName(), Constants.microsoft_patient, trx, null,
							 * (ivMa, null);
							 */
							/*
							 * List<EagleSoftEmployerMaster> esempmaster = null; if (espatients != null &&
							 * espatients.get(0) != null) { OneDriveFile esfemp =
							 * getOneDriveFileFromList(fileOne, Constants.microsoft_emp_master); if (tList
							 * != null) { esempmaster = (List<EagleSoftEmployerMaster>) (List<?>)
							 * rmF.downloadAndReadUsingStream( spService.webUrlForMicrosoftSheets(esfemp,
							 * odriveApp), esfemp.getSheetName(), Constants.microsoft_emp_master, trx,
							 * serviceCode, (IVFTableSheet) ivfList.get(0), ((EagleSoftPatient)
							 * espatients.get(0)).getEmployerId()); } }
							 */
							// System.out.println("----------8888-------------------");
							// End --Read Mapping Table && Eagle Soft table

							// RULE_ID_4 "Coverage Book"
							List<TPValidationResponseDto> dtoRL = new ArrayList<>();

							rule = getRulesFromList(rules, Constants.RULE_ID_4);
							String feeKey = "-1";
							if (espatients != null && espatients.get(patKey) != null
									&& espatients.get(patKey).size() > 0)
								feeKey = ((EagleSoftPatient) espatients.get(patKey).get(0)).getFeeScheduleId();
							if (esfeess != null && espatients != null && espatients.get(patKey).size() > 0) {
								dtoRL = rb.Rule4_B(tListReduced, ivfMap.get(ivx).get(0), messageSource, rule, mappings,
										esfeess.get(feeKey), espatients.get(patKey), bw,type);
							} else {
								if (esfeess == null)
									dtoRL.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
											messageSource.getMessage("rule.fee.notfound", new Object[] { "" }, locale),
											Constants.FAIL,"","",""));
								else if (espatients == null)
									dtoRL.add(new TPValidationResponseDto(rule.getId(), rule.getName(), messageSource
											.getMessage("rule.patient.notfound", new Object[] { "Patient ID-"
													+ ((IVFTableSheet) (ivfMap.get(ivx).get(0))).getPatientId() },
													locale),
											Constants.FAIL,"","",""));
								else
									dtoRL.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
											messageSource.getMessage("rule.patient.notfound",
													new Object[] { "Some Issue in Fee Sheet" }, locale),
											Constants.FAIL,"","",""));

							}
							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_4,
									Constants.rule_log_debug, bw);

							// END

							// RULE_ID_5 "Remaining Deductible, Remaining Balance and Benefit Max as per IV
							// form"
							if (type==Constants.userType_TR) {
							rule = getRulesFromList(rules, Constants.RULE_ID_5);
							dtoRL = new ArrayList<>();
							if (espatients != null) {
								dtoRL = rb.Rule5(ivfMap.get(ivx).get(0), messageSource, rule, espatients.get(patKey),
										bw,type);
								if (dtoRL != null) {
									list.addAll(dtoRL);
									for (TPValidationResponseDto t : dtoRL) {
										dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
												t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
										// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
									}
								}
							} else {
								dtoRL.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
										messageSource.getMessage("rule.patient.notfound", new Object[] { "" }, locale),
										Constants.FAIL,"","",""));

							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_5,
									Constants.rule_log_debug, bw);
							// END Remaining Deductible, Remaining Balance and Benefit Max as per IV
						    }
							// RULE_ID_6 "Percentage Coverage Check"
							rule = getRulesFromList(rules, Constants.RULE_ID_6);

							dtoRL = new ArrayList<>();
							if (espatients != null && esempmaster != null) {
								dtoRL = rb.Rule6(ivfMap.get(ivx).get(0), messageSource, rule,
										esempmaster.get(empMasterKey), espatients.get(patKey), bw, type);
								if (dtoRL != null) {
									list.addAll(dtoRL);
									for (TPValidationResponseDto t : dtoRL) {
										dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
												t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
										// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
									}
								}
							} else if (espatients == null) {
								dtoRL.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
										messageSource.getMessage("rule.patient.notfound", new Object[] { "" }, locale),
										Constants.FAIL,"","",""));
								list.addAll(dtoRL);

							} else {
								dtoRL.add(new TPValidationResponseDto(rule.getId(), rule.getName(), messageSource
										.getMessage("rule.employer.notfound", new Object[] { empMasterKey }, locale),
										Constants.FAIL,"","",""));
								list.addAll(dtoRL);

							}

							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_6,
									Constants.rule_log_debug, bw);
							// END "Percentage Coverage Check"

							// RULE_ID_7 "Alert
							if (type==Constants.userType_TR) {
							rule = getRulesFromList(rules, Constants.RULE_ID_7);
							dtoRL = rb.Rule7(ivfMap.get(ivx).get(0), messageSource, rule, bw);

							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_7,
									Constants.rule_log_debug, bw);

							// END Alert
						    }
							// RULE_ID_19 Downgrading

							rule = getRulesFromList(rules, Constants.RULE_ID_19);
							dtoRL = new ArrayList<>();
							if (esfeess != null && espatients != null) {
								dtoRL = rb.Rule19(tListReduced, ivfMap.get(ivx).get(0), messageSource, rule, mappings,
										esfeess.get(feeKey), espatients.get(patKey), bw,type);

								if (dtoRL != null) {
									list.addAll(dtoRL);
									for (TPValidationResponseDto t : dtoRL) {
										dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
												t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
										// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
									}
								}

							} else {

								RuleEngineLogger.generateLogs(clazz, Constants.RULE_ID_19 + "-rule.fee.notfound ",
										Constants.rule_log_debug, bw);

								if (esfeess == null)
									dtoRL.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
											messageSource.getMessage("rule.fee.notfound", new Object[] { "" }, locale),
											Constants.FAIL,"","",""));
								if (espatients == null)
									dtoRL.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
											messageSource.getMessage("rule.patient.notfound",
													new Object[] { new Object[] {
															"Patient ID-" + ((IVFTableSheet) (ivfMap.get(ivx).get(0)))
																	.getPatientId() } },
													locale),
											Constants.FAIL,"","",""));

								if (dtoRL != null) {
									list.addAll(dtoRL);
									for (TPValidationResponseDto t : dtoRL) {
										dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
												t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
										// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
									}
								}
							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_19,
									Constants.rule_log_debug, bw);
							// END Downgrading

							// RULE_ID_21 "// Frequency Limitations
							rule = getRulesFromList(rules, Constants.RULE_ID_21);
							dtoRL = rb.Rule21(tListReduced, ivfMap.get(ivx).get(0), messageSource, rule, mappings, bw,type);

							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_21,
									Constants.rule_log_debug, bw);

							// END Frequency Limitations

							// RULE_ID_8 "Age Limits
							rule = getRulesFromList(rules, Constants.RULE_ID_8);
							dtoRL = rb.Rule8(tListReduced, ivfMap.get(ivx).get(0), messageSource, rule, bw,type);

							
							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_8,
									Constants.rule_log_debug, bw);

							// END Age Limits

							// RULE_ID_18 "Missing Tooth Clause
							rule = getRulesFromList(rules, Constants.RULE_ID_18);
							dtoRL = rb.Rule18(tListReduced, ivfMap.get(ivx).get(0), messageSource, rule, mappings, bw,type);

							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_18,
									Constants.rule_log_debug, bw);

							// END Missing Tooth Clause

							// RULE_ID_17 " Bundling - Fillings
							rule = getRulesFromList(rules, Constants.RULE_ID_17);
							dtoRL = rb.Rule17(tListReduced, ivfMap.get(ivx).get(0),esfeess.get(feeKey), messageSource, rule, bw,type);

							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_17,
									Constants.rule_log_debug, bw);

							// END

							// RULE_ID_16 " Bundling - X-Rays
							rule = getRulesFromList(rules, Constants.RULE_ID_16);
							dtoRL = rb.Rule16(tListReduced, ivfMap.get(ivx).get(0), messageSource, rule, bw,type);

							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_16,
									Constants.rule_log_debug, bw);

							// END Bundling - X-Rays

							// RULE_ID_15 " SRP Quads Per Day
							rule = getRulesFromList(rules, Constants.RULE_ID_15);
							dtoRL = rb.Rule15(tListReduced, ivfMap.get(ivx).get(0), messageSource, rule, bw,type);

							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_15,
									Constants.rule_log_debug, bw);

							// END SRP Quads Per Day

							// RULE_ID_14 "Sealants
							rule = getRulesFromList(rules, Constants.RULE_ID_14);
							dtoRL = rb.Rule14(tListReduced, ivfMap.get(ivx).get(0), messageSource, rule, bw, type);

							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_14,
									Constants.rule_log_debug, bw);

							// END Sealants

							// RULE_ID_9 "Filling Codes based on Tooth No.

							rule = getRulesFromList(rules, Constants.RULE_ID_9);
							dtoRL = rb.Rule9(tListReduced, messageSource, rule, mappings, bw,type);

							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_9,
									Constants.rule_log_debug, bw);

							// END "Filling Codes based on Tooth No.

							// RULE_ID_10 "Pre Auth
							rule = getRulesFromList(rules, Constants.RULE_ID_10);
							dtoRL = rb.Rule10(tListReduced,ansL, messageSource, rule, mappings, bw,qhList,type);

							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_10,
									Constants.rule_log_debug, bw);

							// EN Pre Auth

							// RULE_ID_11 "Waiting Period.
							rule = getRulesFromList(rules, Constants.RULE_ID_11);
							dtoRL = rb.Rule11(tListReduced, ivfMap.get(ivx).get(0), messageSource, rule, mappings, bw,type);

							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_11,
									Constants.rule_log_debug, bw);

							// END

							// RULE_ID_13 "Build-Ups & Crown Same Day
							rule = getRulesFromList(rules, Constants.RULE_ID_13);
							dtoRL = rb.Rule13(tListReduced, ivfMap.get(ivx).get(0), messageSource, rule, bw,type);

							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_13,
									Constants.rule_log_debug, bw);

							// END "Build-Ups & Crown Same Day

							// RULE_ID_22 "CRA
							rule = getRulesFromList(rules, Constants.RULE_ID_22);
							dtoRL = rb.Rule22(tListReduced, ivfMap.get(ivx).get(0), messageSource, rule, bw,type);

							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_22,
									Constants.rule_log_debug, bw);

							// END "CRA

							// RULE_ID_23 "Xray Bundling
							rule = getRulesFromList(rules, Constants.RULE_ID_23);
							dtoRL = rb.Rule23(tListReduced, ivfMap.get(ivx).get(0),messageSource, rule, bw,type);

							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_23,
									Constants.rule_log_debug, bw);

							// END "Xray Bundling

							//  Filling Bundling 
							rule = getRulesFromList(rules, Constants.RULE_ID_24);
							dtoRL = rb.Rule24(tListReduced, ivfMap.get(ivx).get(0),messageSource, rule,esfeess.get(feeKey), bw,type);

							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_24,
									Constants.rule_log_debug, bw);

							// END  Filling Bundling
                           /*NOW USED NOW							
							// DQ Fillings (Provider Same)
							rule = getRulesFromList(rules, Constants.RULE_ID_51);
							dtoRL = rb.Rule51(tList, ivfMap.get(ivx).get(0),esfeess.get(feeKey), messageSource, rule, bw);
							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_51,
									Constants.rule_log_debug, bw);
								
							//END  DQ Fillings (Provider Same)
							// DQ Fillings (Provider Different)
							rule = getRulesFromList(rules, Constants.RULE_ID_52);
							dtoRL = rb.Rule52(tList, ivfMap.get(ivx).get(0),esfeess.get(feeKey), messageSource, rule, bw);
							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_52,
									Constants.rule_log_debug, bw);
								
							//END  DQ Fillings (Provider Different)
                            */
							// DQ Fillings
							rule = getRulesFromList(rules, Constants.RULE_ID_53);
							dtoRL = rb.Rule53(tListReduced, ivfMap.get(ivx).get(0),esfeess.get(feeKey), messageSource, rule, bw,type);
							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_53,
									Constants.rule_log_debug, bw);
								
							//END  DQ Fillings
                             //
							//  Crown Bundling with Fillings 
							rule = getRulesFromList(rules, Constants.RULE_ID_25);
							dtoRL = rb.Rule25(tListReduced, ivfMap.get(ivx).get(0),messageSource, rule,esfeess.get(feeKey), bw,type);

							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_25,
									Constants.rule_log_debug, bw);

							// END  Crown Bundling with Fillings
							
							//  Filling and Sealant Not Covered
							rule = getRulesFromList(rules, Constants.RULE_ID_26);
							dtoRL = rb.Rule26(tListReduced, ivfMap.get(ivx).get(0),messageSource, rule, bw,type);

							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_26,
									Constants.rule_log_debug, bw);

							// END  Filling and Sealant Not Covered

							//  Sealant Not Covered
							rule = getRulesFromList(rules, Constants.RULE_ID_27);
							dtoRL = rb.Rule27(tListReduced, ivfMap.get(ivx).get(0),messageSource, rule, bw,type);

							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_27,
									Constants.rule_log_debug, bw);

							// END  Sealant Not Covered
							
							//  Restoration Not Covered
							rule = getRulesFromList(rules, Constants.RULE_ID_28);
							dtoRL = rb.Rule28(tListReduced, ivfMap.get(ivx).get(0),messageSource, rule, bw,type);

							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_28,
									Constants.rule_log_debug, bw);

							// END  Restoration Not Covered

							//  Exams Limitation
							rule = getRulesFromList(rules, Constants.RULE_ID_29);
							dtoRL = rb.Rule29(tListReduced, ivfMap.get(ivx).get(0),messageSource, rule, bw, type);

							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_29,
									Constants.rule_log_debug, bw);

							// END  Exams Limitation
							
							//  Cleaning Limitation
							rule = getRulesFromList(rules, Constants.RULE_ID_30);
							dtoRL = rb.Rule30(tListReduced, ivfMap.get(ivx).get(0),messageSource, rule, bw,type);

							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_30,
									Constants.rule_log_debug, bw);

							// END  Cleaning Limitation

							//  Perio Maintainance Clause
							rule = getRulesFromList(rules, Constants.RULE_ID_31);
							dtoRL = rb.Rule31(tListReduced, ivfMap.get(ivx).get(0),messageSource, rule, bw,type);

							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_31,
									Constants.rule_log_debug, bw);

							// END  Perio Maintainance Clause
							
							//  SRP Limitation
							rule = getRulesFromList(rules, Constants.RULE_ID_32);
							dtoRL = rb.Rule32(tListReduced,messageSource, rule, bw,type);

							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_32,
									Constants.rule_log_debug, bw);

							// END  SRP Limitation

							//  Root Canal Clause
							rule = getRulesFromList(rules, Constants.RULE_ID_33);
							dtoRL = rb.Rule33(tListReduced,messageSource, rule, bw,type);

							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_33,
									Constants.rule_log_debug, bw);

							// END  Root Canal Clause

							//  D2954 Clause
							rule = getRulesFromList(rules, Constants.RULE_ID_34);
							dtoRL = rb.Rule34(tListReduced,messageSource, rule, bw,type);

							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_34,
									Constants.rule_log_debug, bw);

							// END  D2954 Clause

							//  Bone Graft Rule
							rule = getRulesFromList(rules, Constants.RULE_ID_35);
							dtoRL = rb.Rule35(tListReduced,messageSource, rule, bw,type);

							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_35,
									Constants.rule_log_debug, bw);

							// END  Bone Graft Rule

							//  Immediate Denture
							rule = getRulesFromList(rules, Constants.RULE_ID_36);
							dtoRL = rb.Rule36(tListReduced,ivfMap.get(ivx).get(0),messageSource, rule, bw,type);

							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_36,
									Constants.rule_log_debug, bw);

							// END  Immediate Denture

							//  Extraction Limitation
							rule = getRulesFromList(rules, Constants.RULE_ID_37);
							dtoRL = rb.Rule37(tListReduced,ivfMap.get(ivx).get(0),messageSource, rule, bw,type);

							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_37,
									Constants.rule_log_debug, bw);

							// END  Extraction Limitation

							// Medicaid Provider Limitation for D0150,D0210,D0330
							rule = getRulesFromList(rules, Constants.RULE_ID_38);
							if (espatientsHis != null && espatientsHis.get(patKey) != null
									&& espatientsHis.get(patKey).size() > 0) {
								dtoRL = rb.Rule38(ivfMap.get(ivx).get(0),tListReduced ,espatientsHis.get(patKey),messageSource, rule, bw,type);

								
							   	
							}else {
								dtoRL = rb.Rule38(ivfMap.get(ivx).get(0),tListReduced ,null,messageSource, rule, bw,type);
								
							}
							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_38,
									Constants.rule_log_debug, bw);
								
							//END Medicaid Provider Limitation for D0150,D0210,D0330
							
							//Age Limitation Prophylaxis
							rule = getRulesFromList(rules, Constants.RULE_ID_39);
							dtoRL = rb.Rule39(ivfMap.get(ivx).get(0),tListReduced ,messageSource, rule, bw,type);
							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_39,
									Constants.rule_log_debug, bw);
								
							//END Age Limitation Prophylaxis
							
							//Space Maintainer-Billateral
							rule = getRulesFromList(rules, Constants.RULE_ID_40);
							dtoRL = rb.Rule40(tListReduced ,messageSource, rule, bw, type);
							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_40,
									Constants.rule_log_debug, bw);
								
							//END Space Maintainer-Billateral

							//MVP VAP
							if (type==Constants.userType_TR){
							rule = getRulesFromList(rules, Constants.RULE_ID_41);
							dtoRL = rb.Rule41(tListReduced,mvpVapList ,messageSource, rule, bw);
							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_41,
									Constants.rule_log_debug, bw);
							}	
							//END MVP VAP

							//Duplicate TP Codes
							rule = getRulesFromList(rules, Constants.RULE_ID_42);
							dtoRL = rb.Rule42(tListReduced ,messageSource, rule, bw,type);
							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_42,
									Constants.rule_log_debug, bw);
								
							//END Duplicate TP Codes

							//Bone Graft (User Input)
							/*
							rule = getRulesFromList(rules, Constants.RULE_ID_43);
							dtoRL = rb.Rule43(tList,ansL ,messageSource, rule, bw);
							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_43,
									Constants.rule_log_debug, bw);
								
							//END Bone Graft (User Input)
							*/
							//Signed Consent Requirements (User Input) //Forms Required
							rule = getRulesFromList(rules, Constants.RULE_ID_44);
							dtoRL = rb.Rule44(ansL ,messageSource, rule, bw,qhList,type);
							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_44,
									Constants.rule_log_debug, bw);
								
							//END Signed Consent Requirements (User Input) //Forms Required
							
							//Ortho (User Input)
							/*
							rule = getRulesFromList(rules, Constants.RULE_ID_45);
							dtoRL = rb.Rule45(tList,ansL ,messageSource, rule, bw);
							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_45,
									Constants.rule_log_debug, bw);
								
							//END Ortho (User Input)
                            */
							//Pre-Authorization (User Input)
							rule = getRulesFromList(rules, Constants.RULE_ID_46);
							dtoRL = rb.Rule46(ivfMap.get(ivx).get(0),tListReduced,ansL ,messageSource,rule,mappings, bw,qhList,type,oldTp);
							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_46,
									Constants.rule_log_debug, bw);
								
							//END Pre-Authorization (User Input)
							
							//Provider Change (User Input)
							rule = getRulesFromList(rules, Constants.RULE_ID_47);
							dtoRL = rb.Rule47(ivfMap.get(ivx).get(0),tListReduced,ansL ,messageSource,rule,mappings, bw,qhList,type,oldTp);
							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_47,
									Constants.rule_log_debug, bw);
								
							//END Provider Change (User Input)
							
							//Exam limitation for CHIP
							rule = getRulesFromList(rules, Constants.RULE_ID_48);
							dtoRL = rb.Rule48(ivfMap.get(ivx).get(0),tListReduced ,messageSource, rule, bw,type);
							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_48,
									Constants.rule_log_debug, bw);
								
							//END Exam limitation for CHIP
							
							//Sealant limitation in CHIP
							rule = getRulesFromList(rules, Constants.RULE_ID_49);
							dtoRL = rb.Rule49(ivfMap.get(ivx).get(0),tList ,messageSource, rule, bw, type);
							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_49,
									Constants.rule_log_debug, bw);
								
							//END Sealant limitation in CHIP
							
							// Major Service Form Requirements (User Input)
							rule = getRulesFromList(rules, Constants.RULE_ID_50);
							dtoRL = rb.Rule50(tList,mappings,ansL ,messageSource, rule, bw,qhList);
							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_50,
									Constants.rule_log_debug, bw);
								
							//END  Major Service Form Requirements (User Input)
							
						}

					} else {
						rule = getRulesFromList(rules, Constants.RULE_ID_2);
						dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(),
								"<b class='error-message-api'>No Data Found in IVF Sheet.</b>", Constants.FAIL,"","","");
						list.add(dtoR);
						// saveReports(authentication, rule,dtoR, dto,(IVFTableSheet)(ivfList.get(0)));
					}
					if (ivfMap != null && ivfMap.get(ivx) != null && tListReduced != null)
						saveReportsList(authentication, rules, tp, (IVFTableSheet) (ivfMap.get(ivx).get(0)), list, off,type);
					// else
					// saveReportsList(authentication, rules, trx, null, list,off);

					if (returnMap == null)
						returnMap = new HashMap<String, List<TPValidationResponseDto>>();
					String debug = "";
					if (dtod.isDebugMode()) {
						if (!tempFname.equals("")) {
							tempFname = tempFname.trim().toLowerCase() + "-" + off.getName().trim().toLowerCase()
									+ currentDate.toString().trim().replaceAll(" ", "").replaceAll(":", "-") + ".txt";
							debug = " - <a target='_blank' href='" + AppUrl + "/appdebug/" + tempFname
									+ "'> Detailed Log</a>";
						} else {
							debug = " - <a target='_blank' href='" + AppUrl + "/appdebug/" + fName
									+ "'> Detailed Log</a>";

						}
					}

					/*
					 * "Change header from following to: From: IVF ID(2) Treatment Plan ID (7181)
					 * Patient ID (11217) - Debug
					 * 
					 * To: TP.id - 7181 IV.id - 2 Of.Name - [office name] PT.id - 11217 Pt.Name -
					 * [pt. name] - Detailed Log"
					 */
					String inv=Constants.TP_ID;
					if (type==Constants.userType_CL) inv=Constants.CL_ID;
					
					if (ivfMap == null && tList == null) {
						returnMap.put(inv+" " + Constants.notFound + " - " + trx + " IV.id  " + Constants.notFound
								+ " - " + ivx + " Of.Name -  " + off.getName(), list);

					} else if (ivfMap == null) {
						returnMap.put(inv+" - " + trx + " IV.id " + Constants.notFound + " - " + ivx + " Of.Name -  "
								+ off.getName(), list);

					} else if (tList == null) {
						returnMap.put(inv+" " + Constants.notFound + " - " + trx + " IV.id  - " + ivx + " Of.Name -  "
								+ off.getName(), list);

					} else {
						
						
						if (type==Constants.userType_TR)
						returnMap.put(inv+" - " + trx + " IV.id - " + ivx + " Of.Name -  " + off.getName()
								+ " PT.id - " + ((IVFTableSheet) ivfMap.get(ivx).get(0)).getPatientId() + " Pt.Name - "
								+ ((IVFTableSheet) ivfMap.get(ivx).get(0)).getPatientName()+" Status - "+StatusTypeEnum.getNameByType(dtod.getStatus())+" " + debug, list);
						else {
							String s=CLAIM_DOS;
							try{s=Constants.SIMPLE_DATE_FORMAT_HEADER.format(Constants.SIMPLE_DATE_FORMAT.parse(CLAIM_DOS));}catch(Exception e) {}
							String t=TRAN_DATE;
							try{t= Constants.SIMPLE_DATE_FORMAT_HEADER.format(Constants.SIMPLE_DATE_FORMAT.parse(TRAN_DATE));}catch(Exception e) {}
							
							returnMap.put("Claim ID"+": " + trx +" of " + off.getName()+ " office | "
						+ " Pt.ID: " + ((IVFTableSheet) ivfMap.get(ivx).get(0)).getPatientId() + " | IV.ID: " + ivx  + " | Pt.Name: "
						+ ((IVFTableSheet) ivfMap.get(ivx).get(0)).getPatientName() + " | DOS: "+ s+" | Tx plan Date: "+t + debug, list);
					   }
					}
					// returnMap.put("IVF ID(" + ivx + ") Treatment Plan IDTP.id (" + trx + ")
					// Patient ID ("
					// + ((IVFTableSheet) ivfMap.get(ivx).get(0)).getPatientId() + ")" + debug,
					// list);
				} // For loop

				// Save Reports

			} catch (Exception e) { // TODO Auto-generated catch block
				e.printStackTrace();
				RuleEngineLogger.generateLogs(clazz, e.getMessage(), Constants.rule_log_debug, bw);

			}

			//
		} finally {

			closeBuffer((BufferedWriter) o[1], (FileWriter) o[2]);
			if (!tempFname.equals("")) {
				// fName="";
				// String n = new Date().toString().trim().replaceAll(" ", "").replaceAll(":",
				// "-") + ".txt";
				File newFile = new File(appLogFolder + tempFname);
				File oldFile = new File(appLogFolder + fName);
				oldFile.renameTo(newFile);
				// Rename file
			}

		}
		// TODO Auto-generated method stub

		return returnMap;
	}

	@Override
	public Map<String, List<TPValidationResponseDto>> saveDisplayUserInputsOnly(TreatmentPlanValidationDto dtod) {
		dbAccesService.setUpSSLCertificates();
		Map<String, List<TPValidationResponseDto>> returnMap = null;
		//Date currentDate = new Date();
		String fName = "";
		BufferedWriter bw = null;
		Object[] o = null;
		String tempFname = "";
		Office off = od.getOfficeByUuid(dtod.getOfficeId());
		try {
			o = logFileToAppendData(off.getName().trim());
			fName = (String) o[0];
			bw = (BufferedWriter) o[1];

			RuleEngineLogger.generateLogs(clazz, "Entering saveDisplayUserInputsOnly..", Constants.rule_log_debug, bw);
			// RuleEngineLogger.generateLogs(clazz,
			// "RuleEngineValidationController", Constants.rule_log_debug,bw);
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			List<TPValidationResponseDto> list2 = new ArrayList<TPValidationResponseDto>();
			List<Rules> rules = tvd.getAllActiveRules();
			List<Mappings> mappings = tvd.getAllMappings();
			MicroSoftGraphToken microsoft = null;

			boolean eagleSoftDBAccessPresent=false;
			List<GoogleSheets> sheets = tvd.getAllGoogleSheetByOffice(off);
			EagleSoftDBDetails esDB = tvd.getESDBDetailsByOffice(off);
			if (esDB!=null) {
				eagleSoftDBAccessPresent=true;
			}
			List<OneDriveFile> fileOne = spd.getOneDriveFileByOfficeId(dtod.getOfficeId());
			TPValidationResponseDto dtoR = null;
			Map<String, List<Object>> ivfMap = null;
			Rules rule = null;
			OneDriveApp odriveApp = spd.getOneDriveAppDetailsByOfficeId(dtod.getOfficeId());
			if (eagleSoftDBAccessPresent ==false && (odriveApp != null && odriveApp.getRefreshToken() != null)) {

				// Generate New Access Token and Refresh Token.
				try {
					microsoft = spService.reGenerateAccAndRefreshTokens(odriveApp, odriveApp.getRefreshToken());
					spService.saveAccessAndRefreshTokenByAuthToken(odriveApp, dtod.getOfficeId(), null,
							microsoft.getAccess_token(), microsoft.getRefresh_token());

				} catch (IOException e) {
					// TODO Auto-generated catch block
					RuleEngineLogger.generateLogs(clazz, "Office 365 sheet not registered with Rule Engine",
							Constants.rule_log_debug, bw);
					dtoR = new TPValidationResponseDto(0, "Generic",
							"Office 365 sheet not registered with Rule Engine.<br>"
									+ "Please contact site Admin. or Click link <a target='_blank' " + "href='" + AppUrl
									+ SharepointInit + "?officeId=" + dtod.getOfficeId()
									+ "'> Register Office With App </a>",
							Constants.FAIL,"","","");
					list2.add(dtoR);
					if (returnMap == null)
						returnMap = new HashMap<String, List<TPValidationResponseDto>>();
					returnMap.put("no", list2);

					return returnMap;
				}

			} else if (eagleSoftDBAccessPresent==false){
				RuleEngineLogger.generateLogs(clazz, "Office 365 sheet not registered with Rule Engine",
						Constants.rule_log_debug, bw);
				dtoR = new TPValidationResponseDto(0, "Generic", "Office 365 sheet not registered with Rule Engine.<br>"
						+ "Please contact site Admin. or Click link <a target='_blank' " + "href='" + AppUrl
						+ SharepointInit + "?officeId=" + dtod.getOfficeId() + "'> Register Office With App </a>",
						Constants.FAIL,"","","");
				list2.add(dtoR);
				if (returnMap == null)
					returnMap = new HashMap<String, List<TPValidationResponseDto>>();
				returnMap.put("no", list2);

				return returnMap;

			}
			
			if (rules != null && rules.size() == 0) {
				RuleEngineLogger.generateLogs(clazz, "Generic- No Active Rules Found", Constants.rule_log_debug, bw);
				dtoR = new TPValidationResponseDto(0, "Generic", "Generic- No Active Rules Found", Constants.FAIL,"","","");
				list2.add(dtoR);
				if (returnMap == null)
					returnMap = new HashMap<String, List<TPValidationResponseDto>>();
				returnMap.put("No Rule Found", list2);

				return returnMap;
			}

			Object principal = authentication.getPrincipal();
			final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails)principal).getUsername());
			JwtUser user = (JwtUser) userDetails;
			int type =user.getUserType();
			try {
				
				Map<String, List<Object>> tMap=null;
				//Map<String, List<Object>> tMapReduced=null;
				//Map<String, List<EagleSoftEmployerMaster>> esempmaster = null;
				//Map<String, List<EagleSoftFeeShedule>> esfeess = null;
				
				
				String ivs[] = dtod.getIvfId().split(",");
				String trids[] = dtod.getTreatmentPlanId().split(",");
				// Read Treatment Plan...
				OneDriveFile tpsheet =null;
				GoogleSheets ivsheet = null;
				if (eagleSoftDBAccessPresent== false) {
					/*
					ReadMicrosoftFile rmF = new ReadMicrosoftFile();
					// Read Fee Treatment Plan key id IVF id ..

					RuleEngineLogger.generateLogs(clazz,
							Constants.rule_log_read_fil_start + "-" + Constants.microsoft_treatement_sheet_name,
							Constants.rule_log_debug, bw);
					tMap = (Map<String, List<Object>>) (Map<String, ?>) rmF
							.downloadAndReadUsingStream(spService.webUrlForMicrosoftSheets(tpsheet, odriveApp),
									tpsheet.getSheetName(), Constants.microsoft_treatement_sheet_name, trids, null, null,
									null);

					RuleEngineLogger.generateLogs(clazz,
							Constants.rule_log_read_fil_end + "-" + Constants.microsoft_treatement_sheet_name,
							Constants.rule_log_debug, bw);
					// End
					GoogleSheets ivsheet = null;
					if (sheets != null && sheets.size() > 0) {
						ivsheet = sheets.get(0);
					} else {
						rule = getRulesFromList(rules, Constants.RULE_ID_2);
						RuleEngineLogger.generateLogs(clazz, "No IVF Sheet Found for Selected Office" + off.getName(),
								Constants.rule_log_debug, bw);
						dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(),
								"<b class='error-message-api'>No IVF Sheet Found for Selected Office-.</b>" + off.getName(),
								Constants.FAIL);
						list2.add(dtoR);
						if (returnMap == null)
							returnMap = new HashMap<String, List<TPValidationResponseDto>>();
						returnMap.put(
								"IVF ID(" + dtod.getIvfId() + ") Treatment Plan ID (" + dtod.getTreatmentPlanId() + ")",
								list2);

						return returnMap;
					}
					// Read IVF Sheet From Google key is IVF id

					RuleEngineLogger.generateLogs(clazz,
							Constants.rule_log_read_fil_start + "-" + Constants.google_ivf_sheet, Constants.rule_log_debug,
							bw);
					ivfMap = ConnectAndReadSheets.readSheet(ivsheet.getSheetId(),
							off.getName() + " " + ivsheet.getAppSheetName(), ivs, CLIENT_SECRET_DIR, CREDENTIALS_FOLDER,
							off.getName(),false,true);

					RuleEngineLogger.generateLogs(clazz, Constants.rule_log_read_fil_end + "-" + Constants.google_ivf_sheet,
							Constants.rule_log_debug, bw);
					// End

					// Read emp_master Key is employee ID
					
					
	
					tpsheet =getOneDriveFileFromList(fileOne, Constants.microsoft_treatement_sheet_name);
					*/
				}else {
					
					//In New Approach 
					
					if (eagleSoftDBAccessPresent) {


						if (type==Constants.userType_TR)tMap=(Map<String, List<Object>>) (Map<String, ?>)dbAccesService.getTreatmentPlanData(trids, esDB,bw);
						//Phase 3 add query
						//tMapReduced=tMap;
						tMap=createCommonDataObject(tMap,authentication,false,null);//create Common Object
						//tMapReduced=createCommonDataObject(tMapReduced,authentication,true,dtod.getStatus());//create Common Object
                         //
						// Read IVF Sheet From Google key is IVF id
						if (sheets != null && sheets.size() > 0) {
							ivsheet = sheets.get(0);
						} else {
							rule = getRulesFromList(rules, Constants.RULE_ID_2);
							RuleEngineLogger.generateLogs(clazz, "No IVF Sheet Found for Selected Office" + off.getName(),
									Constants.rule_log_debug, bw);
							dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(),
									"<b class='error-message-api'>No IVF Sheet Found for Selected Office-.</b>" + off.getName(),
									Constants.FAIL,"","","");
							list2.add(dtoR);
							if (returnMap == null)
								returnMap = new HashMap<String, List<TPValidationResponseDto>>();
							returnMap.put(
									"IVF ID(" + dtod.getIvfId() + ") Treatment Plan ID (" + dtod.getTreatmentPlanId() + ")",
									list2);

							return returnMap;
						}

						RuleEngineLogger.generateLogs(clazz,
								Constants.rule_log_read_fil_start + "-" + Constants.google_ivf_sheet, Constants.rule_log_debug,
								bw);
						
						//
						Set<String> pid=null;
						Map<String,String> t_cl_pid=new HashMap<>();
						//if (tMap!=null && (type==Constants.userType_CL || && type==Constants.userType_TR) ) {
							pid= new  LinkedHashSet<>();
							
							for (Map.Entry<String,List<Object>> entry : tMap.entrySet()) {  
								
					            for (Object obj : entry.getValue()) {
									CommonDataCheck tp = (CommonDataCheck) obj;
									pid.add(tp.getPatient().getId());
									if (t_cl_pid.get(tp.getPatient().getId())==null)t_cl_pid.put(tp.getPatient().getId(),tp.getId());
								}
			
						}
						if (ivs!=null && dtod.getIvfId()!=null && dtod.getIvfId().length()>0) {
						if(esDB.getSheet()!=Constants.VALIDATE_FROM_SHEET) {
							ivfMap = ConnectAndReadSheets.readSheet(ivsheet.getSheetId(),
							off.getName() + " " + ivsheet.getAppSheetName(), ivs, CLIENT_SECRET_DIR, CREDENTIALS_FOLDER,
							off.getName(),false,true);
						}
						}else {
							//Write Query
					     }

						
					}		
			
				}
				
				
				// End
				List<TPValidationResponseDto> list = null;
				for (int y = 0; y < trids.length; y++) {
					list = new ArrayList<TPValidationResponseDto>();
					String TRAN_DATE="";
					String trx = trids[y];// off.getName() + "_" +
					String ivx = "";// off.getName() + "_" +
					CommonDataCheck tp = null;
					//String empMasterKey = "HYPE100sq";// Some hypo value.
					
					List<Object> tList = null;
					if (tMap == null) {
						rule = getRulesFromList(rules, Constants.RULE_ID_2);
						dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(),
								Constants.errorMessOPen + "Invalid Treatment Plan" + Constants.errorMessClose,
								Constants.FAIL,"","","");
						// saveReport(authentication, rule, dtoR, trx, null,off);
						list.add(dtoR);

					} else if (tMap != null && tMap.get(trx) == null) {
						rule = getRulesFromList(rules, Constants.RULE_ID_2);
						dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(),
								Constants.errorMessOPen + "Invalid Treatment Plan (claim)" + Constants.errorMessClose,
								Constants.FAIL,"","","");
						// saveReport(authentication, rule, dtoR, trx, null,off);
						list.add(dtoR);
					} else {
						tList = tMap.get(trx);
						// Create File Name to rename latter..
						tp = (CommonDataCheck) tList.get(0);
						//TRAN_DATE =tp.getCdDetails().getDateLastUpdated();
						if (y == 0)
							tempFname = tp.getPatient().getId();

					}
					
					if (ivs!=null && dtod.getIvfId()!=null && dtod.getIvfId().length()>0) {
						ivx = ivs[y];// off.getName() + "_" +
						
					}else {
						//ivs=dtod.getIvfId().split(",")
						if (tp!=null) { 
						TRAN_DATE= Constants.SIMPLE_DATE_FORMAT.format(new Date());
						String pid =tp.getPatient().getId();
						Map<String, List<Object>> ivfMapPat=null;
						if (esDB.getSheet()==Constants.VALIDATE_FROM_SHEET) {
							CaplineIVFQueryFormDto fd= new CaplineIVFQueryFormDto();
							fd.setPatientIdDB(pid);
							List<CaplineIVFFormDto> civfD=(List<CaplineIVFFormDto>) caplineIVFGoogleFormService.searchIVFData(fd, off);
							ivfMapPat= IVFFormConversionUtil.copyValueToIVFSheet(civfD, off);	
						}
						Object[] maps= DateUtils.selectOneKeyFromMapWithLatestDate(pid,ivsheet,CLIENT_SECRET_DIR,CREDENTIALS_FOLDER,off,TRAN_DATE,true,
								type,esDB.getSheet(),ivfMapPat);
						ivfMap = (Map<String, List<Object>>)maps[1];
						if (ivfMap==null && (Map<String, List<Object>>)maps[0]!=null) {//This means we have no result due to date issue.
						if 	(true) {//This means result was there but not valid date..
							List<TPValidationResponseDto> dtoRL = new ArrayList<>();
							String debug = "";
							ivfMap = (Map<String, List<Object>>)maps[0];
							for ( String key : ivfMap.keySet() ) {
								ivx=key;break;//only length one will be there ..
							   }
							dtoRL.add(new TPValidationResponseDto(1, "The Latest IV Was Not Found", messageSource
									.getMessage("rule.lastest.iv.issue.error", new Object[] { ((IVFTableSheet) ivfMap.get(ivx).get(0)).getGeneralDateIVwasDone() },
											locale),
									Constants.FAIL,"","",""));
							String inv=Constants.TP_ID;
							if (returnMap==null ) returnMap = new HashMap<String, List<TPValidationResponseDto>>();
								returnMap.put(inv+" - " + trx + " IV.id - " + ivx + " Of.Name -  " + off.getName()
										+ " PT.id - " + ((IVFTableSheet) ivfMap.get(ivx).get(0)).getPatientId() + " Pt.Name - "
										+ ((IVFTableSheet) ivfMap.get(ivx).get(0)).getPatientName() + debug, list);
							
							continue;

						  }
						//continue;
						}
						if (ivfMap!=null) {
						for ( String key : ivfMap.keySet() ) {
							ivx=key;break;//only length one will be there ..
						   }
						
					    }
						}
						
					}
					
					// IMPORTANT --SEE THIS --WHY DONE in IVF sheet need to match Officename_IVFID
					//String ivx = ivs[y];// off.getName() + "_" +
					//String patKey = off.getName() + "_" + ivx;
					if (ivfMap != null && ivfMap.get(ivx) != null && ivfMap.get(ivx).get(0) != null ) {

						saveUserInputs(authentication, rules, tList, ivfMap.get(ivx).get(0), list, off, mappings);

					} else {
						rule = getRulesFromList(rules, Constants.RULE_ID_2);
						dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(),
								"<b class='error-message-api'>No Data Found in IVF Sheet.</b>", Constants.FAIL,"","","");
						list.add(dtoR);
						// saveReports(authentication, rule,dtoR, dto,(IVFTableSheet)(ivfList.get(0)));
					}

					if (returnMap == null)
						returnMap = new HashMap<String, List<TPValidationResponseDto>>();
					
				} // For loop

				// Save Reports

			} catch (Exception e) { // TODO Auto-generated catch block
				e.printStackTrace();
				RuleEngineLogger.generateLogs(clazz, e.getMessage(), Constants.rule_log_debug, bw);

			}

			//
		} finally {

			closeBuffer((BufferedWriter) o[1], (FileWriter) o[2]);
			if (!tempFname.equals("")) {
				// fName="";
				// String n = new Date().toString().trim().replaceAll(" ", "").replaceAll(":",
				// "-") + ".txt";
				File newFile = new File(appLogFolder + tempFname);
				File oldFile = new File(appLogFolder + fName);
				oldFile.renameTo(newFile);
				// Rename file
			}

		}
		// TODO Auto-generated method stub

		return returnMap;
	}

	private Rules getRulesFromList(List<Rules> rules, String name) {
		Rules r = null;
		Collection<Rules> ruleGen = Collections2.filter(rules, rule -> rule.getShortName().equals(name));
		for (Rules rule : ruleGen) {
			r = rule;
		}

		return r;
	}

	private Rules getRulesFromListByid(List<Rules> rules, int id) {
		Rules r = null;
		Collection<Rules> ruleGen = Collections2.filter(rules, rule -> rule.getId() == id);
		for (Rules rule : ruleGen) {
			r = rule;
		}

		return r;
	}

	/*
	 * private GoogleSheets getGoogleSheetFromList(List<GoogleSheets> sheets, int
	 * id) { GoogleSheets s = null; Collection<GoogleSheets> ruleGen =
	 * Collections2.filter(sheets, sh -> sh.getId() == id); for (GoogleSheets gs :
	 * ruleGen) { s = gs; }
	 * 
	 * return s; }
	 */
	private OneDriveFile getOneDriveFileFromList(List<OneDriveFile> sheets, String type) {
		OneDriveFile s = null;
		Collection<OneDriveFile> ruleGen = Collections2.filter(sheets, sh -> sh.getSheetType().equals(type));
		for (OneDriveFile gs : ruleGen) {
			s = gs;
		}

		return s;
	}

	/*
	 * private void saveReport(Authentication authentication, Rules rules,
	 * TPValidationResponseDto dtoR, String treatmentPlanId, IVFTableSheet
	 * ivfSheet,Office off) { String email = "admin@admin.com"; User user = null;
	 * boolean update = false; if (authentication != null) { user =
	 * userDao.findUserAndOfficeByEmail(authentication.getName()); //
	 * Hibernate.initialize(user.getOffice()); } else { user =
	 * userDao.findUserByEmail(email); } // get Data from Reports by Treatment Plan
	 * ID..get Reports reports =
	 * tvd.getReportsByTreamentPlanIdAndOffice(treatmentPlanId,off); if (reports ==
	 * null) { reports = new Reports(); reports.setCreatedBy(user);
	 * reports.setGroupRun(1); if (ivfSheet != null) {
	 * reports.setPatientName(ivfSheet.getPatientName());
	 * reports.setPatientDob(ivfSheet.getPatientDOB()); }
	 * reports.setTreatementPlanId(treatmentPlanId);
	 * reports.setOffice(user.getOffice()); Serializable id =
	 * tvd.saveReports(reports); reports.setId((int) id); } else {
	 * reports.setUpdatedBy(user); reports.setGroupRun(reports.getGroupRun() + 1);
	 * tvd.updateReportDate(reports); } // save Report Details ReportDetail rd = new
	 * ReportDetail(); rd.setErrorMessage(dtoR.getMessage()); rd.setRules(rules);
	 * rd.setGroupRun(reports.getGroupRun()); rd.setCreatedBy(user);
	 * rd.setReports(reports); tvd.saveReportDestail(rd); // }
	 */
	private void saveReportsList(Authentication authentication, List<Rules> rules, CommonDataCheck tp,
			IVFTableSheet ivfSheet, List<TPValidationResponseDto> list, Office off,int userType) {
		//String[] p=env.getActiveProfiles();
		//int xx=0;
		//if (xx==0) return ;
		//if (p[0].equalsIgnoreCase("dev")) return;//Not need for report in Dev env.
		try {
			if (ivfSheet == null || tp == null)
				return;
			String email = "admin@admin.com";

			User user = null;
			if (authentication != null) {
				user = userDao.findUserByUsername(authentication.getName());
				// Hibernate.initialize(user.getOffice());
			} else {
				user = userDao.findUserByEmail(email);
			}

			if (userType==Constants.userType_TR){
			Reports reports = null;
			reports = tvd.getReportsByTPIdIVFIDAndOffice(tp.getId(), ivfSheet.getUniqueID().split("_")[1], off);
             
			if (reports == null) {
				reports = new Reports();
				reports.setCreatedBy(user);
				reports.setGroupRun(1);
				if (ivfSheet != null) {
					reports.setPatientName(ivfSheet.getPatientName());
					reports.setPatientDob(ivfSheet.getPatientDOB());
					if (ivfSheet.getUniqueID().split("_").length > 1)
						reports.setIvfFormId(ivfSheet.getUniqueID().split("_")[1]);
					else
						reports.setIvfFormId(ivfSheet.getUniqueID());
				}
				reports.setTreatementPlanId(tp.getId());
				reports.setOffice(off);
				reports.setPatientId(ivfSheet.getPatientId());
				Serializable id = tvd.saveReports(reports);
				reports.setId((int) id);
			} else {
				reports.setGroupRun(reports.getGroupRun() + 1);
				if (ivfSheet != null) {
					reports.setPatientName(ivfSheet.getPatientName());
					reports.setPatientDob(ivfSheet.getPatientDOB());
				}
				reports.setOffice(off);
				reports.setUpdatedBy(user);
				tvd.updateReportDate(reports);
			}
			// save Report Details
			ReportDetail rd = null;
			for (TPValidationResponseDto d : list) {
				rd = new ReportDetail();
				rd.setGroupRun(reports.getGroupRun());
				rd.setErrorMessage(d.getMessage());
				rd.setRules(getRulesFromListByid(rules, d.getRuleId()));
				rd.setCreatedBy(user);
				rd.setReports(reports);
				rd.setReportType(HighLevelReportTypeEnum.TXPLAN.getType());
				rd.setMessageType(MessageUtil.getReportMessageType(d.getMessage()));
				tvd.saveReportDestail(rd);
			}
			}else {
					ReportsClaim reports = null;
					reports = tvd.getReportsClaimByTPIdIVFIDAndOffice(tp.getId(), ivfSheet.getUniqueID().split("_")[1], off);
		             
					if (reports == null) {
						reports = new ReportsClaim();
						reports.setCreatedBy(user);
						reports.setGroupRun(1);
						if (ivfSheet != null) {
							reports.setPatientName(ivfSheet.getPatientName());
							reports.setPatientDob(ivfSheet.getPatientDOB());
							if (ivfSheet.getUniqueID().split("_").length > 1)
								reports.setIvfFormId(ivfSheet.getUniqueID().split("_")[1]);
							else
								reports.setIvfFormId(ivfSheet.getUniqueID());
						}
						reports.setClaimId(tp.getId());
						reports.setOffice(off);
						reports.setPatientId(ivfSheet.getPatientId());
						Serializable id = tvd.saveReports(reports);
						reports.setId((int) id);
					} else {
						reports.setGroupRun(reports.getGroupRun() + 1);
						if (ivfSheet != null) {
							reports.setPatientName(ivfSheet.getPatientName());
							reports.setPatientDob(ivfSheet.getPatientDOB());
						}
						reports.setOffice(off);
						reports.setUpdatedBy(user);
						tvd.updateReportDate(reports);
					}
					// save Report Details
					ReportClaimDetail rd = null;
					for (TPValidationResponseDto d : list) {
						rd = new ReportClaimDetail();
						rd.setGroupRun(reports.getGroupRun());
						rd.setErrorMessage(d.getMessage());
						rd.setRules(getRulesFromListByid(rules, d.getRuleId()));
						rd.setCreatedBy(user);
						rd.setReports(reports);
						rd.setReportType(HighLevelReportTypeEnum.CLAIM.getType());
						rd.setMessageType(MessageUtil.getReportMessageType(d.getMessage()));
						tvd.saveReportDestail(rd);
					}

			}
		} catch (Exception x) {

			x.printStackTrace();

		}
		//
	}

   /**
    * Call this just before Rules validation starts
    * @param authentication
    * @param rules
    * @param tp
    * @param ivfSheet
    * @param list
    * @param off
    * @param mappings
    */
	private void saveUserInputs(Authentication authentication, List<Rules> rules, List<Object> tpList,
			Object ivfSheet, List<TPValidationResponseDto> list, Office off,List<Mappings> mappings) {
		RuleBook rb =new RuleBook();
		List<UserInputRuleQuestionHeader> qhList=userInputQuestionDao.getAllUserInputQuestionsDbModel();
		IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
		int ctConsent=0;
		try {
			if (ivfSheet == null || tpList == null)
				return;
			String email = "admin@admin.com";

			User user = null;
			if (authentication != null) {
				user = userDao.findUserByUsername(authentication.getName());
				// Hibernate.initialize(user.getOffice());
			} else {
				user = userDao.findUserByEmail(email);
			}
			int check=0;
			for (Object obj : tpList) {
				CommonDataCheck tp = (CommonDataCheck) obj;
				
			
			UserInputDto dto = new UserInputDto();
			dto.setOfficeId(off.getUuid());
			dto.setTreatmentPlanId(tp.getId());
			List<QuestionAnswerDto> qansList=userInputQuestionDao.getUserAnswers(dto);
 			if (qansList!=null && qansList.size()>0 && check==0) {
 				break;
 			}	
 			check++;
				//for(QuestionAnswerDto d:qansList) {
			//		d.getAnswer();
			//	}
			//}else {
				//for null case
 			    List<String> orthoList= Arrays.asList( Constants.ORTHO_CODE_UI.split(","));
				 for (UserInputRuleQuestionHeader qh:qhList) {
					 
				
				   //For Rule 10 -- START --Attachment Required //X-Rays/Narratives/Perio Requirements
					 if (qh.getRuleName().equalsIgnoreCase(Constants.User_Input_Name_Question_Attachment_Required)) {
					Mappings mapA = rb.getMappingFromListAdditionalInformationNeeded(mappings, tp.getServiceCode());
					QuestionAnswerDto qadto= new QuestionAnswerDto();
					qadto.setAnswer("");
					if (tp.getServiceCode().equalsIgnoreCase("D0120")) continue;//This is a exceptional Case;
					if (qh.getId()==Constants.Attachment_Required_question_header_id_service_code) {
						
					   qadto.setAnswer(tp.getServiceCode());
					}
					else if (qh.getId()==Constants.Attachment_Required_question_header_id_toothno) 
						   qadto.setAnswer(tp.getTooth());
					else if (qh.getId()==Constants.Attachment_Required_question_header_id_require && mapA!=null) {
						   qadto.setAnswer(mapA.getAdditionalInformationNeeded());
					}else if (qh.getQuestionType().equalsIgnoreCase(Constants.QUESTION_TYPE)) {
						 qadto.setAnswer("No");
					 }
			           qadto.setIvfId(ivf.getUniqueID().split("_")[1]);
			           qadto.setOfficeId(off.getUuid());
			           qadto.setPatId(ivf.getPatientId());
			           qadto.setTpId(tp.getId());
					if (mapA != null) {
						//SAVE DATA HERE ...
						userInputQuestionDao.saveAndUpdateAnswers(qadto, off, qh,user,tp.getServiceCode());
						}
					 }
					//For Rule 10 -- END
					//For Rule Consent Form Requirements-- START Rule44
					 else  if (qh.getRuleName().equalsIgnoreCase(Constants.User_Input_Name_Question_Consent_Form_Requirements)) {
						 
						 //Exception case handled
						 if (ctConsent<4){//4 is no of questions
							 
							 QuestionAnswerDto qadto= new QuestionAnswerDto();
							 qadto.setAnswer("");
							 qadto.setIvfId(ivf.getUniqueID().split("_")[1]);
					         qadto.setOfficeId(off.getUuid());
					         qadto.setPatId(ivf.getPatientId());
					         qadto.setTpId(tp.getId());
					         if (qh.getId()==Constants.Consent_Form_Requirements_header_id_service_code) 
								   qadto.setAnswer("General");
							 else if (qh.getId()==Constants.Consent_Form_Requirements_header_id_tooth)  
								 qadto.setAnswer("");
							 else if (qh.getId()==Constants.Consent_Form_Requirements_header_id_Consent_Form_Name) {
								 qadto.setAnswer("General Consent");
							 }else if (qh.getId()==Constants.Consent_Form_Requirements_header_id_Is_Signed_Consent_Form_Available) {
								 qadto.setAnswer("No");
							 }
					          userInputQuestionDao.saveAndUpdateAnswers(qadto, off, qh,user,null);
					          ctConsent++;
						          	 
						 }
						 Mappings mapA = rb.getMappingFromListConsentNeeded(mappings, tp.getServiceCode());
						 QuestionAnswerDto qadto= new QuestionAnswerDto();
						 qadto.setAnswer("");
						 
						 if (qh.getId()==Constants.Consent_Form_Requirements_header_id_service_code) 
							   qadto.setAnswer(tp.getServiceCode());
						 else if (qh.getId()==Constants.Consent_Form_Requirements_header_id_tooth)  
							 qadto.setAnswer(tp.getTooth());
						 else if (qh.getId()==Constants.Consent_Form_Requirements_header_id_Consent_Form_Name && mapA!=null) {
							 qadto.setAnswer(mapA.getConsentNeeded());
						 }else if (qh.getId()==Constants.Consent_Form_Requirements_header_id_Is_Signed_Consent_Form_Available) {
							 qadto.setAnswer("No");
						 }
						 qadto.setIvfId(ivf.getUniqueID().split("_")[1]);
				         qadto.setOfficeId(off.getUuid());
				         qadto.setPatId(ivf.getPatientId());
				         qadto.setTpId(tp.getId());
				         if (mapA!=null) {
				         userInputQuestionDao.saveAndUpdateAnswers(qadto, off, qh,user,tp.getServiceCode());
				         }
					 }else  if (qh.getRuleName().equalsIgnoreCase(Constants.User_Input_Name_Question_Major_Service_Form_Requirements)) {//Rule50
						 
						 Mappings mapA = rb.getMappingFromListMajorServiceForm(mappings, tp.getServiceCode());
						 QuestionAnswerDto qadto= new QuestionAnswerDto();
						 qadto.setAnswer("");
						 
						 if (qh.getId()==Constants.Major_Service_Form_header_id_Service_code) 
							   qadto.setAnswer(tp.getServiceCode());
						 else if (qh.getId()==Constants.Major_Service_Form_header_id_tooth)  
							 qadto.setAnswer(tp.getTooth());
						 else if (qh.getId()==Constants.Major_Service_Form_header_id_Is_major_Available) {
							 qadto.setAnswer("No");
						 }
						 qadto.setIvfId(ivf.getUniqueID().split("_")[1]);
				         qadto.setOfficeId(off.getUuid());
				         qadto.setPatId(ivf.getPatientId());
				         qadto.setTpId(tp.getId());
				         if (mapA!=null) {
				         userInputQuestionDao.saveAndUpdateAnswers(qadto, off, qh,user,tp.getServiceCode());
				         }
					 }

					//For Rule Consent Form Requirements-- END
					 /*
					 else  if (qh.getRuleName().equalsIgnoreCase(Constants.User_Input_Name_Question_RULE_ORTHO)
							 && (orthoList.contains(tp.getServiceCode()))
							 ) {
						 QuestionAnswerDto qadto= new QuestionAnswerDto();
						 qadto.setAnswer("");
						 if (qh.getQuestionType().equalsIgnoreCase(Constants.QUESTION_TYPE)) {
							 qadto.setAnswer("No");
						 }
						  qadto.setIvfId(ivf.getUniqueID().split("_")[1]);
				         qadto.setOfficeId(off.getUuid());
				         qadto.setPatId(ivf.getPatientId());
				         qadto.setTpId(tp.getId());
				         userInputQuestionDao.saveAndUpdateAnswers(qadto, off, qh,user,null);
					 }
					 */
					 else  if (qh.getRuleName().equalsIgnoreCase(Constants.User_Input_Name_Question_Pre_Authorization) ) {//Rule 10 + 46
						 Mappings mapP = rb.getMappingFromListPreAuth(mappings, tp.getServiceCode());
						 QuestionAnswerDto qadto= new QuestionAnswerDto();
						 if (qh.getId()==Constants.Pre_Authorization_question_header_id_service_code) {
							 qadto.setAnswer(tp.getServiceCode());
							 
						 }else if (qh.getId()==Constants.Pre_Authorization_question_header_id_tooth) {
							 qadto.setAnswer(tp.getTooth());
							 
						 }else if (qh.getId()==Constants.Pre_Authorization_question_header_id_preauth_avail && mapP!=null) {
							 qadto.setAnswer("No");
							 
						 }else if (qh.getId()==Constants.Pre_Authorization_question_header_id_preauth_no) {
							 qadto.setAnswer("");
							 
						 }
							 
						  qadto.setIvfId(ivf.getUniqueID().split("_")[1]);
				         qadto.setOfficeId(off.getUuid());
				         qadto.setPatId(ivf.getPatientId());
				         qadto.setTpId(tp.getId());
				         if (mapP!=null) {
				        	 userInputQuestionDao.saveAndUpdateAnswers(qadto, off, qh,user,null);
				         }
					 }
					 else  if (ivf.getPlanType().trim().toLowerCase().contains(Constants.insurance_Medicaid) && 
							 qh.getRuleName().equalsIgnoreCase(Constants.User_Input_Name_Question_Provider_Change)) {//rule 47
						 QuestionAnswerDto qadto= new QuestionAnswerDto();
						 qadto.setAnswer("");
						 /*
						 if (qh.getQuestionType().equalsIgnoreCase(Constants.QUESTION_TYPE)) {
							 qadto.setAnswer("No");
						 }
						 */
						 if (qh.getId()==Constants.Provider_Change_question_header_id_provider_change) 
							   qadto.setAnswer("");
						 else if (qh.getId()==Constants.Provider_Change_question_header_id_patient_change_provider) 
							   qadto.setAnswer("No");
						 else if (qh.getId()==Constants.Provider_Change_question_header_id_provider) 
							   qadto.setAnswer(ivf.getProviderName());
							   
						 
					     qadto.setIvfId(ivf.getUniqueID().split("_")[1]);
				         qadto.setOfficeId(off.getUuid());
				         qadto.setPatId(ivf.getPatientId());
				         qadto.setTpId(tp.getId());
				         userInputQuestionDao.saveAndUpdateAnswers(qadto, off, qh,user,null);
					 }else  if ( qh.getRuleName().equalsIgnoreCase(Constants.User_Input_Name_Question_Comments)) {//no rule as such
						 QuestionAnswerDto qadto= new QuestionAnswerDto();
						 qadto.setAnswer(ivf.getComments());
						 qadto.setIvfId(ivf.getUniqueID().split("_")[1]);
				         qadto.setOfficeId(off.getUuid());
				         qadto.setPatId(ivf.getPatientId());
				         qadto.setTpId(tp.getId());
				         userInputQuestionDao.saveAndUpdateAnswers(qadto, off, qh,user,null);
					 }
					 /*else  if (tp.getServiceCode().equals("D7953") && qh.getRuleName().equalsIgnoreCase(Constants.User_Input_Name_Question_RULE_BONE_GRAFT)) {
						 QuestionAnswerDto qadto= new QuestionAnswerDto();
						 qadto.setAnswer("");
						 if (qh.getQuestionType().equalsIgnoreCase(Constants.QUESTION_TYPE)) {
							 qadto.setAnswer("No");
						 }
						  qadto.setIvfId(ivf.getUniqueID().split("_")[1]);
				         qadto.setOfficeId(off.getUuid());
				         qadto.setPatId(ivf.getPatientId());
				         qadto.setTpId(tp.getId());
				         userInputQuestionDao.saveAndUpdateAnswers(qadto, off, qh,user,null);
					 }
					 */
				 }
			 //}//else
			}
		} catch (Exception x) {

			x.printStackTrace();

		}
		//
	}
	
	private void saveReportsListBatch(Authentication authentication, List<Rules> rules, IVFTableSheet ivfSheet,
			List<TPValidationResponseDto> list, Office off) {
		
		//String[] p=env.getActiveProfiles();
		//if (p[0].equalsIgnoreCase("dev")) return;//Not need for report in Dev env.

		if (ivfSheet == null)
			return;
		String email = "admin@admin.com";
		try {
			User user = null;
			if (authentication != null) {
				user = userDao.findUserByUsername(authentication.getName());
				// Hibernate.initialize(user.getOffice());
			} else {
				user = userDao.findUserByEmail(email);
			}

			Reports reports = null;
			reports = tvd.getReportsByTPIdIVFIDAndOffice(Constants.prebatchmode, ivfSheet.getUniqueID().split("_")[1],
					off);

			// reports =
			// tvd.getReportsByIVFIdAndOffice(ivfSheet.getUniqueID().split("_")[1], off);
			// else reports = tvd.getReportsByTreamentPlanIdAndOffice(treatmentPlanId,off);
			// else reports = tvd.getReportsByTreamentPlanIdAndOffice(treatmentPlanId,off);
			// if (reports==null) reports =
			// tvd.getReportsByTreamentPlanIdAndOffice(treatmentPlanId,off);

			if (reports == null) {
				reports = new Reports();
				reports.setCreatedBy(user);
				reports.setTreatementPlanId(Constants.prebatchmode);
				reports.setGroupRun(1);
				if (ivfSheet != null) {
					reports.setPatientName(ivfSheet.getPatientName());
					reports.setPatientDob(ivfSheet.getPatientDOB());
					if (ivfSheet.getUniqueID().split("_").length > 1)
						reports.setIvfFormId(ivfSheet.getUniqueID().split("_")[1]);
					else
						reports.setIvfFormId(ivfSheet.getUniqueID());
				}
				reports.setOffice(off);
				reports.setPatientId(ivfSheet.getPatientId());
				Serializable id = tvd.saveReports(reports);
				reports.setId((int) id);
			} else {
				reports.setGroupRun(reports.getGroupRun() + 1);
				if (ivfSheet != null) {
					reports.setPatientName(ivfSheet.getPatientName());
					reports.setPatientDob(ivfSheet.getPatientDOB());
				}
				reports.setOffice(off);
				reports.setUpdatedBy(user);
				tvd.updateReportDate(reports);
			}
			// save Report Details
			ReportDetail rd = null;
			for (TPValidationResponseDto d : list) {
				rd = new ReportDetail();
				rd.setGroupRun(reports.getGroupRun());
				rd.setErrorMessage(d.getMessage());
				rd.setRules(getRulesFromListByid(rules, d.getRuleId()));
				rd.setCreatedBy(user);
				rd.setReports(reports);
				rd.setReportType(HighLevelReportTypeEnum.BATCH.getType());
				rd.setMessageType(MessageUtil.getReportMessageType(d.getMessage()));
				tvd.saveReportDestail(rd);
			}
		} catch (Exception x) {

			x.printStackTrace();

		}
	}
/*
	private List<String> getCodesfromTreamentPlanList(Map<String, List<Object>> tpMap) {
		if (tpMap == null)
			return null;
		List<String> li = new ArrayList<>();
		// using for-each loop for iteration over Map.entrySet()
		for (Map.Entry<String, List<Object>> entry : tpMap.entrySet()) {
			for (Object obj : entry.getValue()) {
				TreatmentPlan tp = (TreatmentPlan) obj;
				li.add(tp.getServiceCode());
			}
		}
		return li;
	}
*/
	@Override
	public Map<String, List<TPValidationResponseDto>> validateTreatmentPlanPreBatch(
			TreatmentPlanBatchValidationDto dto) {
		dbAccesService.setUpSSLCertificates();
//		System.setProperty("javax.net.ssl.trustStore", "c:/es/client/cacerts.jks");
//		System.setProperty("javax.net.ssl.keyStore", "c:/es/client/keystore.jks");
//		System.setProperty("javax.net.ssl.keyStorePassword", "p@ssw0rd");
		Map<String, List<TPValidationResponseDto>> returnMap = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		List<TPValidationResponseDto> list = new ArrayList<TPValidationResponseDto>();
		List<Rules> rules = tvd.getAllActiveRules();
		// List<Mappings> mappings = tvd.getAllMappings();
		//MicroSoftGraphToken microsoft = null;
		//ReadMicrosoftFile rmF = new ReadMicrosoftFile();

		Office off = od.getOfficeByUuid(dto.getOfficeId());
		List<GoogleSheets> sheets = tvd.getAllGoogleSheetByOffice(off);
		
		
		boolean eagleSoftDBAccessPresent=false;
		EagleSoftDBDetails esDB = tvd.getESDBDetailsByOffice(off);
		if (esDB!=null) {
		
			eagleSoftDBAccessPresent=true;
		}
		//List<OneDriveFile> fileOne = null;
		//if (eagleSoftDBAccessPresent==false) fileOne= spd.getOneDriveFileByOfficeId(dto.getOfficeId());
		TPValidationResponseDto dtoR = null;
		Map<String, List<Object>> ivfMap = null;

		// List<Object> ivfList = null;
		Rules rule = null;
		//OneDriveApp odriveApp = null;
		/*
		if (eagleSoftDBAccessPresent==false) odriveApp = spd.getOneDriveAppDetailsByOfficeId(dto.getOfficeId());
		if (eagleSoftDBAccessPresent ==false && (odriveApp != null && odriveApp.getRefreshToken() != null)) {

			// Generate New Access Token and Refresh Token.
			try {
				microsoft = spService.reGenerateAccAndRefreshTokens(odriveApp, odriveApp.getRefreshToken());
				spService.saveAccessAndRefreshTokenByAuthToken(odriveApp, dto.getOfficeId(), null,
						microsoft.getAccess_token(), microsoft.getRefresh_token());

			} catch (IOException e) {
				// TODO Auto-generated catch block
				dtoR = new TPValidationResponseDto(0, "Generic", "Office 365 sheet not registered with Rule Engine.<br>"
						+ "Please contact site Admin. or Click link <a target='_blank' " + "href='" + AppUrl
						+ SharepointInit + "?officeId=" + dto.getOfficeId() + "'> Register Office With App </a>",
						Constants.FAIL,"","","");
				list.add(dtoR);
				if (returnMap == null)
					returnMap = new HashMap<String, List<TPValidationResponseDto>>();
				returnMap.put("no", list);

				return returnMap;
			}

		} else if (eagleSoftDBAccessPresent==false) {
			dtoR = new TPValidationResponseDto(0, "Generic",
					"Office 365 sheet not registered with Rule Engine.<br>"
							+ "Please contact site Admin. or Click link <a target='_blank' " + "href='" + AppUrl
							+ SharepointInit + "?officeId=" + dto.getOfficeId() + "'> Register Office With App </a>",
					Constants.FAIL,"","","");
			list.add(dtoR);
			if (returnMap == null)
				returnMap = new HashMap<String, List<TPValidationResponseDto>>();
			returnMap.put("no", list);

			return returnMap;

		}
        */
		if (rules != null && rules.size() == 0) {
			dtoR = new TPValidationResponseDto(0, "Generic", "Generic- No Active Rules Found", Constants.FAIL,"","","");
			list.add(dtoR);
			if (returnMap == null)
				returnMap = new HashMap<String, List<TPValidationResponseDto>>();
			returnMap.put("no", list);

			return returnMap;
		}

		GoogleSheets ivsheet = null;// ;getGoogleSheetFromList(sheets, Constants.ivTableDataSheetID);
		if (sheets != null) {
			ivsheet = sheets.get(0);
		} else {
			rule = getRulesFromList(rules, Constants.RULE_ID_2);
			dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(),
					"<b class='error-message-api'>No IVF Sheet Found for Selected Office-.</b>" + off.getName(),
					Constants.FAIL,"","","");
			list.add(dtoR);
			if (returnMap == null)
				returnMap = new HashMap<String, List<TPValidationResponseDto>>();
			returnMap.put("no", list);
		}
		String id =null;
		boolean isPat=false;
		id= dto.getIvfId();// These will be Like 1,2,3,4,5 ..We need to Convert it like
		if (id==null) {
			id= dto.getPatientId();// These will be Like 1,2,3,4,5 ..We need to Convert it like
			isPat=true;	
		}
		String[] ids = id.split(",");

		try {
			
			Object principal = authentication.getPrincipal();
			final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails)principal).getUsername());
			JwtUser user = (JwtUser) userDetails;
			int type =user.getUserType();

			// READ IVF Google Sheet
			/// END
			// Read Patient Key is Unique Id from IVF sheet
			OneDriveFile espatient=null;
			Map<String, List<EagleSoftPatient>> espatients=null;
			
			//Map<String, List<EagleSoftPatientWalkHistory>> espatientsHis=null;
			
			Map<String, List<EagleSoftEmployerMaster>> esempmaster = null;
			
			if (eagleSoftDBAccessPresent==false) {
				/*
				ivfMap = ConnectAndReadSheets.readSheet(ivsheet.getSheetId(),
						off.getName() + " " + ivsheet.getAppSheetName(), ids, CLIENT_SECRET_DIR, CREDENTIALS_FOLDER,
						off.getName(),isPat,true);asdsad
			espatient = getOneDriveFileFromList(fileOne, Constants.microsoft_patient);
			// String[] treatmentPlanIds, List<String> codes, Map<String, List<Object>>
			// ivMap,
			// Map<String, List<Object>> patientsMap
			espatients = (Map<String, List<EagleSoftPatient>>) (Map<String, ?>) rmF
					.downloadAndReadUsingStream(spService.webUrlForMicrosoftSheets(espatient, odriveApp),
							espatient.getSheetName(), Constants.microsoft_patient, null, null, ivfMap, null);
			// End
			// Read emp_master Key is employee ID
			if (espatients != null && espatients.size() > 0) {
				OneDriveFile esfemp = getOneDriveFileFromList(fileOne, Constants.microsoft_emp_master);
				// if (tList != null) {
				esempmaster = (Map<String, List<EagleSoftEmployerMaster>>) (Map<String, ?>) rmF
						.downloadAndReadUsingStream(spService.webUrlForMicrosoftSheets(esfemp, odriveApp),
								esfemp.getSheetName(), Constants.microsoft_emp_master, null, null, null, espatients);
				// }
			 }
			 */
			}else {
			       // new Approach ....	
				
				
				if (eagleSoftDBAccessPresent) {
					//tMap=(Map<String, List<Object>>) (Map<String, ?>)dbAccesService.getTreatmentPlanData(trids, esDB,bw);
					
                     //
					// Read IVF Sheet From Google key is IVF id
					if (sheets != null && sheets.size() > 0) {
						ivsheet = sheets.get(0);
					} else {
						rule = getRulesFromList(rules, Constants.RULE_ID_2);
						RuleEngineLogger.generateLogs(clazz, "No IVF Sheet Found for Selected Office" + off.getName(),
								Constants.rule_log_debug, null);
						dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(),
								"<b class='error-message-api'>No IVF Sheet Found for Selected Office-.</b>" + off.getName(),
								Constants.FAIL,"","","");
						list.add(dtoR);
						if (returnMap == null)
							returnMap = new HashMap<String, List<TPValidationResponseDto>>();
						returnMap.put("no", list);

						return returnMap;
					}

					RuleEngineLogger.generateLogs(clazz,
							Constants.rule_log_read_fil_start + "-" + Constants.google_ivf_sheet, Constants.rule_log_debug,
							null);
					if(esDB.getSheet()!=Constants.VALIDATE_FROM_SHEET) {
						ivfMap = ConnectAndReadSheets.readSheet(ivsheet.getSheetId(),
							off.getName() + " " + ivsheet.getAppSheetName(), ids, CLIENT_SECRET_DIR, CREDENTIALS_FOLDER,
							off.getName(),isPat,false);
					}else {
						//write query
						CaplineIVFQueryFormDto fd= new CaplineIVFQueryFormDto();
						Set<String> set = new HashSet<>(Arrays.asList(ids));
						try {
						if (isPat) {
							
						
						List<CaplineIVFFormDto> civfD=(List<CaplineIVFFormDto>) caplineIVFGoogleFormService.searchIVFDataPat(fd, off,set);
						ivfMap= IVFFormConversionUtil.copyValueToIVFSheet(civfD, off);	
						}
						
						else {
							fd.setUniqueIDs(set);
							List<CaplineIVFFormDto> civfD=(List<CaplineIVFFormDto>) caplineIVFGoogleFormService.searchIVFData(fd, off);
							ivfMap= IVFFormConversionUtil.copyValueToIVFSheet(civfD, off);		
						 }
					   }
						catch(Exception x) {
							
					     }
				  }

					// Remove old IV's
					if (isPat && ivfMap!=null) {
						Set<IVFTableSheet> ps= new HashSet<>();
						for (Map.Entry<String, List<Object>> entry : ivfMap.entrySet()) {
							if (entry.getValue() != null) {

								IVFTableSheet ivfSheet = ((IVFTableSheet) entry.getValue().get(0));
								boolean added=true;
								IVFTableSheet rm=null;
								for(IVFTableSheet is:ps) {
									if (is.getPatientId().equals(ivfSheet.getPatientId())) {
										try { 
										if (Constants.SIMPLE_DATE_FORMAT_IVF.parse(is.getGeneralDateIVwasDone()).before(
												Constants.SIMPLE_DATE_FORMAT_IVF.parse(ivfSheet.getGeneralDateIVwasDone()))) {
											rm=is;
										}else {
											added=false;
										}
										}catch (Exception e) {
											// TODO: handle exception
										}
									}
									
								}
								
								if (added) ps.add(ivfSheet);
								if (rm!=null) ps.remove(rm);
								
								
							}
						}
						ivfMap.clear();
						for(IVFTableSheet isv:ps) {
							try { 
							if (Constants.SIMPLE_DATE_FORMAT_IVF.parse(isv.getGeneralDateIVwasDone()).before(new Date())
								|| Constants.SIMPLE_DATE_FORMAT_IVF.parse(isv.getGeneralDateIVwasDone()).equals(new Date())) {
							//if (true) {	
							List<Object> s=new ArrayList<>();
							s.add(isv);
							ivfMap.put(isv.getUniqueID().split("_")[1],s);
							}
							}catch (Exception e) {
								// TODO: handle exception
							}
						}
						
					}
					RuleEngineLogger.generateLogs(clazz, Constants.rule_log_read_fil_end + "-" + Constants.google_ivf_sheet,
							Constants.rule_log_debug, null);

					//
					espatients = (Map<String, List<EagleSoftPatient>>) (Map<String, ?>) dbAccesService.getPatientData(ivfMap, esDB,null);
					
					//espatientsHis= (Map<String, List<EagleSoftPatientWalkHistory>>) (Map<String, ?>) dbAccesService.getPatientHistoryES(ivfMap, esDB,
					//		new SimpleDateFormat(Constants.dateFormatStringESHis).format(new Date()),Constants.Medicaid_Provider_Limitation_MONTH,bw);

							
					if (espatients != null && espatients.size() > 0) {
						esempmaster = (Map<String, List<EagleSoftEmployerMaster>>) (Map<String, ?>) dbAccesService.getEmployeeMaster(espatients, esDB,null);
						//esfeess = (Map<String, List<EagleSoftFeeShedule>>) (Map<String, ?>) dbAccesService.getFeeScheduleData(espatients, esDB,bw);
                     }
				}		
		
			

				
				
			}
			if (isPat && ivfMap!=null) {
				//ids=null;
				List<String> x=new ArrayList<>();
				for (Map.Entry<String,List<Object>> entry : ivfMap.entrySet())  {
			
			 	IVFTableSheet ivf = (IVFTableSheet) entry.getValue().get(0);
			 	x.add(ivf.getUniqueID().split("_")[1]);
				}
				
				ids=x.toArray(new String[0]);
			}
			for (int y = 0; y < ids.length; y++) {
				list = new ArrayList<TPValidationResponseDto>();
				// IMPORTANT --SEE THIS --WHY DONE in IVF sheet need to match Officename_IVFID
				String ivx = ids[y];// off.getName() + "_" +
				String patKey = off.getName() + "_" + ivx;
				// String feeKey="1";
				String empMasterKey = "HYPE100sq";// Some hypo value.
				if (espatients != null && espatients.get(patKey) != null && espatients.get(patKey).get(0) != null) {
					empMasterKey = espatients.get(patKey).get(0).getEmployerId();
				}
				try {

					if (!(ivfMap != null && ivfMap.get(ivx) != null)) {

						rule = getRulesFromList(rules, Constants.RULE_ID_2);
						dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(),
								"<b class='error-message-api'>No Data Found in IVF Sheet.</b>", Constants.FAIL,"","","");
						list.add(dtoR);
						if (returnMap == null)
							returnMap = new HashMap<String, List<TPValidationResponseDto>>();
						returnMap.put("IVF ID -" + Constants.notFound + ivx, list);
						continue;
					}
					// RULE_ID_1 (Eligibility of the patient)
					rule = getRulesFromList(rules, Constants.RULE_ID_1);
					RuleBook rb = new RuleBook();
					List<TPValidationResponseDto> dtoRL1 = new ArrayList<>();
					boolean exit=false;
					if (type==Constants.userType_TR) {
					dtoRL1 = rb.Rule1(ivfMap.get(ivx).get(0), messageSource, rule, true, null,null, null,type,"");
					if (dtoRL1 != null) {
						list.addAll(dtoRL1);
						for (TPValidationResponseDto t : dtoRL1) {
							dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
									t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
							if (t.getResultType().equals(Constants.EXTI_ENGINE)) exit=true;
						}
					 }
					}
					//list.add(dtoR);
					//dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), dtoR.getMessage(),
					//		dtoR.getResultType());

					// END
					if (!exit) {
						List<TPValidationResponseDto> dtoRL = new ArrayList<>();

						if (espatients != null && espatients.size() > 0) {
							// RULE_ID_4 "Coverage Book"

							rule = getRulesFromList(rules, Constants.RULE_ID_4);

							dtoRL = rb.Rule4_A((IVFTableSheet) ivfMap.get(ivx).get(0), messageSource, rule,
									espatients.get(patKey), null);
							if (dtoRL != null) {
								list.addAll(dtoRL);
								
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}

							// END
							// RULE_ID_5 "Remaining Deductible, Remaining Balance and Benefit Max as per IV
							// form"
							if (type==Constants.userType_TR) {
							rule = getRulesFromList(rules, Constants.RULE_ID_5);
							dtoRL = rb.Rule5(ivfMap.get(ivx).get(0), messageSource, rule, espatients.get(patKey), null,type);
							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							}
							// END
							// RULE_ID_6 "Percentage Coverage Check"
							if (esempmaster != null) {
								rule = getRulesFromList(rules, Constants.RULE_ID_6);
								dtoRL = rb.Rule6(ivfMap.get(ivx).get(0), messageSource, rule,
										esempmaster.get(empMasterKey), espatients.get(patKey), null,type);
								if (dtoRL != null) {
									list.addAll(dtoRL);
									for (TPValidationResponseDto t : dtoRL) {
										dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
												t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
										// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
									}
								}
							} else {
								// Employer Not Found....
								dtoRL = rb.employerNotFound((IVFTableSheet) ivfMap.get(ivx).get(0), messageSource);
								list.addAll(dtoRL);
								// Not need to save report.. here
							}
							// END

						} else {
							dtoRL = rb.patientNotFound((IVFTableSheet) ivfMap.get(ivx).get(0), messageSource);
							list.addAll(dtoRL);
							// Not need to save report.. here
							// Patients not found...

						}
					}

				} catch (Exception e) {
					// TODO: handle exception
				}

				if (ivfMap != null && ivfMap.get(ivx) != null)
					saveReportsListBatch(authentication, rules, (IVFTableSheet) (ivfMap.get(ivx).get(0)), list, off);
				// else
				// saveReportsListBatch(authentication, rules, null, list,off);
				if (returnMap == null)
					returnMap = new HashMap<String, List<TPValidationResponseDto>>();
				int a = ivfMap.get(ivx).size();
				if (ivfMap.get(ivx) != null && ivfMap.get(ivx).size() > 0)
					returnMap.put(" IV.id - " + ivx + " Of.Name -  " + off.getName() + " PT.id - "
							+ ((IVFTableSheet) ivfMap.get(ivx).get(0)).getPatientId() + " Pt.Name - "
							+ ((IVFTableSheet) ivfMap.get(ivx).get(0)).getPatientName(), list);

				else if (ivx == null) {
					returnMap.put("IVF ID " + Constants.notFound + " - " + ivx, list);
				} else
					returnMap.put("IVF ID -" + ivx, list);
			} // For LOOP

		}

		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return returnMap;

	}

	@Override
	public Map<String, List<PatientTreamentDto>> getTreatments(TreatmentPlanDto dto) {
		dbAccesService.setUpSSLCertificates();
		List<PatientTreamentDto> list = null;
		Map<String, List<PatientTreamentDto>> map = null;
		PatientTreamentDto ptd = null;
		MicroSoftGraphToken microsoft = null;
		ReadMicrosoftFile rmF = new ReadMicrosoftFile();

		List<OneDriveFile> fileOne = spd.getOneDriveFileByOfficeId(dto.getOfficeId());
		OneDriveApp odriveApp = spd.getOneDriveAppDetailsByOfficeId(dto.getOfficeId());
		OneDriveFile tpsheet = getOneDriveFileFromList(fileOne, Constants.microsoft_treatement_sheet_name);
		String name = "";
		
		boolean eagleSoftDBAccessPresent=false;
		Office off = od.getOfficeByUuid(dto.getOfficeId());
		EagleSoftDBDetails esDB = tvd.getESDBDetailsByOffice(off);
		if (esDB!=null) {
		
			eagleSoftDBAccessPresent=true;
		}

		
		if (eagleSoftDBAccessPresent ==false && (odriveApp != null && odriveApp.getRefreshToken() != null)) {

			// Generate New Access Token and Refresh Token.
			try {
				microsoft = spService.reGenerateAccAndRefreshTokens(odriveApp, odriveApp.getRefreshToken());
				spService.saveAccessAndRefreshTokenByAuthToken(odriveApp, dto.getOfficeId(), null,
						microsoft.getAccess_token(), microsoft.getRefresh_token());

			} catch (IOException e) {
				// TODO Auto-generated catch block
				return null;
			}

		} else if(eagleSoftDBAccessPresent==true){

			//nothing for now...

		}else {
			return null;
		}

		try {
			// READ Treatment Plan
            if(eagleSoftDBAccessPresent==false) {
            /*	
			List<TreatmentPlan> tlist = rmF.downloadAndReadUsingStreamForTreatOnly(
					spService.webUrlForMicrosoftSheets(tpsheet, odriveApp), tpsheet.getSheetName(), dto.getPatientId());
			if (tlist != null) {
				for (TreatmentPlan tp : tlist) {
					if (list == null)
						list = new ArrayList<>();
					ptd = new PatientTreamentDto();
					name = tp.getPatient().getName() + " " + tp.getPatient().getLastName();
					ptd.settDescription(tp.getTreatmentPlanDetails().getDescription());
					ptd.setDateLastUpdated(tp.getTreatmentPlanDetails().getDateLastUpdated());
					ptd.setTreatmentPlanId(tp.getId());
					list.add(ptd);
				}
			}
            */
		}else {
			
			// new approach
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			Object principal = authentication.getPrincipal();
			final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails)principal).getUsername());
			JwtUser user = (JwtUser) userDetails;
			int type =user.getUserType();

			if (type==Constants.userType_TR)  {
			List<TreatmentPlan> tlist = dbAccesService.getTreatmentPlanDataByPatient(dto.getPatientId(), esDB, null);
			//Phase 3 change
			if (tlist != null) {
				for (TreatmentPlan tp : tlist) {
					if (list == null)
						list = new ArrayList<>();
					ptd = new PatientTreamentDto();
					name = tp.getPatient().getName() + " " + tp.getPatient().getLastName();
					ptd.settDescription(tp.getTreatmentPlanDetails().getDescription());
					ptd.setDateLastUpdated(tp.getTreatmentPlanDetails().getDateLastUpdated());
					ptd.setTreatmentPlanId(tp.getId());
					list.add(ptd);
				}
			}	
					
		 }else {
			 
				List<ClaimData> tlist = dbAccesService.getClaimDataByPatient(dto.getPatientId(), esDB, null);
				//Phase 3 change
				if (tlist != null) {
					for (ClaimData tp : tlist) {
						if (list == null)
							list = new ArrayList<>();
						ptd = new PatientTreamentDto();
						name = tp.getPatient().getName() + " " + tp.getPatient().getLastName();
						ptd.settDescription(tp.getDetails().getDescription());
						ptd.setDateLastUpdated(tp.getDetails().getDateLastUpdated());
						ptd.setTreatmentPlanId(tp.getId());
						if (tp.getId()!=null)list.add(ptd);
					}
				}	
			 
		 }
		 }   
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (list != null) {
			List<PatientTreamentDto> unique = list.stream()
					.collect(collectingAndThen(
							toCollection(() -> new TreeSet<>(comparing(PatientTreamentDto::getTreatmentPlanId))),
							ArrayList::new));
			map = new HashMap<>();
			map.put(name + " - " + dto.getPatientId(), unique);
		}
		
		return map;

	}
	
	private Map<String, List<Object>> createCommonDataObject(Map<String, List<Object>> tMap,Authentication authentication,boolean reduced,String status) {
		if (tMap!=null) {
			// using for-each loop for iteration over Map.entrySet() 
			Map<String, List<Object>> tMap1= new HashMap<>();
			
			Object principal = authentication.getPrincipal();
			final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails)principal).getUsername());
			JwtUser user = (JwtUser) userDetails;
			int type =user.getUserType();
			
			
	        for (Map.Entry<String,List<Object>> entry : tMap.entrySet())  {
	            System.out.println("Key = " + entry.getKey() + 
	                             ", Value = " + entry.getValue());
	            List<Object> l= (List<Object>)entry.getValue();
	            List<Object> x= new ArrayList<>();
	            for(Object oL:l) {
	            	if (type==1) {
						TreatmentPlan a=(TreatmentPlan)oL;
						TreatmentPlanDetails b=a.getTreatmentPlanDetails();
						TreatmentPlanPatient c=a.getPatient();
						CommonDataCheck cdc=new CommonDataCheck();
						
						CommonDataDetails cdd=new CommonDataDetails();
						cdd.setDateLastUpdated(b.getDateLastUpdated());
						cdd.setDescription(b.getDescription());
						cdd.setEstSecondary(b.getEstSecondary());
						cdd.setStatus(b.getStatus());
						
						CommonDataPatient cdp=new CommonDataPatient();
						cdp.setDob(c.getDob());
						cdp.setId(c.getId());
						cdp.setLastName(c.getLastName());
						cdp.setName(c.getName());
						
						cdc.setApptId(a.getApptId());
						cdc.setCdDetails(cdd);
						cdc.setDescription(a.getDescription());
						cdc.setEstInsurance(a.getEstInsurance());
						cdc.setEstPrimary(a.getEstPrimary());
						cdc.setFee(a.getFee());
						cdc.setId(a.getId());
						cdc.setLineItem(a.getLineItem());
						cdc.setPatient(cdp);
						cdc.setPatientPortion(a.getPatientPortion());
						cdc.setProviderLastName(a.getProviderLastName());
						cdc.setServiceCode(a.getServiceCode());
						if (a.getServiceCode()==null) cdc.setServiceCode("");
						cdc.setStatus(a.getStatus());
						cdc.setSurface(a.getSurface());
						cdc.setTooth(a.getTooth());
						cdc.setUserType(type);
						if (!reduced)x.add(cdc);
						else {
							//System.out.println("STATUS ---------------->>"+cdc.getStatus()+"----"+status);
							System.out.println("STATUS ---------------->>"+cdd.getStatus()+"----"+status);
						  if (status!=null) {	
						  if ("ALL".trim().toLowerCase().equals(status.trim().toLowerCase()) ||
							     "".equals(status.trim())) {
							  x.add(cdc);
						  }else if (cdd.getStatus().trim().toLowerCase().equals(status.trim().toLowerCase()) && 
								  StatusTypeEnum.post_to_walkout.getType().trim().toLowerCase().equals(status.trim().toLowerCase())) {
							  x.add(cdc);
						  }else if (cdd.getStatus().trim().toLowerCase().equals(status.trim().toLowerCase()) && 
								  StatusTypeEnum.accepted.getType().trim().toLowerCase().equals(status.trim().toLowerCase())) {
							  x.add(cdc);
						  }else if (cdd.getStatus().trim().toLowerCase().equals(status.trim().toLowerCase()) && 
								  StatusTypeEnum.complete.getType().trim().toLowerCase().equals(status.trim().toLowerCase())) {
							  x.add(cdc);
						  }else if (cdd.getStatus().trim().toLowerCase().equals(status.trim().toLowerCase()) && 
								  StatusTypeEnum.proposed.getType().trim().toLowerCase().equals(status.trim().toLowerCase())) {
							  x.add(cdc);
						  }else if (cdd.getStatus().trim().toLowerCase().equals(status.trim().toLowerCase()) && 
								  StatusTypeEnum.rejected.getType().trim().toLowerCase().equals(status.trim().toLowerCase())) {
							  x.add(cdc);
						  }else if (cdd.getStatus().trim().toLowerCase().equals(status.trim().toLowerCase()) && 
								  StatusTypeEnum.referred.getType().trim().toLowerCase().equals(status.trim().toLowerCase())) {
							  x.add(cdc);
						  }/*else if ("Others".trim().toLowerCase().equals(status.trim().toLowerCase()) &&
								  (
									cdd.getStatus().trim().toLowerCase().equals(StatusTypeEnum.complete.getType().trim().trim().toLowerCase()) ||
									cdd.getStatus().trim().toLowerCase().equals(StatusTypeEnum.accepted.getType().trim().toLowerCase())  ||
									cdd.getStatus().trim().toLowerCase().equals(StatusTypeEnum.proposed.getType().trim().toLowerCase())  ||
									cdd.getStatus().trim().toLowerCase().equals(StatusTypeEnum.rejected.getType().trim().toLowerCase())  ||
									cdd.getStatus().trim().toLowerCase().equals(StatusTypeEnum.referred.getType().trim().toLowerCase())
								   ) ) {
							  
							  x.add(cdc);
						   }*/
						  }else {
							  x.add(cdc);
						  }
						 }						
						}
	            	if (type==2) {
						ClaimData a=(ClaimData)oL;
						ClaimDataDetails b=a.getDetails();
						ClaimDataPatient c=a.getPatient();
						CommonDataCheck cdc=new CommonDataCheck();
						
						CommonDataDetails cdd=new CommonDataDetails();
						cdd.setDateLastUpdated(b.getDateLastUpdated());
						cdd.setDescription(b.getDescription());
						cdd.setEstSecondary(b.getEstSecondary());
						cdd.setStatus(b.getStatus());
						
						CommonDataPatient cdp=new CommonDataPatient();
						cdp.setDob(c.getDob());
						cdp.setId(c.getId());
						cdp.setLastName(c.getLastName());
						cdp.setName(c.getName());
						
						cdc.setApptId(a.getApptId());
						cdc.setCdDetails(cdd);
						cdc.setDescription(a.getDescription());
						cdc.setEstInsurance(a.getEstInsurance());
						cdc.setEstPrimary(a.getEstPrimary());
						cdc.setFee(a.getFee());
						cdc.setId(a.getId());
						cdc.setLineItem(a.getLineItem());
						cdc.setPatient(cdp);
						cdc.setPatientPortion(a.getPatientPortion());
						cdc.setProviderLastName(a.getProviderLastName());
						cdc.setServiceCode(a.getServiceCode());
						if (a.getServiceCode()==null) cdc.setServiceCode("");
						cdc.setStatus(a.getStatus());
						cdc.setSurface(a.getSurface());
						cdc.setTooth(a.getTooth());
						cdc.setUserType(type);
						if (!reduced)x.add(cdc);
						else {
						  if (status!=null) {	
						  if ("ALL".trim().toLowerCase().equals(status.trim().toLowerCase()) ||
							     "".equals(status.trim())) {
							  x.add(cdc);
						  }else if (cdd.getStatus().trim().toLowerCase().equals(status.trim().toLowerCase()) && 
								  StatusTypeEnum.post_to_walkout.getType().trim().toLowerCase().equals(status.trim().toLowerCase())) {
							  x.add(cdc);
						  }else if ("Others".trim().toLowerCase().equals(status.trim().toLowerCase()) &&
								  (
									cdd.getStatus().trim().toLowerCase().equals(StatusTypeEnum.complete.getType().trim().trim().toLowerCase()) ||
									cdd.getStatus().trim().toLowerCase().equals(StatusTypeEnum.accepted.getType().trim().toLowerCase())  ||
									cdd.getStatus().trim().toLowerCase().equals(StatusTypeEnum.proposed.getType().trim().toLowerCase())  ||
									cdd.getStatus().trim().toLowerCase().equals(StatusTypeEnum.rejected.getType().trim().toLowerCase())  ||
									cdd.getStatus().trim().toLowerCase().equals(StatusTypeEnum.referred.getType().trim().toLowerCase())
								 ) ) {
							  
							  x.add(cdc);
						   }
						  }else {
							  x.add(cdc);
						  }
						 						
						}
						
						}
	            }
	            tMap1.put(entry.getKey(), x);
	        }
	        tMap=tMap1;
		}
		return tMap;
	}

}
