package com.tricon.ruleengine.service;

import java.util.List;

import com.tricon.ruleengine.dto.EnhancedReportDto;
import com.tricon.ruleengine.dto.ReportDto;
import com.tricon.ruleengine.dto.ReportResponseDto;
import com.tricon.ruleengine.dto.RuleReportDto;
import com.tricon.ruleengine.dto.RuleReportResponseDto;

public interface ReportService {

	
	public List<ReportResponseDto> getReports(ReportDto dto);
	
	public List<?> getEnancedReport(EnhancedReportDto dto);
	
	public RuleReportResponseDto getRuleReport(RuleReportDto dto);
	
}
