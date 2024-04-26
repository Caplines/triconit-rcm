package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class RebillingResponseDto {

	private String requestedRemarks;
	private String rebillingRemarks;
	private String reasonForRebilling;
	private String rebillingRequirements;
	private String rebillingServiceCodes;
	private String requestedBy;
	private String requestedByUuid;
	private boolean rebillingStatus;
	private boolean reCeationOptionChoosen;
	private String dateOfRebiiling;
	private int sectionId;
	private String usedAI;
}
