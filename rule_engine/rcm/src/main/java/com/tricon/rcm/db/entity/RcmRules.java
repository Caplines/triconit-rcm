package com.tricon.rcm.db.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Used in Rule Engine Also. Do not change
 */
@Entity
@Table(name = "rules")
public class RcmRules implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 489977259021273798L;
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	@Column(name = "name", columnDefinition = "text")
	private String name;
	@Column(name = "short_name", columnDefinition = "text", unique = true)
	private String shortName;
	@Column(name = "description", columnDefinition = "text")
	private String description;
	@Column(name = "active")
	private int active;

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getActive() {
		return active;
	}

	public void setActive(int active) {
		this.active = active;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

}
