package com.tricon.rcm.dto;

import com.tricon.rcm.db.entity.RcmCompany;
import com.tricon.rcm.security.JwtUser;

import lombok.Data;

@Data
public class PartialHeader {
	
	String clientName;
	String role;
	int teamId;
	RcmCompany company;
	JwtUser jwtUser;
	

}
