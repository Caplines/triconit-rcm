package com.tricon.rcm.dto.customquery;

import java.util.Date;

public interface ClaimRuleValidationDto {

	String getMessage();
	Date getCd();
	String getFName();
	String getLName();
	int getRuleId();
	String getManualAuto();
	String getName();
	int getMessageType();
	String getRuleType();

}
