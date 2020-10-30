package com.tricon.ruleengine.dto;

import java.util.List;

public class ScrappingInputDto {
	
	private String officeId;
	private int scrapType;
	private boolean isdataFromUi;
	private List<ScrappingUserDataInputDto> listUd;
	private boolean onlyDisplay;
	private String username;
	private String password;
	private String start;
	private String end;
	private String locationProvider;
	private String location;
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	private String siteType;
	
	
	
	
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
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}
	public String getLocationProvider() {
		return locationProvider;
	}
	public void setLocationProvider(String locationProvider) {
		this.locationProvider = locationProvider;
	}
	
	public String getSiteType() {
		return siteType;
	}
	public void setSiteType(String siteType) {
		this.siteType = siteType;
	}
	
	
	

}
