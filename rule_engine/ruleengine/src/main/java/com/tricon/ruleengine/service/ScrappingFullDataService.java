package com.tricon.ruleengine.service;

import java.util.List;
import java.util.Map;

import com.tricon.ruleengine.dto.ScrappingFullDataDetailDto;
import com.tricon.ruleengine.dto.ScrappingFullDataDto;

public interface ScrappingFullDataService {
	
	
	public List<ScrappingFullDataDto> getSiteNames();
	
	public ScrappingFullDataDetailDto getScrappingDetails(int siteId, String  offId);
	
	public String parseFullDataAndSaveDetails(ScrappingFullDataDetailDto dto);
	
	
	

}
