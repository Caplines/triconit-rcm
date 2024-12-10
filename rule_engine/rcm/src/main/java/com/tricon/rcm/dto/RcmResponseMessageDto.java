package com.tricon.rcm.dto;

import com.tricon.rcm.db.entity.RcmOffice;

import lombok.Data;

@Data
public class RcmResponseMessageDto {

	private String message;
	private boolean responseStatus;
	//private RcmOffice office;
}
