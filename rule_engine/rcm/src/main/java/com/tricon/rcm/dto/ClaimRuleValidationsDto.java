package com.tricon.rcm.dto;

import java.util.List;

import lombok.Data;

@Data
public class ClaimRuleValidationsDto {


	String claimUuid;
	List<ClaimRuleValidationDto> data;
}
