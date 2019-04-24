package com.tricon.ruleengine.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.tricon.ruleengine.dto.ScrappingInputDto;
import com.tricon.ruleengine.model.db.Office;

public interface ScrappingService {
	
	public Map<String,List<?>> scrapSite(ScrappingInputDto dto) throws InterruptedException, ExecutionException;
	
	public void updateScrapRunStatus(int siteId, Office office);
	
	public void updateScrapRunStatus();
}
