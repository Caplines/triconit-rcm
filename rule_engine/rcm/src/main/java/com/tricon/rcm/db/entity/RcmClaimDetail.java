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
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "rcm_claim_detail")
public class RcmClaimDetail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9211920299248415092L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;

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
	
	@Column(name = "active",columnDefinition = "BIT default 0")
	private boolean active;
	
	@Column(name = "rcm_remark", length = 255)
	private String rcmRemark;
	
	@Column(name = "rebilled_status",columnDefinition = "BIT default 0")
	private boolean rebilledStatus;

}
