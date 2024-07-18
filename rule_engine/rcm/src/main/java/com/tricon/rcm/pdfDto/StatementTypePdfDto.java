package com.tricon.rcm.pdfDto;

import lombok.Data;

@Data
public class StatementTypePdfDto {

	private String statementType1;
	private String statementType2;
	private String statementType3;

	public String getStatementType1() {
		return statementType1;
	}

	public void setStatementType1(String statementType1) {
		this.statementType1 = statementType1;
	}

	public String getStatementType2() {
		return statementType2;
	}

	public void setStatementType2(String statementType2) {
		this.statementType2 = statementType2;
	}

	public String getStatementType3() {
		return statementType3;
	}

	public void setStatementType3(String statementType3) {
		this.statementType3 = statementType3;
	}

	@Override
	public String toString() {
		return "StatementType [statementType1=" + statementType1 + ", statementType2=" + statementType2
				+ ", statementType3=" + statementType3 + "]";
	}

}
