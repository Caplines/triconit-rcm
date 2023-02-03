package com.tricon.rcm.dto;

import java.util.Date;

import lombok.Data;

@Data
public class RemoteLiteDto {

	String processedDate;
	String transMitDate;
	String patientName;
	String subscriberName;
	String carrrier;
	String status;
	String statusDescription;
	String serviceDate;
	String treatingSignature;
	String hiddenClaims;
	String office;
	
	
	
	public RemoteLiteDto() {
		
	}



	public RemoteLiteDto(String processedDate, String transMitDate, String patientName, String subscriberName,
			String carrrier, String status, String statusDescription, String serviceDate, String treatingSignature,
			String hiddenClaims, String office) {
		super();
		this.processedDate = processedDate;
		this.transMitDate = transMitDate;
		this.patientName = patientName;
		this.subscriberName = subscriberName;
		this.carrrier = carrrier;
		this.status = status;
		this.statusDescription = statusDescription;
		this.serviceDate = serviceDate;
		this.treatingSignature = treatingSignature;
		this.hiddenClaims = hiddenClaims;
		this.office = office;
	}


	
	
	
}
