package com.tricon.ruleengine.dao;

import java.io.Serializable;
import java.util.List;

import com.tricon.ruleengine.dto.QuestionAnswerDto;
import com.tricon.ruleengine.dto.QuestionHeaderDto;
import com.tricon.ruleengine.dto.UserInputDto;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.UserInputRuleQuestionAnswer;
import com.tricon.ruleengine.model.db.UserInputRuleQuestionHeader;

public interface UserInputQuestionDao {

	public List<QuestionHeaderDto> getAllUserInputQuestions();

	public List<QuestionAnswerDto> getUserAnswers(UserInputDto dto);

	public Serializable saveAndUpdateAnswers(QuestionAnswerDto dto, Office off,
			UserInputRuleQuestionHeader userInputRuleQuestionHeader);

	public UserInputRuleQuestionAnswer getUserAnswersByQuestionId(UserInputDto dto, int questionId);

}
