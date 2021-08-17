package com.tricon.esdatareplication.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

//import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


import lombok.Data;

@Data
@MappedSuperclass
@Audited
@EntityListeners(AuditingEntityListener.class)
public class BaseInfoAudit implements Serializable{

	

    /**
	 * 
	 */
	private static final long serialVersionUID = 6568741041468827742L;
	
	
	@UpdateTimestamp
	@Column(name = "last_run_date", nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastRunDate;

}
