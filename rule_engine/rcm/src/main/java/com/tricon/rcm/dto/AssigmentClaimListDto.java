package com.tricon.rcm.dto;

import java.util.List;

import lombok.Data;

@Data
public class AssigmentClaimListDto {

	List<Integer> claimType;
	List<String> insuranceType;
	Integer repeatType;//1= Fresh 2 = Repeat
	List<String> clients;
	
}
