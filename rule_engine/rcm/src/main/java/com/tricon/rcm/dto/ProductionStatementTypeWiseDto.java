package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class ProductionStatementTypeWiseDto {

	private String userUuid;
	private int total;
	private int days;
	private String fname;
	private String clientName;
	private StatementType statementType;

	public class StatementType {

		private int statementType1;
		private int statementType2;
		private int statementType3;

		public int getStatementType1() {
			return statementType1;
		}

		public void setStatementType1(int statementType1) {
			this.statementType1 = statementType1;
		}

		public int getStatementType2() {
			return statementType2;
		}

		public void setStatementType2(int statementType2) {
			this.statementType2 = statementType2;
		}

		public int getStatementType3() {
			return statementType3;
		}

		public void setStatementType3(int statementType3) {
			this.statementType3 = statementType3;
		}

	}

	public ProductionStatementTypeWiseDto(String userUuid, int total, int days, String fname, String clientName,
			StatementType statementType) {
		super();
		this.userUuid = userUuid;
		this.total = total;
		this.days = days;
		this.fname = fname;
		this.clientName = clientName;
		this.statementType = statementType;
	}
	
	public ProductionStatementTypeWiseDto() {
		// TODO Auto-generated constructor stub
	}

}
