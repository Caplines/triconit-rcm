package com.tricon.rcm.dto.customquery;

import java.util.Date;

public interface RcmClaimAttachmentDto {

	String getFileName();
	boolean getStatus();
	boolean getIsDeleted();
	Integer getId();
	Integer getAttachmentId();
	String getClaimUuid();
	String getFileLocation();
	String getRenameFile();
	String getCreatedBy();
	Date getCreatedDate();
	String getUploadedByTeam();
	String getUploadedByUserUuid();
	String getUserUuid();
	
}
