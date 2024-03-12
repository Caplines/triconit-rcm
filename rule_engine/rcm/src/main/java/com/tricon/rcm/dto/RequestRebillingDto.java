package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class RequestRebillingDto {

	private String remarks;
	private String reasonForRebilling;
	private String rebillingRequirements;
	private String rebillingServiceCodes;	
	private String rebillingType;
	private String nextAction;
	private String currentAction;
	private int teamId;
	private String billingUserUuid;
	private int sectionId;
}
