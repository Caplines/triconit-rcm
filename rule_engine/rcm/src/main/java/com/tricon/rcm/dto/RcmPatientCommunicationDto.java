package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class RcmPatientCommunicationDto {

	private String remarks;
	private String desposition;
	private String modeOfFollowUp;
	private String contact;
	private int createdTeamId;
	private String createdBy;
	private String date;
	private int sectionId;
}
