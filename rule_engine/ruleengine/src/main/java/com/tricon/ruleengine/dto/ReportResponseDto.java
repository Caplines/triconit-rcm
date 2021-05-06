package com.tricon.ruleengine.dto;

import java.math.BigInteger;
import java.sql.Timestamp;

public class ReportResponseDto {
	
	private String rep_create_date;
	private String rep_created_by;
	private String rd_created_date;
	private String email;
	private String office_name;
	private int rep_group_run;
	private int rd_group_run;
	private String treatement_plan_id;
	private String dob;
	private String patient_name;
	private String ivf_form_id;
	private int rule_id;
	private String error_message;
	private String rule_name;
	private String patient_id;
	private String name;
    private BigInteger  messageType;
    private String dos;
    
	

	public String getDos() {
		return dos;
	}
	public void setDos(String dos) {
		this.dos = dos;
	}
	public String getRep_created_by() {
		return rep_created_by;
	}
	public void setRep_created_by(String rep_created_by) {
		this.rep_created_by = rep_created_by;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getOffice_name() {
		return office_name;
	}
	public void setOffice_name(String office_name) {
		this.office_name = office_name;
	}
	public int getRep_group_run() {
		return rep_group_run;
	}
	public void setRep_group_run(int rep_group_run) {
		this.rep_group_run = rep_group_run;
	}
	public String getTreatement_plan_id() {
		return treatement_plan_id;
	}
	public void setTreatement_plan_id(String treatement_plan_id) {
		this.treatement_plan_id = treatement_plan_id;
	}
	public String getDob() {
		return dob;
	}
	public void setDob(String dob) {
		this.dob = dob;
	}
	public String getPatient_name() {
		return patient_name;
	}
	public void setPatient_name(String patient_name) {
		this.patient_name = patient_name;
	}
	public String getIvf_form_id() {
		return ivf_form_id;
	}
	public void setIvf_form_id(String ivf_form_id) {
		this.ivf_form_id = ivf_form_id;
	}
	public int getRule_id() {
		return rule_id;
	}
	public void setRule_id(int rule_id) {
		this.rule_id = rule_id;
	}
	public String getError_message() {
		return error_message;
	}
	public void setError_message(String error_message) {
		this.error_message = error_message;
	}

	
	public String getRule_name() {
		return rule_name;
	}
	public void setRule_name(String rule_name) {
		this.rule_name = rule_name;
	}
	public String getPatient_id() {
		return patient_id;
	}
	public void setPatient_id(String patient_id) {
		this.patient_id = patient_id;
	}

	
	public String getRep_create_date() {
		return rep_create_date;
	}
	public void setRep_create_date(String rep_create_date) {
		this.rep_create_date = rep_create_date;
	}
	public String getRd_created_date() {
		return rd_created_date;
	}
	public void setRd_created_date(String rd_created_date) {
		this.rd_created_date = rd_created_date;
	}
	public int getRd_group_run() {
		return rd_group_run;
	}
	public void setRd_group_run(int rd_group_run) {
		this.rd_group_run = rd_group_run;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public BigInteger getMessageType() {
		return messageType;
	}
	public void setMessageType(BigInteger messageType) {
		this.messageType = messageType;
	}

	
	
	

}
