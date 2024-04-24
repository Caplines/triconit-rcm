package com.tricon.rcm.enums;

import java.util.Arrays;
import java.util.Optional;

public enum ClaimStatusEnum {

	Billing(1, "Billing"), 
	ReBilling(2, "Re-billing"),
	Need_to_Audit(3,"Need to Audit"),
	//Need_to_Bill(4,"Need to Bill"),
	Primary_Closed(5, "Primary Closed"), 
	Need_to_call(6,"Need to call"), 
	Primary_Settled(7, "Primary Settled"), 
	Adjustment_Approval_Pending(8,"Adjustment Approval Pending"), 
	Close_The_claim(9, "Close the Claim"),
	Statement(10,"Statement"),
	Bill_to_Secondary(11,"Bill_to secondary"), 
	Statement_CLOSE(12, "Statement Close"), // Puneet
	Need_to_followup(13, "Need to followup"),
	Need_to_followup_Close(14, "Need to followup Close"),// Puneet
	Review(15, "Review"),
	Billed(16, "Billed"),
	Claim_Uploaded(17, "Claim Uploaded"),
	Need_To_void(18,"Need to void"),
	EOB_Required(19,"EOB Required"),
	Need_to_call_Insurance(20,"Need to Call Insurance"),
	Need_to_Follow_up_on_Appeal(21,"Need to Follow up on Appeal"),
	Need_to_Review(22,"Need to Review"),
	Adjustment_Approval_Needed(23,"Adjustment Approval Needed"),
	Need_to_Post(24,"Need to Post"),
	Send_Statement(25,"Send Statement"),
	Send_Text(26,"Send Text"),
	No_Action_Needed(27,"No Action Needed"),
	Need_to_send_To_IC_system(28,"Need to send to IC system"),
	Need_to_Void(29,"Need to Void"),
	Closed(30,"Closed"),
	Voided(31,"Voided"),
	Pending_For_Review(32,"Pending For Review"),
	Pending_For_Billing(33,"Pending For Billing"),
	Claim_Archived(34,"Claim Archived"),
	Claim_UnArchived(35,"Claim UnArchived"),
	Claim_Assign_TO_TL(39,"Claim Assigned to TL"),
	Reviewed(36, "Reviewed"),
	Submitted(37,"Submitted"),
	IN_PROCESS(38,"In Process"),
	POSTED(40,"Posted");
	
	
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
	
	public static ClaimStatusEnum getByType(String type) {
		Optional<ClaimStatusEnum> claimStatusEnums = Arrays.stream(values()).filter(x -> x.getType().equals(type)).findFirst();
		if (claimStatusEnums.isPresent()) {
			return claimStatusEnums.get();

		}
		return null;
	}

}
