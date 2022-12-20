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
	String serviceDate;
	String treatingSignature;
	
	
	public RemoteLiteDto() {
		
	}
	public RemoteLiteDto(String processedDate, String transMitDate, String patientName, String subscriberName,
			String carrrier, String status, String serviceDate, String treatingSignature) {
		super();
		this.processedDate = processedDate;
		this.transMitDate = transMitDate;
		this.patientName = patientName;
		this.subscriberName = subscriberName;
		this.carrrier = carrrier;
		this.status = status;
		this.serviceDate = serviceDate;
		this.treatingSignature = treatingSignature;
	}
	
	
	
}
