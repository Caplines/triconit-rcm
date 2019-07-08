package com.tricon.ruleengine.model.sheet;

public class CommonDataPatient {
	
	
	
	private String id;
	private String name;
	private String lastName;
	private String dob;

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

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public CommonDataPatient() {
	}

	public CommonDataPatient(String id, String name, String dob) {
		super();
		this.id = id;
		this.name = name;
		this.dob = dob;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


}
