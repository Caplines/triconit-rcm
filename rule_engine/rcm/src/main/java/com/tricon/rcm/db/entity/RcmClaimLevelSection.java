package com.tricon.rcm.db.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.tricon.rcm.db.BaseAuditEntity;

import lombok.Data;

@Data
@Entity
@Table(name = "rcm_claim_level_info_section")
public class RcmClaimLevelSection extends BaseAuditEntity implements java.io.Serializable{

	private static final long serialVersionUID = 9133980931964282282L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
    private int id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "claim_uuid", referencedColumnName = "claim_uuid")
	private RcmClaims claim;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id", referencedColumnName = "id")
	private RcmTeam teamId;

	@Column(name = "claim_id")
	private String claimId;
	
	@Column(name = "network_type",length =10 )
	private String network;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "claim_processing_date")
	private Date claimProcessingDate;
	
	@Column(name = "claim_pass_first_go",length =10)
	private String claimPassFirstGo;
	
	@Column(name = "initial_denial")
	private String initialDenial;
	
	@Column(name = "no_of_est_payment")
	private String noOfEstPayment;
	
	@Column(name = "payments_frequency")
	private String paymentFrequency;
	
	@Column(name = "no_of_payment_received")
	private String noOfPaymentReceived;

	@Column(name = "final_submit")
	private boolean finalSubmit;
}
