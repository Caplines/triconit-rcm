package com.tricon.ruleengine.model.sheet;

public class EagleSoftRemBalDedMax {
	
	private String patient_id;
	private String firstName;
	private String lastName;
	private String socialSecurity;
	private String birthDate;
	private String primBbenefitsRemaining;
	private String primRemainingDeductible;
	private String secBenefitsRemaining;
	private String secRemainingDeductible;
	private String employerPrimEmployerMaximumCoverage;
	private String employerSecEmployerMaximumcoverage;
	
	
	
	public EagleSoftRemBalDedMax() {
	}

	public EagleSoftRemBalDedMax(String patient_id, String firstName, String lastName, String socialSecurity,
			String birthDate, String primBbenefitsRemaining, String primRemainingDeductible,
			String secBenefitsRemaining, String secRemainingDeductible, String employerPrimEmployerMaximumCoverage,
			String employerSecEmployerMaximumcoverage) {
		super();
		this.patient_id = patient_id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.socialSecurity = socialSecurity;
		this.birthDate = birthDate;
		this.primBbenefitsRemaining = primBbenefitsRemaining;
		this.primRemainingDeductible = primRemainingDeductible;
		this.secBenefitsRemaining = secBenefitsRemaining;
		this.secRemainingDeductible = secRemainingDeductible;
		this.employerPrimEmployerMaximumCoverage = employerPrimEmployerMaximumCoverage;
		this.employerSecEmployerMaximumcoverage = employerSecEmployerMaximumcoverage;
	}
	public String getPatient_id() {
		return patient_id;
	}
	public void setPatient_id(String patient_id) {
		this.patient_id = patient_id;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getSocialSecurity() {
		return socialSecurity;
	}
	public void setSocialSecurity(String socialSecurity) {
		this.socialSecurity = socialSecurity;
	}
	public String getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}
	public String getPrimBbenefitsRemaining() {
		return primBbenefitsRemaining;
	}
	public void setPrimBbenefitsRemaining(String primBbenefitsRemaining) {
		this.primBbenefitsRemaining = primBbenefitsRemaining;
	}
	public String getPrimRemainingDeductible() {
		return primRemainingDeductible;
	}
	public void setPrimRemainingDeductible(String primRemainingDeductible) {
		this.primRemainingDeductible = primRemainingDeductible;
	}
	public String getSecBenefitsRemaining() {
		return secBenefitsRemaining;
	}
	public void setSecBenefitsRemaining(String secBenefitsRemaining) {
		this.secBenefitsRemaining = secBenefitsRemaining;
	}
	public String getSecRemainingDeductible() {
		return secRemainingDeductible;
	}
	public void setSecRemainingDeductible(String secRemainingDeductible) {
		this.secRemainingDeductible = secRemainingDeductible;
	}
	public String getEmployerPrimEmployerMaximumCoverage() {
		return employerPrimEmployerMaximumCoverage;
	}
	public void setEmployerPrimEmployerMaximumCoverage(String employerPrimEmployerMaximumCoverage) {
		this.employerPrimEmployerMaximumCoverage = employerPrimEmployerMaximumCoverage;
	}
	public String getEmployerSecEmployerMaximumcoverage() {
		return employerSecEmployerMaximumcoverage;
	}
	public void setEmployerSecEmployerMaximumcoverage(String employerSecEmployerMaximumcoverage) {
		this.employerSecEmployerMaximumcoverage = employerSecEmployerMaximumcoverage;
	}
	

}
