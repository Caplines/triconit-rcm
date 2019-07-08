package com.tricon.ruleengine.model.sheet;

public class CommonDataDetails {
	
	
	private String dateLastUpdated;
	private String status;
	private String estSecondary;
	private String description;
	

	public CommonDataDetails() {
	}

	public CommonDataDetails(String dateLastUpdated, String status) {
		super();
		this.dateLastUpdated = dateLastUpdated;
		this.status = status;
	}

	public String getDateLastUpdated() {
		return dateLastUpdated;
	}

	public void setDateLastUpdated(String dateLastUpdated) {
		this.dateLastUpdated = dateLastUpdated;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getEstSecondary() {
		return estSecondary;
	}

	public void setEstSecondary(String estSecondary) {
		this.estSecondary = estSecondary;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


}
