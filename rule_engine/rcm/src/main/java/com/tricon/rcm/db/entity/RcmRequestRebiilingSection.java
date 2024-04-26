package com.tricon.rcm.db.entity;

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
@Table(name = "rcm_request_rebilling_section")
public class RcmRequestRebiilingSection extends BaseAuditEntity implements java.io.Serializable{

	private static final long serialVersionUID = 7669437058634947724L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "claim_uuid", referencedColumnName = "claim_uuid")
	private RcmClaims claim;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id", referencedColumnName = "id")
	private RcmTeam team;
	
	@Column(name = "remarks", columnDefinition = "text")
	private String remarks;

	@Column(name = "reason_for_rebilling")
	private String reasonForRebilling;
	
	@Column(name = "rebilling_requirements")
	private String rebillingRequirements;

	@Column(name = "rebilling_service_codes")
	private String rebillingServiceCodes;	
	
	@Column(name = "rebilling_type",length=50)
	private String rebillingType;
	
	@Column(name = "final_submit")
	private boolean finalSubmit;
	
	@Column(name = "used_ai")
	private String usedAI;
	
}
