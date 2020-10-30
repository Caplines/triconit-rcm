package com.tricon.ruleengine.dto;

import java.util.ArrayList;
import java.util.List;

public class RuleReportResponseDto {

	private int uniqueAllPats;
	private int uniquePassPats;
	private int uniqueFailPats;
	private int uniqueAlertPats;
	
	private List<RuleMessageDetailDto> data= new ArrayList<>();
	
	public int getUniqueAllPats() {
		return uniqueAllPats;
	}
	public void setUniqueAllPats(int uniqueAllPats) {
		this.uniqueAllPats = uniqueAllPats;
	}
	public int getUniquePassPats() {
		return uniquePassPats;
	}
	public void setUniquePassPats(int uniquePassPats) {
		this.uniquePassPats = uniquePassPats;
	}
	public int getUniqueFailPats() {
		return uniqueFailPats;
	}
	public void setUniqueFailPats(int uniqueFailPats) {
		this.uniqueFailPats = uniqueFailPats;
	}
	public List<RuleMessageDetailDto> getData() {
		return data;
	}
	public void setData(List<RuleMessageDetailDto> data) {
		this.data = data;
	}
	public int getUniqueAlertPats() {
		return uniqueAlertPats;
	}
	public void setUniqueAlertPats(int uniqueAlertPats) {
		this.uniqueAlertPats = uniqueAlertPats;
	}
	
	
	
	
	
}
