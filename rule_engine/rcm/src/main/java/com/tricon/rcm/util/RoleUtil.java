package com.tricon.rcm.util;

import com.tricon.rcm.enums.UserRoleEnum;

public class RoleUtil {

	public static String generateRole(UserRoleEnum teamName, String role) {
		role = Constants.ROLE_PREFIX.concat(teamName.getType()).concat(Constants.HYPHEN).concat(role);
		return role;
	}
}
