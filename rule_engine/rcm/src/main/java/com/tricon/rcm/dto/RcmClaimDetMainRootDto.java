package com.tricon.rcm.dto;

import java.util.HashMap;
import java.util.List;

import lombok.Data;

@Data
public class RcmClaimDetMainRootDto {

	String message;
	HashMap<String,List<ClaimDetailDto>> data;
}
