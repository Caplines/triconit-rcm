package com.tricon.rcm.util;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Constants {

	public static final String COMPANY_NAME = "Smilepoint";// This matches with Company Table Name Column... Same in
															// Rule Engine
	public static final String SYSTEM_USER_EMAIL = "SYSTEM";// Default User IN DB
	public static final String RCM_MAPPING_INSURANCE = "Insurance";// Entry needed in DB->rcm_mapping_table
	public static final String RCM_MAPPING_TIMELY_LIMIT = "Timely Filling Sheet";// Entry needed in
																					// DB->rcm_mapping_table
	public static final String RCM_MAPPING_RCM_DATABASE = "RCM DataBase";// Entry needed in DB->rcm_mapping_table

	// Date Format from Eagle Soft.
	public static final SimpleDateFormat SDF_ES_DATE = new SimpleDateFormat("MMM dd, yyyy");

	// Date Format from Google Sheet.
	public static final SimpleDateFormat SDF_SHEET_DATE = new SimpleDateFormat("yyyy-MM-dd");

	// Date Format from Google Sheet Provider .
	public static final SimpleDateFormat SDF_SHEET_PROVIDER_DATE = new SimpleDateFormat("MMM d, yyyy");
	
	public static final SimpleDateFormat SDF_UI = new SimpleDateFormat("MM-dd-YYYY"); 
	public static final SimpleDateFormat SDF_CredentialSheetAnes = new SimpleDateFormat("MM/dd/YYYY");
	public static final SimpleDateFormat SDF_SHEET_PROVIDER_DATE_HELPING = new SimpleDateFormat("M/d/YYYY");
	public static final SimpleDateFormat SDF_SHEET_PROVIDER_DATE_HELPING_YEAR = new SimpleDateFormat("YYYY");
	
   //FROM MYSQL DB	
	public static final SimpleDateFormat SDF_MYSL_DATE = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat SDF_MYSL_DATE_TIME = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	
	public static final int CLAIM_WITH_SYSTEM = 1;

	// public static final String billingClaim="Billing";
	// public static final String reBillingClaim="Re-billing";

	public static final String insuranceTypePrimary = "Primary";
	public static final String insuranceTypeSecondary = "Secondary";

	public static final String secondaryClaimTypeES = "U";

	public static final String ClAIM_PULLED_SUCCESS = "ClAIM PULLED SUCCESS";

	public static final String SYSTEM_INITIAL_COMMENT = "Please Work on New Claim";

	public static final String SYSTEM_OTHER_TEAM_ASSIGN_COMMENT = "Please Work on the Claim";
	public static final String SYSTEM_TRANSFER_TO_TEAM_COMMENT = "Claim Transfered To Team";
	// Taken From Rule Engine
	public static String socketworkingFine = "Connection to office working fine. Agent is running successfully.";
	public static String socketnotworkingFine = "Connection to office is <b style=\"color:red\" class=\"error-message-api\">not</b> working propertly. Make sure Agent is running / router is configured properly.";
	public static String NO_DATA = "-NO-DATA-";
	public static String OfficeNeedToTakeInfo_o = "OfficeNeedToTakeInfo";
	public static String OfficeNeedToTakeInfo_r = "Office Need To Take Info";
	public static final String RULE_TYPE_RULE_ENGINE = "R";
	public static final String RULE_TYPE_RCM = "C";
	public static final String RULE_TYPE_GSHEET= "G";
	public static final String RULE_TYPE_RULE_ENGINE_AND_RCM = RULE_TYPE_RULE_ENGINE + "," + RULE_TYPE_RCM;
	public static final String RULE_TYPE_MANUAL = "MANUAL";
	public static final String RULE_TYPE_AUTO = "AUTO";

	public static final String ALERT = "Alert";
	public static final String FAIL = "FAIL";
	public static final String NOT_FOUND = "NOT FOUND";
	public static final int FAIL_MESSAGE_TYPE = 1;

	public static final String PASS = "PASS";
	// Taken From Rule Engine - END

	public static final int LAST_X_DAYS_TO_CHECK_DET=56;
	public static final String ROLE_PREFIX = "ROLE_";
	public static final String HYPHEN = "_";
	public static final String ADMIN = "ADMIN";
	public static final String UPLOAD_CLAIMS = "UPLOAD_CLAIMS";
	public static final String ACCOUNT_MANAGER = "ACCOUNT_MANAGER";
	public static final String SYSTEM = "SYSTEM";
	public static final String ASSOCIATE = "ASSO";
	public static final String TEAMLEAD = "TL";
	public static final Integer ENABLE = 1;
	public static final Integer DISABLE = 0;
	public static final Integer LENGTH = 8;
	public static final String ROLE_ADMIN = "ROLE_ADMIN";
	public static final String PASSWORD_PATTERN = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	public static final String SHOW_ALL_COMPANY_USERS = "All";
	public static final String MAPPING_TABLE_NAME_RCM_DATABASE = "RCM DataBase";
	public static final String MAPPING_TABLE_NAME_TFS = "Timely Filling Sheet";
	public static final String MAPPING_TABLE_GOOGLE_SHEET_ID = "1txTh07ssmXytBscroqVnbSRppeZjt8x1m391Hd1Fsr4";
	public static final String MAPPING_TABLE_GOOGLE_SHEET_SUB_ID = "0";
	public static final String MAPPING_TABLE_GOOGLE_SHEET_SUB_NAME = "Paste Data Here";
	public static final List<String> DEFAULT_ROLE_FOR_SMILEPOINT = Arrays.asList("ADMIN", "UPLOAD_CLAIMS","ACCOUNT_MANAGER");
	public static final List<String> DEFAULT_ROLE_FOR_OTHERS = Collections.emptyList();
	public static final String CLIENT_MANAGER = "CLIENT_MANAGER";
	public static final String CLIENT_VIEW_ONLY = "CLIENT_VIEW_ONLY";
	public static final String SUPER_ADMIN = "SUPER_ADMIN";
	public static final String REPORTING = "REPORTING";
	public static final String UI_RULEENIGNE_SECTION = "RuleEngine";
	public static final String UI_CLAIM_VALIDATION_SECTION = "ClaimLevelValidation";
	public static final int Primary_Status_Primary=0;
	public static final int Primary_Status_Secondary=1;
	public static final int Primary_Status_Primary_submit=2;
	public static final String CMC="CMC";
	public static final String ProviderJoinCons="<<<>>>";
	//Sheets
	public static final String Provider_Schedule_SHEET="1r1unO-L2wAB6zAEaKYzMEIUi1nG_F6wTzduzaXqiKAg";
	///public static final String Provider_Schedule_SHEET_HELP="Helping";
	public static final String Mapping_Tables="1g9VtQVT5T0-Fp_beLSYhRIbUn-KBqP4TGmYuteMbsd4";
	public static final String Mapping_Tables_Provider="Provider";
	
	//End Sheet
	public static final String REMOVE_ATTACHMENT_PREFIX="del_";
	
	public static final String ARCHIVE_PREFIX="arc_";
	
	public static final int CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED=0;
	public static final int CLAIM_ARCHIVE_PREFIX_CANNOT_SUBMITED=1;
	public static final List<String> SKIP_URL_FROM_RCM_LOGS = Arrays.asList("/api/list-of-claim/d/pdf",
				"/api/issue-claim/d/pdf", "/api/other-teams-work/d/pdf", "/api/ivf/d/pdf", "/api/tp-link/d/pdf",
				"/api/allPendancy/d/pdf", "/api/production/d/pdf", "/api/claim-details/d/pdf", "/api/pendancy/d/pdf");
	
	public static final int MIN_RANGE=1;
	public static final int MAX_RANGE=2;
	public static final int ATTACHMENT_WITH_REMARKS=1;
	public static final String ATTACH_WITH_REMARKS="AttachWithRemarks";

}
