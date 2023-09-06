package com.tricon.rcm.dto;

import java.util.HashMap;
import java.util.List;

import lombok.Data;

@Data
public class RcmClaimAppointmentMainRootDto {

	String message;
	HashMap<String,List<ClaimAppointmentDto>> data;
}
