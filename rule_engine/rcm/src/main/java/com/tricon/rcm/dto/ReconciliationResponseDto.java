package com.tricon.rcm.dto;

import java.util.Set;

import lombok.Data;

@Data
public class ReconciliationResponseDto {

	private String title;
	private String office;
	private int claimsES;
	private int claimsRCM;
	private Set<String> claimsNotFoundRCM;
	private Set<String> claimInUploadErrors;
	private Set<String> discrepancies;
	private Set<com.tricon.rcm.dto.ReconcillationClaimDto> discrepanciesAll;
}
