package com.tricon.ruleengine.dto;

public class TreatmentPlanValidationDto {

	private String treatmentPlanId;
	private String ivfId;
	private String createdBy;
	private String officeId;

	public String getTreatmentPlanId() {
		return treatmentPlanId;
	}

	public void setTreatmentPlanId(String treatmentPlanId) {
		this.treatmentPlanId = treatmentPlanId;
	}

	public String getivfId() {
		return ivfId;
	}

	public void setivfId(String ivfId) {
		this.ivfId = ivfId;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getOfficeId() {
		return officeId;
	}

	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}

}
