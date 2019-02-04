package com.tricon.ruleengine.dto;

public class QuestionHeaderDto {
	
	private int id;
	private String ruleName;
	private String question;
	private String questionType;
	private int questionOrder;
	private String answerAppender;
	private String answerAppenderPosition;
	private String hardCodedAnswer;
	private int canChangeAnswer;
	
	public int getCanChangeAnswer() {
		return canChangeAnswer;
	}
	public void setCanChangeAnswer(int canChangeAnswer) {
		this.canChangeAnswer = canChangeAnswer;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getRuleName() {
		return ruleName;
	}
	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getQuestionType() {
		return questionType;
	}
	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}
	public int getQuestionOrder() {
		return questionOrder;
	}
	public void setQuestionOrder(int questionOrder) {
		this.questionOrder = questionOrder;
	}
	public String getAnswerAppender() {
		return answerAppender;
	}
	public void setAnswerAppender(String answerAppender) {
		this.answerAppender = answerAppender;
	}
	public String getAnswerAppenderPosition() {
		return answerAppenderPosition;
	}
	public void setAnswerAppenderPosition(String answerAppenderPosition) {
		this.answerAppenderPosition = answerAppenderPosition;
	}
	public String getHardCodedAnswer() {
		return hardCodedAnswer;
	}
	public void setHardCodedAnswer(String hardCodedAnswer) {
		this.hardCodedAnswer = hardCodedAnswer;
	}
	
	
	

}
