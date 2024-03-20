package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class ValidationRuleRemarksDto {

	private int ruleId;
	private String remarks;
	private String message;
	private int messageType; 
}
