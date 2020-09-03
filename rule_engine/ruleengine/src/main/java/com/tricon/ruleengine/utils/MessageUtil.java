package com.tricon.ruleengine.utils;

import com.tricon.ruleengine.api.enums.HighLevelReportMessageStatusEnum;

/**
 * 
 * @author Deepak.Dogra
 *
 */
public class MessageUtil {
	public static int getReportMessageType(String message) {
		
		int messageType=HighLevelReportMessageStatusEnum.PASS.getStatus();
		if (message.contains("error-message-api")) messageType=HighLevelReportMessageStatusEnum.FAIL.getStatus();
		else if (message.contains("alert-message-api")) messageType=HighLevelReportMessageStatusEnum.ALERT.getStatus();
		
		return messageType;
		

		
	}

	public static String getTEXTNAORYES(String text) {
		if (text.contains(Constants.SCRAPPING_MANDATORY_WARNING)
			|| text.contains(Constants.SCRAPPING_NOT_FOUND)
			||text.contains(Constants.SCRAPPING_ISSUE_FETCHING)
				) return text;
		System.out.println("getTEXTNAORYES-"+text);
		if (text.equalsIgnoreCase("N/A")) return "No";
		if (text.equalsIgnoreCase("No")) return "No";
		
		else return "Yes";
	}
	
	public static String getTEXTSatisfied(String text) {
		if (text.contains(Constants.SCRAPPING_MANDATORY_WARNING)
			|| text.contains(Constants.SCRAPPING_NOT_FOUND)
			||text.contains(Constants.SCRAPPING_ISSUE_FETCHING)
				) return text;
		System.out.println("getTEXTSatisfied-"+text);
		if (text.equalsIgnoreCase("Satisfied")) return "No";
		else return "Yes";
	}
	
	public static void main(String [] a) {
		System.out.println(getTEXTWaitingPeriodBCBS("Will be satisfied on 5/1/2021", "2020-05-01"));
	}
	public static String getTEXTWaitingPeriodBCBS(String text,String efective) {
		if (text.contains(Constants.SCRAPPING_MANDATORY_WARNING)
			|| text.contains(Constants.SCRAPPING_NOT_FOUND)
			||text.contains(Constants.SCRAPPING_ISSUE_FETCHING)
				) return text;
		
		System.out.println("getTEXTWaitingPeriodBCBS - "+text);//
		try {
			text=text.toLowerCase().replaceAll("[a-zA-Z]", "").trim();//5/1/2021
			//String [] efectiveA=efective.split("-");//2020-05-01 //yyyy-mm-dd
			//String efectiveNew = ((efectiveA[0].length() == 1) ? ("0" + efectiveA[0]) : efectiveA[0]) + "/"
			//		+ ((efectiveA[1].length() == 1) ? ("0" + efectiveA[1]) : efectiveA[1]) + "/" + efectiveA[2];
			String [] textA=text.split("/");//2020-05-01 //yyyy-mm-dd
			String textANew = textA[2]+"-"+((textA[0].length() == 1) ? ("0" + textA[0]) : textA[0]) + "-"
					+ ((textA[1].length() == 1) ? ("0" + textA[1]) : textA[1]);
			String r=DateUtils.getDiffBetweenMonthsIV(efective, textANew);
			System.out.println(r);
			return r;
			
			
			
		}catch(Exception x) {
			System.out.println("No");
			return "No";
		}
		
	}

	public static String removeUptoAge(String siteName, String text) {
		return text.replace("Up to Age ", "").replace(":", "");
	}

	public static String removeLimitedToteeth(String siteName, String text) {
		return text.replace("Limited to teeth ", "").replace(":", "");
	}

}


