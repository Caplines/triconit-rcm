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

//don't need to add unique constraints because we have to maintain history data inside table
@Data
@Entity
@Table(name = "rcm_appeal_level_info_section")
public class RcmAppealLevelInformation extends BaseAuditEntity implements java.io.Serializable {

	private static final long serialVersionUID = 7470532150082428092L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "claim_uuid", referencedColumnName = "claim_uuid")
	private RcmClaims claim;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id", referencedColumnName = "id")
	private RcmTeam teamId;

	@Column(name = "remarks", columnDefinition = "text")
	private String remarks;

	@Column(name = "mode_of_appeal")
	private String modeOfAppeal;

	@Column(name = "ai_tool_used",length =20)
	private String aiToolUsed;

	@Column(name = "appeal_document")
	private String appealDocument;
	
	@Column(name = "final_submit")
	private boolean finalSubmit;

}
