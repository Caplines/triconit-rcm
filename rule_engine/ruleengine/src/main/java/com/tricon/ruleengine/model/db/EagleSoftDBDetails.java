package com.tricon.ruleengine.model.db;

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

/**
 * This entity class represents EagleSoft Database Details 
 * @author Deepak.Dogra
 *
 */

@Entity
@Table(name = "eaglesoft_db_details")
public class EagleSoftDBDetails extends BaseAudit implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5388242519384452950L;


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	private int id;


	@Column(name = "ip_address")
	private String ipAddress;

	@Column(name = "es_port")
	private int eSport;


	@Column(name = "password")
	private String password;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "office_id", unique=true)
	private Office office;

	@Column(name = "is_server")
	private boolean server;
    @Column(name = "is_sheet")
	private int sheet;
	
	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getIpAddress() {
		return ipAddress;
	}


	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}


	public int geteSport() {
		return eSport;
	}


	public void seteSport(int eSport) {
		this.eSport = eSport;
	}


	public Office getOffice() {
		return office;
	}


	public void setOffice(Office office) {
		this.office = office;
	}
	
	
	public boolean isServer() {
		return server;
	}


	public void setServer(boolean server) {
		this.server = server;
	}
    
	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public void setSheet(int sheet) {
		this.sheet = sheet;
	}


	public int getSheet() {
		return sheet;
	}
	
 
	

}
