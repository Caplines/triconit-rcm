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
import javax.persistence.UniqueConstraint;

import lombok.Data;
import lombok.ToString;


@Data
@Entity
@ToString
@Table(name = "rcm_claim_user_section_mapping",uniqueConstraints = {@UniqueConstraint(columnNames = { "section_id", "company_id" ,"team_id","user_id"})})
public class ClaimUserSectionMapping implements Serializable{


	private static final long serialVersionUID = -4468776417536538313L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
    private int id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "section_id",referencedColumnName="id")
    private RcmClaimSection section;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "company_id",referencedColumnName="uuid")
	private RcmCompany company;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id", referencedColumnName = "id")
	private RcmTeam teamId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", referencedColumnName = "uuid")
	private RcmUser user;
	
	@Column(name = "view_access",columnDefinition = "BIT default 0")
	private boolean viewAccess;
	
	@Column(name = "edit_access",columnDefinition = "BIT default 0")
	private boolean editAccess;
}
