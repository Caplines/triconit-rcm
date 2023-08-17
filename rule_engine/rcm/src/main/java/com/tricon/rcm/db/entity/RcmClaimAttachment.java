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
@Table(name = "rcm_claim_attachment")
public class RcmClaimAttachment extends BaseAuditEntity implements java.io.Serializable{

	
	private static final long serialVersionUID = -5020598614852444802L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
    private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "claim_id",referencedColumnName="claim_uuid")
	private RcmClaims uuid;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by_team",referencedColumnName="id",nullable = false)
	private RcmTeam createdByteam;
	
	
	@Column(name = "file_name", nullable = false,length = 50)
	private String fileName;
	
	@Column(name = "file_location", nullable = false)
	private String fileLocation;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "attachment_type_id",referencedColumnName="id",nullable = false)
	private RcmAttachmentType atchType;
	
	@Column(name = "status")
	private boolean status=true;
	
	@Column(name = "is_deleted")
	private boolean deleted;
	
	
}
