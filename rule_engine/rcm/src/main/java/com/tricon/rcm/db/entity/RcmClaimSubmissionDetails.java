package com.tricon.rcm.db.entity;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.tricon.rcm.db.BaseAuditEntity;

import lombok.Data;

@Data
@Entity
@Table(name = "rcm_claims_submission_details")
public class RcmClaimSubmissionDetails extends BaseAuditEntity implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8271542080965089766L;

	@GeneratedValue(generator = "uuid2") // only possible for Id's
	@Id
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name = "id", nullable = false, length = 45)
	private String id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "claim_id",referencedColumnName="claim_uuid" ,unique=true)
	private RcmClaims claim;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "submitted_by",referencedColumnName="uuid")
	private RcmUser submittedBy;
	
	@Column(name = "es_date")
	private Date esDate;//Date & Time of Claim Processing in Eaglesoft
	
	@Column(name = "es_time")
	private String esTime;
	
	@Column(name = "channel")
	private String channel;//0 RemoteLite  |  0 Fax  |  0 Mail  |  0 Portal		
	
	@Column(name = "attachment_send", columnDefinition = "BIT default 1")
    private boolean attachmentSend;		
	
	@Column(name = "preauth", columnDefinition = "BIT default 1")
    private boolean preauth;

	@Column(name = "refferal_letter", columnDefinition = "BIT default 1")
    private boolean refferalLetter;		

	@Column(name = "claim_no")
    private String claimNumber;		
	
	@Column(name = "preauth_no")
    private String preauthNo;
	
	@Column(name = "provider_ref_no")
    private String providerRefNo;
	
	@Column(name = "primary_eob_attached",columnDefinition = "BIT default 0")
    private boolean primaryEOBAttached;
	
	@Column(name = "clean_claim", columnDefinition = "BIT default 1")
    private boolean cleanClaim;	
}
