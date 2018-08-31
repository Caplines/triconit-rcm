package com.tricon.ruleengine.model.sheet;

/**
 * @author Deepak.Dogra
 *
 */
public class TreatmentPlan {
	
	private String id;
	private String apptId;
	private TreatmentPlanPatient patient;
	private TreatmentPlanDetails treatmentPlanDetails;
	private String 	lineItem;
	private String 	serviceCode;
	private String 	description;
	private String 	surface;
	private String 	tooth;
	private String 	status;
	private String 	fee;
	private String  providerLastName;
	private String 	estInsurance;
	private String 	PatientPortion;
	private String 	estPrimary;
	
	
	public TreatmentPlan() {
		
	}
	
	public TreatmentPlan(String id, String apptId, TreatmentPlanPatient patient,
			TreatmentPlanDetails treatmentPlanDetails, String lineItem, String serviceCode, String description,
			String surface, String tooth, String status, String fee, String estInsurance, String patientPortion) {
		super();
		this.id = id;
		this.apptId = apptId;
		this.patient = patient;
		this.treatmentPlanDetails = treatmentPlanDetails;
		this.lineItem = lineItem;
		this.serviceCode = serviceCode;
		this.description = description;
		this.surface = surface;
		this.tooth = tooth;
		this.status = status;
		this.fee = fee;
		this.estInsurance = estInsurance;
		PatientPortion = patientPortion;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getApptId() {
		return apptId;
	}
	public void setApptId(String apptId) {
		this.apptId = apptId;
	}
	public TreatmentPlanPatient getPatient() {
		return patient;
	}
	public void setPatient(TreatmentPlanPatient patient) {
		this.patient = patient;
	}
	public TreatmentPlanDetails getTreatmentPlanDetails() {
		return treatmentPlanDetails;
	}
	public void setTreatmentPlanDetails(TreatmentPlanDetails treatmentPlanDetails) {
		this.treatmentPlanDetails = treatmentPlanDetails;
	}
	public String getLineItem() {
		return lineItem;
	}
	public void setLineItem(String lineItem) {
		this.lineItem = lineItem;
	}
	public String getServiceCode() {
		return serviceCode;
	}
	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getSurface() {
		return surface;
	}
	public void setSurface(String surface) {
		this.surface = surface;
	}
	public String getTooth() {
		return tooth;
	}
	public void setTooth(String tooth) {
		this.tooth = tooth;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getFee() {
		return fee;
	}
	public void setFee(String fee) {
		this.fee = fee;
	}
	public String getEstInsurance() {
		estInsurance=estInsurance.replaceAll("\"", "");
		return estInsurance;
	}
	public void setEstInsurance(String estInsurance) {
		this.estInsurance = estInsurance;
	}
	public String getPatientPortion() {
		return PatientPortion;
	}
	public void setPatientPortion(String patientPortion) {
		PatientPortion = patientPortion;
	}

	public String getEstPrimary() {
		return estPrimary;
	}

	public void setEstPrimary(String estPrimary) {
		this.estPrimary = estPrimary;
	}

	public String getProviderLastName() {
		return providerLastName;
	}

	public void setProviderLastName(String providerLastName) {
		this.providerLastName = providerLastName;
	}
	
	

}
