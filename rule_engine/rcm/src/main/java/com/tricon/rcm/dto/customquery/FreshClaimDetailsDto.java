package com.tricon.rcm.dto.customquery;

import java.util.Date;

public interface FreshClaimDetailsDto {

	String getOfficeUuid();

	String getOfficeName();

	Date getOpdt();

	Date getOpdos();

	//String getSource();

	int getCount();
	
	//int getRebill();
	
	int getRemoteLiteRejections();
	
	void setRemoteLiteRejections(int num);
}
