package com.tricon.ruleengine.model.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "user_input_rule_question_answer",uniqueConstraints = {
		@UniqueConstraint(columnNames = {"tp_id","ivf_id","office_id","pat_id"}) })
public class UserInputRuleQuestionAnswer extends BaseAudit implements java.io.Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4835926323552864684L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	private int id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "question_id",referencedColumnName="id",nullable=true)
    private UserInputRuleQuestionHeader userInputRuleQuestionHeader;
	
	/*
    @Column(name = "question",nullable=false)
    private String question;
    */
	
	
    @Column(name = "tp_id",nullable=false)
	private String tpId;
	
    @Column(name = "ivf_id",nullable=false)
	private String ivfId;

    @Column(name = "pat_id",nullable=false)
	private String patId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "office_id",referencedColumnName="uuid")
	private Office office;
	
	@Column(name = "anwser",nullable=true)
	private String answer;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public UserInputRuleQuestionHeader getUserInputRuleQuestionHeader() {
		return userInputRuleQuestionHeader;
	}

	public void setUserInputRuleQuestionHeader(UserInputRuleQuestionHeader userInputRuleQuestionHeader) {
		this.userInputRuleQuestionHeader = userInputRuleQuestionHeader;
	}

	public String getTpId() {
		return tpId;
	}

	public void setTpId(String tpId) {
		this.tpId = tpId;
	}

	public String getIvfId() {
		return ivfId;
	}

	public void setIvfId(String ivfId) {
		this.ivfId = ivfId;
	}

	public String getPatId() {
		return patId;
	}

	public void setPatId(String patId) {
		this.patId = patId;
	}

	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}
	
	/*
	@Column(name = "answer_order",nullable=false)
	private int answerOrder;
	*/
	
	
}
