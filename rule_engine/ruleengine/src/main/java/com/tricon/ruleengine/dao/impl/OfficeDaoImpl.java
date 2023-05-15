package com.tricon.ruleengine.dao.impl;

import java.util.List;
import java.util.Optional;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import com.tricon.ruleengine.dao.OfficeDao;
import com.tricon.ruleengine.dto.OfficeDto;
import com.tricon.ruleengine.model.db.Office;

@Repository
public class OfficeDaoImpl extends BaseDaoImpl implements OfficeDao {

	@SuppressWarnings("unchecked")
	@Override
	public Optional<List<OfficeDto>> getAllOffices(String companyuuid) {

		Session session = getSession();
		List<OfficeDto> offices = null;
		try {
			// Transaction transaction = session.beginTransaction();
			Criteria criteria = session.createCriteria(Office.class);
			criteria.createAlias("company", "company");
			criteria.add(Restrictions.eq("company.uuid", companyuuid));
			ProjectionList pjList = Projections.projectionList();
			pjList.add(Projections.property("name"), "name");
			pjList.add(Projections.property("uuid"), "uuid");
			criteria.setProjection(pjList);
			criteria.setResultTransformer(Transformers.aliasToBean(OfficeDto.class));
			offices = criteria.list();
			// transaction.commit();
		} finally {
			closeSession(session);

		}
		return Optional.ofNullable((List<OfficeDto>) offices);

	}

	@Override
	public Office getOfficeByUuid(String uuid, String companyuuid) {
		// TODO Auto-generated method stub
		Session session = getSession();
		Object object = null;
		try {
			// Transaction transaction = session.beginTransaction();
			Criteria criteria = session.createCriteria(Office.class);
			criteria.createAlias("company", "company");
			criteria.add(Restrictions.eq("company.uuid", companyuuid));
			criteria.add(Restrictions.eq("uuid", uuid));
			object = criteria.uniqueResult();

		} finally {
			closeSession(session);

		}
		return (Office) object;

	}

	@Override
	public Office getOfficeByName(String name, String companyuuid) {
		// TODO Auto-generated method stub
		return (Office) getEntityByColumnName(Office.class, "name", name);
	}

	@Override
	public List<OfficeDto> getAllOfficesByCompanyName(String companyName) {
		Session session = getSession();
		List<OfficeDto> cL = null;
		try {
            String query="select this_.name as name, this_.uuid as uuid from "  +
            		" office this_ inner join company company1_ on this_.company_id=company1_.uuid where " + 
            		" company1_.name='"+companyName+"'";
			cL=session.createSQLQuery(query).setResultTransformer(Transformers.aliasToBean(OfficeDto.class)). list();
		} finally {
			closeSession(session);
		}
		return cL;
	}

	@Override
	public List<OfficeDto> getAllActiveOfficesByCompanyName(String companyName) {
		Session session = getSession();
		List<OfficeDto> cL = null;
		try {
			String query = "select off.name as name, off.uuid as uuid ,off.active as active from "
					+ " office off inner join company c on c.uuid=off.company_id where "
					+ " c.name=:name and off.active is true";
			SQLQuery q = (SQLQuery) session.createSQLQuery(query).setParameter("name", companyName);
			q.setResultTransformer(Transformers.aliasToBean(OfficeDto.class));
			cL = q.list();
		} finally {
			closeSession(session);
		}
		return cL;
	}

}
