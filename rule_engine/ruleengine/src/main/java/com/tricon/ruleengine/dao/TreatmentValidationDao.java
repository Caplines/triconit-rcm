package com.tricon.ruleengine.dao;

import java.util.List;

import com.tricon.ruleengine.model.db.GoogleSheets;
import com.tricon.ruleengine.model.db.Rules;

public interface TreatmentValidationDao {
	
	public GoogleSheets getSheetByAppSheetId(int id);
	
	public List<Rules> getAllActiveRules();
	
	

}
