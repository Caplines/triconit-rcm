package com.tricon.rcm.dto;

import java.util.List;

import lombok.Data;

@Data
public class RebillingDtoMain {

	private String requestedRemarks;
	private String rebillingRemarks;
	private String reasonForRebilling;
	private List<String>selectedRebillingRequirements; //select rebilling requirement from rebilling UI
	private List<String>selectedRebillingServiceCodes;		//select rebilling service from rebilling UI
	private String requestedBy;
	private String requestedByUuid;
	private boolean rebillingStatus;// rebiiling/ no need to rebill status
	private boolean reCeationOptionChoosen;//Are you Recreating the Claim?
	private String dateOfRebiiling;
	private List<String>originalServiceCodes;//select rebilling service from  requestBilling UI
	private List<String>originalRequirements;//select rebilling requirement from  requestBilling UI
	private int claimTransferNextTeamId;
	private int sectionId;
	private String usedAI;
}
