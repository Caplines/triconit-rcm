package com.tricon.rcm.dto.customquery;

import java.util.Date;

public interface FreshClaimLogDto {

	String getOfficeUuid();
	
	String getOfficeName();

	Date getCd();
	
	int getNewClaimsCount();
	
	String getSource();
	
	String getStatus();
	
}
