package com.tricon.ruleengine.dao.impl;


import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import com.tricon.ruleengine.dao.UserDao;
import com.tricon.ruleengine.dto.OfficeDto;
import com.tricon.ruleengine.dto.UserEmailIdDto;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.User;
import com.tricon.ruleengine.model.db.UserRole;

/**
 * @author Deepak.Dogra
 *
 */
@Repository
public class UserDaoImpl extends BaseDaoImpl implements UserDao {

	@Override
	public User findUserByUsername(String username) {

		
		Session session = getSession();
		Object object = null;
		try {
			//Transaction transaction = session.beginTransaction();
			Criteria criteria = session.createCriteria(User.class);
			criteria.add(Restrictions.eq("userName", username));
			criteria.createAlias("company", "company");
			object =  criteria.uniqueResult();
			
			
		} finally {
			closeSession(session);

		}
		
		return (User) object;
	}

	@Override
	public User findUserByEmail(String email) {

		return (User) getEntityByColumnName(User.class, "email", email);
	}

	@Override
	public User findUserAndOfficeByEmail(String email) {

		Session session = getSession();
		User object = null;
		try {
			Transaction transaction = session.beginTransaction();
			Criteria criteria = session.createCriteria(User.class);
			criteria.add(Restrictions.eq("email", email));
			object =  (User)criteria.uniqueResult();
			Hibernate.initialize(object.getOffice());
			transaction.commit();
			
		} finally {
			closeSession(session);

		}
		return object;

	}

	@Override
	public Office findOfficeById(String officeId) {

		return (Office) getEntityByColumnName(Office.class, "uuid", officeId);

	}

	@Override
	public String registerUser(User user) {
		return (String) saveEntiy(user);
	}
	
	@Override
	public void generateUserRole(UserRole role) {
		 saveEntiy(role);
	}

	@Override
	public void updateUser(User user) {
		// TODO Auto-generated method stub
		updateEntity(user);
	}

	@Override
	public User findUserByUUid(String uuid) {
		return (User) getEntityByColumnName(User.class, "uuid", uuid);
	}
	
	@Override
	public List<UserEmailIdDto> getAllUsers(int active) {
		
		Session session =getSession();
		List<UserEmailIdDto> li=null;
		try {

           Criteria criteria = session.createCriteria(User.class);
           if (active!=-1) criteria.add(Restrictions.eq("active", active));
           ProjectionList pjList = Projections.projectionList();
			pjList.add(Projections.property("email"), "email");
			pjList.add(Projections.property("userName"), "userName");
			pjList.add(Projections.property("uuid"), "uuid");
			pjList.add(Projections.property("firstName"), "firstName");
			pjList.add(Projections.property("lastName"), "lastName");
			
			
			criteria.setProjection(pjList);
			criteria.setResultTransformer(Transformers.aliasToBean(UserEmailIdDto.class));
			li=criteria.list();
			
		}finally {
			closeSession(session);
		}
		return li;
	}

}
