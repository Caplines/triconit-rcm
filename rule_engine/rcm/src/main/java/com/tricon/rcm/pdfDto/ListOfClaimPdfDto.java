package com.tricon.rcm.pdfDto;

import lombok.Data;

@Data
public class ListOfClaimPdfDto {
	private String  billedAmount;
	private String claimAge;
	private String claimId;
	private String dos;
	private String lastTeam;
	private String officeName;
	private String patientId;
	private String patientName;
	private String prName;
	private String primTotal;
	private String primaryInsurance;
	private String secName;
	private String secTotal;
	private String secondaryInsurance;
	private String statusType;
	private String timelyFilingLimitData;
	private String uuid;
	private String primeSecSubmittedTotal;
	private String pendingSince;
	private String ageBracket;
	private String newClaimId;
}
