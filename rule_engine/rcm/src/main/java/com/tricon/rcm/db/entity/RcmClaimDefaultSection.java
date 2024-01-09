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
@Table(name = "rcm_claim_default_section",uniqueConstraints = {@UniqueConstraint(columnNames = { "section_id","team_id"})})
public class RcmClaimDefaultSection  implements java.io.Serializable {

	private static final long serialVersionUID = 360722709369488446L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
    private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "section_id",referencedColumnName="id")
    private RcmClaimSection section;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id", referencedColumnName = "id")
	private RcmTeam teamId;
	
	@Column(name = "view_access",columnDefinition = "BIT default 0")
	private boolean viewAccess;
	
	@Column(name = "edit_access",columnDefinition = "BIT default 0")
	private boolean editAccess;

}
