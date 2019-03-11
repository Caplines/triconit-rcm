package com.tricon.ruleengine.utils;

import org.springframework.security.core.userdetails.User;

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
}
