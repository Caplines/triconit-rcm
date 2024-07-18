package com.tricon.rcm.pdfDto;

import java.util.List;

import com.tricon.rcm.dto.ProductionAgeWiseDto;
import com.tricon.rcm.dto.ProductionCurrentStatusWiseDto;

import lombok.Data;

@Data
public class AgingPdfDto {

	private List<ProductionAgeWiseDto> listOfAgeWiseData;
	private List<ProductionCurrentStatusWiseDto> listOfCurrentStatusWiseData;
	
}
