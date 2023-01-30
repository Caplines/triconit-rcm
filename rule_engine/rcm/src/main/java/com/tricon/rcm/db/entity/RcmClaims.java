package com.tricon.rcm.db.entity;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;

import com.tricon.rcm.db.BaseAuditEntity;

import lombok.Data;

@Data
@Entity
@Table(name = "rcm_claims", uniqueConstraints = { @UniqueConstraint(columnNames = { "claim_id", "office_id" }) })
public class RcmClaims extends BaseAuditEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5125694073291000159L;

	@GeneratedValue(generator = "uuid2") // only possible for Id's
	@Id
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name = "claim_uuid", nullable = false, length = 45)
	private String claimUuid;

	@Column(name = "claim_id", nullable = false)
	private String claimId;// c1

	//@Column(name = "claim_status")
	//private String claimStatus;// For Rule Engine..

	@Column(name = "patient_id", length = 8)
	private String patientId;// c2

	@Column(name = "patient_name", length = 255)
	private String patientName;

	@Column(name = "dos", length = 8)
	private Date dos;// c4

	// @Column(name = "claim_age_days", length = 6)
	// private int claimAgeDays;

	@Column(name = "submitted_total", precision = 5, scale = 2)
	private float submittedTotal;// C5

	@Column(name = "prime_submitted_total", precision = 5, scale = 2)
	private float primeSubmittedTotal;// C6

	@Column(name = "prim_total_paid", precision = 5, scale = 2)
	private float primTotalPaid;// C7

	@Column(name = "prim_date_sent")
	private Date primDateSent;// C8

	@Column(name = "sec_submitted_total", precision = 5, scale = 2)
	private float secSubmittedTotal;// C9

	@Column(name = "sec_date_sent")
	private Date secDateSent;// C10

	@Column(name = "provider_id")
	private String providerId;// C11

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "prim_insurance_company_id", referencedColumnName = "id")
	private RcmInsurance primInsuranceCompanyId;// C12

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sec_insurance_company_id", referencedColumnName = "id")
	private RcmInsurance secInsuranceCompanyId;// C13

	@Column(name = "prim_status")
	private String primStatus;// C14

	@Column(name = "sec_status")
	private String secStatus;// C15

	//@Column(name = "claim_type", length = 255)
	//private String claimType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "current_team_id", referencedColumnName = "id")
	private RcmTeam currentTeamId;

	@Column(name = "patient_birth_date")
	private Date patientBirthDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "office_id", referencedColumnName = "uuid")
	private RcmOffice office;

	///@Column(name = "rcm_team_status")
	//private int rcmTeamStatus;// 0 Mean with SYSTEM 1 =TL 2= Means with Associate//Rethink logic//
	// Look in status Journey table

	@Column(name = "rcm_source")
	private String rcmSource;// 
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "claim_status_type_id", referencedColumnName = "id")
	private RcmClaimStatusType claimStatusType;
	
	
	@Column(name = "regenerated",columnDefinition = "BIT default 0")
	private boolean regenerated;//

	
	@Column(name = "pending",columnDefinition = "BIT default 0")
	private boolean pending;//
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rcm_insurance_type", referencedColumnName = "id")
	private RcmInsuranceType rcmInsuranceType;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "last_work_team_id", referencedColumnName = "id",nullable=true)
	private RcmTeam lastWorkTeamId;

}
