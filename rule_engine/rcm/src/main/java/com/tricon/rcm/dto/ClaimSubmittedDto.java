package com.tricon.rcm.dto;

import java.util.Date;

import lombok.Data;

@Data
public class ClaimSubmittedDto {
	private String officeUuid;
	private Date submittedDate;
	private boolean isSubmitted;
	private boolean isNotSubmitted;
	private Date dos;
}
