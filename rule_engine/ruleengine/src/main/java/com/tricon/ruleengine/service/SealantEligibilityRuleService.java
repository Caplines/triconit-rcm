package com.tricon.ruleengine.service;

import java.util.List;

import com.tricon.ruleengine.model.db.IVFDefaultValue;
import com.tricon.ruleengine.model.db.SealantEligibilityRule;

public interface SealantEligibilityRuleService {
	
	public List<SealantEligibilityRule> getActiveSealantEligibilityRuleData();
	
	public List<IVFDefaultValue> getActiveIVFDefaultValues();

}
