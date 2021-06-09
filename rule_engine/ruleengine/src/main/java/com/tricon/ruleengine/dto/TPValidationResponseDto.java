package com.tricon.ruleengine.dto;

public class TPValidationResponseDto {

	private int ruleId;
	private String ruleName;
	private String message;
	private String resultType;
	private String surface;
	private String tooth;
	private String serviceCode;
	private String patientName;
	private String ivDone;
	private String off;
	private String iName;
	
	
	
	
	public TPValidationResponseDto() {
		super();
		
	}
	
	public TPValidationResponseDto(int ruleId, String ruleName, String message, String resultType,
			String surface,String tooth,String serviceCode) {
		super();
		this.ruleId = ruleId;
		this.ruleName = ruleName;
		this.message = message;
		this.resultType = resultType;
		this.surface=surface;
		this.tooth=tooth;
		this.serviceCode=serviceCode;
		
	}

	public TPValidationResponseDto(int ruleId, String ruleName, String message, String resultType,
			String surface,String tooth,String serviceCode,String patientName,String ivDone) {
		super();
		this.ruleId = ruleId;
		this.ruleName = ruleName;
		this.message = message;
		this.resultType = resultType;
		this.surface=surface;
		this.tooth=tooth;
		this.serviceCode=serviceCode;
		this.patientName=patientName;
		this.ivDone=ivDone;
		
		
		
	}

	public int getRuleId() {
		return ruleId;
	}

	public void setRuleId(int ruleId) {
		this.ruleId = ruleId;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getResultType() {
		return resultType;
	}

	public void setResultType(String resultType) {
		this.resultType = resultType;
	}

	public String getSurface() {
		return surface;
	}

	public void setSurface(String surface) {
		this.surface = surface;
	}

	public String getTooth() {
		return tooth;
	}

	public void setTooth(String tooth) {
		this.tooth = tooth;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getIvDone() {
		return ivDone;
	}

	public void setIvDone(String ivDone) {
		this.ivDone = ivDone;
	}

	public String getOff() {
		return off;
	}

	public void setOff(String off) {
		this.off = off;
	}

	public String getiName() {
		return iName;
	}

	public void setiName(String iName) {
		this.iName = iName;
	}
	
	

}
