package com.tricon.ruleengine.dao.impl;

import java.io.Serializable;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.tricon.ruleengine.dao.UserDao;
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

		return (User) getEntityByColumnName(User.class, "userName", username);
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

}
