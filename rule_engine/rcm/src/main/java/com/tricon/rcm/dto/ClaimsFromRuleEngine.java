package com.tricon.rcm.dto;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ClaimsFromRuleEngine {

	@JsonProperty("c1")
	String patientId;
	
	@JsonProperty("c2")
	String claimId;
	
	@JsonProperty("c3")
	String empty;
	
	@JsonProperty("c4")
	String tranDate;
	
	@JsonProperty("c5")
	float submittedTotal;
	
	@JsonProperty("c6")
	float primSubmittedTotal;
	
	@JsonProperty("c7")
	float primTotalPaid;
	
	@JsonProperty("c8")
	String primDateSent;
	
	@JsonProperty("c9")
	float secSubmittedTotal;
	
	@JsonProperty("c10")
	String secDateSent;
	
	@JsonProperty("c11")
	String providerId;
	
	@JsonProperty("c12")
	String primInsuranceCompanyId;
	
	@JsonProperty("c13")
	String secInsuranceCompanyId;
	
	@JsonProperty("c14")
	String primStatus;
	
	@JsonProperty("c15")
	String secStatus;
	
	@JsonProperty("c16")
	String claimType;
	
	@JsonProperty("c17")
	String birthDate;
	
	@JsonProperty("c18")
	String patientName;
	
}
