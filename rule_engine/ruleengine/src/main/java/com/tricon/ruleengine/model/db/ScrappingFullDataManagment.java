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
@Table(name = "scrapping_full_data_manage")
public class ScrappingFullDataManagment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1231209805104896426L;
	

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	

	@Column(name = "process_count")
	private int processCount;

	@Column(name = "max_count")
	private int maxCount;

	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public int getProcessCount() {
		return processCount;
	}


	public void setProcessCount(int processCount) {
		this.processCount = processCount;
	}


	public int getMaxCount() {
		return maxCount;
	}


	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
	}

	

	
}

