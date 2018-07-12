package com.tricon.ruleengine.utils;

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
	
	public static final int treatmentPlanSheetID=1;
	public static final int ivTableDataSheetID=2;
	public static final int mappingSheetID_CM=6;
	public static final int mappingSheetID_FEE=7;
	public static final int eagleSoftCoverageSheetID=3;
	public static final int eagleSoftFSANDFEESheetID=4;
	public static final int eagleSoftFSNAMESheetID=5;

	public static final String RULE_ID_1="Eligibility of the patient";
	public static final String RULE_ID_2="IVF Details not matched";
	public static final String RULE_ID_3="Treatment Plan not valid";
	public static final String RULE_ID_4="Compare Coverage Book";
	public static final String RULE_ID_5="Remaining Deductible";
	public static final String RULE_ID_6="Percentage Coverage Check";
	public static final String RULE_ID_7="Alerts";
	
	
	
	public static final String SHEET_TYPE_TP="Treatmentplan";
	public static final String SHEET_TYPE_IVF_DATA="IVDATA";
	public static final String SHEET_TYPE_MappingTable_CM="MAPPING_CM";
	public static final String SHEET_TYPE_MappingTable_FEESN="MAPPING_FEESN";
	public static final String SHEET_TYPE_ES_COVERAGE="ES_COVERAGE";
	public static final String SHEET_TYPE_FS_NAME="FS_NAME";
	public static final String SHEET_TYPE_FS_FEE="FS_FEE";

	public static final SimpleDateFormat SIMPLE_DATE_FORMAT= new SimpleDateFormat("MM/dd/yyyy");
	
   public static final String FAIL = "FAIL";
   public static final String PASS = "PASS";

}
