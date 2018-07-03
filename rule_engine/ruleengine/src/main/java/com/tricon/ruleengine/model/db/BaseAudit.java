package com.tricon.ruleengine.model.db;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;

//import org.springframework.data.annotation.CreatedDate;
//import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@Audited
@EntityListeners(AuditingEntityListener.class)
public class BaseAudit implements java.io.Serializable {

//http://www.baeldung.com/database-auditing-jpa
	private static final long serialVersionUID = 7518774133552424997L;

	private Date createdDate;

	private Date updatedDate;
	
	private User createdBy;
	
	private User updatedBy;

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

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "uuid", nullable = false)
	public User getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "uuid", nullable = true)
	public User getUpdatedBy() {
		return this.updatedBy;
	}

	public void setUpdatedBy(User updatedBy) {
		this.updatedBy = updatedBy;
	}

}
