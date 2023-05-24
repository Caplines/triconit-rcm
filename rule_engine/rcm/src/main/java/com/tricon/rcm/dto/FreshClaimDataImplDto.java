package com.tricon.rcm.dto;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;

import lombok.Data;

@Data
public class FreshClaimDataImplDto {

	String statusType;
	String primaryInsurance;
	String secondaryInsurance;
	String prName;
	String secName;
	int claimAge;
	String timelyFilingLimitData;
	float billedAmount;
	float primTotal;
	float secTotal;
	List<String> linkedClaims;
	String ivfId;
	String ivDos;
	
	String tpId;
	String tpDos;
	
	String uuid;
	String claimId;
	Date dos;
	Date patientDob;
	String patientId;
	String patientName;
	boolean pending;
	Date primDateSent;
	String primeStatus;
	Float primeTotalPaid;
	String source;
	Date secDateSend;
	String secStatus;
	Float secSubmittedTotal;
	Float submittedTotal;
	String timeFilLimitDay;
	String officeName;
	String officeUuid;
	int claimStatus;
	String lastTeam;
	String currentTeam;
	int currentTeamId;
	String primInsurance;
	String secInsurance;
	String groupNumber;
	String primePolicyHolder;
	Float primeSecSubmittedTotal;
	Date secPolicyHolderDob;
	Date createdDate;
	String assignedTo;
	String email;
	String firstName;
	String lastName;
	String primaryInsType;
	String secondaryInsType;
	String clientName;
	boolean regenerated;
	String secMemberId;
	String providerId;
	Date pulledDate;
	String secPolicyHolder;

	//AssignedTo
	int assignedToTeam;
	String assignedToTeamName;
	String assignedToRoleName;
	
	String assignedToUuid;
	String assignedToName;
	String assignedByRemark;
	
	int assignedByTeam;
	String assignedByName;
	String assignedByUuid;
	
	
	String claimRemarks;
	String assoicatedClaimUuid;//Primary Secondary
	boolean assoicatedClaimStatus;
	
	boolean  rcmToolChecksValidationsSheet;
	
	boolean primary;
	
	boolean allowEdit;
	
	boolean autoRuleRun;

	String dateLastUpdatedES;// This is DOS but not considered-- we have use Current Date as DOS in TP but in Claim we will consider this .
	String statusES;
	String estSecondaryES;
	String descriptionES;
		
	String treatingProvider;
	String providerOnClaim;
	String providerOnClaimFromSheet;//Provider Name from ES using TreatingProvider
	String treatingProviderFromClaim;//using ProviderId(ES Code) and Sheet
	
	String primePolicyHolderDob;
	String claimType;
	String firstTeam;
	int firstTeamId;
	String primaryEob;
	
	


}

