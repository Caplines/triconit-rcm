package com.tricon.rcm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListOfClaimsCountsDto {

	private String officeUuid;
	private Integer teamId;
	private String pageName;
	private String countType;
	// for pagination
	private int pageNumber;

}
