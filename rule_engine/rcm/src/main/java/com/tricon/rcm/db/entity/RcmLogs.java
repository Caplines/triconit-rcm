package com.tricon.rcm.db.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.tricon.rcm.db.BaseAuditEntity;

import lombok.Data;

@Data
@Entity
@Table(name = "rcm_logs")
public class RcmLogs extends BaseAuditEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4899133419567455914L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;

	@Column(name = "ip", nullable = true)
	private String ip;

	@Column(name = "request_data", columnDefinition="text", nullable = true)
	private String requestData;

	@Column(name = "request_type", nullable = true)
	private String requestType;
	
	@Column(name = "context", nullable = true)
	private int context;


}
