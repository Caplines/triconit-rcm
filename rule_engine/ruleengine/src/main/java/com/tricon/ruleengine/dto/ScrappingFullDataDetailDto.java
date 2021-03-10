package com.tricon.ruleengine.dto;

import java.util.ArrayList;
import java.util.List;

public class ScrappingFullDataDetailDto {
	
	private int siteId;
    private int siteDetailId;
    private String userName;
    private String password;
    /*private String sheetId;
    private String sheetSubId;*/
    private String officeId;
    private String siteName;
    private String siteUrl;
    
	private boolean patientId;
	private boolean firstName;
	private boolean lastName;
	private boolean dob;
    private boolean enrolleeId;
	private boolean ssnNumber;
	private boolean locationProvider;
	private boolean memberId;
	private boolean gradePay;
	private boolean  otp;
	private String  otpValue;
	
	
	private boolean subscribersFirstName;
	private boolean subscribersLastName;
	private boolean subscribersDob;
	
	private String processId;
	
	
	
    
    ///
    List<PatientScrapSearchDto> dto= new ArrayList<>();
    ///
    
	public int getSiteId() {
		return siteId;
	}
	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}
	public int getSiteDetailId() {
		return siteDetailId;
	}
	public void setSiteDetailId(int siteDetailId) {
		this.siteDetailId = siteDetailId;
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
	/*public String getSheetId() {
		return sheetId;
	}
	public void setSheetId(String sheetId) {
		this.sheetId = sheetId;
	}
	public String getSheetSubId() {
		return sheetSubId;
	}
	public void setSheetSubId(String sheetSubId) {
		this.sheetSubId = sheetSubId;
	}*/
	public String getOfficeId() {
		return officeId;
	}
	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}
	public String getSiteName() {
		return siteName;
	}
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	public String getSiteUrl() {
		return siteUrl;
	}
	public void setSiteUrl(String siteUrl) {
		this.siteUrl = siteUrl;
	}
	public List<PatientScrapSearchDto> getDto() {
		return dto;
	}
	public void setDto(List<PatientScrapSearchDto> dto) {
		this.dto = dto;
	}
	public boolean isPatientId() {
		return patientId;
	}
	public void setPatientId(boolean patientId) {
		this.patientId = patientId;
	}
	public boolean isFirstName() {
		return firstName;
	}
	public void setFirstName(boolean firstName) {
		this.firstName = firstName;
	}
	public boolean isLastName() {
		return lastName;
	}
	public void setLastName(boolean lastName) {
		this.lastName = lastName;
	}
	public boolean isDob() {
		return dob;
	}
	public void setDob(boolean dob) {
		this.dob = dob;
	}
	public boolean isEnrolleeId() {
		return enrolleeId;
	}
	public void setEnrolleeId(boolean enrolleeId) {
		this.enrolleeId = enrolleeId;
	}
	public boolean isSsnNumber() {
		return ssnNumber;
	}
	public void setSsnNumber(boolean ssnNumber) {
		this.ssnNumber = ssnNumber;
	}
	public boolean isLocationProvider() {
		return locationProvider;
	}
	public void setLocationProvider(boolean locationProvider) {
		this.locationProvider = locationProvider;
	}
	public boolean isMemberId() {
		return memberId;
	}
	public void setMemberId(boolean memberId) {
		this.memberId = memberId;
	}
	public boolean isGradePay() {
		return gradePay;
	}
	public void setGradePay(boolean gradePay) {
		this.gradePay = gradePay;
	}
	public boolean isSubscribersFirstName() {
		return subscribersFirstName;
	}
	public void setSubscribersFirstName(boolean subscribersFirstName) {
		this.subscribersFirstName = subscribersFirstName;
	}
	public boolean isSubscribersLastName() {
		return subscribersLastName;
	}
	public void setSubscribersLastName(boolean subscribersLastName) {
		this.subscribersLastName = subscribersLastName;
	}
	public boolean isSubscribersDob() {
		return subscribersDob;
	}
	public void setSubscribersDob(boolean subscribersDob) {
		this.subscribersDob = subscribersDob;
	}
	
	public String getProcessId() {
		return processId;
	}
	public void setProcessId(String processId) {
		this.processId = processId;
	}
	public boolean isOtp() {
		return otp;
	}
	public void setOtp(boolean otp) {
		this.otp = otp;
	}
	
	public String getOtpValue() {
		return otpValue;
	}
	public void setOtpValue(String otpValue) {
		this.otpValue = otpValue;
	}

	
	
}
