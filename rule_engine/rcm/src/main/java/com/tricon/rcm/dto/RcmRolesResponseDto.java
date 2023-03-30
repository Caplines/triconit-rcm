package com.tricon.rcm.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class RcmRolesResponseDto {

	private String roleId;
	private String roleName;
	//private String fullRoleName;
}
