package com.tricon.rcm.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class RcmClientDto {

	private String clientName;
	private String companyUuid;
	private String google_sheet_id;
	private String google_sheet_sub_id;
	private String google_sheet_sub_name;

}
