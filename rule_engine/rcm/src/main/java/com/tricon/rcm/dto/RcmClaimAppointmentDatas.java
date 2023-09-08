package com.tricon.rcm.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data 
public class RcmClaimAppointmentDatas {

	
	String officeName;
    List<ClaimAppointmentDto> data= new ArrayList<>();
}
