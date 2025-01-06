package com.tricon.rcm.dto;

import java.util.Date;

import lombok.Data;

@Data
public class PaymentInformationSectionDto {

	private String paymentIssueTo;
	private double amountReceivedInBank;
	private double amountPostedInEs;
	private String checkDeliverTo;
	private String checkNumber;
	private String paymentMode;
	private String amountDateReceivedInBank;
	private String checkCashDate;
	private int sectionId;
	private double paidAmount;
	private int id;
	private Date createdDate;
}
