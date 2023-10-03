package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class AutoRunClaimReponseDto {

	String message;
	String providerOnClaim;
	String providerOnClaimFromSheet;
	String claimType;
	String assignmentOfBenefits;
	ServiceValidationDataDto serviceValidationDataDto;
	String preferredModeOfSubmission;
}
