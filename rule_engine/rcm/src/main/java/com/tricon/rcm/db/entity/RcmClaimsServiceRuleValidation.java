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
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;

import com.tricon.rcm.db.BaseAuditEntity;

import lombok.Data;

@Data
@Entity
@Table(name = "rcm_claims_service_rule_val", uniqueConstraints =
{ @UniqueConstraint(columnNames = { "claim_id","service_code","name"
		                             }) })
public class RcmClaimsServiceRuleValidation  extends BaseAuditEntity implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7997397837722971478L;

	@GeneratedValue(generator = "uuid2") // only possible for Id's
	@Id
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name = "validation_uuid", nullable = false, length = 45)
	private String remarkUuid;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "claim_id", referencedColumnName = "claim_uuid")
	private RcmClaims claim;
	
	@Column(name = "service_code", length = 10)
	private String serviceCode;
	
	@Column(name = "name", length = 100)
	private String name;
	
	@Column(name = "description", length = 255)
	private String description;
	
	@Column(name = "insurance_types", length = 255)
	private String insuranceTypes;
	
	@Column(name = "display_values", length = 255)
	private String displayValues;
	
	@Column(name = "active", columnDefinition = "BIT default 1")
	private boolean active;
	
	@Column(name = "value", length = 255)
	private String value;
	
	@Column(name = "message_type")
	private int messageType;
	
	@Column(name = "remark", columnDefinition = "text", nullable = true)
	private String remark;
	
	@Column(name = "manual_auto", length = 30, nullable = true)
	private String manualAuto;
	
	@Column(name = "answer",  length = 100, nullable = true)
	private String answer;
	
}
