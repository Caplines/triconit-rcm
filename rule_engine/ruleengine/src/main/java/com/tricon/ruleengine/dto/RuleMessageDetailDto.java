package com.tricon.ruleengine.dto;

public class RuleMessageDetailDto{

	private int rid;
	private String patientId;
	private String firstName;
	private String lastName;
	private String message;
	private String officeName;
	private String createdDate;
	private int messageType;
	private String createdBy;
	private String name;
	private String pname;
	private String email;
	private int groupRun;
	private String dos;
	private String ivDate;
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		
		return rid;
	}

	public int getRid() {
		return rid;
	}

	public void setRid(int rid) {
		this.rid = rid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object object) {
		
		boolean result = false;
        if (object == null || object.getClass() != getClass()) {
            result = false;
        } else {
        	RuleMessageDetailDto dto = (RuleMessageDetailDto) object;
        	 if (this.patientId.equals(dto.getPatientId())
             		&& this.officeName.equals(dto.getOfficeName())) {
                 result = true;
            }
            /*if (this.messageType == dto.getMessageType() && this.message.equals( dto.getMessage()) && this.patientId.equals(dto.getPatientId())
            		&& this.officeName.equals(dto.getOfficeName()) && this.createdBy.equals(dto.getCreatedBy())) {
                result = true;
            }*/
        }
        return result;
	}
	
	
	public String getRuleId() {
		return ruleId;
	}
	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}
	private String dob;
	private String ruleId;
	
	
	
	public String getDob() {
		return dob;
	}
	public void setDob(String dob) {
		this.dob = dob;
	}
	public int getGroupRun() {
		return groupRun;
	}
	public void setGroupRun(int groupRun) {
		this.groupRun = groupRun;
	}
	public String getPatientId() {
		return patientId;
	}
	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}
	public String getPname() {
		return pname;
	}
	public void setPname(String pname) {
		this.pname = pname;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getOfficeName() {
		return officeName;
	}
	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public int getMessageType() {
		return messageType;
	}
	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public String getDos() {
		return dos;
	}

	public void setDos(String dos) {
		this.dos = dos;
	}

	public String getIvDate() {
		return ivDate;
	}

	public void setIvDate(String ivDate) {
		this.ivDate = ivDate;
	}
	
	
	
	
}
