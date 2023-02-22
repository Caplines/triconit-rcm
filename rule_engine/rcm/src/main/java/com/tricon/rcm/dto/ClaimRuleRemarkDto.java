package com.tricon.rcm.dto;

import java.util.List;

import lombok.Data;

@Data
public class ClaimRuleRemarkDto {

	String claimUuid;
	List<RuleRemarkDto> data;

}
