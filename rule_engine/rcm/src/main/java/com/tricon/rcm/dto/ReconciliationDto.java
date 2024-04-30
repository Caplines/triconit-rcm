package com.tricon.rcm.dto;

import java.util.Date;

import lombok.Data;

@Data
public class ReconciliationDto {

	private String officeUuid;
	private Date startDate;
	private Date endDate;
}
