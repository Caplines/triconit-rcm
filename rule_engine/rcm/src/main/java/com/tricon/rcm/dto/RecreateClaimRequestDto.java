package com.tricon.rcm.dto;

import java.util.List;

import lombok.Data;

@Data
public class RecreateClaimRequestDto {

	private int actionButtonType;
	private int sectionId;
	private List<String>selectedServiceCodes;
	private List<String>existingNewClaimServiceCodes;
	private String newClaimId;
	private String reasonForRecreation;
	private String recreationRemarks;
	private List<ValidationRuleRemarksDto> validationRuleRemarks;
	private RebillingResponseDto  rebillingResponseDto;
	private List<ClaimFromSheet> claimFromSheet;
	private boolean reCeationOptionChoosen;
}
