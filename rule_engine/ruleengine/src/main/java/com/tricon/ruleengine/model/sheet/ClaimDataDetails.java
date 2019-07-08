package com.tricon.ruleengine.model.sheet;

//import java.util.Date;

/**
 * @author Deepak.Dogra
 *
 */
public class ClaimDataDetails {

	private String dateLastUpdated;// This is DOS but not considered-- we have use Current Date as DOS in TP but in Claim we will consider this .
	private String status;
	private String estSecondary;
	private String description;
	

	public ClaimDataDetails() {
	}

	public ClaimDataDetails(String dateLastUpdated, String status) {
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
