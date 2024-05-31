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
	private Set<Discrepancy> claimInUploadErrors;
	private Set<Discrepancy> discrepancies;
	private Set<com.tricon.rcm.dto.ReconcillationClaimDto> discrepanciesAll;
}
