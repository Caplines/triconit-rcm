package com.tricon.esdatareplication.entity.common;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public class CommonProviders extends CommonCloudColumn {
	
	/**
	 *  Checked
	 */
	private static final long serialVersionUID = 861120456836899889L;
	
	@Column(name = "provider_id", length = 50, nullable = false ,unique =true)
	String providerId;
	
	@Column(name = "first_name", length = 50, nullable = true)
	String firstName;
	
	
	@Column(name = "last_name", length = 50, nullable = true)
	String lastName;
	
	@Column(name = "address_1", length = 50, nullable = true)
	String address1;
	
	@Column(name = "address_2", length = 50, nullable = true)
	String address2;
	
	@Column(name = "city", length = 50, nullable = true)
	String city;

	@Column(name = "state", length = 50, nullable = true)
	String state;
	
	@Column(name = "zipcode", length = 50, nullable = true)
	String zipcode;
	
	@Column(name = "phone", length = 50, nullable = true)
	String phone;
	
	@Column(name = "sex", length = 15, nullable = true)
	String sex;

	@Column(name = "birth_date",  nullable = true)
	@Temporal(TemporalType.DATE)
	Date birth_date;

	@Column(name = "hire_date",nullable = true)
	@Temporal(TemporalType.DATE)
	Date hireDate;

	@Column(name = "social_security", length = 50, nullable = true)
	String socialSecurity;

	@Column(name = "status", length = 50, nullable = true)
	String status;

	@Column(name = "collections_go_to", length = 50, nullable = true)
	String collectionsGoTo;

	@Column(name = "provider_on_insurance", length = 50, nullable = true)
	String providerOnInsurance;

	@Column(name = "federal_tax_id", length = 50, nullable = true)
	String federalTaxId;

	@Column(name = "license", length = 50, nullable = true)
	String license;

	@Column(name = "dea_regulation_number", length = 50, nullable = true)
	String deaRegulationNumber;

	@Column(name = "medicaid", length = 50, nullable = true)
	String medicaid;

	@Column(name = "medicare", length = 50, nullable = true)
	String medicare;

	@Column(name = "bcbs", length = 50, nullable = true)
	String bcbs;

	@Column(name = "delta", length = 50, nullable = true)
	String delta;

	@Column(name = "notes", columnDefinition="text", nullable = true)
	String notes;

	@Column(name = "mtd_wo_statements", length = 50, nullable = true)
	Integer mtdWoStatements;

	@Column(name = "ytd_wo_statements", length = 50, nullable = true)
	Integer ytdWoStatements;

	@Column(name = "mtd_charges", length = 50, nullable = true)
	Integer mtdCharges;

	@Column(name = "ytd_charges", length = 50, nullable = true)
	Integer ytdCharges;

	@Column(name = "mtd_collections", length = 50, nullable = true)
	Integer mtdCollections;

	@Column(name = "ytd_collections", length = 50, nullable = true)
	Integer ytdCollections;
	
	@Column(name = "calculate_productivity",length=50, nullable = true)
	String calculateProductivity;
	
	@Column(name = "position_id",length=50, nullable = true)
	Integer position_id;
	
	@Column(name = "anesthesia_id",length=50, nullable = true)
	String anesthesiaId;

	@Column(name = "specialty",length=50, nullable = true)
	String specialty;

	@Column(name = "password",length=150, nullable = true)
	String password;

	@Column(name = "location",length=50, nullable = true)
	String location;

	@Column(name = "medicaid_specialty",length=50, nullable = true)
	String medicaidSpecialty;

	@Column(name = "medicaid_locator",length=50, nullable = true)
	String medicaidLocator;

	@Column(name = "medicaid_group",length=50, nullable = true)
	String medicaidGroup;

	@Column(name = "association",length=50, nullable = true)
	String association;
	
	@Column(name = "current_bal",length=50, nullable = true)
	Double currentBal;
	
	@Column(name = "contract_balance",length=50, nullable = true)
	Double contractBalance;
	
	@Column(name = "estimated_insurance",length=50, nullable = true)
	Double estimatedInsurance;
	
	@Column(name = "access_basic",length=50, nullable = true)
	String accessBasic;

	@Column(name = "access_accounting",length=50, nullable = true)
	String accessAccounting;

	@Column(name = "access_productivity",length=50, nullable = true)
	String accessProductivity;

	@Column(name = "mtd_new_patients",length=50, nullable = true)
	Integer mtdNewPatients;
	
	@Column(name = "mtd_other_debits",length=50, nullable = true)
	Double mtdOtherDebits;

	@Column(name = "ytd_other_debits",length=50, nullable = true)
	Double ytdOtherDebits;

	@Column(name = "mtd_other_credits",length=50, nullable = true)
	Double mtdOtherCredits;

	@Column(name = "ytd_other_credits",length=50, nullable = true)
	Double ytdOtherCredits;

	
	@Column(name = "other_id",length=50, nullable = true)
	String otherId;

	@Column(name = "billing_entity",length=50, nullable = true)
	String billingEntity;

	@Column(name = "billing_entity_lic_no",length=50, nullable = true)
	String billingEntityLicNo;

	@Column(name = "use_practice_address",length=50, nullable = true)
	String usePracticeAddress;

	@Column(name = "practice_id",length=50, nullable = true)
	Integer practiceId;
	
	@Column(name = "bank_account",length=50, nullable = true)
	String bankAccount;
	
	@Column(name = "access_appointments",length=50, nullable = true)
	String accessAppointments;

	@Column(name = "access_patients",length=50, nullable = true)
	String accessPatients;

	@Column(name = "access_contacts",length=50, nullable = true)
	String accessContacts;

	@Column(name = "access_provider",length=50, nullable = true)
	String accessProvider;

	@Column(name = "access_tx_plan",length=50, nullable = true)
	String accessTxPlan;

	@Column(name = "access_payment_plan",length=50, nullable = true)
	String accessPaymentPlan;

	@Column(name = "access_mass_updates",length=50, nullable = true)
	String accessMassUpdates;

	@Column(name = "site_id",length=50, nullable = true)
	String siteId;

	@Column(name = "voice_id",length=50, nullable = true)
	String voiceId;

	@Column(name = "email",length=50, nullable = true)
	String email;

	@Column(name = "access_prescriptions",length=50, nullable = true)
	String accessPrescriptions;

	@Column(name = "operatory_access",length=50, nullable = true)
	String operatoryAccess;

	@Column(name = "access_medical",length=50, nullable = true)
	String accessMedical;

	@Column(name = "access_timeclock",length=50, nullable = true)
	String accessTimeclock;

	@Column(name = "access_timeclock_management",length=50, nullable = true)
	String accessTimeclockManagement;

	@Column(name = "timesheet_password",length=50, nullable = true)
	String timesheetPassword;

	@Column(name = "other_id_2",length=50, nullable = true)
	String otherId2;

	@Column(name = "other_id_3",length=50, nullable = true)
	String otherId3;

	@Column(name = "other_id_4",length=50, nullable = true)
	String otherId4;

	@Column(name = "other_id_5",length=50, nullable = true)
	String otherId5;

	@Column(name = "access_lab",length=50, nullable = true)
	String accessLab;

	@Column(name = "national_prov_id",length=50, nullable = true)
	String nationalProvId;

	@Column(name = "Other_id_6",length=50, nullable = true)
	String OtherId6;

	@Column(name = "Other_id_7",length=50, nullable = true)
	String OtherId7;

	@Column(name = "Other_id_8",length=50, nullable = true)
	String OtherId8;

	@Column(name = "Other_id_9",length=50, nullable = true)
	String OtherId9;

	@Column(name = "Other_id_10",length=50, nullable = true)
	String OtherId10;

	@Column(name = "Other_id_11",length=50, nullable = true)
	String OtherId11;

	@Column(name = "Other_id_12",length=50, nullable = true)
	String OtherId12;

	@Column(name = "Other_id_13",length=50, nullable = true)
	String OtherId13;

	@Column(name = "Other_id_14",length=50, nullable = true)
	String OtherId14;

	@Column(name = "Other_id_15",length=50, nullable = true)
	String OtherId15;

	@Column(name = "Other_id_16",length=50, nullable = true)
	String OtherId16;

	@Column(name = "Other_id_17",length=50, nullable = true)
	String OtherId17;

	@Column(name = "Other_id_18",length=50, nullable = true)
	String OtherId18;

	@Column(name = "Other_id_19",length=50, nullable = true)
	String OtherId19;

	@Column(name = "Other_id_20",length=50, nullable = true)
	String OtherId20;

	@Column(name = "Other_id_21",length=50, nullable = true)
	String OtherId21;

	@Column(name = "daily_wo_statements",length=50, nullable = true)
	Integer dailyWoStatements;

	@Column(name = "daily_charges",length=50, nullable = true)
    Double dailyCharges;

	@Column(name = "daily_collections",length=50, nullable = true)
	Double dailyCollections;

	@Column(name = "daily_new_patients",length=50, nullable = true)
	Integer dailyNewPatients;

	@Column(name = "daily_other_debits",length=50, nullable = true)
	Double dailyOtherDebits;

	@Column(name = "daily_other_credits",length=50, nullable = true)
	Double dailyOtherCredits;

	@Column(name = "view_docs",length=50, nullable = true)
	String viewDocs;

	@Column(name = "add_docs",length=50, nullable = true)
	String addDocs;

	@Column(name = "edit_docs",length=50, nullable = true)
	String editDocs;

	@Column(name = "delete_docs",length=50, nullable = true)
	String deleteDocs;


	@Column(name = "pass_prompt_1",length=150, nullable = true)
	String passPromp1;

	@Column(name = "pass_answer_1",length=150, nullable = true)
	String passAnswer1;

	@Column(name = "pass_prompt_2",length=150, nullable = true)
	String passPrompt2;

	@Column(name = "pass_answer_2",length=150, nullable = true)
	String passAnswer2;

	@Column(name = "pass_prompt_3",length=150, nullable = true)
	String passPrompt3;

	@Column(name = "pass_answer_3",length=150, nullable = true)
	String passAnswer3;

	@Column(name = "tc_pass_prompt_1",length=150, nullable = true)
	String tcPassPrompt1;

	@Column(name = "tc_pass_answer_1",length=150, nullable = true)
	String tcPassAnswer1;

	@Column(name = "tc_pass_prompt_2",length=150, nullable = true)
	String tcPassPrompt2;

	@Column(name = "tc_pass_answer_2",length=150, nullable = true)
	String tcPassAnswer2;

	@Column(name = "tc_pass_prompt_3",length=150, nullable = true)
	String tcPassPrompt3;

	@Column(name = "tc_pass_answer_3",length=150, nullable = true)
	String tcPassAnswer3;

	@Column(name = "standard_fee_id",length=50, nullable = true)
	Integer standardFeeId;

	@Column(name = "access_site",length=50, nullable = true)
	String accessSite;

	@Column(name = "access_intellicare",length=50, nullable = true)
	String accessIntellicare;

	@Column(name = "security_profile",length=50, nullable = true)
	Integer securityProfile;

	@Column(name = "universal_id",length=50, nullable = true)
	String universalId;

	@Column(name = "provider_color",length=50, nullable = true)
	Integer providerColor;

	@Column(name = "last_detail_id",length=50, nullable = true)
	Integer lastDetailId;

	@Column(name = "ClinicianUserName",length=50, nullable = true)
	String ClinicianUserName;
	
	@Column(name = "ClinicianPassword",length=50, nullable = true)
	String ClinicianPassword;
	
	@Column(name = "last_logon",nullable = true)
	Date lastLogon;

	@Column(name = "encrypted_social_security",length=150, nullable = true)
	String encryptedSocialSecurity;
	
}
