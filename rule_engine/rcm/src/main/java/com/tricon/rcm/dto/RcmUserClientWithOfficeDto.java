package com.tricon.rcm.dto;

import java.util.List;

import lombok.Data;

@Data
public class RcmUserClientWithOfficeDto {
          
	private String clientUuid;
	private String clientName;
	private List<RcmOfficeDto> offices;
}
