package com.tricon.ruleengine.dto;

import java.util.Date;

public class ServiceCodeDateDto {
	
	
	String serviceCode;
	Date dos;
	
	
	
	public ServiceCodeDateDto(String serviceCode, Date dos) {
		super();
		this.serviceCode = serviceCode;
		this.dos = dos;
	}
	public String getServiceCode() {
		return serviceCode;
	}
	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}
	public Date getDos() {
		return dos;
	}
	public void setDos(Date dos) {
		this.dos = dos;
	}
	
	

}
