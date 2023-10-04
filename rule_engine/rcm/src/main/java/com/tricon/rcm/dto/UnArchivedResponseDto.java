package com.tricon.rcm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UnArchivedResponseDto {

	private String message  ;
	private Boolean unArchiveStatus;
	
}
