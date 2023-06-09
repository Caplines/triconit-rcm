package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class CredentialData {

	private String officeName;//A
	private String Location;//B
	private String Providers;//C
	private String providerCode;//D
	private String Speciality;//E
	private String Status;//F
	private String insurance;//G
	private String insuranceCode;//H
	private String planType;//I
	private String requestReceivedDate;//J
	private String welcomeEmail;//K
	private String documentsreceivedFromProviderDate;//L
	private String requestSentInsuranceForms;//M
	private String formsReceivedInsurancesDate;//N
	private String applicationFilling;//O
	private String applicationSent;//P
	private String aplicationReceived;//Q
	private String applicationSubmitted;//R
	private String dateConfirmation;//S
	private String requestNo;//T
	private String applicationStatus;//U
	private String recredentialingDueDate;//V
	private String recredentialingComments;//W
	private String resubmittedBy;//X
	private String resubmittedDate;//Y
	private String effectiveDate;//Z
	private String completionDate;//AA
	private String followUpDate;//AB
	private String lastSuccessfulFollow;//AC
	private String lastFollowedBy;//AD
	private String remarks;//AE
	private String remarksDelay;//AF
	private String credentialingConfirmation;//AG
	private String applicationSubmittedBy;//AH
	private String applicationSubmittedTo;//AI
	private String tpaFS;//AJ
	private String terminationDate;//AK
	private String notSure;//AL
	private String nextFollowupDate;//AM
	private String documentReceivedOn;//AN
	private String auditorName;//AO
	private String auditDate;//AP
	private String credentialingStatus;//AQ
	private String reCredentialing;//AR
	private String category;//AS
	private String sourceAudit;//AT
	private String wellcomeLetter;//AU
	private String auditorsRemarks;//AV 
	private String endofString;//AW
	
	public CredentialData(String officeName, String location, String providers, String providerCode, String speciality,
			String status, String insurance, String insuranceCode, String planType, String requestReceivedDate,
			String welcomeEmail, String documentsreceivedFromProviderDate, String requestSentInsuranceForms,
			String formsReceivedInsurancesDate, String applicationFilling, String applicationSent,
			String aplicationReceived, String applicationSubmitted, String dateConfirmation, String requestNo,
			String applicationStatus, String recredentialingDueDate, String recredentialingComments,
			String resubmittedBy, String resubmittedDate, String effectiveDate, String completionDate,
			String followUpDate, String lastSuccessfulFollow, String lastFollowedBy, String remarks,
			String remarksDelay, String credentialingConfirmation, String applicationSubmittedBy,
			String applicationSubmittedTo, String tpaFS, String terminationDate, String notSure,
			String nextFollowupDate, String documentReceivedOn, String auditorName, String auditDate,
			String credentialingStatus, String reCredentialing, String category, String sourceAudit,
			String wellcomeLetter, String auditorsRemarks,String endofString) {
		super();
		this.officeName = officeName;
		Location = location;
		Providers = providers;
		this.providerCode = providerCode;
		Speciality = speciality;
		Status = status;
		this.insurance = insurance;
		this.insuranceCode = insuranceCode;
		this.planType = planType;
		this.requestReceivedDate = requestReceivedDate;
		this.welcomeEmail = welcomeEmail;
		this.documentsreceivedFromProviderDate = documentsreceivedFromProviderDate;
		this.requestSentInsuranceForms = requestSentInsuranceForms;
		this.formsReceivedInsurancesDate = formsReceivedInsurancesDate;
		this.applicationFilling = applicationFilling;
		this.applicationSent = applicationSent;
		this.aplicationReceived = aplicationReceived;
		this.applicationSubmitted = applicationSubmitted;
		this.dateConfirmation = dateConfirmation;
		this.requestNo = requestNo;
		this.applicationStatus = applicationStatus;
		this.recredentialingDueDate = recredentialingDueDate;
		this.recredentialingComments = recredentialingComments;
		this.resubmittedBy = resubmittedBy;
		this.resubmittedDate = resubmittedDate;
		this.effectiveDate = effectiveDate;
		this.completionDate = completionDate;
		this.followUpDate = followUpDate;
		this.lastSuccessfulFollow = lastSuccessfulFollow;
		this.lastFollowedBy = lastFollowedBy;
		this.remarks = remarks;
		this.remarksDelay = remarksDelay;
		this.credentialingConfirmation = credentialingConfirmation;
		this.applicationSubmittedBy = applicationSubmittedBy;
		this.applicationSubmittedTo = applicationSubmittedTo;
		this.tpaFS = tpaFS;
		this.terminationDate = terminationDate;
		this.notSure = notSure;
		this.nextFollowupDate = nextFollowupDate;
		this.documentReceivedOn = documentReceivedOn;
		this.auditorName = auditorName;
		this.auditDate = auditDate;
		this.credentialingStatus = credentialingStatus;
		this.reCredentialing = reCredentialing;
		this.category = category;
		this.sourceAudit = sourceAudit;
		this.wellcomeLetter = wellcomeLetter;
		this.auditorsRemarks = auditorsRemarks;
		this.endofString= endofString;
	}
	
	
	
	
}
