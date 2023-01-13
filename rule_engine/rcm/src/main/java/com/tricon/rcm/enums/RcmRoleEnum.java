package com.tricon.rcm.enums;


public enum RcmRoleEnum {

	TL("TL", "TeamLead", true), ASSO("ASSO", "Associate", true), SYSTEM("SYSTEM", "System", false),
	ADMIN("ADMIN", "Admin",true);

	final private String name;
	final private String fullName;
	final private boolean visibility;

	private RcmRoleEnum(String name, String fullName, boolean visibility2) {
		this.name = name;
		this.fullName = fullName;
		this.visibility = visibility2;
	}

	public String getName() {
		return name;
	}

	public String getFullName() {
		return fullName;
	}
	

	public boolean isVisibility() {
		return visibility;
	}

}
