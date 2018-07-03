package com.tricon.ruleengine.dao.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Metamodel;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import com.tricon.ruleengine.dao.OfficeDao;
import com.tricon.ruleengine.dto.OfficeDto;
import com.tricon.ruleengine.model.db.Office;

@Repository
public class OfficeDaoImpl extends BaseDaoImpl implements OfficeDao{

	@SuppressWarnings("unchecked")
	@Override
	public Optional<List<OfficeDto>>  getAllOffices() {
		
		Session session = getSession();
		List<OfficeDto> offices  = null;
		try {
			Transaction transaction = session.beginTransaction();
			Criteria criteria = session.createCriteria(Office.class);
			//criteria.createAlias("offices", "offices");
			ProjectionList pjList = Projections.projectionList();
			pjList.add(Projections.property("name"), "name");
			pjList.add(Projections.property("uuid"), "uuid");
			criteria.setProjection(pjList);
			criteria.setResultTransformer(Transformers.aliasToBean(OfficeDto.class));
			offices = criteria.list();
			transaction.commit();
		} finally {
			closeSession(session);

		}
		return Optional.ofNullable((List<OfficeDto>) offices);
		
	}
	
	

}
