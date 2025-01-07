package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class RcmFollowUpInsuranceDto {

	private String modeOfFollowUp;
	private String refNumber;
	private String insuranceRepName;
	private String currentClaimStatus;
	private String followUpRemarks;
	private String nextFollowUpRequired;
	private String nextFollowUpDate;
	private String followByTeam;
	private String followByUser;
	private String followByUserLastName;
	private int sectionId;
	private String typeOfFollowUp;//Appeal or Claim
}
