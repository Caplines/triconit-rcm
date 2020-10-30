package com.tricon.ruleengine.dao;

import java.util.List;

import com.tricon.ruleengine.dto.EnhancedReportDto;
import com.tricon.ruleengine.dto.ReportDto;
import com.tricon.ruleengine.dto.ReportResponseDto;
import com.tricon.ruleengine.dto.RuleMessageDetailDto;
import com.tricon.ruleengine.dto.RuleReportDto;

public interface ReportDao {

	public List<ReportResponseDto> getReports(ReportDto dto);
	
	public List<?> getEnancedReport(EnhancedReportDto dto);
	
	public List<RuleMessageDetailDto> getRuleReports(RuleReportDto dto);
}
