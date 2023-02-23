package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class ClaimRuleValidationDto {

	String message;
	int messageType;
	int ruleId;
	
}
