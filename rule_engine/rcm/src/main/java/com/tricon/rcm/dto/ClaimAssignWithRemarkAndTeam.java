package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class ClaimAssignWithRemarkAndTeam {
	
	private String claimUuid;
	private Integer assignToTeamId;
	private String remark;
	

}
