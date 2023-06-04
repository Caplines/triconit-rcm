package com.tricon.rcm.dto.customquery;


public interface PendingClaimToReAssignDto {

	String getClaimAssignedTo();
	String getClaimUuid();
	int getClaimAssignmentId();
	String getOfficeId();
}
