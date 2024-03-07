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

import lombok.Data;

@Data
@Entity
@Table(name = "rcm_insurance_type", uniqueConstraints = { @UniqueConstraint(columnNames = { "name"}) })
public class RcmInsuranceType {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;

	@Column(name = "name", nullable = false)
	private String name;
	
	@Column(name = "code", nullable = true)
	private String code;
	
	@Column(name = "next_follow_up_gap",columnDefinition = "integer default 0")
	private int nextFollowUpGap;
	
	@Column(name = "days_after_claim_come_posting",columnDefinition = "integer default 0")
	private int daysAfterClaimToComePosting;
	
	/*@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id", referencedColumnName = "id")
	private RcmTeam team;
    */
}
