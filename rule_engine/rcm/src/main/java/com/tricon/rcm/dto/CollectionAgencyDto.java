package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class CollectionAgencyDto {

	private String collectionType;
	private String debtNumber;
	private String reason;
	private String remarks;
	private String modeOfPayment;
	private double amountReceived;
	private double commisionCharged;
	private double netAmountReceived;
	private int buttonType;
	private int sectionId;
}
