package com.tricon.rcm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ClaimAppointmentDto {

	
	@JsonProperty("c1")
	String patientId;
	
	@JsonProperty("c2")
	String startDate;////2023-08-31 09:00:00.000000 
	
	@JsonProperty("c3")
	String startDate1;////2023-08-31 09:00:00.000000 

}
