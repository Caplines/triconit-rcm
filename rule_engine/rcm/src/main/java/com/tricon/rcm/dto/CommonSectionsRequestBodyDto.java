package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class CommonSectionsRequestBodyDto {
	
	private String claimUuid;
	private ClaimLevelInformationDto claimInfoModel;
	private AppealInformationDto appealInfoModel;
	private boolean finalSubmit;
	private boolean moveToNextTeam;

}
