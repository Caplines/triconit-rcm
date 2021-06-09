package com.tricon.ruleengine.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tricon.ruleengine.dao.SealantEligibilityRuleDao;
import com.tricon.ruleengine.model.db.IVFDefaultValue;
import com.tricon.ruleengine.model.db.SealantEligibilityRule;
import com.tricon.ruleengine.service.SealantEligibilityRuleService;

@Transactional
@Service
public class SealantEligibilityRuleServiceImpl implements SealantEligibilityRuleService {

	@Autowired
	SealantEligibilityRuleDao dao;
	
	@Override
	public List<SealantEligibilityRule> getActiveSealantEligibilityRuleData() {
		// TODO Auto-generated method stub
		return dao.getActiveSealantEligibilityRuleData();
	}

	@Override
	public List<IVFDefaultValue> getActiveIVFDefaultValues() {
		// TODO Auto-generated method stub
		return dao.getActiveIVFDefaultValues();
	}

}
