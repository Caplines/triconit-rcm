package com.tricon.ruleengine.dao;

import java.util.List;

import com.tricon.ruleengine.dto.EnhancedReportDto;
import com.tricon.ruleengine.dto.RcmClaimDto;
import com.tricon.ruleengine.dto.ReportDto;
import com.tricon.ruleengine.dto.ReportResponseDto;
import com.tricon.ruleengine.dto.RuleMessageDetailDto;
import com.tricon.ruleengine.dto.RuleReportDto;
import com.tricon.ruleengine.dto.RuleStatusDto;
import com.tricon.ruleengine.model.db.Office;

public interface ReportDao {

	public List<ReportResponseDto> getReports(ReportDto dto);
	
	public List<?> getEnancedReport(EnhancedReportDto dto);
	
	public List<RuleMessageDetailDto> getRuleReports(RuleReportDto dto);
	
	public List<RuleMessageDetailDto> getRuleReportsAll(RuleReportDto dto);
	
	public List<ReportResponseDto> getReportsForSealant(ReportDto dto);
	
	public List<RuleStatusDto> getFailedRulesByData(RcmClaimDto dto,Office office);
	
	public int getFailedRulesByDataMaxGroupRun(RcmClaimDto dto,Office office);
}
