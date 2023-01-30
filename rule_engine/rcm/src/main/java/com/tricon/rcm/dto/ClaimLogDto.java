package com.tricon.rcm.dto;

import java.util.Date;

import lombok.Data;

@Data
public class ClaimLogDto {

	
	String source;
	String officeUuid;
	int status;
	int newClaimsCount;
	Date cd;
	String officeName;
	
	public ClaimLogDto(String source, String officeUuid, int status, int newClaimsCount, Date cd,String officeName) {
		super();
		this.source = source;
		this.officeUuid = officeUuid;
		this.status = status;
		this.newClaimsCount = newClaimsCount;
		this.cd = cd;
		this.officeName=officeName;
	}
	
	
}
