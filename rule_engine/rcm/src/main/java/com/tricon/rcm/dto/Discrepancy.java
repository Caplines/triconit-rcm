package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class Discrepancy {

	String claimId;
	String claimUUid;
	String patientName;
	String patientId;
	boolean primary;
	boolean archived;
}
