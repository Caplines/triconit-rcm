package com.tricon.rcm.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class ClaimSubmissionDto {
	
	String claimUuid;
	Date esDate;
	String channel;
	Boolean attachmentSend;
	Boolean preauth;
	Boolean refferalLetter;
	String claimNumber;
	String preauthNo;
	String providerRefNo;
	String esTime;
	Boolean cleanClaim;
	boolean primaryEOBAttached;

}
