package com.tricon.rcm.dto;

import java.util.List;

import lombok.Data;

@Data
public class RebillingDto {

	private String requestedRemarks;
	private String rebillingRemarks;
	private String reasonForRebilling;
	private String rebillingRequirements;
	private String selectedRebillingServiceCodes;	
	private String rebillingServiceCodes;	
	private String requestedBy;
	private String requestedByUuid;
	private boolean rebillingStatus;
	private boolean reCeationOptionChoosen;
	private String dateOfRebiiling;
	private List<String>selectedServiceCodes;
	private List<String>selectedRequirements;
	private int sectionId;
}
