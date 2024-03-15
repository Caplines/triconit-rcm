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
@Table(name = "rcm_collection_agency_section")
public class RcmCollectionAgency extends BaseAuditEntity implements java.io.Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = -3580748526050991525L;

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
	
	@Column(name = "collection_type")
	private String collectionType;
	
	@Column(name = "debt_number")
	private String debtNumber;
	
	@Column(name = "reason")
	private String reason;
	
	@Column(name = "remarks", columnDefinition = "text")
	private String remarks;
	
	@Column(name = "mode_of_payment",length=10)
	private String modeOfPayment;
	
	@Column(name = "amount_received")
	private double amountReceived;
	
	@Column(name = "commision_charged")
	private double commisionCharged;
	
	@Column(name = "net_amount_received")
	private double netAmountReceived;
	
	@Column(name = "button_type")
	private int buttonType;
	
	@Column(name = "final_submit")
	private boolean finalSubmit;
}
