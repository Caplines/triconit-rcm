package com.tricon.ruleengine.service;

import java.util.List;

import com.tricon.ruleengine.dto.ReportDto;
import com.tricon.ruleengine.dto.ReportResponseDto;

public interface ReportService {

	
	public List<ReportResponseDto> getReports(ReportDto dto);
}
