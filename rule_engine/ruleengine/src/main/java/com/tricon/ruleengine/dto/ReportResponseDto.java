package com.tricon.ruleengine.dto;

import java.util.Date;
import java.util.List;

import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.ReportDetail;
import com.tricon.ruleengine.model.db.Reports;

public class ReportResponseDto {
	
	private Date rep_created_date;
	private String rep_created_by;
	private Date rd_created_date;
	private String email;
	private String office_name;
	private int rep_group_run;
	private String treatement_plan_id;
	private String dob;
	private String patient_name;
	private String ivf_form_id;
	private int rule_id;
	private String error_message;
	private String rl_name;
	public Date getRep_created_date() {
		return rep_created_date;
	}
	public void setRep_created_date(Date rep_created_date) {
		this.rep_created_date = rep_created_date;
	}
	public String getRep_created_by() {
		return rep_created_by;
	}
	public void setRep_created_by(String rep_created_by) {
		this.rep_created_by = rep_created_by;
	}
	public Date getRd_created_date() {
		return rd_created_date;
	}
	public void setRd_created_date(Date rd_created_date) {
		this.rd_created_date = rd_created_date;
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
	public String getRl_name() {
		return rl_name;
	}
	public void setRl_name(String rl_name) {
		this.rl_name = rl_name;
	}

	

}
