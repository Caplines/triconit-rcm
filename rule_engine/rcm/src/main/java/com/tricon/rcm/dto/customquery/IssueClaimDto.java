package com.tricon.rcm.dto.customquery;

import java.util.Date;

public interface IssueClaimDto {

	String getClaimId();
	String getIssue();
	String getSource();
	String getOfficeName();
	Date getCreatedDate(); 
	int getId();
	boolean getIsArchive();
	
}
