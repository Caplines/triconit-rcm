package com.tricon.ruleengine.service;

import java.util.List;

import com.tricon.ruleengine.dto.QuestionAnswerDto;
import com.tricon.ruleengine.dto.QuestionHeaderDto;
import com.tricon.ruleengine.dto.UserAnswerDto;
import com.tricon.ruleengine.dto.UserInputDto;
import com.tricon.ruleengine.model.db.Office;

public interface UserInputService {
	
	public List<QuestionHeaderDto> getUserInput();
	
	public List<QuestionAnswerDto> getUserAnswers(UserInputDto dto);

	public List<QuestionAnswerDto> getUserAnswersPermanent(UserInputDto dto);
	
	public void saveUserAnswers(List<UserAnswerDto> userAnswerDto);

	public List<QuestionAnswerDto> getUserAnswers(String patId,String ivfId,String TRAN_DATE, Office office);

	public List<QuestionAnswerDto> getUserAnswersPermanent(String patId,String ivfId,String TRAN_DATE, Office office);
}
