package com.tricon.rcm.dto.customquery;

import java.util.Date;

public interface FreshClaimDetailsDto {

	String getOfficeUuid();

	String getOfficeName();

	Date getClaimFetchDate();

	int getClaimCount();

	String getCreateByName();

	String getSource();

	int getStatus();
}
