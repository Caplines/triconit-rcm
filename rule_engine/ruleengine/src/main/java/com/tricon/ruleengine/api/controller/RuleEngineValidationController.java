package com.tricon.ruleengine.api.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RestController;

import com.tricon.ruleengine.dto.DiagnosticDTO;
import com.tricon.ruleengine.dto.GenericResponse;
import com.tricon.ruleengine.dto.PatientTreamentDto;
import com.tricon.ruleengine.dto.QuestionAnswerDto;
import com.tricon.ruleengine.dto.QuestionHeaderDto;
import com.tricon.ruleengine.dto.TPValidationResponseDto;
import com.tricon.ruleengine.dto.TreatmentPlanBatchValidationDto;
import com.tricon.ruleengine.dto.TreatmentPlanDto;
import com.tricon.ruleengine.dto.TreatmentPlanValidationDto;
import com.tricon.ruleengine.dto.UserInputDto;
import com.tricon.ruleengine.dto.UserInputQuestionAnswerDto;
import com.tricon.ruleengine.logger.RuleEngineLogger;
import com.tricon.ruleengine.service.EagleSoftDBAccessService;
import com.tricon.ruleengine.service.GoogleReportService;
import com.tricon.ruleengine.service.TreatmentPlanService;
import com.tricon.ruleengine.service.UserInputService;
import com.tricon.ruleengine.utils.Constants;

/**
 * @author Deepak.Dogra
 *
 */

@RestController
public class RuleEngineValidationController {

	static Class<?> clazz = RuleEngineValidationController.class;
	
	@Autowired
	UserInputService userInputService;

	@Value("${app.debug.folder}")
	private String appLogFolder;

	@Autowired
	TreatmentPlanService tPService;

	@Autowired
	EagleSoftDBAccessService es;

	@Autowired
	GoogleReportService gs;

	@CrossOrigin
	@GetMapping
	@RequestMapping(value = "/appdebug/{fname}")
	public void readLogFile(@PathVariable(value = "fname", required = true) String name, HttpServletRequest request,
			HttpServletResponse response) {

		try {
			// get your file as InputStream
			InputStream is = new FileInputStream(appLogFolder + name);
			// copy it to response's OutputStream
			org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
			response.setContentType("text/plain");
			response.flushBuffer();
		} catch (IOException ex) {
			// log.info("Error writing file to output stream. Filename was '{}'", name, ex);
			throw new RuntimeException("IOError writing file to output stream");
		}
	}

	@CrossOrigin
	@PostMapping
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@RequestMapping(value = "/validateTreatmentPlan", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> validateTreatementPlan(@RequestBody TreatmentPlanValidationDto dto) {

		// dto.setTreatmentPlanId("22095");
		Map<String, List<TPValidationResponseDto>> map=null;
		RuleEngineLogger.generateLogs(clazz, "RuleEngineUserInputController", Constants.rule_log_debug, null);
		if (dto.isInputMode()) {
		        tPService.saveDisplayUserInputsOnly(dto);
				List<QuestionHeaderDto> data1 = userInputService.getUserInput();
				UserInputDto uDto=new UserInputDto();
				uDto.setOfficeId(dto.getOfficeId());
				uDto.setTreatmentPlanId(dto.getTreatmentPlanId());
				
				List<QuestionAnswerDto> ansL=userInputService.getUserAnswers(uDto);
				
				List<QuestionHeaderDto> data1F = new ArrayList<>();
				List<Integer> qid=new ArrayList<>();
				if (ansL!=null) {
					for(QuestionAnswerDto a:ansL) {
						qid.add(a.getQuestionId());
					}
				}
				if (data1!=null) {
				for(QuestionHeaderDto q:data1) {
					if(qid.contains(q.getId())) {
						data1F.add(q);
					}
				 }
				}
				UserInputQuestionAnswerDto d=new UserInputQuestionAnswerDto();
				d.setDataHeader(data1F);
				d.setQuestionAnswer(ansL);
				return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", d));
		     
		}
		else {
			 map = tPService.validateTreatmentPlan(dto);
				RuleEngineLogger.generateLogs(clazz, "RuleEngineValidationController", Constants.rule_log_debug, null);
			 return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", map));
		}

	}

	@CrossOrigin
	@PostMapping
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@RequestMapping(value = "/validateTreatmentPlanPreBatch", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> validateTreatementPlanPreBatch(@RequestBody TreatmentPlanBatchValidationDto dto) {

		// dto.setTreatmentPlanId("22095");
		if (dto.getIvfId()!=null &&  dto.getIvfId().equals("")) {
			dto.setIvfId(null);
			
		}
		if (dto.getPatientId()!=null &&  dto.getPatientId().equals("")) {
			dto.setPatientId(null);
			
		}
		Map<String, List<TPValidationResponseDto>> map = tPService.validateTreatmentPlanPreBatch(dto);
		RuleEngineLogger.generateLogs(clazz, "RuleEngineValidationController", Constants.rule_log_debug, null);
		//System.out.println("calledddddddddd-----------");

		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", map));
	}

	@CrossOrigin
	@PostMapping
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@RequestMapping(value = "/validateTreatmentPlanBatch", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> validateTreatementPlanBatch(@RequestBody TreatmentPlanValidationDto dto) {

		// dto.setTreatmentPlanId("22095");
		dto.setInputMode(false);//always do this..we will not Open in Input mode for Batch..... 
		Map<String, List<TPValidationResponseDto>> map = tPService.validateTreatmentPlan(dto);
		RuleEngineLogger.generateLogs(clazz, "RuleEngineValidationController", Constants.rule_log_debug, null);

		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", map));
	}


	@CrossOrigin
	@RequestMapping(value = "/generateTreatmentId", method = RequestMethod.POST)
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public ResponseEntity<?> generatTreatmentId(@RequestBody TreatmentPlanDto dto) {
		Map<String, List<PatientTreamentDto>> map = tPService.getTreatments(dto);

		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "Treatment Ids Fetched Successfully", map));
	}

	@CrossOrigin
	@PostMapping
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@RequestMapping(value = "/diagnosticcheck", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> diagnosticCheck(@RequestBody DiagnosticDTO dto) {

		es.setUpSSLCertificates();
		List<String[]> list =null;

		if (!dto.getOfficeId().equals("All offices")) {
			 list =new ArrayList<>();
			list.add(es.doDiagnosticCheck(dto.getOfficeId()));
		} else {
			list= es.doDiagnosticCheck();
		}

		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", list));
	}
}
