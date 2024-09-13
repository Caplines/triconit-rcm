package com.tricon.rcm.dto.customquery;

public interface OpenAndUnopendClaimStatusDto {

	String getComments();

	int getId();

	Boolean getActive();

	String getCreatedDate();

	String getUpdatedDate();

	int getTeamId();
	
	String getClaimUuid();
}
