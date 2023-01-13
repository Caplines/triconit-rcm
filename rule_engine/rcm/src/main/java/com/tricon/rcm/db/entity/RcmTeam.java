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
@Table(name = "rcm_team")
public class RcmTeam {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;

	@Column(name = "name", unique = true, nullable = false)
	private String name;
	
	@Column(name = "name_id", unique = true, nullable = false)
	private String nameId;

	@Column(name = "description", nullable = false)
	private String description;

	@Column(name = "active", nullable = false)
	private int active;



}
