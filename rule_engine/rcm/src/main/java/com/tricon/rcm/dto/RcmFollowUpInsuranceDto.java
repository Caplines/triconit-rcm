package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class RcmFollowUpInsuranceDto {

	private String modeOfFolloWUp;
	private String refNumber;
	private String insuranceRepName;
	private String currentClaimStatus;
	private String followUpRemarks;
	private String nextFollowUpRequired;
	private String nextFollowUphDate;
	private int sectionId;
}
