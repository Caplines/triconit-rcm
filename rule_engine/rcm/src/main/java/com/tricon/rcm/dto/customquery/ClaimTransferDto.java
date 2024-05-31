package com.tricon.rcm.dto.customquery;

import java.util.List;

import lombok.Data;

@Data
public class ClaimTransferDto {

	private List<String>claimUuid;
	private String remarks;
}
