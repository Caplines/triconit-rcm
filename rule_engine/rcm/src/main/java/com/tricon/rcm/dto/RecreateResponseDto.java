package com.tricon.rcm.dto;

import java.util.List;

import lombok.Data;

@Data
public class RecreateResponseDto {

	private List<String>serviceCodesNewClaim;
	private List<ValidateRecreateClaimResponseDto>validationResponse;
    private boolean isSecondaryValid;
}
