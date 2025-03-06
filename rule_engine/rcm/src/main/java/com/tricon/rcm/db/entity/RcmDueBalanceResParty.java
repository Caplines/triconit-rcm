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
@Table(name = "rcm_due_balance_res_party")
public class RcmDueBalanceResParty extends BaseAuditEntity implements java.io.Serializable {


	private static final long serialVersionUID = 1638805538884480621L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "claim_uuid", referencedColumnName = "claim_uuid")
	private RcmClaims claim;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pulled_by_team", referencedColumnName = "id")
	private RcmTeam pulledByTeam;
	
	@Column(name = "due_balance_res_party")
	private String dueBalanceResponsibleParty;
	
	@Column(name = "res_party")
	private String responsibleParty;
}
