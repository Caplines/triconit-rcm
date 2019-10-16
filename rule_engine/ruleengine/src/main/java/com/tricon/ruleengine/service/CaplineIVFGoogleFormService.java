package com.tricon.ruleengine.service;

import java.util.Set;

import com.tricon.ruleengine.dto.CaplineIVFFormDto;
import com.tricon.ruleengine.dto.CaplineIVFQueryFormDto;

public interface CaplineIVFGoogleFormService {
	
	
	public Integer saveIVFFormData(CaplineIVFFormDto d) throws Exception;
	public Object  searchIVFData(CaplineIVFQueryFormDto d) throws Exception;
	public Object  convertPatientDataToIVFSheetData(Set<String> patIds,String officeName) throws Exception;
}
