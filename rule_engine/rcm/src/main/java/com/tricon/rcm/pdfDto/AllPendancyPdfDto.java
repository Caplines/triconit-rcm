package com.tricon.rcm.pdfDto;

import java.util.Map;

import lombok.Data;

@Data
public class AllPendancyPdfDto {

	private String officeName;
	private Map<String, Object> sortedCounts1;
	private Map<String, Object> sortedDates1;
	private Map<String, Object> sortedPending;
}
