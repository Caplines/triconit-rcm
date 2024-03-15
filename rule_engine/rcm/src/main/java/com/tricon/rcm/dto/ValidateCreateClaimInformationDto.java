package com.tricon.rcm.dto;

import java.util.List;

import lombok.Data;

@Data
public class ValidateCreateClaimInformationDto {

	private String currentClaimUuid;
	private String newClaimId;
	private String buttonType;
	//private List<String>selectedServiceCodes;
}
