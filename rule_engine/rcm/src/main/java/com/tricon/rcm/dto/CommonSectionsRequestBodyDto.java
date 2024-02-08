package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class CommonSectionsRequestBodyDto {
	
	private Integer sectionId;
	private String claimUuid;
	private ClaimLevelInformationDto claimInfoModel;
	private AppealInformationDto appealInfoModel;
	private boolean finalSubmit;

}
