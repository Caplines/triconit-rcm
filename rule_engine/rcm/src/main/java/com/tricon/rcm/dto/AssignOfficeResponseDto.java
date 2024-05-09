package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class AssignOfficeResponseDto {

	private String userId;
	private String officeId;
	private Integer teamId;
	private String teamName;
	private boolean userExist;
}
