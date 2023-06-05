package com.tricon.rcm.dto;

import java.util.Date;

import lombok.Data;

@Data
public class PendencyKeyValDto {

	String teamName;
	int teamId;
	int count;
	Date minDate;
	Date dt;
}
