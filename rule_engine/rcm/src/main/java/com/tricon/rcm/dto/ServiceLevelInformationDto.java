package com.tricon.rcm.dto;

import java.util.List;

import lombok.Data;

@Data
public class ServiceLevelInformationDto {

	private int sectionId;
	private List<ServiceLevelRequestBodyDto> serviceLevelBody;
}
