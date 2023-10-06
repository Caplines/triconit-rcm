package com.tricon.ruleengine.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tricon.ruleengine.dto.DigitizationRuleEngineResult;
import com.tricon.ruleengine.dto.EnhancedReportDto;
import com.tricon.ruleengine.dto.ReportDto;
import com.tricon.ruleengine.dto.ReportResponseDto;
import com.tricon.ruleengine.dto.RuleReportDto;
import com.tricon.ruleengine.dto.RuleReportResponseDto;
import com.tricon.ruleengine.dto.TPValidationResponseDto;
import com.tricon.ruleengine.dto.TeamwiseDataExcelDto;

public interface ReportService {

	
	public List<ReportResponseDto> getReports(ReportDto dto);
	
	public List<?> getEnancedReport(EnhancedReportDto dto);
	
	public RuleReportResponseDto getRuleReport(RuleReportDto dto);
	
	public  List<DigitizationRuleEngineResult> getReportsForGoogleSheet(ReportDto dto);
	
	public  Map<String,List<ReportResponseDto>> getReportsForSealant(ReportDto dto,boolean pdf);
	
	public Object[] generateSealntPDF(ReportDto dto);
	
	public Object[] generateSealntPDByUIData(HashMap<String,List<TPValidationResponseDto>> rdto);
	
	public RuleReportResponseDto getRuleReportAllMessage(RuleReportDto dto);
	
	
	public Object[] generateTeamwiseExcel(TeamwiseDataExcelDto  dto);
	

}
