package com.tricon.ruleengine.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tricon.ruleengine.dao.OfficeDao;
import com.tricon.ruleengine.dao.TreatmentValidationDao;
import com.tricon.ruleengine.dto.OfficeDto;
import com.tricon.ruleengine.logger.RuleEngineLogger;
import com.tricon.ruleengine.model.db.EagleSoftDBDetails;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.sheet.ClaimData;
import com.tricon.ruleengine.model.sheet.ClaimDataDetails;
import com.tricon.ruleengine.model.sheet.ClaimDataPatient;
import com.tricon.ruleengine.model.sheet.EagleSoftEmployerMaster;
import com.tricon.ruleengine.model.sheet.EagleSoftFeeShedule;
import com.tricon.ruleengine.model.sheet.EagleSoftPatient;
import com.tricon.ruleengine.model.sheet.EagleSoftPatientWalkHistory;
import com.tricon.ruleengine.model.sheet.EagleSoftPolicyholdeDob;
import com.tricon.ruleengine.model.sheet.IVFTableSheet;
import com.tricon.ruleengine.model.sheet.InsuranceDetail;
import com.tricon.ruleengine.model.sheet.PatientDeductiblewithBenefits;
import com.tricon.ruleengine.model.sheet.PatientPolicyHolder;
import com.tricon.ruleengine.model.sheet.Perio;
import com.tricon.ruleengine.model.sheet.PreferanceFeeSchedule;
import com.tricon.ruleengine.model.sheet.PreferredDentist;
import com.tricon.ruleengine.model.sheet.TreatmentPlan;
import com.tricon.ruleengine.model.sheet.TreatmentPlanDetails;
import com.tricon.ruleengine.model.sheet.TreatmentPlanPatient;
import com.tricon.ruleengine.security.JwtUser;
import com.tricon.ruleengine.service.EagleSoftDBAccessService;
import com.tricon.ruleengine.utils.Constants;
import com.tricon.ruleengine.utils.EagleSoftFetchData;
import com.tricon.ruleengine.utils.EagleSoftQuery;
import com.tricon.ruleengine.utils.EagleSoftQueryObject;

@Service
public class EagleSoftDBAccessServiceImpl implements EagleSoftDBAccessService {

	@Autowired
	@Qualifier("jwtUserDetailsService")
    private UserDetailsService userDetailsService;
	
	@Value("${es.ssl.client.certificate.trustStore}")
	private String trustStore;

	@Value("${es.ssl.client.certificate.keyStore}")
	private String keyStore;

	@Value("${es.ssl.client.password}")
	private String password;

	/** If true, accept any ES server certificate (local dev only). Default false. */
	@Value("${es.ssl.client.trustAll:false}")
	private boolean trustAll;

	static Class<?> clazz = EagleSoftDBAccessServiceImpl.class;

	/** TrustManager that accepts any server certificate. For local dev only when ES server cert is not in truststore. */
	private static final TrustManager[] TRUST_ALL_MANAGERS = new TrustManager[] {
		new X509TrustManager() {
			@Override
			public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {}
			@Override
			public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {}
			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() { return new java.security.cert.X509Certificate[0]; }
		}
	};

	@Autowired
	TreatmentValidationDao tvd;

	@Autowired
	OfficeDao od;

	@Override
	public Map<String, List<?>> getPatientData(String insuranceType,Map<String, List<Object>> ivfMap, EagleSoftDBDetails esDB,
			BufferedWriter bw) {
		// TODO Auto-generated method stub
		EagleSoftFetchData d = new EagleSoftFetchData();
		Map<String, List<?>> returnMap = null;
		RuleEngineLogger.generateLogs(clazz, "Patient Data Start ", Constants.rule_log_debug, bw);

		if (ivfMap != null) {
			List<String> ids = new ArrayList<>();
			for (Map.Entry<String, List<Object>> entry : ivfMap.entrySet()) {
				if (entry.getValue() != null) {

					IVFTableSheet ivfSheet = ((IVFTableSheet) entry.getValue().get(0));
					ids.add(ivfSheet.getPatientId());
					RuleEngineLogger.generateLogs(clazz,
							"IVF -> PatientId: " + ivfSheet.getPatientId() + ", UniqueID: " + ivfSheet.getUniqueID()
									+ ", Name: " + ivfSheet.getPatientName() + ", DOB: " + ivfSheet.getPatientDOB(),
							Constants.rule_log_debug, bw);
				}
			}

		String[] pids = ids.toArray(new String[ids.size()]);
		RuleEngineLogger.generateLogs(clazz, "PatientIds to query ES: " + String.join(",", pids),
				Constants.rule_log_debug, bw);

		EagleSoftQueryObject q = null;
		String queryType;
		if (insuranceType==null) { q= prepairEagleSoftQueryObject(pids, EagleSoftQuery.patient_query_pri,
				EagleSoftQuery.patient_query_CL_COUNT); queryType="pri(null)"; }
		else if (insuranceType!=null && insuranceType.equals(Constants.INSURANCE_TYPE_PRI) || insuranceType.equals("")) { q= prepairEagleSoftQueryObject(pids, EagleSoftQuery.patient_query_pri,
				EagleSoftQuery.patient_query_CL_COUNT); queryType="pri"; }
		else { q= prepairEagleSoftQueryObject(pids, EagleSoftQuery.patient_query_sec,
				EagleSoftQuery.patient_query_CL_COUNT); queryType="sec"; }

		org.apache.logging.log4j.LogManager.getLogger(clazz.getName()).info(
				"[DIAG getPatientData] patientIds=" + String.join(",", pids)
				+ " insuranceType=" + insuranceType + " queryType=" + queryType
				+ " esDB=" + (esDB != null ? esDB.getIpAddress() : "NULL"));

		String data = d.getDataUsingSockets(esDB, q, trustStore, keyStore, password, bw);

		org.apache.logging.log4j.LogManager.getLogger(clazz.getName()).info(
				"[DIAG getPatientData] socket result: "
				+ (data == null ? "NULL (no data returned from ES)"
					: "data.length=" + data.length() + " | raw=" + (data.length() <= 50 ? data : data.substring(0, 50) + "...")));

		// When the INNER JOIN patient query returns "null" the patient has no employer
		// set in EagleSoft (prim/sec_employer_id IS NULL). Fall back to a patient-only
		// query so we still get the patient row; employer-dependent rules will then fail
		// with "employer not found" rather than the misleading "patient not found".
		if ("null".equals(data)) {
			org.apache.logging.log4j.LogManager.getLogger(clazz.getName()).info(
					"[DIAG getPatientData] Primary query returned null for patientIds=" + String.join(",", pids)
					+ " - patient likely has no employer in ES. Trying fallback patient-only query.");
			EagleSoftQueryObject qFallback = prepairEagleSoftQueryObject(
					pids, EagleSoftQuery.patient_query_fallback, EagleSoftQuery.patient_query_CL_COUNT);
			data = d.getDataUsingSockets(esDB, qFallback, trustStore, keyStore, password, bw);
			org.apache.logging.log4j.LogManager.getLogger(clazz.getName()).info(
					"[DIAG getPatientData] fallback result: "
					+ (data == null ? "NULL"
						: "data.length=" + data.length() + " | raw=" + (data.length() <= 100 ? data : data.substring(0, 100) + "...")));
		}

	if (data != null) {
		// Use thread-local formatters to avoid the shared static SimpleDateFormat
		// concurrency bug (SimpleDateFormat is NOT thread-safe).
		SimpleDateFormat fmtOut = new SimpleDateFormat("MM/dd/yyyy");
		SimpleDateFormat fmtIn  = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat fmtInTs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			ObjectMapper map = new ObjectMapper();
			Map<String, Object> cMap = map.readValue(data, new TypeReference<Map<String, Object>>() {
			});

			// cMap is null when EagleSoft returns "null" (zero-row result from socket server).
			if (cMap == null) {
				org.apache.logging.log4j.LogManager.getLogger(clazz.getName()).warn(
						"[DIAG getPatientData] cMap is NULL after JSON parse - ES returned empty/null response for patientIds="
						+ String.join(",", pids) + ". Raw data was: [" + data + "]");
				return returnMap;
			}

			Map<String, List<String>> dataMap = (Map<String, List<String>>) cMap.get("dataMap");

			// dataMap is null when the JSON has no "dataMap" key (unexpected response format).
			if (dataMap == null) {
				org.apache.logging.log4j.LogManager.getLogger(clazz.getName()).warn(
						"[DIAG getPatientData] dataMap is NULL in cMap keys=" + cMap.keySet()
						+ " for patientIds=" + String.join(",", pids));
				return returnMap;
			}

			org.apache.logging.log4j.LogManager.getLogger(clazz.getName()).info(
					"[DIAG getPatientData] dataMap row count=" + dataMap.size()
					+ " | raw snippet=" + (data.length() > 300 ? data.substring(0, 300) : data));

			RuleEngineLogger.generateLogs(clazz, "Patient RAW DATA-" + dataMap.toString(),
					Constants.rule_log_debug, bw);
			List<Object> list = null;
			for (Map.Entry<String, List<String>> entry : dataMap.entrySet()) {
					if (entry.getValue() != null) {
						List<String> des = (List<String>) (entry.getValue());
						EagleSoftPatient pat = new EagleSoftPatient();
						try {
							pat.setPatientId(des.get(0));
							pat.setFirstName(des.get(1));
							pat.setLastName(des.get(2));

							// Parse birth_date with fallback: try yyyy-MM-dd first,
							// then yyyy-MM-dd HH:mm:ss (some ES versions return a timestamp).
							String rawDob = des.get(3);
							String parsedDob = null;
							try {
								parsedDob = fmtOut.format(fmtIn.parse(rawDob));
							} catch (Exception dobEx) {
								try {
									parsedDob = fmtOut.format(fmtInTs.parse(rawDob));
									RuleEngineLogger.generateLogs(clazz,
											"Patient DOB parsed with timestamp format for patientId=" + des.get(0) + ", rawDob=" + rawDob,
											Constants.rule_log_debug, bw);
								} catch (Exception dobEx2) {
									parsedDob = rawDob;
									RuleEngineLogger.generateLogs(clazz,
											"Patient DOB parse failed for patientId=" + des.get(0) + ", rawDob=" + rawDob + " - using raw value. Error: " + dobEx2.getMessage(),
											Constants.rule_log_debug, bw);
								}
							}
							pat.setBirthDate(parsedDob);

						pat.setSocialSecurity(des.get(4));
						pat.setPrimMemberId(des.get(5));
						pat.setStatus(des.get(6));
						pat.setResponsiblePartyStatus(des.get(7));
						pat.setResponsibleParty(des.get(8));
						// Employer columns (9-21) may be null when patient has no employer
						// in EagleSoft (LEFT OUTER JOIN returns null for those columns).
						pat.setMaximumCoverage(safeGet(des, 9));
						pat.setPrimBenefitsRemaining(des.get(10));
						pat.setPrimRemainingDeductible(des.get(11));
						pat.setSecBenefitsRemaining(des.get(12));
						pat.setSecRemainingDeductible(des.get(13));
						pat.setEmployerId(safeGet(des, 14));
						pat.setEmployerName(safeGet(des, 15));
						pat.setFeeScheduleId(safeGet(des, 16));
						pat.setFeeScheduleName(safeGet(des, 17));
						pat.setCovBookHeaderId(safeGet(des, 18));
						pat.setCovBookHeaderName(safeGet(des, 19));
						pat.setInsuranceName(safeGet(des, 20));
						pat.setGroupNumber(safeGet(des, 21));
						pat.setSecMemberId(des.get(22));

						if (pat.getSecMemberId() == null) pat.setSecMemberId("");
						if (pat.getPrimMemberId() == null) pat.setPrimMemberId("");
						if (pat.getEmployerId() == null) pat.setEmployerId("");

							RuleEngineLogger.generateLogs(clazz,
									"[ES Patient Loaded] patientId=" + pat.getPatientId()
											+ ", name=" + pat.getFirstName() + " " + pat.getLastName()
											+ ", dob=" + pat.getBirthDate()
											+ ", employerId=" + pat.getEmployerId()
											+ ", feeScheduleId=" + pat.getFeeScheduleId()
											+ ", primMemberId=" + pat.getPrimMemberId()
											+ ", secMemberId=" + pat.getSecMemberId(),
									Constants.rule_log_debug, bw);

							for (Map.Entry<String, List<Object>> entry2 : ivfMap.entrySet()) {
								if (entry.getValue() != null) {
									IVFTableSheet ivfSheet = ((IVFTableSheet) entry2.getValue().get(0));
									RuleEngineLogger.generateLogs(clazz,
											"[Key Match Attempt] ES patientId=" + pat.getPatientId().trim()
													+ " vs IVF patientId=" + ivfSheet.getPatientId()
													+ " | IVF UniqueID=" + ivfSheet.getUniqueID()
													+ " | IVF Name=" + ivfSheet.getPatientName()
													+ " | IVF DOB=" + ivfSheet.getPatientDOB(),
											Constants.rule_log_debug, bw);
									if (pat.getPatientId().trim().equalsIgnoreCase(ivfSheet.getPatientId().trim())) {
										if (returnMap == null) returnMap = new HashMap<>();
										if (returnMap.containsKey(ivfSheet.getUniqueID())) {
											list = (List<Object>) (List<?>) returnMap.get(ivfSheet.getUniqueID());
											list.add(pat);
										} else {
											list = new ArrayList<>();
											list.add(pat);
											returnMap.put(ivfSheet.getUniqueID(), list);
										}
										RuleEngineLogger.generateLogs(clazz,
												"[Key Match SUCCESS] ES patientId=" + pat.getPatientId().trim()
														+ " stored under key=" + ivfSheet.getUniqueID(),
												Constants.rule_log_debug, bw);
									}
								}
							}
						} catch (Exception rowEx) {
							RuleEngineLogger.generateLogs(clazz,
									"[Patient Row Parse ERROR] row=" + des + " error=" + rowEx.getMessage(),
									Constants.rule_log_debug, bw);
						}
					}
				}

			if (returnMap == null) {
				String msg = "[DIAG getPatientData] returnMap is NULL - no ES patient matched any IVF patient. "
						+ "IVF patient IDs queried: " + String.join(",", ids);
				RuleEngineLogger.generateLogs(clazz, msg, Constants.rule_log_debug, bw);
				org.apache.logging.log4j.LogManager.getLogger(clazz.getName()).warn(msg);
			} else {
				String msg = "[DIAG getPatientData] returnMap keys=" + returnMap.keySet().toString();
				RuleEngineLogger.generateLogs(clazz, msg, Constants.rule_log_debug, bw);
				org.apache.logging.log4j.LogManager.getLogger(clazz.getName()).info(msg);
			}

		} catch (Exception e) {
			RuleEngineLogger.generateLogs(clazz, "[Patient Data FATAL ERROR] " + e.getMessage(),
					Constants.rule_log_debug, bw);
			org.apache.logging.log4j.LogManager.getLogger(clazz.getName()).warn(
					"[DIAG getPatientData] FATAL ERROR parsing response for patientIds="
					+ String.join(",", pids) + ": " + e.getClass().getSimpleName() + " - " + e.getMessage());
		}
	}

		}

		return returnMap;
	}

	@Override
	public Map<String, List<?>> getEmployeeMaster(Map<String, List<EagleSoftPatient>> espatients,
			EagleSoftDBDetails esDB, BufferedWriter bw) {
		// TODO Auto-generated method stub
		EagleSoftFetchData d = new EagleSoftFetchData();
		Map<String, List<?>> returnMap = null;
		RuleEngineLogger.generateLogs(clazz, "Employee Master Start", Constants.rule_log_debug, bw);

		if (espatients != null) {
			List<String> ids = new ArrayList<>();
			for (Map.Entry<String, List<EagleSoftPatient>> entry : espatients.entrySet()) {
				if (entry.getValue() != null) {

					EagleSoftPatient pSheet = ((EagleSoftPatient) entry.getValue().get(0));
					ids.add(pSheet.getEmployerId());
				}
			}

			String[] pids = ids.toArray(new String[ids.size()]);

			EagleSoftQueryObject q = prepairEagleSoftQueryObject(pids, EagleSoftQuery.employeemaster_query,
					EagleSoftQuery.employeemaster_query_CL_COUNT);
			String data = d.getDataUsingSockets(esDB, q, trustStore, keyStore, password, bw);
			if (data != null) {
				EagleSoftEmployerMaster emp = null;
				try {
					ObjectMapper map = new ObjectMapper();
					// Patient patQ = map.readValue(r, Patient.class);
					Map<String, Object> cMap = map.readValue(data, new TypeReference<Map<String, Object>>() {
					});

					RuleEngineLogger.generateLogs(clazz, "Employee MASTER DATA -" + cMap.get("dataMap").toString(),
							Constants.rule_log_debug, bw);
					Map<String, List<String>> dataMap = (Map<String, List<String>>) cMap.get("dataMap");
					List<Object> list = null;
					for (Map.Entry<String, List<String>> entry : dataMap.entrySet()) {
						if (entry.getValue() != null) {
							List<String> des = (List<String>) (entry.getValue());
							emp = new EagleSoftEmployerMaster();

							emp.setEmployerId(des.get(0));
							emp.setEmployerName(des.get(1));
							emp.setEmployerGroupNumber(des.get(2));
							emp.setEmployerMaximumCoverage(des.get(3));
							emp.setServiceTtypeId(des.get(4));
							emp.setServiceTypeDescription(des.get(5));
							emp.setPercentage(des.get(6));
							if (emp.getPercentage() != null && emp.getPercentage().contains(".00")) {
								emp.setPercentage(emp.getPercentage().replace(".00", ""));

							}
							emp.setDeductibleApplies(des.get(7));
							emp.setInsuranceName(des.get(8));
							//
							for (Map.Entry<String, List<EagleSoftPatient>> entry2 : espatients.entrySet()) {
								if (entry.getValue() != null) {

									EagleSoftPatient pats = ((EagleSoftPatient) entry2.getValue().get(0));
									if (emp.getEmployerId().equals(pats.getEmployerId())) {
										if (returnMap == null)
											returnMap = new HashMap<>();
										if (returnMap.containsKey(pats.getEmployerId())) {
											// if the key has already been used,
											// we'll just grab the array list and add the value to it
											list = (List<Object>) (List<?>) returnMap.get(pats.getEmployerId());
											list.add(emp);
										} else {
											// if the key hasn't been used yet,
											// we'll create a new ArrayList<String> object, add the value
											// and put it in the array list with the new key
											list = new ArrayList<>();
											list.add(emp);
											returnMap.put(pats.getEmployerId(), list);
										}
									}
								}
							}

							//

						}
					}

				} catch (Exception e) {
					RuleEngineLogger.generateLogs(clazz, "Employee Master DATA- ERROR- " + e.getMessage(),
							Constants.rule_log_debug, bw);

				}
			}

		}

		return returnMap;
	}

	/*
	 * public static void main(String []a) throws ParseException { String aa="0.00";
	 * System.out.println(aa.contains(".00")); System.out.println(aa.replace(".00",
	 * ""));
	 * Constants.SIMPLE_DATE_FORMAT.format(Constants.SIMPLE_DATE_FORMAT_IVF.parse(
	 * "2009-11-01")); }
	 */
	@Override
	public Map<String, List<?>> getTreatmentPlanData(String[] trids, EagleSoftDBDetails esDB, BufferedWriter bw) {

		Map<String, List<?>> returnMap = null;
		EagleSoftFetchData d = new EagleSoftFetchData();
		RuleEngineLogger.generateLogs(clazz, "Treatment Plan Data Start- ", Constants.rule_log_debug, bw);

		EagleSoftQueryObject q = prepairEagleSoftQueryObject(trids, EagleSoftQuery.treatment_plan_query,
				EagleSoftQuery.treatment_plan_query_CL_COUNT);
		String data = d.getDataUsingSockets(esDB, q, trustStore, keyStore, password, bw);

		if (data != null) {
			try {
				ObjectMapper map = new ObjectMapper();
				// Patient patQ = map.readValue(r, Patient.class);
				Map<String, Object> cMap = map.readValue(data, new TypeReference<Map<String, Object>>() {
				});

				RuleEngineLogger.generateLogs(clazz, "TREATMENT PLAN DATA-" + cMap.get("dataMap").toString(),
						Constants.rule_log_debug, bw);
				Map<String, List<String>> dataMap = (Map<String, List<String>>) cMap.get("dataMap");
				TreatmentPlan tp = null;
				TreatmentPlanPatient patient = null;
				TreatmentPlanDetails treatmentPlanDetails = null;
				List<Object> list = null;
				for (Map.Entry<String, List<String>> entry : dataMap.entrySet()) {
					if (entry.getValue() != null) {
						List<String> des = (List<String>) (entry.getValue());
						tp = new TreatmentPlan();
						patient = new TreatmentPlanPatient();
						treatmentPlanDetails = new TreatmentPlanDetails();

						tp.setApptId(des.get(0));
						tp.setId(des.get(1));
						patient.setId(des.get(2));
						patient.setName(des.get(3));
						patient.setLastName(des.get(4));
						patient.setDob(des.get(5));
						tp.setPatient(patient);
						treatmentPlanDetails.setDateLastUpdated(Constants.SIMPLE_DATE_FORMAT
								.format(Constants.SIMPLE_DATE_FORMAT_IVF.parse((des.get(6)))));
						tp.setStatus(des.get(7));
						treatmentPlanDetails.setEstSecondary(des.get(8));
						treatmentPlanDetails.setDescription(des.get(9));
						tp.setLineItem(des.get(10));
						tp.setServiceCode(des.get(11));
						tp.setDescription(des.get(12));
						tp.setSurface(des.get(13));
						tp.setTooth(des.get(14));
						if (tp.getTooth() == null)
							tp.setTooth("NA");
						else if (tp.getTooth() != null && tp.getTooth().trim().equals(""))
							tp.setTooth("NA");// NA Mean All Tooth.. like cleaning..
						treatmentPlanDetails.setStatus(des.get(15));
						tp.setFee(des.get(16));
						tp.setProviderLastName(des.get(17));
						tp.setEstInsurance(des.get(18));
						tp.setPatientPortion(des.get(19));
						tp.setEstPrimary(des.get(20));
                        tp.setPatientPortionSec(des.get(21));
                        tp.setProviderFirstName(des.get(22));
						tp.setTreatmentPlanDetails(treatmentPlanDetails);

						if (returnMap == null)
							returnMap = new HashMap<>();
						if (returnMap.containsKey(tp.getId())) {
							// if the key has already been used,
							// we'll just grab the array list and add the value to it
							list = (List<Object>) (List<?>) returnMap.get(tp.getId());
							list.add(tp);
						} else {
							// if the key hasn't been used yet,
							// we'll create a new ArrayList<String> object, add the value
							// and put it in the array list with the new key
							list = new ArrayList<>();
							list.add(tp);
							returnMap.put(tp.getId(), list);
						}

					}
				}

			} catch (Exception e) {
				RuleEngineLogger.generateLogs(clazz, "Treatment Plan DATA- ERROR- " + e.getMessage(),
						Constants.rule_log_debug, bw);

			}
		}
		// TODO Auto-generated method stub
		return returnMap;
	}

	@Override
	public Map<String, List<?>> getClaimData(String[] trids, EagleSoftDBDetails esDB, BufferedWriter bw) {

		Map<String, List<?>> returnMap = null;
		EagleSoftFetchData d = new EagleSoftFetchData();
		RuleEngineLogger.generateLogs(clazz, "Claim Data DATA- START- ", Constants.rule_log_debug, bw);

		EagleSoftQueryObject q = prepairEagleSoftQueryObject(trids, EagleSoftQuery.claim_query,
				EagleSoftQuery.claim_query_CL_COUNT);
		String data = d.getDataUsingSockets(esDB, q, trustStore, keyStore, password, bw);

		if (data != null) {
			try {
				ObjectMapper map = new ObjectMapper();
				// Patient patQ = map.readValue(r, Patient.class);
				Map<String, Object> cMap = map.readValue(data, new TypeReference<Map<String, Object>>() {
				});

				RuleEngineLogger.generateLogs(clazz, "Claim Data -" + cMap.get("dataMap").toString(),
						Constants.rule_log_debug, bw);
				Map<String, List<String>> dataMap = (Map<String, List<String>>) cMap.get("dataMap");
				ClaimData tp = null;
				ClaimDataPatient patient = null;
                ClaimDataDetails details = null;
				List<Object> list = null;
				for (Map.Entry<String, List<String>> entry : dataMap.entrySet()) {
					if (entry.getValue() != null) {
						List<String> des = (List<String>) (entry.getValue());
						tp = new ClaimData();
						patient = new ClaimDataPatient();
						details = new ClaimDataDetails();

						tp.setApptId(des.get(0));
						tp.setId(des.get(1));
						patient.setId(des.get(2));
						patient.setName(des.get(3));
						patient.setLastName(des.get(4));
						patient.setDob(des.get(5));
						tp.setPatient(patient);
						details.setDateLastUpdated(Constants.SIMPLE_DATE_FORMAT
								.format(Constants.SIMPLE_DATE_FORMAT_IVF.parse((des.get(6)))));//This date is issue ..
						tp.setStatus(des.get(7));
						details.setEstSecondary(des.get(8));
						details.setDescription(des.get(9));
						tp.setLineItem(des.get(10));
						tp.setServiceCode(des.get(11));
						tp.setDescription(des.get(12));
						tp.setSurface(des.get(13));
						tp.setTooth(des.get(14));
						if (tp.getTooth() == null)
							tp.setTooth("NA");
						else if (tp.getTooth() != null && tp.getTooth().trim().equals(""))
							tp.setTooth("NA");// NA Mean All Tooth.. like cleaning..
						details.setStatus(des.get(15));
						tp.setFee(des.get(16));
						tp.setProviderLastName(des.get(17));
						tp.setEstInsurance(des.get(18));
						tp.setPatientPortion(des.get(19));
						tp.setEstPrimary(des.get(20));
						tp.setPatientPortionSec(des.get(21));
						tp.setProviderFirstName(des.get(22));
						tp.setDetails(details);

						if (returnMap == null)
							returnMap = new HashMap<>();
						if (returnMap.containsKey(tp.getId())) {
							// if the key has already been used,
							// we'll just grab the array list and add the value to it
							list = (List<Object>) (List<?>) returnMap.get(tp.getId());
							list.add(tp);
						} else {
							// if the key hasn't been used yet,
							// we'll create a new ArrayList<String> object, add the value
							// and put it in the array list with the new key
							list = new ArrayList<>();
							list.add(tp);
							returnMap.put(tp.getId(), list);
						}

					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				RuleEngineLogger.generateLogs(clazz, "Claim Data DATA- ERROR- " + e.getMessage(),
						Constants.rule_log_debug, bw);

			}
		}
		// TODO Auto-generated method stub
		return returnMap;
	}
	//
	@Override
	public Map<String, List<?>> getFeeScheduleData(Map<String, List<EagleSoftPatient>> espatients,
			EagleSoftDBDetails esDB, BufferedWriter bw) {
		// TODO Auto-generated method stub
		EagleSoftFetchData d = new EagleSoftFetchData();
		Map<String, List<?>> returnMap = null;
		RuleEngineLogger.generateLogs(clazz, "FEE Schedule DATA START FETCH DATA", Constants.rule_log_debug, bw);

		if (espatients != null) {
			List<String> ids = new ArrayList<>();
			for (Map.Entry<String, List<EagleSoftPatient>> entry : espatients.entrySet()) {
				if (entry.getValue() != null) {

					EagleSoftPatient pSheet = ((EagleSoftPatient) entry.getValue().get(0));
					if (pSheet.getFeeScheduleId()!=null) ids.add(pSheet.getFeeScheduleId());
				}
			}

			String[] pids = ids.toArray(new String[ids.size()]);

			EagleSoftQueryObject q = prepairEagleSoftQueryObject(pids, EagleSoftQuery.feeShedule_query,
					EagleSoftQuery.feeShedule_query_CL_COUNT);
			String data = d.getDataUsingSockets(esDB, q, trustStore, keyStore, password, bw);
			if (data != null) {
				EagleSoftFeeShedule esfeee = null;
				try {
					ObjectMapper map = new ObjectMapper();
					// Patient patQ = map.readValue(r, Patient.class);
					Map<String, Object> cMap = map.readValue(data, new TypeReference<Map<String, Object>>() {
					});

					RuleEngineLogger.generateLogs(clazz, "FEE Schedule DATA-" + cMap.get("dataMap").toString(),
							Constants.rule_log_debug, bw);
					Map<String, List<String>> dataMap = (Map<String, List<String>>) cMap.get("dataMap");
					List<Object> list = null;
					for (Map.Entry<String, List<String>> entry : dataMap.entrySet()) {
						if (entry.getValue() != null) {
							List<String> des = (List<String>) (entry.getValue());
							esfeee = new EagleSoftFeeShedule();

							esfeee.setFeeId(des.get(0));
							esfeee.setName(des.get(1));
							esfeee.setFeesServiceCode(des.get(2));
							esfeee.setFeesFee(des.get(3));

							//
							for (Map.Entry<String, List<EagleSoftPatient>> entry2 : espatients.entrySet()) {
								if (entry.getValue() != null) {

									EagleSoftPatient pats = ((EagleSoftPatient) entry2.getValue().get(0));
									if (esfeee.getFeeId().equals(pats.getFeeScheduleId())) {
										if (returnMap == null)
											returnMap = new HashMap<>();
										if (returnMap.containsKey(pats.getFeeScheduleId())) {
											// if the key has already been used,
											// we'll just grab the array list and add the value to it
											list = (List<Object>) (List<?>) returnMap.get(pats.getFeeScheduleId());
											list.add(esfeee);
										} else {
											// if the key hasn't been used yet,
											// we'll create a new ArrayList<String> object, add the value
											// and put it in the array list with the new key
											list = new ArrayList<>();
											list.add(esfeee);
											returnMap.put(pats.getFeeScheduleId(), list);
										}
									}
								}
							}

							//

						}
					}

				} catch (Exception e) {
					RuleEngineLogger.generateLogs(clazz, "FEE Schedule DATA- ERROR- " + e.getMessage(),
							Constants.rule_log_debug, bw);
				}
			}

		}

		return returnMap;
	}

	@Override
	public List<TreatmentPlan> getTreatmentPlanDataByPatient(String patientId, EagleSoftDBDetails esDB,
			BufferedWriter bw) {
		// TODO Auto-generated method stub
		EagleSoftFetchData d = new EagleSoftFetchData();
		List<TreatmentPlan> list = null;
		RuleEngineLogger.generateLogs(clazz, " Treatment Plan START FETCH DATA for patient-" + patientId,
				Constants.rule_log_debug, bw);

		EagleSoftQueryObject q = prepairEagleSoftQueryObject(new String[] { patientId },
				EagleSoftQuery.treatment_plan_by_pat_query, EagleSoftQuery.treatment_plan_by_pat_query_CL_COUNT);
		String data = d.getDataUsingSockets(esDB, q, trustStore, keyStore, password, bw);
		if (data != null) {
			try {
				ObjectMapper map = new ObjectMapper();
				// Patient patQ = map.readValue(r, Patient.class);
				Map<String, Object> cMap = map.readValue(data, new TypeReference<Map<String, Object>>() {
				});

				RuleEngineLogger.generateLogs(clazz, "TREATMENT PLAN DATA-" + cMap.get("dataMap").toString(),
						Constants.rule_log_debug, bw);
				Map<String, List<String>> dataMap = (Map<String, List<String>>) cMap.get("dataMap");
				TreatmentPlan tp = null;
				TreatmentPlanPatient patient = null;
				TreatmentPlanDetails treatmentPlanDetails = null;
				list = new ArrayList<>();
				for (Map.Entry<String, List<String>> entry : dataMap.entrySet()) {
					if (entry.getValue() != null) {
						List<String> des = (List<String>) (entry.getValue());
						tp = new TreatmentPlan();
						patient = new TreatmentPlanPatient();
						treatmentPlanDetails = new TreatmentPlanDetails();

						tp.setApptId(des.get(0));
						tp.setId(des.get(1));
						patient.setId(des.get(2));
						patient.setName(des.get(3));
						patient.setLastName(des.get(4));
						patient.setDob(des.get(5));
						tp.setPatient(patient);
						treatmentPlanDetails.setDateLastUpdated(Constants.SIMPLE_DATE_FORMAT
								.format(Constants.SIMPLE_DATE_FORMAT_IVF.parse((des.get(6)))));
						tp.setStatus(des.get(7));
						treatmentPlanDetails.setEstSecondary(des.get(8));
						treatmentPlanDetails.setDescription(des.get(9));
						tp.setLineItem(des.get(10));
						tp.setServiceCode(des.get(11));
						tp.setDescription(des.get(12));
						tp.setSurface(des.get(13));
						tp.setTooth(des.get(14));
						if (tp.getTooth() == null)
							tp.setTooth("NA");
						else if (tp.getTooth() != null && tp.getTooth().trim().equals(""))
							tp.setTooth("NA");// NA Mean All Tooth.. like cleaning..
						treatmentPlanDetails.setStatus(des.get(15));
						tp.setFee(des.get(16));
						tp.setProviderLastName(des.get(17));
						tp.setEstInsurance(des.get(18));
						tp.setPatientPortion(des.get(19));
						tp.setEstPrimary(des.get(20));
						tp.setPatientPortionSec(des.get(21));
						tp.setTreatmentPlanDetails(treatmentPlanDetails);
						list.add(tp);
					}

				}

			} catch (Exception e) {
				RuleEngineLogger.generateLogs(clazz, "Treatment Plan DATA- ERROR- " + e.getMessage(),
						Constants.rule_log_debug, bw);

			}
		}
		return list;
	}

	@Override
	public List<ClaimData> getClaimDataByPatient(String patientId, EagleSoftDBDetails esDB,
			BufferedWriter bw) {
		// TODO Auto-generated method stub
		EagleSoftFetchData d = new EagleSoftFetchData();
		List<ClaimData> list = null;
		RuleEngineLogger.generateLogs(clazz, " Claim START FETCH DATA for patient-" + patientId,
				Constants.rule_log_debug, bw);

		EagleSoftQueryObject q = prepairEagleSoftQueryObject(new String[] { patientId },
				EagleSoftQuery.claim_by_pat_query, EagleSoftQuery.claim_by_pat_query_CL_COUNT);
		String data = d.getDataUsingSockets(esDB, q, trustStore, keyStore, password, bw);
		if (data != null) {
			try {
				ObjectMapper map = new ObjectMapper();
				// Patient patQ = map.readValue(r, Patient.class);
				Map<String, Object> cMap = map.readValue(data, new TypeReference<Map<String, Object>>() {
				});

				RuleEngineLogger.generateLogs(clazz, "Claim Patient DATA-" + cMap.get("dataMap").toString(),
						Constants.rule_log_debug, bw);
				Map<String, List<String>> dataMap = (Map<String, List<String>>) cMap.get("dataMap");
				ClaimData tp = null;
				ClaimDataPatient patient = null;
                ClaimDataDetails details = null;
				list = new ArrayList<>();
				for (Map.Entry<String, List<String>> entry : dataMap.entrySet()) {
					if (entry.getValue() != null) {
						List<String> des = (List<String>) (entry.getValue());
						tp = new ClaimData();
						patient = new ClaimDataPatient();
						details = new ClaimDataDetails();

						tp.setApptId(des.get(0));
						tp.setId(des.get(1));
						patient.setId(des.get(2));
						patient.setName(des.get(3));
						patient.setLastName(des.get(4));
						patient.setDob(des.get(5));
						tp.setPatient(patient);
						details.setDateLastUpdated(Constants.SIMPLE_DATE_FORMAT
								.format(Constants.SIMPLE_DATE_FORMAT_IVF.parse((des.get(6)))));
						tp.setStatus(des.get(7));
						details.setEstSecondary(des.get(8));
						details.setDescription(des.get(9));
						tp.setLineItem(des.get(10));
						tp.setServiceCode(des.get(11));
						tp.setDescription(des.get(12));
						tp.setSurface(des.get(13));
						tp.setTooth(des.get(14));
						if (tp.getTooth() == null)
							tp.setTooth("NA");
						else if (tp.getTooth() != null && tp.getTooth().trim().equals(""))
							tp.setTooth("NA");// NA Mean All Tooth.. like cleaning..
						details.setStatus(des.get(15));
						tp.setFee(des.get(16));
						tp.setProviderLastName(des.get(17));
						tp.setEstInsurance(des.get(18));
						tp.setPatientPortion(des.get(19));
						tp.setEstPrimary(des.get(20));
						tp.setPatientPortionSec(des.get(21));
						tp.setDetails(details);
						list.add(tp);
					}

				}

			} catch (Exception e) {
				RuleEngineLogger.generateLogs(clazz, "Treatment Plan DATA- ERROR- " + e.getMessage(),
						Constants.rule_log_debug, bw);

			}
		}
		return list;
	}

	@Override
	public LinkedHashMap<String, List<String>> getGoogleReportData(String query, String ids, int columnCount,
			EagleSoftDBDetails esDB, BufferedWriter bw) {
		EagleSoftFetchData d = new EagleSoftFetchData();
		LinkedHashMap<String, List<String>> dataMap = null;
		EagleSoftQueryObject q = null;
		if (ids != null)
			q = prepairEagleSoftQueryObject(ids.split(","), query, columnCount);
		else
			q = prepairEagleSoftQueryObject(null, query, columnCount);
		try {
        RuleEngineLogger.generateLogs(clazz, "Google Report Query-" +esDB.getOffice().getName(),
					Constants.rule_log_debug, bw);
		}catch (Exception e) {
			// TODO: handle exception
		}
        RuleEngineLogger.generateLogs(clazz, "Google Report Query-" +query+"--"+ids,
				Constants.rule_log_debug, bw);
		String data = d.getDataUsingSockets(esDB, q, trustStore, keyStore, password, bw);
		if (data != null) {
			try {
				ObjectMapper map = new ObjectMapper();
				// Patient patQ = map.readValue(r, Patient.class);
				Map<String, Object> cMap = map.readValue(data, new TypeReference<Map<String, Object>>() {
				});
 
	          RuleEngineLogger.generateLogs(clazz, "Google Report Query-" + cMap.get("dataMap").toString(),
						Constants.rule_log_debug, bw);
				dataMap = (LinkedHashMap<String, List<String>>) cMap.get("dataMap");
				/*
				 * for (Map.Entry<String, List<String>> entry : dataMap.entrySet()) { if
				 * (entry.getValue() != null) { List<String> des = (List<String>)
				 * (entry.getValue());
				 * 
				 * 
				 * 
				 * } }
				 */
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return dataMap;

	}
	
	
	@Override
	public Map<String, List<?>> getPatientHistoryES(Map<String, List<Object>> ivfMap, EagleSoftDBDetails esDB,String dateString,int months,
			BufferedWriter bw) {
		// TODO Auto-generated method stub
		EagleSoftFetchData d = new EagleSoftFetchData();
		Map<String, List<?>> returnMap = null;
		RuleEngineLogger.generateLogs(clazz, "Patient Data Start ", Constants.rule_log_debug, bw);

		if (ivfMap != null) {
			List<String> ids = new ArrayList<>();
			for (Map.Entry<String, List<Object>> entry : ivfMap.entrySet()) {
				if (entry.getValue() != null) {

					IVFTableSheet ivfSheet = ((IVFTableSheet) entry.getValue().get(0));
					ids.add(ivfSheet.getPatientId());
				}
			}

			String[] pids = ids.toArray(new String[ids.size()]);

			EagleSoftQueryObject q = prepairEagleSoftQueryObjectPatHis(pids, EagleSoftQuery.patient_history_by_months,
					EagleSoftQuery.patient_history_by_months_CL_COUNT,dateString,months);
			String data = d.getDataUsingSockets(esDB, q, trustStore, keyStore, password, bw);
			if (data != null) {
				EagleSoftPatientWalkHistory pat = null;
				try {
					ObjectMapper map = new ObjectMapper();
					// Patient patQ = map.readValue(r, Patient.class);
					Map<String, Object> cMap = map.readValue(data, new TypeReference<Map<String, Object>>() {
					});

					RuleEngineLogger.generateLogs(clazz, "Patient DATA-" + cMap.get("dataMap").toString(),
							Constants.rule_log_debug, bw);
					Map<String, List<String>> dataMap = (Map<String, List<String>>) cMap.get("dataMap");
					List<Object> list = null;
					for (Map.Entry<String, List<String>> entry : dataMap.entrySet()) {
						if (entry.getValue() != null) {
							List<String> des = (List<String>) (entry.getValue());
							pat = new EagleSoftPatientWalkHistory();

							pat.setStatementNumber(des.get(0));
							pat.setPatientId(des.get(1));
							pat.setTransDate(Constants.SIMPLE_DATE_FORMAT
									.format(Constants.SIMPLE_DATE_FORMAT_IVF.parse((des.get(2)))));
							pat.setServiceCode(des.get(3));
							pat.setProviderId(des.get(4));
							pat.setOldTooth(des.get(5));
							pat.setSurface(des.get(6));
							pat.setFees(des.get(7));
							pat.setFirstNameP(des.get(8));
							pat.setLastNameP(des.get(9));
						
							//
							for (Map.Entry<String, List<Object>> entry2 : ivfMap.entrySet()) {
								if (entry.getValue() != null) {

									IVFTableSheet ivfSheet = ((IVFTableSheet) entry2.getValue().get(0));
									if ((pat.getPatientId().trim().equalsIgnoreCase(ivfSheet.getPatientId().trim()))) {
										if (returnMap == null)
											returnMap = new HashMap<>();
										if (returnMap.containsKey(ivfSheet.getUniqueID())) {
											// if the key has already been used,
											// we'll just grab the array list and add the value to it
											list = (List<Object>) (List<?>) returnMap.get(ivfSheet.getUniqueID());
											list.add(pat);
										} else {
											// if the key hasn't been used yet,
											// we'll create a new ArrayList<String> object, add the value
											// and put it in the array list with the new key
											list = new ArrayList<>();
											list.add(pat);
											returnMap.put(ivfSheet.getUniqueID(), list);
										}
									}
								}
							}

							//

						}
					}

				} catch (Exception e) {
					RuleEngineLogger.generateLogs(clazz, " Patient History Data- ERROR- " + e.getMessage(),
							Constants.rule_log_debug, bw);
				}
			}

		}

		return returnMap;
	}

	

	/**
	 * Never send trids as null send empty
	 * 
	 * @param trids
	 * @param query
	 * @param columnCount
	 * @return
	 */
	private EagleSoftQueryObject prepairEagleSoftQueryObject(String[] ids, String query, int columnCount) {

		String rep = "";
		String comma = "";
		String id = "";

		if (ids != null) {
			for (String trid : ids) {
				// Trim whitespace so IDs like "11873 " don't silently miss rows in ES.
				String trimmed = trid != null ? trid.trim() : "";
				rep = rep + comma + "?";
				id = id + comma + trimmed;
				comma = ",";
			}
		}
		query = query.replace(EagleSoftQuery.contstant_REP, rep);
		EagleSoftQueryObject o = new EagleSoftQueryObject();
		o.setColumnCount(columnCount);
		o.setIds(id);
		if (ids != null)
			o.setPrepStCount(ids.length);
		else
			o.setPrepStCount(0);
		o.setQuery(query);

		return o;
	}

	/**
	 * Never send trids as null send empty
	 * 
	 * @param trids
	 * @param query
	 * @param columnCount
	 * @return
	 */
	private EagleSoftQueryObject prepairEagleSoftQueryObjectPatHis(String[] ids, String query, int columnCount,String dateString,int months) {

		String rep = "";
		String comma = "";
		String id = "";

		if (ids != null) {
			for (String trid : ids) {
				String trimmed = trid != null ? trid.trim() : "";
				rep = rep + comma + "?";
				id = id + comma + trimmed;
				comma = ",";
			}
		}
		query = query.replace(EagleSoftQuery.contstant_REP, rep);
		query = query.replaceAll(EagleSoftQuery.contstant_REP_DATE, dateString);
		query = query.replaceAll(EagleSoftQuery.contstant_REP_MONTH, months+"");
				
		EagleSoftQueryObject o = new EagleSoftQueryObject();
		o.setColumnCount(columnCount);
		o.setIds(id);
		if (ids != null)
			o.setPrepStCount(ids.length);
		else
			o.setPrepStCount(0);
		o.setQuery(query);

		return o;
	}

	@Override
	public void setUpSSLCertificates() {
		if (trustAll) {
			// Local dev: accept any ES server certificate (e.g. server uses different cert than client truststore).
			try {
				SSLContext ctx = SSLContext.getInstance("TLS");
				ctx.init(null, TRUST_ALL_MANAGERS, null);
				EagleSoftFetchData.setESSSLSocketFactory(ctx.getSocketFactory());
				RuleEngineLogger.generateLogs(clazz, "ES SSL using trust-all (es.ssl.client.trustAll=true). Do not use in production.", Constants.rule_log_debug, null);
			} catch (Exception e) {
				RuleEngineLogger.generateLogs(clazz, "ES SSL trust-all context failed: " + e.getMessage(), Constants.rule_log_debug, null);
				EagleSoftFetchData.setESSSLSocketFactory(null);
			}
			return;
		}

		File trustStoreFile = new File(trustStore);
		File keyStoreFile = new File(keyStore);
		if (!trustStoreFile.canRead() || !keyStoreFile.canRead()) {
			RuleEngineLogger.generateLogs(clazz,
					"ES SSL certs missing or unreadable. trustStore=" + trustStore + " (exists=" + trustStoreFile.exists() + "), keyStore=" + keyStore
							+ " (exists=" + keyStoreFile.exists() + "). Run rule_engine/scripts/generate-es-ssl-certs.sh and mount certs/ into the container at /opt/project/tricon/client.",
					Constants.rule_log_debug, null);
			EagleSoftFetchData.setESSSLSocketFactory(null);
			return;
		}
		System.setProperty("javax.net.ssl.trustStore", trustStore);
		System.setProperty("javax.net.ssl.keyStore", keyStore);
		System.setProperty("javax.net.ssl.keyStorePassword", password);

		// Build an explicit SSLContext from our truststore/keystore so ES connections use it.
		// The JVM default SSLContext is created once and may have an empty truststore if it was
		// initialized before these properties were set, causing "trustAnchors parameter must be non-empty".
		try {
			char[] pass = password != null ? password.toCharArray() : null;
			KeyStore ts = KeyStore.getInstance(KeyStore.getDefaultType());
			try (FileInputStream fis = new FileInputStream(trustStoreFile)) {
				ts.load(fis, pass);
			}
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init(ts);

			KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
			try (FileInputStream fis = new FileInputStream(keyStoreFile)) {
				ks.load(fis, pass);
			}
			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(ks, pass);

			SSLContext ctx = SSLContext.getInstance("TLS");
			ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
			EagleSoftFetchData.setESSSLSocketFactory(ctx.getSocketFactory());
		} catch (Exception e) {
			RuleEngineLogger.generateLogs(clazz,
					"ES SSL context creation failed: " + e.getClass().getSimpleName() + " - " + e.getMessage() + ". ES connections may fail.",
					Constants.rule_log_debug, null);
			EagleSoftFetchData.setESSSLSocketFactory(null);
		}
	}

	@Override
	public String[] doDiagnosticCheck(String officeUuid,String companyUUid) {
		// TODO Auto-generated method stub
		Office office=null;
		if (companyUUid!=null) {
			 office = od.getOfficeByUuid(officeUuid,companyUUid);
		}else {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails)principal).getUsername());
		JwtUser user = (JwtUser) userDetails;
		
		 office = od.getOfficeByUuid(officeUuid,user.getCompany().getUuid());
		}
		String[] ret = new String[3];
		ret[2] = office.getName();
		EagleSoftDBDetails esDB = tvd.getESDBDetailsByOffice(office);
		if (esDB != null) {
			ret[1] = esDB.getIpAddress();

			EagleSoftFetchData d = new EagleSoftFetchData();
			Socket socket = d.getConnectionToES(esDB,null);
			if (socket != null) {
				d.closeConnectionToES(socket);
				ret[0] = Constants.socketworkingFine;
			} else {
				ret[0] = Constants.socketnotworkingFine;
			}
		} else {
			ret[1] = "No IP configured.";
			ret[0] = Constants.socketnotworkingFine;

		}
		return ret;
	}

	@Override
	public List<String[]> doDiagnosticCheck() {
		// TODO Auto-generated method stub

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails)principal).getUsername());
		JwtUser user = (JwtUser) userDetails;

		
		Optional<List<OfficeDto>> offices = od.getAllOffices(user.getCompany().getUuid());
		List<String[]> rList = new ArrayList<>();
		String[] ret = null;
		if (offices.isPresent() && offices.get() != null) {
			List<OfficeDto> dtoList = offices.get();
			for (OfficeDto dto : dtoList) {
				ret = new String[3];
				
				ret[2] = dto.getName();
				EagleSoftDBDetails esDB = tvd.getESDBDetailsByOffice(od.getOfficeByName(dto.getName(),user.getCompany().getUuid()));
				if (esDB != null) {
					ret[1] = esDB.getIpAddress();

					EagleSoftFetchData d = new EagleSoftFetchData();
					Socket socket = d.getConnectionToES(esDB,null);
					if (socket != null) {
						d.closeConnectionToES(socket);
						ret[0] = Constants.socketworkingFine;
					} else {
						ret[0] = Constants.socketnotworkingFine;
					}
				} else {
					ret[1] = "No IP configured.";
					ret[0] = Constants.socketnotworkingFine;

				}
				rList.add(ret);
				
			}
		}
		return rList;
	}
	
	@Override
	public Map<String, List<?>> getPerioDataForPatients(Map<String, List<Object>> ivfMap,EagleSoftDBDetails esDB,BufferedWriter bw){

		// TODO Auto-generated method stub
		EagleSoftFetchData d = new EagleSoftFetchData();
		Map<String, List<?>> returnMap = null;
		RuleEngineLogger.generateLogs(clazz, "Perio Start", Constants.rule_log_debug, bw);

		if (ivfMap != null) {
			List<String> ids = new ArrayList<>();
			for (Map.Entry<String, List<Object>> entry : ivfMap.entrySet()) {
				if (entry.getValue() != null) {

					IVFTableSheet ivfSheet = ((IVFTableSheet) entry.getValue().get(0));
					ids.add(ivfSheet.getPatientId());
				}
			}

			String[] pids = ids.toArray(new String[ids.size()]);

			EagleSoftQueryObject q = prepairEagleSoftQueryObject(pids, EagleSoftQuery.perio_query,
					EagleSoftQuery.perio_query_CL_COUNT);
			String data = d.getDataUsingSockets(esDB, q, trustStore, keyStore, password, bw);
			if (data != null) {
				Perio perio = null;
				try {
					ObjectMapper map = new ObjectMapper();
					// Patient patQ = map.readValue(r, Patient.class);
					Map<String, Object> cMap = map.readValue(data, new TypeReference<Map<String, Object>>() {
					});

					RuleEngineLogger.generateLogs(clazz, "Perio DATA-" + cMap.get("dataMap").toString(),
							Constants.rule_log_debug, bw);
					Map<String, List<String>> dataMap = (Map<String, List<String>>) cMap.get("dataMap");
					List<Object> list = null;
					for (Map.Entry<String, List<String>> entry : dataMap.entrySet()) {
						if (entry.getValue() != null) {
							List<String> des = (List<String>) (entry.getValue());
							perio = new Perio();

							perio.setPatientId(des.get(0));
							perio.setDateEntered(des.get(1));
							perio.setProviderId(des.get(2));
							perio.setTooth1(decodeUnicode(des.get(3)));
							perio.setTooth2(decodeUnicode(des.get(4)));
							perio.setTooth3(decodeUnicode(des.get(5)));
							perio.setTooth4(decodeUnicode(des.get(6)));
							perio.setTooth5(decodeUnicode(des.get(7)));
							perio.setTooth6(decodeUnicode(des.get(8)));
							perio.setTooth7(decodeUnicode(des.get(9)));
							perio.setTooth8(decodeUnicode(des.get(10)));
							perio.setTooth9(decodeUnicode(des.get(11)));
							perio.setTooth10(decodeUnicode(des.get(12)));
							perio.setTooth11(decodeUnicode(des.get(13)));
							perio.setTooth12(decodeUnicode(des.get(14)));
							perio.setTooth13(decodeUnicode(des.get(15)));
							perio.setTooth14(decodeUnicode(des.get(16)));
							perio.setTooth15(decodeUnicode(des.get(17)));
							perio.setTooth16(decodeUnicode(des.get(18)));
							perio.setTooth17(decodeUnicode(des.get(19)));
							perio.setTooth18(decodeUnicode(des.get(20)));
							perio.setTooth19(decodeUnicode(des.get(21)));
							perio.setTooth20(decodeUnicode(des.get(22)));
							perio.setTooth21(decodeUnicode(des.get(23)));
							perio.setTooth22(decodeUnicode(des.get(24)));
							perio.setTooth23(decodeUnicode(des.get(25)));
							perio.setTooth24(decodeUnicode(des.get(26)));
							perio.setTooth25(decodeUnicode(des.get(27)));
							perio.setTooth26(decodeUnicode(des.get(28)));
							perio.setTooth27(decodeUnicode(des.get(29)));
							perio.setTooth28(decodeUnicode(des.get(30)));
							perio.setTooth29(decodeUnicode(des.get(31)));
							perio.setTooth30(decodeUnicode(des.get(32)));
							perio.setTooth31(decodeUnicode(des.get(33)));
							perio.setTooth32(decodeUnicode(des.get(34)));
							//perio.setProviderLastName(des.get(35));
							
							
							//
							for (Map.Entry<String, List<Object>> entry2 : ivfMap.entrySet()) {
								if (entry.getValue() != null) {

									IVFTableSheet ivfSheet = ((IVFTableSheet) entry2.getValue().get(0));
									if ((perio.getPatientId().trim().equalsIgnoreCase(ivfSheet.getPatientId().trim()))) {
										if (returnMap == null)
											returnMap = new HashMap<>();
										if (returnMap.containsKey(ivfSheet.getUniqueID())) {
											// if the key has already been used,
											// we'll just grab the array list and add the value to it
											list = (List<Object>) (List<?>) returnMap.get(ivfSheet.getUniqueID());
											list.add(perio);
										} else {
											// if the key hasn't been used yet,
											// we'll create a new ArrayList<String> object, add the value
											// and put it in the array list with the new key
											list = new ArrayList<>();
											list.add(perio);
											returnMap.put(ivfSheet.getUniqueID(), list);
										}
									}
								}
							}

							//

						}
					}

				} catch (Exception e) {
					RuleEngineLogger.generateLogs(clazz, "Perio  DATA- ERROR- " + e.getMessage(),
							Constants.rule_log_debug, bw);

				}
			}

		}

		return returnMap;

	
	}
	
	@Override
	public Map<String, List<?>> getInsuranceDetailByPatientId(String insuranceType,Map<String, List<Object>> ivfMap, EagleSoftDBDetails esDB,
			BufferedWriter bw) {
		// TODO Auto-generated method stub
		EagleSoftFetchData d = new EagleSoftFetchData();
		Map<String, List<?>> returnMap = null;
		RuleEngineLogger.generateLogs(clazz, "Insurance Data Start ", Constants.rule_log_debug, bw);

		if (ivfMap != null) {
			List<String> ids = new ArrayList<>();
			for (Map.Entry<String, List<Object>> entry : ivfMap.entrySet()) {
				if (entry.getValue() != null) {

					IVFTableSheet ivfSheet = ((IVFTableSheet) entry.getValue().get(0));
					ids.add(ivfSheet.getPatientId());
				}
			}

			String[] pids = ids.toArray(new String[ids.size()]);

			EagleSoftQueryObject q = null;
			if (insuranceType==null)insuranceType="";
			if (insuranceType.equals(Constants.INSURANCE_TYPE_PRI) || insuranceType.equals(""))q= prepairEagleSoftQueryObject(pids, EagleSoftQuery.patient_insurance_pri_query,
					EagleSoftQuery.patient_insurance_query_CL_COUNT);
			else q= prepairEagleSoftQueryObject(pids, EagleSoftQuery.patient_insurance_sec_query,
					EagleSoftQuery.patient_insurance_query_CL_COUNT);
	
			String data = d.getDataUsingSockets(esDB, q, trustStore, keyStore, password, bw);
			if (data != null) {
				InsuranceDetail ins = null;
				try {
					ObjectMapper map = new ObjectMapper();
					// Patient patQ = map.readValue(r, Patient.class);
					Map<String, Object> cMap = map.readValue(data, new TypeReference<Map<String, Object>>() {
					});

					RuleEngineLogger.generateLogs(clazz, "Insurance DATA-" + cMap.get("dataMap").toString(),
							Constants.rule_log_debug, bw);
					Map<String, List<String>> dataMap = (Map<String, List<String>>) cMap.get("dataMap");
					List<Object> list = null;
					for (Map.Entry<String, List<String>> entry : dataMap.entrySet()) {
						if (entry.getValue() != null) {
							List<String> des = (List<String>) (entry.getValue());
							ins = new InsuranceDetail();

							ins.setPatientId(des.get(0));
							ins.setEmployerName(des.get(1));
							ins.setInsuranceName(des.get(2));
							ins.setInsuranceAddress(des.get(3));
							ins.setInsuranceCity(des.get(4));
							ins.setInsuranceState(des.get(5));
							ins.setInsuranceZipCode(des.get(6));
							ins.setInsurancePhone(des.get(7));
							
							//
							for (Map.Entry<String, List<Object>> entry2 : ivfMap.entrySet()) {
								if (entry.getValue() != null) {

									IVFTableSheet ivfSheet = ((IVFTableSheet) entry2.getValue().get(0));
									if ((ins.getPatientId().trim().equalsIgnoreCase(ivfSheet.getPatientId().trim()))) {
										if (returnMap == null)
											returnMap = new HashMap<>();
										if (returnMap.containsKey(ivfSheet.getUniqueID())) {
											// if the key has already been used,
											// we'll just grab the array list and add the value to it
											list = (List<Object>) (List<?>) returnMap.get(ivfSheet.getUniqueID());
											list.add(ins);
										} else {
											// if the key hasn't been used yet,
											// we'll create a new ArrayList<String> object, add the value
											// and put it in the array list with the new key
											list = new ArrayList<>();
											list.add(ins);
											returnMap.put(ivfSheet.getUniqueID(), list);
										}
									}
								}
							}

							//

						}
					}

				} catch (Exception e) {
					RuleEngineLogger.generateLogs(clazz, "Insurance DATA- ERROR- " + e.getMessage(),
							Constants.rule_log_debug, bw);
				}
			}

		}

		return returnMap;
	}

	@Override
	public Map<String, List<?>> getPreferanceFeeScheduleByPatientId(Map<String, List<Object>> ivfMap, EagleSoftDBDetails esDB,
			BufferedWriter bw) {
		// TODO Auto-generated method stub
		EagleSoftFetchData d = new EagleSoftFetchData();
		Map<String, List<?>> returnMap = null;
		RuleEngineLogger.generateLogs(clazz, "PreferanceFeeSchedule Data Start ", Constants.rule_log_debug, bw);

		if (ivfMap != null) {
			List<String> ids = new ArrayList<>();
			for (Map.Entry<String, List<Object>> entry : ivfMap.entrySet()) {
				if (entry.getValue() != null) {

					IVFTableSheet ivfSheet = ((IVFTableSheet) entry.getValue().get(0));
					ids.add(ivfSheet.getPatientId());
				}
			}

			String[] pids = ids.toArray(new String[ids.size()]);

			EagleSoftQueryObject q = null;
			q= prepairEagleSoftQueryObject(pids, EagleSoftQuery.preferance_fee_schedule_query,
					EagleSoftQuery.preferance_fee_schedule_query_CL_COUNT);
			String data = d.getDataUsingSockets(esDB, q, trustStore, keyStore, password, bw);
			if (data != null) {
				PreferanceFeeSchedule pref = null;
				try {
					ObjectMapper map = new ObjectMapper();
					// Patient patQ = map.readValue(r, Patient.class);
					Map<String, Object> cMap = map.readValue(data, new TypeReference<Map<String, Object>>() {
					});

					RuleEngineLogger.generateLogs(clazz, "PreferanceFeeSchedule DATA-" + cMap.get("dataMap").toString(),
							Constants.rule_log_debug, bw);
					Map<String, List<String>> dataMap = (Map<String, List<String>>) cMap.get("dataMap");
					List<Object> list = null;
					for (Map.Entry<String, List<String>> entry : dataMap.entrySet()) {
						if (entry.getValue() != null) {
							List<String> des = (List<String>) (entry.getValue());
							pref = new PreferanceFeeSchedule();

							pref.setPatientId(des.get(0));
							pref.setFeeId(des.get(1));
							pref.setName(des.get(2));
							pref.setFeeLevelId(des.get(3));
							
							//
							for (Map.Entry<String, List<Object>> entry2 : ivfMap.entrySet()) {
								if (entry.getValue() != null) {

									IVFTableSheet ivfSheet = ((IVFTableSheet) entry2.getValue().get(0));
									if ((pref.getPatientId().trim().equalsIgnoreCase(ivfSheet.getPatientId().trim()))) {
										if (returnMap == null)
											returnMap = new HashMap<>();
										if (returnMap.containsKey(ivfSheet.getUniqueID())) {
											// if the key has already been used,
											// we'll just grab the array list and add the value to it
											list = (List<Object>) (List<?>) returnMap.get(ivfSheet.getUniqueID());
											list.add(pref);
										} else {
											// if the key hasn't been used yet,
											// we'll create a new ArrayList<String> object, add the value
											// and put it in the array list with the new key
											list = new ArrayList<>();
											list.add(pref);
											returnMap.put(ivfSheet.getUniqueID(), list);
										}
									}
								}
							}

							//

						}
					}

				} catch (Exception e) {
					RuleEngineLogger.generateLogs(clazz, "FEE Schedule DATA- ERROR- " + e.getMessage(),
							Constants.rule_log_debug, bw);
				}
			}

		}

		return returnMap;
	}
	
	@Override
	public Map<String, List<?>> getPolicyHolderByPatientId(Map<String, List<Object>> ivfMap, EagleSoftDBDetails esDB,
			BufferedWriter bw,boolean primary) {
		// TODO Auto-generated method stub
		EagleSoftFetchData d = new EagleSoftFetchData();
		Map<String, List<?>> returnMap = null;
		RuleEngineLogger.generateLogs(clazz, "PolicyHolderByPatientId Data Start ", Constants.rule_log_debug, bw);

		if (ivfMap != null) {
			// ids must be built outside the loop - previously was re-created on each
			// iteration, leaving only the last patient ID in the list.
			List<String> ids = new ArrayList<>();
			for (Map.Entry<String, List<Object>> entry1 : ivfMap.entrySet()) {
				if (entry1.getValue() != null) {
					IVFTableSheet ivfSheet = ((IVFTableSheet) entry1.getValue().get(0));
					ids.add(ivfSheet.getPatientId());
				}
			}

			String[] pids = ids.toArray(new String[ids.size()]);

			EagleSoftQueryObject q = null;
			q= prepairEagleSoftQueryObject(pids, EagleSoftQuery.policy_holder_schedule_query,
					EagleSoftQuery.policy_holder_schedule_query_CL_COUNT);
			/*if (primary)q= prepairEagleSoftQueryObject(pids, EagleSoftQuery.policy_holder_schedule_query_pr,
					EagleSoftQuery.policy_holder_schedule_query_CL_COUNT);
			
			else q= prepairEagleSoftQueryObject(pids, EagleSoftQuery.policy_holder_schedule_query_sec,
					EagleSoftQuery.policy_holder_schedule_query_CL_COUNT);
			*/		
			String data = d.getDataUsingSockets(esDB, q, trustStore, keyStore, password, bw);
			if (data != null) {
				PatientPolicyHolder pph = null;
				try {
					ObjectMapper map = new ObjectMapper();
					// Patient patQ = map.readValue(r, Patient.class);
					Map<String, Object> cMap = map.readValue(data, new TypeReference<Map<String, Object>>() {
					});

					RuleEngineLogger.generateLogs(clazz, "PolicyHolderByPatientId DATA-" + cMap.get("dataMap").toString(),
							Constants.rule_log_debug, bw);
					Map<String, List<String>> dataMap = (Map<String, List<String>>) cMap.get("dataMap");
					List<Object> list = null;
					for (Map.Entry<String, List<String>> entryd: dataMap.entrySet()) {
						if (entryd.getValue() != null) {
							List<String> des = (List<String>) (entryd.getValue());
							pph = new PatientPolicyHolder();

							pph.setPatientId(des.get(0));
							pph.setPolicyHolder(des.get(1));
							pph.setPolicyHolderSec(des.get(2));
							pph.setRelation(des.get(3));
							//
							for (Map.Entry<String, List<Object>> entry2 : ivfMap.entrySet()) {
								if (entryd.getValue() != null) {

									IVFTableSheet ivfSheet = ((IVFTableSheet) entry2.getValue().get(0));
									if ((pph.getPatientId().trim().equalsIgnoreCase(ivfSheet.getPatientId().trim()))) {
										if (returnMap == null)
											returnMap = new HashMap<>();
										if (returnMap.containsKey(ivfSheet.getUniqueID())) {
											// if the key has already been used,
											// we'll just grab the array list and add the value to it
											list = (List<Object>) (List<?>) returnMap.get(ivfSheet.getUniqueID());
											list.add(pph);
										} else {
											// if the key hasn't been used yet,
											// we'll create a new ArrayList<String> object, add the value
											// and put it in the array list with the new key
											list = new ArrayList<>();
											list.add(pph);
											returnMap.put(ivfSheet.getUniqueID(), list);
										}
									}
								}
							}

							//

						}
					}

				} catch (Exception e) {
					RuleEngineLogger.generateLogs(clazz, "PolicyHolderByPatientId --> DATA- ERROR- " + e.getMessage(),
							Constants.rule_log_debug, bw);
				}
			}
		  // }

		}

		return returnMap;
	}
	
	@Override
	public Map<String, List<?>> getPolicyHolderDobByPatientId(String insuranceType, Map<String, List<Object>> ivfMap, EagleSoftDBDetails esDB,
			BufferedWriter bw) {
		// TODO Auto-generated method stub
		EagleSoftFetchData d = new EagleSoftFetchData();
		Map<String, List<?>> returnMap = null;
		RuleEngineLogger.generateLogs(clazz, "PolicyHolderDobByPatientId Data Start ", Constants.rule_log_debug, bw);

		if (ivfMap != null) {
			List<String> ids = new ArrayList<>();
			for (Map.Entry<String, List<Object>> entry1 : ivfMap.entrySet()) {
				if (entry1.getValue() != null) {
					IVFTableSheet ivfSheet = ((IVFTableSheet) entry1.getValue().get(0));
					ids.add(ivfSheet.getPatientId());
				}
			}

			String[] pids = ids.toArray(new String[ids.size()]);

			/*EagleSoftQueryObject q = null;
			q= prepairEagleSoftQueryObject(pids, EagleSoftQuery.policy_holder_dob_query,
					EagleSoftQuery.policy_holder_dob_query_CL_COUNT);
			/*if (primary)q= prepairEagleSoftQueryObject(pids, EagleSoftQuery.policy_holder_schedule_query_pr,
					EagleSoftQuery.policy_holder_schedule_query_CL_COUNT);
			
			else q= prepairEagleSoftQueryObject(pids, EagleSoftQuery.policy_holder_schedule_query_sec,
					EagleSoftQuery.policy_holder_schedule_query_CL_COUNT);
			*/	
			EagleSoftQueryObject q = null;
			if (insuranceType==null) q= prepairEagleSoftQueryObject(pids, EagleSoftQuery.policy_holder_dob_query_pr,
					EagleSoftQuery.policy_holder_dob_query_CL_COUNT);
			else if (insuranceType!=null && insuranceType.equals(Constants.INSURANCE_TYPE_PRI) || insuranceType.equals(""))q= prepairEagleSoftQueryObject(pids, EagleSoftQuery.policy_holder_dob_query_pr,
					EagleSoftQuery.policy_holder_dob_query_CL_COUNT);
			else q= prepairEagleSoftQueryObject(pids, EagleSoftQuery.policy_holder_dob_query_sec,
					EagleSoftQuery.policy_holder_dob_query_CL_COUNT);
			String data = d.getDataUsingSockets(esDB, q, trustStore, keyStore, password, bw);
			if (data != null) {
				EagleSoftPolicyholdeDob pph = null;
				try {
					ObjectMapper map = new ObjectMapper();
					// Patient patQ = map.readValue(r, Patient.class);
					Map<String, Object> cMap = map.readValue(data, new TypeReference<Map<String, Object>>() {
					});

					RuleEngineLogger.generateLogs(clazz, "PolicyHolderDobByPatientId DATA-" + cMap.get("dataMap").toString(),
							Constants.rule_log_debug, bw);
					Map<String, List<String>> dataMap = (Map<String, List<String>>) cMap.get("dataMap");
					List<Object> list = null;
					for (Map.Entry<String, List<String>> entryd: dataMap.entrySet()) {
						if (entryd.getValue() != null) {
							List<String> des = (List<String>) (entryd.getValue());
							pph = new EagleSoftPolicyholdeDob();

							pph.setPatientId(des.get(0));
							pph.setPolicyHolderDob(des.get(6));
							for (Map.Entry<String, List<Object>> entry2 : ivfMap.entrySet()) {
								if (entryd.getValue() != null) {

									IVFTableSheet ivfSheet = ((IVFTableSheet) entry2.getValue().get(0));
									if ((pph.getPatientId().trim().equalsIgnoreCase(ivfSheet.getPatientId().trim()))) {
										if (returnMap == null)
											returnMap = new HashMap<>();
										if (returnMap.containsKey(ivfSheet.getUniqueID())) {
											// if the key has already been used,
											// we'll just grab the array list and add the value to it
											list = (List<Object>) (List<?>) returnMap.get(ivfSheet.getUniqueID());
											list.add(pph);
										} else {
											// if the key hasn't been used yet,
											// we'll create a new ArrayList<String> object, add the value
											// and put it in the array list with the new key
											list = new ArrayList<>();
											list.add(pph);
											returnMap.put(ivfSheet.getUniqueID(), list);
										}
									}
								}
							}
							}

					}

				} catch (Exception e) {
					e.printStackTrace();
					RuleEngineLogger.generateLogs(clazz, "PolicyHolderDobByPatientId --> DATA- ERROR- " + e.getMessage(),
							Constants.rule_log_debug, bw);
				}
			}
		  // }

		}

		return returnMap;
	}
	
	@Override
	public Map<String, List<?>> getPatientDeductibelWithBenefits(String insuranceType,Map<String, List<Object>> ivfMap, EagleSoftDBDetails esDB,
			BufferedWriter bw) {
		// TODO Auto-generated method stub
		EagleSoftFetchData d = new EagleSoftFetchData();
		Map<String, List<?>> returnMap = null;
		RuleEngineLogger.generateLogs(clazz, "Patient Data Start ", Constants.rule_log_debug, bw);

		if (ivfMap != null) {
			List<String> ids = new ArrayList<>();
			for (Map.Entry<String, List<Object>> entry : ivfMap.entrySet()) {
				if (entry.getValue() != null) {

					IVFTableSheet ivfSheet = ((IVFTableSheet) entry.getValue().get(0));
					ids.add(ivfSheet.getPatientId());
				}
			}

			String[] pids = ids.toArray(new String[ids.size()]);

			EagleSoftQueryObject q = null;
			if (insuranceType==null) q= prepairEagleSoftQueryObject(pids, EagleSoftQuery.primary_deductible,
					EagleSoftQuery.primary_deductible_CL_COUNT);
			else if (insuranceType!=null && insuranceType.equals(Constants.INSURANCE_TYPE_PRI) || insuranceType.equals(""))q= prepairEagleSoftQueryObject(pids, EagleSoftQuery.primary_deductible,
					EagleSoftQuery.primary_deductible_CL_COUNT);
			else q= prepairEagleSoftQueryObject(pids, EagleSoftQuery.secondary_deductible,
					EagleSoftQuery.secondary_deductible_CL_COUNT);
			String data = d.getDataUsingSockets(esDB, q, trustStore, keyStore, password, bw);
			if (data != null) {
				PatientDeductiblewithBenefits pat = null;
				try {
					ObjectMapper map = new ObjectMapper();
					// Patient patQ = map.readValue(r, Patient.class);
					Map<String, Object> cMap = map.readValue(data, new TypeReference<Map<String, Object>>() {
					});

					RuleEngineLogger.generateLogs(clazz, "Patient Benefits DATA-" + cMap.get("dataMap").toString(),
							Constants.rule_log_debug, bw);
					Map<String, List<String>> dataMap = (Map<String, List<String>>) cMap.get("dataMap");
					List<Object> list = null;
					for (Map.Entry<String, List<String>> entry : dataMap.entrySet()) {
						if (entry.getValue() != null) {
							List<String> des = (List<String>) (entry.getValue());
							pat = new PatientDeductiblewithBenefits();

							pat.setPatientId(des.get(0));
							pat.setServiceDescription(des.get(1));
							pat.setBenefitPrecentage(des.get(2));
							pat.setBenefitDeductible(des.get(3));
							for (Map.Entry<String, List<Object>> entry2 : ivfMap.entrySet()) {
								if (entry.getValue() != null) {

									IVFTableSheet ivfSheet = ((IVFTableSheet) entry2.getValue().get(0));
									if ((pat.getPatientId().trim().equalsIgnoreCase(ivfSheet.getPatientId().trim()))) {
										if (returnMap == null)
											returnMap = new HashMap<>();
										if (returnMap.containsKey(ivfSheet.getUniqueID())) {
											// if the key has already been used,
											// we'll just grab the array list and add the value to it
											list = (List<Object>) (List<?>) returnMap.get(ivfSheet.getUniqueID());
											list.add(pat);
										} else {
											// if the key hasn't been used yet,
											// we'll create a new ArrayList<String> object, add the value
											// and put it in the array list with the new key
											list = new ArrayList<>();
											list.add(pat);
											returnMap.put(ivfSheet.getUniqueID(), list);
										}
									}
								}
							}

							//

						}
					}

				} catch (Exception e) {
					RuleEngineLogger.generateLogs(clazz, "Patient Benefits DATA- ERROR- " + e.getMessage(),
							Constants.rule_log_debug, bw);
				}
			}

		}

		return returnMap;
	}
	
	
	@Override
	public Map<String, List<?>> getPreferredDentist(Map<String, List<Object>> ivfMap,EagleSoftDBDetails esDB,BufferedWriter bw){

		// TODO Auto-generated method stub
		EagleSoftFetchData d = new EagleSoftFetchData();
		Map<String, List<?>> returnMap = null;
		RuleEngineLogger.generateLogs(clazz, "PreferredDentist Start", Constants.rule_log_debug, bw);

		if (ivfMap != null) {
			List<String> ids = new ArrayList<>();
			for (Map.Entry<String, List<Object>> entry : ivfMap.entrySet()) {
				if (entry.getValue() != null) {

					IVFTableSheet ivfSheet = ((IVFTableSheet) entry.getValue().get(0));
					ids.add(ivfSheet.getPatientId());
				}
			}

			String[] pids = ids.toArray(new String[ids.size()]);

			EagleSoftQueryObject q = prepairEagleSoftQueryObject(pids, EagleSoftQuery.preferred_dentist_query,
					EagleSoftQuery.preferred_dentist_query_CL_COUNT);
			String data = d.getDataUsingSockets(esDB, q, trustStore, keyStore, password, bw);
			if (data != null) {
				PreferredDentist preferredDentist = null;
				try {
					ObjectMapper map = new ObjectMapper();
					// Patient patQ = map.readValue(r, Patient.class);
					Map<String, Object> cMap = map.readValue(data, new TypeReference<Map<String, Object>>() {
					});

					RuleEngineLogger.generateLogs(clazz, "PreferredDentist DATA-" + cMap.get("dataMap").toString(),
							Constants.rule_log_debug, bw);
					Map<String, List<String>> dataMap = (Map<String, List<String>>) cMap.get("dataMap");
					List<Object> list = null;
					for (Map.Entry<String, List<String>> entry : dataMap.entrySet()) {
						if (entry.getValue() != null) {
							List<String> des = (List<String>) (entry.getValue());
							preferredDentist = new PreferredDentist();

							preferredDentist.setPatientId(des.get(0));
							preferredDentist.setPatientName(des.get(1));
							preferredDentist.setPreferredDentist(des.get(2));
							preferredDentist.setProviderName(des.get(3));
							
							
							//
							for (Map.Entry<String, List<Object>> entry2 : ivfMap.entrySet()) {
								if (entry.getValue() != null) {

									IVFTableSheet ivfSheet = ((IVFTableSheet) entry2.getValue().get(0));
									if ((preferredDentist.getPatientId().trim().equalsIgnoreCase(ivfSheet.getPatientId().trim()))) {
										if (returnMap == null)
											returnMap = new HashMap<>();
										if (returnMap.containsKey(ivfSheet.getUniqueID())) {
											// if the key has already been used,
											// we'll just grab the array list and add the value to it
											list = (List<Object>) (List<?>) returnMap.get(ivfSheet.getUniqueID());
											list.add(preferredDentist);
										} else {
											// if the key hasn't been used yet,
											// we'll create a new ArrayList<String> object, add the value
											// and put it in the array list with the new key
											list = new ArrayList<>();
											list.add(preferredDentist);
											returnMap.put(ivfSheet.getUniqueID(), list);
										}
									}
								}
							}

							//

						}
					}

				} catch (Exception e) {
					RuleEngineLogger.generateLogs(clazz, "PreferredDentist  DATA- ERROR- " + e.getMessage(),
							Constants.rule_log_debug, bw);

				}
			}

		}

		return returnMap;

	
	}
	
	/** Returns des.get(index) or null if the list is too short or the value is null/empty. */
	private String safeGet(List<String> des, int index) {
		if (des == null || index >= des.size()) return null;
		String v = des.get(index);
		return (v != null && !v.isEmpty()) ? v : null;
	}

	private String decodeUnicode(String v) {
		v=v.replaceAll("\n", "");
		//System.out.println(v);
			try {
		byte[] b=v.getBytes("UTF-8");
		v="";	
		for(int n=0;n<b.length;n++) {
			if ((b[n]+"").equals("32")) continue;
			v=v+b[n]+",";
		}
		v=v.replaceAll(",$", "");
		 }catch(Exception u) {
			 return "";
		 }
		return v;	
	}

}
