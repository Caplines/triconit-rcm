package com.tricon.rcm.dto.customquery;

import java.util.Date;

public interface RcmClaimDataDto {

	
	String getClaimUuid();
	String getClaimId();
	Date getDos();
	String getPatientId();
	String getProviderOnClaim();
	String getTreatingProvider();
	int getCurrentState();
}
