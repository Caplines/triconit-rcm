package com.tricon.rcm.dto;

import java.util.List;

import lombok.Data;

@Data
public class ClaimNotesDto {

	String claimUuid;
	List<ClaimNoteDto> data;
}
