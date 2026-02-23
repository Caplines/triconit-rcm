package com.tricon.ruleengine.dto;

import java.util.Set;

public class CaplineIVFQueryFormDto {

	private String officeNameDB;
	private String patientIdDB;
	private String employerNameDB;
	private String generalDateIVFDoneDB;
	private String patientName;
	private String passwordRE;
	private String uniqueID;
	private String columns;
	private String sheetId;
	private String sheetSubId;
	private Set<String> uniqueIDs;
	private String policyHolderNameDB;
	private String policyHolderDobDB;
	private String patientDobDB;
	private String generalDateIVFDoneDBBet;
	private String clause;
	private String websiteNameDB;
	private String pdf;
	private String newFormat;
	private String ivformTypeId;
	private String forSelantData;
	private String insuranceName;
	private String groupName;
	private String groupNumber;
	private String annualMax;
	private String individualDeductible;
	private String preventiveServicesPct;
	private String basicServicesPct;
	private String majorServicesPct;
	
	
	public String getClause() {
		return clause;
	}
	public void setClause(String clause) {
		this.clause = clause;
	}
	public String getPolicyHolderNameDB() {
		return policyHolderNameDB;
	}
	public void setPolicyHolderNameDB(String policyHolderNameDB) {
		this.policyHolderNameDB = policyHolderNameDB;
	}
	public String getPolicyHolderDobDB() {
		return policyHolderDobDB;
	}
	public void setPolicyHolderDobDB(String policyHolderDobDB) {
		this.policyHolderDobDB = policyHolderDobDB;
	}
	public Set<String> getUniqueIDs() {
		return uniqueIDs;
	}
	public void setUniqueIDs(Set<String> uniqueIDs) {
		this.uniqueIDs = uniqueIDs;
	}
	public String getOfficeNameDB() {
		return officeNameDB;
	}
	public void setOfficeNameDB(String officeNameDB) {
		this.officeNameDB = officeNameDB;
	}
	public String getPatientIdDB() {
		return patientIdDB;
	}
	public void setPatientIdDB(String patientIdDB) {
		this.patientIdDB = patientIdDB;
	}
	public String getEmployerNameDB() {
		return employerNameDB;
	}
	public void setEmployerNameDB(String employerNameDB) {
		this.employerNameDB = employerNameDB;
	}
	public String getGeneralDateIVFDoneDB() {
		return generalDateIVFDoneDB;
	}
	public void setGeneralDateIVFDoneDB(String generalDateIVFDoneDB) {
		this.generalDateIVFDoneDB = generalDateIVFDoneDB;
	}
	public String getPatientName() {
		return patientName;
	}
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	public String getPasswordRE() {
		return passwordRE;
	}
	public void setPasswordRE(String passwordRE) {
		this.passwordRE = passwordRE;
	}
	public String getUniqueID() {
		return uniqueID;
	}
	public void setUniqueID(String uniqueID) {
		this.uniqueID = uniqueID;
	}
	public String getColumns() {
		return columns;
	}
	public void setColumns(String columns) {
		this.columns = columns;
	}
	public String getSheetId() {
		return sheetId;
	}
	public void setSheetId(String sheetId) {
		this.sheetId = sheetId;
	}
	public String getSheetSubId() {
		return sheetSubId;
	}
	public void setSheetSubId(String sheetSubId) {
		this.sheetSubId = sheetSubId;
	}
	public String getPatientDobDB() {
		return patientDobDB;
	}
	public void setPatientDobDB(String patientDobDB) {
		this.patientDobDB = patientDobDB;
	}
	public String getGeneralDateIVFDoneDBBet() {
		return generalDateIVFDoneDBBet;
	}
	public void setGeneralDateIVFDoneDBBet(String generalDateIVFDoneDBBet) {
		this.generalDateIVFDoneDBBet = generalDateIVFDoneDBBet;
	}
	public String getWebsiteNameDB() {
		return websiteNameDB;
	}
	public void setWebsiteNameDB(String websiteNameDB) {
		this.websiteNameDB = websiteNameDB;
	}
	public String getPdf() {
		return pdf;
	}
	public void setPdf(String pdf) {
		this.pdf = pdf;
	}
	public String getNewFormat() {
		return newFormat;
	}
	public void setNewFormat(String newFormat) {
		this.newFormat = newFormat;
	}
	public String getIvformTypeId() {
		return ivformTypeId;
	}
	public void setIvformTypeId(String ivformTypeId) {
		this.ivformTypeId = ivformTypeId;
	}
	public String getForSelantData() {
		return forSelantData;
	}
	public void setForSelantData(String forSelantData) {
		this.forSelantData = forSelantData;
	}
	



	public String getInsuranceName() {
		return insuranceName;
	}

	public void setInsuranceName(String insuranceName) {
    this.insuranceName = insuranceName != null ? insuranceName.trim() : "";
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName != null ? groupName.trim() : "";
	}
	
	public String getGroupNumber() {
		return groupNumber;
	}

	public void setGroupNumber(String groupNumber) {
		this.groupNumber = groupNumber != null ? groupNumber.trim() : "";
	}

	public String getAnnualMax() {
		return annualMax;
	}
	public void setAnnualMax(String annualMax) {
		this.annualMax = annualMax != null ? annualMax.trim() : "";
	}
	public String getIndividualDeductible() {
		return individualDeductible;
	}
	public void setIndividualDeductible(String individualDeductible) {
		this.individualDeductible = individualDeductible != null ? individualDeductible.trim() : "";
	}
	public String getPreventiveServicesPct() {
		return preventiveServicesPct;
	}
	public void setPreventiveServicesPct(String preventiveServicesPct) {
		this.preventiveServicesPct = preventiveServicesPct != null ? preventiveServicesPct.trim() : "";
	}
	public String getBasicServicesPct() {
		return basicServicesPct;
	}
	public void setBasicServicesPct(String basicServicesPct) {
		this.basicServicesPct = basicServicesPct != null ? basicServicesPct.trim() : "";
	}
	public String getMajorServicesPct() {
		return majorServicesPct;
	}
	public void setMajorServicesPct(String majorServicesPct) {
		this.majorServicesPct = majorServicesPct != null ? majorServicesPct.trim() : "";
	}
}
