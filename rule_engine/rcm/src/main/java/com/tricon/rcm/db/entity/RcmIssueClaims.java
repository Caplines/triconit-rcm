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
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;


import com.tricon.rcm.db.BaseAuditEntity;

import lombok.Data;

@Data
@Entity
@Table(name = "rcm_issue_claims", uniqueConstraints = { @UniqueConstraint(columnNames = { "claim_id", "office_id" }) })
public class RcmIssueClaims extends BaseAuditEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4203721099233903917L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;

	@Column(name = "claim_id", nullable = false)
	private String claimId;
	
	@Column(name = "issue", nullable = false,columnDefinition="text")
	private String issue;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "office_id", referencedColumnName = "uuid", nullable = false)
	private RcmOffice office;
	
	@Column(name = "resolved", nullable = false)
	private boolean  resolved;
	
}
