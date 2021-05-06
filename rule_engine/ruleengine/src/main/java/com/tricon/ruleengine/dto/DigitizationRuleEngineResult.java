package com.tricon.ruleengine.dto;

import java.util.List;
import java.util.Map;

public class DigitizationRuleEngineResult {

	
	private String id;
	private String name;
	private String message;
	private String runDate;
	private String OfficeName;
	private int messageType;
	private String documentId;
	private String patientId;
	private String dos;
	
	
	public String getDos() {
		return dos;
	}
	public void setDos(String dos) {
		this.dos = dos;
	}
	public String getPatientId() {
		return patientId;
	}
	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}
	private Map<Integer,List<String[]>> ruleMap;
	/*
	 SELECT * FROM reports_claim r limit 10;
SELECT * FROM reports order by created_date desc limit 100;
SELECT * FROM report_detail r order by created_date desc limit 100;
SELECT patient_id FROM patient_detail p where id=138964;
SELECT * FROM patient p where id=35616;

	 */
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getRunDate() {
		return runDate;
	}
	public void setRunDate(String runDate) {
		this.runDate = runDate;
	}
	public Map<Integer, List<String[]>> getRuleMap() {
		return ruleMap;
	}
	public void setRuleMap(Map<Integer, List<String[]>> ruleMap) {
		this.ruleMap = ruleMap;
	}
	public String getOfficeName() {
		return OfficeName;
	}
	public void setOfficeName(String officeName) {
		OfficeName = officeName;
	}
	public int getMessageType() {
		return messageType;
	}
	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}
	
	public String getDocumentId() {
		return documentId;
	}
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
	
	
	
	
	
}
