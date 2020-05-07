package com.tricon.ruleengine.dto.scrapping;

import com.tricon.ruleengine.model.sheet.FullWebsiteDataParsingSheet;

public class FullWebsiteScrapDto {

	private String status;

	public FullWebsiteDataParsingSheet getFullWebsiteDataParsingSheet() {
		return fullWebsiteDataParsingSheet;
	}

	public void setFullWebsiteDataParsingSheet(FullWebsiteDataParsingSheet fullWebsiteDataParsingSheet) {
		this.fullWebsiteDataParsingSheet = fullWebsiteDataParsingSheet;
	}

	private FullWebsiteDataParsingSheet fullWebsiteDataParsingSheet;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
