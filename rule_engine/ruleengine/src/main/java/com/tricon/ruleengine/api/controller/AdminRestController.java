package com.tricon.ruleengine.api.controller;

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
     * in @PreAuthorize such as 'hasRole()' to determine if a user has access. Remember that the hasRole expression assumes a
     * 'ROLE_' prefix on all role names. So 'ADMIN' here is actually stored as 'ROLE_ADMIN' in database!
     **/
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
	@RequestMapping(value = "/report", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> generateReport(@RequestBody ReportDto dto) {
		return ResponseEntity.ok(reportService.getReports(dto));
	}

    @CrossOrigin
	@RequestMapping(value = "/report3", method = RequestMethod.GET)
	public ResponseEntity<?> generateReport() {
    	ReportDto dto=new ReportDto();
    	dto.setReportType(ReportTypeEnum.ReportType.OfficeId.toString());
    	dto.setReportField1("f015515d-7df2-11e8-8432-8c16451459cd");//
 		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "Report Created Successfully", reportService.getReports(dto)));
	}
}