package com.tricon.ruleengine.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.tricon.ruleengine.dto.QuestionAnswerDto;
import com.tricon.ruleengine.dto.QuestionHeaderDto;
import com.tricon.ruleengine.dto.UserAnswerDto;
import com.tricon.ruleengine.dto.UserInputDto;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.User;
import com.tricon.ruleengine.model.db.UserInputRuleQuestionAnswer;
import com.tricon.ruleengine.model.db.UserInputRuleQuestionHeader;

public interface UserInputQuestionDao {

	public List<QuestionHeaderDto> getAllUserInputQuestions();
	
	public List<UserInputRuleQuestionHeader> getAllUserInputQuestionsDbModel();

	public List<QuestionAnswerDto> getUserAnswers(UserInputDto dto);

	public Serializable saveAndUpdateAnswers(QuestionAnswerDto dto,Office off,UserInputRuleQuestionHeader userInputRuleQuestionHeader,User user,String serviceCode);

	public UserInputRuleQuestionAnswer getUserAnswersByQuestionIdServiceCode(UserInputDto dto, int questionId,String serivceCode);

	public void saveUserAnswers(Integer [] ids,Map<Integer,UserAnswerDto> map);

}
