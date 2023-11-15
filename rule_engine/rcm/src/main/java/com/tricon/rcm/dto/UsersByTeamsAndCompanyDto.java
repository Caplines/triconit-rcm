package com.tricon.rcm.dto;

import java.util.List;

import lombok.Data;

@Data
public class UsersByTeamsAndCompanyDto {

	private String clientName;
	private String clientUuid;
	private List<RcmUserToDto>users;
}
