package com.tricon.ruleengine.model.sheet;

public class CRAReqMappingDto {

	private String serialNo;
	private String insuranceName;
	private String planType;
	private String groupEmpName;
	private String d0601;
	private String d0602;
	private String d0603;
	private String craRequired;
	
	public CRAReqMappingDto(String serialNo, String insuranceName, String planType, String groupEmpName, String d0601,
			String d0602, String d0603, String craRequired) {
		super();
		this.serialNo = serialNo;
		this.insuranceName = insuranceName;
		this.planType = planType;
		this.groupEmpName = groupEmpName;
		this.d0601 = d0601;
		this.d0602 = d0602;
		this.d0603 = d0603;
		this.craRequired = craRequired;
	}
	public String getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}
	public String getInsuranceName() {
		return insuranceName;
	}
	public void setInsuranceName(String insuranceName) {
		this.insuranceName = insuranceName;
	}
	public String getPlanType() {
		return planType;
	}
	public void setPlanType(String planType) {
		this.planType = planType;
	}
	
	
	public String getGroupEmpName() {
		return groupEmpName;
	}
	public void setGroupEmpName(String groupEmpName) {
		this.groupEmpName = groupEmpName;
	}
	public String getD0601() {
		return d0601;
	}
	public void setD0601(String d0601) {
		this.d0601 = d0601;
	}
	public String getD0602() {
		return d0602;
	}
	public void setD0602(String d0602) {
		this.d0602 = d0602;
	}
	public String getD0603() {
		return d0603;
	}
	public void setD0603(String d0603) {
		this.d0603 = d0603;
	}
	public String getCraRequired() {
		return craRequired;
	}
	public void setCraRequired(String craRequired) {
		this.craRequired = craRequired;
	}
	
	
	
}
