package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class CurrentStatusAndNextActionDto {

	
	private String currentClaimStatusRcm;
	private String currentClaimStatusEs;
	private String remarks;
	private String nextAction;
	private int assignToTeam;
	private String assignToTeamName;
	private int sectionId;
}
