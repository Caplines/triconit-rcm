package com.tricon.rcm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ClaimReconcillationDto {

	
	@JsonProperty("c1")
	String claimId;
	
	@JsonProperty("c2")
	String patientId;
	
	@JsonProperty("c3")
	String status;
	
	@JsonProperty("c4")
	String transDate;
	
	@JsonProperty("c5")
	String type;

}