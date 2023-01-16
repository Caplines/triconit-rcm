package com.tricon.rcm.enums;

public enum ClaimTypeEnum {

	P("Primary","_P"),
	S("Secondary","_S"),
	U("Secondary","_S"),
	E("Primary","_P");
	
	final private String type;
	final private String suffix;
	
	private ClaimTypeEnum(String type,String suffix) {
		this.type = type;
		this.suffix = suffix;
	}

	public String getType() {
		return type;
	}

	public String getSuffix() {
		return suffix;
	}


	
}
