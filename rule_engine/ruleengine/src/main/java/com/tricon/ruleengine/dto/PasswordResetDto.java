package com.tricon.ruleengine.dto;

public class PasswordResetDto {

	private String uuid;
	private String password;
	
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
