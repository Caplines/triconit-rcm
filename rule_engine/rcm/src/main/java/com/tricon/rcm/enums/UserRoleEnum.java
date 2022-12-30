package com.tricon.rcm.enums;

import java.util.Arrays;
import java.util.Optional;

public enum UserRoleEnum {

	BILLING(1, "BILLING"), LC3(2, "LC3"), PATIENT_CALLING(3, "PATIENT_CALLING"), OFFICE(4, "OFFICE"),
	INTERNAL_AUDIT(5, "INTERNAL_AUDIT"), IV_TEAM(6, "IV_TEAM");

	final private int id;
	final private String type;

	private UserRoleEnum(int id, String type) {
		this.id = id;
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public static UserRoleEnum valueOf(int value) {
		Optional<UserRoleEnum> data = Arrays.stream(values()).filter(x -> x.getId() == value).findFirst();
		if (data.isPresent()) {
			return data.get();
		}
		return null;
	}
}
