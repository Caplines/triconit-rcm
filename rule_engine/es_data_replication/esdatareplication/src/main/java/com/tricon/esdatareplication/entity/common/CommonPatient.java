package com.tricon.esdatareplication.entity.common;

import java.sql.Time;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;


import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public class CommonPatient extends CommonCloudColumn{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1990940111528929546L;

	@Column(name = "patient_id", length = 50,unique=true)
	String patientId;
	
	@Column(name = "first_name", length = 50)
	String firstName;
	
	@Column(name = "last_name", length = 50)
	String lastName;
	
	@Column(name = "salutation", length = 50)
	String salutation;;
	
	@Column(name = "address_1", length = 50)
	String address1;
	
	
	@Column(name = "address_2", length = 50)
	String address2;
	
	@Column(name = "city", length = 50)
	String city;
	
	@Column(name = "state", length = 50)
	String state;
	
	
	@Column(name = "zipcode", length = 50)
	String zipcode;
	
	@Column(name = "home_phone", length = 50)
	String homePhone;
	
	@Column(name = "work_phone", length = 50)
	String workPhone;
	
	@Column(name = "ext", length = 50)
	String ext;
	
	@Column(name = "status", length = 50)
	String status;
	
	@Column(name = "sex", length = 50)
	String sex;
	
	@Column(name = "marital_status", length = 50)
	String maritalStatus;
	
	@Column(name = "responsible_party_status", length = 50)
	String responsiblePartyStatus;
	
	@Column(name = "responsible_party", length = 50)
	String responsibleParty;
	
	
	@Column(name = "social_security", length = 50)
	String socialSecurity;
	
	@Column(name = "birth_date")
	Date birthDate;
	
	
	@Column(name = "notes",  columnDefinition="text")
	String notes;
	
	
	@Column(name = "preferred_dentist", length = 50)
	String preferredDentist;
	
	@Column(name = "preferred_hygienist", length = 50)
	String preferredHygienist;

	@Column(name = "recall_frequency", length = 50)
	int recallFrequency;
	
	
	@Column(name = "cleaning_time", length = 50)
	int cleaningTime;
    
    
    @Column(name = "receive_recalls", length = 50)
	String receiveReCalls;
    
    @Column(name = "discount_id", length = 50)
	int discountId;
    
    @Column(name = "current_bal", length = 50)
	int currentBal;
    
    @Column(name = "thirty_day", length = 50)
	int thirtyDay;
    
    @Column(name = "sixty_day", length = 50)
	int sixtyDay;
    
    @Column(name = "ninety_day", length = 50)
	int ninetyDay;
    
    @Column(name = "contract_balance", length = 50)
    int contractBalance;
     
    @Column(name = "estimated_insurance", length = 50)
    int estimatedInsurance;
    
    @Column(name = "first_visit_date")
    Date firstVisitDate;
    
    @Column(name = "last_date_date", length = 50)
    Date lastDateSeen;

    
	@Column(name = "cancelled_appointments", length = 50)
	int cancelledAppointments;
    
    @Column(name = "charges_mtd", length = 50)
    int chargesMtd;
    
    @Column(name = "collections_mtd", length = 50)
    int collectionsMtd;
    
    @Column(name = "charges_ytd", length = 50)
    int chargesYtd;
    
    
    @Column(name = "collections_ytd", length = 50)
    int collectionsYtd;
     
    @Column(name = "failed_appointments", length = 50)
    int failedAppointments;
	
	@Column(name = "prim_responsible_id", length = 50)
	String primResponsibleId;
	
	@Column(name = "prim_relationship", length = 50)
	String primRelationship;
	
	@Column(name = "prim_employer_id", length = 50)
	int primEmployerId;
	
	@Column(name = "prim_outstanding_balance", length = 50)
	int primOutstandingBalance;
	
	
	@Column(name = "prim_benefits_remaining", length = 50)
	int primBenefitsRemaining;
	
	@Column(name = "prim_remaining_deductible", length = 50)
	int primRemainingDeductible;

	
	@Column(name = "sec_responsible_id", length = 50)
	String secResponsibleId;
	
	@Column(name = "sec_relationship", length = 50)
	String secRelationship;
	
	@Column(name = "sec_employer_id", length = 50)
	int secEmployerId;
	
	@Column(name = "sec_outstanding_balance", length = 50)
	int secOutstandingBalance;
	
	@Column(name = "sec_benefits_remaining", length = 50)
	int secBenefitsRemaining;
	
	@Column(name = "sec_remaining_deductible", length = 50)
	int secRemainingDeductible;
	
	@Column(name = "short_notice", length = 50)
	String shortNotice;
	
	@Column(name = "prefers_ampm", length = 50)
	String prefersAmpm;
	
	@Column(name = "last_regular_appointment")
	Date lastRegularAppointment;
	
	@Column(name = "next_regular_appointment")	
	Date nextRegularAppointment;	
	
	@Column(name = "last_preventive_appointment")
	Date lastPreventiveAppointment;
	
	@Column(name = "next_preventive_appointment")
	Date nextPreventiveAppointment;
	
	
	@Column(name = "fee_level_id", length = 50)
	int feeLevelId;
	
	@Column(name = "recommended_work", length = 50)
	String recommendedWork;
	
	@Column(name = "submitted_total", length = 50)
	int submittedTotal;
	
	@Column(name = "prim_total_paid", length = 50)
	int primTotalPaid;
	
	@Column(name = "sec_total_paid", length = 50)
	int secTotalPaid;
	
	@Column(name = "patient_status", length = 50)
	String patientStatus;
	
	@Column(name = "policy_holder_status", length = 50)
	String policyHolderStatus;
	
	@Column(name = "next_recall_date")
	Date nextRecallDate;
	
	@Column(name = "last_recall_date")
	Date lastRecallDate;
	
	@Column(name = "ytd_visits", length = 50)
	int ytdVisits;
	
	@Column(name = "next_preventive_appt_time", length = 50)
	Time nextPreventiveApptTime;
	
	@Column(name = "next_regular_appt_time", length = 50)
	Time nextRegularApptTime;
	
	@Column(name = "school", length = 50)
	String school;
	
	@Column(name = "school_city", length = 50)
	String schoolCity;
	
	@Column(name = "employment_status", length = 50)
	String employmentStatus;
	
	@Column(name = "employment_id_number", length = 50)
	String employmentIdNumber;
	
	@Column(name = "student_status", length = 50)
	String studentStatus;
	
	@Column(name = "medicaid_id", length = 50)
	String medicaidId;
	
	@Column(name = "death_indicator", length = 50)
	String deathIndicator;
	
	@Column(name = "signature_on_file", length = 50)
	String signatureOnFile;
	
	@Column(name = "release_info_on_file", length = 50)
	String releaseInfoOnFile;
	
	@Column(name = "carrier_id", length = 50)
	String carrierId;
	
	@Column(name = "epsdt_flag", length = 50)
	String epsdtFlag;
	
	
	@Column(name = "patient_image_id", length = 50)
	int patientImageId;
	
	@Column(name = "other_id", length = 50)
	String otherId;
	
	@Column(name = "guards_id", length = 50)
	String guardsId;
	
	@Column(name = "practice_id", length = 50)
	int practiceId;
	
	@Column(name = "email_address", length = 150)
	String emailAddress;
	
	@Column(name = "date_entered")
	Date dateEntered;
	
	@Column(name = "last_soft_exam")
	Date lastSoftExam;
	
	@Column(name = "last_restorative_exam")
	Date lastRestorativeExam;
	
	@Column(name = "last_tmj_exam")
	Date lastTmjExam;
	
	@Column(name = "last_occl_exam")
	Date lastOcclExam;

	@Column(name = "last_intraoral_exam")
	Date lastIntraoralExam;
	
	@Column(name = "last_radiography_exam")
	Date lastRadiographyExam;
	
	
	@Column(name = "last_cosmetic_exam")
	Date lastCosmeticExam;
	
	@Column(name = "last_head_exam")
	Date lastHeadExam;
	
	@Column(name = "last_habits_exam")
	Date lastHabitsExam;
	
	@Column(name = "last_general_exam")
	Date lastGeneralExam;
	
	@Column(name = "last_cancer_exam")
	Date lastCancerExam;
	
	
	@Column(name = "last_history_exam")
	Date lastHistoryExam;
	
	@Column(name = "last_bitewings")
	Date lastBitewings;
	
	@Column(name = "last_full_mouth")
	Date lastFullMouth;
	
	
	@Column(name = "teeth_status", length = 50)
	String teethStatus;
	
	
	@Column(name = "recall_batch", length = 50)
	int recallBatch;
	
	
	@Column(name = "last_pano_date")
	Date lastPanoDate;
	
	@Column(name = "pharmacy_id", length = 50)
	int pharmacyId;	
	
	@Column(name = "neither_appointments", length = 50)
	int neitherAppointments;
	
	@Column(name = "pre_med", length = 50)
	String preMed;
	
	@Column(name = "rx_id", length = 50)
	String rxId;
	
	@Column(name = "missing_teeth", length = 60)
	String missingTeeth;
	
	@Column(name = "receive_email", length = 50)
	String receiveEmail;
	
	
	@Column(name = "chart_id", length = 50)
	String chartId;
	
	@Column(name = "cell_phone", length = 50)
	String cellPhone;
	
	@Column(name = "pager_phone", length = 50)
	String pagerPhone;
	
	
	@Column(name = "drivers_license", length = 50)
	String driversLicense;
	
	@Column(name = "hipaa_priv_pract", length = 50)
	String hipaaPrivPract;
	
	@Column(name = "hipaa_authorization", length = 50)
	String hipaaAuthorization;
	
	@Column(name = "hipaa_priv_pract_date")
	Date hipaaPrivPractDate;
	
	@Column(name = "hipaa_authorization_date")
	Date hipaaAuthorizationDate;
	
	@Column(name = "hipaa_consent", length = 50)
	String hipaaConsent;
	
	
	@Column(name = "hipaa_consent_date")
	Date hipaaConsentDate;
	
	@Column(name = "prim_member_id", length = 50)
	String primMemberId;
	
	
	@Column(name = "sec_member_id", length = 50)
	String secMemberId;
	
	@Column(name = "daily_charges", length = 50)
	int dailyCharges;
	
	@Column(name = "daily_collections", length = 50)
	int dailyCollections;

	@Column(name = "ortho_patient", length = 50)
	String orthoPatient;
	
	@Column(name = "ortho_months_of_tx", length = 50)
	int orthoMonthsOfTx;
	
	@Column(name = "ortho_date_started")
	Date orthoDateStarted;
	
	@Column(name = "ortho_ins_bill_freq", length = 50)
	int orthoInsBillFreq;
	
	@Column(name = "preferred_name", length = 50)
	String preferredName;
	
	@Column(name = "middle_initial", length = 50)
	String middleInitial;
	
	@Column(name = "school_address", length = 50)
	String schoolAddress;
	
	@Column(name = "school_state", length = 50)
	String schoolState;
	
	@Column(name = "school_zipcode", length = 50)
	String schoolZipcode;
	
	@Column(name = "sec_depend_code", length = 50)
	String secDependCode;

	@Column(name = "caesy_language", length = 50)
	int caesylanguage;
	
	@Column(name = "receives_sms", length = 50)
	String receivesSms;
	
	
	@Column(name = "medicaid_seq_num", length = 50)
	String medicaidSeqNum;
	
	@Column(name = "universal_id", length = 50)
	String universalId;
	
	
	@Column(name = "password", length = 50)
	String password;
	
	@Column(name = "security_question_one", length = 50)
	String securityQuestionOne;
	
	
	@Column(name = "security_question_two", length = 256)
	String securityQuestionTwo;
	
	
	@Column(name = "security_question_three", length = 256)
	String securityQuestionThree;
	
	
	@Column(name = "security_answer_one", length = 256)
	String securityAnswerOne;
	
	
	@Column(name = "security_answer_two", length = 256)
	String securityAnswerTwo;
	
	
	@Column(name = "security_answer_three", length = 256)
	String securityAnswerThree;
	
	@Column(name = "registration_verified ", length = 256)
	boolean registrationVerified; 
	
	@Column(name = "DolphinID", length = 50)
	String DolphinID;
	
	
	@Column(name = "password_salt", length = 150)
	String passwordSalt;
	
	
	@Column(name = "encrypted_social_security", length = 150)
	String encryptedSocialSecurity;
	
	@Column(name = "last_medical_history")
	Date lastMedicalHistory;

	
	public CommonPatient() {
		super();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
