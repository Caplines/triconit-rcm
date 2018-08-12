package com.tricon.ruleengine.dto;

import java.util.Date;
import java.util.List;

public class HistoryMatcherDto {
	
	List<ServiceCodeDateDto> serviceCodeDate;
	Date mostRecentDos;//not relevant
	String historyTooth;//not relevant
    int count;
	
    
    
    
    public HistoryMatcherDto(List<ServiceCodeDateDto> serviceCodeDate, Date mostRecentDos,
			String historyTooth, int count) {
		super();
		this.serviceCodeDate = serviceCodeDate;
		this.mostRecentDos = mostRecentDos;
		this.historyTooth = historyTooth;
		this.count = count;
	}
	public List<ServiceCodeDateDto> getServiceCodeDate() {
		return serviceCodeDate;
	}
	public void setServiceCodeDate(List<ServiceCodeDateDto> serviceCodeDate) {
		this.serviceCodeDate = serviceCodeDate;
	}
	public Date getMostRecentDos() {
		return mostRecentDos;
	}
	public void setMostRecentDos(Date mostRecentDos) {
		this.mostRecentDos = mostRecentDos;
	}
	public String getHistoryTooth() {
		return historyTooth;
	}
	public void setHistoryTooth(String historyTooth) {
		this.historyTooth = historyTooth;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
    
    
	
 	
	
}
