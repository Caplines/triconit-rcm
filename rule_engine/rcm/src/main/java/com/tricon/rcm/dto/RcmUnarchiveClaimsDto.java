package com.tricon.rcm.dto;

import java.util.List;

import lombok.Data;
import lombok.ToString;


@Data
@ToString
public class RcmUnarchiveClaimsDto {

	 private List<UnarchiveClaimsPayloadDto>unarchiveClaims;
}
