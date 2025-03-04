package com.tricon.rcm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DueBalDto {

	@JsonProperty("c1")
	String dueBal;
	
	@JsonProperty("c2")
	String resParty;////2023-08-31 09:00:00.000000 
}
