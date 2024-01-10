package com.tricon.rcm.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "rcm_claim_section_category")
public class RcmSectionCategory implements java.io.Serializable{

	private static final long serialVersionUID = -7429410272124276890L;

    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
    private int id;
	
	@Column(name = "section_category")
	private String category;
}
