package com.tricon.ruleengine.model.sheet;

public class EagleSoftFeeShedule {

	private String feeId;
	private String name;
	private String  feesServiceCode;
	private String  feesFee;
	
	
	public EagleSoftFeeShedule(String feeId, String name, String feesServiceCode, String feesFee) {
		super();
		this.feeId = feeId;
		this.name = name;
		this.feesServiceCode = feesServiceCode;
		this.feesFee = feesFee;
	}
	public String getFeeId() {
		return feeId;
	}
	public void setFeeId(String feeId) {
		this.feeId = feeId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFeesServiceCode() {
		return feesServiceCode;
	}
	public void setFeesServiceCode(String feesServiceCode) {
		this.feesServiceCode = feesServiceCode;
	}
	public String getFeesFee() {
		return feesFee;
	}
	public void setFeesFee(String feesFee) {
		this.feesFee = feesFee;
	}
	
	
}
