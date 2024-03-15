package com.tricon.rcm.dto;

import lombok.Data;

@Data

public class ValidateRecreateClaimResponseDto {
	
	private int ruleId;
	private String ruleName;
	private String message;
	private String resultType;
	public ValidateRecreateClaimResponseDto(int ruleId, String ruleName, String message, String resultType) {
		super();
		this.ruleId = ruleId;
		this.ruleName = ruleName;
		this.message = message;
		this.resultType = resultType;
	}
	
	
}
