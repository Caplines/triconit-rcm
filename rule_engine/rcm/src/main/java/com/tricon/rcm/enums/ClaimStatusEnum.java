package com.tricon.rcm.enums;

import java.util.Arrays;
import java.util.Optional;

public enum ClaimStatusEnum {

	Billing(1, "Billing"), 
	ReBilling(2, "Re-billing"),
	Awaiting_Insurance_Settlement(3,"Awaiting Insurance Settlement"),
	Unbilled_Internal_Audit(4, "Unbilled (Internal Audit)"),
	Unbilled_Billing(5, "Unbilled (Billing)"),
	Primary_Closed(6, "Primary Closed"), 
	Need_to_call(7,"Need to call"), 
	Primary_Settled(8, "Primary Settled"), 
	Adjustment_Approval_Pending(9,"Adjustment Approval Pending"), 
	Closed(10, "Closed"),
	Statement(11,"Statement"),
	Bill_to_Secondary(12,"Bill_to secondary"), 
	Statement_CLOSE(13, "Statement Close"), // Puneet
	Need_to_followup(14, "Need to followup"),
	Need_to_followup_Close(15, "Need to followup Close"),// Puneet
	Review(15, "Review");
	
	final private int id;
	final private String type;

	private ClaimStatusEnum(int id, String type) {
		this.type = type;
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public int getId() {
		return id;
	}

	public static ClaimStatusEnum getById(int id) {
		Optional<ClaimStatusEnum> claimStatusEnums = Arrays.stream(values()).filter(x -> x.getId() == id).findFirst();
		if (claimStatusEnums.isPresent()) {
			return claimStatusEnums.get();

		}
		return null;
	}

}
