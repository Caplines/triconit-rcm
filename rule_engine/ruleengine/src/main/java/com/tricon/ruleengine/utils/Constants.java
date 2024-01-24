package com.tricon.ruleengine.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

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
	public static final String COMPANY_NAME="Smilepoint";//This matches with Company Table  Name Column...  same in rcm TOO
	
	//Entry from Rules Table (These values should Match from Database..)
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
	public static final String RULE_ID_51="DQ Fillings (Provider Same)";//NOT USED NOW
	public static final String RULE_ID_52="DQ Fillings (Provider Different)";////NOT USED NOW
	public static final String RULE_ID_53="DQ Fillings";
	public static final String RULE_ID_54="FMX/Pano Rule";
	public static final String RULE_ID_55="Perio Depth Checker";
	public static final String RULE_ID_56="Exception Rule";//This is from Sheet https://docs.google.com/spreadsheets/d/1r_9il1-9p5xfPNBhSIRTZNNqFLPH2EKKdtj1eOo1rDs/edit?skip_itp2_check=true&pli=1#gid=0
	public static final String RULE_ID_57="Exam Frequency Limitation";
	public static final String RULE_ID_58="Exam Frequency Limitation (D0145)";
	public static final String RULE_ID_59="BWX Age Limitation";
	public static final String RULE_ID_60="Exams Age Limitation";
	public static final String RULE_ID_61="Humana Exception";
	public static final String RULE_ID_62="FCL Dental Exception";
	public static final String RULE_ID_63="Service not Covered (Chip)";
	public static final String RULE_ID_64="IntraOral Periapical";
	public static final String RULE_ID_65="Nitrous Oxide";
	public static final String RULE_ID_66="COB Primary";
	public static final String RULE_ID_67="Percentage coverage check(OS)";//Only for OS FORM..z
	
	//68 to 73c is common in error_en.properties
	public static final String RULE_ID_68="Sealant Eligibility Frequency";//Only Sealants .. 
	public static final String RULE_ID_69="Sealant Eligibility( Tooth Eligible)";//Only Sealants .. 
	public static final String RULE_ID_70="Sealant Eligibility( Tooth not Eligible)";//Only Sealants .. 
	public static final String RULE_ID_71="Sealant Eligibility( Tooth not Eligible Age)";//Only Sealants .. 
	public static final String RULE_ID_72="Sealant Eligibility( Tooth not Eligible Freq)";//Only Sealants .. 
	public static final String RULE_ID_73="Sealant Eligibility( patient)";//Only Sealants .. 
	
	public static final String RULE_ID_74="Waiting Period Checks(OS)";//Only for OS FORM..
	public static final String RULE_ID_75="Bridge Clause";
	

	public static final String RULE_ID_76="Patient Name";
	public static final String RULE_ID_77="Patient DOB";
	public static final String RULE_ID_78="Member ID";
	public static final String RULE_ID_79="Insurance and Address";
	public static final String RULE_ID_80="Denture validation 1";
	public static final String RULE_ID_81="Denture validation 2";
	public static final String RULE_ID_82="Provider certification status";
	public static final String RULE_ID_83="Policy Holder Match";
	
	public static final String RULE_ID_84="Provider Name";
	public static final String RULE_ID_85="Perio Maintenance with Prophy and Fluoride";
	public static final String RULE_ID_86="Oral hygiene with Prophy and Fluoride";
	public static final String RULE_ID_87="Provider Certification";
	public static final String RULE_ID_88="D0140 with Treatment";
	public static final String RULE_ID_89="D0140 with D0220";
	public static final String RULE_ID_90="Schedule Charges";
	public static final String RULE_ID_91="Immediate Dentures with Extraction";
	public static final String RULE_ID_92="Complete Denture with Extraction";
	public static final String RULE_ID_93="Prophy, Sealants and Fluoride on same DOS with D0140";
	public static final String RULE_ID_94="Sealant Age limitation";
	public static final String RULE_ID_95="Space Maintainer age and Frq limitation";
	public static final String RULE_ID_96="Recementation Frq Limit D1551";
	public static final String RULE_ID_97="Recementation Frq Limit D1552";
	public static final String RULE_ID_98="Recementation Frq Limit D1553";
	public static final String RULE_ID_99="Space Maintainer Tooth Compatibility for Quads";
	public static final String RULE_ID_100="Space Maintainer Tooth Compatibility for Arch";
	public static final String RULE_ID_101="Unspecified periodontal procedure - D4999";
	public static final String RULE_ID_102="FCL Dental Plan";//? DXXX
	public static final String RULE_ID_103="Distal Shoe Space Maintainer - Fixed - Unilateral Age Limitation";
	public static final String RULE_ID_104="Codes not covered in IV";
	public static final String RULE_ID_105="IV Comments";
	public static final String RULE_ID_106="Codes Compatible with Arch";
	public static final String RULE_ID_107="Codes compatible with quads";
	public static final String RULE_ID_108="Ortho treatment not given";
	
	//Rules now also used in RCM TOOL..take care of Ids. RCM Starts From 300
	

	//public static final String RULE_ID_79="Provider Name";	
	
	
	
	
	
	
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
	//public static final SimpleDateFormat SIMPLE_DATE_FORMAT_DB= new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat SIMPLE_DATE_FORMAT_YEAR= new SimpleDateFormat("yyyy");
	public static final SimpleDateFormat SIMPLE_DATE_FORMAT_HEADER= new SimpleDateFormat("MMM dd,yyyy");
	
	
   public static final String FAIL = "FAIL";
   public static final int FAIL_MESSAGE_TYPE = 1;
   
   public static final String PASS = "PASS";
   public static final String DEBUG = "DEBUG";
   public static final String EXTI_ENGINE = "EXIT";
   
   public static final String Digitization_of_RE_Results_SpreadSheeId= "1PSzfq1J7ajKWwM9Y7uUsLQ2hPWB0_f8mMs16IF9R69Q";
   public static final int Digitization_of_RE_Results_TP= 0;
   public static final int Digitization_of_RE_Results_Cl = 634516941;
   public static final int Digitization_of_RE_Results_pat = 2006499654;
   
   
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
	public static String adult_Medicaid="Adult Medicaid";
	
	public static String insurance_Mcna="mcna";
	public static String insurance_PPO="ppo";
	public static String insurance_Medicare="medicare";
	public static String insurance_Delta_Dental="delta dental";
	public static String insurance_guardian="guardian";
	public static String insurance_denta_quest="dentaquest";
	public static String insurance_BCBS="bcbs";
	public static String insurance_Humana="humana";
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
	public static String sealanthmode="SEALANTMODE";
	
	
	public static NumberFormat formatter = new DecimalFormat("#0.00");
	
	
	public static String notFound="<b style='color:red' class='error-message-api'> NOT FOUND </b>";
	
	public static String socketworkingFine="Connection to office working fine. Agent is running successfully.";
	
	public static String socketnotworkingFine="Connection to office is <b style=\"color:red\" class=\"error-message-api\">not</b> working propertly. Make sure Agent is running / router is configured properly.";
	
	
    //Service Codes //Phase 2
	public static String EXTRACTION_SC="D7140,D7210,D7220,D7230,D7240,D7241,D7250";
	public static String CROWN_SC="D2740,D2750,D2790,D2791";
	public static String FILLING_AT_SC="D2330,D2331,D2332,D2335";
	public static String FILLING_PT_SC="D2391,D2392,D2393,D2394,P2391,P2392,P2393,P2394";
	public static String FILLING_MM_SC="M2391,M2392,M2393M2394";

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
	public static String User_Input_Name_Question_Perio_Depth_Checker="Perio Depth Checker";
	
	
	
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
	public static int Perio_Depth_Checker=30;
	
	
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
	
	public static String DQ_PLAN_NAME_CHECK= "DQ Chip,DQ Medicaid,MCNA Chip,MCNA Medicaid,MCNA - MEDICAID," + 
			                                 "DENTAQUEST - MEDICAID,MCNA - CHIP,DENTAQUEST - CHIP,DENTAQUEST,MCNA";
	
	public static int userType_TR=1;//for Treatment
	
	public static int userType_CL=2;//for claim
	
	public static String TP_ID="TP.id";
	public static String CL_ID="CL.id";
	
	
	public static String invalidStr_TP="Invalid Treatment Plan";
	
	public static String invalidStr_Cl="Invalid Claim";
	
	public static String TP="Treatment Plan";
	
	public static String CL="Claim";
	
	
	public static String PATH_SEPERATOR_XML_IVF ="======!!!!======";
	
	//0 == Validate from RDBMS 1= Validdate from Google Sheet		
	public static int VALIDATE_FROM_SHEET=0; 
	//public static int VALIDATE_FROM_RDBMS=1;
	
	//public static String INSURANCE_TPYE_IVF_PRIMARY="primary";
	//public static String INSURANCE_TPYE_IVF_SECONDARY="secondary";
	
	public static String SCRAPPING_MANDATORY_WARNING="MAND.DAT.MISS";
	public static String SCRAPPING_NOT_FOUND="NOTFOUND";
	public static String SCRAPPING_ISSUE_FETCHING="ISS.FETCH";
	public static String SCRAPPING_ISSUE_FETCHING_CODE="CODE_ISSUE";
	public static String SCRAPPING_MAIN_CONDTION_MET="MAIN_CON_MET";
	
	public static final  String NO_FREQUENCY="No Frequency";
	
	public static final  String PATIENT_FOUND="Patient found";
	
	public static final  String PATIENT_NOT_ACTIVE="Patient not active";
		
	public static final String INSURANCE_TYPE_PRI="Primary";//Blank also means primary 
	public static final String INSURANCE_TYPE_SEC="Secondary"; 
	
	public static final int ATTEMPT_TO_ADD_NEW_COLUMNS=50;
	
	
	public static String IV_GENERAL_FORM_NAME="General form";
	public static int IV_GENERAL_FORM_NAME_ID=1;
	public static String IV_ORAL_SURGERY_FORM_NAME="Oral Surgery form";
	public static int IV_ORAL_SURGERY_FORM_NAME_ID=2;
	
	public static String IV_ORTHO_FORM_NAME="Ortho";
	public static int IV_ORTHO_FORM_NAME_ID=3;
	
	
	public static String OfficeNeedToTakeInfo_o="OfficeNeedToTakeInfo";
	public static String OfficeNeedToTakeInfo_r="Office Need To Take Info";
	
	
	public static String REMOTE_LITE_SITE_NAME="Remote Lite";
	
	public static final String QUERY_FOR_Reconcillation="Production Reconcillation";
	public static final String QUERY_FOR_DTP_PlannedServices="DTP_Planned Services";
	public static final String QUERY_FOR_DTP_Treatmentplans="DTP_Treatment plans";
	public static final String QUERY_FOR_DTP_Appointment ="DTP_Appointment";
	public static final String QUERY_FOR_ItemizedCash="Itemized Cash";
	public static final String NO_DATA_FOUND="No Data Found";
	public static final String OFFICE_NAME_INCORRECT="OfficeName or Password is Incorrect";
	public static final String DATE_PARSING_EXCEPTION="Error while Date Parsing or Incorrect DateFormat";
	
	public static final List<String> codesToCheckForDentureValidation1= Arrays.asList("IMP", "WXBITE", "TRYIN");
	public static final List<String> codesToCheckForProviderCertStatus= Arrays.asList("D0145", "D9230", "D9248");
	
	
	public static final String RULE_TYPE_RULE_ENGINE="R";
	public static final String RULE_TYPE_RCM="C";
	public static final String RULE_TYPE_RULE_ENGINE_AND_RCM=RULE_TYPE_RULE_ENGINE+","+RULE_TYPE_RCM;
	public static final String RULE_TYPE_MANUAL="MANUAL";
	public static final String RULE_TYPE_AUTO="AUTO";
	
	
}
