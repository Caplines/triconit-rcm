package com.tricon.rcm.dto.customquery;

import java.util.Date;

public interface FreshClaimDataDto {
	
	String getOfficeName();
	String getUuid();
	String getClaimId();
	String getPatientId();
	Date getDos();
	String getPatientName();
	String getStatusType();
	String getPrimaryInsurance();
	String getSecondaryInsurance();
	String getPrName();
	String getSecName();
	String getLastTeam();
	int getClaimAge();
	String getTimelyFilingLimitData();
	float getBilledAmount();
	float getPrimTotal();
	float getSecTotal();
	Float getPrimeSecSubmittedTotal();
	Integer getAttachmentCount();
	String getLastTeamRemark();
	Date getPendingSince();
	
	
	String getStatusES();
	String getStatusESUpdated();
	int getNextAction();
	Date getFollowUpDate();
	Float getDueBalance();
	Date getUpdatedDate();
	
	String getProviderSpeciality();
	
	String getClaimStatus();
	
	boolean getClaimTypeStatus();
	String getAssignedToFName();
	String getAssignedToLName();
	String getAssignedToTeam();
	int getRebilledStatus();
	String getSecondaryStarted();
	
}
