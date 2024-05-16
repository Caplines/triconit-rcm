package com.tricon.ruleengine.dto;

public class CodeValueDto {

	String code;
	String value;
	
	
	public CodeValueDto(String code, String value) {
		super();
		this.code = code;
		this.value = value;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
}
