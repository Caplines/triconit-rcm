package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class ClaimRuleVaidationValueDto {

	int ruleId;
	String message;
	int messageType;
	String ruleName;
	String ruleDesc;
	String manualAuto;
}
