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
import lombok.ToString;

@Data
@Entity
@ToString
@Table(name = "rcm_claim_cycle")

public class ClaimCycle extends BaseAuditEntity implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9020998999319567250L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
    private int id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "claim_id", referencedColumnName = "claim_uuid")
	private RcmClaims claim;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "current_team_id",referencedColumnName="id")
	private RcmTeam currentTeamId;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "next_action")
	private String nextAction;
	
	@Column(name = "status_updated")
	private String StatusUpdated;
	
}
