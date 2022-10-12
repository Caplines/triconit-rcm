package com.tricon.ruleengine.utils;

public class EagleSoftQuery {
	
	
	//treatment_plan_query claim_query treatment_plan_by_pat_query claim_by_pat_query
	public final static String  contstant_REP= " _REPLACE_ME ";
	
	public final static String  contstant_REP_DATE= " _RE_DATE_STRING_ME ";
	
	public final static String  contstant_REP_MONTH= " _RE_MONTH_STRING_ME ";
	
	public final static String  treatment_plan_query="select pla.appt_id as app_id,tp.treatment_plan_id as id,"
			+ "pat.patient_id as pat_id,pat.first_name as name, pat.last_name as last_name,pat.birth_date as dob,"
			+ "pla.date_planned as date_of_service,tp.status as status ,tpi.est_secondary as est_secondary,"
			+ " tp.description as description, tpi.line_number as line_item,"//11
			+ " pla.service_code as service_code,pla.description as description2,"
			+ " pla.surface as surface,pla.tooth as tooth,pla.status as status2,pla.fee as fee,"
			+ " pr.last_name as provider_last_name, tpi.est_primary as est_insurance,"
			+ " (pla.fee- tpi.est_primary ) as patient_portion,tpi.est_primary as est_primary,(pla.fee- tpi.est_secondary ) as patient_portion_sec"
			+ " from  treatment_plan_items tpi, treatment_plans tp,"
			+ " patient pat , planned_services pla,provider pr where "
			+ " tpi.treatment_plan_id in ("+contstant_REP+") and tpi.treatment_plan_id=tp.treatment_plan_id"
			+ " and tp.patient_id=pat.patient_id and  pla.line_number=tpi.line_number and pla.patient_id=pat.patient_id and pla.provider_id=pr.provider_id";


	
	public final static int  treatment_plan_query_CL_COUNT=22;

	public final static String  claim_query="select tp.sequence as app_id,tp.claim_id as id,"
			+ "pat.patient_id as pat_id,pat.first_name as name, pat.last_name as last_name,pat.birth_date as dob,"
			+ "tp.tran_date as date_of_service,tp.status as status ,tp.est_secondary as est_secondary,"
			+ " tp.description as description, tp.tran_num as line_item,"//11
			+ " tp.service_code as service_code,tp.description as description2,"
			+ " tp.surface as surface,tp.tooth as tooth,tp.status as status2,tp.fee as fee,"
			+ " pr.last_name as provider_last_name, tp.est_primary as est_insurance,"
			+ " (tp.fee- tp.est_primary ) as patient_portion,tp.est_primary as est_primary,(tp.fee- tp.est_secondary ) as patient_portion_sec"
			+ " from  transactions tp left outer join transactions_detail tpi on tpi.tran_num=tp.tran_num, insurance_claim cl, "
			+ " patient pat ,provider pr "
			+ " where "
			+ " tp.claim_id in ("+contstant_REP+") and cl.claim_id=tp.claim_id  "
			+ " and tp.patient_id=pat.patient_id and  pr.provider_id=tp.provider_id";


	
	public final static int  claim_query_CL_COUNT=22;

	private static String patient_query="select pat.patient_id as pat_id,pat.first_name as name,"
			+ " pat.last_name as last_name,pat.birth_date as birth_date,"
			+ " pat.social_security as social_security, pat.prim_member_id as prim_member_id,pat.status as status,"
			+ " pat.responsible_party_status as responsible_party_status,pat.responsible_party as responsibleparty,"
			+ " emp.maximum_coverage as maximum_coverage, pat.prim_benefits_remaining as prim_benefits_remaining,"
			+ " pat.prim_remaining_deductible as prim_remaining_deductible, pat.sec_benefits_remaining as sec_benefits_remaining,"
			+ " pat.sec_remaining_deductible as sec_remaining_deductible, emp.employer_id as employer_id,"
			+ " emp.name as employer_name, fs.fee_id as feescheduleid,"
			+ " fs.name as feeschedulename,cov.book_id as covbookheaderid,"
			+ " cov.name as covbookheadername,ins.name as insurancename,emp.group_number as employergroupnumber,pat.sec_member_id as sec_member_id "
			+ " from  patient pat , employer emp left outer join cov_book_header  cov on cov.book_id=emp.book_id Left outer join  fee_schedule  fs on fs.fee_id=emp.fee_schedule "
			+ " left outer join insurance_company ins on ins.insurance_company_id=emp.insurance_company_id "
			+ " where ";

	public final static String  patient_query_pri=patient_query 
			+ " pat.patient_id  in ("+contstant_REP+") and pat.prim_employer_id=emp.employer_id  ";

	public final static String  patient_query_sec=patient_query
			+" pat.patient_id  in ("+contstant_REP+") and pat.sec_employer_id=emp.employer_id  ";

	public final static int  patient_query_CL_COUNT=23;

	public final static String  employeemaster_query="select emp.employer_id as employerid,emp.name as employername,"
			+ " emp.group_number as employergroupnumber,emp.maximum_coverage as employermaximumcoverage,"
			+ " ser.service_type_id as servicetypeid,ser.description as servicetypedescription,ben.percentage  as percentage "
			+ ",ben.deductible_applies as deductibleapplies,ins.name as insurancename "
			+ "  from employer emp left outer join insurance_company ins on ins.insurance_company_id=emp.insurance_company_id, benefits ben ,service_type ser where emp.employer_id in ("+contstant_REP+") and ben.employer_id=emp.employer_id"
		    + " and ben.service_type_id=ser.service_type_id ";


	public final static int  employeemaster_query_CL_COUNT=9;

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
			+ " (pla.fee- tpi.est_primary ) as patient_portion,tpi.est_primary as est_primary,(pla.fee- tpi.est_secondary ) as patient_portion_sec"
			+ " from  treatment_plan_items tpi, treatment_plans tp,"
			+ " patient pat , planned_services pla,provider pr where "
			+ " tpi.patient_id in ("+contstant_REP+") and tpi.treatment_plan_id=tp.treatment_plan_id"
			+ " and tp.patient_id=pat.patient_id and  pla.line_number=tpi.line_number and pla.patient_id=pat.patient_id and pla.provider_id=pr.provider_id";


	
	public final static int  treatment_plan_by_pat_query_CL_COUNT=22;
	
	public final static String  claim_by_pat_query="select tp.sequence as app_id,tp.claim_id as id,"
			+ "pat.patient_id as pat_id,pat.first_name as name, pat.last_name as last_name,pat.birth_date as dob,"
			+ "tp.tran_date as date_of_service,tp.status as status ,tp.est_secondary as est_secondary,"
			+ " tp.description as description, tp.tran_num as line_item,"//11
			+ " tp.service_code as service_code,tp.description as description2,"
			+ " tp.surface as surface,tp.tooth as tooth,tp.status as status2,tp.fee as fee,"
			+ " pr.last_name as provider_last_name, tp.est_primary as est_insurance,"
			+ " (tp.fee- tp.est_primary ) as patient_portion,tp.est_primary as est_primary,(tp.fee- tp.est_secondary ) as patient_portion_sec"
			+ " from  transactions tp left outer join transactions_detail tpi on tpi.tran_num=tp.tran_num,"
			+ " patient pat ,provider pr where "
			+ " tp.patient_id in ("+contstant_REP+") "
			+ " and tp.patient_id=pat.patient_id and tp.provider_id=pr.provider_id";


	
	public final static int  claim_by_pat_query_CL_COUNT=22;
	
	//HERE 111 - YYYY/MM/DD
	public final static String patient_history_by_months="select wpat.statement_num as statement_num,  wpat.patient_id as  patient_id,"
			+ "tran.tran_date as tran_date, tran.service_code as service_code, tran.provider_id as provider_id, tran.old_tooth as old_tooth,"
			+ "tran.surface as surface, tran.fee as fee ,"
			+ "pr.first_name as provider_f,pr.last_name as provider_l"
			+ " from  walkout_patient wpat, transactions tran,"
			+ " provider pr where "
			+ " wpat.patient_id in ("+contstant_REP+") "
			+ " and wpat.patient_id=tran.patient_id and  tran.provider_id=pr.provider_id "
			+ " and birth_date between DATEADD( MONTH, "+contstant_REP_MONTH+", '"+contstant_REP_DATE+"' ) and convert(datetime, '"+contstant_REP_DATE+"', 111)";


	
	public final static int  patient_history_by_months_CL_COUNT=10;
	
	
	public final static String  perio_query="select top 1 p.patient_id,p.date_entered,p.provider_id,tooth_1,tooth_2," + 
			                      " tooth_3,tooth_4,tooth_5,tooth_6,tooth_7,tooth_8,tooth_9," + 
			                      " tooth_10,tooth_11,tooth_12,tooth_13,tooth_14,tooth_15," + 
			                      " tooth_16,tooth_17,tooth_18,tooth_19,tooth_20,tooth_21," + 
			                      " tooth_22,tooth_23,tooth_24,tooth_25,tooth_26,tooth_27," + 
	 		                      " tooth_28,tooth_29,tooth_30,tooth_31,tooth_32 from patperio p " + 
			                      " where patient_id in ("+contstant_REP+") order by date_entered desc ";


	
	public final static int  perio_query_CL_COUNT=35;
	
	private static String patient_insurance_query=" select DISTINCT p.patient_id,e.name,c.name,c.address_1,c.city,c.state,c.zipcode,c.phone1 from patient p, employer e, "
            + " insurance_claim ic, insurance_company c where ";

	
	public final static String  patient_insurance_pri_query=patient_insurance_query 
			                      + "  p.patient_id  in ("+contstant_REP+") and "
			                      + " p.prim_employer_id = e.employer_id and ic.prim_insurance_company_id = c.insurance_company_id and"  
			                      + " e.insurance_company_id = c.insurance_company_id  ";
	
	public final static String  patient_insurance_sec_query=patient_insurance_query
                                  + "  p.patient_id  in ("+contstant_REP+") and "
                                  + " p.sec_employer_id = e.employer_id and ic.prim_insurance_company_id = c.insurance_company_id and"  
                                  + " e.insurance_company_id = c.insurance_company_id  ";

	public final static int  patient_insurance_query_CL_COUNT=8;

	public final static String  preferance_fee_schedule_query="select p.patient_id,fs.fee_id,fs.name,p.fee_level_id from patient p, fee_schedule fs  "
             + " where  p.patient_id  in ("+contstant_REP+") and "
             + " and fs.fee_id = p.fee_level_id"; 
     
    public final static int  preferance_fee_schedule_query_CL_COUNT=3;
    
	//public final static String  policy_holder_schedule_query="select p.patient_id,amd.patient_first_name +' '+ amd.patient_last_name AS 'Policy Holder Name'"+
	//		" from patient p, account_merge_data amd where p.prim_responsible_id = amd.account_patient_id and p.patient_id in ("+contstant_REP+")";
    public final static String  policy_holder_schedule_query="select patient_id, prim_policy_holder, sec_policy_holder,relation_to_prim_policy_holder from patient_letter"
    		+                 " where patient_id  in ("+contstant_REP+")";
    public final static String  policy_holder_schedule_query_pr="select p.patient_id,string(p.first_name,' ',p.last_name) as Policy_Holder_Name from patient p where p.patient_id = "
    		+ "              (Select top 1 i.prim_responsible_id  from insurance_claim i  where i.patient_id in ("+contstant_REP+"))";
    public final static String  policy_holder_schedule_query_sec="select p.patient_id,string(p.first_name,' ',p.last_name) as Policy_Holder_Name from patient p where p.patient_id ="
    		+ "              (Select top 1 i.sec_responsible_id  from insurance_claim i  where i.patient_id in ("+contstant_REP+"))";
    public final static int  policy_holder_schedule_query_CL_COUNT=4;


}
