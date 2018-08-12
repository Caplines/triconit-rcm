package com.tricon.ruleengine.dao;

import java.util.List;

import com.tricon.ruleengine.dto.ReportDto;
import com.tricon.ruleengine.dto.ReportResponseDto;
import com.tricon.ruleengine.model.db.Reports;

public interface ReportDao {

	public List<ReportResponseDto> getReports(ReportDto dto);
}
