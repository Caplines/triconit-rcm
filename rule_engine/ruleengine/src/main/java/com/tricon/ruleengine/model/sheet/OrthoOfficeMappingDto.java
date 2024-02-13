package com.tricon.ruleengine.model.sheet;

public class OrthoOfficeMappingDto {
	
	private String officeName;
	private String orthoTxProvided;
	
    public OrthoOfficeMappingDto(String officeName, String orthoTxProvided) {
		super();
		this.officeName = officeName;
		this.orthoTxProvided = orthoTxProvided;
	}
	
	public String getOfficeName() {
		return officeName;
	}
	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}
	public String getOrthoTxProvided() {
		return orthoTxProvided;
	}
	public void setOrthoTxProvided(String orthoTxProvided) {
		this.orthoTxProvided = orthoTxProvided;
	}
	
	

}
