package com.tricon.rcm.db.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;


@Data
@Entity
@Table(name = "office",uniqueConstraints = { @UniqueConstraint(columnNames = { "company_id", "name" }) })//Used in Rule Engine As well..
public class RcmOffice implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1630135841595773455L;
	
	@GeneratedValue(generator = "uuid2")//only possible for Id's
	@Id
	@GenericGenerator(name = "uuid2",strategy = "uuid2")
	@Column(name = "uuid", nullable = false, length = 45)
	private String uuid;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "company_id",referencedColumnName="uuid")
	private RcmCompany company;
	
	@Column(name = "name", length = 45)
	private String name;
	
	@CreationTimestamp
	@Column(name = "created_date", nullable = false, updatable = false)
	private Date createdDate;
	
	@UpdateTimestamp
	@Column(name = "updated_date", nullable = true)
	private Date updatedDate;
	
	@Column(name = "id", unique=true)
	private int key;
	
}
