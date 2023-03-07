package com.tricon.rcm.dto;

import java.util.List;

import lombok.Data;

@Data
public class ClaimServiceValidationGSheet {

	//Store as Hashmap with Service code as Key
	List<ClaimServiceValidationGSheetData> data;
}
