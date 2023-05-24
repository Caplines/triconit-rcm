package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class ProivderHelpingSheetDto {

	
	String date;
	String officeName;
	String treatingProvider;
	String type;
	
	public ProivderHelpingSheetDto(String date, String officeName, String treatingProvider, String type) {
		super();
		this.date = date;
		this.officeName = officeName;
		this.treatingProvider = treatingProvider;
		this.type = type;
	}
	
	
}
