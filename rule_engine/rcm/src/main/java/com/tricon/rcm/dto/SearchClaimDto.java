package com.tricon.rcm.dto;

import java.util.List;

import lombok.Data;

@Data
public class SearchClaimDto {

	private List<String> clientUuid;
	private List<String> officeUuid;
	private String claimId;
	private String patientId;
	private String startDate;
	private String endDate;
	private List<Integer> ageCategory;
	private List<String> claimStatus;
	private List<String> insuranceName;
	private List<String> insuranceType;
	private List<String> providerName;
	private List<String> providerType;
	private List<Integer> responsibleTeam;
	private String showArchive;
	private int defaultButtonType;
	
	//for pagination
	private int pageNumber;
	
}
