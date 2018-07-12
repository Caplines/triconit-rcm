package com.tricon.ruleengine.utils;


import java.util.Date;

public class DateUtils {
	
	//Compares Two Date 
	public static boolean compareDates(Date one,Date two) {
	System.out.println(one);
	System.out.println(two);
	
	return (one.compareTo(two)>=0);
	}

	//Compares Two Date 
	public static void CheckForStringInDate(String date) throws RuleEngineDateException{

		 if( date.matches(".*[a-zA-Z]+.*")){
	          throw  new RuleEngineDateException("Date Contains String");
	        }
	}

}
