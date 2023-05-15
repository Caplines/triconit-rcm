package com.tricon.ruleengine.dto;

public class OfficeDto {

	private String name;
	private String uuid;
	private boolean active;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public OfficeDto(String uuid, String name) {
		this.uuid = uuid;
		this.name = name;
	}

	public OfficeDto() {
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	
   
	
}
