package com.tricon.esdatareplication.util;

import java.text.SimpleDateFormat;

public class Constants {

	public static SimpleDateFormat SDF_LOG_FILE = new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss");

	public static final String QUERY_TOP_REP = "TOP__REP__";
	public static final String QUERY_START_REP = "START__REP__";

	public static final String QUERY_WHERE_CLAUSE_REP = "WHERE__REP__";

	public static final String TABLE_PATIENT = "patient";// 1
	public static final String TABLE_CHAIRS = "chairs";// 2
	public static final String TABLE_APPOINTMENT = "appointment";// 3
	public static final String TABLE_PAYTYPE = "paytype";// 4
	public static final String TABLE_EMPLOYER = "employer";// 5
	public static final String TABLE_TRANSACTIONS = "transactions";// 6
	public static final String TABLE_TRANSACTIONS_DETAIL = "transactions_detail";// 7
	public static final String TABLE_PAYMENT_PROVIDER = "payment_provider";// 8
	public static final String TABLE_PLANNED_SERVICES = "planned_services";// 9
	public static final String TABLE_TREATMENT_PLAN_ITEMS = "treatment_plan_items";// 10
	public static final String TABLE_TREATMENT_PLANS = "treatment_plans";// 11
	public static final String TABLE_PROVIDER = "provider";// 12
	public static final String TABLE_TRANSACTIONS_HEADER = "transactions_header";// 13
	public static final String TABLE_REPLICA_IN_CLOUD = "es_data_replica_";

	public static final String PATIENTS_COLUMNS = "patient_id,first_name,last_name,salutation,address_1,address_2,city,state,zipcode,home_phone,"
			+ "work_phone,ext,status,sex,marital_status,responsible_party_status,responsible_party,social_security,"
			+ "birth_date,notes,preferred_dentist,preferred_hygienist,recall_frequency,cleaning_time,receive_recalls,"
			+ "discount_id,current_bal,thirty_day,sixty_day,ninety_day,contract_balance,estimated_insurance,"
			+ "first_visit_date,last_date_seen,cancelled_appointments,charges_mtd,collections_mtd,charges_ytd,collections_ytd,"
			+ "failed_appointments,prim_responsible_id,prim_relationship,prim_employer_id,prim_outstanding_balance,"
			+ "prim_benefits_remaining,prim_remaining_deductible,sec_responsible_id,sec_relationship,"
			+ "sec_employer_id,sec_outstanding_balance,sec_benefits_remaining,sec_remaining_deductible,short_notice,"
			+ "prefers_ampm,last_regular_appointment,next_regular_appointment,last_preventive_appointment,"
			+ "next_preventive_appointment,fee_level_id,recommended_work,submitted_total,prim_total_paid,sec_total_paid,"
			+ "patient_status,policy_holder_status,next_recall_date,last_recall_date,ytd_visits,"
			+ "next_preventive_appt_time,next_regular_appt_time,school,school_city,employment_status,employment_id_number,"
			+ "student_status,medicaid_id,death_indicator,signature_on_file,release_info_on_file,"
			+ "carrier_id,epsdt_flag,patient_image_id,other_id,guards_id,practice_id,email_address,date_entered,last_soft_exam,"
			+ "last_restorative_exam,last_tmj_exam,last_occl_exam,last_intraoral_exam,last_radiography_exam,last_cosmetic_exam,"
			+ "last_head_exam,last_habits_exam,last_general_exam,last_cancer_exam,last_history_exam,last_bitewings,"
			+ "last_full_mouth,teeth_status,recall_batch,last_pano_date,pharmacy_id,neither_appointments,"
			+ "pre_med,rx_id,missing_teeth,receive_email,chart_id,cell_phone,pager_phone,drivers_license,"
			+ "hipaa_priv_pract,hipaa_authorization,hipaa_priv_pract_date,hipaa_authorization_date,hipaa_consent,"
			+ "hipaa_consent_date,prim_member_id,sec_member_id,daily_charges,daily_collections,"
			+ "ortho_patient,ortho_months_of_tx,ortho_date_started,ortho_ins_bill_freq,preferred_name,"
			+ "middle_initial,school_address,school_state,school_zipcode,sec_depend_code,caesy_language,"
			+ "receives_sms,medicaid_seq_num,universal_id,password,security_question_one,security_question_two,"
			+ "security_question_three,security_answer_one,security_answer_two,security_answer_three,registration_verified,"
			+ "DolphinID,password_salt,encrypted_social_security,last_medical_history ";

	public static final String APPOINTMENT_COLUMNS = "appointment_id,description,allday_event,start_time,"
			+ "end_time,patient_id,recall_id,location_id,	" + "location_free_form,classification,	"
			+ "appointment_type_id,prefix,dollars_scheduled,date_appointed,	"
			+ "date_confirmed,appointment_notes,deletion_note,sooner_if_possible,	"
			+ "scheduled_by,modified_by,appointment_name,arrival_status,	"
			+ "arrival_time,inchair_time,walkout_time,confirmation_status,	" + "confirmation_note,auto_confirm_sent,"
			+ "recurrence_id,private,priority,appointment_data ";

	public static final String EMPLOYER_COLUMNS = "employer_id,name,address_1,address_2,"
			+ "city,state,zipcode,contact,phone1," + "phone1ext,phone2,phone2ext,fax,"
			+ "signature_on_file_yn,group_number,group_name," + "insurance_company_id,form_id,fee_schedule,"
			+ "maximum_coverage,lifetime_maximum_yn,yearly_deductible,"
			+ "lifetime_deductible_yn,beginning_month,submit_electronically," + "m_number_submitted,y_number_submitted,"
			+ "m_amount_submitted,y_amount_submitted," + "m_number_received,y_number_received,"
			+ "m_amount_received,y_amount_received," + "notes,carrier_type,receive_paper_claim,"
			+ "estimate_insurance,provider_id_flag," + "patient_id_flag,patient_ssn_flag,"
			+ "trojan_id,secondary_calculation," + "secondary_provider_id,book_id,"
			+ "status,central_id,daily_number_submitted," + "daily_amount_submitted,daily_number_received,"
			+ "daily_amount_received,bill_standard_fee," + "do_not_track_yn,show_tax_on_ins_claim_yn,"
			+ "secondary_form_id,id_treating_dentist," + "trojan_mc,Managed_care,division_section_no,"
			+ "id_facility_by,adjustment_type ";

	public static final String PAYMENTPROVIDER_COLUMNS = "tran_num,provider_id,amount,practice_id,prod_provider_id ";

	public static final String PLANNEDSERVICE_COLUMNS = "appt_group,appt_id,completion_date,created_from_upgrade,"
			+ "date_planned,description,fee,lab_code,lab_code2,"
			+ "lab_fee,lab_fee2,line_number,old_tooth,patient_id,pre_fee,procedure_type_codes,"
			+ "provider_id,sequence,service_code,sort_order,"
			+ "standard_fee_id,status,status_date,surface,tooth,unusual_remarks ";

	public static final String PROVIDER_COLUMNS = "provider_id,first_name,last_name,address_1,"
			+ "address_2,city,state,zipcode,phone,sex,birth_date,"
			+ "hire_date,social_security,status,collections_go_to," + "provider_on_insurance,federal_tax_id,license,"
			+ "dea_regulation_number,medicaid,medicare,bcbs,delta,notes,mtd_wo_statements,"
			+ "ytd_wo_statements,mtd_charges,ytd_charges,mtd_collections," + "ytd_collections,calculate_productivity,"
			+ "position_id,anesthesia_id,specialty,password,location,medicaid_specialty,"
			+ "medicaid_locator,medicaid_group,association,current_bal,contract_balance,estimated_insurance,"
			+ "access_basic,access_accounting,access_productivity,mtd_new_patients,mtd_other_debits,ytd_other_debits,mtd_other_credits,ytd_other_credits,other_id,billing_entity,billing_entity_lic_no,"
			+ "use_practice_address,practice_id,bank_account,access_appointments,access_patients,access_contacts,access_provider,access_tx_plan,access_payment_plan,access_mass_updates,site_id,voice_id,email,access_prescriptions,operatory_access,access_medical,access_timeclock,access_timeclock_management,"
			+ "timesheet_password,other_id_2,other_id_3,other_id_4,other_id_5,access_lab,national_prov_id,Other_id_6,Other_id_7,Other_id_8,Other_id_9,Other_id_10,Other_id_11,Other_id_12,Other_id_13,Other_id_14,Other_id_15,Other_id_16,Other_id_17,Other_id_18,Other_id_19,Other_id_20,Other_id_21,daily_wo_statements,daily_charges,daily_collections,daily_new_patients,daily_other_debits,"
			+ "daily_other_credits,view_docs,add_docs,edit_docs,delete_docs,pass_prompt_1,pass_answer_1,pass_prompt_2,pass_answer_2,pass_prompt_3,pass_answer_3,tc_pass_prompt_1,tc_pass_answer_1,tc_pass_prompt_2,tc_pass_answer_2,tc_pass_prompt_3,tc_pass_answer_3,standard_fee_id,access_site,access_intellicare,security_profile,universal_id,provider_color,last_detail_id,ClinicianUserName,ClinicianPassword,last_logon,encrypted_social_security";

	public static final String TRANSACTIONS_COLUMNS = "tran_num,user_id,type,tran_date,patient_id,resp_party_id,amount,service_code,paytype_id,sequence,"
			+ "provider_id,collections_go_to,statement_num,old_tooth,surface,fee,discount_surcharge,tax,description,defective,impacts,status,adjustment_type,"
			+ "claim_id,est_primary,est_secondary,paid_primary,paid_secondary,provider_practice_id,patient_practice_id,bulk_payment_num,aging_date,tooth,"
			+ "lab_fee,lab_fee2,lab_code,lab_code2,pre_fee,standard_fee_id,practice_id,procedure_type_codes,balance";

	public static final String TRANSACTIONSDETAILS_COLUMNS = "detail_id,tran_num,user_id,date_entered,provider_id,collections_go_to,"
			+ "patient_id,amount,provider_practice_id,patient_practice_id,applied_to,status,status_modifier,posneg ";
	public static final String TREATEMENTPLANITEMS_COLUMNS = "treatment_plan_id,patient_id,line_number,est_primary,est_secondary,approved_by_insurance,"
			+ " submit_yn,claim_id,sort_order,discount,apply_discount";
	public static final String TREATEMENTPLANS_COLUMNS = "treatment_plan_id,patient_id,description,status,date_entered,user_id,"
			+ "date_last_updated,last_updated_by,notes";

	public static final String TRANSACTIONS_HEADER_COLUMNS = "tran_num,user_id,type,tran_date,resp_party_id," + 
			"amount,service_code,paytype_id,sequence,statement_num,surface,fee,discount_surcharge," + 
			"tax,description,defective,impacts,status,adjustment_type,claim_id,est_primary," + 
			"est_secondary,paid_primary,paid_secondary,bulk_payment_num,aging_date,tooth,lab_fee,lab_fee2,lab_code,lab_code2,pre_fee,standard_fee_id,practice_id,procedure_type_codes,balance";

	/*
	 * Query to check what ids are missing ES
	 */
	public static final String APPOINTMENT_COLUMNS_DELETE_CHECK = " a.appointment_id+1 AS st, MIN(b.appointment_id) - 1 AS en " + 
			"  FROM appointment  as a, appointment as  b" + 
			"  WHERE "+Constants.QUERY_WHERE_CLAUSE_REP +" and a.appointment_id < b.appointment_id GROUP BY a.appointment_id" + 
			"  HAVING st < MIN(b.appointment_id) ";
	
	
	public static final String TREATMENT_PLANS_COLUMNS_DELETE_CHECK = " a.treatment_plan_id+1 AS st, MIN(b.treatment_plan_id) - 1 AS en " + 
			"  FROM treatment_plans  as a, treatment_plans as  b" + 
			"  WHERE "+Constants.QUERY_WHERE_CLAUSE_REP +" and a.treatment_plan_id < b.treatment_plan_id GROUP BY a.treatment_plan_id" + 
			"  HAVING st < MIN(b.treatment_plan_id) ";
	
	
	//GOT THIS QUERY FROM VIEW OF EAGLE SOFT
	public static final String PAYMENTPROVIDER_QUERY=  "  transactions_detail.tran_num as tran_num, " +
			" transactions_detail.collections_go_to as provider_id, " +
			" SUM(-transactions_detail.amount) AS amount, " +
			" transactions_detail.provider_practice_id as practice_id, " +
			" transactions_detail.provider_id as prod_provider_id " +
			" FROM transactions_detail, " +
			" transactions_header " +
			" WHERE  "+ //+ Constants.QUERY_WHERE_CLAUSE_REP+//(transactions_header.tran_date between  '2000-01-01' and '2022-01-01' or   transactions_detail.date_entered between  '2000-01-01' and '2022-01-01')
			//" and" +
			" transactions_header.tran_num = transactions_detail.tran_num AND transactions_detail.status = 1 AND " +
			" 	((transactions_header.type IN ('A','P','I','Y','C','W') AND transactions_header.status IN ('A','D','V')) OR " +
			" 	(transactions_header.type = 'D' AND transactions_header.status = 'V')) "+
			" GROUP BY transactions_detail.tran_num, transactions_detail.collections_go_to, transactions_detail.provider_id, transactions_detail.provider_practice_id " +
			" ORDER BY transactions_detail.tran_num, transactions_detail.collections_go_to, transactions_detail.provider_id, transactions_detail.provider_practice_id " +
	        " "; 
   
	
	public static final SimpleDateFormat SimpleDateformatForEsQuery = new SimpleDateFormat("yyyy-MM-dd");

	public static final String CACHE_NAME_FOR_PROP = "__CACHE__";
	// Log messages
	public static final String START_PROCESS = "-Start Process-";
	public static final String END_PROCESS = "- Start Process-";
	public static final String START_PROCESS_TIME = "Start Time";
	public static final String END_PROCESS_TIME = "End Time";
	public static final String PROCESS_STATUS = "Process Status";
	public static final String PROCESS_EXCEPTION = "Process Exception";
	public static final String SECOND_TIME_TABLE_DATA_PULL = "Table updation Process";
	public static final String FIRST_TIME_TABLE_DATA_PULL = "Data pulled from table for first time";
	public static final String RECORDS_UPDATED_IN_TABLE = "Records updated in table";
	public static final String RECORDS_INSERTED_IN_TABLE = "New Count of records entered in table";
	public static final String RECORDS_UPDATED_IN_TABLE_CLOUD = "Records updated in table";
	public static final String ERROR_IN_PUSHING_TO_CLOUD= "Error in Pushing to cloud:";
	public static final String TABLE_DATA_DELETION_PROCESS = "Table records deletion Process";
	
	
	
	
	

}
