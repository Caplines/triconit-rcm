package com.tricon.rcm.enums;

public enum ClaimTypeEnum {

	P("Primary","_P","P"),
	S("Secondary","_S","S"),
	UU("Secondary","_S","U"),
	PP("Secondary","_S","P"),
	E("Primary","_P","E"),
	EE("Secondary","_P","E");
	
	final private String type;
	final private String suffix;
	final private String value;
	
	private ClaimTypeEnum(String type,String suffix,String value) {
		this.type = type;
		this.suffix = suffix;
		this.value=value;
	}

	public String getType() {
		return type;
	}

	public String getSuffix() {
		return suffix;
	}

	public String getValue() {
		return value;
	}
	
}
