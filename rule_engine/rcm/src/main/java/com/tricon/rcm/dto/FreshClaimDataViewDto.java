package com.tricon.rcm.dto;

import java.util.Date;

import com.tricon.rcm.dto.customquery.FreshClaimDataDto;

import lombok.Data;

@Data
public class FreshClaimDataViewDto {
	
	String OfficeName;
	String uuid;
	String claimId;
	String opatientId;
	Date dos;
	String patientName;
	String statusType;
	String primaryInsurance;
	String secondaryInsurance;
	String pPrName;
	String secName;
	String lastTeam;
	int claimAge;
	String timelyFilingLimitData;
	float billedAmount;
	float primTotal;
	float secTotal;
	Float primeSecSubmittedTotal;
	Integer attachmentCount;
	String  lastTeamRemark;

}
