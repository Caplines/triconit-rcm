package com.tricon.esdatareplication.eaglesoft;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tricon.esdatareplication.entity.repdb.Chairs;
import com.tricon.esdatareplication.entity.repdb.PayType;
import com.tricon.esdatareplication.entity.repdb.Patient;

@Service
public class PrepairESDataFromFromResultSet {

	public List<?> createTableData(ResultSet rs, Class<?> clazz) {

		System.out.println("clazz.getName()-->"+clazz.getName());
		System.out.println("clazz.getName()-->"+clazz.getClass().getName());
		System.out.println(clazz.equals(PayType.class));
		
		if (clazz.equals(Chairs.class))
			return createChairData(rs);
		else if (clazz.equals(PayType.class))
			return createPayTypeData(rs);
		else if (clazz.equals(Patient.class))
			return createPatientData(rs);
		return null;
	}

	private List<Chairs> createChairData(ResultSet rs) {

		List<Chairs> cList=new ArrayList<>();
		Chairs chairs =null;
		try {
		while (rs.next()) {
			chairs= new Chairs();
			// chairs.
			chairs.setPatientId(rs.getInt("patient_id"));
			chairs.setChairName(rs.getString("chair_name"));
			chairs.setSunTempId(rs.getInt("sun_temp_id"));
			chairs.setMonTempId(rs.getInt("mon_temp_id"));
			chairs.setTueTempId(rs.getInt("tue_temp_id"));
			chairs.setWedTempId(rs.getInt("wed_temp_id"));
			chairs.setThuTempId(rs.getInt("thu_temp_id"));
			chairs.setFriTempId(rs.getInt("fri_temp_id"));
			chairs.setSatTempId(rs.getInt("sat_temp_id"));
			chairs.setPracticeId(rs.getInt("practice_id"));
			chairs.setMovedToCloud(0);
			cList.add(chairs);
			// dataList.add(rs.getString(x));

		}
		}catch(Exception n) {
			n.printStackTrace();	
		}
		return cList;
	}
	
	private List<PayType> createPayTypeData(ResultSet rs) {

		List<PayType> cList=new ArrayList<>();
		PayType payType =null;
		try {
		while (rs.next()) {
			payType= new PayType();
			// chairs.
			payType.setPayTypId(rs.getInt("paytype_id"));
			payType.setSequence(rs.getInt("sequence"));
			payType.setDescription(rs.getString("description"));
			payType.setPrompt(rs.getString("prompt"));
			payType.setDisplayOonPaymentScreen(rs.getString("display_on_payment_screen"));
			payType.setIncludeOnDepositYn(rs.getString("include_on_deposit_yn"));
			payType.setCentralId(rs.getString("central_id"));
			payType.setSystemRequired(rs.getInt("system_required"));
			payType.setMovedToCloud(0);
			cList.add(payType);
			// dataList.add(rs.getString(x));

		}
		}catch(Exception n) {
			n.printStackTrace();
		}
		return cList;
	}

	private List<Patient> createPatientData(ResultSet rs) {

		List<Patient> cList=new ArrayList<>();
		Patient p =null;
		try {
		while (rs.next()) {
			p= new Patient();
			p.setPatientId(rs.getString("patient_id"));
			p.setFirstName(rs.getString("first_name"));
			p.setLastName(rs.getString("last_name"));
			p.setSalutation(rs.getString("salutation"));
			p.setAddress1(rs.getString("address_1"));
			p.setAddress2(rs.getString("address_2"));
			p.setCity(rs.getString("city"));
			p.setState(rs.getString("state"));
			p.setZipcode(rs.getString("zipcode"));
			p.setHomePhone(rs.getString("home_phone"));
			p.setWorkPhone(rs.getString("work_phone"));
			p.setExt(rs.getString("ext"));
			p.setStatus(rs.getString("status"));
			p.setSex(rs.getString("sex"));
			p.setMaritalStatus(rs.getString("marital_status"));
			p.setResponsiblePartyStatus(rs.getString("responsible_party_status"));
			p.setResponsibleParty(rs.getString("responsible_party"));
			p.setSocialSecurity(rs.getString("social_security"));
			p.setBirthDate(rs.getDate("birth_date"));
			p.setNotes(rs.getString("notes"));
			p.setPreferredDentist(rs.getString("preferred_dentist"));
			p.setPreferredHygienist(rs.getString("preferred_hygienist"));
			p.setRecallFrequency(rs.getInt("recall_frequency"));
			p.setCleaningTime(rs.getInt("cleaning_time"));
			p.setReceiveReCalls(rs.getString("receive_recalls"));
			p.setDiscountId(rs.getInt("discount_id"));
			p.setCurrentBal(rs.getInt("current_bal"));
			p.setThirtyDay(rs.getInt("thirty_day"));
			p.setSixtyDay(rs.getInt("sixty_day"));
			p.setNinetyDay(rs.getInt("ninety_day"));
			p.setContractBalance(rs.getInt("contract_balance"));
			p.setEstimatedInsurance(rs.getInt("estimated_insurance"));
			p.setFirstVisitDate(rs.getDate("first_visit_date"));
			p.setLastDateSeen(rs.getDate("last_date_seen"));
			p.setCancelledAppointments(rs.getInt("cancelled_appointments"));
			p.setChargesMtd(rs.getInt("charges_mtd"));
			p.setCollectionsMtd(rs.getInt("collections_mtd"));
			p.setChargesYtd(rs.getInt("charges_ytd"));
			p.setCollectionsYtd(rs.getInt("collections_ytd"));
			p.setFailedAppointments(rs.getInt("failed_appointments"));
			p.setPrimResponsibleId(rs.getString("prim_responsible_id"));
			p.setPrimRelationship(rs.getString("prim_relationship"));
			p.setPrimEmployerId(rs.getInt("prim_employer_id"));////
			p.setPrimOutstandingBalance(rs.getInt("prim_outstanding_balance"));
			p.setPrimBenefitsRemaining(rs.getInt("prim_benefits_remaining"));
			p.setPrimRemainingDeductible(rs.getInt("prim_remaining_deductible"));
			p.setSecResponsibleId(rs.getString("sec_responsible_id"));
			p.setSecRelationship(rs.getString("sec_relationship"));
			p.setSecEmployerId(rs.getInt("sec_employer_id"));
			p.setSecOutstandingBalance(rs.getInt("sec_outstanding_balance"));
			p.setSecBenefitsRemaining(rs.getInt("sec_benefits_remaining"));
			p.setSecRemainingDeductible(rs.getInt("sec_remaining_deductible"));
			p.setShortNotice(rs.getString("short_notice"));
			p.setPrefersAmpm(rs.getString("prefers_ampm"));
			p.setLastRegularAppointment(rs.getDate("last_regular_appointment"));
			p.setNextRegularAppointment(rs.getDate("next_regular_appointment"));
			p.setLastPreventiveAppointment(rs.getDate("last_preventive_appointment"));
			p.setNextPreventiveAppointment(rs.getDate("next_preventive_appointment"));
			p.setFeeLevelId(rs.getInt("fee_level_id"));
			p.setRecommendedWork(rs.getString("recommended_work"));
			p.setSubmittedTotal(rs.getInt("submitted_total"));
			p.setPrimTotalPaid(rs.getInt("prim_total_paid"));
			p.setSecTotalPaid(rs.getInt("sec_total_paid"));
			p.setPatientStatus(rs.getString("patient_status"));
			p.setPatientStatus(rs.getString("policy_holder_status"));
			p.setNextRecallDate(rs.getDate("next_recall_date"));
			p.setLastRecallDate(rs.getDate("last_recall_date"));
			p.setYtdVisits(rs.getInt("ytd_visits"));
			p.setNextPreventiveApptTime(rs.getTime("next_preventive_appt_time"));
			p.setNextRegularApptTime(rs.getTime("next_regular_appt_time"));
			p.setSchool(rs.getString("school"));
			p.setSchoolCity(rs.getString("school_city"));
			p.setEmploymentStatus(rs.getString("employment_status"));
			p.setEmploymentIdNumber(rs.getString("employment_id_number"));
			p.setStudentStatus(rs.getString("student_status"));
			p.setMedicaidId(rs.getString("medicaid_id"));
			p.setDeathIndicator(rs.getString("death_indicator"));
			p.setSignatureOnFile(rs.getString("signature_on_file"));
			p.setReleaseInfoOnFile(rs.getString("release_info_on_file"));
			p.setCarrierId(rs.getString("carrier_id"));
			p.setEpsdtFlag(rs.getString("epsdt_flag"));
			p.setPatientImageId(rs.getInt("patient_image_id"));
			p.setOtherId(rs.getString("other_id"));
			p.setGuardsId(rs.getString("guards_id"));
			p.setPracticeId(rs.getInt("practice_id"));
			p.setEmailAddress(rs.getString("email_address"));
			p.setDateEntered(rs.getDate("date_entered"));
			p.setLastSoftExam(rs.getDate("last_soft_exam"));
			p.setLastRestorativeExam(rs.getDate("last_restorative_exam"));
			p.setLastTmjExam(rs.getDate("last_tmj_exam"));
			p.setLastOcclExam(rs.getDate("last_occl_exam"));
			p.setLastIntraoralExam(rs.getDate("last_intraoral_exam"));
			p.setLastRadiographyExam(rs.getDate("last_radiography_exam"));
			p.setLastCosmeticExam(rs.getDate("last_cosmetic_exam"));
			p.setLastHeadExam(rs.getDate("last_head_exam"));
			p.setLastHabitsExam(rs.getDate("last_habits_exam"));
			p.setLastGeneralExam(rs.getDate("last_general_exam"));
			p.setLastCancerExam(rs.getDate("last_cancer_exam"));
			p.setLastHistoryExam(rs.getDate("last_history_exam"));
			p.setLastBitewings(rs.getDate("last_bitewings"));
			p.setLastFullMouth(rs.getDate("last_full_mouth"));
			p.setTeethStatus(rs.getString("teeth_status"));
			p.setRecallBatch(rs.getInt("recall_batch"));
			p.setLastPanoDate(rs.getDate("last_pano_date"));
			p.setPharmacyId(rs.getInt("pharmacy_id"));
			p.setNeitherAppointments(rs.getInt("neither_appointments"));
			p.setPreMed(rs.getString("pre_med"));
			p.setRxId(rs.getString("rx_id"));
			p.setMissingTeeth(rs.getString("missing_teeth"));
			p.setReceiveEmail(rs.getString("receive_email"));
			p.setChartId(rs.getString("chart_id"));
			p.setCellPhone(rs.getString("cell_phone"));
			p.setPagerPhone(rs.getString("pager_phone"));
			p.setDriversLicense(rs.getString("drivers_license"));
			p.setHipaaPrivPract(rs.getString("hipaa_priv_pract"));
			p.setHipaaAuthorization(rs.getString("hipaa_authorization"));
			p.setHipaaPrivPractDate(rs.getDate("hipaa_priv_pract_date"));
			p.setHipaaAuthorizationDate(rs.getDate("hipaa_authorization_date"));
			p.setHipaaConsent(rs.getString("hipaa_consent"));
			p.setHipaaConsentDate(rs.getDate("hipaa_consent_date"));
			p.setPrimMemberId(rs.getString("prim_member_id"));
			p.setSecMemberId(rs.getString("sec_member_id"));
			p.setDailyCharges(rs.getInt("daily_charges"));
			p.setDailyCollections(rs.getInt("daily_collections"));
			p.setOrthoPatient(rs.getString("ortho_patient"));
			p.setOrthoMonthsOfTx(rs.getInt("ortho_months_of_tx")); 
			p.setOrthoDateStarted(rs.getDate("ortho_date_started"));
			p.setOrthoInsBillFreq(rs.getInt("ortho_ins_bill_freq"));
		    p.setPreferredName(rs.getString("preferred_name"));
		    p.setMiddleInitial(rs.getString("middle_initial"));
		    p.setSchoolAddress(rs.getString("school_address"));
		    p.setSchoolState(rs.getString("school_state"));
		    p.setSchoolZipcode(rs.getString("school_zipcode"));
		    p.setSecDependCode(rs.getString("sec_depend_code"));
		    p.setCaesylanguage(rs.getInt("caesy_language"));
		    p.setReceivesSms(rs.getString("receives_sms"));
		    p.setMedicaidSeqNum(rs.getString("medicaid_seq_num"));
		    p.setUniversalId(rs.getString("universal_id"));
		    p.setPassword(rs.getString("password"));
		    p.setSecurityQuestionOne(rs.getString("security_question_one"));
		    p.setSecurityQuestionTwo(rs.getString("security_question_two"));
		    p.setSecurityQuestionThree(rs.getString("security_question_three"));
		    p.setSecurityAnswerOne(rs.getString("security_answer_one"));
		    p.setSecurityAnswerTwo(rs.getString("security_answer_two"));
		    p.setSecurityAnswerThree(rs.getString("security_answer_three"));
		    p.setRegistrationVerified(rs.getBoolean("registration_verified"));
		    p.setDolphinID(rs.getString("DolphinID"));
		    p.setPasswordSalt(rs.getString("password_salt"));
		    p.setEncryptedSocialSecurity(rs.getString("encrypted_social_security"));
		    p.setLastMedicalHistory(rs.getDate("last_medical_history"));
			p.setMovedToCloud(0);
			cList.add(p);
			

		}
		}catch(Exception n) {
			n.printStackTrace();
			
		}
		return cList;
	}

}
