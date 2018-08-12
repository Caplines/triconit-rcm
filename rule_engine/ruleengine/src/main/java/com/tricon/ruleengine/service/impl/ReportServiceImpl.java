package com.tricon.ruleengine.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tricon.ruleengine.dao.ReportDao;
import com.tricon.ruleengine.dto.ReportDto;
import com.tricon.ruleengine.dto.ReportResponseDto;
import com.tricon.ruleengine.model.db.Reports;
import com.tricon.ruleengine.service.ReportService;

@Transactional
@Service
public class ReportServiceImpl implements ReportService{

	@Autowired ReportDao rd; 
	
	@Override
	public List<ReportResponseDto> getReports(ReportDto dto) {
		
		List<ReportResponseDto> reports= rd.getReports(dto);
		// TODO Auto-generated method stub
		
		return reports;
		
	}

}
