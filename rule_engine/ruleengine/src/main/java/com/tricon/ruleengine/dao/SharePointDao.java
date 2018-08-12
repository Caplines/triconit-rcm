package com.tricon.ruleengine.dao;

import java.util.List;

import com.tricon.ruleengine.model.db.OneDriveApp;
import com.tricon.ruleengine.model.db.OneDriveFile;

public interface SharePointDao {
	
	public OneDriveApp getOneDriveAppDetailsByOfficeId(String officeId);

	public OneDriveApp getOneDriveAppDetailsByAuthCode(String authCode);

	public void updateOneDrive(OneDriveApp oneD);

	public List<OneDriveFile> getOneDriveFileByOfficeId(String officeId);
}
