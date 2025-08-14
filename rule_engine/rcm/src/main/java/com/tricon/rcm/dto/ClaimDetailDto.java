package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class ClaimDetailDto {

	String apptId;
	String description;
	String estInsurance;
	String estSecondary;
	String estPrimary;
	String fee;
	String id;
	String lineItem;
	String patientPortion;
	String patientPortionSec;
	String providerLastName;
	String serviceCode;
	String status;
	String surface;
	String tooth;
	ClaimPatientDetail pd;
	ClaimDataDetails details;
}



