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
	Date serviceDate;
	String treatingSignature;
}
