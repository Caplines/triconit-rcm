package com.tricon.rcm.dto;

import java.util.List;

import lombok.Data;

@Data
public class ReconciliationResponseDto {

	private String title;
	private int claimsES;
	private int claimsRCM;
	private List<String> discrepancies;
}
