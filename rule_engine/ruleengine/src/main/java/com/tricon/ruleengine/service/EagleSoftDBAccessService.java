package com.tricon.ruleengine.service;

import java.io.BufferedWriter;
import java.util.List;
import java.util.Map;

import com.tricon.ruleengine.model.db.EagleSoftDBDetails;
import com.tricon.ruleengine.model.sheet.EagleSoftPatient;
import com.tricon.ruleengine.model.sheet.TreatmentPlan;

public interface EagleSoftDBAccessService {

	
	public Map<String, List<?>> getPatientData(Map<String, List<Object>> ivfMap,EagleSoftDBDetails esDB,BufferedWriter bw);
	
	public Map<String, List<?>> getEmployeeMaster(Map<String, List<EagleSoftPatient>> espatients,EagleSoftDBDetails esDB,BufferedWriter bw);
	
	public Map<String, List<?>> getTreatmentPlanData(String trids[],EagleSoftDBDetails esDB,BufferedWriter bw);
	
	public Map<String, List<?>> getFeeScheduleData(Map<String, List<EagleSoftPatient>> espatients,EagleSoftDBDetails esDB,BufferedWriter bw);

	public List<TreatmentPlan> getTreatmentPlanDataByPatient(String patientId,EagleSoftDBDetails esDB,BufferedWriter bw);

	public Map<String, List<String>> getGoogleReportData(String query, String ids,int columnCount,EagleSoftDBDetails esDB,BufferedWriter bw);

   public void setUpSSLCertificates();
}
