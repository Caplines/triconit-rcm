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

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;

@Data
@Entity
@Table(name = "rcm_claim_attachment")
public class RcmClaimAttachment implements java.io.Serializable{

	
	private static final long serialVersionUID = -5020598614852444802L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
    private Integer id;
	
	@Column(name = "claim_id", nullable = false,length = 45)
	private String claimId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id",referencedColumnName="id",nullable = false)
	private RcmTeam team;
	
	@CreationTimestamp
	@Column(name = "created_date", nullable = false, updatable = false)
	private Date createdDate;
	
	@UpdateTimestamp
	@Column(name = "updated_date")
	private Date updatedDate;
	
	@Column(name = "fileName", nullable = false,length = 50)
	private String fileName;
	
	@Column(name = "fileLocation", nullable = false)
	private String fileLocation;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "attachment_type_id",referencedColumnName="id",nullable = false)
	private RcmAttachmentType atchType;
	
	
}
