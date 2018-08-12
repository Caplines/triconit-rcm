package com.tricon.ruleengine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MicroSoftTreatmentValueJson {
	
	@JsonProperty("@odata.context")
	private String token_type;
	
	@JsonProperty("@odata.type")
	private String scope;
	
	@JsonProperty("@odata.id")
	private Integer  expires_in;
	
	@JsonProperty("values")
	private Integer ext_expires_in;

	@JsonProperty("access_token")
	private String access_token;
	
	@JsonProperty("refresh_token")
	private String refresh_token;
	
	
	public String getToken_type() {
		return token_type;
	}
	public void setToken_type(String token_type) {
		this.token_type = token_type;
	}
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	public String getAccess_token() {
		return access_token;
	}
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
	public String getRefresh_token() {
		return refresh_token;
	}
	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}
	public int getExpires_in() {
		return expires_in;
	}
	public void setExpires_in(Integer expires_in) {
		this.expires_in = expires_in;
	}
	public int getExt_expires_in() {
		return ext_expires_in;
	}
	public void setExt_expires_in(Integer ext_expires_in) {
		this.ext_expires_in = ext_expires_in;
	}
	

}
