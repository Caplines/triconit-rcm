package com.tricon.rcm.dto.customquery;

public interface RcmClaimAttachmentDto {

	String getFileName();
	boolean getStatus();
	boolean getIsDeleted();
	int getId();
	int getAttachmentId();
	String getClaimUuid();
	String getFileLocation();
	
}
