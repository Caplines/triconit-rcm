package com.tricon.rcm.enums;

public enum RcmCompanyEnum {
	
	Simplepoint("Simplepoint", new RcmRoleEnum[] { RcmRoleEnum.ADMIN }),
	UDG("UDG", new RcmRoleEnum[] {}),
	MAYERLAND("Meyerland", new RcmRoleEnum[] {}),
	GRANDPRARIE("Grand Prarie", new RcmRoleEnum[] {});
	
	final private String name;
	final private RcmRoleEnum[] defaultRoles;
	private RcmCompanyEnum(String name, RcmRoleEnum[] role) {
		this.name = name;
		this.defaultRoles = role;
	}
	public String getName() {
		return name;
	}
	public RcmRoleEnum[] getRole() {
		return defaultRoles;
	}

}
