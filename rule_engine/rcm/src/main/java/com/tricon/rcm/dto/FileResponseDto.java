package com.tricon.rcm.dto;

import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class FileResponseDto {

	private String msg;
	private boolean fileResponseStatus;
	
}
