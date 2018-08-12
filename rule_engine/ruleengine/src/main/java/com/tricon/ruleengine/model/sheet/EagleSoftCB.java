package com.tricon.ruleengine.model.sheet;

public class EagleSoftCB {
	
	private String bookId;
	private String covBbookHeaderName;
	private String serviceCode;
	private String dateModified;
	private String dollars;
	private String ucr;
	private String deductibleApplies;
	private String dollarsOrPercent;
	private String percentage;
	private String forgiveDifference;
	private String coPay;
	
	
	public EagleSoftCB() {
	}

	public EagleSoftCB(String bookId, String covBbookHeaderName, String serviceCode, String dateModified,
			String dollars, String ucr, String deductibleApplies, String dollarsOrPercent, String percentage,
			String forgiveDifference, String coPay) {
		super();
		this.bookId = bookId;
		this.covBbookHeaderName = covBbookHeaderName;
		this.serviceCode = serviceCode;
		this.dateModified = dateModified;
		this.dollars = dollars;
		this.ucr = ucr;
		this.deductibleApplies = deductibleApplies;
		this.dollarsOrPercent = dollarsOrPercent;
		this.percentage = percentage;
		this.forgiveDifference = forgiveDifference;
		this.coPay = coPay;
	}
	public String getBookId() {
		return bookId;
	}
	public void setBookId(String bookId) {
		this.bookId = bookId;
	}
	public String getCovBbookHeaderName() {
		return covBbookHeaderName;
	}
	public void setCovBbookHeaderName(String covBbookHeaderName) {
		this.covBbookHeaderName = covBbookHeaderName;
	}
	public String getServiceCode() {
		return serviceCode;
	}
	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}
	public String getDateModified() {
		return dateModified;
	}
	public void setDateModified(String dateModified) {
		this.dateModified = dateModified;
	}
	public String getDollars() {
		return dollars;
	}
	public void setDollars(String dollars) {
		this.dollars = dollars;
	}
	public String getUcr() {
		return ucr;
	}
	public void setUcr(String ucr) {
		this.ucr = ucr;
	}
	public String getDeductibleApplies() {
		return deductibleApplies;
	}
	public void setDeductibleApplies(String deductibleApplies) {
		this.deductibleApplies = deductibleApplies;
	}
	public String getDollarsOrPercent() {
		return dollarsOrPercent;
	}
	public void setDollarsOrPercent(String dollarsOrPercent) {
		this.dollarsOrPercent = dollarsOrPercent;
	}
	public String getPercentage() {
		return percentage;
	}
	public void setPercentage(String percentage) {
		this.percentage = percentage;
	}
	public String getForgiveDifference() {
		return forgiveDifference;
	}
	public void setForgiveDifference(String forgiveDifference) {
		this.forgiveDifference = forgiveDifference;
	}
	public String getCoPay() {
		return coPay;
	}
	public void setCoPay(String coPay) {
		this.coPay = coPay;
	}
	
	

}
