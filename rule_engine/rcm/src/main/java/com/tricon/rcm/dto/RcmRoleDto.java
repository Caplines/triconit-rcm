package com.tricon.rcm.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class RcmRoleDto {

	private String roleName;
	private String roleId;
	//private boolean isTeamMandatory;
}
