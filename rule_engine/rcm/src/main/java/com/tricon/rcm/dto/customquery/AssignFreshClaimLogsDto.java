package com.tricon.rcm.dto.customquery;

import java.util.Date;

public interface AssignFreshClaimLogsDto {

	String getOfficeName();
	
	int getCount();
	//
	Date getOpdt();

	Date getOpdos();
	
	String getOfficeUuid();
	
	String getFName();
	
	String getLName();
	
	String getAssignedUser();

	

	//String getSource();

	
	
	//int getRebill();
	
	int getRemoteLiteRejections();
	
}
