package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class CurrentStatusAndNextActionDto {

	
	private String currentClaimStatusRcm;
	private String currentClaimStatusEs;
	private String remarks;
	private String nextAction;
	private int assignToTeamId;
	private int sectionId;
	private String buttonType;
}
