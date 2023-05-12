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
	
	private List<IssueClaimDto> data;
	private Integer pageSize;
	private Integer pageNumber;
	private Long totalElements;
	private Long totalPages;
}
