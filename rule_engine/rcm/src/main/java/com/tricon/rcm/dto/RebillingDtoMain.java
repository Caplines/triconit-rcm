package com.tricon.rcm.dto;

import java.util.List;

import lombok.Data;


public class RebillingDtoMain {

	 String requestedRemarks;
	 String rebillingRemarks;
	 String reasonForRebilling;
	 List<String>selectedRebillingRequirements; //select rebilling requirement from rebilling UI
	 List<String>selectedRebillingServiceCodes;		//select rebilling service from rebilling UI
	 String requestedBy;
	 String requestedByUuid;
	 boolean rebillingStatus;// rebiiling/ no need to rebill status
	 boolean reCeationOptionChoosen;//Are you Recreating the Claim?
	 String dateOfRebiiling;
	 List<String>originalServiceCodes;//select rebilling service from  requestBilling UI
	 List<String>originalRequirements;//select rebilling requirement from  requestBilling UI
	 int claimTransferNextTeamId;
	 int sectionId;
	 String usedAI;
	public String getRequestedRemarks() {
		return requestedRemarks;
	}
	public void setRequestedRemarks(String requestedRemarks) {
		this.requestedRemarks = requestedRemarks;
	}
	public String getRebillingRemarks() {
		return rebillingRemarks;
	}
	public void setRebillingRemarks(String rebillingRemarks) {
		this.rebillingRemarks = rebillingRemarks;
	}
	public String getReasonForRebilling() {
		return reasonForRebilling;
	}
	public void setReasonForRebilling(String reasonForRebilling) {
		this.reasonForRebilling = reasonForRebilling;
	}
	public List<String> getSelectedRebillingRequirements() {
		return selectedRebillingRequirements;
	}
	public void setSelectedRebillingRequirements(List<String> selectedRebillingRequirements) {
		this.selectedRebillingRequirements = selectedRebillingRequirements;
	}
	public List<String> getSelectedRebillingServiceCodes() {
		return selectedRebillingServiceCodes;
	}
	public void setSelectedRebillingServiceCodes(List<String> selectedRebillingServiceCodes) {
		this.selectedRebillingServiceCodes = selectedRebillingServiceCodes;
	}
	public String getRequestedBy() {
		return requestedBy;
	}
	public void setRequestedBy(String requestedBy) {
		this.requestedBy = requestedBy;
	}
	public String getRequestedByUuid() {
		return requestedByUuid;
	}
	public void setRequestedByUuid(String requestedByUuid) {
		this.requestedByUuid = requestedByUuid;
	}
	public boolean isRebillingStatus() {
		return rebillingStatus;
	}
	public void setRebillingStatus(boolean rebillingStatus) {
		this.rebillingStatus = rebillingStatus;
	}
	public boolean isReCeationOptionChoosen() {
		return reCeationOptionChoosen;
	}
	public void setReCeationOptionChoosen(boolean reCeationOptionChoosen) {
		this.reCeationOptionChoosen = reCeationOptionChoosen;
	}
	public String getDateOfRebiiling() {
		return dateOfRebiiling;
	}
	public void setDateOfRebiiling(String dateOfRebiiling) {
		this.dateOfRebiiling = dateOfRebiiling;
	}
	public List<String> getOriginalServiceCodes() {
		return originalServiceCodes;
	}
	public void setOriginalServiceCodes(List<String> originalServiceCodes) {
		this.originalServiceCodes = originalServiceCodes;
	}
	public List<String> getOriginalRequirements() {
		return originalRequirements;
	}
	public void setOriginalRequirements(List<String> originalRequirements) {
		this.originalRequirements = originalRequirements;
	}
	public int getClaimTransferNextTeamId() {
		return claimTransferNextTeamId;
	}
	public void setClaimTransferNextTeamId(int claimTransferNextTeamId) {
		this.claimTransferNextTeamId = claimTransferNextTeamId;
	}
	public int getSectionId() {
		return sectionId;
	}
	public void setSectionId(int sectionId) {
		this.sectionId = sectionId;
	}
	public String getUsedAI() {
		return usedAI;
	}
	public void setUsedAI(String usedAI) {
		this.usedAI = usedAI;
	}
	 
	 
}
