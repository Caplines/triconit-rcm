package com.tricon.ruleengine.dto;

import com.tricon.ruleengine.model.db.Office;

public class OrthoGoogleSheetDto {

	String uniqId; //A
	String office; //b
	String esid;//c
	String patientName; //d
	String patientDOB; //e
	String policyHolderName; //f
	String policyHolderDOB; //G
	String appointmentDate;//H
	String iVType;//i
	String insuranceName;//j
	String insuranceContactNo;//k
	String memberID;//l
	String ssn;//m
	String employerName;//n
	String groupNo; //o
	String payerId;//p
	String insuranceAddress;//q
	String providerName;//r
	String effectiveDate;//s
	String policyTermDate;//t
	String network;//u
	String dependentCoveredUpToAge;//v
	String ageLimitForOrtho;//w
	String workInProgress;//x
	String coordinationBenefits;//y
	String calender;//z
	String insuranceBillingCycle;//Aa
	String orthoCoverragePer;//Ab
	String waitingPeriodForOrtho;//Ac
	String timelyFilingLimit;//Ad
	String orthoMax;//Ae
	String orthoMaxRemaining;//Af
	String deductibleForOrtho;//Ag
	String d8070;//Ah
	String d8080;//Ai
	String d8090;//Aj
	String d8670;//Ak
	String d8690;//Al
	String csrName;//Am
	String referenceNo;//An
	String IVUpdatedBy;//Ao
	String status;//Ap
	String remarks;//AQ
	String completionDate;//AR
	
	//For Log Purpose
	int rowCounter;
	String statusDump;
	Office officeDb;
	
	public OrthoGoogleSheetDto(String uniqId, String office, String esid, String patientName, String patientDOB,
			String policyHolderName, String policyHolderDOB, String appointmentDate, String iVType,
			String insuranceName, String insuranceContactNo, String memberID, String ssn, String employerName,
			String groupNo, String payerId, String insuranceAddress, String providerName, String effectiveDate,
			String policyTermDate, String network, String dependentCoveredUpToAge, String ageLimitForOrtho,
			String workInProgress, String coordinationBenefits, String calender, String insuranceBillingCycle,
			String orthoCoverragePer, String waitingPeriodForOrtho, String timelyFilingLimit, String orthoMax,
			String orthoMaxRemaining, String deductibleForOrtho, String d8070, String d8080, String d8090, String d8670,
			String d8690, String csrName, String referenceNo, String iVUpdatedBy, String status, String remarks,
			String completionDate) {
		super();
		this.uniqId = uniqId;
		this.office = office;
		this.esid = esid;
		this.patientName = patientName;
		this.patientDOB = patientDOB;
		this.policyHolderName = policyHolderName;
		this.policyHolderDOB = policyHolderDOB;
		this.appointmentDate = appointmentDate;
		this.iVType = iVType;
		this.insuranceName = insuranceName;
		this.insuranceContactNo = insuranceContactNo;
		this.memberID = memberID;
		this.ssn = ssn;
		this.employerName = employerName;
		this.groupNo = groupNo;
		this.payerId = payerId;
		this.insuranceAddress = insuranceAddress;
		this.providerName = providerName;
		this.effectiveDate = effectiveDate;
		this.policyTermDate = policyTermDate;
		this.network = network;
		this.dependentCoveredUpToAge = dependentCoveredUpToAge;
		this.ageLimitForOrtho = ageLimitForOrtho;
		this.workInProgress = workInProgress;
		this.coordinationBenefits = coordinationBenefits;
		this.calender = calender;
		this.insuranceBillingCycle = insuranceBillingCycle;
		this.orthoCoverragePer = orthoCoverragePer;
		this.waitingPeriodForOrtho = waitingPeriodForOrtho;
		this.timelyFilingLimit = timelyFilingLimit;
		this.orthoMax = orthoMax;
		this.orthoMaxRemaining = orthoMaxRemaining;
		this.deductibleForOrtho = deductibleForOrtho;
		this.d8070 = d8070;
		this.d8080 = d8080;
		this.d8090 = d8090;
		this.d8670 = d8670;
		this.d8690 = d8690;
		this.csrName = csrName;
		this.referenceNo = referenceNo;
		IVUpdatedBy = iVUpdatedBy;
		this.status = status;
		this.remarks = remarks;
		this.completionDate = completionDate;
	}
	public String getUniqId() {
		return uniqId;
	}
	public void setUniqId(String uniqId) {
		this.uniqId = uniqId;
	}
	public String getOffice() {
		return office;
	}
	public void setOffice(String office) {
		this.office = office;
	}
	public String getEsid() {
		return esid;
	}
	public void setEsid(String esid) {
		this.esid = esid;
	}
	public String getPatientName() {
		return patientName;
	}
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	public String getPatientDOB() {
		return patientDOB;
	}
	public void setPatientDOB(String patientDOB) {
		this.patientDOB = patientDOB;
	}
	public String getPolicyHolderName() {
		return policyHolderName;
	}
	public void setPolicyHolderName(String policyHolderName) {
		this.policyHolderName = policyHolderName;
	}
	public String getPolicyHolderDOB() {
		return policyHolderDOB;
	}
	public void setPolicyHolderDOB(String policyHolderDOB) {
		this.policyHolderDOB = policyHolderDOB;
	}
	public String getAppointmentDate() {
		return appointmentDate;
	}
	public void setAppointmentDate(String appointmentDate) {
		this.appointmentDate = appointmentDate;
	}
	public String getiVType() {
		return iVType;
	}
	public void setiVType(String iVType) {
		this.iVType = iVType;
	}
	public String getInsuranceName() {
		return insuranceName;
	}
	public void setInsuranceName(String insuranceName) {
		this.insuranceName = insuranceName;
	}
	public String getInsuranceContactNo() {
		return insuranceContactNo;
	}
	public void setInsuranceContactNo(String insuranceContactNo) {
		this.insuranceContactNo = insuranceContactNo;
	}
	public String getMemberID() {
		return memberID;
	}
	public void setMemberID(String memberID) {
		this.memberID = memberID;
	}
	public String getSsn() {
		return ssn;
	}
	public void setSsn(String ssn) {
		this.ssn = ssn;
	}
	public String getEmployerName() {
		return employerName;
	}
	public void setEmployerName(String employerName) {
		this.employerName = employerName;
	}
	public String getGroupNo() {
		return groupNo;
	}
	public void setGroupNo(String groupNo) {
		this.groupNo = groupNo;
	}
	public String getPayerId() {
		return payerId;
	}
	public void setPayerId(String payerId) {
		this.payerId = payerId;
	}
	public String getInsuranceAddress() {
		return insuranceAddress;
	}
	public void setInsuranceAddress(String insuranceAddress) {
		this.insuranceAddress = insuranceAddress;
	}
	public String getProviderName() {
		return providerName;
	}
	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}
	public String getEffectiveDate() {
		return effectiveDate;
	}
	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	public String getPolicyTermDate() {
		return policyTermDate;
	}
	public void setPolicyTermDate(String policyTermDate) {
		this.policyTermDate = policyTermDate;
	}
	public String getNetwork() {
		return network;
	}
	public void setNetwork(String network) {
		this.network = network;
	}
	public String getDependentCoveredUpToAge() {
		return dependentCoveredUpToAge;
	}
	public void setDependentCoveredUpToAge(String dependentCoveredUpToAge) {
		this.dependentCoveredUpToAge = dependentCoveredUpToAge;
	}
	public String getAgeLimitForOrtho() {
		return ageLimitForOrtho;
	}
	public void setAgeLimitForOrtho(String ageLimitForOrtho) {
		this.ageLimitForOrtho = ageLimitForOrtho;
	}
	public String getWorkInProgress() {
		return workInProgress;
	}
	public void setWorkInProgress(String workInProgress) {
		this.workInProgress = workInProgress;
	}
	public String getCoordinationBenefits() {
		return coordinationBenefits;
	}
	public void setCoordinationBenefits(String coordinationBenefits) {
		this.coordinationBenefits = coordinationBenefits;
	}
	public String getCalender() {
		return calender;
	}
	public void setCalender(String calender) {
		this.calender = calender;
	}
	public String getInsuranceBillingCycle() {
		return insuranceBillingCycle;
	}
	public void setInsuranceBillingCycle(String insuranceBillingCycle) {
		this.insuranceBillingCycle = insuranceBillingCycle;
	}
	public String getOrthoCoverragePer() {
		return orthoCoverragePer;
	}
	public void setOrthoCoverragePer(String orthoCoverragePer) {
		this.orthoCoverragePer = orthoCoverragePer;
	}
	public String getWaitingPeriodForOrtho() {
		return waitingPeriodForOrtho;
	}
	public void setWaitingPeriodForOrtho(String waitingPeriodForOrtho) {
		this.waitingPeriodForOrtho = waitingPeriodForOrtho;
	}
	public String getTimelyFilingLimit() {
		return timelyFilingLimit;
	}
	public void setTimelyFilingLimit(String timelyFilingLimit) {
		this.timelyFilingLimit = timelyFilingLimit;
	}
	public String getOrthoMax() {
		return orthoMax;
	}
	public void setOrthoMax(String orthoMax) {
		this.orthoMax = orthoMax;
	}
	public String getOrthoMaxRemaining() {
		return orthoMaxRemaining;
	}
	public void setOrthoMaxRemaining(String orthoMaxRemaining) {
		this.orthoMaxRemaining = orthoMaxRemaining;
	}
	public String getDeductibleForOrtho() {
		return deductibleForOrtho;
	}
	public void setDeductibleForOrtho(String deductibleForOrtho) {
		this.deductibleForOrtho = deductibleForOrtho;
	}
	public String getD8070() {
		return d8070;
	}
	public void setD8070(String d8070) {
		this.d8070 = d8070;
	}
	public String getD8080() {
		return d8080;
	}
	public void setD8080(String d8080) {
		this.d8080 = d8080;
	}
	public String getD8090() {
		return d8090;
	}
	public void setD8090(String d8090) {
		this.d8090 = d8090;
	}
	public String getD8670() {
		return d8670;
	}
	public void setD8670(String d8670) {
		this.d8670 = d8670;
	}
	public String getD8690() {
		return d8690;
	}
	public void setD8690(String d8690) {
		this.d8690 = d8690;
	}
	public String getCsrName() {
		return csrName;
	}
	public void setCsrName(String csrName) {
		this.csrName = csrName;
	}
	public String getReferenceNo() {
		return referenceNo;
	}
	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}
	public String getIVUpdatedBy() {
		return IVUpdatedBy;
	}
	public void setIVUpdatedBy(String iVUpdatedBy) {
		IVUpdatedBy = iVUpdatedBy;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getCompletionDate() {
		return completionDate;
	}
	public void setCompletionDate(String completionDate) {
		this.completionDate = completionDate;
	}
	public int getRowCounter() {
		return rowCounter;
	}
	public void setRowCounter(int rowCounter) {
		this.rowCounter = rowCounter;
	}
	public String getStatusDump() {
		return statusDump;
	}
	public void setStatusDump(String statusDump) {
		this.statusDump = statusDump;
	}
	public Office getOfficeDb() {
		return officeDb;
	}
	public void setOfficeDb(Office officeDb) {
		this.officeDb = officeDb;
	}
	
	
	
}
