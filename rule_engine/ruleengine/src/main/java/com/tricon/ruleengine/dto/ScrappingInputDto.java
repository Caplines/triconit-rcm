package com.tricon.ruleengine.dto;

import java.util.List;

public class ScrappingInputDto {
	
	private String officeId;
	private int scrapType;
	private boolean isdataFromUi;
	private List<ScrappingUserDataInputDto> listUd;
	private boolean onlyDisplay;
	
	public String getOfficeId() {
		return officeId;
	}
	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}

	
	public int getScrapType() {
		return scrapType;
	}
	public void setScrapType(int scrapType) {
		this.scrapType = scrapType;
	}
	public boolean isIsdataFromUi() {
		return isdataFromUi;
	}
	public void setIsdataFromUi(boolean isdataFromUi) {
		this.isdataFromUi = isdataFromUi;
	}
	public List<ScrappingUserDataInputDto> getListUd() {
		return listUd;
	}
	public void setListUd(List<ScrappingUserDataInputDto> listUd) {
		this.listUd = listUd;
	}
	public boolean isOnlyDisplay() {
		return onlyDisplay;
	}
	public void setOnlyDisplay(boolean onlyDisplay) {
		this.onlyDisplay = onlyDisplay;
	}
	
	
	
	
	
	
	

}
