package com.tricon.rcm.dto.customquery;

public interface ReconcillationClaimDto {

	String getClaimId();
	String getClaimUuid();
	int getCurrentState();//Archive /Unarchive
	int getCurrentStatus(); 
	
}
