package com.tricon.rcm.db;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.tricon.rcm.db.entity.RcmUser;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseAuditEntity {
	
	@CreationTimestamp
	@Column(name = "created_date", nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

    @UpdateTimestamp
	@Column(name = "updated_date", nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedDate;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by",referencedColumnName="uuid")
	private RcmUser createdBy;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "updated_by",referencedColumnName="uuid")
	private RcmUser updatedBy;

}
