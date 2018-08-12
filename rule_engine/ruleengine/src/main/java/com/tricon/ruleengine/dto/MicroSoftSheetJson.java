package com.tricon.ruleengine.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MicroSoftSheetJson {
	
	@JsonProperty("@odata.context")
	private String context;
	
	@JsonProperty("@odata.type")
	private String dType;
	
	@JsonProperty("@odata.id")
	private String  id;
	
	@JsonProperty("values")
	private List<Object> data;

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getdType() {
		return dType;
	}

	public void setdType(String dType) {
		this.dType = dType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Object> getData() {
		return data;
	}

	public void setData(List<Object> data) {
		this.data = data;
	}

	
	

	

}
