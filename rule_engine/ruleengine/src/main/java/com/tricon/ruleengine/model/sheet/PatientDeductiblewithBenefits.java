package com.tricon.ruleengine.model.sheet;

public class PatientDeductiblewithBenefits {

	private String patientId;
	private String serviceDescription;
	private String benefitPrecentage;
	private String benefitDeductible;
	
	public String getPatientId() {
		return patientId;
	}
	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}
	public String getServiceDescription() {
		return serviceDescription;
	}
	public void setServiceDescription(String serviceDescription) {
		this.serviceDescription = serviceDescription;
	}
	public String getBenefitPrecentage() {
		return benefitPrecentage;
	}
	public void setBenefitPrecentage(String benefitPrecentage) {
		this.benefitPrecentage = benefitPrecentage;
	}
	public String getBenefitDeductible() {
		return benefitDeductible;
	}
	public void setBenefitDeductible(String benefitDeductible) {
		this.benefitDeductible = benefitDeductible;
	}
	
	
}
