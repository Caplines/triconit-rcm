package com.tricon.rcm.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class ClientSectionMappingDto {

	private String clientUuid;
	private String clientName;
	private String userUuid;
	private List<RcmTeamSectionAccessDto>teamsWithSections;
}
