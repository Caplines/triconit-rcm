package com.tricon.rcm.dto;

import java.util.List;

import com.tricon.rcm.dto.customquery.RcmCompanyWithGsheetDto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class RcmClientResponseDto {

	private String clientName;
	private String companyUuid;
	private List<RcmCompanyWithGsheetDto>header;
}
