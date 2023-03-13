package com.tricon.rcm.util;

import com.tricon.rcm.enums.HighLevelReportMessageStatusEnum;

public class MessageUtil {

	public static int getReportMessageType(String message) {

		int messageType = HighLevelReportMessageStatusEnum.PASS.getStatus();
		if (message.contains("error-message-api"))
			messageType = HighLevelReportMessageStatusEnum.FAIL.getStatus();
		else if (message.contains("alert-message-api"))
			messageType = HighLevelReportMessageStatusEnum.ALERT.getStatus();

		return messageType;

	}
}
