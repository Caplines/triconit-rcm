package com.tricon.rcm.dto;

import java.util.List;

import lombok.Data;

@Data
public class ClaimEditDto {

	String claimUuid;
	List<ClaimRuleValidationDto> claimManualRuleValidationList;
	List<ClaimNoteDto> claimNoteDtoList;
	List<RuleRemarkDto> ruleRemarkDto;
	ClaimSubmissionDto submissionDto;
	List<ClaimServiceDto> serviceCodeValidationDto;
	String claimRemark;
	boolean submission;
}
