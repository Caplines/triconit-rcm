package com.tricon.ruleengine.dao;

import java.io.Serializable;
import java.util.List;

import com.tricon.ruleengine.dto.ScrappingFullDataDetailDto;
import com.tricon.ruleengine.dto.ScrappingFullDataDto;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.ScrappingSiteDetailsFull;
import com.tricon.ruleengine.model.db.ScrappingSiteFull;

public interface ScrapingFullDataDoa {

	
	public List<ScrappingFullDataDto> getSiteNames();
	
	public ScrappingFullDataDetailDto getScrappingDetails(int siteId, Office off);
	
	public ScrappingSiteFull findScrappingSiteFullById(int siteId);

	public ScrappingSiteDetailsFull findScrappingDetailsById(int siteDetailId);
	
	public Serializable saveScrappingDetailsById(ScrappingSiteDetailsFull sdFull);
	
	public void updateScrappingDetailsById(ScrappingSiteDetailsFull sdFull);
	
	public int findMaxProxyPort(int siteDetailId);
	
	public void updateScrappingSiteRunningStatusAll();
	
	public String findAnyRunnigfullScrapBSiteName(String name);
}
