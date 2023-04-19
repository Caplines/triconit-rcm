package com.tricon.rcm.db.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "rcm_exception_logs")
public class RcmExceptionLogs {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;

	@CreationTimestamp
	@Column(name = "created_date")
	private Date createdDate;
	
	@Column(name = "stack_trace")
	private String stackTrace;
	
	@Column(name = "occurs_in")
	private String occursIn;
	
}
