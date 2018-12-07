package com.tricon.ruleengine.service;


import com.tricon.ruleengine.model.db.EagleSoftDBDetails;
import com.tricon.ruleengine.model.db.Office;

public interface EagleSoftDBService {
	
	public EagleSoftDBDetails getESDBDetailsByOffice(Office offcie);
	

}
