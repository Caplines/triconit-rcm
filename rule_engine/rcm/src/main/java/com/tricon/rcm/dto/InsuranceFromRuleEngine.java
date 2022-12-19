package com.tricon.rcm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class InsuranceFromRuleEngine {

	@JsonProperty("c1")
	String insuranceCompanyId;
	
	@JsonProperty("c2")
	String name;
}
