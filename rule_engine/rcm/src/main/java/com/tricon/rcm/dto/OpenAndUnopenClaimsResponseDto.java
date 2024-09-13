package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class OpenAndUnopenClaimsResponseDto {

	
	private String comments;

	private int id;

	private boolean active;

	private String createdDate;

	private String updatedDate;

	private int teamId;
	
	private String claimId;
	
	private String officeName;
	
	private String clientName;
}
