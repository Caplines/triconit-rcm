package com.tricon.ruleengine.service.scrapfull;

import com.tricon.ruleengine.dto.ScrappingFullDataDetailDto;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.ScrappingSiteDetailsFull;
import com.tricon.ruleengine.model.db.User;

public interface BCBSDnoaconnect {
	
	
	public String scrapSite(ScrappingSiteDetailsFull scrappingSiteDetail, ScrappingFullDataDetailDto dto,
			User user,Office office);
}
