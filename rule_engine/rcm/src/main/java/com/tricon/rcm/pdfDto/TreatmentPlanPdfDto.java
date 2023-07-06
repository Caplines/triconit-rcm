package com.tricon.rcm.pdfDto;

import lombok.Data;

@Data
public class TreatmentPlanPdfDto {

	 private String provider;
	 private String service;
	 private String description;
	 private double fee;
     private String datePlan;
     private String appt;
     private String tth;
     private String surf;
     private double ins;
     private double pat;
	
}
