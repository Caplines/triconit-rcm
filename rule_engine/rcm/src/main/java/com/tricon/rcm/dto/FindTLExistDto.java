package com.tricon.rcm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FindTLExistDto {

	private Integer assignToTeamId;
	private String claimUuid;
}
