package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class TimelyFilingLimitDto {

	
	//String insuranceName;
	String insuranceCode;
	//String remarks;
	//String appealLimit;
	String timelyFilingLimit;
	
	public TimelyFilingLimitDto(String insuranceCode, String timelyFilingLimit) {
		super();
		this.insuranceCode = insuranceCode;
		//this.remarks = remarks;
		//this.appealLimit = appealLimit;
		this.timelyFilingLimit = timelyFilingLimit;
	}
	
	
	
}
