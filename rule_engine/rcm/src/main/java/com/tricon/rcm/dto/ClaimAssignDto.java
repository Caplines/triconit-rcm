package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class ClaimAssignDto {

	String claimUuid;
	boolean toLead;
	int otherTeamId;
	String remark;
	String teamLeadUuid;
	int assignToTeam;
	private String currentClaimStatusRcm;
	private String currentClaimStatusEs;
	private String nextAction;
	private String withNextActionData; // use for claim assign to TL  with  next action section data to create claim cycle
	
}
