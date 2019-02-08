package com.tricon.ruleengine.service;

import java.util.List;

import com.tricon.ruleengine.dto.QuestionAnswerDto;
import com.tricon.ruleengine.dto.QuestionHeaderDto;
import com.tricon.ruleengine.dto.UserAnswerDto;
import com.tricon.ruleengine.dto.UserInputDto;

public interface UserInputService {
	
	public List<QuestionHeaderDto> getUserInput();
	
	public List<QuestionAnswerDto> getUserAnswers(UserInputDto dto);

	public void saveUserAnswers(List<UserAnswerDto> userAnswerDto);

}
