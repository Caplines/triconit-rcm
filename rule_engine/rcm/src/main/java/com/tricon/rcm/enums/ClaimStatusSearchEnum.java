package com.tricon.rcm.enums;

import java.util.Arrays;
import java.util.Optional;

public enum ClaimStatusSearchEnum {
	STATUS_BILLED("Billed", 0), STATUS_UNBILLED("Unbilled", 1),STATUS_CLOSED("Closed", 2),STATUS_OPEN("Open", 4);

	final private String status;
	final private int value;

	private ClaimStatusSearchEnum(String status, int value) {
		this.status = status;
		this.value = value;
	}

	public String getStatus() {
		return status;
	}

	public int getValue() {
		return value;
	}
	
	public static int getStatusByValue(int value) {
		Optional<ClaimStatusSearchEnum> status = Arrays.stream(values()).filter(x -> x.getValue() == value).findFirst();
		if (status.isPresent())
			return status.get().getValue();
		else
			return 0;
	}
	
	public static int getStatusByStatus(String status) {
		Optional<ClaimStatusSearchEnum> search = Arrays.stream(values()).filter(x -> x.getStatus().equals(status)).findFirst();
		if (search.isPresent())
			return search.get().getValue();
		else
			return -1;
	}
	
	
}
