package com.tricon.ruleengine.dto;

import java.util.Date;

public class QuestionAnswerDto {
	
	private int answerId;
	private int questionId;
	private String tpId;
	private String ivfId;
	private String patId;
	private String officeId;
	private String answer;
	private String serviceCode;
	private Date txPlanValidationDate;
	private String savedPermanent;
	
	
	public int getAnswerId() {
		return answerId;
	}
	public void setAnswerId(int answerId) {
		this.answerId = answerId;
	}
	public int getQuestionId() {
		return questionId;
	}
	public void setQuestionId(int questionId) {
		this.questionId = questionId;
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
	public String getOfficeId() {
		return officeId;
	}
	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public String getServiceCode() {
		return serviceCode;
	}
	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}
	public Date getTxPlanValidationDate() {
		return txPlanValidationDate;
	}
	public void setTxPlanValidationDate(Date txPlanValidationDate) {
		this.txPlanValidationDate = txPlanValidationDate;
	}
	public String getSavedPermanent() {
		return savedPermanent;
	}
	public void setSavedPermanent(String savedPermanent) {
		this.savedPermanent = savedPermanent;
	}
	
	
	
	

}
