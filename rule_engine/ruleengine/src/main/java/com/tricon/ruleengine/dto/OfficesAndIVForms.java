package com.tricon.ruleengine.dto;

import java.util.List;

import com.tricon.ruleengine.model.db.IVFormType;

public class OfficesAndIVForms {
	
	List<OfficeDto> offices;
	List<IVFormType> ivforms;
	
	
	public List<OfficeDto> getOffices() {
		return offices;
	}
	public void setOffices(List<OfficeDto> offices) {
		this.offices = offices;
	}
	public List<IVFormType> getIvforms() {
		return ivforms;
	}
	public void setIvforms(List<IVFormType> ivforms) {
		this.ivforms = ivforms;
	}
	
	
	
	

}
