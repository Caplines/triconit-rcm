package com.tricon.rcm.dto.download;

import lombok.Data;

@Data
public class ClaimDownloadDto {
	int	 billedAmount;
	int claimAge;
	String claimId;
	String claimType;
	String dos;
	String lastTeam;
	String officeName;
	String patientId;
	String patientName;
	String prName;
	int primTotal;
	String primaryInsurance;
	String secName;
	int secTotal;
	String secondaryInsurance;
	String statusType;
	String timelyFilingLimitData;
	String uuid;
}
