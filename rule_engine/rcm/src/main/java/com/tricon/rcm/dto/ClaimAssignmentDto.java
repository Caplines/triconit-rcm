package com.tricon.rcm.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ClaimAssignmentDto {
 
	 private String oldClaimUserUuid;
	 private String newClaimUserUuid;
}
