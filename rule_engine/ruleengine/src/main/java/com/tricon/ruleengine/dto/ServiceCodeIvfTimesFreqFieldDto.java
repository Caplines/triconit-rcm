package com.tricon.ruleengine.dto;

public class ServiceCodeIvfTimesFreqFieldDto {

	String serviceCode;
	String fieldName;
	String freqency;
	int count;
	int times;
	String tooth;
	String dos;
	String surface;
	String serviceCodeHis;//Used in humana and FMX Pano
	
	
	
	

	public ServiceCodeIvfTimesFreqFieldDto(String serviceCode, String fieldName, String freqency,int count,int times,String surface) {
		super();
		this.serviceCode = serviceCode;
		this.fieldName = fieldName;
		this.freqency = freqency;
		this.count = count;
		this.times = times;
		this.surface = surface;
		
		
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFreqency() {
		return freqency;
	}

	public void setFreqency(String freqency) {
		this.freqency = freqency;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}

	public String getTooth() {
		return tooth;
	}

	public void setTooth(String tooth) {
		this.tooth = tooth;
	}

	public String getDos() {
		return dos;
	}

	public void setDos(String dos) {
		this.dos = dos;
	}

	public String getSurface() {
		return surface;
	}

	public void setSurface(String surface) {
		this.surface = surface;
	}

	public String getServiceCodeHis() {
		return serviceCodeHis;
	}

	public void setServiceCodeHis(String serviceCodeHis) {
		this.serviceCodeHis = serviceCodeHis;
	}
	
	

}
