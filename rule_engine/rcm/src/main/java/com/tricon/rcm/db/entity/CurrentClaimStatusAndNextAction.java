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
@Table(name = "rcm_next_action_required_section")
public class CurrentClaimStatusAndNextAction extends BaseAuditEntity implements java.io.Serializable{

	private static final long serialVersionUID = -172366051067643888L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "claim_uuid", referencedColumnName = "claim_uuid")
	private RcmClaims claim;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "assign_to_team", referencedColumnName = "id")
	private RcmTeam assignToTeam;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id", referencedColumnName = "id")
	private RcmTeam team;
	
	@Column(name = "final_submit")
	private boolean finalSubmit;
	
	@Column(name = "remarks",columnDefinition = "TEXT")
	private String remarks;
	
	@Column(name = "current_claim_status_rcm")
	private String currentClaimStatusRcm;
	
	@Column(name = "current_claim_status_es")
	private String currentClaimStatusEs;
	
	@Column(name = "next_action")
	private String nextAction;
	
}
