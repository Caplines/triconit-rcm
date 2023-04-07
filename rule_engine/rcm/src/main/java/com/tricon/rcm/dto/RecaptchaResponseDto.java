package com.tricon.rcm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RecaptchaResponseDto {

	private boolean success;
	private String hostName;

	@JsonProperty("success")
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	@JsonProperty("hostname")
	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
}
