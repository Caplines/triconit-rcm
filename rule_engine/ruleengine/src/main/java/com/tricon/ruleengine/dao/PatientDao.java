package com.tricon.ruleengine.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.tricon.ruleengine.dto.CaplineIVFFormDto;
import com.tricon.ruleengine.dto.CaplineIVFQueryFormDto;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.Patient;
import com.tricon.ruleengine.model.db.PatientDetail;
import com.tricon.ruleengine.model.db.PatientHistory;
import com.tricon.ruleengine.model.db.User;

public interface PatientDao {
	
	
	public Patient checkforPatientWithIdAndOffice(String patientid, Office off, Patient pat);
	public PatientHistory getPatientHistory(String patientid, Office off);
	public PatientDetail getPatientDetails(String patientid, Office off);
	
	public Patient savePatientDataWithDetailsAndHistory(Patient pat,Office off, User user,Date date) throws Exception;
	public Patient updateOnlyPatient(Patient pat,Office off, User user)  throws Exception;
	public Patient updatePatientDataWithDetailsAndHistory(Patient pat,Office off, User use,boolean detailsSave)  throws Exception;
	
	public List<Patient> searchPatientByPatientId(Set<String> patientIds, Office off);
	public List<CaplineIVFFormDto> searchPatientDetailFromIVF(CaplineIVFQueryFormDto dto, Office off,Set<String> patIds);
	public List<PatientHistory> searchPatientHistoryForPatient(Set<String> patientIds, Office off,Set<String> patDids);
	public List<Object> searchPatientDetailFromIVFGivenColumns(CaplineIVFQueryFormDto dto, Office off);
	
	public List<Object> searchPatientHistoryFromIVFGivenColumns(CaplineIVFQueryFormDto dto, Office off);
	
	public void deletePatientHistoryByIds(String[] ids );
	
	

}
