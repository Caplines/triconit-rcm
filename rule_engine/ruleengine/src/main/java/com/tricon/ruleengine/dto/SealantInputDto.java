package com.tricon.ruleengine.dto;

import java.util.List;

public class SealantInputDto {

	private String officeId;
	private int scrapType;
	private List<ScrappingUserDataInputDto> listUd;
	private boolean onlyDisplay;
	private String userName;
	private String password;
	private String locationProvider;
	private String location;
	private boolean saveDataInRdbms;
	private boolean runRules;
	private int sid;
	
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
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getLocationProvider() {
		return locationProvider;
	}
	public void setLocationProvider(String locationProvider) {
		this.locationProvider = locationProvider;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public boolean isSaveDataInRdbms() {
		return saveDataInRdbms;
	}
	public void setSaveDataInRdbms(boolean saveDataInRdbms) {
		this.saveDataInRdbms = saveDataInRdbms;
	}
	public boolean isRunRules() {
		return runRules;
	}
	public void setRunRules(boolean runRules) {
		this.runRules = runRules;
	}
	public int getSid() {
		return sid;
	}
	public void setSid(int sid) {
		this.sid = sid;
	}

	
	
	
}
