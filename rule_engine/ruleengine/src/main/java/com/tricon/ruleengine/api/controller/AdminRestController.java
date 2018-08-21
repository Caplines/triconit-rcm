package com.tricon.ruleengine.api.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tricon.ruleengine.api.enums.ReportTypeEnum;
import com.tricon.ruleengine.dto.GenericResponse;
import com.tricon.ruleengine.dto.ReportDto;
import com.tricon.ruleengine.dto.ReportResponseDto;
import com.tricon.ruleengine.dto.UserRegistrationDto;
import com.tricon.ruleengine.service.ReportService;
import com.tricon.ruleengine.service.UserService;

@RestController
@RequestMapping("admin")
public class AdminRestController {

	@Autowired
	private UserService userService;

	@Autowired
	private ReportService reportService;

	/**
	 * in @PreAuthorize such as 'hasRole()' to determine if a user has access.
	 * Remember that the hasRole expression assumes a 'ROLE_' prefix on all role
	 * names. So 'ADMIN' here is actually stored as 'ROLE_ADMIN' in database!
	 **///
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<?> getProtectedGreeting() {
		return ResponseEntity.ok("Greetings from admin protected method!");
	}

	@CrossOrigin
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> registerUser(@RequestBody UserRegistrationDto dto) {
		return ResponseEntity.ok(userService.registerUser(dto));
	}


	@CrossOrigin
	@RequestMapping(value = "/report3", method = RequestMethod.GET)
	public ResponseEntity<?> generateReport() {
		ReportDto dto = new ReportDto();
		dto.setReportType(ReportTypeEnum.ReportType.Date.toString());
		dto.setReportField1("08/14/2018");
		dto.setOfficeId("fc1d7afd-7df2-11e8-8432-8c16451459cd");//
		List<ReportResponseDto> li = reportService.getReports(dto);
		Map<String, List<ReportResponseDto>> map = new LinkedHashMap<>();
		List<ReportResponseDto> a = new ArrayList<>();
		String k="";
		if (li != null)
			for (ReportResponseDto d : li) {
                k=d.getRd_group_run()+"). Patient ID- "+d.getPatient_id()+ " Patient Name- "+d.getPatient_name() + " IVF ID-"+d.getIvf_form_id() +" TR. ID-"+d.getTreatement_plan_id();
				if (map.containsKey(k)) {
					// if the key has already been used,
					// we'll just grab the array list and add the value to it
					a = (List<ReportResponseDto>) map.get(k + "");
				//System.out.println(d.getRd_created_date().toLocalDateTime().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
					
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
}