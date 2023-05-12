package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class RcmClaimsServiceRuleValidationDto {

	private String remarkUuid;

	private String serviceCode;

	private String name;

	private String description;

	private String value;

	private int messageType;

	private String remark;
	
	private String manualAuto;
	
	private String answer;
	
    private String teamName;
	
	private String displayValues;

}
