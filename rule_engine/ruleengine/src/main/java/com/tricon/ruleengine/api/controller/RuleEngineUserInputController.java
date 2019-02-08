package com.tricon.ruleengine.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tricon.ruleengine.dto.GenericResponse;
import com.tricon.ruleengine.dto.QuestionAnswerDto;
import com.tricon.ruleengine.dto.QuestionHeaderDto;
import com.tricon.ruleengine.dto.UserAnswerDto;
import com.tricon.ruleengine.dto.UserInputDto;
import com.tricon.ruleengine.dto.UserInputQuestionAnswerDto;
import com.tricon.ruleengine.logger.RuleEngineLogger;
import com.tricon.ruleengine.model.db.UserInputRuleQuestionAnswer;
import com.tricon.ruleengine.service.UserInputService;
import com.tricon.ruleengine.utils.Constants;

/**
 * @author Deepak.Dogra
 *
 */

@RestController
public class RuleEngineUserInputController {

	static Class<?> clazz = RuleEngineUserInputController.class;

	@Autowired
	UserInputService userInputService;

	@CrossOrigin
	@PostMapping
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@RequestMapping(value = "/getUserInputQuestionsAns", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> getUserInputQuestionsWithAnswer(@RequestBody UserInputDto dto) {

		RuleEngineLogger.generateLogs(clazz, "RuleEngineUserInputController", Constants.rule_log_debug, null);
		List<QuestionHeaderDto> data1 = userInputService.getUserInput();
		List<QuestionAnswerDto> ansL=userInputService.getUserAnswers(dto);
		
		UserInputQuestionAnswerDto d=new UserInputQuestionAnswerDto();
		d.setDataHeader(data1);
		d.setQuestionAnswer(ansL);
		
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", d));
	}

	@CrossOrigin
	@PostMapping
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@RequestMapping(value = "/saveUserInput", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> saveUserInput(@RequestBody List<UserAnswerDto> userAnswerDto) {

	//List<UserAnswerDto>	li=dtoList.getAnswerList();
	RuleEngineLogger.generateLogs(clazz, "RuleEngineUserInputController", Constants.rule_log_debug, null);
		
	    userInputService.saveUserAnswers(userAnswerDto);
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", ""));
	

 }
}
