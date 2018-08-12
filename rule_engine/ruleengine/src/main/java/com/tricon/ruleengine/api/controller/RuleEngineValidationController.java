package com.tricon.ruleengine.api.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import com.tricon.ruleengine.dto.TPValidationResponseDto;
import com.tricon.ruleengine.dto.TreatmentPlanBatchValidationDto;
import com.tricon.ruleengine.dto.TreatmentPlanValidationDto;
import com.tricon.ruleengine.logger.RuleEngineLogger;
import com.tricon.ruleengine.service.TreatmentPlanService;
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
	TreatmentPlanService tPService;
	
	@CrossOrigin
	@GetMapping
	@RequestMapping(value = "/appdebug/{fname}")
	public void readLogFile(@PathVariable(value = "fname", required = true) String name,
			HttpServletRequest request,HttpServletResponse response) {
		
		System.out.println("ddddddddddd");
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
		System.out.println("calledddddddddd-----------");
		
		
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
		System.out.println("calledddddddddd-----------");
		
		
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", map));
	}

}
