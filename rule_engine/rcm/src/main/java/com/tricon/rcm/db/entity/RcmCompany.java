package com.tricon.rcm.db.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "company")//Used in Rule Engine As well..
public class RcmCompany {

	private String uuid;
	private String name;
	private String adddress;
	private Date createdDate;
	private Date updatedDate;


	public RcmCompany() {
	}

	public RcmCompany( String uuid) {
		this.uuid = uuid;
	}

	public RcmCompany(String uuid, String name, String adddress) {
		this.uuid = uuid;
		this.name = name;
		this.adddress = adddress;
	}

	@GeneratedValue(generator = "uuid2")//only possible for Id's
	@Id
	@GenericGenerator(name = "uuid2",strategy = "uuid2")
	@Column(name = "uuid", nullable = false, length = 45)
	public String getUuid() {
		return this.uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Column(name = "name", length = 45)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "adddress")
	public String getAdddress() {
		return this.adddress;
	}

	public void setAdddress(String adddress) {
		this.adddress = adddress;
	}

	@CreationTimestamp
	@Column(name = "created_date", nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

    @UpdateTimestamp
	@Column(name = "updated_date", nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}
}
