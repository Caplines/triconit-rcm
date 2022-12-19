package com.tricon.rcm.dto;

import java.util.ArrayList;

import lombok.Data;

@Data
public class RcmInsuranceDataDto {

	public String message;
	public ArrayList<RcmClaimDataDto> datas;
	
}
