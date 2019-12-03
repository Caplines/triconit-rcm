package com.tricon.ruleengine.dao.impl;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import com.tricon.ruleengine.dao.PatientDao;
import com.tricon.ruleengine.dto.CaplineIVFFormDto;
import com.tricon.ruleengine.dto.CaplineIVFQueryFormDto;
import com.tricon.ruleengine.logger.RuleEngineLogger;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.Patient;
import com.tricon.ruleengine.model.db.PatientDetail;
import com.tricon.ruleengine.model.db.PatientHistory;
import com.tricon.ruleengine.model.db.User;
import com.tricon.ruleengine.utils.Constants;

@Repository
public class PatientDaoImpl extends BaseDaoImpl implements PatientDao {

	
	static Class<?> clazz = PatientDaoImpl.class;
	
	@Override
	public Patient checkforPatientWithIdAndOffice(String patientid, Office off,Patient patH) {

		Session session = getSession();
		Patient pat = null;
		try {
			Criteria criteria = session.createCriteria(Patient.class);
			criteria.add(Restrictions.eq("patientId", patientid));
			criteria.createAlias("office", "off");
			criteria.createAlias("patientHistory", "patientHistory", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("patientDetails", "patientDetails", JoinType.LEFT_OUTER_JOIN);
			criteria.add(Restrictions.eq("off.uuid", off.getUuid()));
			
			/*if (patH.getPatientDetails() != null && patH.getPatientDetails().size() > 0
					&& patH.getPatientDetails() != null && patH.getPatientDetails().size() > 0) {
				Iterator<PatientDetail> iter = patH.getPatientDetails().iterator();
				PatientDetail patO = iter.next();
			
			criteria.add(Restrictions.eq("patientDetails", patO.getGeneralDateIVwasDone()));
			}
            */
			pat = (Patient) criteria.uniqueResult();
		} finally {
			closeSession(session);
		}
		return pat;
	}

	@Override
	public PatientHistory getPatientHistory(String patientid, Office off) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PatientDetail getPatientDetails(String patientid, Office off) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Patient updateOnlyPatient(Patient pat,Office off, User user)  throws Exception{
	updateEntity(pat);
      return pat;		
	}
	
	@Override
	public Patient savePatientDataWithDetailsAndHistory(Patient pat, Office off, User user, Date date) throws Exception{
		Session session = getSession();
		try {

			// Transaction transaction = session.beginTransaction();
			pat.setCreatedBy(user);
			pat.setId(((Integer) session.save(pat)));
			// Patient Detail Start
			Iterator<PatientDetail> iter = pat.getPatientDetails().iterator();
			PatientDetail pd = iter.next();
			pd.setCreatedBy(user);
			session.save(pd);
			// Patient Detail End
			// History start
			for (PatientHistory phi : pat.getPatientHistory()) {
				phi.setOffice(off);
				phi.setPatient(pat);
				//phi.setPd(pd);
				session.save(phi);
			}
			// transaction.commit();
		} finally {
			closeSession(session);

		}
		return pat;
	}

	@Override
	public Patient updatePatientDataWithDetailsAndHistory(Patient pat, Office off, User user,boolean detailsSave) throws Exception {
		// TODO Auto-generated method stub
		//Session session = getSession();
		try {
			//Transaction transaction = session.beginTransaction();
			// Break into 3 methods
			updateEntity(pat);
			RuleEngineLogger.generateLogs(clazz, "Entering To Save patient from DUMP.."
					+pat.getPatientId(), Constants.rule_log_debug, null);
			
			Iterator<PatientDetail> iter = pat.getPatientDetails().iterator();
			PatientDetail pd = iter.next();
			// String x=null;
			if (detailsSave) pd= (PatientDetail) saveEntiy(pd);
			else updateEntity(pd);
			for (PatientHistory ph : pat.getPatientHistory()) {
				ph.setPatient(pat);
				ph.setOffice(off);
				//ph.setPd(pd);
				saveEntiy(ph);
			}
			// x.split("");
			//transaction.commit();
		} finally {
			//closeSession(session);
		}
		// session.flush();
		// save data in patient table

		// update or save data in Patient Details table

		// update history data
		// pat.setId(0);
		/*
		 * Session session = getSession(); Set<PatientHistory>
		 * newPH=pat.getPatientHistory(); Set<PatientDetail> newPD=
		 * pat.getPatientDetails(); try {
		 * 
		 * Transaction transaction = session.beginTransaction(); Criteria criteria =
		 * session.createCriteria(Patient.class);
		 * criteria.add(Restrictions.eq("patientId", pat.getPatientId()));
		 * criteria.createAlias("office", "off"); criteria.createAlias("patientHistory",
		 * "patientHistory",JoinType.LEFT_OUTER_JOIN);
		 * criteria.createAlias("patientDetails",
		 * "patientDetails",JoinType.LEFT_OUTER_JOIN);
		 * criteria.add(Restrictions.eq("off.uuid", off.getUuid()));
		 * 
		 * Object obj=criteria.uniqueResult(); if (obj!=null) { //update record pat
		 * =(Patient) obj; pat.setUpdatedBy(user); pat.setUpdatedDate(date);
		 * session.update(pat); update=true; session.flush(); if
		 * (pat.getPatientDetails()!=null && pat.getPatientDetails().size()>0) {
		 * Iterator<PatientDetail> iter = pat.getPatientDetails().iterator();
		 * PatientDetail pd = iter.next(); session.evict(pd); } }else {
		 * 
		 * //save data.. pat.setCreatedBy(user);
		 * pat.setId(((Integer)session.save(pat))); } //Patient Detail Start if (update)
		 * { Iterator<PatientDetail> iter = pat.getPatientDetails().iterator();
		 * PatientDetail pd = iter.next(); Iterator<PatientDetail> iterD =
		 * newPD.iterator(); PatientDetail pdN = iterD.next(); if
		 * (!pd.getGeneralDateIVwasDone().equals(pd.getGeneralDateIVwasDone())) { //save
		 * pdN.setPatient(pat); session.save(pdN); }else { //update pdN.setPatient(pat);
		 * pdN.setId(pd.getId()); pdN.setUpdatedBy(user); pdN.setUpdatedDate(date);
		 * session.update(pdN); session.flush(); } }else { Iterator<PatientDetail> iter
		 * = newPD.iterator(); PatientDetail pd = iter.next(); pd.setPatient(pat);
		 * session.save(pd);
		 * 
		 * }
		 * 
		 * //Patient Detail End
		 * 
		 * //History start Set<PatientHistory> phl =pat.getPatientHistory(); if
		 * (newPH!=null && newPH.size()>0) { if (phl==null) phl= new HashSet<>();
		 * //means history already there so now only insert new record if available
		 * Set<PatientHistory> result1=new HashSet<>(); boolean added=false;
		 * for(PatientHistory n:newPH) { added=true; for(PatientHistory o:phl) { if (
		 * o.getHistoryCode().equals(n.getHistoryCode()) &&
		 * o.getHistoryTooth().equals(n.getHistoryTooth()) &&
		 * o.getHistorySurface().equals(n.getHistorySurface()) &&
		 * o.getHistoryDOS().equals(n.getHistoryDOS()) ){ added=false; } } if(added)
		 * result1.add(n); }//for for(PatientHistory phi:result1) { phi.setOffice(off);
		 * phi.setPatient(pat); session.save(phi); } }else { //insert all data
		 * for(PatientHistory phi:pat.getPatientHistory()) { phi.setOffice(off);
		 * phi.setPatient(pat); session.save(phi); session.flush();
		 * 
		 * }
		 * 
		 * } //History end transaction.commit(); } finally { closeSession(session);
		 * 
		 * }
		 */
		// return Optional.ofNullable((List<OfficeDto>) offices);

		return pat;
	}

	
	@Override
	public List<Patient> searchPatientByPatientId(Set<String> patientIds, Office off) {
		Session session = getSession();
		List<Patient> patL=null;
		try {
			
			Criteria criteria = session.createCriteria(Patient.class);
			criteria.add(Restrictions.in("patientId", patientIds));
			criteria.createAlias("office", "off");
			patL = (List<Patient>) criteria.list();
			
		} finally {
			closeSession(session);
		}
		return patL;
	}
	
	@Override
	public List<CaplineIVFFormDto> searchPatientDetailFromIVF(CaplineIVFQueryFormDto dto, Office off,Set<String> patIds) {
		Session session = getSession();
		List<CaplineIVFFormDto> cL=null;
		try {
			/*
			Criteria criteria = session.createCriteria(Patient.class);
			if (!dto.getPatientIdDB().equals(""))criteria.add(Restrictions.eq("patientId", dto.getPatientIdDB()));
			criteria.createAlias("office", "off");
			criteria.createAlias("patientHistory", "patientHistory", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("patientDetails", "patientDetails", JoinType.LEFT_OUTER_JOIN);
			criteria.add(Restrictions.eq("off.uuid", off.getUuid()));
			if (!dto.getEmployerNameDB().equals(""))
				criteria.add(Restrictions.eq("patientDetails.employerName", dto.getEmployerNameDB()).ignoreCase());
			if (!dto.getGeneralDateIVFDoneDB().equals(""))
				criteria.add(Restrictions.eq("patientDetails.generalDateIVwasDone", dto.getGeneralDateIVFDoneDB()));
			

			patL = (List<Patient>) criteria.list();
			
			*/
			////policy18//policy19/policy20 -- need to verify
			String inclause=(dto!=null && dto.getPatientIdDB()!=null && !dto.getPatientIdDB().equals("")? " and p.patient_id in ('"+dto.getPatientIdDB()+"') ":" ");
			if (patIds!=null) {
				inclause= " and p.patient_id in ("+String.join(", ", patIds)+")";
			}
			
			String query = "select p.id as pidDB,pd.id as id,policy_holder as basicInfo5,p.patient_id as basicInfo21,"
					+ " concat(coalesce(p.first_name,''),' ',coalesce(p.last_name,'')) as basicInfo2, p.dob as basicInfo6, "
					+ " ins_name as basicInfo3 , tax_id as basicInfo4, ins_contact as basicInfo7, "
					+ " cs_sr_name as basicInfo8, policy_holder_dob as basicInfo9 , employer_name as basicInfo10 ,"
					+ " const_tx_recall_np as basicInfo11 , ref as basicInfo12 , member_ssn as basicInfo13,"
					+ " group_p as basicInfo14, cob_status as basicInfo15,memberId as basicInfo16,apt_date as  basicInfo17, "
					+ " payer_id as basicInfo18, provider_name as basicInfo19, ins_address as basicInfo20,"
					+ " plan_type as policy1, cra_required as policy17,plan_termed_date as policy2, plan_network as policy3, "
					+ " plan_fee_schedule_name as policy4, plan_effective_date as policy5, plan_calendar_fiscal_year as policy6,"
					+ " plan_annual_max as policy7,plan_annual_max_remaining as policy8,plan_individual_dedudtible as policy9,"
					+ " plan_individual_deductible_remaining as policy10,plan_dependents_covered_to_age as policy11,plan_pre_d_mandatory as policy12,"
					+ " plan_non_duplicate_clause as policy13,plan_full_time_student_status as policy14,plan_assignment_of_benefits as policy15,"
					+ " plan_coverage_book as policy16,basic_percentage as percentages1,basic_subject_deductible as percentages2,"
					+ " major_percentage as percentages3,major_subject_deductible as percentages4,endo_dontics_percentage as percentages5,"
					+ " endo_subjectdeductible as percentages6,perio_surgrey_percentage as percentages7,perio_surgery_subject_deductible as percentages8,"
					+ " preventive_percentage as percentages9,diagnostic_percentage as percentages10,pa_xrays_percentage as percentages11,"
					+ " claim_filling_limit as percentages12,missingtooth_clause as prosthetics1,replacementclause as prosthetics2,"
					+ " crowns_d2750D2740_pays_prep_seat_date as prosthetics3,night_guards_d9940fl as prosthetics4,"
					+ " basic_waiting_period as waitingPeriod1,major_waiting_period as waitingPeriod2,sscd2930fl as ssc1,sscd2931fl as ssc2,"
					+ " exam_d0120_fl as exams1,exams_d0140_fl as exams2,eexams_d0145_fl as exams3,exams_d0150_fl as exams4,x_rays_bw_sfl as xrays1,"
					+ " x_rays_pad0220_fl as xrays2,x_rays_pad0230_fl as xrays3,x_rays_fm_xfl as xrays4,x_rays_bundling as xrays5,"
					+ " flouride_d1208_fl as fluroide1,flouride_age_limit as fluroide2,varnish_d1206_fl as fluroide3,varnish_d1206age_limit as fluroide4,"
					+ " sealants_d1351_percentage as sealantsD,sealants_d1351_fl as sealants1,sealants_d1351_age_limit as sealants2,"
					+ " sealants_d1351_primary_molars_covered as sealants3,sealants_d1351_pre_molars_covered as sealants4,"
					+ " sealants_d1351_permanent_molars_covered as sealants5,prophy_d1110_fl as prophy1,prophy_d1120_fl as prophy2,"
					+ " name1201110_roll_over_age as rollage,s_rpd4341_percentage as perio1,s_rpd4341_fl as perio2,s_rpd4341_quads_per_day as perio3,"
					+ " s_rpd4341_days_bw_treatment as perio4,perio_maintenance_d4910_percentage as perioMnt1,perio_maintenance_d4910_fl as perioMnt2,"
					+ " perio_maintenance_d4910_altw_prophy_d0110 as perioMnt3,fillings_bundling as fillings,fmdd4355_percentage as perioMnt4,"
					+ " fmdd4355_fl as perioMnt5,gingivitis_d4346_percentage as perioMnt6,gingivitis_d4346_fl as perioMnt7,nitrous_d9230_percentage as sedations1,"
					+ " iv_sedation_d9243_percentage as sedations2,iv_sededation_d9248_percentage as sedations3,extractions_minor_percentage as extractions1,"
					+ " extractions_major_percentage as extractions2,crown_length_d4249_percentage as oral1,crown_length_d4249_fl as oral2,"
					+ " alveo_d7311_covered_with_ext as oral3,alveo_d7311_fl as oral4,alveoD7310Covered_with_ext as oral5,alveo_d7310fl as oral6,"
					+ " complete_dentures_d5110_d5120_fl as dentures1,immediate_dentures_d5130_d5140_fl as dentures2,partial_dentures_d5213_d5214_fl as dentures3,"
					+ " interim_partial_dentures_d5214_fl as dentures4,bone_grafts_d7953_covered_with_ext as dentures5,bone_grafts_d7953_fl as dentures6,"
					+ " implant_coverage_d6010_percentage as implants1,implant_coverage_d6057_percentage as implants2,implant_coverage_d6190_percentage as implants3,"
					+ " implant_supported_porc_ceramic_d606_percentage as implants4,post_composites_d2391_percentage as posterior1,"
					+ " post_composites_d2391_fl as posterior2,posterior_composites_d2391_downgrade as posterior3,crowns_d2750_d2740_percentage as posterior4,"
					+ " crowns_d2750_d2740_fl as posterior5,crowns_d2750_d2740_downgrade as posterior6,night_guards_d9940_percentage as posterior7,"
					+ " d9310_percentage as posterior8,d9310_fl as posterior9,buildups_d2950_covered as posterior10,buildups_d2950_fl as posterior11,"
					+ " buildups_d2950_same_day_crown as posterior12,orthoPercentage as ortho1,ortho_max as ortho2 ,ortho_age_limit as ortho3,"
					+ " ortho_subject_deductible as ortho4,general_date_iv_wasdone as date,comments as comments,general_benefits_verified_by as benefits,d0120 as policy18,d2391 as policy19 "
					+ " from patient_detail pd , patient p where "
					+ " pd.office_id='"+off.getUuid()+"'  and pd.patient_id=p.id " 
					//(!dto.getPatientIdDB().equals("")? " and p.patient_id in ('"+dto.getPatientIdDB()+"') ":" ")+
					+ inclause +
					(dto!=null && dto.getUniqueID()!=null && !dto.getUniqueID().equals("")? " and pd.id ="+dto.getUniqueID()+" ":" ")+
					(dto!=null && dto.getUniqueIDs()!=null && dto.getUniqueIDs().size()>0? " and pd.id in ("+String.join(", ", dto.getUniqueIDs())+") " :"  " )+
					(dto!=null && dto.getEmployerNameDB()!=null && !dto.getEmployerNameDB().equals("")? " and pd.employer_name like '%"+dto.getEmployerNameDB()+"%' ":" ")+
					(dto!=null && dto.getPolicyHolderDobDB()!=null && !dto.getPolicyHolderDobDB().equals("")? " and pd.policy_holder_dob like '%"+dto.getPolicyHolderDobDB()+"%' ":" ")+
					(dto!=null && dto.getPolicyHolderNameDB()!=null && !dto.getPolicyHolderNameDB().equals("")? " and pd.policy_holder like '%"+dto.getPolicyHolderNameDB()+"%' ":" ")+
					(dto!=null && dto.getPatientDobDB()!=null && !dto.getPatientDobDB().equals("")? " and p.dob = '"+dto.getPatientDobDB()+"' ":" ")+
					(dto!=null && dto.getPatientName()!=null && !dto.getPatientName().equals("")? " and (concat(coalesce(first_name,''),' ',coalesce(last_name,'')) like '%"+dto.getPatientName()+"%')"+" ":" ")+
					(dto!=null && dto.getGeneralDateIVFDoneDB()!=null && !dto.getGeneralDateIVFDoneDB().equals("")? " and pd.general_date_iv_wasdone ='"+dto.getGeneralDateIVFDoneDB()+"' ":" ")
					//patientDobDB
					;
          cL=session.createSQLQuery(query).setResultTransformer(Transformers.aliasToBean(CaplineIVFFormDto.class)). list();
		} finally {
			closeSession(session);
		}
		return cL;
	}

	@Override
	public List<Object> searchPatientDetailFromIVFGivenColumns(CaplineIVFQueryFormDto dto, Office off) {
		Session session = getSession();
		List<Object> cL=null;
		try {
			String inclause=(dto!=null && dto.getPatientIdDB()!=null && !dto.getPatientIdDB().equals("")? " and p.patient_id in ('"+dto.getPatientIdDB()+"') ":" ");
			if (dto.getPatientIdDB()!=null) {
				inclause= " and p.patient_id ='"+dto.getPatientIdDB()+"' ";
			}
			
			String query = "select '"+dto.getOfficeNameDB()+"'," +dto.getColumns()+" from patient_detail pd , patient p where "
					+ " pd.office_id='"+off.getUuid()+"'  and pd.patient_id=p.id " 
					+ inclause +
					(dto!=null && dto.getUniqueID()!=null && !dto.getUniqueID().equals("")? " and pd.id ="+dto.getUniqueID()+" ":" ")+
					(dto!=null && dto.getEmployerNameDB()!=null && !dto.getEmployerNameDB().equals("")? " and pd.employer_name like '%"+dto.getEmployerNameDB()+"%' ":" ")+
					(dto!=null && dto.getPatientName()!=null && !dto.getPatientName().equals("")? " and (concat(coalesce(first_name,''),' ',coalesce(last_name,'')) like '%"+dto.getPatientName()+"%')"+" ":" ")+
					(dto!=null && dto.getGeneralDateIVFDoneDB()!=null && !dto.getGeneralDateIVFDoneDB().equals("")? " and pd.general_date_iv_wasdone ='"+dto.getGeneralDateIVFDoneDB()+"' ":" ")
					
					;
          cL=session.createSQLQuery(query).list();
		} finally {
			closeSession(session);
		}
		return cL;
	}
	@Override
	public List<PatientHistory> searchPatientHistoryForPatient(Set<String> patientIds, Office off,int patDid) {
		Session session = getSession();
		List<PatientHistory> patH=null;
		try {
			/*
			Criteria criteria = session.createCriteria(PatientHistory.class);
			criteria.createAlias("patient", "patient");
			criteria.add(Restrictions.in("patient.patientId", patientIds));
			//criteria.createAlias("pd", "pd");
			//criteria.add(Restrictions.in("pd.id", patDid));
			criteria.createAlias("office", "off");
			patH = (List<PatientHistory>) criteria.list();
			*/
			String query =" select p.patient_id as pid,ph.history_code as historyCode, ph.history_tooth as historyTooth," + 
					" ph.history_surface as historySurface, ph.history_dos as historyDOS," + 
					" ph.id as id from Patient_history ph,office o,patient p where p.id=ph.patient_id and o.uuid=ph.office_id and"+
					" ph.patient_id in ( "+String.join(", ", patientIds)+") and o.uuid='"+
					off.getUuid()+"' order by  STR_TO_DATE(history_dos, '%Y-%m-%d') desc";
			
			patH=session.createSQLQuery(query).setResultTransformer(Transformers.aliasToBean(PatientHistory.class)). list();
			
			
		} finally {
			closeSession(session);
		}
		return patH;
	}
}
