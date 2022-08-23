package com.tricon.ruleengine.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import com.tricon.ruleengine.dao.DaysCalculationDao;
import com.tricon.ruleengine.model.db.ReplicationDays;

@Repository
public class DaysCalDaoImpl extends BaseDaoImpl implements DaysCalculationDao
{
	private ReplicationDays replicationDays=null;
	
	@Override
	public ReplicationDays findByQueryName(String query) {
		Session s=getSession();		
		try {
		Criteria criteria=s.createCriteria(ReplicationDays.class);
		criteria.add(Restrictions.eq("queryName",query.trim()));
		replicationDays=(ReplicationDays)criteria.uniqueResult();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			closeSession(s);
		}
		return replicationDays;
	}
}


