package com.tricon.ruleengine.eaglesoft;


import com.fasterxml.jackson.annotation.JsonProperty;

//@JsonPropertyOrder({ "id","firstName" })
public class Patient {

	int id;
	String firstName;
	String lastName;
	String dob;	
	
	@JsonProperty("id")
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	@JsonProperty("firstName")
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	@JsonProperty("lastName")
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	@JsonProperty("dateOfBirth")
	public String getDob() {
		return dob;
	}
	public void setDob(String dob) {
		this.dob = dob;
	}
	
	
}
