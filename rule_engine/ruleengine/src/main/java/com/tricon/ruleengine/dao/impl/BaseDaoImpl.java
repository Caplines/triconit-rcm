package com.tricon.ruleengine.dao.impl;

import java.io.Serializable;
import java.util.List;

//import javax.persistence.criteria.CriteriaBuilder;
//import javax.persistence.criteria.CriteriaQuery;
//import javax.persistence.criteria.Root;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
//import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;

//import com.tricon.ruleengine.dto.OfficeDto;
//import com.tricon.ruleengine.model.db.Office;
//import com.tricon.ruleengine.model.db.User;

/**
 * @author Deepak.Dogra
 *
 */
public class BaseDaoImpl {

	@Autowired
	protected SessionFactory sessionFactory;

	protected Session getSession() {

		return sessionFactory.openSession();

	}

	protected void closeSession(Session s) {
		//System.out.println(s.isOpen());
		if (s != null && s.isOpen()) {
			s.close();
		}
	}

	/*
	 * protected Object executeSqlQuery(String sql,boolean
	 * uniqueResult,Optional<Object> objectTransform) { Session session =
	 * getSession(); SQLQuery query = session.createSQLQuery(sql); Object result =
	 * null; if (uniqueResult) result= query.uniqueResult(); else if
	 * (objectTransform!=null && objectTransform.isPresent()) result=
	 * query.setResultTransformer((ResultTransformer)objectTransform.get()).list();
	 * else result= query.list(); closeSession(session); return result; }
	 */
	protected Serializable saveEntiy(Object object) {
		Session session = getSession();
		Serializable serializable = null;
		try {
			Transaction transaction = session.beginTransaction();
			serializable = session.save(object);
			transaction.commit();
		} finally {
			closeSession(session);

		}
		return serializable;

	}

	/**
	 * This will return Single Result (Only Use for Unique columns values)
	 * @param clazz
	 * @param columnName
	 * @param columnValue
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Object getEntityByColumnName(Class clazz, String columnName, Object columnValue) {
		Session session = getSession();
		Object object = null;
		try {
			Transaction transaction = session.beginTransaction();
			Criteria criteria = session.createCriteria(clazz);
			criteria.add(Restrictions.eq(columnName, columnValue));
			object =  criteria.uniqueResult();
			//offices=(List<OfficeDto>) criteria.setResultTransformer(Transformers.aliasToBean(OfficeDto.class)).list();
			/*
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery criteriaQuery = builder.createQuery(clazz);
			Root queriedObject = criteriaQuery.from(clazz);
			criteriaQuery.where(builder.equal(queriedObject.get(columnName), columnValue));
			object =  session.createQuery(criteriaQuery).getSingleResult();
			*/
			transaction.commit();
			
		} finally {
			closeSession(session);

		}
		return object;

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Object getEntityById(Class clazz, Serializable columnValue) {
		Session session = getSession();
		Object object = null;
		try {
			Transaction transaction = session.beginTransaction();
			object = session.byId(clazz).load(columnValue);
			transaction.commit();
		} finally {
			closeSession(session);

		}
		return object;

	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected List<Object> getWholeEntity(Class clazz) {
		Session session = getSession();
		List<Object> list = null;
		try {
			Transaction transaction = session.beginTransaction();
			Criteria criteria = session.createCriteria(clazz);
			list=criteria.list();
			transaction.commit();
		} finally {
			closeSession(session);

		}
		return list;

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected List<Object> getEntitiesByColumnName(Class clazz, String columnName, Object columnValue) {
		Session session = getSession();
		List<Object> list = null;
		try {
			Transaction transaction = session.beginTransaction();
			Criteria criteria = session.createCriteria(clazz);
			criteria.add(Restrictions.eq(columnName, columnValue));
			list =  criteria.list();
			transaction.commit();
			
		} finally {
			closeSession(session);

		}
		return list;

	}

	protected void updateEntity(Object object) {
		Session session = getSession();
		try {
			Transaction transaction = session.beginTransaction();
			session.update(object);
			transaction.commit();
		} finally {
			closeSession(session);

		}
	}
}
