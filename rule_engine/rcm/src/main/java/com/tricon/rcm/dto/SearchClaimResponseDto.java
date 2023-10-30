package com.tricon.rcm.dto;

import java.util.Date;

import lombok.Data;

@Data
public class SearchClaimResponseDto {

	private String officeName;
	private String uuid;
	private String claimId;
	private String patientId;
	private Date dos;
	private String patientName;;
	private String statusType;
	private String primaryInsurance;
	private String secondaryInsurance;
	private String prName;
	private String secName;
	private int claimAge;
	private String timelyFilingLimitData;
	private float billedAmount;
	private float primTotal;
	private float secTotal;
	private Float primeSecSubmittedTotal;
	private String clientName;
}
