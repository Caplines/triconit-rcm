package com.tricon.ruleengine.dto;

public class TreatmentPlanBatchValidationDto {

	private String ivfId;
	private String officeId;
	private String patientId;
	private String insType;
	//private String ivformTypeId;
	
	
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
	public String getPatientId() {
		return patientId;
	}
	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}
	public String getInsType() {
		return insType;
	}
	public void setInsType(String insType) {
		this.insType = insType;
	}
	/*public String getIvformTypeId() {
		return ivformTypeId;
	}
	public void setIvformTypeId(String ivformTypeId) {
		this.ivformTypeId = ivformTypeId;
	}*/




}
