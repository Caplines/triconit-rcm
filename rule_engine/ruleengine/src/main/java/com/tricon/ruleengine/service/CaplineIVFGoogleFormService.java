package com.tricon.ruleengine.service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Set;

import com.tricon.ruleengine.dto.CaplineIVFFormDto;
import com.tricon.ruleengine.dto.CaplineIVFQueryFormDto;
import com.tricon.ruleengine.model.db.Office;

public interface CaplineIVFGoogleFormService {
	
	
	public Integer saveIVFFormData(CaplineIVFFormDto d,Office office) throws Exception;
	public Object  searchIVFData(CaplineIVFQueryFormDto d,Office office) throws Exception;
	public Object  searchIVFDataForGoogleSheet(CaplineIVFQueryFormDto d,Office office) throws Exception;
	public Object  searchIVFDataforApp(CaplineIVFQueryFormDto d) throws Exception;
	public Object  convertPatientDataToIVFSheetData(Set<String> patIds,String officeName) throws Exception;
	public ByteArrayOutputStream  generatePDF(CaplineIVFQueryFormDto dto,Office office);
}
