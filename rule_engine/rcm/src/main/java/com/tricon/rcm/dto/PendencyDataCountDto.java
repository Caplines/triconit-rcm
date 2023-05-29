package com.tricon.rcm.dto;


import java.util.List;

import lombok.Data;

@Data
public class PendencyDataCountDto {

	
	String officeName;
	String officeUUid;
	String teamName;
	int teamId;
	List<PendencyKeyValDto> keyData; 
}
