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
@Table(name = "rebilled_claim_validation_remarks")
public class RcmRebilledClaimValidationRemark extends BaseAuditEntity implements java.io.Serializable {


	private static final long serialVersionUID = -4938866367944892605L;

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
	
	@Column(name = "remark")
	private String remark;
	
	@Column(name = "rule_id")
	private int ruleId;
}
