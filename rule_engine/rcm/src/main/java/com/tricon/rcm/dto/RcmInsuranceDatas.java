package com.tricon.rcm.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class RcmInsuranceDatas {

	String officeName;
	List<InsuranceFromRuleEngine> data= new ArrayList<>();
}
