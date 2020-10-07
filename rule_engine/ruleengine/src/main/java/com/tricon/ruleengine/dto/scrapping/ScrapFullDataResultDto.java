package com.tricon.ruleengine.dto.scrapping;

import java.util.List;


public class ScrapFullDataResultDto {

	
	private int count;
	private  List<ScrapPatient> listPatient;
	
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public List<ScrapPatient> getListPatient() {
		return listPatient;
	}
	public void setListPatient(List<ScrapPatient> listPatient) {
		this.listPatient = listPatient;
	}
	
	
	
}
