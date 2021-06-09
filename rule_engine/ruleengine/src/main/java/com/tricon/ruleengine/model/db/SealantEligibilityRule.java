package com.tricon.ruleengine.model.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "sealant_eligib_rule",uniqueConstraints = {	@UniqueConstraint(columnNames = {"insurance_name","plan_name","covered_tooth_no"}) })
public class SealantEligibilityRule implements java.io.Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2986228115603189121L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;

	@Column(name = "insurance_name", nullable = false)
	private String insuranceName;

	@Column(name = "plan_name", nullable = false)
	private String planName;
 
	/*
	@Column(name = "pre_auth", nullable = false)
	private String preAuth;

	@Column(name = "code_tooth_1_d1351", nullable = false)
	private String codetooth1D1351;

	@Column(name = "code_tooth_2_d1352", nullable = false)
	private String codetooth2D1352;

	@Column(name = "last_sealant_d1351", nullable = false)
	private String lastSealantD1351;

	@Column(name = "last_sealant_d1352", nullable = false)
	private String lastSealantD1352;

	@Column(name = "eligibility_last_sealant", nullable = false)
	private String eligibilityLastSealant;

	@Column(name = "other_done", nullable = false)
	private String otherDone;

	@Column(name = "eligible", nullable = false)
	private String eligible;

	@Column(name = "patient_under_allowed_age_range", nullable = false)
	private String patientUnderAllowedAgeRange;
    */
	
	@Column(name = "covered_tooth_no", nullable = false)
	private String coveredToothNo ;

	@Column(name = "from_age", nullable = false)
	private int fromAge;

	@Column(name = "to_age", nullable = false)
	private int toAge;

	/*
	@Column(name = "patient_age", nullable = false)
	private String patientAge;
    */
	
	@Column(name = "active", nullable = false)
	private int active;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getInsuranceName() {
		return insuranceName;
	}

	public void setInsuranceName(String insuranceName) {
		this.insuranceName = insuranceName;
	}

	public String getPlanName() {
		return planName;
	}

	public void setPlanName(String planName) {
		this.planName = planName;
	}

	public String getCoveredToothNo() {
		return coveredToothNo;
	}

	public void setCoveredToothNo(String coveredToothNo) {
		this.coveredToothNo = coveredToothNo;
	}

	public int getFromAge() {
		return fromAge;
	}

	public void setFromAge(int fromAge) {
		this.fromAge = fromAge;
	}

	public int getToAge() {
		return toAge;
	}

	public void setToAge(int toAge) {
		this.toAge = toAge;
	}

	public int getActive() {
		return active;
	}

	public void setActive(int active) {
		this.active = active;
	}


	
	
	
	
}
