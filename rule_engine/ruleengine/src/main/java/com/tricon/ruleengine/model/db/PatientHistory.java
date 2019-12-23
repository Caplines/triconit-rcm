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
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;

@Entity
@Table(name = "Patient_history")
public class PatientHistory implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7798914147037335129L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "patient_id")
	@Cascade( { org.hibernate.annotations.CascadeType.ALL })
	private Patient patient;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "office_id")
	private Office office;

	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "patient_detail_id")
	private PatientDetail pd;
	
	@Column(name = "history_code")
    private  String historyCode;
	
	@Column(name = "history_tooth")
    private  String historyTooth;
	
	@Column(name = "history_surface")
    private  String historySurface;
	
	@Column(name = "history_dos")
    private  String historyDOS;

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

	public String getHistoryCode() {
		return historyCode;
	}

	public void setHistoryCode(String historyCode) {
		this.historyCode = historyCode;
	}

	public String getHistoryTooth() {
		return historyTooth;
	}

	public void setHistoryTooth(String historyTooth) {
		this.historyTooth = historyTooth;
	}

	public String getHistorySurface() {
		return historySurface;
	}

	public void setHistorySurface(String historySurface) {
		this.historySurface = historySurface;
	}

	public String getHistoryDOS() {
		return historyDOS;
	}

	public void setHistoryDOS(String historyDOS) {
		this.historyDOS = historyDOS;
	}

	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}
	
	public PatientHistory() {
		super();
	}

	public PatientHistory(String historyCode, String historyTooth,
			String historySurface, String historyDOS) {
		super();
		this.historyCode = historyCode;
		this.historyTooth = historyTooth;
		this.historySurface = historySurface;
		this.historyDOS = historyDOS;
	}

	
	public PatientDetail getPd() {
		return pd;
	}

	public void setPd(PatientDetail pd) {
		this.pd = pd;
	}
	
	@Transient
	private  String pid;

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}
	
	@Transient
	private  int pdid;

	public int getPdid() {
		return pdid;
	}

	public void setPdid(int pdid) {
		this.pdid = pdid;
	}

		
	
	
}
