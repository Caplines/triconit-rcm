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
@Table(name = "rcm_patient_statement_section")
public class RcmPatientStatementSection  extends BaseAuditEntity implements java.io.Serializable {

	private static final long serialVersionUID = 8171267275219612609L;
	

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
	
	@Column(name = "mode_of_statement")
	private String modeOfStatement;
	
	@Column(name = "reson")
	private String reason;
	
	@Column(name = "statement_type")
	private String statementType;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "amount_statement")
	private double amountStatement;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "next_review_date")
	private Date nextReviewDate;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "next_statement_date")
	private Date nextStatementDate;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "statement_sending_date")
	private Date statementSendingDate;
	
	@Column(name = "remarks",columnDefinition = "TEXT")
	private String remarks;
	
	@Column(name = "statement_notes",columnDefinition = "TEXT")
	private String statementNotes;
	
	@Column(name = "balance_sheet_link", columnDefinition = "text")
	private String balanceSheetLink;
	
	@Column(name = "button_type")
	private int buttonType;
	
	@Column(name = "final_submit")
	private boolean finalSubmit;
	
	
	
	
	
}
