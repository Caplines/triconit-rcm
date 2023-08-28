package com.tricon.rcm.dto;

import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RcmArchiveClaimsDto {

	     private List<ArchiveClaimsPayloadDto>archiveClaims;
}
