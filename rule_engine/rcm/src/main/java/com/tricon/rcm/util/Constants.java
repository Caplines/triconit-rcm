package com.tricon.rcm.util;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.tricon.rcm.enums.RcmTeamEnum;

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
	
	//Flag for submit button when upload attachment with remarks inside others_team_work page
	public static final String ATTACH_WITH_REMARKS="AttachWithRemarks";
	public static final String ATTACH_WITH_REMARKS_REBILL="AttachWithRemarksRebill";
	
	public static final String SHOW_ALL_PREFIX = "all";
	public static final String TRUE_PREFIX = "true";
	public static final String FALSE_PREFIX = "false";
	public static final int SIXTY_PLUS_AR  = 1;
	public static final int OVERDUE_UNBILLED_MEDICAID  = 2;
	public static final int OVERDUE_UNBILLED_NON_MEDICAID=3;
	public static final int OVERDUE_CLAIM_DATE_DIFF=7;
	public static final String SUBMITTED_CLAIMS="submitted";
	public static final String UNSUBMITTED_CLAIMS="unSubmitted";
	public static final String PAYMENT_MODE_CHECK="Check";
	public static final String PAYMENT_MODE_EFT="EFT";
	public static final String PAYMENT_MODE_VCC="VCC";
	

	public static final List<String> PAGE_NAME = Arrays.asList("Pendancy", "Pendancy-other", "Pendancy-other-dos",
			"Pendancy-other-dop");
	public static final List<String> COUNT_TYPE = Arrays.asList("opdt", "opdos", "opdtd",
			"opdosd","tp");
	
	//TEAM TRANSFER
	public static final int FROMBILLINGTOPOSTING=RcmTeamEnum.PAYMENT_POSTING.getId();
	public static final int CLAIM_POSTING_STATE=1;
	public static final String From_Claim_Closing="From Claim Closing";
	public static final String From_1_Statement="From 1st Statement";
    public static final String From_2_Statement="From 2nd Statement";
    public static final int SEND_STATEMENT_BUTTON_TYPE_FOR_PATIENT_STATEMENT_SECTION=2;
    public static final int NEED_TO_HOLD_BUTTON_TYPE_FOR_PATIENT_STATEMENT_SECTION=1;

	public static final int BUTTON_TYPE_ONE_FOR_COLLECTION_SECTION = 1;
	public static final int BUTTON_TYPE_TWO_FOR_COLLECTION_SECTION = 2;
	public static final int BUTTON_TYPE_THREE_FOR_COLLECTION_SECTION = 3;
	
	public static final String PRIMARY_PREFIX="_P";
	public static final String SECONDARY_PREFIX="_S";
	
	public static final int BUTTON_TYPE_ATTACH_SECONDARY = 1;
	public static final int BUTTON_TYPE_RECREATE_FULL_CLAIM= 2;
	public static final int BUTTON_TYPE_RECREATE_PARTIAL_CLAIM = 3;
	
	public static final int MAX_COMMENT_DATA_PER_PAGE=10;
	public static final int MAX_CLAIM_FETCH_DATA_PER_QUERY=100;
	
	public static final int FROMPOSTINGTOAGING=RcmTeamEnum.AGING.getId();
    
	public static final int CLAIM_CLOSED =30;
	
	public static final String BUTTON_TYPE_ASSIGN_TO_OTHER_TEAM="assignToOtherTeam";
	public static final String BUTTON_TYPE_ASSIGN_TO_SAME_TEAM="assignToSameTeam";
    public static final String BUTTON_TYPE_ARCHIVE="archive";
    public static final String BUTTON_TYPE_ASSIGN_TO_TL="assignToTeamLead";

    
}
