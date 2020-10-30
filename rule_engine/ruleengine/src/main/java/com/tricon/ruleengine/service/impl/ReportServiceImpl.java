package com.tricon.ruleengine.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tricon.ruleengine.dao.ReportDao;
import com.tricon.ruleengine.dto.EnhancedReportDto;
import com.tricon.ruleengine.dto.ReportDto;
import com.tricon.ruleengine.dto.ReportResponseDto;
import com.tricon.ruleengine.dto.RuleMessageDetailDto;
import com.tricon.ruleengine.dto.RuleReportDto;
import com.tricon.ruleengine.dto.RuleReportResponseDto;
import com.tricon.ruleengine.service.ReportService;

@Transactional
@Service
public class ReportServiceImpl implements ReportService {

	@Autowired
	ReportDao rd;

	@Override
	public List<ReportResponseDto> getReports(ReportDto dto) {

		List<ReportResponseDto> reports = rd.getReports(dto);
		// TODO Auto-generated method stub

		return reports;

	}

	@Override
	public List<?> getEnancedReport(EnhancedReportDto dto) {
		List<?> reports = rd.getEnancedReport(dto);
		// TODO Auto-generated method stub
		return reports;
	}

	@Override
	public RuleReportResponseDto getRuleReport(RuleReportDto dto) {

		List<RuleMessageDetailDto> li = rd.getRuleReports(dto);
		RuleReportResponseDto d = new RuleReportResponseDto();
		d.setUniqueAllPats(0);
		d.setUniqueFailPats(0);
		d.setUniquePassPats(0);
		d.setUniqueAlertPats(0);
		List<RuleMessageDetailDto> fi= new ArrayList<>();
		if (li != null) {
			Set<String> all = new HashSet<>();
			Set<String> fail = new HashSet<>();
			Set<String> pass = new HashSet<>();
			Set<String> alert = new HashSet<>();

			for (RuleMessageDetailDto dt : li) {

				all.add(dt.getPatientId());
				if (dt.getMessageType() == 2) {
					pass.add(dt.getPatientId());
				} else if (dt.getMessageType() == 1) {
					fi.add(dt);
					fail.add(dt.getPatientId());
				} else if (dt.getMessageType() == 3) {
					alert.add(dt.getPatientId());
				}
				
				

			}
			d.setUniqueAllPats(all.size());
			d.setUniqueFailPats(fail.size());
			d.setUniquePassPats(pass.size());
			d.setUniqueAlertPats(alert.size());
			d.setData(fi);
		}

		return d;
	}

}
