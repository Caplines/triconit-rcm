package com.tricon.rcm.dto.customquery;

import java.util.Date;

import lombok.Data;

@Data
public class AssignFreshClaimLogsImplDto implements AssignFreshClaimLogsDto{

	String officeUuid;

	String officeName;

	String opdt;

	String opdos;
	
	Date opdtd;

	Date opdosd;

	int count;
	
	String assignedUser;
	
	String fName;
	
	String lName;
	
	
	int remoteLiteRejections;

	

    int assignTeamId;
	



}
