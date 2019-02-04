package com.tricon.ruleengine.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.google.common.collect.Collections2;
import com.tricon.ruleengine.dao.OfficeDao;
import com.tricon.ruleengine.dao.SharePointDao;
import com.tricon.ruleengine.dao.TreatmentValidationDao;
import com.tricon.ruleengine.dao.UserDao;
import com.tricon.ruleengine.dto.MicroSoftGraphToken;
import com.tricon.ruleengine.dto.PatientTreamentDto;
import com.tricon.ruleengine.dto.QuestionAnswerDto;
import com.tricon.ruleengine.dto.QuestionHeaderDto;
import com.tricon.ruleengine.dto.TPValidationResponseDto;
import com.tricon.ruleengine.dto.TreatmentPlanBatchValidationDto;
import com.tricon.ruleengine.dto.TreatmentPlanDto;
import com.tricon.ruleengine.dto.TreatmentPlanValidationDto;
import com.tricon.ruleengine.dto.UserInputDto;
import com.tricon.ruleengine.logger.RuleEngineLogger;
import com.tricon.ruleengine.model.db.EagleSoftDBDetails;
import com.tricon.ruleengine.model.db.GoogleSheets;
import com.tricon.ruleengine.model.db.Mappings;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.OneDriveApp;
import com.tricon.ruleengine.model.db.OneDriveFile;
import com.tricon.ruleengine.model.db.ReportDetail;
import com.tricon.ruleengine.model.db.Reports;
import com.tricon.ruleengine.model.db.Rules;
import com.tricon.ruleengine.model.db.User;
import com.tricon.ruleengine.model.db.UserInputRuleQuestionHeader;
import com.tricon.ruleengine.model.sheet.EagleSoftEmployerMaster;
import com.tricon.ruleengine.model.sheet.EagleSoftFeeShedule;
import com.tricon.ruleengine.model.sheet.EagleSoftPatient;
import com.tricon.ruleengine.model.sheet.IVFTableSheet;
import com.tricon.ruleengine.model.sheet.TreatmentPlan;
import com.tricon.ruleengine.service.EagleSoftDBAccessService;
import com.tricon.ruleengine.service.EagleSoftDBService;
import com.tricon.ruleengine.service.SharePointService;
import com.tricon.ruleengine.service.TreatmentPlanService;
import com.tricon.ruleengine.utils.ConnectAndReadSheets;
import com.tricon.ruleengine.utils.Constants;
import com.tricon.ruleengine.utils.EagleSoftFetchData;
import com.tricon.ruleengine.utils.ReadMicrosoftFile;
import com.tricon.ruleengine.utils.RuleBook;
import com.tricon.ruleengine.dao.UserInputQuestionDao;
import static java.util.Comparator.comparingInt;
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

			RuleEngineLogger.generateLogs(clazz, "Entering To Validate Treatment Plan", Constants.rule_log_debug, bw);
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
			
			if (rules != null && rules.size() == 0) {
				RuleEngineLogger.generateLogs(clazz, "Generic- No Active Rules Found", Constants.rule_log_debug, bw);
				dtoR = new TPValidationResponseDto(0, "Generic", "Generic- No Active Rules Found", Constants.FAIL);
				list2.add(dtoR);
				if (returnMap == null)
					returnMap = new HashMap<String, List<TPValidationResponseDto>>();
				returnMap.put("No Rule Found", list2);

				return returnMap;
			}

			try {
				
				Map<String, List<EagleSoftPatient>> espatients=null;
				Map<String, List<Object>> tMap=null;
				Map<String, List<EagleSoftEmployerMaster>> esempmaster = null;
				Map<String, List<EagleSoftFeeShedule>> esfeess = null;
				
				
				String ivs[] = dtod.getIvfId().split(",");
				String trids[] = dtod.getTreatmentPlanId().split(",");
				// Read Treatment Plan...
				OneDriveFile tpsheet =null;
				if (eagleSoftDBAccessPresent== false) {
					
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
				}else {
					
					//In New Approach 
					
					if (eagleSoftDBAccessPresent) {
						tMap=(Map<String, List<Object>>) (Map<String, ?>)dbAccesService.getTreatmentPlanData(trids, esDB,bw);
						
                         //
						// Read IVF Sheet From Google key is IVF id
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

						RuleEngineLogger.generateLogs(clazz,
								Constants.rule_log_read_fil_start + "-" + Constants.google_ivf_sheet, Constants.rule_log_debug,
								bw);
						ivfMap = ConnectAndReadSheets.readSheet(ivsheet.getSheetId(),
								off.getName() + " " + ivsheet.getAppSheetName(), ivs, CLIENT_SECRET_DIR, CREDENTIALS_FOLDER,
								off.getName(),false);

						RuleEngineLogger.generateLogs(clazz, Constants.rule_log_read_fil_end + "-" + Constants.google_ivf_sheet,
								Constants.rule_log_debug, bw);

						//
						espatients = (Map<String, List<EagleSoftPatient>>) (Map<String, ?>) dbAccesService.getPatientData(ivfMap, esDB,bw);
								
						if (espatients != null && espatients.size() > 0) {
							esempmaster = (Map<String, List<EagleSoftEmployerMaster>>) (Map<String, ?>) dbAccesService.getEmployeeMaster(espatients, esDB,bw);
							esfeess = (Map<String, List<EagleSoftFeeShedule>>) (Map<String, ?>) dbAccesService.getFeeScheduleData(espatients, esDB,bw);
	                     }
					}		
			
				}
				
				
				// End
				List<TPValidationResponseDto> list = null;
				for (int y = 0; y < ivs.length; y++) {
					list = new ArrayList<TPValidationResponseDto>();
					// IMPORTANT --SEE THIS --WHY DONE in IVF sheet need to match Officename_IVFID
					String ivx = ivs[y];// off.getName() + "_" +
					String trx = trids[y];// off.getName() + "_" +
					String patKey = off.getName() + "_" + ivx;
					// String feeKey = "1";
					TreatmentPlan tp = null;
					String empMasterKey = "HYPE100sq";// Some hypo value.
					if (espatients != null && espatients.get(patKey) != null && espatients.get(patKey).get(0) != null) {
						empMasterKey = espatients.get(patKey).get(0).getEmployerId();
					}

					// Treatment Plan Sheet
					// Get web Url of TP sheet
					List<Object> tList = null;
					if (tMap == null) {
						rule = getRulesFromList(rules, Constants.RULE_ID_2);
						dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(),
								Constants.errorMessOPen + "Invalid Treatment Plan" + Constants.errorMessClose,
								Constants.FAIL);
						// saveReport(authentication, rule, dtoR, trx, null,off);
						list.add(dtoR);

					} else if (tMap != null && tMap.get(trx) == null) {
						rule = getRulesFromList(rules, Constants.RULE_ID_2);
						dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(),
								Constants.errorMessOPen + "Invalid Treatment Plan" + Constants.errorMessClose,
								Constants.FAIL);
						// saveReport(authentication, rule, dtoR, trx, null,off);
						list.add(dtoR);
					} else {
						tList = tMap.get(trx);
						// Create File Name to rename latter..
						tp = (TreatmentPlan) tList.get(0);
						if (y == 0)
							tempFname = tp.getPatient().getId();

					}

					if (ivfMap != null && ivfMap.get(ivx) != null && ivfMap.get(ivx).get(0) != null) {

						//Save User input first
						TreatmentPlan t1= new  TreatmentPlan();
						t1.setServiceCode("D2740");
						t1.setTooth("asds");
						t1.setId("TEST");
						
						saveUserInputs(authentication, rules, t1, ivfMap.get(ivx).get(0), list, off, mappings);
						
						TreatmentPlan t12= new  TreatmentPlan();
						t12.setServiceCode("D3320");
						t12.setTooth("asadds");
						t12.setId("TEST");
						//Phase 2 User Input Work.
						saveUserInputs(authentication, rules, t12, ivfMap.get(ivx).get(0), list, off, mappings);
						// RULE_ID_1 (Eligibility of the patient)

						rule = getRulesFromList(rules, Constants.RULE_ID_1);
						RuleBook rb = new RuleBook();
						dtoR = rb.Rule1(ivfMap.get(ivx).get(0), messageSource, rule, false, tList, bw);
						RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_1,
								Constants.rule_log_debug, bw);
						list.add(dtoR);
						dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), dtoR.getMessage(),
								dtoR.getResultType());

						if (!dtoR.getResultType().equals(Constants.EXTI_ENGINE)) {

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
								dtoRL = rb.Rule4_B(tList, ivfMap.get(ivx).get(0), messageSource, rule, mappings,
										esfeess.get(feeKey), espatients.get(patKey), bw);
							} else {
								if (esfeess == null)
									dtoRL.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
											messageSource.getMessage("rule.fee.notfound", new Object[] { "" }, locale),
											Constants.FAIL));
								else if (espatients == null)
									dtoRL.add(new TPValidationResponseDto(rule.getId(), rule.getName(), messageSource
											.getMessage("rule.patient.notfound", new Object[] { "Patient ID-"
													+ ((IVFTableSheet) (ivfMap.get(ivx).get(0))).getPatientId() },
													locale),
											Constants.FAIL));
								else
									dtoRL.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
											messageSource.getMessage("rule.patient.notfound",
													new Object[] { "Some Issue in Fee Sheet" }, locale),
											Constants.FAIL));

							}
							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_4,
									Constants.rule_log_debug, bw);

							// END

							// RULE_ID_5 "Remaining Deductible, Remaining Balance and Benefit Max as per IV
							// form"
							rule = getRulesFromList(rules, Constants.RULE_ID_5);
							dtoRL = new ArrayList<>();
							if (espatients != null) {
								dtoRL = rb.Rule5(ivfMap.get(ivx).get(0), messageSource, rule, espatients.get(patKey),
										bw);
								if (dtoRL != null) {
									list.addAll(dtoRL);
									for (TPValidationResponseDto t : dtoRL) {
										dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
												t.getResultType());
										// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
									}
								}
							} else {
								dtoRL.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
										messageSource.getMessage("rule.patient.notfound", new Object[] { "" }, locale),
										Constants.FAIL));

							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_5,
									Constants.rule_log_debug, bw);
							// END Remaining Deductible, Remaining Balance and Benefit Max as per IV

							// RULE_ID_6 "Percentage Coverage Check"
							rule = getRulesFromList(rules, Constants.RULE_ID_6);

							dtoRL = new ArrayList<>();
							if (espatients != null && esempmaster != null) {
								dtoRL = rb.Rule6(ivfMap.get(ivx).get(0), messageSource, rule,
										esempmaster.get(empMasterKey), espatients.get(patKey), bw);
								if (dtoRL != null) {
									list.addAll(dtoRL);
									for (TPValidationResponseDto t : dtoRL) {
										dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
												t.getResultType());
										// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
									}
								}
							} else if (espatients == null) {
								dtoRL.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
										messageSource.getMessage("rule.patient.notfound", new Object[] { "" }, locale),
										Constants.FAIL));
								list.addAll(dtoRL);

							} else {
								dtoRL.add(new TPValidationResponseDto(rule.getId(), rule.getName(), messageSource
										.getMessage("rule.employer.notfound", new Object[] { empMasterKey }, locale),
										Constants.FAIL));
								list.addAll(dtoRL);

							}

							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_6,
									Constants.rule_log_debug, bw);
							// END "Percentage Coverage Check"

							// RULE_ID_7 "Alert
							rule = getRulesFromList(rules, Constants.RULE_ID_7);
							dtoRL = rb.Rule7(ivfMap.get(ivx).get(0), messageSource, rule, bw);

							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_7,
									Constants.rule_log_debug, bw);

							// END Alert

							// RULE_ID_19 Downgrading

							rule = getRulesFromList(rules, Constants.RULE_ID_19);
							dtoRL = new ArrayList<>();
							if (esfeess != null && espatients != null) {
								dtoRL = rb.Rule19(tList, ivfMap.get(ivx).get(0), messageSource, rule, mappings,
										esfeess.get(feeKey), espatients.get(patKey), bw);

								if (dtoRL != null) {
									list.addAll(dtoRL);
									for (TPValidationResponseDto t : dtoRL) {
										dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
												t.getResultType());
										// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
									}
								}

							} else {

								RuleEngineLogger.generateLogs(clazz, Constants.RULE_ID_19 + "-rule.fee.notfound ",
										Constants.rule_log_debug, bw);

								if (esfeess == null)
									dtoRL.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
											messageSource.getMessage("rule.fee.notfound", new Object[] { "" }, locale),
											Constants.FAIL));
								if (espatients == null)
									dtoRL.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
											messageSource.getMessage("rule.patient.notfound",
													new Object[] { new Object[] {
															"Patient ID-" + ((IVFTableSheet) (ivfMap.get(ivx).get(0)))
																	.getPatientId() } },
													locale),
											Constants.FAIL));

								if (dtoRL != null) {
									list.addAll(dtoRL);
									for (TPValidationResponseDto t : dtoRL) {
										dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
												t.getResultType());
										// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
									}
								}
							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_19,
									Constants.rule_log_debug, bw);
							// END Downgrading

							// RULE_ID_21 "// Frequency Limitations
							rule = getRulesFromList(rules, Constants.RULE_ID_21);
							dtoRL = rb.Rule21(tList, ivfMap.get(ivx).get(0), messageSource, rule, mappings, bw);

							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_21,
									Constants.rule_log_debug, bw);

							// END Frequency Limitations

							// RULE_ID_8 "Age Limits
							rule = getRulesFromList(rules, Constants.RULE_ID_8);
							dtoRL = rb.Rule8(tList, ivfMap.get(ivx).get(0), messageSource, rule, bw);

							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_8,
									Constants.rule_log_debug, bw);

							// END Age Limits

							// RULE_ID_18 "Missing Tooth Clause
							rule = getRulesFromList(rules, Constants.RULE_ID_18);
							dtoRL = rb.Rule18(tList, ivfMap.get(ivx).get(0), messageSource, rule, mappings, bw);

							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_18,
									Constants.rule_log_debug, bw);

							// END Missing Tooth Clause

							// RULE_ID_17 " Bundling - Fillings
							rule = getRulesFromList(rules, Constants.RULE_ID_17);
							dtoRL = rb.Rule17(tList, ivfMap.get(ivx).get(0), messageSource, rule, bw);

							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_17,
									Constants.rule_log_debug, bw);

							// END

							// RULE_ID_16 " Bundling - X-Rays
							rule = getRulesFromList(rules, Constants.RULE_ID_16);
							dtoRL = rb.Rule16(tList, ivfMap.get(ivx).get(0), messageSource, rule, bw);

							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_16,
									Constants.rule_log_debug, bw);

							// END Bundling - X-Rays

							// RULE_ID_15 " SRP Quads Per Day
							rule = getRulesFromList(rules, Constants.RULE_ID_15);
							dtoRL = rb.Rule15(tList, ivfMap.get(ivx).get(0), messageSource, rule, bw);

							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_15,
									Constants.rule_log_debug, bw);

							// END SRP Quads Per Day

							// RULE_ID_14 "Sealants
							rule = getRulesFromList(rules, Constants.RULE_ID_14);
							dtoRL = rb.Rule14(tList, ivfMap.get(ivx).get(0), messageSource, rule, bw);

							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_14,
									Constants.rule_log_debug, bw);

							// END Sealants

							// RULE_ID_9 "Filling Codes based on Tooth No.

							rule = getRulesFromList(rules, Constants.RULE_ID_9);
							dtoRL = rb.Rule9(tList, messageSource, rule, mappings, bw);

							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_9,
									Constants.rule_log_debug, bw);

							// END "Filling Codes based on Tooth No.

							// RULE_ID_10 "Pre Auth
							rule = getRulesFromList(rules, Constants.RULE_ID_10);
							dtoRL = rb.Rule10(tList, messageSource, rule, mappings, bw);

							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_10,
									Constants.rule_log_debug, bw);

							// EN Pre Auth

							// RULE_ID_11 "Waiting Period.
							rule = getRulesFromList(rules, Constants.RULE_ID_11);
							dtoRL = rb.Rule11(tList, ivfMap.get(ivx).get(0), messageSource, rule, mappings, bw);

							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_11,
									Constants.rule_log_debug, bw);

							// END

							// RULE_ID_13 "Build-Ups & Crown Same Day
							rule = getRulesFromList(rules, Constants.RULE_ID_13);
							dtoRL = rb.Rule13(tList, ivfMap.get(ivx).get(0), messageSource, rule, bw);

							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_13,
									Constants.rule_log_debug, bw);

							// END "Build-Ups & Crown Same Day

							// RULE_ID_22 "CRA
							rule = getRulesFromList(rules, Constants.RULE_ID_22);
							dtoRL = rb.Rule22(tList, ivfMap.get(ivx).get(0), messageSource, rule, bw);

							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_22,
									Constants.rule_log_debug, bw);

							// END "CRA

							// RULE_ID_23 "Medicaid-1
							rule = getRulesFromList(rules, Constants.RULE_ID_23);
							dtoRL = rb.Rule23(tList, ivfMap.get(ivx).get(0),messageSource, rule, bw);

							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_23,
									Constants.rule_log_debug, bw);

							// END "Medicaid-1

							//  Medicaid-2 
							rule = getRulesFromList(rules, Constants.RULE_ID_24);
							dtoRL = rb.Rule24(tList, ivfMap.get(ivx).get(0),messageSource, rule,esfeess.get(feeKey), bw);

							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}
							RuleEngineLogger.generateLogs(clazz, Constants.rule_log_exit + "-" + Constants.RULE_ID_24,
									Constants.rule_log_debug, bw);

							// END  Medicaid-2

						}

					} else {
						rule = getRulesFromList(rules, Constants.RULE_ID_2);
						dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(),
								"<b class='error-message-api'>No Data Found in IVF Sheet.</b>", Constants.FAIL);
						list.add(dtoR);
						// saveReports(authentication, rule,dtoR, dto,(IVFTableSheet)(ivfList.get(0)));
					}
					if (ivfMap != null && ivfMap.get(ivx) != null && tList != null)
						saveReportsList(authentication, rules, tp, (IVFTableSheet) (ivfMap.get(ivx).get(0)), list, off);
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
					if (ivfMap == null && tList == null) {
						returnMap.put("TP.id " + Constants.notFound + " - " + trx + " IV.id  " + Constants.notFound
								+ " - " + ivx + " Of.Name -  " + off.getName(), list);

					} else if (ivfMap == null) {
						returnMap.put("TP.id - " + trx + " IV.id " + Constants.notFound + " - " + ivx + " Of.Name -  "
								+ off.getName(), list);

					} else if (tList == null) {
						returnMap.put("TP.id " + Constants.notFound + " - " + trx + " IV.id  - " + ivx + " Of.Name -  "
								+ off.getName(), list);

					} else {
						returnMap.put("TP.id - " + trx + " IV.id - " + ivx + " Of.Name -  " + off.getName()
								+ " PT.id - " + ((IVFTableSheet) ivfMap.get(ivx).get(0)).getPatientId() + " Pt.Name - "
								+ ((IVFTableSheet) ivfMap.get(ivx).get(0)).getPatientName() + debug, list);
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
	private void saveReportsList(Authentication authentication, List<Rules> rules, TreatmentPlan tp,
			IVFTableSheet ivfSheet, List<TPValidationResponseDto> list, Office off) {
		//int a=1;
		//if (a==1)return ;
		try {
			if (ivfSheet == null || tp == null)
				return;
			String email = "admin@admin.com";

			User user = null;
			if (authentication != null) {
				user = userDao.findUserAndOfficeByEmail(authentication.getName());
				// Hibernate.initialize(user.getOffice());
			} else {
				user = userDao.findUserByEmail(email);
			}

			Reports reports = null;
			reports = tvd.getReportsByTPIdIVFIDAndOffice(tp.getId(), ivfSheet.getUniqueID().split("_")[1], off);
			// else reports = tvd.getReportsByTreamentPlanIdAndOffice(treatmentPlanId,off);
			// else reports = tvd.getReportsByTreamentPlanIdAndOffice(treatmentPlanId,off);
			// if (reports==null) reports =
			// tvd.getReportsByTreamentPlanIdAndOffice(treatmentPlanId,off);

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
				tvd.saveReportDestail(rd);
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
	private void saveUserInputs(Authentication authentication, List<Rules> rules, TreatmentPlan tp,
			Object ivfSheet, List<TPValidationResponseDto> list, Office off,List<Mappings> mappings) {
		RuleBook rb =new RuleBook();
		List<UserInputRuleQuestionHeader> qhList=userInputQuestionDao.getAllUserInputQuestionsDbModel();
		IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
		try {
			if (ivfSheet == null || tp == null)
				return;
			String email = "admin@admin.com";

			User user = null;
			if (authentication != null) {
				user = userDao.findUserAndOfficeByEmail(authentication.getName());
				// Hibernate.initialize(user.getOffice());
			} else {
				user = userDao.findUserByEmail(email);
			}
			
			UserInputDto dto = new UserInputDto();
			dto.setOfficeId(off.getUuid());
			dto.setTreatmentPlanId(tp.getId());
			//List<QuestionAnswerDto> qansList=userInputQuestionDao.getUserAnswers(dto);
//			if (qansList!=null && qansList.size()>0) {
//				
//				for(QuestionAnswerDto d:qansList) {
//					d.getAnswer();
//				}
//			}else {
//				//for null case
				 for (UserInputRuleQuestionHeader qh:qhList) {
					 
				
				   //For Rule 10 -- START
					 if (qh.getRuleName().equalsIgnoreCase(Constants.User_Input_Name_Question_RULE_10)) {
					Mappings mapA = rb.getMappingFromListAdditionalInformationNeeded(mappings, tp.getServiceCode());
					Mappings mapP = rb.getMappingFromListPreAuth(mappings, tp.getServiceCode());
					QuestionAnswerDto qadto= new QuestionAnswerDto();
					qadto.setAnswer("");
					if (qh.getId()==Constants.RULE_10_question_header_id_checkpoints) 
					   qadto.setAnswer(tp.getServiceCode());
					else if (qh.getId()==Constants.RULE_10_question_header_id_toothno) 
						   qadto.setAnswer(tp.getTooth());
					else if (qh.getId()==Constants.RULE_10_question_header_id_require) 
						   qadto.setAnswer(mapA.getAdditionalInformationNeeded());
			           qadto.setIvfId(ivf.getUniqueID().split("_")[1]);
			           qadto.setOfficeId(off.getUuid());
			           qadto.setPatId(ivf.getPatientId());
			           qadto.setTpId(tp.getId());
					if (mapA != null) {
						//SAVE DATA HERE ...
						userInputQuestionDao.saveAndUpdateAnswers(qadto, off, qh,user,tp.getServiceCode());
						}
					if (mapP != null) {
						//SAVE DATA HERE ...
						userInputQuestionDao.saveAndUpdateAnswers(qadto, off, qh,user,tp.getServiceCode());
					}
					 }
					//For Rule 10 -- END
					//For Rule Overall-- START
					 else  if (qh.getRuleName().equalsIgnoreCase(Constants.User_Input_Name_Question_OVERALL)) {
						 QuestionAnswerDto qadto= new QuestionAnswerDto();
						 qadto.setAnswer("");
						 if (qh.getId()==Constants.RULE_OVERALL_question_header_id_checkpoints) 
							   qadto.setAnswer(qh.getHardCodedAnswer());
						 else if (qh.getId()==Constants.RULE_OVERALL_question_header_id_a_all_met)  
							 qadto.setAnswer("");
						 qadto.setIvfId(ivf.getUniqueID().split("_")[1]);
				         qadto.setOfficeId(off.getUuid());
				         qadto.setPatId(ivf.getPatientId());
				         qadto.setTpId(tp.getId());
				         userInputQuestionDao.saveAndUpdateAnswers(qadto, off, qh,user,null);
					 }
					//For Rule Overall-- END
					 else  if (qh.getRuleName().equalsIgnoreCase(Constants.User_Input_Name_Question_RULE_ORTHO)) {
						 QuestionAnswerDto qadto= new QuestionAnswerDto();
						 qadto.setAnswer("");
						  qadto.setIvfId(ivf.getUniqueID().split("_")[1]);
				         qadto.setOfficeId(off.getUuid());
				         qadto.setPatId(ivf.getPatientId());
				         qadto.setTpId(tp.getId());
				         userInputQuestionDao.saveAndUpdateAnswers(qadto, off, qh,user,null);
					 }
					 else  if (qh.getRuleName().equalsIgnoreCase(Constants.User_Input_Name_Question_RULE_PREAUTH)) {
						 QuestionAnswerDto qadto= new QuestionAnswerDto();
						 qadto.setAnswer("");
						  qadto.setIvfId(ivf.getUniqueID().split("_")[1]);
				         qadto.setOfficeId(off.getUuid());
				         qadto.setPatId(ivf.getPatientId());
				         qadto.setTpId(tp.getId());
				         userInputQuestionDao.saveAndUpdateAnswers(qadto, off, qh,user,null);
					 }
					 else  if (qh.getRuleName().equalsIgnoreCase(Constants.User_Input_Name_Question_RULE_PC)) {
						 QuestionAnswerDto qadto= new QuestionAnswerDto();
						 qadto.setAnswer("");
						  qadto.setIvfId(ivf.getUniqueID().split("_")[1]);
				         qadto.setOfficeId(off.getUuid());
				         qadto.setPatId(ivf.getPatientId());
				         qadto.setTpId(tp.getId());
				         userInputQuestionDao.saveAndUpdateAnswers(qadto, off, qh,user,null);
					 }
				 }
			//}

		} catch (Exception x) {

			x.printStackTrace();

		}
		//
	}
	
	private void saveReportsListBatch(Authentication authentication, List<Rules> rules, IVFTableSheet ivfSheet,
			List<TPValidationResponseDto> list, Office off) {
		if (ivfSheet == null)
			return;
		String email = "admin@admin.com";
		try {
			User user = null;
			if (authentication != null) {
				user = userDao.findUserAndOfficeByEmail(authentication.getName());
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
				tvd.saveReportDestail(rd);
			}
		} catch (Exception x) {

			x.printStackTrace();

		}
	}

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
		MicroSoftGraphToken microsoft = null;
		ReadMicrosoftFile rmF = new ReadMicrosoftFile();

		Office off = od.getOfficeByUuid(dto.getOfficeId());
		List<GoogleSheets> sheets = tvd.getAllGoogleSheetByOffice(off);
		
		
		boolean eagleSoftDBAccessPresent=false;
		EagleSoftDBDetails esDB = tvd.getESDBDetailsByOffice(off);
		if (esDB!=null) {
		
			eagleSoftDBAccessPresent=true;
		}
		List<OneDriveFile> fileOne = null;
		if (eagleSoftDBAccessPresent==false) fileOne= spd.getOneDriveFileByOfficeId(dto.getOfficeId());
		TPValidationResponseDto dtoR = null;
		Map<String, List<Object>> ivfMap = null;

		// List<Object> ivfList = null;
		Rules rule = null;
		OneDriveApp odriveApp = null;
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
						Constants.FAIL);
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
					Constants.FAIL);
			list.add(dtoR);
			if (returnMap == null)
				returnMap = new HashMap<String, List<TPValidationResponseDto>>();
			returnMap.put("no", list);

			return returnMap;

		}

		if (rules != null && rules.size() == 0) {
			dtoR = new TPValidationResponseDto(0, "Generic", "Generic- No Active Rules Found", Constants.FAIL);
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
					Constants.FAIL);
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
			// READ IVF Google Sheet
			/// END
			// Read Patient Key is Unique Id from IVF sheet
			OneDriveFile espatient=null;
			Map<String, List<EagleSoftPatient>> espatients=null;
			Map<String, List<EagleSoftEmployerMaster>> esempmaster = null;
			
			if (eagleSoftDBAccessPresent==false) {
				ivfMap = ConnectAndReadSheets.readSheet(ivsheet.getSheetId(),
						off.getName() + " " + ivsheet.getAppSheetName(), ids, CLIENT_SECRET_DIR, CREDENTIALS_FOLDER,
						off.getName(),isPat);
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
								Constants.FAIL);
						list.add(dtoR);
						if (returnMap == null)
							returnMap = new HashMap<String, List<TPValidationResponseDto>>();
						returnMap.put("no", list);

						return returnMap;
					}

					RuleEngineLogger.generateLogs(clazz,
							Constants.rule_log_read_fil_start + "-" + Constants.google_ivf_sheet, Constants.rule_log_debug,
							null);
					ivfMap = ConnectAndReadSheets.readSheet(ivsheet.getSheetId(),
							off.getName() + " " + ivsheet.getAppSheetName(), ids, CLIENT_SECRET_DIR, CREDENTIALS_FOLDER,
							off.getName(),isPat);

					RuleEngineLogger.generateLogs(clazz, Constants.rule_log_read_fil_end + "-" + Constants.google_ivf_sheet,
							Constants.rule_log_debug, null);

					//
					espatients = (Map<String, List<EagleSoftPatient>>) (Map<String, ?>) dbAccesService.getPatientData(ivfMap, esDB,null);
							
					if (espatients != null && espatients.size() > 0) {
						esempmaster = (Map<String, List<EagleSoftEmployerMaster>>) (Map<String, ?>) dbAccesService.getEmployeeMaster(espatients, esDB,null);
						//esfeess = (Map<String, List<EagleSoftFeeShedule>>) (Map<String, ?>) dbAccesService.getFeeScheduleData(espatients, esDB,bw);
                     }
				}		
		
			

				
				
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
								"<b class='error-message-api'>No Data Found in IVF Sheet.</b>", Constants.FAIL);
						list.add(dtoR);
						if (returnMap == null)
							returnMap = new HashMap<String, List<TPValidationResponseDto>>();
						returnMap.put("IVF ID -" + Constants.notFound + ivx, list);
						continue;
					}
					// RULE_ID_1 (Eligibility of the patient)
					rule = getRulesFromList(rules, Constants.RULE_ID_1);
					RuleBook rb = new RuleBook();
					dtoR = rb.Rule1(ivfMap.get(ivx).get(0), messageSource, rule, true, null, null);
					list.add(dtoR);
					dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), dtoR.getMessage(),
							dtoR.getResultType());

					// END
					if (!dtoR.getResultType().equals(Constants.EXTI_ENGINE)) {
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
											t.getResultType());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}

							// END
							// RULE_ID_5 "Remaining Deductible, Remaining Balance and Benefit Max as per IV
							// form"

							rule = getRulesFromList(rules, Constants.RULE_ID_5);
							dtoRL = rb.Rule5(ivfMap.get(ivx).get(0), messageSource, rule, espatients.get(patKey), null);
							if (dtoRL != null) {
								list.addAll(dtoRL);
								for (TPValidationResponseDto t : dtoRL) {
									dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
											t.getResultType());
									// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
								}
							}

							// END
							// RULE_ID_6 "Percentage Coverage Check"
							if (esempmaster != null) {
								rule = getRulesFromList(rules, Constants.RULE_ID_6);
								dtoRL = rb.Rule6(ivfMap.get(ivx).get(0), messageSource, rule,
										esempmaster.get(empMasterKey), espatients.get(patKey), null);
								if (dtoRL != null) {
									list.addAll(dtoRL);
									for (TPValidationResponseDto t : dtoRL) {
										dtoR = new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
												t.getResultType());
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

		}else {
			
			// new approach
			List<TreatmentPlan> tlist = dbAccesService.getTreatmentPlanDataByPatient(dto.getPatientId(), esDB, null);
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
					
		}
            
		}
		catch (IOException e) {
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

}
