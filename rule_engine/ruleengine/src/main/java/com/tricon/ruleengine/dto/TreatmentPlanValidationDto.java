package com.tricon.ruleengine.dto;

public class TreatmentPlanValidationDto {

	private String treatmentPlanId;
	private String ivfId;
	private String officeId;
	private boolean debugMode;
	
	public String getTreatmentPlanId() {
		return treatmentPlanId;
	}
	public void setTreatmentPlanId(String treatmentPlanId) {
		this.treatmentPlanId = treatmentPlanId;
	}
	public String getIvfId() {
		return ivfId;
	}
	public void setIvfId(String ivfId) {
		this.ivfId = ivfId;
	}
	public String getOfficeId() {
		return officeId;
	}
	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}
	public boolean isDebugMode() {
		return debugMode;
	}
	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}




}
