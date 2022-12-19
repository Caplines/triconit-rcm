package com.tricon.rcm.dto;


import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class RcmClaimDataDto {
	
	public String officeName;
	public List<ClaimsFromRuleEngine> data = new ArrayList<>();

}
