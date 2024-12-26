package com.tricon.rcm.enums;

import java.util.Arrays;
import java.util.Optional;

public enum ClaimAssignTypeEnum {

	ASSIGNOTHERTEAM("AssignOtherTeam", 2), ASSIGNSAMETEAM("AssignSameTeam", 1), UNASSIGN("UNASSIGN", 3);

	final private String name;
	final private int value;

	private ClaimAssignTypeEnum(String name, int value) {
		this.name = name;
		this.value = value;
		
	}

	public String getName() {
		return name;
	}

	public int getValue() {
		return value;
	}
	
	

	public static ClaimAssignTypeEnum getTypeOfValue(int value) {
		Optional<ClaimAssignTypeEnum> type = Arrays.stream(values()).filter(x -> x.getValue() == value).findFirst();
		if (type.isPresent())
			return type.get();
		else
			return null;
	}
	
}
