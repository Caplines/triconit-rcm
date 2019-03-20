package com.tricon.ruleengine.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

public class Constants {
	
	/** The Constant BLANK. */
	public static final String BLANK = "";
	
	/** The Constant COLON. */
	public static final String COLON = " : ";
	
	/** The Constant DASH. */
	public static final String DASH = " - ";
	
	
	public static final String APP_ROLE_USER="ROLE_USER";
	
	public static final String admin_email="admin@admin.com";
	
	//Entry From Google Sheet table
	//public static final int treatmentPlanSheetID=1;
	/*
	public static final int ivTableDataSheetID=2;
	public static final int mappingSheetID_CM=6;
	public static final int mappingSheetID_FEE=7;
	public static final int eagleSoftCoverageSheetID=3;
	public static final int eagleSoftFSANDFEESheetID=4;
	public static final int eagleSoftFSNAMESheetID=5;
	public static final int eagleSoftRemDedBalSheetID=8;
    */
	
	//Entry from Rules Table
	public static final String RULE_ID_1="Eligibility of the patient";
	public static final String RULE_ID_2="IVF Details not matched";
	public static final String RULE_ID_3="Treatment Plan not valid";
	public static final String RULE_ID_4="Compare Coverage Book";
	public static final String RULE_ID_5="Remaining Deductible";
	public static final String RULE_ID_6="Percentage Coverage Check";
	public static final String RULE_ID_7="Alerts";
	public static final String RULE_ID_8="Age Limit";
	public static final String RULE_ID_9="Filling Codes based on Tooth No";
	public static final String RULE_ID_10="Pre-Auth";
	public static final String RULE_ID_11="Waiting Period Checks";
	public static final String RULE_ID_12="Deleted";//Deleted..
	public static final String RULE_ID_13="Build-Ups";
	public static final String RULE_ID_14="Sealants";
	public static final String RULE_ID_15="SRP Quads Per Day";
	public static final String RULE_ID_16="Bundling - X-Rays";
	public static final String RULE_ID_17="Bundling - Fillings";
	public static final String RULE_ID_18="Missing Tooth Clause";
	public static final String RULE_ID_19="Downgrading";
	public static final String RULE_ID_20="Do not use Reserved";
	public static final String RULE_ID_21="Frequency Limitations";
	public static final String RULE_ID_22="CRA";
	public static final String RULE_ID_23="Xray Bundling";
	public static final String RULE_ID_24="Filling Bundling";
	public static final String RULE_ID_25="Crown Bundling with Fillings";
	public static final String RULE_ID_26="Filling and Sealant Not Covered";
	public static final String RULE_ID_27="Sealant Not Covered";
	public static final String RULE_ID_28="Restoration Not Covered";
	public static final String RULE_ID_29="Exams Limitation";
	public static final String RULE_ID_30="Cleaning Limitation";
	public static final String RULE_ID_31="Perio Maintainance Clause";
	public static final String RULE_ID_32="SRP Limitation";
	public static final String RULE_ID_33="Root Canal Clause";
	public static final String RULE_ID_34="D2954 Clause";
	public static final String RULE_ID_35="Bone Graft Rule";
	public static final String RULE_ID_36="Immediate Denture";
	public static final String RULE_ID_37="Extraction Limitation";
	public static final String RULE_ID_38="Medicaid Provider Limitation";
	public static final String RULE_ID_39="Age Limitation Prophylaxis";
	public static final String RULE_ID_40="Space Maintainer-Billateral";
	public static final String RULE_ID_41="MVP VAP";
	public static final String RULE_ID_42="Duplicate TP Codes";
	public static final String RULE_ID_43="Bone Graft (User Input)";
	public static final String RULE_ID_44="Signed Consent Requirements";
	public static final String RULE_ID_45="Ortho (User Input)";
	public static final String RULE_ID_46="Pre-Authorization";
	public static final String RULE_ID_47="Provider Change";
	public static final String RULE_ID_48="Exam limitation for CHIP";
	public static final String RULE_ID_49="Sealant limitation in CHIP";
	public static final String RULE_ID_50="Major Service Form Requirements";
	public static final String RULE_ID_51="Provider is Same";
	public static final String RULE_ID_52="Provider is Different";
	
	
	
	/*
	public static final String SHEET_TYPE_TP="Treatmentplan";
	public static final String SHEET_TYPE_IVF_DATA="IVDATA";
	public static final String SHEET_TYPE_MappingTable_CM="MAPPING_CM";
	public static final String SHEET_TYPE_MappingTable_FEESN="MAPPING_FEESN";
	public static final String SHEET_TYPE_ES_COVERAGE="ES_COVERAGE";
	public static final String SHEET_TYPE_ES_FS_NAME="ES_FS_NAME";
	public static final String SHEET_TYPE_ES_FS_FEE="ES_FS_FEE";
	public static final String SHEET_TYPE_ES_REM_DED_BAL="ES_REM_DED_BAL";
	*/

	public static final SimpleDateFormat SIMPLE_DATE_FORMAT= new SimpleDateFormat("MM/dd/yyyy");
	public static final SimpleDateFormat SIMPLE_DATE_FORMAT_IVF= new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat SIMPLE_DATE_FORMAT_YEAR= new SimpleDateFormat("yyyy");
	
   public static final String FAIL = "FAIL";
   public static final String PASS = "PASS";
   public static final String DEBUG = "DEBUG";
   public static final String EXTI_ENGINE = "EXIT";
   
   public static final String NotApplicable = "Not Applicable";
   public static final String ALERT = "Alert";
   public static final String NotNeeded = "Not Needed";
   
   
   public static final String ONLY_MESSAGE = "MESSAGE";
   
	public static final String APP_AccessToken="code";
	public static final String APP_RereshToken="code";
	public static final String APP_AuthToken="code";
	
	
	public static final String authority = "https://login.microsoftonline.com";
	public static final String authorizeUrl = authority + "/common/oauth2/v2.0/authorize";
	public static final String authorizeUrlToken = authority + "/common/oauth2/v2.0/token";
	//public static final String authorizeUrlRefreshToken = authority + "/common/oauth2/v2.0/token";
	public static String[] scopes = { "offline_access", "files.read.all" };
	/// SEE ALL FILE https://graph.microsoft.com/v1.0/me/drive/root/children
	public static final String grapworkUrlDownload = "https://graph.microsoft.com/v1.0/me/drive/items/item-id";
			 //01SHWGTYMQ6OMDV434VZHY56IHIAWS2O5R/workbook/worksheets('All Patients')/range(address='a2:d4')?$select=values";
	//https://graph.microsoft.com/v1.0/me/drive/items/01RHW5ABI3AW6MCDMZDZDLP2AOXW3AMC55/workbook/worksheets('All Patients')/range(address='a1')?$select=values
	public static final String graphRangeUrl="https://graph.microsoft.com/v1.0/me/drive/items/--id--/workbook/worksheets('--name--')/usedRange?$select=values";
	
	//Create session
	public static final String grapworkUrlSession = "https://graph.microsoft.com/v1.0/me/drive/items/--id--/workbook/createSession";
	//https://graph.microsoft.com/v1.0/drives/.../workbook/createSession
	//
	
	
	
	public static String errorMessOPen="<b class='error-message-api'>";
	public static String errorMessClose="</b>";
	
	//public static String microsoft_coverage_sheet_name="Coverage";
	public static String microsoft_treatement_sheet_name="Treatment Plan";
	//public static String microsoft_fsname="FS Name";
	public static String microsoft_feeSchedule_master="Fee Schedule Master";
	public static String microsoft_emp_master="Employer Master";
	public static String microsoft_patient="Patient";
	public static String google_ivf_sheet="IVF Sheet";
	public static String insurance_Medicaid="medicaid";
	public static String insurance_Chip="chip";
	public static double insurance_Medicaid_max_fee=70.64;
	public static int history_codes_size=60;
	
	
	
	//public static String microsoft_Rem_bal_ded_max="Rem. Bal, Rem. Ded., Max  ";
	
	//Logs
	public static String rule_log_debug="DEBUG";
	public static String rule_log_enter="Entering the Rule --";
	public static String rule_log_exit="Exiting The Rule --";
	public static String rule_log_read_fil_start="Start Reading The File --";
	public static String rule_log_read_fil_end="Ending Reading The File -- ";
	
	
	public static String prebatchmode="PREBATCHMODE";
	
	
	public static NumberFormat formatter = new DecimalFormat("#0.00");
	
	
	public static String notFound="<b style='color:red' class='error-message-api'> NOT FOUND </b>";
	
	public static String socketworkingFine="Connection to office working fine. Agent is running successfully.";
	
	public static String socketnotworkingFine="Connection to office is <b style=\"color:red\" class=\"error-message-api\">not</b> working propertly. Make sure Agent is running / router is configured properly.";
	
	
    //Service Codes //Phase 2
	public static String EXTRACTION_SC="D7140,D7210,D7220,D7230,D7240,D7241,D7250";
	public static String CROWN_SC="D2740,D2750,D2790,D2791";
	public static String FILLING_AT_SC="D2330,D2331,D2332,D2335";
	public static String FILLING_PT_SC="D2391,D2392,D2393,D2394,P2391,P2392,P2393,P2394";
	public static String SEALANT_SC="D1351";
	public static String COMPLETE_DENTURE_SC=" D5110,D5120,D5130,D5140";
	public static String PARTIAL_DENTURE_SC="D5211,D5212,D5213,D5214,D5225,D5226";
	public static String DENTURE_SC="D5110,D5120,D5130,D5140";
	public static String STAIN_LESS_STEEL_CROWN_SC="D2930,D2931";
	
	public static String ORTHO_CODE_UI="D8080,D8070,D8090,D8010";
	
	
	//Constants Related to Questions (user_input_rule_question_header) table;
	
	public static String User_Input_Name_Question_Attachment_Required="X-Rays/Narratives/Perio Requirements";//X-Rays/Narratives/Perio Requirements
	//public static String User_Input_Name_Question_Forms_Required="Forms Required";
	//public static String User_Input_Name_Question_RULE_ORTHO="RULE_ORTHO";
	public static String User_Input_Name_Question_Pre_Authorization="Pre-Authorization Requirements";
	public static String User_Input_Name_Question_Provider_Change="Provider Change";
	public static String User_Input_Name_Question_Consent_Form_Requirements="Consent Form Requirements";
	//public static String User_Input_Name_Question_RULE_BONE_GRAFT="RULE_BONE_GRAFT";
	public static String User_Input_Name_Question_Comments="Comments in IV Form";
	public static String User_Input_Name_Question_Major_Service_Form_Requirements="Major Service Form Requirements";
	
	
	
	public static int Attachment_Required_question_header_id_service_code=1;
	public static int Attachment_Required_question_header_id_toothno=2;
	public static int Attachment_Required_question_header_id_require=3;
	public static int Attachment_Required_question_header_id_a_all_met=4;
	public static int Attachment_Required_question_header_id_notes_nar=5;
	
	//public static int Forms_Required_question_header_id_checkpoints=6;
	//public static int Forms_Required_question_header_id_a_all_met=7;
	
	public static int RULE_ORTHO_question_header_id_narrtive=8;
	public static int RULE_ORTHO_question_header_id_duration=9;
	public static int RULE_ORTHO_question_header_id_month_r=10;
	public static int RULE_ORTHO_question_header_id_downpayment=11;
	public static int RULE_ORTHO_question_header_id_banding_date=12;
	
	public static int Pre_Authorization_question_header_id_service_code=13;
	public static int Pre_Authorization_question_header_id_tooth=14;
	public static int Pre_Authorization_question_header_id_preauth_avail=21;
	public static int Pre_Authorization_question_header_id_preauth_no=22;
	
	public static int Provider_Change_question_header_id_provider_change=15;
	public static int Provider_Change_question_header_id_patient_change_provider=16;
	public static int Provider_Change_question_header_id_provider=20;
	
	public static int Consent_Form_Requirements_header_id_service_code=23;
	public static int Consent_Form_Requirements_header_id_tooth=24;
	public static int Consent_Form_Requirements_header_id_Consent_Form_Name=25;
	public static int Consent_Form_Requirements_header_id_Is_Signed_Consent_Form_Available=26;
	
	public static int Major_Service_Form_header_id_Service_code=27;
	public static int Major_Service_Form_header_id_tooth=28;
	public static int Major_Service_Form_header_id_Is_major_Available=29;
	
	
	//public static int Provider_Change_question_header_id_ref_missing=17;//Not used now
	
	public static int RULE_BONE_GRAFT_IMPLANT=18;
	
	public static int Comments_IN_IV_question_header_id=19;
	
	public static String QUESTION_TYPE="Y_N";
	
	public static String QUESTION_TYPE_FILL="FILL";
	
	

   //patient History check month in limitation rule
	public static int Medicaid_Provider_Limitation_MONTH=-36;
	public static String dateFormatStringESHis="yyyy/MM/dd";
	
	//Reports
	public static String EN_REP_TYPE_SEP="TYPE";
	public static String EN_REP_COUNT_SEP=";";
	
	
}
