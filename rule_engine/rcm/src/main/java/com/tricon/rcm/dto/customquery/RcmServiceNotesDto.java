package com.tricon.rcm.dto.customquery;

import java.util.Date;

public interface RcmServiceNotesDto {
	
	String getServiceCode();
	String getNotes();
	String getCreatedBy();
	String getTeamName();
	Date getDate();

}
