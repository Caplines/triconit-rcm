package com.tricon.ruleengine.service;

import java.util.List;
import java.util.Map;

import com.tricon.ruleengine.dto.ScrappingFullDataDetailDto;
import com.tricon.ruleengine.dto.ScrappingFullDataDto;
import com.tricon.ruleengine.dto.scrapping.ScrapPatient;
import com.tricon.ruleengine.model.db.PatientTemp;
import com.tricon.ruleengine.model.db.ScrappingFullDataManagmentProcess;

public interface ScrappingFullDataService {
	
	
	public List<ScrappingFullDataDto> getSiteNames();
	
	public List<ScrappingFullDataDto> getSiteNamesBySiteType(String type);
	
	public ScrappingFullDataDetailDto getScrappingDetails(int siteId, String  offId,String userName);
	
	public String parseFullDataAndSaveDetails(ScrappingFullDataDetailDto dto,String userName);
	
	public String findRunningStatus(ScrappingFullDataDetailDto dto);
	
	public int getScrappingFullDataManagmentProcessCount(int id);
	
	public List<ScrapPatient> getScrappingStatusByPatIdsTemp(String ids);
	
	public String getScrappingFullDataManagmentProcessStatus(int id);
	
	public ScrappingFullDataDetailDto getScrappingDetailsForRcm(int siteId,String offId,String userName);
	

}
