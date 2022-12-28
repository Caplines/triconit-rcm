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
@Table(name = "rcm_claim_assignment")
public class RcmClaimAssignment extends BaseAuditEntity implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 113571778937002573L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "claim_id",referencedColumnName="claim_uuid")
	private RcmClaims claimId;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "assigned_by",referencedColumnName="uuid")
	private RcmUser assignedBy;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "assigned_to",referencedColumnName="uuid")
	private RcmUser assignedTo;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "current_team_id",referencedColumnName="id")
	private RcmTeam currentTeamId;
	
	@Column(name = "comment_assigned_by")
	private String commentAssignedBy;
	
	@Column(name = "comment_assigned_to")
	private String commentAssignedTo;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "status_id",referencedColumnName="id")
	private RcmClaimStatusType rcmClaimStatus;
}
