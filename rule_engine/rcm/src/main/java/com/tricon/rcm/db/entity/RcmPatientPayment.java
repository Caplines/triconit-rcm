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
@Table(name = "rcm_patient_payment_section")
public class RcmPatientPayment extends BaseAuditEntity implements java.io.Serializable{
	
	private static final long serialVersionUID = 6481615179668679409L;

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
	
	@Column(name = "amount_collected_claims")
	private double amountCollectedClaims;

	@Temporal(TemporalType.DATE)
	@Column(name = "date_of_payment")
	private Date dateOfPayment;

	@Column(name = "posted_in_pms",length =10)
	private String postedInPMS;

	@Column(name = "mode_of_payment")
	private String modeOfPayment;
	
	@Column(name = "due_balance_in_pms")
	private double dueBalanceInPMS;
	
	@Column(name = "final_submit")
	private boolean finalSubmit;

}
