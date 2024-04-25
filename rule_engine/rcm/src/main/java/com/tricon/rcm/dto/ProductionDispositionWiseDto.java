package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class ProductionDispositionWiseDto {

	private String officeName;
	private int voiceMailLeft;
	private int paymentPromised;
	private int paymentMade;
	private int wrongNo;
	private int notReadyToPay;
	private int statementRequested;
	
	public ProductionDispositionWiseDto(String officeName, int voiceMailLeft, int paymentPromised, int paymentMade,
			int wrongNo, int notReadyToPay, int statementRequested) {
		super();
		this.officeName = officeName;
		this.voiceMailLeft = voiceMailLeft;
		this.paymentPromised = paymentPromised;
		this.paymentMade = paymentMade;
		this.wrongNo = wrongNo;
		this.notReadyToPay = notReadyToPay;
		this.statementRequested = statementRequested;
	}
	
	

}
