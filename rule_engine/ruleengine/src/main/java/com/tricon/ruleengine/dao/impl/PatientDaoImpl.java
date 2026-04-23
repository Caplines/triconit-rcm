package com.tricon.ruleengine.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.stereotype.Repository;

import com.tricon.ruleengine.dao.PatientDao;
import com.tricon.ruleengine.dto.CaplineDataReplicationDto;
import com.tricon.ruleengine.dto.CaplineIVFFormDto;
import com.tricon.ruleengine.dto.CaplineIVFQueryFormDto;
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
import com.tricon.ruleengine.model.db.User;
import com.tricon.ruleengine.utils.Constants;

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
			+ " iv_sedation_d9243_percentage as sedations2,iv_sededation_d9245_percentage as sedations4,extractions_minor_percentage as extractions1,"
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
			+ " d7321fr as d7321fr,d7473fr as d7473fr,sedations1fr as sedations1fr,sedations3fr as sedations3fr,sedations4fr as sedations4fr,d9239fr as d9239fr,sedations2fr as sedations2fr,"
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
			+ " perio_d9910 as perioD9910, d9910_frequency as d9910Frequency, oonbenfits as oonbenfits, d9630 as d9630,d9630fr as d9630fr,d0431 as d0431,d0431fr as d0431fr,d4999 as d4999,d4999fr as d4999fr,"
			+ " d2962 as d2962,d2962_fr as d2962fr,history_count as  historyCount,"
			+ " d0145 as d0145, d0150 as d0150,d2750 as d2750,d2750_fr as d2750fr,"
			+ " d0220 as d0220,d0220_freq as d0220Freq,d0230 as d0230,"
			+ " bwx as bwx,d0210 as d0210,d0210_freq as d0210Freq,"
			+ " d0350_freq as d0350Freq,bwx_freq as bwxFreq,d2931 as d2931,"
			+ " d1206 as d1206,d1208 as d1208,b_which_code as bWhichCode,"
			+ " d5110_20 as d5110_20, d1330_freq as d1330Freq,"
			+ " d5111_12_13_14 as d5111_12_13_14,d5130_40 as d5130_40,"
			+ " d5810_c as d5810_c,d5225_26_c as d5225_26_c,"
			+ " extractions1_fr as extractions1fr,extractions2_fr as extractions2fr,"
			+ " implants_c as implantsC,d1520_26_27 as d1520_26_27,"
			+ " d1520_26_27_fr as d1520_26_27_fr,waiting_period as waitingPeriod,"
			+ " wip as wip,ins_billing_c as insBillingC,benefit_period as benefitPeriod,"
			+ " waiting_period_drop as waitingPeriodDrop,d8070 as d8070,"
			+ " d8080 as d8080,d8090 as d8090,d8670 as d8670,d8680 as d8680,"
			+ " d8690 as d8690,d8070_fr as d8070fr, d8080_fr as d8080fr,"
			+ " d8090_fr as d8090fr,d8670_fr as d8670fr,d8680_fr as d8680fr,"
			+ " d8690_fr as d8690fr,apptype as apptype,sec_provider_name as secProviderName,"
			+ " sec_prov_network as secProvNetwork,yes_no_assign_to_office as yesNoAssignToffice,"
			+ " policy18 as d0120,d1110 as d1110, d1120 as d1120, d7953_extraction as d7953Extraction, "
			+ " d8660 as d8660 ,d8660_fr as d8660fr,d8210 as d8210, d8210_fr as d8210fr,"
			+ " d8220 as d8220,d8220_fr as d8220fr,d8020 as d8020,d8020_fr as d8020fr,"
			+ " d8692 as d8692,d8692_fr as d8692fr,implantsC_percentage as implantsCPercentage, "
			+ " does_exam_share_freq as doesExamShareFreq,d5110_20_percentage as d511020Percentage, "
			+ " d5130_40_percentage as d513040Percentage,d5810_c_percentage as d5810CPercentage,d9310 as d9310,d9310_fr as d9310fr,"
			+ " d6011 as d6011,d6011_fr as d6011fr,d5862 as d5862,d5862_fr as d5862fr,d7311_select as d7311Select,d5213142625 as d5213142625,d5213142625_fr as d5213142625fr,d2954 as d2954,d2954_fr as d2954fr,"
			+ " policy21 as policy21,share_fr2 as shareFr2,policy20 as policy20,exams5 as exams5,d0367 as d0367,d0180 as d0180,exams6 as exams6";

	/**
	 * {@link com.tricon.ruleengine.service.impl.CaplineIVFGoogleFormServiceImpl#saveAllData} uses
	 * {@code Patient} after this DAO closes its session. Initialize every lazy association that
	 * code path may read (after Criteria lookup, including duplicate-PatientDetail2 fallback).
	 */
	private void eagerInitPatientGraphForIvfSave(Patient loaded) {
		if (loaded == null) {
			return;
		}
		Hibernate.initialize(loaded.getPatientDetails());
		Hibernate.initialize(loaded.getPatientHistory());
		Hibernate.initialize(loaded.getPatientDetails2());
		if (loaded.getPatientDetails() != null) {
			for (PatientDetail pdEager : loaded.getPatientDetails()) {
				Hibernate.initialize(pdEager.getiVFormType());
				Hibernate.initialize(pdEager.getPatientDetails2());
			}
		}
		if (loaded.getPatientHistory() != null) {
			for (PatientHistory phEager : loaded.getPatientHistory()) {
				if (phEager.getPd() != null) {
					Hibernate.initialize(phEager.getPd());
					Hibernate.initialize(phEager.getPd().getiVFormType());
				}
			}
		}
	}

	@Override
	public Patient checkforPatientWithId(String patientid, Office off) {
		// Retry once: transient TCP drops to AWS RDS can cause the @OneToOne secondary
		// SELECTs to fail mid-session. A fresh C3P0 connection (tested on checkout) fixes it.
		String hql = "SELECT DISTINCT p FROM Patient p " +
			"LEFT JOIN FETCH p.patientDetails " +
			"LEFT JOIN FETCH p.patientHistory " +
			"WHERE p.patientId = :pid AND p.office.uuid = :officeUuid";
		for (int attempt = 1; attempt <= 2; attempt++) {
			Session session = getSession();
			try {
				@SuppressWarnings("unchecked")
				java.util.List<Patient> patients = session.createQuery(hql)
					.setParameter("pid", patientid)
					.setParameter("officeUuid", off.getUuid())
					.list();
				if (patients.isEmpty()) {
					return null;
				}
				Patient loaded = patients.get(0);
				eagerInitPatientGraphForIvfSave(loaded);
				return loaded;
			} catch (Exception e) {
				RuleEngineLogger.generateLogs(clazz,
					"checkforPatientWithId attempt " + attempt + " failed for patient ["
					+ patientid + "]: " + e.getMessage(),
					Constants.rule_log_debug, null);
				// PatientDetail2 duplicate-row issue: clean up the duplicate and retry via
				// the Criteria-based path that already handles this case correctly.
				if (attempt == 1 && e.getMessage() != null
						&& e.getMessage().contains("com.tricon.ruleengine.model.db.PatientDetail2")
						&& e.getMessage().contains("More than one row with the given identifier was found: ")) {
					try {
						String pdId = e.getMessage()
							.split(", for class: com.tricon.ruleengine.model.db.PatientDetail2")[0]
							.split("More than one row with the given identifier was found: ")[1].trim();
						Patient cleaned = checkForDuplicatePatDetials2(patientid, off, null, false, pdId);
						if (cleaned != null) {
							eagerInitPatientGraphForIvfSave(cleaned);
							return cleaned;
						}
					} catch (Exception ex) {
						RuleEngineLogger.generateLogs(clazz,
							"checkForDuplicatePatDetials2 fallback failed for patient [" + patientid + "]: " + ex.getMessage(),
							Constants.rule_log_debug, null);
					}
				}
			} finally {
				closeSession(session);
			}
		}
		return null;
	}
	@Override
	public Patient checkforPatientWithIdAndOffice(String patientid, Office off,Patient patH,boolean chekcforException) {

		return checkforPatient(patientid, off,patH, chekcforException, null);
	}

	@Override
	public Patient checkforPatientWithIdAndOfficeAndGeneralDate(String patientid, Office off, Patient patH,
			boolean chekcforException, String generalDate) {
		// Retry once on transient connection failure (same reason as checkforPatientWithId).
		String hql = "SELECT DISTINCT p FROM Patient p " +
			"LEFT JOIN FETCH p.patientDetails pd " +
			"LEFT JOIN FETCH p.patientHistory " +
			"WHERE p.patientId = :pid AND p.office.uuid = :officeUuid";
		if (generalDate != null && !generalDate.isEmpty()) {
			hql += " AND pd.generalDateIVwasDone = :generalDate";
		}
		for (int attempt = 1; attempt <= 2; attempt++) {
			Session session = getSession();
			try {
				org.hibernate.Query q = session.createQuery(hql);
				q.setParameter("pid", patientid);
				q.setParameter("officeUuid", off.getUuid());
				if (generalDate != null && !generalDate.isEmpty()) {
					q.setParameter("generalDate", generalDate);
				}
				@SuppressWarnings("unchecked")
				java.util.List<Patient> results = q.list();
				if (results.isEmpty()) {
					return null;
				}
				Patient loaded = results.get(0);
				// patientDetails2 and nested graph are lazy; initialize before session closes, otherwise
				// updatePatientDataWithDetailsAndHistory2() hits LazyInitializationException on .size()
				eagerInitPatientGraphForIvfSave(loaded);
				return loaded;
			} catch (Exception e) {
				RuleEngineLogger.generateLogs(clazz,
					"checkforPatientWithIdAndOfficeAndGeneralDate attempt " + attempt
					+ " failed for patient [" + patientid + "]: " + e.getMessage(),
					Constants.rule_log_debug, null);
				// PatientDetail2 duplicate-row issue: clean up the duplicate and fall back
				// to the no-date lookup so the patient is always found.
				if (attempt == 1 && e.getMessage() != null
						&& e.getMessage().contains("com.tricon.ruleengine.model.db.PatientDetail2")
						&& e.getMessage().contains("More than one row with the given identifier was found: ")) {
					try {
						String pdId = e.getMessage()
							.split(", for class: com.tricon.ruleengine.model.db.PatientDetail2")[0]
							.split("More than one row with the given identifier was found: ")[1].trim();
						Patient cleaned = checkForDuplicatePatDetials2(patientid, off, patH, false, pdId);
						if (cleaned != null) {
							eagerInitPatientGraphForIvfSave(cleaned);
							return cleaned;
						}
					} catch (Exception ex) {
						RuleEngineLogger.generateLogs(clazz,
							"checkForDuplicatePatDetials2 fallback failed for patient [" + patientid + "]: " + ex.getMessage(),
							Constants.rule_log_debug, null);
					}
				}
			} finally {
				closeSession(session);
			}
		}
		return null;
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
		if (pat != null) {
			eagerInitPatientGraphForIvfSave(pat);
		}
	} catch (HibernateException e) {
		closeSession(session);
		if (chekcforException && e.getMessage() != null
				&& e.getMessage().contains("com.tricon.ruleengine.model.db.PatientDetail2")) {

			pat = checkForDuplicatePatDetials2(patientid, off, patH, false, e.getMessage()
					.split(", for class: com.tricon.ruleengine.model.db.PatientDetail2")[0]
					.split("More than one row with the given identifier was found: ")[1]);
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
public Patient updatePatientDataWithDetailsAndHistory2(Patient pat, boolean detailsSave, boolean onlySave) throws Exception {
    Patient p = pat;
    try {
        RuleEngineLogger.generateLogs(clazz, "Entering To Save patient from DUMP 2.."
                + pat.getPatientId(), Constants.rule_log_debug, null);

        Iterator<PatientDetail> iter = pat.getPatientDetails().iterator();
        PatientDetail pd = iter.next();

        if (detailsSave) {
            int id = (Integer) saveEntiy(pd);
            pd.setId(id);

            PatientDetail2 pd2 = pd.getPatientDetails2();
            if (pd2.getPatient() == null) pd2.setPatient(pd.getPatient());
            if (pd2.getPatientDetail() == null) pd2.setPatientDetail(pd);
            pd2.setCreatedBy(pd.getCreatedBy());
            int id1 = (Integer) saveEntiy(pd2);

            Set<PatientDetail> s = new HashSet<>();
            s.add(pd);
            pat.setPatientDetails(s);
            p = pat;

        } else if (!onlySave) {
            updateEntity(pd);

            PatientDetail2 pd21 = pd.getPatientDetails2();

            // Fix: Re-fetch patientDetails2 within an active session to avoid LazyInitializationException
            Set<PatientDetail2> setPD2 = null;
            try {
                Patient freshPat = (Patient) getSession().get(Patient.class, pat.getId());
                if (freshPat != null) {
                    Hibernate.initialize(freshPat.getPatientDetails2());
                    setPD2 = freshPat.getPatientDetails2();
                }
            } catch (Exception e) {
                RuleEngineLogger.generateLogs(clazz, "Could not re-fetch patientDetails2 for patient "
                        + pat.getPatientId() + " - " + e.getMessage(), Constants.rule_log_debug, null);
                setPD2 = null;
            }

            Integer p21Id = -1;
            if (setPD2 != null && setPD2.size() > 0) {
                for (PatientDetail2 ps : setPD2) {
                    if (ps.getPatientDetail().getId() == pd.getId()) {
                        p21Id = ps.getId();
                    }
                }
                if (p21Id != -1) {
                    pd21.setId(p21Id);
                    pd21.setPatientDetail(pd);
                    pd21.setCreatedBy(pat.getCreatedBy());
                    pd21.setUpdatedBy(pat.getUpdatedBy());
                    pd21.setPatient(pat);
                    updateEntity(pd21);
                } else {
                    pd21.setCreatedBy(pat.getCreatedBy());
                    pd21.setPatient(pat);
                    pd21.setPatientDetail(pd);
                    saveEntiy(pd21);
                }
            } else {
                pd21.setCreatedBy(pat.getCreatedBy());
                pd21.setPatient(pat);
                pd21.setPatientDetail(pd);
                saveEntiy(pd21);
            }

            Set<PatientDetail> s = new HashSet<>();
            s.add(pd);
            pat.setPatientDetails(s);
            p = pat;
        }

    } finally {
        // session cleanup handled by caller/Spring
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
			if (dto.getForSelantData().equals("sealant")) orderBy= " group by pd.id, pd2.id order by STR_TO_DATE( pd.general_date_iv_wasdone, '%Y-%m-%d') desc limit 1 ";
			
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
					(dto!=null && dto.getIvformTypeId()!=null && !dto.getIvformTypeId().equals("")? " and pd.iv_form_type_id ='"+dto.getIvformTypeId()+"' ":" ") + 

                     (dto!=null && dto.getGroupName()!=null && !dto.getGroupName().equals("") ? " and pd.employer_name like '%"+dto.getGroupName()+"%' " : " ") + 
                     (dto!=null && dto.getGroupNumber()!=null && !dto.getGroupNumber().equals("") ? " and pd.group_p like '%"+dto.getGroupNumber()+"%' " : " ") +
                     (dto!=null && dto.getPreventiveServicesPct()!=null && !dto.getPreventiveServicesPct().equals("") ? " and pd.preventive_percentage = '"+dto.getPreventiveServicesPct()+"' " : " ") +
					 (dto!=null && dto.getBasicServicesPct()!=null && !dto.getBasicServicesPct().equals("") ? " and pd.basic_percentage = '"+dto.getBasicServicesPct()+"' " : " ") +
					 (dto!=null && dto.getMajorServicesPct()!=null && !dto.getMajorServicesPct().equals("") ? " and pd.major_percentage = '"+dto.getMajorServicesPct()+"' "  : " ") +
					 (dto!=null && dto.getInsuranceName()!=null && !dto.getInsuranceName().equals("") ? " and pd.ins_name like '%"+dto.getInsuranceName()+"%' " : " ") +
					 (dto!=null && dto.getAnnualMax()!=null && !dto.getAnnualMax().equals("") ? " and pd.plan_annual_max = '"+dto.getAnnualMax()+"' " : " ") + 
					 (dto!=null && dto.getIndividualDeductible()!=null && !dto.getIndividualDeductible().equals("")  ? " and pd.plan_individual_dedudtible = '"+dto.getIndividualDeductible()+"' " : " ") +

					orderBy;
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
			
			String query = "select '"+dto.getOfficeNameDB()+"'," +dto.getColumns()+" from patient_detail pd,patient_detail_2 pd2 , patient p where "
					+ " pd.office_id='"+off.getUuid()+"'  and pd.patient_id=p.id and pd2.patient_id=p.id and pd2.patient_detail_id=pd.id  " 
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
		Session s=null;
		List<Object> data=new ArrayList<>();
		String finalQuery="",queryFor="";
		queryFor=(o.getQueryName().isEmpty())?"NOT_FOUND":o.getQueryName().trim();
		switch(queryFor)
		{
		    case Constants.QUERY_FOR_Reconcillation:
		    	finalQuery="select  "+o.getSelectcolumns()+" from "
		    			+ "es_data_replica_patient p , es_data_replica_transactions_detail t, es_data_replica_transactions_header th "
		    			+ "where th.tran_num = t.tran_num and  p.office_id='"+office.getUuid()+"' "
		    			+ "and th.impacts = 'P' and t.office_id='"+office.getUuid()+"' "
		    			+ "and p.office_id=th.office_id and t.patient_id = p.patient_id and th.tran_num = t.tran_num and "
		    			+ "t.date_entered  BETWEEN "+o.getGndatebet()+"";
		    	break;
		    case Constants.QUERY_FOR_DTP_PlannedServices:
		    	finalQuery="select "+o.getSelectcolumns()+" from es_data_replica_patient p join "
		    			+ "es_data_replica_planned_services ps "
		    			+ "on (p.patient_id=ps.patient_id and p.office_id=ps.office_id and p.office_id='"+office.getUuid()+"' "
		    			+ "and ps.date_planned BETWEEN "+o.getGndatebet()+") "
		    			+ "LEFT JOIN es_data_replica_employer e ON "
		    			+ "(p.prim_employer_id = e.employer_id and p.office_id='"+office.getUuid()+"' and "
		    			+ "p.office_id=e.office_id and "
		    			+ "ps.date_planned BETWEEN "+o.getGndatebet()+") "
		    			+ "JOIN es_data_replica_provider pr ON (ps.provider_id = pr.provider_id and  ps.office_id=pr.office_id)"; 
		    	break;
		    case Constants.QUERY_FOR_DTP_Treatmentplans:
		    	finalQuery="select "+o.getSelectcolumns()+" from es_data_replica_treatment_plans tp "
		    			+ "JOIN es_data_replica_treatment_plan_items ti "
		    			+ "ON ti.treatment_plan_id = tp.treatment_plan_id WHERE tp.date_entered BETWEEN "+o.getGndatebet()+" and "
		    			+ "ti.office_id=tp.office_id and tp.office_id='"+office.getUuid()+"'";
		    	break;
		    case Constants.QUERY_FOR_DTP_Appointment:
		    	finalQuery="select "+o.getSelectcolumns()+" from es_data_replica_appointment a "
		    			+ "WHERE DATE(a.start_time) BETWEEN "+o.getGndatebet()+" "
		    			+ "and a.office_id='"+office.getUuid()+"'";
		    	break;
		    	
		   case Constants.QUERY_FOR_ItemizedCash:			   
			   //start TransactionQuery
			   String transactionQuery="(select * from "
			    		+ "(select  h.tran_num,user_id,type,tran_date, "
			    		+ "(SELECT  t.patient_id FROM es_data_replica_transactions_detail t WHERE "
			    		+ "t.office_id=h.office_id and "
			    		+ "t.office_id='"+office.getUuid()+"' and "
			    		+ "t.tran_num = h.tran_num "
			    		+ "order by patient_id limit 1) as patient_id, "
			    		+ "resp_party_id,amount,service_code, "
			    		+ "paytype_id,sequence, "
			    		+ "(SELECT  t.provider_id FROM es_data_replica_transactions_detail t WHERE "
			    		+ "t.office_id=h.office_id and "
			    		+ "t.office_id='"+office.getUuid()+"' and provider_id is not null and "
			    		+ "t.tran_num = h.tran_num ORDER BY provider_id limit 1) as provider_id,"
			    		+ "(SELECT  t.collections_go_to FROM es_data_replica_transactions_detail t  WHERE "
			    		+ "t.office_id=h.office_id and "
			    		+ "t.office_id='"+office.getUuid()+"' and collections_go_to is not null and "
			    		+ "t.tran_num = h.tran_num ORDER BY collections_go_to  limit 1) as collections_go_to, "
			    		+ "statement_num,null as old_tooth,surface,fee,discount_surcharge,tax, "
			    		+ "description,defective,impacts,status,adjustment_type,claim_id,est_primary, "
			    		+ "est_secondary,paid_primary,paid_secondary,"
			    		+ "(SELECT  t.provider_practice_id FROM es_data_replica_transactions_detail t WHERE "
			    		+ "t.office_id=h.office_id and t.office_id='"+office.getUuid()+"' and provider_practice_id is not null and t.tran_num = h.tran_num ORDER BY provider_id limit 1) as provider_practice_id, "
			    		+ "(SELECT  t.patient_practice_id FROM es_data_replica_transactions_detail t  WHERE "
			    		+ "t.office_id=h.office_id and "
			    		+ "t.office_id='"+office.getUuid()+"' and patient_practice_id is not null and "
			    		+ "t.tran_num = h.tran_num ORDER BY collections_go_to  limit 1) as patient_practice_id, "
			    		+ "bulk_payment_num,aging_date, "
			    		+ "tooth,lab_fee,lab_fee2,lab_code,lab_code2,pre_fee,standard_fee_id,practice_id, "
			    		+ "procedure_type_codes,balance,h.office_id as office_idt "
			    		+ "from  es_data_replica_transactions_header h where h.office_id='"+office.getUuid()+"' "
			    		+ "and h.tran_date BETWEEN "+o.getGndatebet()+") as tran_num) t, ";
			    //start PayemntProviderQuery
			   String paymentProviderQuery="(SELECT t.tran_num as tran_num, "
			    		+ "t.collections_go_to as provider_id, "
			    		+ "SUM(-t.amount) AS amount, "
			    		+ "t.provider_practice_id as practice_id,"
			    		+ "t.provider_id as prod_provider_id,h.office_id as office_idp "
			    		+ "FROM es_data_replica_transactions_detail t, "
			    		+ "es_data_replica_transactions_header h WHERE "
			    		+ "h.office_id='"+office.getUuid()+"' "
			    		+ "and h.office_id=t.office_id and "
			    		+ "(h.tran_date BETWEEN "+o.getGndatebet()+") and "
			    		+ "h.tran_num = t.tran_num AND t.status = 1 AND "
			    		+ "((h.type IN ('A','P','I','Y','C','W') AND h.status IN ('A','D','V')) OR "
			    		+ "(h.type = 'D' AND h.status = 'V')) "
			    		+ "GROUP BY t.tran_num, t.collections_go_to, t.provider_id, t.provider_practice_id "
			    		+ "ORDER BY t.tran_num, t.collections_go_to, t.provider_id, t.provider_practice_id) py ";			    		
		    	finalQuery="select "+o.getSelectcolumns()+" from "+transactionQuery+paymentProviderQuery+",es_data_replica_patient p,es_data_replica_paytype pt "
		    			+ "where p.office_id='"+office.getUuid()+"' and t.paytype_id = pt.paytype_id and pt.office_id=p.office_id and "
		    			+ "t.tran_num = py.tran_num  and p.patient_id=t.patient_id and p.office_id=t.office_idt and py.office_idp=t.office_idt "
		    			+ "GROUP BY t.patient_id,t.tran_date,t.paytype_id,pt.description,t.provider_id";
		    	break;
		    	default:RuleEngineLogger.generateLogs(clazz, "No Match Found", Constants.rule_log_debug, null);
		}
		RuleEngineLogger.generateLogs(clazz, finalQuery, Constants.rule_log_debug, null);
		try {
          if(finalQuery!=null && !finalQuery.isEmpty()) {
          s=getSession();
		  Query q=s.createSQLQuery(finalQuery);
		  data=q.list();
		  }
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally{
			if(s!=null) {
			closeSession(s);
			}
		}
		return data;
	}
	
	 public static void main(String[] args) {

	        // unsorted array
	        int[] integerList = {-55, -44, -33, -88, -99};

	        // Getting the natural (ascending) order of the array
	        Arrays.sort(integerList);

	        // Getting the last item of the now sorted array (which represents the maximum, in other words: highest number)
	        int max = integerList.length-1;

	        // reversing the order with a simple for-loop
	        System.out.println("Array in descending order:");
	        for(int i=max; i>=0; i--) {
	            System.out.println(integerList[i]);
	        }

	        // You could make the code even shorter skipping the variable max and use
	        // "int i=integerList.length-1" instead of int "i=max" in the parentheses of the for-loop
	    }
	
}

