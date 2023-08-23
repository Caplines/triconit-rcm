package com.tricon.rcm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
public class ClaimAttachmentsResponseDto {

	private String fileName;
	private boolean isDeleted;
	private int id;
	private int attachmentId;
}
