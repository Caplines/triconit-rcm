package com.tricon.rcm.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Google Spreadsheet IDs and tab names for RCM (backed by env vars in
 * application-*.properties).
 */
@Component
@ConfigurationProperties(prefix = "rcm.sheet")
public class RcmGoogleSheetsProperties {

	private String rcmDatabaseId;
	private String rcmDatabaseSubId;
	private String rcmDatabaseSubName;
	private String mappingWorkbookId;
	private String mappingProviderTab;
	private String providerScheduleId;
	private String serviceValidationId;
	private String serviceValidationTab;
	private String credentialTrackerId;
	private String credentialTrackerAnesTab;
	private String credentialTrackerMasterTab;
	private String remoteLiteId;
	private String remoteLiteTab;

	public String getRcmDatabaseId() {
		return rcmDatabaseId;
	}

	public void setRcmDatabaseId(String rcmDatabaseId) {
		this.rcmDatabaseId = rcmDatabaseId;
	}

	public String getRcmDatabaseSubId() {
		return rcmDatabaseSubId;
	}

	public void setRcmDatabaseSubId(String rcmDatabaseSubId) {
		this.rcmDatabaseSubId = rcmDatabaseSubId;
	}

	public String getRcmDatabaseSubName() {
		return rcmDatabaseSubName;
	}

	public void setRcmDatabaseSubName(String rcmDatabaseSubName) {
		this.rcmDatabaseSubName = rcmDatabaseSubName;
	}

	public String getMappingWorkbookId() {
		return mappingWorkbookId;
	}

	public void setMappingWorkbookId(String mappingWorkbookId) {
		this.mappingWorkbookId = mappingWorkbookId;
	}

	public String getMappingProviderTab() {
		return mappingProviderTab;
	}

	public void setMappingProviderTab(String mappingProviderTab) {
		this.mappingProviderTab = mappingProviderTab;
	}

	public String getProviderScheduleId() {
		return providerScheduleId;
	}

	public void setProviderScheduleId(String providerScheduleId) {
		this.providerScheduleId = providerScheduleId;
	}

	public String getServiceValidationId() {
		return serviceValidationId;
	}

	public void setServiceValidationId(String serviceValidationId) {
		this.serviceValidationId = serviceValidationId;
	}

	public String getServiceValidationTab() {
		return serviceValidationTab;
	}

	public void setServiceValidationTab(String serviceValidationTab) {
		this.serviceValidationTab = serviceValidationTab;
	}

	public String getCredentialTrackerId() {
		return credentialTrackerId;
	}

	public void setCredentialTrackerId(String credentialTrackerId) {
		this.credentialTrackerId = credentialTrackerId;
	}

	public String getCredentialTrackerAnesTab() {
		return credentialTrackerAnesTab;
	}

	public void setCredentialTrackerAnesTab(String credentialTrackerAnesTab) {
		this.credentialTrackerAnesTab = credentialTrackerAnesTab;
	}

	public String getCredentialTrackerMasterTab() {
		return credentialTrackerMasterTab;
	}

	public void setCredentialTrackerMasterTab(String credentialTrackerMasterTab) {
		this.credentialTrackerMasterTab = credentialTrackerMasterTab;
	}

	public String getRemoteLiteId() {
		return remoteLiteId;
	}

	public void setRemoteLiteId(String remoteLiteId) {
		this.remoteLiteId = remoteLiteId;
	}

	public String getRemoteLiteTab() {
		return remoteLiteTab;
	}

	public void setRemoteLiteTab(String remoteLiteTab) {
		this.remoteLiteTab = remoteLiteTab;
	}
}
