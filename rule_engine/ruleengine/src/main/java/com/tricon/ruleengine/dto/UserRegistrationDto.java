package com.tricon.ruleengine.dto;

public class UserRegistrationDto {
	
	private String firstName;
	private String lastName;
	private String email;
	private String userName;
	
	private String password;
	private String officeId;
	private int userType;
	private String cuuid;
	
	
	
	public String getOfficeId() {
		return officeId;
	}
	public void setOfficeId(String officeId) {
		this.officeId = officeId;
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getUserType() {
		return userType;
	}
	public void setUserType(int userType) {
		this.userType = userType;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getCuuid() {
		return cuuid;
	}
	public void setCuuid(String cuuid) {
		this.cuuid = cuuid;
	}
	
	

}
