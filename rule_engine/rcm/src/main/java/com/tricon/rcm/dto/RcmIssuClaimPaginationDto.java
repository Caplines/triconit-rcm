package com.tricon.rcm.dto;

import java.util.List;

import com.tricon.rcm.dto.customquery.IssueClaimDto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class RcmIssuClaimPaginationDto {
	
	private List<com.tricon.rcm.dto.IssueClaimDto> data;
	private int totalPages;
	private int pageSize;
	private int pageNumber;
	private long totalElements;
}
