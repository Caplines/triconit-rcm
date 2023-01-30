package com.tricon.rcm.util;

import java.text.SimpleDateFormat;

public class Constants {

	public static final String COMPANY_NAME="Capline";//This matches with Company Table  Name Column... Same in Rule Engine 
	public static final String SYSTEM_USER_EMAIL="SYSTEM";//Default User  IN DB
	public static final String RCM_MAPPING_INSURANCE="Insurance";//Entry needed in DB->rcm_mapping_table 
	public static final String RCM_MAPPING_RCM_DATABASE="RCM DataBase";//Entry needed in DB->rcm_mapping_table
	
	//Date Format from  Eagle Soft.
	public static final SimpleDateFormat SDF_ES_DATE= new SimpleDateFormat("yyyy-MM-dd");
	
	//Date Format from  Google Sheet.
	public static final SimpleDateFormat SDF_SHEET_DATE= new SimpleDateFormat("yyyy-MM-dd");
	
	public static final int CLAIM_WITH_SYSTEM= 1;
	
	public static final String billingClaim="Billing";
	public static final String reBillingClaim="Re-billing";
	
	public static final String insuranceTypePrimary="Primary";
	public static final String insuranceTypeSecondary="Secondary";
	
	
	public static final String secondaryClaimTypeES="U";
	
	public static final String ClAIM_PULLED_SUCCESS="ClAIM PULLED SUCCESS";
	
	public static final String SYSTEM_INITIAL_COMMENT="Please Work o New Claim";
	
	//Taken From Rule Engine
    public static String socketworkingFine="Connection to office working fine. Agent is running successfully.";
	public static String socketnotworkingFine="Connection to office is <b style=\"color:red\" class=\"error-message-api\">not</b> working propertly. Make sure Agent is running / router is configured properly.";
	public static String NO_DATA="-NO-DATA-";
	//Taken From Rule Engine - END
	
	public static final String ROLE_PREFIX="ROLE_";
	public static final String  HYPHEN="_";
	public static final String  ADMIN="ADMIN";
	public static final String  SYSTEM="SYSTEM";
	public static final String  ASSOCIATE="ASSO";
	public static final Integer ENABLE=1;
	public static final Integer DISABLE=0;
	public static final Integer LENGTH=8;
	public static final String  ROLE_ADMIN="ROLE_ADMIN";
	public static final String PASSWORD_PATTERN = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

}
