package com.tricon.ruleengine.api.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
import com.tricon.ruleengine.security.JwtUser;
import com.tricon.ruleengine.service.ReportService;
import com.tricon.ruleengine.utils.Constants;

/**
 * 
 * @author Deepak.Dogra
 *
 */
@Controller
public class ReportController {
	
	@Autowired
	private ReportService reportService;

    @Autowired
    @Qualifier("jwtUserDetailsService")
    private UserDetailsService userDetailsService;

    
	
	@CrossOrigin
	@RequestMapping(value = "/report", method = RequestMethod.POST)
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public ResponseEntity<?> generateReport(@RequestBody ReportDto dto) {
		dto.setmType("t");
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "Report Created Successfully",prepareData(dto,"TR. ID-")));
	}

	@CrossOrigin
	@RequestMapping(value = "/reportcl", method = RequestMethod.POST)
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public ResponseEntity<?> generateReportCL(@RequestBody ReportDto dto) {
		dto.setmType("c");
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "Report Created Successfully",prepareData(dto,"CL. ID-")));
	}
	
	private Map<String, List<ReportResponseDto>> prepareData(ReportDto dto,String inv) {
		List<ReportResponseDto> li = reportService.getReports(dto);
		Map<String, List<ReportResponseDto>> map = new LinkedHashMap<>();
		List<ReportResponseDto> a = new ArrayList<>();
		String k = "";
		if (li != null)
			for (ReportResponseDto d : li) {
				k = d.getRd_group_run() + "). Patient ID- " + d.getPatient_id() + " Patient Name- "
						+ d.getPatient_name() + " IVF ID-" + d.getIvf_form_id() + " "+inv
						+ d.getTreatement_plan_id() +" Run By-"+d.getName();
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
		return map;

	}


	@CrossOrigin
	@RequestMapping(value = "/enreport", method = RequestMethod.POST)
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public ResponseEntity<?> generateEnancedReport(@RequestBody EnhancedReportDto dto) {
		dto.setmType("t");
		List<?> li = reportService.getEnancedReport(dto);
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "Report Created Successfully", li));
	}
    
	@CrossOrigin
	@RequestMapping(value = "/enreportcl", method = RequestMethod.POST)
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public ResponseEntity<?> generateEnancedReportCL(@RequestBody EnhancedReportDto dto) {
		dto.setmType("c");
		List<?> li = reportService.getEnancedReport(dto);
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "Report Created Successfully", li));
	}
	
}
