package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class EOBDto {

	private String eobLink;
	private int sectionId;
	private String extension;
	private String date;
	private String attachByTeam;
	private String attachBy;
	private String eobPathLink;
	private int id;
}
