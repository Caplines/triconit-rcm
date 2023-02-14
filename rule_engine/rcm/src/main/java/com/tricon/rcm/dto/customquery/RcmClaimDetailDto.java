package com.tricon.rcm.dto.customquery;

import java.util.Date;

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
	String getSimeFilLimitDay();
	String getOfficeName();
	String getOfficeUuid();
	int getClaimStatus();
	String getLastTeam();
	String getCurrentTeam();
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
	String getClientName();
	boolean getRegenerated();
	String getSecMemberId();
	
	
}
