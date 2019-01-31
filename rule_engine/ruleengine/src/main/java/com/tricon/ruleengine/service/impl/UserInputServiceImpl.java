package com.tricon.ruleengine.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tricon.ruleengine.dao.UserInputQuestionDao;
import com.tricon.ruleengine.dto.QuestionAnswerDto;
import com.tricon.ruleengine.dto.QuestionHeaderDto;
import com.tricon.ruleengine.dto.UserInputDto;
import com.tricon.ruleengine.service.UserInputService;

/**
 * 
 * @author Deepak.Dogra
 *
 */
@Service
public class UserInputServiceImpl implements UserInputService {

	@Autowired
	UserInputQuestionDao uiqDao;

	@Override
	public List<QuestionHeaderDto> getUserInput() {
		// TODO Auto-generated method stub
		return uiqDao.getAllUserInputQuestions();
	}
	
	@Override
	public List<QuestionAnswerDto> getUserAnswers(UserInputDto dto){
		
		return uiqDao.getUserAnswers(dto);
	}

}
