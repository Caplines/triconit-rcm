package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class ProductionStatementTypeWiseDto {

	private String userUuid;
	private int total;
	private int days;
	private String fname;
	private String lname;
	private String clientName;
	private StatementType statementType;



	public ProductionStatementTypeWiseDto(String userUuid, int total, int days, String fname, String clientName,String lname,
			StatementType statementType) {
		super();
		this.userUuid = userUuid;
		this.total = total;
		this.days = days;
		this.fname = fname;
		this.lname = fname;
		this.clientName = clientName;
		this.statementType = statementType;
	}
	
	public ProductionStatementTypeWiseDto() {
		// TODO Auto-generated constructor stub
	}

}
