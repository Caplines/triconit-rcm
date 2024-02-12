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
	List<ClaimServiceDto> serCVDto;
	String claimRemark;
	String ruleEngineRunRemark;
	boolean submission;
	//boolean assignToTL;
	boolean assignToOtherTeam;
	String assignToComment;
	String assignTouuid;
	int assignToTeam;
	boolean byPassPendingCheck;
}
