package com.tricon.rcm.db.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.tricon.rcm.db.BaseAuditEntity;

import lombok.Data;

@Data
@Entity
@Table(name = "rcm_remote_lite_count")
public class RcmRemoteStatusCount extends BaseAuditEntity implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4620987119373238283L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "office_id")
	private RcmOffice office;
	
	@Column(name = "rejected_count", length =5)
	private int rejectedCount;
	
	@Column(name = "accepted_count", length =5)
	private int acceptedCount;
	
	@Column(name = "printed_count", length =5)
	private int printedCount;
	
	@Column(name = "duplicate_count", length =5)
	private int duplicateCount;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rcm_claim_log_id",referencedColumnName="id")
	private RcmClaimLog rcmClaimLog;
	
	
}
