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
import lombok.ToString;

@Data
@Entity
@ToString
@Table(name = "rcm_claim_client_section_mapping",uniqueConstraints = {@UniqueConstraint(columnNames = { "section_id", "company_id" ,"team_id"})})
public class RcmClientSectionMapping implements java.io.Serializable{
	
	private static final long serialVersionUID = 5225121480804692038L;
	
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
	
	@Column(name = "view_access",columnDefinition = "BIT default 0")
	private boolean viewAccess;
	
	@Column(name = "edit_access",columnDefinition = "BIT default 0")
	private boolean editAccess;
}
