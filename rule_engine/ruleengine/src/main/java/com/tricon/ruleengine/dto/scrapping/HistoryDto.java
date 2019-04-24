package com.tricon.ruleengine.dto.scrapping;

public class HistoryDto {
	
	private String code;
	private String description;
	private String tooth;
	private String surface;
	private String dos;
	
	/*
	public HistoryDto(String code, String description, String tooth, String surface, String dos) {
		super();
		this.code = code;
		this.description = description;
		this.tooth = tooth;
		this.surface = surface;
		this.dos = dos;
	}
	*/
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTooth() {
		return tooth;
	}
	public void setTooth(String tooth) {
		this.tooth = tooth;
	}
	public String getSurface() {
		return surface;
	}
	public void setSurface(String surface) {
		this.surface = surface;
	}
	public String getDos() {
		return dos;
	}
	public void setDos(String dos) {
		this.dos = dos;
	}
	
	

}
