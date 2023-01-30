package com.tricon.rcm.enums;


public enum RcmRoleEnum {

	TL("TL", "TeamLead", true,false,true), ASSO("ASSO", "Associate", true,false,true), SYSTEM("SYSTEM", "System", false,false,false),
	ADMIN("ADMIN", "Admin",true,false,false),CLIENT_MANAGER("CLIENT_MANAGER", "Client Manager",false,true,true);

	final private String name;
	final private String fullName;
	final private boolean roleVisibilityForSmilepoint;
	final private boolean roleVisibilityForOthers;
	final private boolean isTeamMandatory;

	private RcmRoleEnum(String name, String fullName, boolean roleVisibilityForSmilepoint, boolean roleVisibilityForOthers,boolean  isTeamMandatory) {
		this.name = name;
		this.fullName = fullName;
		this.roleVisibilityForSmilepoint = roleVisibilityForSmilepoint;
		this.roleVisibilityForOthers = roleVisibilityForOthers;
		this. isTeamMandatory= isTeamMandatory;
	}

	public String getName() {
		return name;
	}

	public String getFullName() {
		return fullName;
	}
	

	public boolean isRoleVisibilityForSmilepoint() {
		return roleVisibilityForSmilepoint;
	}

	public boolean isRoleVisibilityForOthers() {
		return roleVisibilityForOthers;
	}

	public boolean isTeamMandatory() {
		return isTeamMandatory;
	}
	

}
