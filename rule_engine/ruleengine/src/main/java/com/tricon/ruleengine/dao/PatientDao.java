package com.tricon.ruleengine.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.tricon.ruleengine.dto.CaplineIVFFormDto;
import com.tricon.ruleengine.dto.CaplineIVFQueryFormDto;
import com.tricon.ruleengine.dto.scrapping.ScrapPatient;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.Patient;
import com.tricon.ruleengine.model.db.PatientDetail;
import com.tricon.ruleengine.model.db.PatientHistory;
import com.tricon.ruleengine.model.db.PatientTemp;
import com.tricon.ruleengine.model.db.User;

public interface PatientDao {
	
	
	public Patient checkforPatientWithIdAndOffice(String patientid, Office off, Patient pat,boolean checkforException);
	public Patient checkforPatientWithIdAndOfficeAndGeneralDate(String patientid, Office off, Patient pat,boolean checkforException,String generalDate);
	
	public PatientHistory getPatientHistory(String patientid, Office off);
	public PatientDetail getPatientDetails(String patientid, Office off);
	
	public Patient savePatientDataWithDetailsAndHistory(Patient pat,Office off, User user,Date date) throws Exception;
	public Patient updateOnlyPatient(Patient pat,Office off, User user)  throws Exception;
	public void updatePatientDataWithDetailsAndHistory1(Patient pat)  throws Exception;
	public Patient updatePatientDataWithDetailsAndHistory2(Patient pat,boolean detailsSave,boolean onlySave)  throws Exception;
	public void updatePatientDataWithDetailsAndHistory3(Patient pat,Office off)  throws Exception;
	
	public List<Patient> searchPatientByPatientId(Set<String> patientIds, Office off);
	public List<CaplineIVFFormDto> searchPatientDetailFromIVF(CaplineIVFQueryFormDto dto, Office off,Set<String> patIds,boolean temp);
	public List<PatientHistory> searchPatientHistoryForPatient(Set<String> patientIds, Office off,Set<String> patDids,boolean temp);
	public List<Object> searchPatientDetailFromIVFGivenColumns(CaplineIVFQueryFormDto dto, Office off);
	
	public List<Object> searchPatientHistoryFromIVFGivenColumns(CaplineIVFQueryFormDto dto, Office off);
	
	public void deletePatientHistoryByIds(String[] ids );
	public Patient checkforPatientWithId(String patientid, Office off);
	
	/**
	 * For temp tables only
	 * @param patientid
	 * @param off
	 * @return
	 */
	public PatientTemp checkforPatientWithIdAndOfficeTemp(String patientid, Office off);
	
	public Integer savePatientTempDataWithDetailsAndHistory(PatientTemp pat, Office off, User user) throws Exception;
	
	public void updatePatientTempDataOnly(PatientTemp pat) throws Exception;
	
	public List<ScrapPatient> getScrappingStatusByPatIdsTemp(List<Integer> ids); 
	

}
