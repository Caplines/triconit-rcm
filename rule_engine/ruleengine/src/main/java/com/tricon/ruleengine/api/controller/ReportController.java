package com.tricon.ruleengine.api.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tricon.ruleengine.api.enums.HighLevelReportTypeEnum;
import com.tricon.ruleengine.dto.EnhancedReportDto;
import com.tricon.ruleengine.dto.GenericResponse;
import com.tricon.ruleengine.dto.ReportDto;
import com.tricon.ruleengine.dto.ReportResponseDto;
import com.tricon.ruleengine.service.ReportService;

/**
 * 
 * @author Deepak.Dogra
 *
 */
@Controller
public class ReportController {
	
	@Autowired
	private ReportService reportService;

	
	@CrossOrigin
	@RequestMapping(value = "/report", method = RequestMethod.POST)
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public ResponseEntity<?> generateReport(@RequestBody ReportDto dto) {
		List<ReportResponseDto> li = reportService.getReports(dto);
		Map<String, List<ReportResponseDto>> map = new LinkedHashMap<>();
		List<ReportResponseDto> a = new ArrayList<>();
		String k = "";
		if (li != null)
			for (ReportResponseDto d : li) {
				k = d.getRd_group_run() + "). Patient ID- " + d.getPatient_id() + " Patient Name- "
						+ d.getPatient_name() + " IVF ID-" + d.getIvf_form_id() + " TR. ID-"
						+ d.getTreatement_plan_id();
				if (map.containsKey(k)) {
					// if the key has already been used,
					// we'll just grab the array list and add the value to it
					a = (List<ReportResponseDto>) map.get(k + "");

					a.add(d);
				} else {
					// if the key hasn't been used yet,
					// we'll create a new ArrayList<String> object, add the value
					// and put it in the array list with the new key
					a = new ArrayList<>();
					a.add(d);
					map.put(k + "", a);
				}

			}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "Report Created Successfully", map));
	}

	
	@CrossOrigin
	@RequestMapping(value = "/enreport", method = RequestMethod.GET)
	//@PreAuthorize("hasAnyRole('USER', 'ADMIN')")//@RequestBody EnhancedReportDto dto
	public ResponseEntity<?> generateEnancedReport() {
		EnhancedReportDto dto=new EnhancedReportDto();
		//dto.setOfficeId("da0c77a8-aaaf-11e8-8544-8c16451459cd");//Liberty..
		//dto.setStartDate("03/11/2018");
		//dto.setEndDate("03/11/2019");
		//dto.setPatId("7152");
		//dto.setTpId("6067");
		//dto.setReportType(HighLevelReportTypeEnum.BATCH.getType());
		//dto.setReportType(HighLevelReportTypeEnum.BATCH_NUM.getType());
		//dto.setReportType(HighLevelReportTypeEnum.TXPLAN.getType());
		//dto.setReportType(HighLevelReportTypeEnum.TXPLAN_NUM.getType());
		
		
		List<?> li = reportService.getEnancedReport(dto);
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "Report Created Successfully", li));
	}
    
	
}
