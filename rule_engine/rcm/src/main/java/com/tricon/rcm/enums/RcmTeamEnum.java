package com.tricon.rcm.enums;

public enum RcmTeamEnum {

	SYSYEM(1,"SYSTEM",new RcmRoleEnum[]{RcmRoleEnum.SYSTEM}),
	LC3(1,"SYSTEM",new RcmRoleEnum[]{RcmRoleEnum.TL,RcmRoleEnum.ASSO});

	
	final private int id;
	final private String name;
	final private RcmRoleEnum[] role;
	
	
	private RcmTeamEnum(int id,String name,RcmRoleEnum[] role) {
		this.id = id;
		this.name = name;
		this.role = role;
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




	

	
}
