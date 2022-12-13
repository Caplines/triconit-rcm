package com.tricon.ruleengine.dto;

import java.util.ArrayList;
import java.util.List;

public class TreatmentPlanValidationDto {

	private String treatmentPlanId;
	private String ivfId;
	private String officeId;
	private boolean debugMode;
	private boolean inputMode;
	private String status;
	private String insType;
	private List<IgnoreDataArrayDto> ignoreDataArray= new ArrayList<>();
	
	//private String ivformTypeId;
	
	//private List<String> statses;
	
	
	
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
	public boolean isInputMode() {
		return inputMode;
	}
	public void setInputMode(boolean inputMode) {
		this.inputMode = inputMode;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getInsType() {
		return insType;
	}
	public void setInsType(String insType) {
		this.insType = insType;
	}
	//public String getIvformTypeId() {
	//	return ivformTypeId;
	//}
	//public void setIvformTypeId(String ivformTypeId) {
	//	this.ivformTypeId = ivformTypeId;
	//}
	public List<IgnoreDataArrayDto> getIgnoreDataArray() {
		return ignoreDataArray;
	}
	public void setIgnoreDataArray(List<IgnoreDataArrayDto> ignoreDataArray) {
		this.ignoreDataArray = ignoreDataArray;
	}
	
	




}
