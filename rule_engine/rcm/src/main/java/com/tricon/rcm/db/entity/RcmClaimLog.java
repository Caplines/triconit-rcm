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

import com.tricon.rcm.db.BaseAuditEntity;

import lombok.Data;

@Data
@Entity
@Table(name = "rcm_claim_log")
public class RcmClaimLog  extends BaseAuditEntity implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8865249020600013405L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;

	@Column(name = "source", nullable = false)
	private String source;
	
	@Column(name = "new_claims_count", nullable = false)
	private int newClaimsCount;
	
	@Column(name = "status", nullable = false)
	private int status;//1 Means Fine 0 Mean Fetch claim Service Ran but not able to fetch Claims 
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "office_id",referencedColumnName="uuid")
	private RcmOffice office;
	
}

