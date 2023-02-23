package com.tricon.rcm.db.entity;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;

import com.tricon.rcm.db.BaseAuditEntity;

import lombok.Data;

@Data
@Entity
@Table(name = "rcm_claim_notes", uniqueConstraints = { @UniqueConstraint(columnNames = { "claim_id" }) })
public class RcmClaimNotes extends BaseAuditEntity implements Serializable {



	/**
	 * 
	 */
	private static final long serialVersionUID = -2225056133520646542L;

	@GeneratedValue(generator = "uuid2") // only possible for Id's
	@Id
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name = "notes_uuid", nullable = false, length = 45)
	private String remarkUuid;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "claim_id", referencedColumnName = "claim_uuid")
	private RcmClaims claim;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "notes_by", referencedColumnName = "uuid")
	private RcmUser notesBy;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "note_id", referencedColumnName = "id")
	private RcmClaimNoteType noteType;
	
	@Column(name = "note", columnDefinition = "text", nullable = false)
	String note;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id", referencedColumnName = "id")
	private RcmTeam teamId;

	@Column(name = "active", columnDefinition = "BIT default 1")
	private boolean active;
}
