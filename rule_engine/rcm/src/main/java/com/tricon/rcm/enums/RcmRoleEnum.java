package com.tricon.rcm.enums;


public enum RcmRoleEnum {

	TL("TL","Team Lead"),
	ASSO("ASSO","Associate"),
	SYSTEM("SYSTEM","SYSTEM"),
	ADMIN("ADMIN","Admin");
	
	final private String name;
	final private String fullName;
	
	private RcmRoleEnum(String name, String fullName) {
		this.name = name;
		this.fullName = fullName;
	}

	public String getName() {
		return name;
	}

	public String getFullName() {
		return fullName;
	}
	


}
