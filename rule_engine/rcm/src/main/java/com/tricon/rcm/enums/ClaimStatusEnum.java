package com.tricon.rcm.enums;

import java.util.Arrays;
import java.util.Optional;

public enum ClaimStatusEnum {

	//Make same entry in rcm_claim_status_type table
	Billing(1, "Billing"), 
	ReBilling(2, "Re-billing"),
	Need_to_Audit(3,"Need to Audit"),
	Need_to_Bill(4,"Need to Bill"),
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
	SEND_TO_AGING(18,"Send to Aging"),
	EOB_Required(19,"EOB Required"),
	Need_to_call_Insurance(20,"Need to Call Insurance"),
	Need_to_Follow_up_on_Appeal(21,"Need to Follow up on Appeal"),
	Need_to_Review(22,"Need to Review"),
	Adjustment_Approval_Needed(23,"Adjustment Approval Needed"),
	Need_to_Post(24,"Need to Post"),
	Statement_SENT(25,"Statement Sent"),
	Send_Text(26,"Send Text"),
	No_Action_Needed(27,"No Action Needed"),
	Need_to_send_To_IC_system(28,"Need to send to IC system"),
	Need_to_Void(29,"Need to Void"),
	Case_Closed(30,"Case Closed"),
	Voided(31,"Voided"),
	Pending_For_Review(32,"Pending For Review"),
	Pending_For_Billing(33,"Pending For Billing"),
	Claim_Archived(34,"Claim Archived"),
	Claim_UnArchived(35,"Claim UnArchived"),
	Claim_Assign_TO_TL(39,"Claim Assigned to TL"),
	Reviewed(36, "Reviewed"),
	Submitted(37,"Submitted"),
	IN_PROCESS(38,"In Process"),
	POSTED(40,"Posted"),
	Pending_For_Review_By_CDP(41,"Pending For Review by CDP"),
	Pending_From_Insurance(42,"Pending From Insurance"),
	Insurance_Claim_Closed(43,"Insurance Claim Closed"),
	Insurance_Claim_in_Process(44,"Insurance Claim in Process"),
	Need_to_send_Appeal(45,"Need to send Appeal"),
	Case_Sent_To_Collections(46,"Case sent to Collections"),
	Claim_Denied(47,"Claim Denied"),
	Payment_Promised(48,"Payment Promised"),
	Appeal_In_Process(49,"Appeal in Process"),
	Appeal_Rejected(50,"Appeal Rejected"),
	Claim_not_on_File(51,"Claim not on File"),
	Paid_In_Full(52,"Paid in full"),
	Partial_Paid(53,"Partial paid"),
	Claim_Incorrectly_Denied(54,"Claim Incorrectly Denied"),
	Claim_On_Hold(55,"Claim on Hold"),
	Claim_Incorrectly_Processed(56,"Claim Incorrectly Processed"),
	Processed_Under_Capitation(57,"Processed Under Capitation"),
	Amount_Recouped_For_Some_Other_Claim(58,"Amount Recouped For Some Other Claim"),
	Claim_Processed_In_PMS_But_Not_Billed(59,"Claim Processed in PMS but not billed"),
	Payment_Issued_To_Patient(60,"Payment Issued to Patient"),
	EOB_Requested(61,"EOB Requested"),
	Need_to_Void_Partial_Claim(62,"Need to Void Partial Claim"),
	Need_to_Void_Full_Claim(63,"Need to Void Full Claim"),
	Need_to_Follow_Up(64,"Need to Follow up"),
	Need_to_Call_Insurance_For_Follow_UP(65,"Need to Call Insurance for Follow up"),
	Need_to_Adjust_Post_Approval(66,"Need to Adjust post Approval"),
	Need_to_Send_Statement(67,"Need to Send Statement"),
	Need_to_Send_Text_to_Patient_For_Payment(68,"Need to Send Text to Patient for Payment"),
	Need_to_Send_to_Collections(69,"Need to send to Collections"),
	Need_to_Call_Insurance_For_EOB(70,"Need to Call Insurance for EOB"),
	Need_to_Check_Payment_Status(71,"Need to Check Payment Status"),
	Need_to_Check_Claim_Status_On_Web(72,"Need to Check Claim Status on Web"),
	Need_to_Bill_Secondary_Insurance(73,"Need to Bill Secondary Insurance"),
	Need_Additional_Information_For_Claim(74,"Need Additional Information For Claim"),
	Need_to_Follow_Up_For_Void_Request(75,"Need to follow up for Void Request"),
	Need_to_Get_Provider_Credentialed(76,"Need to get Provider Credentialed"),
	Need_to_Call_Insurance_For_reprocessing(77,"Need to call Insurance for reprocessing"),
	NEED_TO_REBILL(78,"Need to re-bill"),
	APPEAL_UPHELD(79,"Appeal Upheld"),
	NEED_TO_FOLLOWUP_ON_VOID(80,"Need to followup on void"),
	Additional_Information_Provided_For_Claim(81,"Additional Information Provided For Claim")
	;
	
	
	
	
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
