package com.tricon.rcm.enums;

import java.util.Arrays;
import java.util.Optional;

public enum AgeBracketEnum {

	AGE_BRACKET_0_30("0-30", 1,30,0),
    AGE_BRACKET_31_60("31-60", 2,31,60),
    AGE_BRACKET_61_90("61-90", 3,61,90),
    AGE_BRACKET_90("90+", 4,90,0);

	final private String ageCategory;
	final private int value;
	final private int firstRange;
	final private int secondRange;

	private AgeBracketEnum(String ageCategory, int value,int firstRange,int secondRange) {
		this.ageCategory = ageCategory;
		this.value = value;
		this.firstRange = firstRange;
		this.secondRange = secondRange;
	}

	public String getAgeCategory() {
		return ageCategory;
	}

	public int getValue() {
		return value;
	}
	
	public int getFirstRange() {
		return firstRange;
	}

	public int getSecondRange() {
		return secondRange;
	}

	public static int getAgeByValue(int value) {
		Optional<AgeBracketEnum> age = Arrays.stream(values()).filter(x -> x.getValue() == value).findFirst();
		if (age.isPresent())
			return age.get().getValue();
		else
			return 0;
	}
	
	public static int getRangeByValue(int value, int range) {
		Optional<AgeBracketEnum> age = Arrays.stream(values()).filter(x -> x.getValue() == value).findFirst();
		int rangeValue = 0;
		if (age.isPresent()) {
			if (age.get().getValue() == 1 && range == 1)
				rangeValue = age.get().getFirstRange();
			else if (age.get().getValue() == 2 && range == 1)
				rangeValue = age.get().getFirstRange();
			else if (age.get().getValue() == 2 && range == 2)
				rangeValue = age.get().getSecondRange();
			else if (age.get().getValue() == 3 && range == 1)
				rangeValue = age.get().getFirstRange();
			else if (age.get().getValue() == 3 && range == 2)
				rangeValue = age.get().getSecondRange();
			else if (age.get().getValue() == 4 && range == 1)
				rangeValue = age.get().getFirstRange();
			else
				return rangeValue;
		} else
			return rangeValue;
		return rangeValue;
	}
}
