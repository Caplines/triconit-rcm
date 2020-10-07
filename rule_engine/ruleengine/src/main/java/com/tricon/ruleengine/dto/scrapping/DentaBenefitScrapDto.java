package com.tricon.ruleengine.dto.scrapping;

import java.util.HashMap;

public class DentaBenefitScrapDto {

	private String[] types;
	private HashMap<String,String> map = new HashMap<>();
	private boolean[] mandatory;
	private float age;

	public String[] getTypes() {
		return types;
	}

	public void setTypes(String[] types) {
		this.types = types;
	}

	
	public boolean[] getMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean[] mandatory) {
		this.mandatory = mandatory;
	}

	public HashMap<String, String> getMap() {
		return map;
	}

	public void setMap(HashMap<String, String> map) {
		this.map = map;
	}

	public float getAge() {
		return age;
	}

	public void setAge(float age) {
		this.age = age;
	}

	
	
	

}
