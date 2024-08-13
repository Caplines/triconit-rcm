package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class ReconcillationClaimDto {

	String claimId;
	String claimUuid;
	int currentState;//Archive /Unarchive
	int currentStatus; 
	String patientId;
	String statusEsUpdated;//billed unbilled closed
	String patientName;
}
