package com.tricon.ruleengine.dao;

import java.util.List;

import com.tricon.ruleengine.model.db.OSIVFormCodes;

public interface OSIVFormCodesDao {
	
	public List<OSIVFormCodes> getAllActiveOSIVCodes();

}
