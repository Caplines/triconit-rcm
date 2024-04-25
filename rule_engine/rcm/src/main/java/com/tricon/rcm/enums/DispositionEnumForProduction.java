package com.tricon.rcm.enums;

public enum DispositionEnumForProduction {

	VOICE_MAIL_LEFT("Voice Mail Left"), PAYMENT_PROMISED("Payment Promised"), PAYMENT_MADE("Payment Made"),
	WRONG_NO("Wrong No"), NOT_READY_TO_PAY("Not Ready To Pay"), STATEMENT_REQUESTED("Statement Requested");

	final private String name;

	private DispositionEnumForProduction(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
