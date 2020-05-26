package com.tricon.ruleengine.model.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * This entity class represents Patient table
 * @author Deepak.Dogra
 *
 */

@Entity
@Table(name = "patient_temp")//,uniqueConstraints = {//uniqueConstraints = {@UniqueConstraint(columnNames= {"first", "second"})})//, @UniqueConstraint(columnNames = "ivf_form_id"),@UniqueConstraint(columnNames = "office_id")
		//@UniqueConstraint(columnNames = {"patient_id","office_id","website_name"}) })
        //no unique constraints.. Insert always new records on every Parsing of data
public class PatientTemp extends BaseAudit implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4321770126878620411L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;
	
	@Column(name = "patient_id")
	private String patientId;
	
	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;
	
	@Column(name = "salutation")
	private String salutation;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "office_id")
	private Office office;
	
	@Column(name = "dob")
	private String dob;
 
	@Column(name = "website_name")
	private String websiteName;
	
	@Column(name = "status", columnDefinition = "text")
	private String status;
	
	@Column(name = "row_number1")
	private String rowNumber;
	
	

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "patient")
	private Set<PatientDetailTemp> patientDetails = new HashSet<PatientDetailTemp>(0);

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "patient")
	private Set<PatientHistoryTemp> patientHistory = new HashSet<PatientHistoryTemp>(0);

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
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

	public String getSalutation() {
		return salutation;
	}

	public void setSalutation(String salutation) {
		this.salutation = salutation;
	}

	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public Set<PatientDetailTemp> getPatientDetails() {
		return patientDetails;
	}

	public String getRowNumber() {
		return rowNumber;
	}

	public void setRowNumber(String rowNumber) {
		this.rowNumber = rowNumber;
	}

	public void setPatientDetails(Set<PatientDetailTemp> patientDetails) {
		this.patientDetails = patientDetails;
	}

	public Set<PatientHistoryTemp> getPatientHistory() {
		return patientHistory;
	}

	public void setPatientHistory(Set<PatientHistoryTemp> patientHistory) {
		this.patientHistory = patientHistory;
	}

	public String getWebsiteName() {
		return websiteName;
	}

	public void setWebsiteName(String websiteName) {
		this.websiteName = websiteName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}





}
