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
import javax.persistence.UniqueConstraint;

/**
 * 
 * @author Deepak.Dogra
 *
 */
@Entity
@Table(name = "scrapping_site_details" , uniqueConstraints=
@UniqueConstraint(columnNames={"scrapping_site_id", "google_sheet_name","office_id"}))
public class ScrappingSiteDetails extends BaseAudit implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3725441871862683227L;

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "scrapping_site_id")
	private ScrappingSite scrappingSite;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "office_id")
	private Office office;

	@Column(name = "user_name")
	private String userName;

	@Column(name = "password")
	private String password;

	@Column(name = "google_sheet_id")
	private String googleSheetId;

	@Column(name = "google_sheet_name")
	private String googleSheetName;

	@Column(name = "is_running")
	private boolean isRunning;

	@Column(name = "proxy_port")
	private String proxyPort;

	@Column(name = "row_count")
	private int rowCount;

	@Column(name = "google_sub_id")
	private String googleSubId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ScrappingSite getScrappingSite() {
		return scrappingSite;
	}

	public void setScrappingSite(ScrappingSite scrappingSite) {
		this.scrappingSite = scrappingSite;
	}

	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getGoogleSheetId() {
		return googleSheetId;
	}

	public void setGoogleSheetId(String googleSheetId) {
		this.googleSheetId = googleSheetId;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public String getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(String proxyPort) {
		this.proxyPort = proxyPort;
	}

	public int getRowCount() {
		return rowCount;
	}

	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	public String getGoogleSheetName() {
		return googleSheetName;
	}

	public void setGoogleSheetName(String googleSheetName) {
		this.googleSheetName = googleSheetName;
	}

	public String getGoogleSubId() {
		return googleSubId;
	}

	public void setGoogleSubId(String googleSubId) {
		this.googleSubId = googleSubId;
	}

	
	
}
