package com.tricon.rcm.dto;

import java.util.List;

import com.tricon.rcm.dto.customquery.RcmClaimNoteDto;

import lombok.Data;

@Data
public class ClaimAllNotesDto {

	List<RcmClaimNoteDto> noteType; 
	List<RcmClaimNoteDto> nt;
}
