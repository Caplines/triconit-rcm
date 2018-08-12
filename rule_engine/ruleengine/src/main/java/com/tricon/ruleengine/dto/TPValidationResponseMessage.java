package com.tricon.ruleengine.dto;

public class TPValidationResponseMessage {

	private String message;

	public TPValidationResponseMessage(String message) {
		super();
		this.message=message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}


}
