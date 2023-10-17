package com.tricon.rcm.dto.customquery;

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
	String getCreatedDate();
	String getUploadedByTeam();
	String getUploadedByUserUuid();
	String getUserUuid();
	
}
