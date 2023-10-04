package com.tricon.rcm.pdfDto;



import lombok.Data;

@Data
public class IssueClaimPdfDto {

	private String officeName;
	private String claimId;
	private String createdDate;
	private String source;
	private String issue;
	private String newClaimId;
}
