package com.tricon.rcm.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class IssueClaimDto {

	private String claimId;
	private String issue;
	private String source;
	private String officeName;
	private Date createdDate; 
	private int id;
	private boolean isArchive;
	public IssueClaimDto(String claimId, String issue, String source, String officeName, Date createdDate, int id,
			boolean isArchive) {
		super();
		this.claimId = claimId;
		this.issue = issue;
		this.source = source;
		this.officeName = officeName;
		this.createdDate = createdDate;
		this.id = id;
		this.isArchive = isArchive;
	}
	
	
	
}
