package com.tricon.rcm.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RcmClientGSheetDto {

	private String name;
	private String google_sheet_id;
	private String google_sheet_sub_id;
	private String google_sheet_sub_name;
}
