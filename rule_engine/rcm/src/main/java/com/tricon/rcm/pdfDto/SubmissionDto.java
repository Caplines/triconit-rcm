package com.tricon.rcm.pdfDto;

import lombok.Data;

@Data
public class SubmissionDto {
	private String claimUuid;
	private String esDate;
	private String channel;
	private boolean attachmentSend;
	private boolean preauth;
	private boolean refferalLetter;
	private String claimNumber;
	private String preauthNo;
	private String providerRefNo;
	private String 	esTime;
}
