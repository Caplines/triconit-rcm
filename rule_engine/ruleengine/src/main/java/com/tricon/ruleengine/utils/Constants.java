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
	public static final String RULE_ID_23="Medicaid-1";
	public static final String RULE_ID_24="Medicaid-2";
	public static final String RULE_ID_25="Medicaid-3";
	public static final String RULE_ID_26="Crown";
	public static final String RULE_ID_27="Filling";
	public static final String RULE_ID_28="Extraction";
	public static final String RULE_ID_29="Exam Codes";
	public static final String RULE_ID_30="Cleaning";
	public static final String RULE_ID_31="D4910";
	public static final String RULE_ID_32="Same Quad";
	public static final String RULE_ID_34="Post and Core";
	public static final String RULE_ID_35="Bone Graft/Alveoplasty";
	public static final String RULE_ID_36="D5130, D5140";
	public static final String RULE_ID_37="Extraction -2";
	public static final String RULE_ID_39="Age Limitation Prophylaxis_D1110/D1120";
	public static final String RULE_ID_40="Space Maintainer-Billateral_D1515";
		
	
	
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
	
	
	
	//Constants Related to Questions (user_input_rule_question_header) talbe;
	
	public static String User_Input_Name_Question_RULE_10="RULE_10";
	public static String User_Input_Name_Question_OVERALL="OVERALL";
	public static String User_Input_Name_Question_RULE_ORTHO="RULE_ORTHO";
	public static String User_Input_Name_Question_RULE_PREAUTH="RULE_PREAUTH";
	public static String User_Input_Name_Question_RULE_PC="RULE_PC";
	public static String User_Input_Name_Question_RULE_BONE_GRAFT="RULE_BONE_GRAFT";
	
	
	public static int RULE_10_question_header_id_checkpoints=1;
	public static int RULE_10_question_header_id_toothno=2;
	public static int RULE_10_question_header_id_require=3;
	public static int RULE_10_question_header_id_a_all_met=4;
	public static int RULE_10_question_header_id_notes_nar=5;
	
	public static int RULE_OVERALL_question_header_id_checkpoints=6;
	public static int RULE_OVERALL_question_header_id_a_all_met=7;
	
	public static int RULE_ORTHO_question_header_id_narrtive=8;
	public static int RULE_ORTHO_question_header_id_turation=9;
	public static int RULE_ORTHO_question_header_id_month_r=10;
	public static int RULE_ORTHO_question_header_id_downpayment=11;
	public static int RULE_ORTHO_question_header_id_banding_date=12;
	
	public static int RULE_PRE_AUTH_question_header_id_avail=13;
	public static int RULE_PRE_AUTH_question_header_id_refno=14;
	
	public static int RULE_PC_question_header_id_provider_change=15;
	public static int RULE_PC_question_header_id_patient_change_provier=16;
	public static int RULE_PC_question_header_id_ref_missing=17;
	
	public static int RULE_BONE_GRAFT_IMPLANT=18;
	
	


}
