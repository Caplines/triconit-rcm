package com.tricon.rcm.dto.customquery;



public interface AssignFreshClaimLogsDto {

	String getOfficeName();
	
	int getCount();
	//
	String getOpdt(); 

	String getOpdos(); 
	
	String getOfficeUuid();
	
	String getFName();
	
	String getLName();
	
	String getAssignedUser();
	
	String getStatusESUpdated();

	String getClaimId();
	
	int getPrimaryC();

	//String getSource();

	
	
	//int getRebill();
	
	int getRemoteLiteRejections();
	
	int getAssignTeamId();
	
	String getCompanyName();
	
	int getPending();
	
	int getRebilledStatus();
	
}
