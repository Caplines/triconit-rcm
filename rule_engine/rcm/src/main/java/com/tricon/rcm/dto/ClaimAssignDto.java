package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class ClaimAssignDto {

	String claimUuid;
	boolean toLead;
	int otherTeamId;
	String remark;
	String teamLeadUuid;
	
}
