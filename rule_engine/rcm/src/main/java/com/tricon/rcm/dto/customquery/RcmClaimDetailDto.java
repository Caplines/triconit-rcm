package com.tricon.rcm.dto.customquery;

import java.util.Date;

import javax.persistence.Column;

public interface RcmClaimDetailDto {

	String getUuid();
	String getClaimId();
	Date getDos();
	Date getPatientDob();
	String getPatientId();
	String getPatientName();
	boolean getPending();
	Date getPrimDateSent();
	String getPrimeStatus();
	Float getPrimeTotalPaid();
	String getSource();
	Date getSecDateSend();
	String getSecStatus();
	Float getSecSubmittedTotal();
	Float getSubmittedTotal();
	String getTimeFilLimitDay();
	String getOfficeName();
	String getOfficeUuid();
	int getClaimStatus();
	String getLastTeam();
	String getCurrentTeam();
	int getCurrentTeamId();
	String getPrimInsurance();
	String getSecInsurance();
	String getGroupNumber();
	String getPrimePolicyHolder();
	Float getPrimeSecSubmittedTotal();
	Date getSecPolicyHolderDob();
	Date getCreatedDate();
	String getAssignedTo();
	String getEmail();
	String getFirstName();
	String getLastName();
	String getPrimaryInsType();
	String getSecondaryInsType();
	String getPrimaryInsCode();
	String getSecondaryInsCode();
	String getClientName();
	boolean getRegenerated();
	String getSecMemberId();
    String getProviderId();
	Date getPulledDate();
	String getSecPolicyHolder();
	boolean getAutoRuleRun();
	String getPrimePolicyHolderDob();
	
	
    String getDateLastUpdatedES();// This is DOS but not considered-- we have use Current Date as DOS in TP but in Claim we will consider this .
	String getStatusES();
	String getEstSecondaryES();
	String getDescriptionES();
	String getTreatingProvider();
	String getProviderOnClaim();
	String getProviderOnClaimFromSheet();//Provider Name from ES using TreatingProvider
	String getTreatingProviderFromClaim();//using ProviderId(ES Code) and Sheet
	String getIvId();
	String getIvDos();
	String getTpId();
	String getTpDos();
	String getClaimType();
	String getFirstTeam();
	int getFirstTeamId();
	String getPrimaryEob();
	
	String getPrimaryInsCodeSheet();
	String getSecondaryInsCodeSheet();
	
}
