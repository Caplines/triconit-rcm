package com.tricon.ruleengine.model.db;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "patient_detail_temp2")
public class PatientDetailTemp2 extends CommonPatientDetailLeft2 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6903793181793730011L;
    
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "patient_id")
	private Patient patient;

	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "patient_detail_id")
	private PatientDetailTemp patientDetail;


	public PatientDetailTemp getPatientDetail() {
		return patientDetail;
	}


	public void setPatientDetail(PatientDetailTemp patientDetail) {
		this.patientDetail = patientDetail;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public Patient getPatient() {
		return patient;
	}


	public void setPatient(Patient patient) {
		this.patient = patient;
	}




	//OS FORM
	/*
	private String subscriberName;//subscriberName
	subscriberName;//subscriberName
	subscriberdob;//subscriberdob --No
	basicInfo13;//SSN      --
	memberID;//basicInfo16
	patientName;//basicInfo2 
	patientDob;//basicInfo6
	csrName;//basicInfo8
	ref;//basicInfo12
	providerName;//name="basicInfo19"
	taxID;//basicInfo4
	planType;//policy1
	network;//policy3
	efficetiveDate;//policy5
	cyFy;//policy6 ///Cross check
	feeSchedule;//policy4
	depCovUptoAge;//policy11
	corrdOfBenefits;//
	whatAmountD7210;
	maximum;//policy7
	deductible;//policy8 ///Cross check
	allowAmountD7240;
	remainigBenefits;//policy9 ///Cross check
	remainingDeductible;//policy10 ///Cross check
	radio3;
	radio4;
	radio5;
	radio1;
	radio2;
	
	D7210;
	D7220;
	D7230;
	D7240;
	D7250;
	D7310;
	D7311;
	D7320;
	D7321;
	D7473;
	D9230;
	D9248;
	D9239;
	D9243;
	D4263;
	D4264;
	D6104;
	D7953;
	D3310;
	D3320;
	D3330;
	D3346;
	D3347;
	D3348;
	D6010;
	D6057;
	
	D6058
	D6190
	D4249
	D7951
	D9310
	D4266;
	D4267;
	D4341;
	D4273;
	D6065;
	D7251;
	ivSedation;//TEXT TYPE
	radio6;//policy15
	assignDate;//date
	*/

	
	
}
