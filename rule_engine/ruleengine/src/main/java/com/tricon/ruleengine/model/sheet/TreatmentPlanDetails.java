package com.tricon.ruleengine.model.sheet;

import java.util.Date;

/**
 * @author Deepak.Dogra
 *
 */
public class TreatmentPlanDetails {

	private String dateLastUpdated;//This is DOS
	private String status;
	
	public TreatmentPlanDetails(String dateLastUpdated, String status) {
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
	
	
}
