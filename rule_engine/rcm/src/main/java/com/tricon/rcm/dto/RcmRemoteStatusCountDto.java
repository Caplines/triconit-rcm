package com.tricon.rcm.dto;

import javax.persistence.Column;

import lombok.Data;

@Data
public class RcmRemoteStatusCountDto {

	
	private int rejectedCount;
	private int acceptedCount;
	private int printedCount;
	private int duplicateCount;
}
