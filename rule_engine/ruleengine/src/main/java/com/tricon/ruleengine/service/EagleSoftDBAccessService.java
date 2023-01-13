package com.tricon.ruleengine.service;

import java.io.BufferedWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.tricon.ruleengine.model.db.EagleSoftDBDetails;
import com.tricon.ruleengine.model.sheet.ClaimData;
import com.tricon.ruleengine.model.sheet.EagleSoftPatient;
import com.tricon.ruleengine.model.sheet.InsuranceDetail;
import com.tricon.ruleengine.model.sheet.Perio;
import com.tricon.ruleengine.model.sheet.TreatmentPlan;

public interface EagleSoftDBAccessService {

	
	public Map<String, List<?>> getPatientData(String insuranceType,Map<String, List<Object>> ivfMap,EagleSoftDBDetails esDB,BufferedWriter bw);
	
	public Map<String, List<?>> getEmployeeMaster(Map<String, List<EagleSoftPatient>> espatients,EagleSoftDBDetails esDB,BufferedWriter bw);
	
	public Map<String, List<?>> getTreatmentPlanData(String trids[],EagleSoftDBDetails esDB,BufferedWriter bw);
	
	public Map<String, List<?>> getClaimData(String trids[],EagleSoftDBDetails esDB,BufferedWriter bw);
	
	public Map<String, List<?>> getFeeScheduleData(Map<String, List<EagleSoftPatient>> espatients,EagleSoftDBDetails esDB,BufferedWriter bw);

	public List<TreatmentPlan> getTreatmentPlanDataByPatient(String patientId,EagleSoftDBDetails esDB,BufferedWriter bw);
	
	public List<ClaimData> getClaimDataByPatient(String patientId, EagleSoftDBDetails esDB,	BufferedWriter bw);

	public LinkedHashMap<String, List<String>> getGoogleReportData(String query, String ids,int columnCount,EagleSoftDBDetails esDB,BufferedWriter bw);
	
	
	public Map<String, List<?>> getPerioDataForPatients(Map<String, List<Object>> ivfMap,EagleSoftDBDetails esDB,BufferedWriter bw);

	public Map<String, List<?>> getPatientHistoryES(Map<String, List<Object>> ivfMap, EagleSoftDBDetails esDB,String dateString,int months,
	BufferedWriter bw);

    public void setUpSSLCertificates();

    public String[] doDiagnosticCheck(String officeUuidB,String comanyuuid);
    
    public List<String[]> doDiagnosticCheck();
    
    public Map<String, List<?>> getInsuranceDetailByPatientId(String insuranceType,Map<String, List<Object>> ivfMap, EagleSoftDBDetails esDB,	BufferedWriter bw);

    public Map<String, List<?>> getPreferanceFeeScheduleByPatientId(Map<String, List<Object>> ivfMap, EagleSoftDBDetails esDB,	BufferedWriter bw);

    public Map<String, List<?>> getPolicyHolderByPatientId(Map<String, List<Object>> ivfMap, EagleSoftDBDetails esDB,	BufferedWriter bw,boolean primary);
}
