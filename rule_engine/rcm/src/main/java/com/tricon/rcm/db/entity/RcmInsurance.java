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
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.tricon.rcm.db.BaseAuditEntity;

import lombok.Data;

@Data
@Entity
@Table(name = "rcm_insurance", uniqueConstraints = { @UniqueConstraint(columnNames = { "insurance_id", "office_id" }) })
public class RcmInsurance extends BaseAuditEntity implements Serializable{

   /**
	 * 
	 */
	private static final long serialVersionUID = 5571111391743504351L;
	

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "insurance_id", nullable = false)
	private String insuranceId;
	
	@Column(name = "active", nullable = false)
	private Boolean active;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "office_id", referencedColumnName = "uuid")
	private RcmOffice office;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "insurance_type_id", referencedColumnName = "id",nullable=true)
	private RcmInsuranceType insuranceType;
}
