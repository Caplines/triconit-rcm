package com.tricon.rcm.dto;

import java.util.List;

import lombok.Data;

@Data
public class ClaimFromSheet {
	
	private String clientName;
	private String officeName;
	private String officeKey;
	private String claimId;
	private String accountId;// same as PatientId
	private String patientName;
	private String paitentDob;
	private String dos;
	private String primaryBilledAmount;
	private String claimTypeP;
	private String primaryClaimStatus;
	private String providerIdProviderName;
	private String treatingProviderName;
	private String primaryEstAmount;
	private String primaryInsuranceCompany;
	private String insuranceName;
	private String primaryMemberId;
	private String primaryInsuranceAddress;
	private String primaryGroupNumber;
	private String primaryPolicyHolderName;
	private String primaryPolicyHolderDob;
	
	private String secondaryBIlledAmount;
	private String secondaryClaimSubmissionDate;
	private String primaryPaid;
	private String claimTypeS;
	private String secondaryClaimStatus;
	private String providerIdReport;
	private String secondaryEstAmount;
	private String secondaryInsuranceCompany;
	private String secondaryInsuranceName;
	private String secondaryMemberId;
	private String secondaryInsuranceAddress;
	private String secondaryGroupNumber;
	private String secondaryPolicyHolder;
	private String secondaryPolicyHolderDob;
	private List<String> serviceCodes;
	private String primaryInsuranceContactNo;
	private String secondaryInsuranceContactNo;
	private String patientContactNo;
	private List<String> toothAndSurfaces;
	
	public ClaimFromSheet() {
		
	}
	public ClaimFromSheet(String clientName, String officeName,String officeKey, String claimId, String accountId, String patientName,
			String paitentDob, String dos, String primaryBilledAmount, String claimTypeP, String primaryClaimStatus,
			String providerIdProviderName, String primaryEstAmount, String primaryInsuranceCompany,
			String insuranceName, String primaryMemberId, String primaryInsuranceAddress, String primaryGroupNumber,
			String primaryPolicyHolderName, String primaryPolicyHolderDob, String secondaryBIlledAmount,
			String secondaryClaimSubmissionDate, String primaryPaid, String claimTypeS, String secondaryClaimStatus,
			String providerIdReport, String secondaryEstAmount, String secondaryInsuranceCompany,
			String secondaryInsuranceName, String secondaryMemberId, String secondaryInsuranceAddress,
			String secondaryGroupNumber, String secondaryPolicyHolder, String secondaryPolicyHolderDob) {
		super();
		this.clientName = clientName;
		this.officeName = officeName;
		this.officeKey = officeKey;
		this.claimId = claimId;
		this.accountId = accountId;
		this.patientName = patientName;
		this.paitentDob = paitentDob;
		this.dos = dos;
		this.primaryBilledAmount = primaryBilledAmount;
		this.claimTypeP = claimTypeP;
		this.primaryClaimStatus = primaryClaimStatus;
		this.providerIdProviderName = providerIdProviderName;
		this.primaryEstAmount = primaryEstAmount;
		this.primaryInsuranceCompany = primaryInsuranceCompany;
		this.insuranceName = insuranceName;
		this.primaryMemberId = primaryMemberId;
		this.primaryInsuranceAddress = primaryInsuranceAddress;
		this.primaryGroupNumber = primaryGroupNumber;
		this.primaryPolicyHolderName = primaryPolicyHolderName;
		this.primaryPolicyHolderDob = primaryPolicyHolderDob;
		this.secondaryBIlledAmount = secondaryBIlledAmount;
		this.secondaryClaimSubmissionDate = secondaryClaimSubmissionDate;
		this.primaryPaid = primaryPaid;
		this.claimTypeS = claimTypeS;
		this.secondaryClaimStatus = secondaryClaimStatus;
		this.providerIdReport = providerIdReport;
		this.secondaryEstAmount = secondaryEstAmount;
		this.secondaryInsuranceCompany = secondaryInsuranceCompany;
		this.secondaryInsuranceName = secondaryInsuranceName;
		this.secondaryMemberId = secondaryMemberId;
		this.secondaryInsuranceAddress = secondaryInsuranceAddress;
		this.secondaryGroupNumber = secondaryGroupNumber;
		this.secondaryPolicyHolder = secondaryPolicyHolder;
		this.secondaryPolicyHolderDob = secondaryPolicyHolderDob;
	}
	
	
	
	
	

	
}
