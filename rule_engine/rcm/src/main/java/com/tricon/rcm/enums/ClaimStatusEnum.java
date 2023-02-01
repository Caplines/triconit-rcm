package com.tricon.rcm.enums;

public enum ClaimStatusEnum {

	Billing(1,"Billing"),
	ReBilling(2,"Re-billing");
	
	final private int id;
	final private String type;
	
	private ClaimStatusEnum(int id,String type) {
		this.type = type;
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public int getId() {
		return id;
	}
}
