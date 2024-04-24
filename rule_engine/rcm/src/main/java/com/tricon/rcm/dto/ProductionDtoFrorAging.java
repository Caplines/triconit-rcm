package com.tricon.rcm.dto;

import java.util.List;

import lombok.Data;

@Data
public class ProductionDtoFrorAging {

	private List<ProductionAgeWiseDto> listOfAgeWiseData;
	private List<ProductionCurrentStatusWiseDto> listOfCurrentStatusWiseData;
}
