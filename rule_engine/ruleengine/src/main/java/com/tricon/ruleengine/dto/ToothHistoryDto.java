package com.tricon.ruleengine.dto;

public class ToothHistoryDto {
	
	String historyCode;
	String historyDos;
	String historyTooth;
	
	
	
	public ToothHistoryDto(String historyCode, String historyDos, String historyTooth) {
		super();
		this.historyCode = historyCode;
		this.historyDos = historyDos;
		this.historyTooth = historyTooth;
	}
	public String getHistoryCode() {
		return historyCode;
	}
	public void setHistoryCode(String historyCode) {
		this.historyCode = historyCode;
	}
	public String getHistoryDos() {
		return historyDos;
	}
	public void setHistoryDos(String historyDos) {
		this.historyDos = historyDos;
	}
	public String getHistoryTooth() {
		return historyTooth;
	}
	public void setHistoryTooth(String historyTooth) {
		this.historyTooth = historyTooth;
	}

	
	
}
