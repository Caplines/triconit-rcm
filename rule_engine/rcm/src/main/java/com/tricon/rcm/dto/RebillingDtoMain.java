package com.tricon.rcm.dto;

import java.util.List;

import lombok.Data;

@Data
public class RebillingDtoMain {

	 String requestedRemarks;
	 String rebillingRemarks;
	 String reasonForRebilling;
	 List<String>selectedRebillingRequirements; //select rebilling requirement from rebilling UI
	 List<String>selectedRebillingServiceCodes;		//select rebilling service from rebilling UI
	 String requestedBy;
	 String requestedByUuid;
	 boolean rebillingStatus;// rebiiling/ no need to rebill status
	 boolean reCeationOptionChoosen;//Are you Recreating the Claim?
	 String dateOfRebiiling;
	 List<String>originalServiceCodes;//select rebilling service from  requestBilling UI
	 List<String>originalRequirements;//select rebilling requirement from  requestBilling UI
	 int claimTransferNextTeamId;
	 int sectionId;
	 String usedAI;
}
