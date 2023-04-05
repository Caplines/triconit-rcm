package com.tricon.rcm.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.tricon.rcm.dto.RcmRolesResponseDto;
import com.tricon.rcm.util.Constants;

public enum RcmRoleEnum {

	TL("TL", "TeamLead",true),
	ASSO("ASSO", "Associate",true), 
	SYSTEM("SYSTEM", "System",false),
	ADMIN("ADMIN", "Admin",true),
	REPORTING("REPORTING","Reporting",true),
	SUPER_ADMIN("SUPER_ADMIN","Super Admin",true);

	final private String name;
	final private String fullName;
	final private boolean isRoleVisible;

	private RcmRoleEnum(String name, String fullName,boolean isRoleVisible) {
		this.name = name;
		this.fullName = fullName;
		this.isRoleVisible=isRoleVisible;
	}

	public String getName() {
		return name;
	}

	public String getFullName() {
		return fullName;
	}
	
	public boolean isRoleVisible() {
		return isRoleVisible;
	}
	
	public static String validateRoles(String role)
	{
		Optional<RcmRoleEnum> roles = Arrays.stream(values()).filter(x -> x.getName().equals(role)).findFirst();
		if(roles.isPresent()) {
			 
			 return roles.get().getName();
		}
		return null;
	}
	
	public static RcmRolesResponseDto getRoles(String role) {		
		String removePrefix=role.substring(5);
		Optional<RcmRoleEnum> data = Arrays.stream(values()).filter(x -> x.getName().equals(removePrefix)).findFirst();
		RcmRolesResponseDto rolesResponseDto = null;
		if (data.isPresent()) {
				rolesResponseDto = new RcmRolesResponseDto();
				rolesResponseDto.setRoleId(data.get().name());
				rolesResponseDto.setRoleName(data.get().getFullName());
				return rolesResponseDto;
			}
			return null;
		}
}
