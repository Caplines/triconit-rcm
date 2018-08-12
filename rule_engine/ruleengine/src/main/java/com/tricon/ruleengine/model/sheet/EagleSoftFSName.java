package com.tricon.ruleengine.model.sheet;

public class EagleSoftFSName {
	
	private String feeId;
	private String name;
	
	
	public EagleSoftFSName() {
	}

	public EagleSoftFSName(String feeId, String name) {
		super();
		this.feeId = feeId;
		this.name = name;
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
	
	

}
