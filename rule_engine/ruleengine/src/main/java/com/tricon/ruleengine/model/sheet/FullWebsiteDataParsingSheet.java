package com.tricon.ruleengine.model.sheet;

public class FullWebsiteDataParsingSheet {
	
	
	private String fName;
	private String lName;
	private String enrolleeId;
	private String patientId;
	private String dob;
	private String dependentFirstName;
	private String dependentDob;
	private String requiredDataForRuleEngine;
	private String rowNumber;
	
	
	
	public FullWebsiteDataParsingSheet(String fName, String lName,String patientId, String enrolleeId, String dob,
			String dependentFirstName, String dependentDob, String requiredDataForRuleEngine,String rowNumber) {
		super();
		this.fName = fName;
		this.lName = lName;
		this.patientId=patientId;
		this.enrolleeId = enrolleeId;
		this.dob = dob;
		this.dependentFirstName = dependentFirstName;
		this.dependentDob = dependentDob;
		this.requiredDataForRuleEngine = requiredDataForRuleEngine;
		this.rowNumber=rowNumber;
	}
	public String getPatientId() {
		return patientId;
	}
	public void setPatientId(String patientId) {
		this.patientId = patientId;
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
	public String getEnrolleeId() {
		return enrolleeId;
	}
	public void setEnrolleeId(String enrolleeId) {
		this.enrolleeId = enrolleeId;
	}
	public String getDob() {
		return dob;
	}
	public void setDob(String dob) {
		this.dob = dob;
	}
	public String getDependentFirstName() {
		return dependentFirstName;
	}
	public void setDependentFirstName(String dependentFirstName) {
		this.dependentFirstName = dependentFirstName;
	}
	public String getDependentDob() {
		return dependentDob;
	}
	public void setDependentDob(String dependentDob) {
		this.dependentDob = dependentDob;
	}
	public String getRequiredDataForRuleEngine() {
		return requiredDataForRuleEngine;
	}
	public void setRequiredDataForRuleEngine(String requiredDataForRuleEngine) {
		this.requiredDataForRuleEngine = requiredDataForRuleEngine;
	}
	public String getRowNumber() {
		return rowNumber;
	}
	public void setRowNumber(String rowNumber) {
		this.rowNumber = rowNumber;
	}

	
	
	
}
