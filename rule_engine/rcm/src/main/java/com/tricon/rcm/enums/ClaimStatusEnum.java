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
	Need_to_Bill_Secondary_Insurance(73,"Need to Bill Secondary Insurance"),//Same name in Constants also
	//public static final String Need_to_Bill_Secondary_Insurance="Need to Bill Secondary Insurance";
	Need_Additional_Information_For_Claim(74,"Need Additional Information For Claim"),
	Need_to_Follow_Up_For_Void_Request(75,"Need to follow up for Void Request"),
	Need_to_Get_Provider_Credentialed(76,"Need to get Provider Credentialed"),
	Need_to_Call_Insurance_For_reprocessing(77,"Need to call Insurance for reprocessing"),
	NEED_TO_REBILL(78,"Need to re-bill"),
	APPEAL_UPHELD(79,"Appeal Upheld"),
	NEED_TO_FOLLOWUP_ON_VOID(80,"Need to followup on void"),
	Additional_Information_Provided_For_Claim(81,"Additional Information Provided For Claim"),
	Additional_Information_Required(82, "Additional information required"),
	Need_To_Call_Patient_For_Information(83, "Need to call patient for Information"),
	Need_To_Fix_The_Walkout(84, "Need to Fix the Walkout"),
	Awaiting_Prosthetic_Code_Delivery(85, "Awaiting prosthetic code delivery"),
	Need_To_Bill_To_Patient(86, "Need to Bill to Patient"),
	Need_To_Adjust_Off(87, "Need to Adjust Off"),
	Transferred_To_Posting_Team(88, "Transferred to Posting Team"),
	Need_To_Send_2nd_Level_Appeal(89, "Need to send 2nd Level Appeal"),
	Second_Level_Appeal_Sent(90, "2nd Level Appeal Sent"),
	Appeal_XRay_Narrative_Perio_Sent_Fax(91, "Appeal/X-Ray/Narrative/Perio Sent (Fax)"),
	Appeal_XRay_Narrative_Perio_Sent_Web(92, "Appeal/X-Ray/Narrative/Perio Sent (Web)"),
	Appeal_XRay_Narrative_Perio_Sent_Paper(93, "Appeal/X-Ray/Narrative/Perio Sent (Paper)"),
	Installment_Received_And_Need_To_Followup(94, "Installment Received & Need to Followup"),
	Info_Required_From_Office(95, "Info required from Office"),
	Adjusted_Off(96, "Adjusted Off"),
	Billed_To_Patient(97, "Billed to Patient"),
	Posted_And_Recreated(98, "Posted and Recreated"),
	Review_Today_Billing_Remarks(99, "Review Today (Please see Billing Remarks)"),
	Rebilled_Paper(100, "Rebilled (Paper)"),
	Rebilled_Fax(101, "Rebilled (Fax)"),
	Rebilled_Web(102, "Rebilled (Web)"),
	Rebilled_PMS(103, "Rebilled (PMS)"),
	Need_To_Send_CIF(104, "Need to send CIF"),
	Transferred_To_Aging_Team(105, "Transferred to Aging Team"),
	Review_Today_Posting_Remarks(106, "Review Today (Please see Posting Remarks)"),
	Billed_To_Secondary(107, "Billed to Secondary"),
	EOB_Required_From_Office(108, "EOB Required from Office"),
	ReProcessed_While_On_Call(109, "Re-Processed while on call"),
	Need_Second_Level_Review(110, "Need Second Level Review"),
	CIF_Sent_To_Office(111, "CIF Sent to Office"),
	Posting_Confirmation_Approval_Required_From_Office(112, "Posting Confirmation/Approval Required from Office"),
	Transferred_To_Credentialing_Team(113, "Transferred to Credentialing Team"),
	NOA_Sent_To_Office(114, "NOA Sent to Office"),
	Rebilled_Email(115, "Rebilled (Email)"),
	Appeal_Sent_Status(116, "Appeal Sent"),
	NOA_Sent_To_Insurance(117, "NOA Sent to Insurance"),
	Need_To_Raise_Void_Request(118, "Need to Raise Void Request"),
	CIF_Mailed_To_Dentical(119, "CIF Mailed to Dentical"),
	Paid_By_Insurance(120, "Paid"),
	Appeal_Needed(121, "Appeal Needed"),
	EOB_Not_Generated(122, "EOB Not Generated"),
	Reprocessed_Status(123, "Reprocessed"),
	Claim_Received_No_Claim_Number_Generated(124, "Claim received but no claim number generated")
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
