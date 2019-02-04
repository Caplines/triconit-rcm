package com.tricon.ruleengine.model.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "user_input_rule_question_header",uniqueConstraints = {
		@UniqueConstraint(columnNames = {"question","rule_name"}) })
public class UserInputRuleQuestionHeader extends BaseAudit implements java.io.Serializable {
	

	private static final long serialVersionUID = -7433330084412674020L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	private int id;
	
	@Column(name = "rule_name",nullable=false)
	private String ruleName;
	
	@Column(name = "question",nullable=false)
	private String question;
	
	@Column(name = "question_type",nullable=false)
	private String questionType;
	
	@Column(name = "question_order",nullable=false)
	private int questionOrder;

	@Column(name = "ans_appender",nullable=true)
	private String answerAppender;

	@Column(name = "ans_appender_position",nullable=true)
	private String answerAppenderPosition;

	@Column(name = "hard_coded_answer",nullable=true)
	private String hardCodedAnswer;

	@Column(name = "can_change_answer",nullable=false)
	private int canChangeAnswer;

	@Column(name = "active",nullable=false)
	private int active;

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

	public int getCanChangeAnswer() {
		return canChangeAnswer;
	}

	public void setCanChangeAnswer(int canChangeAnswer) {
		this.canChangeAnswer = canChangeAnswer;
	}

	public int getActive() {
		return active;
	}

	public void setActive(int active) {
		this.active = active;
	}
	
	
	

}
