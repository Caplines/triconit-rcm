package com.tricon.ruleengine.model.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="replication_days")
public class ReplicationDays 
{
    
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Integer id;


	@Column(name = "query_name", nullable = false)
	private String queryName;

	@Column(name = "description", nullable = false)
	private String description;
	
	@Column(name = "days", nullable = false)
	private int days;

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getQueryName() {
		return queryName;
	}

	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public String toString() {
		return "ReplicationDays [id=" + id + ", queryName=" + queryName + ", description=" + description + ", days="
				+ days + "]";
	}
}
