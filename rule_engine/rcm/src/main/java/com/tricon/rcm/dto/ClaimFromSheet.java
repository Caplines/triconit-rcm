package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class ClaimFromSheet {
	
	private String officeName;										 				
	private String actionRequired;
	private String patientId;
	private String patientName;
	private String paitentDob;
	private String subscriberName;
	private String subscriberDob;
	private String claimId;
	private String dateOfService;
	private String provider;
	private String claimType;
	private String primaryEOB;
	private String totalBilled;
	private String insuranceEstimatedAmount;
	private String insuranceMemberId;
	private String insuranceCompanyName;
	private String insuranceType;
	public ClaimFromSheet(String officeName, String actionRequired, String patientId, String patientName,
			String paitentDob, String subscriberName, String subscriberDob, String claimId, String dateOfService,
			String provider, String claimType, String primaryEOB, String totalBilled, String insuranceEstimatedAmount,
			String insuranceMemberId, String insuranceCompanyName, String insuranceType) {
		super();
		this.officeName = officeName;
		this.actionRequired = actionRequired;
		this.patientId = patientId;
		this.patientName = patientName;
		this.paitentDob = paitentDob;
		this.subscriberName = subscriberName;
		this.subscriberDob = subscriberDob;
		this.claimId = claimId;
		this.dateOfService = dateOfService;
		this.provider = provider;
		this.claimType = claimType;
		this.primaryEOB = primaryEOB;
		this.totalBilled = totalBilled;
		this.insuranceEstimatedAmount = insuranceEstimatedAmount;
		this.insuranceMemberId = insuranceMemberId;
		this.insuranceCompanyName = insuranceCompanyName;
		this.insuranceType = insuranceType;
	}
	
	
	
}
