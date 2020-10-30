package com.tricon.ruleengine.dto;

import org.hibernate.criterion.Projections;

public class ScrappingSiteDetailsDto {

	private String userName;
	private String password;
	private String googleSheetId;
	private String googleSheetName;
	private String googleSubId;
	private String locationProvider;
	private int sid;
	//private String siteType;
	private String location;
	
	
	
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
	public String getGoogleSheetId() {
		return googleSheetId;
	}
	public void setGoogleSheetId(String googleSheetId) {
		this.googleSheetId = googleSheetId;
	}
	public String getGoogleSheetName() {
		return googleSheetName;
	}
	public void setGoogleSheetName(String googleSheetName) {
		this.googleSheetName = googleSheetName;
	}
	public String getGoogleSubId() {
		return googleSubId;
	}
	public void setGoogleSubId(String googleSubId) {
		this.googleSubId = googleSubId;
	}
	public String getLocationProvider() {
		return locationProvider;
	}
	public void setLocationProvider(String locationProvider) {
		this.locationProvider = locationProvider;
	}
	public int getSid() {
		return sid;
	}
	public void setSid(int sid) {
		this.sid = sid;
	}
	
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	
	
	
	
	
	
}
