package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class EOBDto {

	private String eobLink;
	private int sectionId;
	private String extension;
	private String date;
	private int attachByTeamId;
	private String attachBy;
	private String attachByLastName;
	private String eobPathLink;
	private int id;
}
