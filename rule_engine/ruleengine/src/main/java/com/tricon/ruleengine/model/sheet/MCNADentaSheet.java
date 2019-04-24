package com.tricon.ruleengine.model.sheet;

/**
 * 
 * @author Deepak.Dogra
 *
 */
public class MCNADentaSheet {

	private String fName;
	private String lName;
	private String subscriberId;
	private String dob;
	private String insuranceName;
	private String rowNumber;
	private String zip;

	public MCNADentaSheet(String fName, String lName, String subscriberId, String dob, String insuranceName,
			String rowNumber) {
		super();
		this.fName = fName;
		this.lName = lName;
		this.subscriberId = subscriberId;
		this.dob = dob;
		this.insuranceName = insuranceName;
		this.rowNumber = rowNumber;
	}

	public String getRowNumber() {
		return rowNumber;
	}

	public void setRowNumber(String rowNumber) {
		this.rowNumber = rowNumber;
	}

	public String getfName() {
		return fName;
	}

	public void setfName(String fName) {
		this.fName = fName;
	}

	
	public String getlName() {
		return lName;
	}

	public void setlName(String lName) {
		this.lName = lName;
	}

	public String getSubscriberId() {
		return subscriberId;
	}

	public void setSubscriberId(String subscriberId) {
		this.subscriberId = subscriberId;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getInsuranceName() {
		return insuranceName;
	}

	public void setInsuranceName(String insuranceName) {
		this.insuranceName = insuranceName;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

}
