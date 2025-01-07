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
import javax.persistence.UniqueConstraint;

import com.tricon.rcm.db.BaseAuditEntity;

import lombok.Data;

@Data
@Entity
@Table(name = "rcm_service_level_section")
public class RcmServiceLevelInformation extends BaseAuditEntity implements java.io.Serializable{

	private static final long serialVersionUID = -897578150199014826L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;
	
	@Column(name = "s_no")
	private int sNum;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "claim_id", referencedColumnName = "claim_uuid")
	private RcmClaims claim;

	@Column(name = "appt_id", length = 15)
	private String apptId;

	@Column(name = "description", length = 255)
	private String description;

	@Column(name = "est_insurance", length = 15)
	private String estInsurance;

	@Column(name = "est_primary", length = 15)
	private String estPrimary;

	@Column(name = "fee", length = 15)
	private String fee;

	@Column(name = "id_es", length = 15)
	private String idEs;

	@Column(name = "line_item", length = 20)
	private String lineItem;

	@Column(name = "patient_portion", length = 20)
	private String patientPortion;

	@Column(name = "patient_portion_sec", length = 20)
	private String patientPortionSec;

	// @Column(name = "pd", length = 15)
	// private String pd;

	@Column(name = "provider_last_name", length = 100)
	private String providerLastName;

	@Column(name = "status", length = 15)
	private String status;

	@Column(name = "surface", length = 255)
	private String surface;

	@Column(name = "tooth", length = 255)
	private String tooth;

	@Column(name = "service_code", length = 15)
	private String serviceCode;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id", referencedColumnName = "id")
	private RcmTeam team;
	
	@Column(name = "allowed_amount")
	private double allowedAmount;
	
	@Column(name = "paid_amount")
	private double paidAmount;
	
	@Column(name = "adjustment_amount")
	private double adjustmentAmount;
	
	@Column(name = "bill_to_patient_amount")
	private double billToPatientAmount;
	
	@Column(name = "adjustment_reason")
	private String adjustmentReason;
	
	@Column(name = "btp_reason")
	private String btpReason;
	
	@Column(name = "notes",columnDefinition = "TEXT")
	private String notes;
	
	@Column(name = "action")
	private String action;
	
	@Column(name = "group_run")
	private int groupRun;
	
	@Column(name = "final_submit")
	private boolean finalSubmit;
	
	@Column(name = "reconciliation_pass")
	private boolean reconciliation;
	
	@Column(name = "active",columnDefinition = "BIT default 1")
	private boolean active;
	
	@Column(name = "flag",columnDefinition = "BIT default 1")
	private boolean flag;
	
	@Column(name = "rebilled_status",columnDefinition = "BIT default 0")
	private boolean rebilledStatus;
	
	@Column(name = "mark_as_deleted")
	private boolean markAsDeleted;
	
	@Column(name = "balance_from_es_before_posting")
	private double balanceFromEsBeforePosting;

	@Column(name = "balance_from_es_after_posting")
	private double balanceFromEsAfterPosting;
	
	@Column(name = "credit_adjustment_amount")
	private double creditAdjustmentAmount;

	@Column(name = "debit_adjustment_amount")
	private double debitAdjustmentAmount;
	
	@Column(name = "action_taken")
	private String actionTaken;
	
	@Column(name = "additional_info_provided")
	private String additionalInfoProvided;
}
