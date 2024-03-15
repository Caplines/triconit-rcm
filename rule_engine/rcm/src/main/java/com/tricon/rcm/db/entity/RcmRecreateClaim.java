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
@Table(name = "rcm_recreation_claim_section")
public class RcmRecreateClaim extends BaseAuditEntity implements java.io.Serializable{

	private static final long serialVersionUID = 4188165252426004335L;

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
	
	@Column(name = "remarks_recreation")
	private String remarksRecreation;
	
	@Column(name = "reason_for_recreation")
	private String reasonForRecreation;
	
	@Column(name = "button_type")
	private int buttonType;
	
	@Column(name = "new_claim_id")
	private String newClaimId;
	
	@Column(name = "final_submit")
	private boolean finalSubmit;
}
