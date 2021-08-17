package com.tricon.esdatareplication.eaglesoft;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tricon.esdatareplication.entity.repdb.Appointment;
import com.tricon.esdatareplication.entity.repdb.Chairs;
import com.tricon.esdatareplication.entity.repdb.Employer;
import com.tricon.esdatareplication.entity.repdb.PayType;
import com.tricon.esdatareplication.entity.repdb.PaymentProvider;
import com.tricon.esdatareplication.entity.repdb.PlannedServices;
import com.tricon.esdatareplication.entity.repdb.Provider;
import com.tricon.esdatareplication.entity.repdb.Transactions;
import com.tricon.esdatareplication.entity.repdb.TransactionsDetail;
import com.tricon.esdatareplication.entity.repdb.TreatmentPlanItems;
import com.tricon.esdatareplication.entity.repdb.TreatmentPlans;
import com.tricon.esdatareplication.entity.repdb.Patient;

@Service
public class PrepairESDataFromFromResultSet {

	public List<?> createTableData(ResultSet rs, Class<?> clazz) {

		System.out.println("clazz.getName()-->" + clazz.getName());
		System.out.println("clazz.getName()-->" + clazz.getClass().getName());
		System.out.println(clazz.equals(PayType.class));

		if (clazz.equals(Chairs.class))
			return createChairData(rs);
		else if (clazz.equals(PayType.class))
			return createPayTypeData(rs);
		else if (clazz.equals(Patient.class))
			return createPatientData(rs);
		else if (clazz.equals(Appointment.class))
			return createAppointmentData(rs);
		else if (clazz.equals(Transactions.class))
			return createTransactionsData(rs);
		else if (clazz.equals(TransactionsDetail.class))
			return createTransactionsDetailData(rs);
		else if (clazz.equals(PaymentProvider.class))
			return createPaymentProviderData(rs);
		else if (clazz.equals(PlannedServices.class))
			return createPlannedServicesData(rs);
		else if (clazz.equals(TreatmentPlans.class))
			return createTreatmentPlansData(rs);
		else if (clazz.equals(TreatmentPlanItems.class))
			return createTreatmentPlanItemsData(rs);
		else if (clazz.equals(Employer.class))
			return createEmployerData(rs);
		else if (clazz.equals(Provider.class))
			return createProviderData(rs);
		
		return null;
	}

	private List<Chairs> createChairData(ResultSet rs) {

		List<Chairs> cList = new ArrayList<>();
		Chairs chairs = null;
		try {
			while (rs.next()) {
				chairs = new Chairs();
				// chairs.
				//chairs.setPatientId(rs.getInt("patient_id"));
				chairs.setChairNum(rs.getInt("chair_num"));
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
		} catch (Exception n) {
			n.printStackTrace();
		}
		return cList;
	}

	private List<PayType> createPayTypeData(ResultSet rs) {

		List<PayType> cList = new ArrayList<>();
		PayType payType = null;
		try {
			while (rs.next()) {
				payType = new PayType();
				// chairs.
				payType.setPayTypeId(rs.getInt("paytype_id"));
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
		} catch (Exception n) {
			n.printStackTrace();
		}
		return cList;
	}

	private List<Patient> createPatientData(ResultSet rs) {

		List<Patient> cList = new ArrayList<>();
		Patient p = null;
		try {
			while (rs.next()) {
				p = new Patient();
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
		} catch (Exception n) {
			n.printStackTrace();

		}
		return cList;
	}

	private List<TransactionsDetail> createTransactionsDetailData(ResultSet rs) {

		List<TransactionsDetail> cList = new ArrayList<>();
		TransactionsDetail p = null;
		try {
			while (rs.next()) {
				p =new TransactionsDetail();
				p.setDetailId(rs.getInt("detail_id"));
				p.setTranNum(rs.getInt("tran_num"));
				p.setUserId(rs.getString("user_id"));
				p.setDateEntered(rs.getDate("date_entered"));
				p.setProviderId(rs.getString("provider_id"));
				p.setCollectionsGoTo(rs.getString("collections_go_to"));
				p.setPatientId(rs.getString("patient_id"));
				p.setAmount(rs.getDouble("amount"));
				p.setProviderPracticeId(rs.getInt("provider_practice_id"));
				p.setPatientPracticeId(rs.getInt("patient_practice_id"));
				p.setAppliedTo(rs.getInt("applied_to"));
				p.setStatus(rs.getInt("status"));
				p.setStatusModifier(rs.getInt("status_modifier"));
				p.setPosneg(rs.getInt("posneg"));
				p.setMovedToCloud(0);
				cList.add(p);

			}
		} catch (Exception n) {
			n.printStackTrace();

		}
		return cList;
	}

	private List<Transactions> createTransactionsData(ResultSet rs) {

		List<Transactions> cList = new ArrayList<>();
		Transactions p = null;
		try {
			while (rs.next()) {
				p= new Transactions();
				p.setTranNum(rs.getInt("tran_num"));
				p.setUserId(rs.getString("user_id"));
				p.setType(rs.getString("type"));
				p.setTranDate(rs.getDate("tran_date"));
				p.setPatientId(rs.getString("patient_id"));
				p.setRespPartyId(rs.getString("resp_party_id"));
				p.setAmount(rs.getDouble("amount"));
				p.setServiceCode(rs.getString("service_code"));
				p.setPaytypeId(rs.getInt("paytype_id"));
				p.setSequence(rs.getInt("sequence"));
				p.setProviderId(rs.getString("provider_id"));
				p.setCollectionsGoTo(rs.getString("collections_go_to"));
				p.setStatementNum(rs.getInt("statement_num"));
				p.setOldTooth(rs.getInt("old_tooth"));
				p.setSurface(rs.getString("surface"));
				p.setFee(rs.getDouble("fee"));
				p.setDiscountSurcharge(rs.getDouble("discount_surcharge"));
				p.setTax(rs.getDouble("tax"));
				p.setDescription(rs.getString("description"));
				p.setDefective(rs.getString("defective"));
				p.setImpacts(rs.getString("impacts"));
				p.setStatus(rs.getString("status"));
				p.setAdjustmentType(rs.getInt("adjustment_type"));
				p.setClaimId(rs.getInt("claim_id"));
				p.setEstPrimary(rs.getDouble("est_primary"));
				p.setEstSecondary(rs.getDouble("est_secondary"));
				p.setPaidPrimary(rs.getDouble("paid_primary"));
				p.setPaidSecondary(rs.getDouble("paid_secondary"));
				p.setProviderPracticeId(rs.getInt("provider_practice_id"));
				p.setPatientPracticeId(rs.getInt("patient_practice_id"));
				p.setBulkPaymentNum(rs.getInt("bulk_payment_num"));
				p.setAgingDate(rs.getDate("aging_date"));
				p.setTooth(rs.getString("tooth"));
				p.setLabFee(rs.getDouble("lab_fee"));
				p.setLabFee2(rs.getDouble("lab_fee2"));
				p.setLabCode(rs.getString("lab_code"));
				p.setLabCode2(rs.getString("lab_code2"));
				p.setPreFee(rs.getDouble("pre_fee"));
				p.setStandardFeeId(rs.getInt("standard_fee_id"));
				p.setPracticeId(rs.getInt("practice_id"));
				p.setProcedureTypeCodes(rs.getString("procedure_type_codes"));
				p.setBalance(rs.getDouble("balance"));
				cList.add(p);
			}
		} catch (Exception n) {
			n.printStackTrace();

		}
		return cList;
	}

	private List<TreatmentPlans> createTreatmentPlansData(ResultSet rs) {

		List<TreatmentPlans> cList = new ArrayList<>();
		TreatmentPlans p = null;
		try {
			while (rs.next()) {
				p= new TreatmentPlans();
				p.setTreatmentPlanId(rs.getInt("treatment_plan_id"));
				p.setPatientId(rs.getInt("patient_id"));
				p.setDescription(rs.getString("description"));
				p.setStatus(rs.getString("status"));
				p.setDateEntered(rs.getDate("date_entered"));
				p.setUserId(rs.getString("user_id"));
				p.setDateLastUpdated(rs.getDate("date_last_updated"));
				p.setLastUpdatedBy(rs.getString("last_updated_by"));
				p.setNotes(rs.getString("notes"));
				p.setMovedToCloud(0);
				cList.add(p);

			}
		} catch (Exception n) {
			n.printStackTrace();

		}
		return cList;
	}

	private List<TreatmentPlanItems> createTreatmentPlanItemsData(ResultSet rs) {

		List<TreatmentPlanItems> cList = new ArrayList<>();
		TreatmentPlanItems p = null;
		try {
			while (rs.next()) {
                p= new TreatmentPlanItems();
				p.setTreatmentPlanId(rs.getInt("treatment_plan_id"));
				p.setPatientId(rs.getString("patient_id"));
				p.setLineNumber(rs.getInt("line_number"));
				p.setEstPrimary(rs.getDouble("est_primary"));
				p.setEstSecondary(rs.getDouble("est_secondary"));
				p.setApprovedByInsurance(rs.getString("approved_by_insurance"));
				p.setSubmitYn(rs.getString("submit_yn"));
				p.setClaimId(rs.getInt("claim_id"));
				p.setSortOrder(rs.getInt("sort_order"));
				p.setDiscount(rs.getDouble("discount"));
				p.setApplyDiscount(rs.getString("apply_discount"));
				p.setMovedToCloud(0);
				cList.add(p);

			}
		} catch (Exception n) {
			n.printStackTrace();

		}
		return cList;
	}

	private List<Provider> createProviderData(ResultSet rs) {

		List<Provider> cList = new ArrayList<>();
		Provider p = null;
		try {
			while (rs.next()) {
                p= new Provider();
				p.setProviderId(rs.getInt("provider_id"));
				p.setFirstName(rs.getString("first_name"));
				p.setLastName(rs.getString("last_name"));
				p.setAddress1(rs.getString("address_1"));
				p.setAddress2(rs.getString("address_2"));
				p.setCity(rs.getString("city"));
				p.setState(rs.getString("state"));
				p.setZipcode(rs.getString("zipcode"));
				p.setPhone(rs.getString("phone"));
				p.setSex(rs.getString("sex"));
				p.setBirth_date(rs.getDate("birth_date"));
				p.setHireDate(rs.getDate("hire_date"));
				p.setSocialSecurity(rs.getString("social_security"));
				p.setStatus(rs.getString("status"));
				p.setCollectionsGoTo(rs.getString("collections_go_to"));
				p.setProviderOnInsurance(rs.getString("provider_on_insurance"));
				p.setFederalTaxId(rs.getString("federal_tax_id"));
				p.setLicense(rs.getString("license"));
				p.setDeaRegulationNumber(rs.getString("dea_regulation_number"));
				p.setMedicaid(rs.getString("medicaid"));
				p.setMedicare(rs.getString("medicare"));
				p.setBcbs(rs.getString("bcbs"));
				p.setDelta(rs.getString("delta"));
				p.setNotes(rs.getString("notes"));
				p.setMtdWoStatements(rs.getInt("mtd_wo_statements"));
				p.setYtdWoStatements(rs.getInt("ytd_wo_statements"));
				p.setMtdCharges(rs.getInt("mtd_charges"));
				p.setYtdCharges(rs.getInt("ytd_charges"));
				p.setMtdCollections(rs.getInt("mtd_collections"));
				p.setYtdCollections(rs.getInt("ytd_collections"));
				p.setCalculateProductivity(rs.getString("calculate_productivity"));
				p.setPosition_id(rs.getInt("position_id"));
				p.setAnesthesiaId(rs.getString("anesthesia_id"));
				p.setSpecialty(rs.getString("specialty"));
				p.setPassword(rs.getString("password"));
				p.setLocation(rs.getString("location"));
				p.setMedicaidSpecialty(rs.getString("medicaid_specialty"));
				p.setMedicaidLocator(rs.getString("medicaid_locator"));
				p.setMedicaidGroup(rs.getString("medicaid_group"));
				p.setAssociation(rs.getString("association"));
				p.setCurrentBal(rs.getDouble("current_bal"));
				p.setContractBalance(rs.getDouble("contract_balance"));
				p.setEstimatedInsurance(rs.getDouble("estimated_insurance"));
				p.setAccessBasic(rs.getString("access_basic"));
				p.setAccessAccounting(rs.getString("access_accounting"));
				p.setAccessProductivity(rs.getString("access_productivity"));
				p.setMtdNewPatients(rs.getInt("mtd_new_patients"));
				p.setMtdOtherDebits(rs.getDouble("mtd_other_debits"));
				p.setYtdOtherDebits(rs.getDouble("ytd_other_debits"));
				p.setMtdOtherCredits(rs.getDouble("mtd_other_credits"));
				p.setYtdOtherCredits(rs.getDouble("ytd_other_credits"));
				p.setOtherId(rs.getString("other_id"));
				p.setBillingEntity(rs.getString("billing_entity"));
				p.setBillingEntityLicNo(rs.getString("billing_entity_lic_no"));
				p.setUsePracticeAddress(rs.getString("use_practice_address"));
				p.setPracticeId(rs.getInt("practice_id"));
				p.setBankAccount(rs.getString("bank_account"));
				p.setAccessAppointments(rs.getString("access_appointments"));
				p.setAccessPatients(rs.getString("access_patients"));
				p.setAccessContacts(rs.getString("access_contacts"));
				p.setAccessProvider(rs.getString("access_provider"));
				p.setAccessTxPlan(rs.getString("access_tx_plan"));
				p.setAccessPaymentPlan(rs.getString("access_payment_plan"));
				p.setAccessMassUpdates(rs.getString("access_mass_updates"));
				p.setSiteId(rs.getString("site_id"));
				p.setVoiceId(rs.getString("voice_id"));
				p.setEmail(rs.getString("email"));
				p.setAccessPrescriptions(rs.getString("access_prescriptions"));
				p.setOperatoryAccess(rs.getString("operatory_access"));
				p.setAccessMedical(rs.getString("access_medical"));
				p.setAccessTimeclock(rs.getString("access_timeclock"));
				p.setAccessTimeclockManagement(rs.getString("access_timeclock_management"));
				p.setTimesheetPassword(rs.getString("timesheet_password"));
				p.setOtherId2(rs.getString("other_id_2"));
				p.setOtherId3(rs.getString("other_id_3"));
				p.setOtherId4(rs.getString("other_id_4"));
				p.setOtherId5(rs.getString("other_id_5"));
				p.setAccessLab(rs.getString("access_lab"));
				p.setNationalProvId(rs.getString("national_prov_id"));
				p.setOtherId6(rs.getString("Other_id_6"));
				p.setOtherId7(rs.getString("Other_id_7"));
				p.setOtherId8(rs.getString("Other_id_8"));
				p.setOtherId9(rs.getString("Other_id_9"));
				p.setOtherId10(rs.getString("Other_id_10"));
				p.setOtherId11(rs.getString("Other_id_11"));
				p.setOtherId12(rs.getString("Other_id_12"));
				p.setOtherId13(rs.getString("Other_id_13"));
				p.setOtherId14(rs.getString("Other_id_14"));
				p.setOtherId15(rs.getString("Other_id_15"));
				p.setOtherId16(rs.getString("Other_id_16"));
				p.setOtherId17(rs.getString("Other_id_17"));
				p.setOtherId18(rs.getString("Other_id_18"));
				p.setOtherId19(rs.getString("Other_id_19"));
				p.setOtherId20(rs.getString("Other_id_20"));
				p.setOtherId21(rs.getString("Other_id_21"));
				p.setDailyWoStatements(rs.getInt("daily_wo_statements"));
				p.setDailyCharges(rs.getDouble("daily_charges"));
				p.setDailyCollections(rs.getDouble("daily_collections"));
				p.setDailyNewPatients(rs.getInt("daily_new_patients"));
				p.setDailyOtherDebits(rs.getDouble("daily_other_debits"));
				p.setDailyOtherCredits(rs.getDouble("daily_other_credits"));
				p.setViewDocs(rs.getString("view_docs"));
				p.setAddDocs(rs.getString("add_docs"));
				p.setEditDocs(rs.getString("edit_docs"));
				p.setDeleteDocs(rs.getString("delete_docs"));
				p.setPassPromp1(rs.getString("pass_prompt_1"));
				p.setPassAnswer1(rs.getString("pass_answer_1"));
				p.setPassPrompt2(rs.getString("pass_prompt_2"));
				p.setPassAnswer2(rs.getString("pass_answer_2"));
				p.setPassPrompt3(rs.getString("pass_prompt_3"));
				p.setPassAnswer3(rs.getString("pass_answer_3"));
				p.setPassPromp1(rs.getString("tc_pass_prompt_1"));
				p.setTcPassAnswer1(rs.getString("tc_pass_answer_1"));
				p.setPassPrompt2(rs.getString("tc_pass_prompt_2"));
				p.setTcPassAnswer2(rs.getString("tc_pass_answer_2"));
				p.setTcPassPrompt3(rs.getString("tc_pass_prompt_3"));
				p.setTcPassAnswer3(rs.getString("tc_pass_answer_3"));
				p.setStandardFeeId(rs.getInt("standard_fee_id"));
				p.setAccessSite(rs.getString("access_site"));
				p.setAccessIntellicare(rs.getString("access_intellicare"));
				p.setSecurityProfile(rs.getInt("security_profile"));
				p.setUniversalId(rs.getString("universal_id"));
				p.setProviderColor(rs.getInt("provider_color"));
				p.setLastDetailId(rs.getInt("last_detail_id"));
				p.setClinicianUserName(rs.getString("ClinicianUserName"));
				p.setClinicianPassword(rs.getString("ClinicianPassword"));
				p.setLastLogon(rs.getDate("last_logon"));
				p.setEncryptedSocialSecurity(rs.getString("encrypted_social_security"));
				p.setMovedToCloud(0);
				cList.add(p);
			}
		} catch (Exception n) {
			n.printStackTrace();

		}
		return cList;
	}

	private List<PlannedServices> createPlannedServicesData(ResultSet rs) {

		List<PlannedServices> cList = new ArrayList<>();
		PlannedServices p = null;
		try {
			while (rs.next()) {
				p= new PlannedServices();
				p.setApptGroup(rs.getInt("appt_group"));
				p.setApptId(rs.getInt("appt_id"));
				p.setCompletionDate(rs.getDate("completion_date"));
				p.setCreatedFromUpgrade(rs.getString("created_from_upgrade"));
				p.setDatePlanned(rs.getDate("date_planned"));
				p.setDescription(rs.getString("description"));
				p.setFee(rs.getDouble("fee"));
				p.setLabCode(rs.getString("lab_code"));
				p.setLabCode2(rs.getString("lab_code2"));
				p.setLabFee(rs.getDouble("lab_fee"));
				p.setLabFee2(rs.getDouble("lab_fee2"));
				p.setLineNumber(rs.getInt("line_number"));
				p.setOldTooth(rs.getString("old_tooth"));
				p.setPatientId(rs.getString("patient_id"));
				p.setPreFee(rs.getDouble("pre_fee"));
				p.setProcedureTypeCodes(rs.getString("procedure_type_codes"));
				p.setProviderId(rs.getString("provider_id"));
				p.setSequence(rs.getInt("sequence"));
				p.setServiceCode(rs.getString("service_code"));
				p.setSortOrder(rs.getInt("sort_order"));
				p.setStandardFeeId(rs.getInt("standard_fee_id"));
				p.setStatus(rs.getString("status"));
				p.setStatusDate(rs.getDate("status_date"));
				p.setSurface(rs.getString("surface"));
				p.setTooth(rs.getString("tooth"));
				p.setUnusualRemarks(rs.getString("unusual_remarks"));
				p.setMovedToCloud(0);
				cList.add(p);
			}
		} catch (Exception n) {
			n.printStackTrace();

		}
		return cList;
	}

	private List<PaymentProvider> createPaymentProviderData(ResultSet rs) {

		List<PaymentProvider> cList = new ArrayList<>();
		PaymentProvider p = null;
		try {
			while (rs.next()) {
                p= new PaymentProvider();
				p.setTranNum(rs.getInt("tran_num"));
				p.setProviderId(rs.getString("provider_id"));
				p.setAmount(rs.getDouble("amount"));
				p.setPracticeId(rs.getInt("practiceId"));
				p.setProdProviderId(rs.getString("prod_provider_id"));
				p.setMovedToCloud(0);
				cList.add(p);

			}
		} catch (Exception n) {
			n.printStackTrace();

		}
		return cList;
	}

	private List<Employer> createEmployerData(ResultSet rs) {

		List<Employer> cList = new ArrayList<>();
		Employer p = null;
		try {
			while (rs.next()) {
				p= new Employer();
				p.setEmployerId(rs.getInt("employer_id"));
				p.setName(rs.getString("name"));
				p.setAddress1(rs.getString("address_1"));
				p.setAddress2(rs.getString("address_2"));
				p.setCity(rs.getString("city"));
				p.setState(rs.getString("state"));
				p.setZipCode(rs.getString("zipcode"));
				p.setContact(rs.getString("contact"));
				p.setPhone1(rs.getString("phone1"));
				p.setPhone1ext(rs.getString("phone1ext"));
				p.setPhone2(rs.getString("phone2"));
				p.setPhone2ext(rs.getString("phone2ext"));
				p.setFax(rs.getString("fax"));
				p.setSignatureOnFileYn(rs.getString("signature_on_file_yn"));
				p.setGroupNumber(rs.getString("group_number"));
				p.setGroupName(rs.getString("group_name"));
				p.setInsuranceCompanyId(rs.getInt("insurance_company_id"));
				p.setFormId(rs.getInt("form_id"));
				p.setFeeSchedule(rs.getInt("fee_schedule"));
				p.setMaximumCoverage(rs.getDouble("maximum_coverage"));
				p.setLifetimeMaximumYn(rs.getString("lifetime_maximum_yn"));
				p.setYearlyDeductible(rs.getDouble("yearlyDeductible"));
				p.setLifetimeDeductibleYn(rs.getString("lifetimeDeductible_yn"));
				p.setBeginningMonth(rs.getInt("beginning_month"));
				p.setSubmitElectronically(rs.getString("submit_electronically"));
				p.setMNumberSubmitted(rs.getInt("m_number_submitted"));
				p.setYNumberReceived(rs.getInt("y_number_submitted"));
				p.setMAmountSubmitted(rs.getDouble("m_amount_submitted"));
				p.setYAmountSubmitted(rs.getDouble("y_amount_submitted"));
				p.setMNumberReceived(rs.getInt("m_number_received"));
				p.setYNumberReceived(rs.getInt("y_number_received"));
				p.setMAmountReceived(rs.getDouble("m_amount_received"));
				p.setYAmountReceived(rs.getDouble("y_amount_received"));
				p.setNotes(rs.getString("notes"));
				p.setCarrierType(rs.getString("carrier_type"));
				p.setReceivePaperClaim(rs.getString("receive_paper_claim"));
				p.setEstimateInsurance(rs.getString("estimate_insurance"));
				p.setProviderIdFlag(rs.getString("provider_id_flag"));
				p.setPatientIdFlag(rs.getString("patient_id_flag"));
				p.setPatientSsnFlag(rs.getString("patient_ssn_flag"));
				p.setTrojanId(rs.getString("trojan_id"));
				p.setSecondaryCalculation(rs.getString("secondary_calculation"));
				p.setSecondaryProviderId(rs.getString("secondary_provider_id"));
				p.setBookId(rs.getInt("book_id"));
				p.setStatus(rs.getString("status"));
				p.setCentralId(rs.getString("central_id"));
				p.setDailyNumberSubmitted(rs.getInt("daily_number_submitted"));
				p.setDailyAmountSubmitted(rs.getDouble("daily_amount_submitted"));
				p.setDailyNumberReceived(rs.getInt("daily_number_received"));
				p.setDailyAmountReceived(rs.getDouble("daily_amount_received"));
				p.setBillStandardFee(rs.getString("bill_standard_fee"));
				p.setDoNotTrackYn(rs.getString("do_not_track_yn"));
				p.setShowTaxOnInsClaimYn(rs.getString("show_tax_on_ins_claim_yn"));
				p.setSecondaryFormId(rs.getInt("secondary_form_id"));
				p.setIdTreatingDentist(rs.getString("id_treating_dentist"));
				p.setTrojanMc(rs.getString("trojan_mc"));
				p.setManagedCare(rs.getString("Managed_care"));
				p.setDivisionSectionNo(rs.getString("division_section_no"));
				p.setIdFacilityBy(rs.getString("id_facility_by"));
				p.setAdjustmentType(rs.getInt("adjustment_type"));
				p.setMovedToCloud(0);
				cList.add(p);
			}
		} catch (Exception n) {
			n.printStackTrace();

		}
		return cList;
	}

	private List<Appointment> createAppointmentData(ResultSet rs) {

		List<Appointment> cList = new ArrayList<>();
		Appointment p = null;
		try {
			while (rs.next()) {
				p = new Appointment();
				p.setAppointmentId(rs.getInt("appointment_id"));
				p.setDescription(rs.getString("description"));
				p.setAlldayEvent(rs.getBoolean("allday_event"));
				p.setStartTime(rs.getDate("start_time"));
				p.setEndTime(rs.getDate("end_time"));
				p.setPatientId(rs.getString("patient_id"));
				p.setRecallId(rs.getInt("recall_id"));
				p.setLocationId(rs.getInt("location_id"));
				p.setLocationFreeForm(rs.getString("location_free_form"));
				p.setClassification(rs.getInt("classification"));
				p.setAppointmentTypeId(rs.getInt("appointment_type_id"));
				p.setPrefix(rs.getString("prefix"));
				p.setDollarsScheduled(rs.getDouble("dollars_scheduled"));
				p.setDateAppointed(rs.getDate("date_appointed"));
				p.setDateConfirmed(rs.getDate("date_confirmed"));
				p.setAppointmentNotes(rs.getString("appointment_notes"));
				p.setDeletionNote(rs.getString("deletion_note"));
				p.setSoonerIfPossible(rs.getString("sooner_if_possible"));
				p.setScheduledBy(rs.getString("scheduled_by"));
				p.setModifiedBy(rs.getString("modified_by"));
				p.setAppointmentName(rs.getString("appointment_name"));
				p.setArrivalStatus(rs.getInt("arrival_status"));
				p.setArrivalTime(rs.getDate("arrival_time"));
				p.setInchairTime(rs.getDate("inchair_time"));
				p.setWalkoutTime(rs.getDate("walkout_time"));
				p.setConfirmationStatus(rs.getInt("confirmation_status"));
				p.setConfirmationNote(rs.getString("confirmation_note"));
				p.setAutoConfirmSent(rs.getInt("auto_confirm_sent"));
				p.setRecurrenceId(rs.getInt("recurrence_id"));
				p.setPrivateR(rs.getBoolean("private"));
				p.setPriority(rs.getInt("priority"));
				p.setAppointmentData(rs.getString("appointment_data"));
				cList.add(p);
			}
		} catch (Exception n) {
			n.printStackTrace();

		}
		return cList;
	}
}
