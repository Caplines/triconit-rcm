package com.tricon.rcm.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class FreshClaimDataImplDto {

	String officeName;
	String officeUuid;
	String uuid;
	String claimId;
	String patientId;
	Date dos;
	String patientName;
	String statusType;
	String primaryInsurance;
	String secondaryInsurance;
	String prName;
	String secName;
	String lastTeam;
	int claimAge;
	String timelyFilingLimitData;
	float billedAmount;
	float primTotal;
	float secTotal;
	List<String> linkedClaims;
	String ivfId;
	String tpId;
	
	

}
