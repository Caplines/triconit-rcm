package com.tricon.ruleengine.dto;

import java.util.List;
import java.util.Map;

public class TeamwiseDataExcelDto {

	
	Map<String, List<ReportResponseDto>> data;
	String d1;
	String d2;
	
	public Map<String, List<ReportResponseDto>> getData() {
		return data;
	}
	public void setData(Map<String, List<ReportResponseDto>> data) {
		this.data = data;
	}
	public String getD1() {
		return d1;
	}
	public void setD1(String d1) {
		this.d1 = d1;
	}
	public String getD2() {
		return d2;
	}
	public void setD2(String d2) {
		this.d2 = d2;
	}
	
	
}
