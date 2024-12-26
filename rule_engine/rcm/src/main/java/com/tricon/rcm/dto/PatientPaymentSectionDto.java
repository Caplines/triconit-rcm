package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class PatientPaymentSectionDto {

	private double amountCollectedClaims;
	private String dateOfPayment;
	private String postedInPMS;
	private String modeOfPayment;
	private double dueBalanceInPMS;
	private int sectionId;
	private String checkNumber;
	private String cardNumber;
}
