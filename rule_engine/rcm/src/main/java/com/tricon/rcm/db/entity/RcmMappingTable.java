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

import lombok.Data;

@Data
@Entity
@Table(name = "rcm_mapping_table")
public class RcmMappingTable implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5451770989210615183L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;
	
	@Column(name = "name", unique = true,length=100)
	private String name;
	
	@Column(name = "google_sheet_id" , length=255)
	private String googleSheetId;
	
	@Column(name = "google_sheet_sub_id" , length=255)
	private String googleSheetSubId;
	
	@Column(name = "google_sheet_sub_name" , length=255)
	private String googleSheetSubName;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "office_id")
	private RcmOffice office;
}
