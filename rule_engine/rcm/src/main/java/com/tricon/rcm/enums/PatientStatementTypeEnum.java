package com.tricon.rcm.enums;

import java.util.Arrays;
import java.util.Optional;

public enum PatientStatementTypeEnum {

	STATEMENT_1(1,"From Claim Closing", 7),
	STATEMENT_2(2,"From 1st Statement",15),
	STATEMENT_3(3,"From 2nd Statement",15);
	

	final private String remark;
	final private int days;
	final private int type;

	private PatientStatementTypeEnum(int type,String remark, int days) {
		this.type = type;
		this.remark = remark;
		this.days = days;
	
	}

	public int getType() {
		return type;
	}

	public String getRemark() {
		return remark;
	}


	public int getDays() {
		return days;
	}

	public static PatientStatementTypeEnum getPatientStatementTypeByType(int type) {
		Optional<PatientStatementTypeEnum> statement = Arrays.stream(values()).filter(x -> x.getType() == type).findFirst();
		if (statement.isPresent())
			return statement.get();
		else
			return null;
	}
	
	public static PatientStatementTypeEnum getPatientStatementTypeByRemark(String remark) {
		Optional<PatientStatementTypeEnum> statement = Arrays.stream(values()).filter(x -> x.getRemark() == remark).findFirst();
		if (statement.isPresent())
			return statement.get();
		else
			return null;
	}
	
	
}
