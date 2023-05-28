package com.tricon.rcm.dto.customquery;

import java.util.Date;

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

	

	//String getSource();

	
	
	//int getRebill();
	
	int getRemoteLiteRejections();
	
	int getAssignTeamId();
	
}
