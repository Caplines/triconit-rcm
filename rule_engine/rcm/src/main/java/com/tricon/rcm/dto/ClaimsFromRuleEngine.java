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
	String submittedTotal;
	
	@JsonProperty("c6")
	String primSubmittedTotal;
	
	@JsonProperty("c7")
	String primTotalPaid;
	
	@JsonProperty("c8")
	String primDateSent;
	
	@JsonProperty("c9")
	String secSubmittedTotal;
	
	@JsonProperty("c10")
	String secDateSent;
	
	@JsonProperty("c11")
	String providerId;
	
	@JsonProperty("c12")
	String primInsuranceCompanyId;
	
	@JsonProperty("c13")
	String sec_insurance_company_id;
	
	@JsonProperty("c14")
	String prim_status;
	
	@JsonProperty("c15")
	String sec_status;
	
	@JsonProperty("c16")
	String claim_type;
	
	@JsonProperty("c17")
	Date birthDate;
	

	
}
