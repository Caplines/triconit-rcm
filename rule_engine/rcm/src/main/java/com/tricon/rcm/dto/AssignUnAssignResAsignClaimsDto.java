package com.tricon.rcm.dto;

import java.util.List;

import lombok.Data;

@Data
public class AssignUnAssignResAsignClaimsDto {

	List<String> claimIds;
	int teamId;
	String userId;
	int type;//assign/Reassign//unassign//ClaimAssignTypeEnum
	String comment;
	String clientId;//not always needed
	
}
