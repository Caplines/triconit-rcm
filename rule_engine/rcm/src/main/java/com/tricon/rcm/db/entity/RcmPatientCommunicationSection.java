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
@Table(name = "rcm_patient_communication_section")
public class RcmPatientCommunicationSection extends BaseAuditEntity implements java.io.Serializable{

	private static final long serialVersionUID = 5132019746943916197L;
	
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
	
	@Column(name = "remarks",columnDefinition = "TEXT")
	private String remarks;
	
	@Column(name = "desposition")
	private String desposition;
	
	@Column(name = "mode_of_follow",length =20)
	private String modeOfFollowUp;
	
	@Column(name = "contact",length =20)
	private String contact;
	
	@Column(name = "final_submit")
	private boolean finalSubmit;

}
