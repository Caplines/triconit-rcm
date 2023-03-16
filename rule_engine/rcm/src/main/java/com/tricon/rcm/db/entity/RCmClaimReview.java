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

import org.hibernate.annotations.GenericGenerator;

import com.tricon.rcm.db.BaseAuditEntity;

import lombok.Data;

@Data
@Entity
@Table(name = "rcm_claim_review")
public class RCmClaimReview extends BaseAuditEntity implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9208476444610434282L;

	@GeneratedValue(generator = "uuid2") // only possible for Id's
	@Id
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name = "uuid", nullable = false, length = 45)
	private String uuid;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "claim_id",referencedColumnName="claim_uuid")
	private RcmClaims claims;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reviwed_by",referencedColumnName="uuid")
	private RcmUser reviwedBy;
	
	@Column(name = "comments",columnDefinition="text", nullable = false)
	String comments;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id",referencedColumnName="id")
	private RcmTeam teamId;
	
	@Column(name = "active",columnDefinition = "BIT default 1")
	private boolean active;//
	
}
