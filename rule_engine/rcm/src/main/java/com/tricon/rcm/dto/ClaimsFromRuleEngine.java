package com.tricon.rcm.dto;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ClaimsFromRuleEngine {

	@JsonProperty("c1")
	String claimId;
	
	@JsonProperty("c2")
	String patientId;
	
	@JsonProperty("c3")
	String patientName;
	
	@JsonProperty("c4")
	String birthDate;
	
	@JsonProperty("c5")
	String tranDate;
	
	@JsonProperty("c6")
	float submittedTotal;
	
	@JsonProperty("c7")
	String claimType;
	
	@JsonProperty("c8")
	String primSecStatus;
	
	@JsonProperty("c9")
	String providerId;
	
	@JsonProperty("c10")
	float primSecSubmittedTotal;
	
	@JsonProperty("c11")
	String primSecInsuranceCompanyId;

	@JsonProperty("c12")
	String insuranceCompanyName;
	
	@JsonProperty("c13")
	String secMemberId;
	
	@JsonProperty("c14")
	String insuranceCompanyFullAddress;
	
	@JsonProperty("c15")
	String groupNumber;
	
	@JsonProperty("c16")
	String primeSecPolicyHolder;
	
	@JsonProperty("c17")
	String homePhone;
	
	@JsonProperty("c18")
	String workPhone;
	
	@JsonProperty("c19")
	String cellPhone;
	
	//Now for Secondary
	@JsonProperty("c20")
	String primDateSent;
	
	@JsonProperty("c21")
	float primTotalPaid;
	

	
}
