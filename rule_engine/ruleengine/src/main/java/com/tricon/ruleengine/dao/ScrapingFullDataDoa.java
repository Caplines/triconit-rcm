package com.tricon.ruleengine.dao;

import java.io.Serializable;
import java.util.List;

import com.tricon.ruleengine.dto.ScrappingFullDataDetailDto;
import com.tricon.ruleengine.dto.ScrappingFullDataDto;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.ScrappingFullDataManagment;
import com.tricon.ruleengine.model.db.ScrappingFullDataManagmentProcess;
import com.tricon.ruleengine.model.db.ScrappingSiteDetailsFull;
import com.tricon.ruleengine.model.db.ScrappingSiteFull;
import com.tricon.ruleengine.model.db.ScrappingSiteFullMaster;

public interface ScrapingFullDataDoa {

	
	public List<ScrappingFullDataDto> getSiteNames();
	
	public List<ScrappingFullDataDto> getSiteNamesBySiteType(String type);
	
	public ScrappingFullDataDetailDto getScrappingDetails(int siteId, Office off);
	
	public ScrappingSiteFull findScrappingSiteFullById(int siteId);

	public ScrappingSiteDetailsFull findScrappingDetailsById(int siteDetailId);
	
	public Serializable saveScrappingDetailsById(ScrappingSiteDetailsFull sdFull);
	
	public void updateScrappingDetailsById(ScrappingSiteDetailsFull sdFull);
	
	public int findMaxProxyPortScrappinSiteDetailsFull();
	
	public void updateScrappingSiteRunningStatusAll();
	
	public String findAnyRunnigfullScrapBSiteName(String name);

	public ScrappingFullDataManagment getScrappingFullDataManagmentData();
	
	public void increasecrapCount(ScrappingFullDataManagment manage);
	
	public void updateScrappingSiteManagement();
	
	public Serializable createScrappingSiteManagementProcess(ScrappingFullDataManagmentProcess manageP);
	
	public ScrappingFullDataManagmentProcess getScrappingFullDataManagmentDataProcess(int id);
	
	public void updateScrappingFullDataManagmentProcess(ScrappingFullDataManagmentProcess manage);
	
	public String  getTaxmapping(Office office,String type);
	
	public ScrappingSiteFullMaster getScrappingSiteFullMaster(int scrapSiteId);
	
}
