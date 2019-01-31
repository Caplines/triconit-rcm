package com.tricon.ruleengine.dto;

import java.util.List;

public class UserInputQuestionAnswerDto {
	
	
	List<QuestionHeaderDto> dataHeader;
	List<QuestionAnswerDto> questionAnswer;
	public List<QuestionHeaderDto> getDataHeader() {
		return dataHeader;
	}
	public void setDataHeader(List<QuestionHeaderDto> dataHeader) {
		this.dataHeader = dataHeader;
	}
	public List<QuestionAnswerDto> getQuestionAnswer() {
		return questionAnswer;
	}
	public void setQuestionAnswer(List<QuestionAnswerDto> questionAnswer) {
		this.questionAnswer = questionAnswer;
	}
	
	
	
	
	

}
