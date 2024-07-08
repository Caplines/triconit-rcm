package com.tricon.rcm.dto.customquery;

import java.sql.Date;

public interface RcmClaimSubmissionDto {

	String getClaimUuid();
	Date getEsDate();
	String getChannel();
	boolean getAttachmentSend();
	boolean getPreauth();
	boolean getRefferalLetter();
	String getClaimNumber();
	String getPreauthNo();
	String getProviderRefNo();
	String getFName();
	String getLName();
	String getUuid();
	String getEsTime();
	boolean getCleanClaim();

}
