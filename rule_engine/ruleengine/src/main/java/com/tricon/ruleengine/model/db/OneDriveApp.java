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

@Entity
@Table(name = "one_drive_app")
public class OneDriveApp extends BaseAudit implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8358378205431676300L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	private int id;


	@Column(name = "application_id",unique=true)
	private String appLicationId;
	
	@Column(name = "client_secret")
	private String clientSecret;

	@Column(name = "access_token",columnDefinition="text")
	private String accessToken;

	@Column(name = "refresh_token",columnDefinition="text")
	private String refreshToken;
	
	@Column(name = "auth_token",unique=true,columnDefinition="text")
	private String authToken;

	@Column(name = "rd_url")
	private String rdUrl;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "office_id")
	private Office office;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAppLicationId() {
		return appLicationId;
	}

	public void setAppLicationId(String appLicationId) {
		this.appLicationId = appLicationId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}

	public String getRdUrl() {
		return rdUrl;
	}

	public void setRdUrl(String rdUrl) {
		this.rdUrl = rdUrl;
	}
	
	

}
