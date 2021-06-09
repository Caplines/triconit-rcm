package com.tricon.ruleengine.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.tricon.ruleengine.dao.SealantEligibilityRuleDao;
import com.tricon.ruleengine.model.db.IVFDefaultValue;
import com.tricon.ruleengine.model.db.SealantEligibilityRule;


@Repository
public class SealantEligibilityRuleDaoImpl extends BaseDaoImpl implements SealantEligibilityRuleDao{

	@Override
	public List<SealantEligibilityRule> getActiveSealantEligibilityRuleData() {
		// TODO Auto-generated method stub
		Session session = getSession();
		List<Object> list = null;
		try {
			Criteria criteria = session.createCriteria(SealantEligibilityRule.class);
			criteria.add(Restrictions.eq("active", 1));
			list =  criteria.list();
			
		} finally {
			closeSession(session);

		}
		return (List<SealantEligibilityRule>) (List<?>) list;

	}

	@Override
	public List<IVFDefaultValue> getActiveIVFDefaultValues() {
		Session session = getSession();
		List<Object> list = null;
		try {
			Criteria criteria = session.createCriteria(IVFDefaultValue.class);
			criteria.add(Restrictions.eq("active", 1));
			list =  criteria.list();
			
		} finally {
			closeSession(session);

		}
		return (List<IVFDefaultValue>) (List<?>) list;
	}

}
