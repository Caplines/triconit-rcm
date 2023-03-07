package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class TPValidationResponseDto {

	private int ruleId;
	private String ruleName;
	private String message;
	private String resultType;
	private String surface;
	private String tooth;
	private String serviceCode;
	private String patientName;
	private String ivDone;
	private String off;
	private String iName;
	
	
	public TPValidationResponseDto(int ruleId, String ruleName, String message, String resultType,
			String surface,String tooth,String serviceCode) {
		super();
		this.ruleId = ruleId;
		this.ruleName = ruleName;
		this.message = message;
		this.resultType = resultType;
		this.surface=surface;
		this.tooth=tooth;
		this.serviceCode=serviceCode;
		
	}
}
