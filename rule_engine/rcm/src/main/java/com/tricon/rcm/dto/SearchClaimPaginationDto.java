package com.tricon.rcm.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class SearchClaimPaginationDto {

	private List<SearchClaimResponseDto> data;
	private int totalPages;
	private int pageSize;
	private int pageNumber;
	private long totalElements;
}
