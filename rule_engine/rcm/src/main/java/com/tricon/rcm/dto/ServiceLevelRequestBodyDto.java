package com.tricon.rcm.dto;

import java.util.List;

import lombok.Data;

@Data
public class ServiceLevelRequestBodyDto {

	private String notes;
	private String btpReason;
	private String adjustmentReason;
	private double billToPatientAmount;
	private double adjustmentAmount;
	private double paidAmount;
	private double allowedAmount;
	private String serviceCode;
	private String action;
	private String tooth;
	private String surface;
	private int sNo;
	private String estPrimary;
	private String fee;
	private boolean finalSubmit;
	private List<ServiceLevelNotes>serviceCodeNotes;
}
