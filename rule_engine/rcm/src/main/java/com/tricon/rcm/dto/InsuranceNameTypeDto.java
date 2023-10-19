package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class InsuranceNameTypeDto {

	String  clientName;
	String  insuranceName;
	String  insuranceType;
	String  insuranceCode;
	String  preferredModeOfSubmission;
	
	public InsuranceNameTypeDto(String clientName,String insuranceName, String insuranceType) {
		super();
		this.clientName=clientName;
		this.insuranceName = insuranceName;
		this.insuranceType = insuranceType;
	}
	
	
	
}
