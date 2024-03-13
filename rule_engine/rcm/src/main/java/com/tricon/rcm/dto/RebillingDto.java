package com.tricon.rcm.dto;

import java.util.List;

import lombok.Data;

@Data
public class RebillingDto {

	private String requestedRemarks;
	private String rebillingRemarks;
	private String reasonForRebilling;
	private List<String>selectedRebillingRequirements; //select rebilling requirement from rebilling UI
	private List<String>selectedRebillingServiceCodes;		//select rebilling service from rebilling UI
	private String requestedBy;
	private String requestedByUuid;
	private boolean rebillingStatus;
	private boolean reCeationOptionChoosen;
	private String dateOfRebiiling;
	private List<String>originalServiceCodes;//select rebilling service from  requestBilling UI
	private List<String>originalRequirements;//select rebilling requirement from  requestBilling UI
	private int claimTransferNextTeamId;
	private int sectionId;
}
