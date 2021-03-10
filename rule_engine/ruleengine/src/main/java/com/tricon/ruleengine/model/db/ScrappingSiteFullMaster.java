package com.tricon.ruleengine.model.db;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "scrapping_site_details_master")
public class ScrappingSiteFullMaster extends BaseAudit implements Serializable{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 6956672626178564895L;

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "scrapping_site_id")
	private ScrappingSiteFull scrappingSite;
	
	@Column(name = "patient_id")
	private boolean patientId;

	@Column(name = "first_name")
	private boolean firstName;
	
	@Column(name = "last_name")
	private boolean lastName;
	
	@Column(name = "dob")
	private boolean dob;

	@Column(name = "enrollee_Id")
	private boolean enrolleeId;

	@Column(name = "ssn_number")
	private boolean ssnNumber;
	
	@Column(name = "member_id")
	private boolean memberId;

	@Column(name = "location_provider")
	private boolean locationProvider;

	@Column(name = "grade_pay")
	private boolean gradePay;

	@Column(name = "subscribers_fname")
	private boolean subscribersFirstName;

	@Column(name = "subscribers_lname")
	private boolean subscribersLastName;

	@Column(name = "subscribers_dob")
	private boolean subscribersDob;

	@Column(name = "otp")
	private boolean otp;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ScrappingSiteFull getScrappingSite() {
		return scrappingSite;
	}

	public void setScrappingSite(ScrappingSiteFull scrappingSite) {
		this.scrappingSite = scrappingSite;
	}

	public boolean isPatientId() {
		return patientId;
	}

	public void setPatientId(boolean patientId) {
		this.patientId = patientId;
	}

	public boolean isFirstName() {
		return firstName;
	}

	public void setFirstName(boolean firstName) {
		this.firstName = firstName;
	}

	public boolean isLastName() {
		return lastName;
	}

	public void setLastName(boolean lastName) {
		this.lastName = lastName;
	}

	public boolean isDob() {
		return dob;
	}

	public void setDob(boolean dob) {
		this.dob = dob;
	}

	public boolean isEnrolleeId() {
		return enrolleeId;
	}

	public void setEnrolleeId(boolean enrolleeId) {
		this.enrolleeId = enrolleeId;
	}

	public boolean isSsnNumber() {
		return ssnNumber;
	}

	public void setSsnNumber(boolean ssnNumber) {
		this.ssnNumber = ssnNumber;
	}

	public boolean isLocationProvider() {
		return locationProvider;
	}

	public void setLocationProvider(boolean locationProvider) {
		this.locationProvider = locationProvider;
	}

	public boolean isMemberId() {
		return memberId;
	}

	public void setMemberId(boolean memberId) {
		this.memberId = memberId;
	}

	public boolean isGradePay() {
		return gradePay;
	}

	public void setGradePay(boolean gradePay) {
		this.gradePay = gradePay;
	}

	public boolean isSubscribersFirstName() {
		return subscribersFirstName;
	}

	public void setSubscribersFirstName(boolean subscribersFirstName) {
		this.subscribersFirstName = subscribersFirstName;
	}

	public boolean isSubscribersLastName() {
		return subscribersLastName;
	}

	public void setSubscribersLastName(boolean subscribersLastName) {
		this.subscribersLastName = subscribersLastName;
	}

	public boolean isSubscribersDob() {
		return subscribersDob;
	}

	public void setSubscribersDob(boolean subscribersDob) {
		this.subscribersDob = subscribersDob;
	}

	public boolean isOtp() {
		return otp;
	}

	public void setOtp(boolean otp) {
		this.otp = otp;
	}

	
	
	
}
