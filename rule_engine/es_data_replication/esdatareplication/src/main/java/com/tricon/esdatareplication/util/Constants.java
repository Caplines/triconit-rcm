package com.tricon.esdatareplication.util;

import java.text.SimpleDateFormat;

public class Constants {

	public static SimpleDateFormat SDF_LOG_FILE = new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss");
	
	public static final String QUERY_TOP_REP="TOP__REP__";
	public static final String QUERY_START_REP="START__REP__";
	
	public static final String QUERY_WHERE_CLAUSE_REP="WHERE__REP__";
	
	
	public static final String PATIENTS_COLUMNS="patient_id,first_name,last_name,salutation,address_1,address_2,city,state,zipcode,home_phone,"+
			"work_phone,ext,status,sex,marital_status,responsible_party_status,responsible_party,social_security,"+
			"birth_date,notes,preferred_dentist,preferred_hygienist,recall_frequency,cleaning_time,receive_recalls,"+
			"discount_id,current_bal,thirty_day,sixty_day,ninety_day,contract_balance,estimated_insurance,"+
			"first_visit_date,last_date_seen,cancelled_appointments,charges_mtd,collections_mtd,charges_ytd,collections_ytd,"+
			"failed_appointments,prim_responsible_id,prim_relationship,prim_employer_id,prim_outstanding_balance,"+
			"prim_benefits_remaining,prim_remaining_deductible,sec_responsible_id,sec_relationship,"+
			"sec_employer_id,sec_outstanding_balance,sec_benefits_remaining,sec_remaining_deductible,short_notice,"+
			"prefers_ampm,last_regular_appointment,next_regular_appointment,last_preventive_appointment,"+
			"next_preventive_appointment,fee_level_id,recommended_work,submitted_total,prim_total_paid,sec_total_paid,"+
			"patient_status,policy_holder_status,next_recall_date,last_recall_date,ytd_visits,"+
			"next_preventive_appt_time,next_regular_appt_time,school,school_city,employment_status,employment_id_number,"+
			"student_status,medicaid_id,death_indicator,signature_on_file,release_info_on_file,"+
			"carrier_id,epsdt_flag,patient_image_id,other_id,guards_id,practice_id,email_address,date_entered,last_soft_exam,"+
			"last_restorative_exam,last_tmj_exam,last_occl_exam,last_intraoral_exam,last_radiography_exam,last_cosmetic_exam,"+
			"last_head_exam,last_habits_exam,last_general_exam,last_cancer_exam,last_history_exam,last_bitewings,"+
			"last_full_mouth,teeth_status,recall_batch,last_pano_date,pharmacy_id,neither_appointments,"+
			"pre_med,rx_id,missing_teeth,receive_email,chart_id,cell_phone,pager_phone,drivers_license,"+
			"hipaa_priv_pract,hipaa_authorization,hipaa_priv_pract_date,hipaa_authorization_date,hipaa_consent,"+
			"hipaa_consent_date,prim_member_id,sec_member_id,daily_charges,daily_collections,"+
			"ortho_patient,ortho_months_of_tx,ortho_date_started,ortho_ins_bill_freq,preferred_name,"+
			"middle_initial,school_address,school_state,school_zipcode,sec_depend_code,caesy_language,"+
			"receives_sms,medicaid_seq_num,universal_id,password,security_question_one,security_question_two,"+
			"security_question_three,security_answer_one,security_answer_two,security_answer_three,registration_verified,"+
			"DolphinID,password_salt,encrypted_social_security,last_medical_history"; 
	
	public static final SimpleDateFormat SimpleDateformatForEsQuery= new SimpleDateFormat("yyyy-MM-dd");
	
			
	
	//public static int FETCHED_ROW_COUNT_FROM_DB=100;
	
}
