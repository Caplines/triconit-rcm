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

	public static String removeUptoAge(String siteName, String text) {
		return text.replace("Up to Age ", "").replace(":", "");
	}

	public static String removeLimitedToteeth(String siteName, String text) {
		return text.replace("Limited to teeth ", "").replace(":", "");
	}

}


