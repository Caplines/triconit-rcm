package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class ClaimLevelInformationDto {
	
	private String claimId;
	private String network;
	private String claimProcessingDate;
	private String claimPassFirstGo;
	private String initialDenial;
	private String noOfEstPayment;
	private String paymentFrequency;
	private String noOfPaymentReceived;
	private int sectionId;

}
