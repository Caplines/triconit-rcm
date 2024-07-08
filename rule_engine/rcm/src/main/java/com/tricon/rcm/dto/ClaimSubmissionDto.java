package com.tricon.rcm.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class ClaimSubmissionDto {
	
	String claimUuid;
	Date esDate;
	String channel;
	boolean attachmentSend;
	boolean preauth;
	boolean refferalLetter;
	String claimNumber;
	String preauthNo;
	String providerRefNo;
	String esTime;
	boolean cleanClaim;

}
