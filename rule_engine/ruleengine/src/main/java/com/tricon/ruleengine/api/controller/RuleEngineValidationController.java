package com.tricon.ruleengine.api.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tricon.ruleengine.dto.GenericResponse;
import com.tricon.ruleengine.dto.PatientTreamentDto;
import com.tricon.ruleengine.dto.ReportDto;
import com.tricon.ruleengine.dto.ReportResponseDto;
import com.tricon.ruleengine.dto.TPValidationResponseDto;
import com.tricon.ruleengine.dto.TreatmentPlanBatchValidationDto;
import com.tricon.ruleengine.dto.TreatmentPlanDto;
import com.tricon.ruleengine.dto.TreatmentPlanValidationDto;
import com.tricon.ruleengine.logger.RuleEngineLogger;
import com.tricon.ruleengine.service.ReportService;
import com.tricon.ruleengine.service.TreatmentPlanService;
import com.tricon.ruleengine.service.UserService;
import com.tricon.ruleengine.utils.Constants;

/**
 * @author Deepak.Dogra
 *
 */

@RestController
public class RuleEngineValidationController {

	static Class<?> clazz = RuleEngineValidationController.class;
	
	@Value("${app.debug.folder}")
	private String appLogFolder;
	
	@Autowired
	private ReportService reportService;

	
	@Autowired
	TreatmentPlanService tPService;
	
	@CrossOrigin
	@GetMapping
	@RequestMapping(value = "/appdebug/{fname}")
	public void readLogFile(@PathVariable(value = "fname", required = true) String name,
			HttpServletRequest request,HttpServletResponse response) {
		
		 try {
		      // get your file as InputStream
			 InputStream is = new FileInputStream(appLogFolder+name);
		      // copy it to response's OutputStream
		      org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
		      response.setContentType("text/plain");
		      response.flushBuffer();
		    } catch (IOException ex) {
		      //log.info("Error writing file to output stream. Filename was '{}'", name, ex);
		      throw new RuntimeException("IOError writing file to output stream");
		    }	}

	@CrossOrigin
	@PostMapping
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@RequestMapping(value = "/validateTreatmentPlan", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> validateTreatementPlan(@RequestBody TreatmentPlanValidationDto dto) {

		//dto.setTreatmentPlanId("22095");
		Map<String,List<TPValidationResponseDto>> map=	tPService.validateTreatmentPlan(dto);
		RuleEngineLogger.generateLogs(clazz, "RuleEngineValidationController", Constants.rule_log_debug,null);
		
		
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", map));
	}

	
	@CrossOrigin
	@PostMapping
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@RequestMapping(value = "/validateTreatmentPlanPreBatch", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> validateTreatementPlanPreBatch(@RequestBody TreatmentPlanBatchValidationDto dto) {

		//dto.setTreatmentPlanId("22095");
		Map<String,List<TPValidationResponseDto>> map=	tPService.validateTreatmentPlanPreBatch(dto);
		RuleEngineLogger.generateLogs(clazz, "RuleEngineValidationController", Constants.rule_log_debug,null);
		System.out.println("calledddddddddd-----------");
		
		
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", map));
	}

	@CrossOrigin
	@PostMapping
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@RequestMapping(value = "/validateTreatmentPlanBatch", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> validateTreatementPlanBatch(@RequestBody TreatmentPlanValidationDto dto) {

		//dto.setTreatmentPlanId("22095");
		Map<String,List<TPValidationResponseDto>> map=	tPService.validateTreatmentPlan(dto);
		RuleEngineLogger.generateLogs(clazz, "RuleEngineValidationController", Constants.rule_log_debug,null);
		
		
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", map));
	}
	
	@CrossOrigin
	@RequestMapping(value = "/report", method = RequestMethod.POST)
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public ResponseEntity<?> generateReport(@RequestBody ReportDto dto) {
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
	@RequestMapping(value = "/generateTreatmentId", method = RequestMethod.POST)
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public ResponseEntity<?> generatTreatmentId(@RequestBody TreatmentPlanDto dto) {
		Map<String,List<PatientTreamentDto>> map = tPService.getTreatments(dto);
		
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "Treatment Ids Fetched Successfully", map));
	}

	@CrossOrigin
	@RequestMapping(value = "/generateTreatmentId1", method = RequestMethod.GET)
	public ResponseEntity<?> generatTreatmentId1() {
		TreatmentPlanDto dto= new TreatmentPlanDto();
		dto.setOfficeId("fc1d7afd-7df2-11e8-8432-8c16451459cd");
		dto.setPatientId("9396");
		
		Map<String,List<PatientTreamentDto>> map = tPService.getTreatments(dto);
		
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "Treatment Ids Fetched Successfully", map));
	}

}
