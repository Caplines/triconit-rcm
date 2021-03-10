package com.tricon.ruleengine.service;

import java.util.List;

import com.tricon.ruleengine.model.db.OSIVFormCodes;

public interface OSIVFormCodesService {

	public List<OSIVFormCodes> getAllActiveOSIVCodes();
}
