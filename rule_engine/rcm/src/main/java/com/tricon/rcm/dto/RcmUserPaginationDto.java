package com.tricon.rcm.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class RcmUserPaginationDto {

    private List<RcmUserToDto>data;
	private Integer pageSize;
	private Integer pageNumber;
	private Long totalElements;
}
