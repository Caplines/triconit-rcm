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
@Table(name = "rcm_insurance_follow_up_section")
public class RcmInsuranceFollowUpSection extends BaseAuditEntity implements java.io.Serializable{

	private static final long serialVersionUID = 4685845172306789718L;
	
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
	
	@Column(name = "folllow_up_mode",length = 20)
	private String modeOfFolloWUp;
	
	@Column(name = "ref_number")
	private String refNumber;
	
	@Column(name = "insurance_rep_name")
	private String insuranceRepName;
	
	@Column(name = "current_claim_status")
	private String currentClaimStatus;
	
	@Column(name = "follow_up_remarks",columnDefinition ="TEXT")
	private String followUpRemarks;
	
	@Column(name = "next_follow_up_required",length = 5)
	private String nextFollowUpRequired;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "next_follow_up_date")
	private Date nextFollowUphDate;
	
	@Column(name = "final_submit")
	private boolean finalSubmit;
	
	

}
