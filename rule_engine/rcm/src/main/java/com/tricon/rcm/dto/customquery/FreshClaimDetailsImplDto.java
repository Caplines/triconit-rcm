package com.tricon.rcm.dto.customquery;

import java.util.Date;

import lombok.Data;

@Data
public class FreshClaimDetailsImplDto  {
	
	String officeUuid;

	String officeName;

	Date opdt;

	Date opdos;

	//String getSource();

	int count;
	
	//int getRebill();
	
	int remoteLiteRejections;
	
	
}
