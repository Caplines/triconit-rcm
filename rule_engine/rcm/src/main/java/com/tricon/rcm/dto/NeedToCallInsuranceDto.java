package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class NeedToCallInsuranceDto {
	
	private int teamToCall;
	private String reasonOfCalling;
	private String remarks;
	private String dateOfCalling;
	private int sectionId;

}
