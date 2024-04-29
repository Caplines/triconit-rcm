package com.tricon.rcm.dto;

import java.util.List;

import com.tricon.rcm.dto.customquery.ProductionDto;

import lombok.Data;

@Data
public class ProductionForCDP {

	 private List<ProductionDto> cdpForInsuranceFollowUp;
	 private List<ProductionDto> cdpForAppeal;
}
