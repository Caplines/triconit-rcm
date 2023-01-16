package com.tricon.rcm.db.entity;

import java.io.Serializable;
import java.sql.Date;

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

import org.hibernate.annotations.GenericGenerator;

import com.tricon.rcm.db.BaseAuditEntity;

import lombok.Data;

@Data
@Entity
@Table(name = "rcm_client_claim_sheet", uniqueConstraints = { @UniqueConstraint(columnNames = { "company_id","sheet_id"}) })
public class RcmClientClaimSheet  extends BaseAuditEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 640911021448949151L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "company_id",referencedColumnName="uuid")
	private RcmCompany company;
	
	@Column(name = "sheet_id", length = 100)
	private String sheetId;
	
	@Column(name = "sheet_name", length = 100)
	private String sheetName;
	
	@Column(name = "sheet_sub_id", length = 100)
	private String sheetSubId;
}
