package com.tricon.ruleengine.dao;

import java.util.List;

import com.tricon.ruleengine.model.db.IVFDefaultValue;
import com.tricon.ruleengine.model.db.SealantEligibilityRule;

public interface SealantEligibilityRuleDao {

	public List<SealantEligibilityRule> getActiveSealantEligibilityRuleData();

	public List<IVFDefaultValue> getActiveIVFDefaultValues();

}
