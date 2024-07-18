package com.tricon.rcm.pdfDto;

import lombok.Data;

@Data
public class PatientStatementPdfDto {
	
	private String fname;
	private String lname;
	private String total;
	private String days;
	private String clientName;
	private StatementTypePdfDto statementType;

}
