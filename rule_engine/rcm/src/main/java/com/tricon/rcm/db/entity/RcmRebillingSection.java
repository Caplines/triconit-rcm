package com.tricon.rcm.db.entity;

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
@Table(name = "rcm_rebilling_section")
public class RcmRebillingSection extends BaseAuditEntity implements java.io.Serializable{

	private static final long serialVersionUID = 4746747760184129819L;

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
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "requested_by",referencedColumnName="uuid")
	private RcmUser requestedBy;
	
	@Column(name = "remarks_rebilling")
	private String remarks;
	
	@Column(name = "remarks_requested")
	private String requestedRemarks;

	@Column(name = "reason_for_rebilling")
	private String reasonForRebilling;
	
	@Column(name = "rebilling_requirements")
	private String rebillingRequirements;

	@Column(name = "rebilling_service_codes")
	private String rebillingServiceCodes;	
	
	@Column(name = "is_rebilling")
	private boolean isRebilling;
	
	@Column(name = "receation_option_choosen")
	private boolean reCeationOptionChoosen;
	
	@Column(name = "final_submit")
	private boolean finalSubmit;
}
