package com.tricon.rcm.enums;

import java.util.Arrays;
import java.util.Optional;

import com.tricon.rcm.util.Constants;

public enum RcmTeamEnum {

	SYSYEM(1, "SYSTEM", new RcmRoleEnum[] { RcmRoleEnum.SYSTEM },true,false),
	ADMIN(2, "ADMIN", new RcmRoleEnum[] { RcmRoleEnum.ADMIN },true,false),
	PATIENT_CALLING(3, "PATIENT_CALLING", new RcmRoleEnum[] { RcmRoleEnum.TL, RcmRoleEnum.ASSO },true,true),
	OFFICE(4, "OFFICE", new RcmRoleEnum[] { RcmRoleEnum.TL, RcmRoleEnum.ASSO },true,true),
	INTERNAL_AUDIT(5, "INTERNAL_AUDIT", new RcmRoleEnum[] { RcmRoleEnum.TL, RcmRoleEnum.ASSO },true,true),
	IV_TEAM(6, "IV_TEAM", new RcmRoleEnum[] { RcmRoleEnum.TL, RcmRoleEnum.ASSO },true,true),
	BILLING(7, "BILLING", new RcmRoleEnum[] { RcmRoleEnum.TL, RcmRoleEnum.ASSO },true,true),
	LC3(8, "LC3", new RcmRoleEnum[] { RcmRoleEnum.TL, RcmRoleEnum.ASSO },true,true),
	OTHER(9, "OTHER", new RcmRoleEnum[] { RcmRoleEnum.TL},false,true);
	
	final private int id;
	final private String name;
	final private RcmRoleEnum[] role;
	final private boolean isSmilepoint;
	final private boolean isRoleVisible;
	
	private RcmTeamEnum(int id,String name,RcmRoleEnum[] role,boolean isSmilepoint, boolean isRoleVisible) {
		this.id = id;
		this.name = name;
		this.role = role;
		this.isSmilepoint=isSmilepoint;
		this.isRoleVisible=isRoleVisible;
	}


	public boolean isRoleVisible() {
		return isRoleVisible;
	}


	public boolean isSmilepoint() {
		return isSmilepoint;
	}


	public int getId() {
		return id;
	}


	public String getName() {
		return name;
	}


	public RcmRoleEnum[] getRole() {
		return role;
	}




	

	
	public static String generateRole(int value, String roleType) {
		Optional<RcmTeamEnum> teamEnum = Arrays.stream(values()).filter(x -> x.getId() == value).findFirst();
		Optional<RcmRoleEnum> roleEnum = Arrays.stream(RcmRoleEnum.values())
				.filter(x -> x.getFullName().equals(roleType) && x.isVisibility() == true).findFirst();
		String teamName = "", roleName = "", role = "";
		if (teamEnum.isPresent() && roleEnum.isPresent()) {
			teamName = teamEnum.get().getName();
			roleName = roleEnum.get().getName();
			role = Constants.ROLE_PREFIX.concat(teamName).concat(Constants.HYPHEN).concat(roleName);
			return role;
		}
		if(value==0 &&roleEnum.isPresent()) {
			roleName = roleEnum.get().getName();
			role = Constants.ROLE_PREFIX.concat(roleName);
			return role;
		}
		return null;
	}
}
