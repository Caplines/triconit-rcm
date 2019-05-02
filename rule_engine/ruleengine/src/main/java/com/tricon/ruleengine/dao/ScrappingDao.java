package com.tricon.ruleengine.dao;

import java.util.List;

import com.tricon.ruleengine.dto.ScrappingSiteDetailsDto;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.ScrappingSite;
import com.tricon.ruleengine.model.db.ScrappingSiteDetails;

public interface ScrappingDao {
	
	public List<ScrappingSiteDetails> getScrappingSiteDetailDetails();
	
	public ScrappingSiteDetails getScrappingSiteDetailsDetail(int siteDetailId, Office office);
	
	public ScrappingSite getScrappingSiteDetails(int siteId, Office office);
	
	public void updateScrappingSiteRunningStatus(ScrappingSiteDetails sd);
	
	public void updateScrappingSiteRunningStatusAll();
	
	public ScrappingSiteDetailsDto getScrappingSiteDetailsDetailSDto(int siteDetailId, Office office);
	
	
}
