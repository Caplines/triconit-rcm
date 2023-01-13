package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class InsuranceNameTypeDto {

	String  insuranceName;
	String  insuranceType;
	
	public InsuranceNameTypeDto(String insuranceName, String insuranceType) {
		super();
		this.insuranceName = insuranceName;
		this.insuranceType = insuranceType;
	}
	
	
	
}
