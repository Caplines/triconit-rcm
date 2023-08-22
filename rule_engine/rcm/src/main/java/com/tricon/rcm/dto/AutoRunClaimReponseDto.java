package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class AutoRunClaimReponseDto {

	String message;
	String providerOnClaim;
	String claimType;
	String assignmentOfBenefits;
	ServiceValidationDataDto serviceValidationDataDto;
}
