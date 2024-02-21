package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class PaymentInformationSectionDto {

	private String paymentIssueTo;
	private String amountReceivedInBank;
	private String amountPostedInEs;
	private String checkDeliverTo;
	private String checkNumber;
	private String paymentMode;
	private String amountDateReceivedInBank;
	private String checkCashDate;
	private int sectionId;
	private String paidAmount;
	
}
