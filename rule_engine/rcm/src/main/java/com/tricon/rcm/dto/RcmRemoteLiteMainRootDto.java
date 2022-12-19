package com.tricon.rcm.dto;

import java.util.List;

import lombok.Data;

@Data
public class RcmRemoteLiteMainRootDto {

	String message;
	List<RcmRemoteLiteSiteDetailsDto> data;
	String status;
	
}
