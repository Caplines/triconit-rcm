package com.tricon.rcm.enums;

import java.util.Arrays;
import java.util.Optional;

public enum InsuranceTypeEnum {

	PPO("PPO", 15), HMO("HMO", 15), AMC("AMC", 7), CMC("CMC", 7), MCR("MCR", 7), CHIP("CHIP", 7),
	OON_PPO("OON PPO", 15), HMO_MCR("HMO MCR", 15);

	final private String insType;
	final private int days;

	private InsuranceTypeEnum(String insType, int days) {
		this.insType = insType;
		this.days = days;
	}

	public String getInsType() {
		return insType;
	}

	public int getDays() {
		return days;
	}
	
	
	public static int getDaysByType(String insType) {
		int days = 0;
		Optional<InsuranceTypeEnum> data = Arrays.stream(values()).filter(x -> x.getInsType().equals(insType))
				.findFirst();
		if (data.isPresent()) {
			days = data.get().getDays();
			return days;
		}
		return 0;
	}

}
