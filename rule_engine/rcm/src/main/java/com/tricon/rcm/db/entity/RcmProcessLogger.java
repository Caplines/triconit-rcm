package com.tricon.rcm.db.entity;

import java.time.LocalTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "rcm_process_logger")
public class RcmProcessLogger implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7415227758170929809L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private long id;

	@Column(name = "start_time", nullable = false)
	private LocalTime startTime;

	@Column(name = "create_date", nullable = false, updatable = false)
	private Date createdDate;
	
	@Column(name = "end_time")
	private LocalTime endTime;

	@Column(name = "parameters", columnDefinition = "TEXT")
	private String parameters;

	@Column(name = "process_name")
	private String processName;

	@Column(name = "created_by")
	private String createdBy;
	
	@Column(name = "status",columnDefinition = "BIT default 0")
	private boolean status;

}
