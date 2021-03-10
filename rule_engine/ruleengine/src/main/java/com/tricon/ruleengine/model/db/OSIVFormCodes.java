package com.tricon.ruleengine.model.db;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * This entity class represents OS IV Form Codes
 * @author Deepak.Dogra
 *
 */

@Entity
@Table(name = "ocivcode",uniqueConstraints = {//uniqueConstraints = {@UniqueConstraint(columnNames= {"first", "second"})})//, @UniqueConstraint(columnNames = "ivf_form_id"),@UniqueConstraint(columnNames = "office_id")
		@UniqueConstraint(columnNames = {"code"}) })

public class OSIVFormCodes implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8379366994689883493L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;
	
	@Column(name = "code")
	private String code;
	
	//for Precentage
	@Column(name = "get_name")
	private String getName;

	@Column(name = "set_name")
	private String setName;
	
	//for frequency
	@Column(name = "get_name_f")
	private String getNameF;

	@Column(name = "set_name_f")
	private String setNameF;

	@Column(name = "active")
	private int active;

	public String getGetNameF() {
		return getNameF;
	}

	public void setGetNameF(String getNameF) {
		this.getNameF = getNameF;
	}

	public String getSetNameF() {
		return setNameF;
	}

	public void setSetNameF(String setNameF) {
		this.setNameF = setNameF;
	}



	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getGetName() {
		return getName;
	}

	public void setGetName(String getName) {
		this.getName = getName;
	}

	public String getSetName() {
		return setName;
	}

	public void setSetName(String setName) {
		this.setName = setName;
	}

	public int getActive() {
		return active;
	}

	public void setActive(int active) {
		this.active = active;
	}



	

}
