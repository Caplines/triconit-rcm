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

import lombok.Data;

@Data
@Entity
@Table(name = "rcm_claim_section")
public class RcmClaimSection implements java.io.Serializable {

	private static final long serialVersionUID = 570225875296029053L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
    private int id;
	
	@Column(name = "section_name",unique =true)
	private String sectionName;
	
	@Column(name = "section_display_name")
	private String displayName;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "active",columnDefinition = "BIT default 1")
	private boolean active;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "section_category",referencedColumnName="id")
    private RcmSectionCategory sectionCategory;
}
