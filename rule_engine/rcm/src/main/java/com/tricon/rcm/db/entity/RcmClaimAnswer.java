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
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.tricon.rcm.db.BaseAuditEntity;

import lombok.Data;

@Data
@Entity
@Table(name = "rcm_claim_answer")
public class RcmClaimAnswer extends BaseAuditEntity implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3061770400704865792L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "claim_id",referencedColumnName="claim_uuid")
	private RcmClaims claim;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "question_id",nullable=false)
	private RcmClaimQuestions question;
	
	@Column(name = "anwser",length=255)
	private String anwser;
	
	
	
}
