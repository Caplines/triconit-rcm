package com.tricon.ruleengine.utils;

public class EagleSoftQuery {
	
	
	
	public final static String  contstant_REP= " _REPLACE_ME ";
	
	public final static String  treatment_plan_query="select pla.appt_id as app_id,tp.treatment_plan_id as id,"
			+ "pat.patient_id as pat_id,pat.first_name as name, pat.last_name as last_name,pat.birth_date as dob,"
			+ "pla.date_planned as date_of_service,tp.status as status ,tpi.est_secondary as est_secondary,"
			+ " tp.description as description, tpi.line_number as line_item,"//11
			+ " pla.service_code as service_code,pla.description as description2,"
			+ " pla.surface as surface,pla.tooth as tooth,pla.status as status2,pla.fee as fee,"
			+ " pr.last_name as provider_last_name, tpi.est_primary as est_insurance,"
			+ " (pla.fee- tpi.est_primary ) as patient_portion,tpi.est_primary as est_primary"
			+ " from  treatment_plan_items tpi, treatment_plans tp,"
			+ " patient pat , planned_services pla,provider pr where "
			+ " tpi.treatment_plan_id in ("+contstant_REP+") and tpi.treatment_plan_id=tp.treatment_plan_id"
			+ " and tp.patient_id=pat.patient_id and  pla.line_number=tpi.line_number and pla.patient_id=pat.patient_id and pla.provider_id=pr.provider_id";


	
	public final static int  treatment_plan_query_CL_COUNT=21;

	public final static String  patient_query="select pat.patient_id as pat_id,pat.first_name as name,"
			+ " pat.last_name as last_name,pat.birth_date as birth_date,"
			+ " pat.social_security as social_security, pat.prim_member_id as prim_member_id,pat.status as status,"
			+ " pat.responsible_party_status as responsible_party_status,pat.responsible_party as responsibleparty,"
			+ " emp.maximum_coverage as maximum_coverage, pat.prim_benefits_remaining as prim_benefits_remaining,"
			+ " pat.prim_remaining_deductible as prim_remaining_deductible, pat.sec_benefits_remaining as sec_benefits_remaining,"
			+ " pat.sec_remaining_deductible as sec_remaining_deductible, emp.employer_id as employer_id,"
			+ " emp.name as employer_name, fs.fee_id as feescheduleid,"
			+ " fs.name as feeschedulename,cov.book_id as covbookheaderid,"
			+ " cov.name as covbookheadername"
			+ " from  patient pat , employer emp left outer join cov_book_header  cov on cov.book_id=emp.book_id Left outer join  fee_schedule  fs on fs.fee_id=emp.fee_schedule  where "
			+ " pat.patient_id  in ("+contstant_REP+") and pat.prim_employer_id=emp.employer_id  ";

	public final static int  patient_query_CL_COUNT=20;

	public final static String  employeemaster_query="select emp.employer_id as employerid,emp.name as employername,"
			+ " emp.group_number as employergroupnumber,emp.maximum_coverage as employermaximumcoverage,"
			+ " ser.service_type_id as servicetypeid,ser.description as servicetypedescription,ben.percentage  as percentage "
			+ ",ben.deductible_applies as deductibleapplies  "
			+ "  from employer emp , benefits ben ,service_type ser where emp.employer_id in ("+contstant_REP+") and ben.employer_id=emp.employer_id"
		    + " and ben.service_type_id=ser.service_type_id ";


	public final static int  employeemaster_query_CL_COUNT=8;

	public final static String  feeShedule_query=" select fe.fee_id as feeid, fs.name as name,"
			+ " fe.service_code as feeservicecode , fe.fee  as feesfee from "
			+ " fees fe,fee_schedule fs where fs.fee_id in ("+contstant_REP+") and fs.fee_id=fe.fee_id ";
	
	public final static int  feeShedule_query_CL_COUNT=4;

	
	public final static String  treatment_plan_by_pat_query="select pla.appt_id as app_id,tp.treatment_plan_id as id,"
			+ "pat.patient_id as pat_id,pat.first_name as name, pat.last_name as last_name,pat.birth_date as dob,"
			+ "pla.date_planned as date_of_service,tp.status as status ,tpi.est_secondary as est_secondary,"
			+ " tp.description as description, tpi.line_number as line_item,"//11
			+ " pla.service_code as service_code,pla.description as description2,"
			+ " pla.surface as surface,pla.tooth as tooth,pla.status as status2,pla.fee as fee,"
			+ " pr.last_name as provider_last_name, tpi.est_primary as est_insurance,"
			+ " (pla.fee- tpi.est_primary ) as patient_portion,tpi.est_primary as est_primary"
			+ " from  treatment_plan_items tpi, treatment_plans tp,"
			+ " patient pat , planned_services pla,provider pr where "
			+ " tpi.patient_id in ("+contstant_REP+") and tpi.treatment_plan_id=tp.treatment_plan_id"
			+ " and tp.patient_id=pat.patient_id and  pla.line_number=tpi.line_number and pla.patient_id=pat.patient_id and pla.provider_id=pr.provider_id";


	
	public final static int  treatment_plan_by_pat_query_CL_COUNT=21;
}
