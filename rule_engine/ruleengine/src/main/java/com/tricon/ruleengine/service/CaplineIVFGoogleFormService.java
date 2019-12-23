package com.tricon.ruleengine.service;

import java.util.Date;
import java.util.Set;

import com.tricon.ruleengine.dto.CaplineIVFFormDto;
import com.tricon.ruleengine.dto.CaplineIVFQueryFormDto;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.Patient;
import com.tricon.ruleengine.model.db.User;

public interface CaplineIVFGoogleFormService {
	
	
	public Object[] saveIVFFormData(CaplineIVFFormDto d,Office office) throws Exception;
	public Object  searchIVFData(CaplineIVFQueryFormDto d,Office office) throws Exception;
	public Object  searchIVFDataForGoogleSheet(CaplineIVFQueryFormDto d,Office office) throws Exception;
	
	public Object  searchIVFHistoryDataForGoogleSheet(CaplineIVFQueryFormDto d,Office office) throws Exception;
	
	public Object  searchIVFDataforApp(CaplineIVFQueryFormDto d,Office off) throws Exception;
	public Object  convertPatientDataToIVFSheetData(Set<String> patIds,String officeName) throws Exception;
	public Object[]  generatePDF(CaplineIVFQueryFormDto dto,Office office);
	public Object[] saveAllData (Patient pat, Office office, Date date,User user);
	public void fillUpGoogleSheet(CaplineIVFQueryFormDto dto,Office office);
	public Object searchIVFDataPat(CaplineIVFQueryFormDto d,Office office,Set<String> patIds) throws Exception;
}
