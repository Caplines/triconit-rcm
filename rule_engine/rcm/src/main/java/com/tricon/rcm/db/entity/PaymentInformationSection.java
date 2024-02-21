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
@Table(name = "rcm_payment_information_section")
public class PaymentInformationSection extends BaseAuditEntity implements java.io.Serializable {

	private static final long serialVersionUID = -1889856364116633983L;

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

	@Column(name = "payment_issue_to")
	private String paymentIssueTo;

	@Column(name = "amount_received_in_bank")
	private Double amountReceivedInBank;

	@Column(name = "amount_posted_in_es")
	private Double amountPostedInEs;

	@Column(name = "check_deliver_to")
	private String checkDeliverTo;

	@Column(name = "check_number")
	private String checkNumber;

	@Temporal(TemporalType.DATE)
	@Column(name = "amount_date_received_in_bank")
	private Date amountDateReceivedInBank;

	@Temporal(TemporalType.DATE)
	@Column(name = "check_cash_date")
	private Date checkCashDate;
	
	@Column(name = "payment_mode",length =10)
	private String paymentMode;

	@Column(name = "final_submit")
	private boolean finalSubmit;

}
