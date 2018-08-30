package com.tricon.ruleengine.dto;

public class PatientTreamentDto {

	private String treatmentPlanId;
	private String tDescription;
	private String dateLastUpdated;
	

	public String getTreatmentPlanId() {
		return treatmentPlanId;
	}

	public void setTreatmentPlanId(String treatmentPlanId) {
		this.treatmentPlanId = treatmentPlanId;
	}


	public String gettDescription() {
		return tDescription;
	}

	public void settDescription(String tDescription) {
		this.tDescription = tDescription;
	}

	public String getDateLastUpdated() {
		return dateLastUpdated;
	}

	public void setDateLastUpdated(String dateLastUpdated) {
		this.dateLastUpdated = dateLastUpdated;
	}
	

}
