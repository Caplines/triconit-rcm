package com.tricon.ruleengine.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tricon.ruleengine.dao.PatientDao;
import com.tricon.ruleengine.dto.CaplineDataReplicationDto;
import com.tricon.ruleengine.dto.CaplineIVFFormDto;
import com.tricon.ruleengine.dto.CaplineIVFQueryFormDto;
import com.tricon.ruleengine.dto.OfficeDto;
import com.tricon.ruleengine.dto.scrapping.ScrapPatient;
import com.tricon.ruleengine.logger.RuleEngineLogger;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.Patient;
import com.tricon.ruleengine.model.db.PatientDetail;
import com.tricon.ruleengine.model.db.PatientDetail2;
import com.tricon.ruleengine.model.db.PatientDetailTemp;
import com.tricon.ruleengine.model.db.PatientDetailTemp2;
import com.tricon.ruleengine.model.db.PatientHistory;
import com.tricon.ruleengine.model.db.PatientHistoryTemp;
import com.tricon.ruleengine.model.db.PatientTemp;
import com.tricon.ruleengine.model.db.ReplicationDays;
import com.tricon.ruleengine.model.db.User;
import com.tricon.ruleengine.utils.Constants;
import com.tricon.ruleengine.utils.DaysCalculation;

@Repository
public class PatientDaoImpl extends BaseDaoImpl implements PatientDao {
	
	static Class<?> clazz = PatientDaoImpl.class;
	
	static String queryPatAll=" iv_form_type_id as ivFormTypeId, COALESCE(pd.id, 0) as id,policy_holder as basicInfo5,p.patient_id as basicInfo21,"
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
			+ " ortho_subject_deductible as ortho4,general_date_iv_wasdone as date,comments as comments,general_benefits_verified_by as benefits,d0120 as policy18,d2391 as policy19, "
			+ " preventive_sub_ded as percentages13,diagnostic_sub_ded as percentages14,pa_xrays_sub_ded as percentages15, " //add new Columns here
		    + " den_5225_per as den5225,den_f_5225_fr as denf5225,den_5226_per as den5226, den_f_5226_fr as denf5226, " //add new Columns here
			+ " bridges1 as bridges1,bridges2 as bridges2,will_downgrade_applicable as cdowngrade, " //add new Columns here
			+ " implants_fr_d6010 as implants5,implants_fr_d6057 as implants6,implants_fr_d6065 as implants7,implants_fr_d6190 as implants8, " //add new Columns here
			+ " crown_grade_code as posterior17,fmx_per as percentages16, " //add new Columns here will_crown_grade as posterior16
			+ " ortho_remaining as ortho5,ortho_waiting_period as waitingPeriod3, night_guards_d9945_percentage as posterior18, " //add new Columns here
			+ " night_guards_d9944_fr as posterior19,night_guards_d9945_fr as posterior20,fillings_in_year as fill1," //add new Columns here
			+ " extractions_in_year as extr1, crowns_in_year as crn1,waiting_period4 as waitingPeriod4,share_fr as shareFr, DATE_FORMAT(p.created_date, '%d/%m/%y %T') as createdDate, " ////add new Columns here
			+ " pedo1 as pedo1, pedo2 as pedo2,pano1 as pano1, pano2 as pano2, d4381 as d4381,"
			+ " ckd0120 as ckD0120,ckd0140 as ckD0140,ckd0145 as ckD0145,ckd0150 as ckD0150,ckd0160 as ckD0160,ckd210 as ckD210,"
			+ " ckd220 as ckD220,ckd230 as ckD230,ckd330 as ckD330,ckd274 as ckD274,  "
			+ " d0160_freq as d0160Freq,d2391_freq as d2391Freq,d0330_freq as d0330Freq,d4381_freq as d4381Freq,  "
			+ " pd.d3330 as d3330,d3330_freq as d3330Freq,licence as licence,npi as npi,freq_d2934 as  freqD2934, "
			+ " radio3 as radio3,radio4 as radio4,radio5 as radio5, radio1 as radio1,radio2 as radio2,  "
			+ " corrd_Of_benefits as corrdOfBenefits,what_amount_d7210 as whatAmountD7210,allow_amount_d7240 as allowAmountD7240,d7210 as D7210,d7220 as D7220,d7230 as D7230,d7240 as D7240,"
			+ " d7250 as d7250,d7310 as d7310,d7311 as d7311,"
			+ " d7320 as d7320,d7321 as d7321,d7473 as d7473,d9239 as d9239,"
			+ " d4263 as d4263,d4264 as d4264,d6104 as d6104,d7953 as d7953,"
			+ " d3310 as d3310,d3320 as d3320,pd2.d3330 as D33300,d3346 as d3346,"
			+ " d3347 as d3347,d3348 as d3348,d6058 as d6058,d7951 as d7951,"
			+ " d4266 as d4266,d4267 as d4267,d4273 as d4273,"
			+ " d7251 as d7251,ivSedation as ivSedation, "
			+ " d7210fr as d7210fr,d7220fr as d7220fr,d7230fr as d7230fr,d7240fr as d7240fr,"
			+ " d7250fr as d7250fr,d7310fr as d7310fr,d7311fr as d7311fr,d7320fr as d7320fr,"
			+ " d7321fr as d7321fr,d7473fr as d7473fr,sedations1fr as sedations1fr,sedations3fr as sedations3fr,d9239fr as d9239fr,sedations2fr as sedations2fr,"
			+ " d4263fr as d4263fr,d4264fr as d4264fr,d6104fr as d6104fr,d7953fr as d7953fr,"
			+ " d3310fr as d3310fr,d3320fr as d3320fr,d3346fr as d3346fr,d3347fr as d3347fr,"
			+ " d3348fr as d3348fr,d6058fr as d6058fr,oral1fr as oral1fr,d7951fr as d7951fr,"
			+ " d4266fr as d4266fr,d4267fr as d4267fr,perio1fr as perio1fr,d4273fr as d4273fr,d7251fr as d7251fr, "
			+ " d7472 as d7472, d7472fr as d7472fr,d7280 as d7280,d7280fr as d7280fr,d7282 as d7282,d7282fr as d7282fr,"
			+ " d7283 as d7283,d7283fr as d7283fr,d7952 as d7952,d7952fr as d7952fr,d7285 as d7285,d7285fr as d7285fr,"
			+ " d6114 as d6114,d6114fr as d6114fr,d5860 as d5860,d5860fr as d5860fr, d5110 as d5110,d5110fr as d5110fr,"
			+ " d5130 as d5130,d5130fr as d5130fr,d0140 as d0140,s_remarks as sRemarks,m_policy as mPolicy ,m_mip as mMIP,es_bcbs as esBcbs,obtain_mpn as obtainMPN,waiting_period_duration as waitingPeriodDuration, "
			+ " d0350 as d0350,d1330 as d1330,d2930 as d2930,srpd_4341 as srpd4341,major_d72101 as majord72101,"
			+ " fmx_subject_to_ded as fmxSubjectToDed,d1510 as d1510,d1510_freq as d1510Freq,d1516 as d1516,d1516_freq as d1516Freq,"
			+ " d1517 as d1517,d1517_freq as d1517Freq,d3220 as d3220,d3220_freq as d3220Freq,out_network_message as outNetworkMessage,"
			+ " os_plan_type as osPlanType,sm_age_limit as smAgeLimit,perio_d4921 as perioD4921, d4921_frequency as d4921Frequency,perio_d4266 as perioD4266,d4266_frequency as d4266Frequency,"
			+ " perio_d9910 as perioD9910, d9910_frequency as d9910Frequency ";
	
	@Override
	public Patient checkforPatientWithId(String patientid, Office off) {

		Session session = getSession();
		Patient pat = null;
		try {
			Criteria criteria = session.createCriteria(Patient.class);
			criteria.add(Restrictions.eq("patientId", patientid));
			criteria.createAlias("office", "off");
			criteria.add(Restrictions.eq("off.uuid", off.getUuid()));
			/*if (patH.getPatientDetails() != null && patH.getPatientDetails().size() > 0
					&& patH.getPatientDetails() != null && patH.getPatientDetails().size() > 0) {
				Iterator<PatientDetail> iter = patH.getPatientDetails().iterator();
				PatientDetail patO = iter.next();
			
			criteria.add(Restrictions.eq("patientDetails", patO.getGeneralDateIVwasDone()));
			}
            */
			pat = (Patient) criteria.uniqueResult();
			if (pat!=null)Hibernate.initialize(pat.getPatientDetails());
			if (pat!=null)Hibernate.initialize(pat.getPatientHistory());
		}catch(HibernateException e){
			closeSession(session);
			
		}catch(Exception e){
		}
		finally {
			closeSession(session);
		}
		return pat;
	}
	@Override
	public Patient checkforPatientWithIdAndOffice(String patientid, Office off,Patient patH,boolean chekcforException) {

		return checkforPatient(patientid, off,patH, chekcforException, null);
	}

	@Override
	public Patient checkforPatientWithIdAndOfficeAndGeneralDate(String patientid, Office off,Patient patH,boolean chekcforException,String generalDate) {

		Session session = getSession();
		Patient pat = null;
		try {
			Criteria criteria = session.createCriteria(Patient.class);
			criteria.add(Restrictions.eq("patientId", patientid));
			criteria.createAlias("office", "off");
			criteria.createAlias("patientHistory", "patientHistory", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("patientDetails", "patientDetails", JoinType.INNER_JOIN);
			criteria.createAlias("patientDetails2", "patientDetails2", JoinType.LEFT_OUTER_JOIN);
			criteria.add(Restrictions.eq("off.uuid", off.getUuid()));
			if (generalDate!=null ) {
			if (!generalDate.equals(""))criteria.add(Restrictions.eq("patientDetails.generalDateIVwasDone", generalDate));
			}
			/*if (patH.getPatientDetails() != null && patH.getPatientDetails().size() > 0
					&& patH.getPatientDetails() != null && patH.getPatientDetails().size() > 0) {
				Iterator<PatientDetail> iter = patH.getPatientDetails().iterator();
				PatientDetail patO = iter.next();
			
			criteria.add(Restrictions.eq("patientDetails", patO.getGeneralDateIVwasDone()));
			}
            */
			pat = (Patient) criteria.uniqueResult();
			if (pat!=null)Hibernate.initialize(pat.getPatientDetails());
		}catch(HibernateException e){
			closeSession(session);
			if (chekcforException && e.getMessage().contains("com.tricon.ruleengine.model.db.PatientDetail2")) {
				
				pat =checkForDuplicatePatDetials2(patientid,off,patH,false,e.getMessage().split(", for class: com.tricon.ruleengine.model.db.PatientDetail2")[0].split("More than one row with the given identifier was found: ")[1]);
			}
		}catch(Exception e){
		}
		finally {
			closeSession(session);
		}
		return pat;
	}

	private Patient checkforPatient(String patientid, Office off,Patient patH,boolean chekcforException,String generalDate) {
		Session session = getSession();
		Patient pat = null;
		try {
			Criteria criteria = session.createCriteria(Patient.class);
			criteria.add(Restrictions.eq("patientId", patientid));
			criteria.createAlias("office", "off");
			criteria.createAlias("patientHistory", "patientHistory", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("patientDetails", "patientDetails", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("patientDetails2", "patientDetails2", JoinType.LEFT_OUTER_JOIN);
			criteria.add(Restrictions.eq("off.uuid", off.getUuid()));
			if (generalDate!=null ) {
				if (!generalDate.equals(""))criteria.add(Restrictions.eq("patientDetails.generalDateIVwasDone", generalDate));
			}
			/*if (patH.getPatientDetails() != null && patH.getPatientDetails().size() > 0
					&& patH.getPatientDetails() != null && patH.getPatientDetails().size() > 0) {
				Iterator<PatientDetail> iter = patH.getPatientDetails().iterator();
				PatientDetail patO = iter.next();
			
			criteria.add(Restrictions.eq("patientDetails", patO.getGeneralDateIVwasDone()));
			}
            */
			pat = (Patient) criteria.uniqueResult();
		}catch(HibernateException e){
			closeSession(session);
			if (chekcforException && e.getMessage().contains("com.tricon.ruleengine.model.db.PatientDetail2")) {
				
				pat =checkForDuplicatePatDetials2(patientid,off,patH,false,e.getMessage().split(", for class: com.tricon.ruleengine.model.db.PatientDetail2")[0].split("More than one row with the given identifier was found: ")[1]);
			}
		}catch(Exception e){
		}
		finally {
			closeSession(session);
		}
		return pat;
		
	}
	private Patient checkForDuplicatePatDetials2(String patientid, Office off,Patient patH,boolean chekcforException,String patDetailId) {
		Session session = getSession();

		try {
			String q="select id from patient_detail_2  where patient_detail_id="+patDetailId;
			List<Integer>pats = session.createSQLQuery(q).list();
			int id=-100;
			
		if (pats.size()==2) {
		for(Integer two:pats) {
			id= two.intValue();
			break;
		}
		String query="delete from patient_detail_2 where id="+id;
		session.createSQLQuery(query).executeUpdate();
		closeSession(session);
		return checkforPatientWithIdAndOffice(patientid,off,patH,false) ;
		}else return null;
		}catch(Exception n) {
			n.printStackTrace();
		}finally {
			closeSession(session);
		}
	   return null;	
		
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
			int id= (Integer)session.save(pd);
			pd.setId(id);
			
			PatientDetail2 pd2= pd.getPatientDetails2();
			pd2.setCreatedBy(user);
			pd2.setPatient(pd.getPatient());
			pd2.setPatientDetail(pd);
			session.save(pd2);
			
			// Patient Detail End
			// History start
			for (PatientHistory phi : pat.getPatientHistory()) {
				phi.setOffice(off);
				phi.setPatient(pat);
				phi.setPd(pd);
				session.save(phi);
			}
			// transaction.commit();
		} finally {
			closeSession(session);

		}
		return pat;
	}

	@Override
	public void updatePatientDataWithDetailsAndHistory1(Patient pat) throws Exception {
		// TODO Auto-generated method stub
		//Session session = getSession();
		try {
			//Transaction transaction = session.beginTransaction();
			// Break into 3 methods
			updateEntity(pat);
			RuleEngineLogger.generateLogs(clazz, "Entering To Save patient from DUMP 1.."
					+pat.getPatientId(), Constants.rule_log_debug, null);
			// x.split("");
			//transaction.commit();
		} finally {
			//closeSession(session);
		}

		//return pat;
	}

	@Override
	public Patient updatePatientDataWithDetailsAndHistory2(Patient pat,boolean detailsSave,boolean onlySave) throws Exception {
		// TODO Auto-generated method stub
		//Session session = getSession();
		Patient p=pat;
		try {
			//Transaction transaction = session.beginTransaction();
			// Break into 3 methods
			RuleEngineLogger.generateLogs(clazz, "Entering To Save patient from DUMP 2.."
					+pat.getPatientId(), Constants.rule_log_debug, null);
			
			Iterator<PatientDetail> iter = pat.getPatientDetails().iterator();
			PatientDetail pd = iter.next();
			
			
			
			
			// String x=null;
			if (detailsSave) {
				int id= (Integer)saveEntiy(pd);
				pd.setId(id);
				
				PatientDetail2 pd2 = pd.getPatientDetails2();
				if (pd2.getPatient()==null) pd2.setPatient(pd.getPatient());
				if (pd2.getPatientDetail()==null) pd2.setPatientDetail(pd);
				pd2.setCreatedBy(pd.getCreatedBy());
				int id1= (Integer)saveEntiy(pd2);
				
				
				Set<PatientDetail> s=new HashSet<>();
				s.add(pd);
				pat.setPatientDetails(s);
				p=pat;
				
			}
			else if (!onlySave) {
				updateEntity(pd);
				//updateEntity(pd2);
				PatientDetail2 pd21= pd.getPatientDetails2();
				Set<PatientDetail2> setPD2 = pat.getPatientDetails2();
				 Integer p21Id=-1;
				if (setPD2!=null && setPD2.size()>0) {
					for(PatientDetail2 ps:setPD2) {
						if (ps.getPatientDetail().getId()==pd.getId()) {
							p21Id=ps.getId();
						}
					}
					if (p21Id!=-1) {
					  pd21.setId(p21Id);
					  pd21.setPatientDetail(pd);
					  pd21.setCreatedBy(pat.getCreatedBy());
					  pd21.setUpdatedBy(pat.getUpdatedBy());
					  pd21.setPatient(pat);
					  updateEntity(pd21);
					}else {
						pd21.setCreatedBy(pat.getCreatedBy());
						pd21.setPatient(pat);
						pd21.setPatientDetail(pd);
						saveEntiy(pd21);
					}
				}else {
					pd21.setCreatedBy(pat.getCreatedBy());
					pd21.setPatient(pat);
					pd21.setPatientDetail(pd);
					saveEntiy(pd21);
				}
					//pd21.setUpdatedBy(pat.getUpdatedBy());
					
					
				
				
				Set<PatientDetail> s=new HashSet<>();
				s.add(pd);
				pat.setPatientDetails(s);
				p=pat;
				//p=pd;
				
			}
			
			// x.split("");
			//transaction.commit();
		} finally {
			//closeSession(session);
		}
         return p;
		
	}

	@Override
	public void updatePatientDataWithDetailsAndHistory3(Patient pat, Office off) throws Exception {
		// TODO Auto-generated method stub
		//Session session = getSession();
		RuleEngineLogger.generateLogs(clazz, "Entering To Save patient from DUMP 3.."
				+pat.getPatientId(), Constants.rule_log_debug, null);
		try {
			Iterator<PatientDetail> iter = pat.getPatientDetails().iterator();
			PatientDetail pd = iter.next();
			for (PatientHistory ph : pat.getPatientHistory()) {
				ph.setPatient(pat);
				ph.setOffice(off);
				ph.setPd(pd);
				saveEntiy(ph);
			}
			// x.split("");
			//transaction.commit();
		} finally {
			//closeSession(session);
		}

		//return pat;
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
	public List<CaplineIVFFormDto> searchPatientDetailFromIVF(CaplineIVFQueryFormDto dto, Office off,Set<String> patIds,boolean temp) {
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
			String tableName="";
			String extraInClause=" ";
			String status=" ";
			String fromClause=" from patient"+tableName+" p , patient_detail"+tableName+" pd left join patient_detail_"+tableName+"2 pd2 on pd2.patient_detail_id=pd.id where " + 
					          "	 pd.office_id='"+off.getUuid()+"'  and pd.patient_id=p.id " ;
			if (!temp) {
				tableName="_temp";
				extraInClause=(dto!=null && dto.getWebsiteNameDB()!=null && !dto.getWebsiteNameDB().equals("")? " and p.website_name = '"+dto.getWebsiteNameDB()+"' ":" ");
				status=", p.status as status,p.grade_pay as gradePay ";
				fromClause=" from patient_detail"+tableName+" pd right join  patient"+tableName+" p on pd.patient_id=p.id left join patient_detail"+tableName+"2 pd2 on pd2.patient_detail_id=pd.id where " + 
				          "	 p.office_id='"+off.getUuid()+"' " ;
			}
			////policy18//policy19/policy20 -- need to verify
			String inclause=(dto!=null && dto.getPatientIdDB()!=null && !dto.getPatientIdDB().equals("")? " and p.patient_id in ('"+dto.getPatientIdDB()+"') ":" ");
			if (patIds!=null) {
				inclause= " and p.patient_id in ("+String.join(", ", patIds)+")";
			}
			
			String orderBy=" order by p.created_date desc ";
			if (dto.getForSelantData()==null) dto.setForSelantData("");
			if (dto.getForSelantData().equals("sealant")) orderBy= " group by pd.id order by STR_TO_DATE( pd.general_date_iv_wasdone, '%Y-%m-%d') desc limit 1 ";
			
			String query = "select p.id as pidDB "+status +","+queryPatAll
					//+ " as  " //add new Columns here
					
					+ fromClause
					//(!dto.getPatientIdDB().equals("")? " and p.patient_id in ('"+dto.getPatientIdDB()+"') ":" ")+
					+ inclause +
					 extraInClause+
					(dto!=null && dto.getUniqueID()!=null && !dto.getUniqueID().equals("")? " and pd.id ="+dto.getUniqueID()+" ":" ")+
					(dto!=null && dto.getUniqueIDs()!=null && dto.getUniqueIDs().size()>0? " and pd.id in ("+String.join(", ", dto.getUniqueIDs())+") " :"  " )+
					(dto!=null && dto.getEmployerNameDB()!=null && !dto.getEmployerNameDB().equals("")? " and pd.employer_name like '%"+dto.getEmployerNameDB()+"%' ":" ")+
					(dto!=null && dto.getPolicyHolderDobDB()!=null && !dto.getPolicyHolderDobDB().equals("")? " and pd.policy_holder_dob like '%"+dto.getPolicyHolderDobDB()+"%' ":" ")+
					(dto!=null && dto.getPolicyHolderNameDB()!=null && !dto.getPolicyHolderNameDB().equals("")? " and pd.policy_holder like '%"+dto.getPolicyHolderNameDB()+"%' ":" ")+
					(dto!=null && dto.getPatientDobDB()!=null && !dto.getPatientDobDB().equals("")? " and p.dob = '"+dto.getPatientDobDB()+"' ":" ")+
					(dto!=null && dto.getPatientName()!=null && !dto.getPatientName().equals("")? " and (concat(coalesce(first_name,''),' ',coalesce(last_name,'')) like '%"+dto.getPatientName()+"%')"+" ":" ")+
					(dto!=null && dto.getGeneralDateIVFDoneDB()!=null && !dto.getGeneralDateIVFDoneDB().equals("")? " and pd.general_date_iv_wasdone ='"+dto.getGeneralDateIVFDoneDB()+"' ":" ")+
					(dto!=null && dto.getIvformTypeId()!=null && !dto.getIvformTypeId().equals("")? " and pd.iv_form_type_id ='"+dto.getIvformTypeId()+"' ":" ")
					+ orderBy;
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
					(dto!=null && dto.getGeneralDateIVFDoneDB()!=null && !dto.getGeneralDateIVFDoneDB().equals("")? " and pd.general_date_iv_wasdone ="+dto.getGeneralDateIVFDoneDB()+" ":" ")+
					(dto!=null && dto.getGeneralDateIVFDoneDBBet()!=null && !dto.getGeneralDateIVFDoneDBBet().equals("")? " and (pd.general_date_iv_wasdone "+dto.getGeneralDateIVFDoneDBBet()+" ) ":" ")+
					(dto!=null && dto.getClause()!=null && !dto.getClause().equals("")? " and  "+dto.getClause()+"  ":" ")
					
					;
          cL=session.createSQLQuery(query).list();
		} finally {
			closeSession(session);
		}
		return cL;
	}
	
	//h.history_code,history_tooth,history_surface,history_dos,p.patient_id,pd.general_date_iv_wasdone
	@Override
	public List<Object> searchPatientHistoryFromIVFGivenColumns(CaplineIVFQueryFormDto dto, Office off) {
		Session session = getSession();
		List<Object> cL=null;
		try {
			String inclause=(dto!=null && dto.getPatientIdDB()!=null && !dto.getPatientIdDB().equals("")? " and p.patient_id in ('"+dto.getPatientIdDB()+"') ":" ");
			if (dto.getPatientIdDB()!=null) {
				inclause= " and p.patient_id ='"+dto.getPatientIdDB()+"' ";
			}
			
			String query = "select '"+dto.getOfficeNameDB()+"'," +dto.getColumns()+" from   patient_detail pd , patient p, Patient_history h where "
					+ " pd.office_id='"+off.getUuid()+"'  and pd.patient_id=p.id and h.patient_id=p.id and h.patient_detail_id=pd.id " 
					+ inclause +
					(dto!=null && dto.getUniqueID()!=null && !dto.getUniqueID().equals("")? " and pd.id ="+dto.getUniqueID()+" ":" ")+
					(dto!=null && dto.getEmployerNameDB()!=null && !dto.getEmployerNameDB().equals("")? " and pd.employer_name like '%"+dto.getEmployerNameDB()+"%' ":" ")+
					(dto!=null && dto.getPatientName()!=null && !dto.getPatientName().equals("")? " and (concat(coalesce(first_name,''),' ',coalesce(last_name,'')) like '%"+dto.getPatientName()+"%')"+" ":" ")+
					(dto!=null && dto.getGeneralDateIVFDoneDB()!=null && !dto.getGeneralDateIVFDoneDB().equals("")? " and pd.general_date_iv_wasdone ="+dto.getGeneralDateIVFDoneDB()+" ":" ")+
					(dto!=null && dto.getGeneralDateIVFDoneDBBet()!=null && !dto.getGeneralDateIVFDoneDBBet().equals("")? " and (pd.general_date_iv_wasdone "+dto.getGeneralDateIVFDoneDBBet()+" ) ":" ")+
					(dto!=null && dto.getClause()!=null && !dto.getClause().equals("")? " and  "+dto.getClause()+"  ":" ")
					
					;
          cL=session.createSQLQuery(query).list();
		} finally {
			closeSession(session);
		}
		return cL;
	}
	
	@Override
	public List<PatientHistory> searchPatientHistoryForPatient(Set<String> patientIds, Office off,Set<String> patDids,boolean temp) {
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
			String tableName="";
			if (!temp) tableName="_temp";
			String query =" select ph.patient_detail_id as pdid ,p.patient_id as pid,ph.history_code as historyCode, ph.history_tooth as historyTooth," + 
					" ph.history_surface as historySurface, ph.history_dos as historyDOS," + 
					" ph.id as id from Patient_history"+tableName+" ph,office o,patient"+tableName+" p where p.id=ph.patient_id and o.uuid=ph.office_id and"+
					" ph.patient_detail_id in ( "+String.join(",", patDids)+") and ph.patient_id in ( "+String.join(", ", patientIds)+") and o.uuid='"+
					off.getUuid()+"' order by  STR_TO_DATE(history_dos, '%Y-%m-%d') desc";
			
			patH=session.createSQLQuery(query).setResultTransformer(Transformers.aliasToBean(PatientHistory.class)). list();
			
			
		} finally {
			closeSession(session);
		}
		return patH;
	}

	@Override
	public void deletePatientHistoryByIds(String[] ids) {
		Session session = getSession();
		try {
			String query="delete from Patient_history where id in ("+String.join(",", ids)+")";
			session.createSQLQuery(query).executeUpdate();
			
		} finally {
			closeSession(session);
		}
		
		
	}
	
	@Override
	public PatientTemp checkforPatientWithIdAndOfficeTemp(String patientid, Office off) {

		Session session = getSession();
		PatientTemp pat = null;
		try {
			Criteria criteria = session.createCriteria(PatientTemp.class);
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
			pat = (PatientTemp) criteria.uniqueResult();
		} finally {
			closeSession(session);
		}
		return pat;
	}

	@Override
	public Integer savePatientTempDataWithDetailsAndHistory(PatientTemp pat, Office off, User user) throws Exception{
		Session session = getSession();
		Integer i=0;
		try {

			// Transaction transaction = session.beginTransaction();
			pat.setCreatedBy(user);
			pat.setId(((Integer) session.save(pat)));
			i=pat.getId();
			// Patient Detail Start
			if (pat.getPatientDetails()!=null && pat.getPatientDetails().size()>0) {
			Iterator<PatientDetailTemp> iter = pat.getPatientDetails().iterator();
			PatientDetailTemp pd = iter.next();
			
			
			pd.setCreatedBy(user);
			int id= (Integer)session.save(pd);
			pd.setId(id);
			
			PatientDetailTemp2 pd2= pd.getPatientDetails2();
			if (pd2!=null) {
				pd2.setPatientDetail(pd);
				pd2.setPatient(pd.getPatient());
				session.save(pd2);
			
			}
			// Patient Detail End
			// History start
			for (PatientHistoryTemp phi : pat.getPatientHistory()) {
				phi.setOffice(off);
				phi.setPatient(pat);
				phi.setPd(pd);
				session.save(phi);
			}
		   }
			// transaction.commit();
		} finally {
			closeSession(session);

		}
		return i;
	}

	@Override
	public void updatePatientTempDataOnly(PatientTemp pat) throws Exception{
		Session session = getSession();
		try {

			Transaction transaction = session.beginTransaction();
			session.update(pat);
			transaction.commit();
			
		    } finally {
			closeSession(session);
          
		}
		//return pat;
	}
	
	@Override
	public List<ScrapPatient> getScrappingStatusByPatIdsTemp(List<Integer>ids){
		Session session = getSession();
		List<ScrapPatient> pats = null;
		try {
			Criteria criteria = session.createCriteria(PatientTemp.class);
			
			criteria.add(Restrictions.in("id", ids));
			
			ProjectionList pjList = Projections.projectionList();
			pjList.add(Projections.property("patientId"), "patientId");
			pjList.add(Projections.property("firstName"), "firstName");
			pjList.add(Projections.property("lastName"), "lastName");
			pjList.add(Projections.property("status"), "status");
			pjList.add(Projections.property("dob"), "dob");
			criteria.setProjection(pjList);
			criteria.setResultTransformer(Transformers.aliasToBean(ScrapPatient.class));
			Object o=(Object) criteria.list();
			if (o!=null) pats = (List<ScrapPatient>)o;
		} finally {
			closeSession(session);
		}
		return pats;
		
	}
	
	/**
	 * This method makes query with the help of column name that comes from API and After executing query
	 * data comes from particular table and holds into List of Object
	 */
	@Override
	public List<Object> replicationQueries(CaplineDataReplicationDto o,Office office)throws Exception
	{
		Session s=getSession();
		List<Object> data=new ArrayList<>();
		String finalQuery="",queryFor="";
		queryFor=(o.getQueryName().isEmpty())?"NOT_FOUND":o.getQueryName().trim();
		switch(queryFor)
		{
		    case Constants.QUERY_FOR_Reconcillation:
		    	finalQuery="select "+o.getSelectcolumns()+" from office off, es_data_replica_patient p "
		    			+ "JOIN es_data_replica_transactions_detail t ON t.patient_id = p.patient_id JOIN "
		    			+ "es_data_replica_transactions_header th on th.tran_num = t.tran_num WHERE th.impacts = 'P' AND DATE(t.date_entered) BETWEEN "+o.getGndatebet()+""
		    			+ " and off.uuid=p.office_id and off.uuid='"+office.getUuid()+"'";
		    	break;
		    case Constants.QUERY_FOR_DTP_PlannedServices:
		    	finalQuery="select "+o.getSelectcolumns()+" from office off,es_data_replica_patient p "
		    			+ "JOIN es_data_replica_planned_services ps ON p.patient_id=ps.patient_id "
		    			+ "LEFT JOIN es_data_replica_employer e ON (p.prim_employer_id = e.employer_id) "
		    			+ "JOIN es_data_replica_provider pr ON ps.provider_id = pr.provider_id WHERE ps.date_planned BETWEEN "+o.getGndatebet()+" and off.uuid=p.office_id and off.uuid='"+office.getUuid()+"'";
		    	break;
		    case Constants.QUERY_FOR_DTP_Treatmentplans:
		    	finalQuery="select "+o.getSelectcolumns()+" from office off,es_data_replica_treatment_plans tp "
		    			+ "JOIN es_data_replica_treatment_plan_items ti ON ti.treatment_plan_id = tp.treatment_plan_id WHERE tp.date_entered BETWEEN "+o.getGndatebet()+" and off.uuid=tp.office_id and off.uuid='"+office.getUuid()+"'";
		    	break;
		    case Constants.QUERY_FOR_DTP_Appointment:
		    	finalQuery="select "+o.getSelectcolumns()+" from office off,es_data_replica_appointment a WHERE DATE(a.start_time) BETWEEN "+o.getGndatebet()+" and off.uuid=a.office_id and off.uuid='"+office.getUuid()+"'";
		    	break;
		    	
		   case Constants.QUERY_FOR_ItemizedCash:
		    	/*finalQuery="select "+o.getSelectcolumns()+" from office off,es_data_replica_transactions t "
		    			+ "JOIN es_data_replica_payment_provider py ON t.tran_num = py.tran_num "
		    			+ "JOIN es_data_replica_paytype pt ON t.paytype_id = pt.paytype_id "
		    			+ "JOIN es_data_replica_patient p ON p.patient_id=t.patient_id WHERE t.tran_date BETWEEN "+o.getGndatebet()+" and off.uuid=p.office_id and off.uuid='"+office.getUuid()+"' GROUP BY t.patient_id,CONCAT(p.first_name,' ', p.last_name ),t.tran_date,t.paytype_id,pt.description,t.provider_id";
		    	break;*/
		    	default:System.out.println("No Match Found");
		}
		System.out.println(finalQuery);
		try {
          if(finalQuery!=null && !finalQuery.isEmpty()) {
		  Query q=s.createSQLQuery(finalQuery);
		  data=q.list();
		  }
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally{
			closeSession(s);
		}
		return data;
	}
	
}
