package com.tricon.ruleengine.model.db;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @author Deepak.Dogra
 *
 */
@Entity
@Table(name = "scrapping_full_data_manage_process")
public class ScrappingFullDataManagmentProcess extends BaseAudit implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6998138431531758322L;


	/**
	 * 
	 */
	

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	

	@Column(name = "count")
	private int count;
	
	@Column(name="status")
	private String status;


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public int getCount() {
		return count;
	}


	public void setCount(int count) {
		this.count = count;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}

	
	
	
}

