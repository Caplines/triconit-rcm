package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class RcmPatientStatementDto {

	private String modeOfStatement;
	private String reason;
	private String statementType;
	private String status;
	private double amountStatement;
	private String nextReviewDate;
	private String nextStatementDate;
	private String statementSendingDate;
	private String remarks;
	private String statementNotes;
	private String balanceSheetLink;
	private byte buttonType;
	private int sectionId;
}
