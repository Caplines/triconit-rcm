package com.tricon.ruleengine.dao.impl;

import javax.persistence.criteria.CriteriaBuilder;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.tricon.ruleengine.dao.UserDao;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.User;

/**
 * @author Deepak.Dogra
 *
 */
@Repository
public class UserDaoImpl extends BaseDaoImpl implements UserDao {

	@Override
	public User findUserByUsername(String username) {

		return (User) getEntityByColumnName(User.class, "", username);
	}

	@Override
	public User findUserByEmail(String email) {

		return (User) getEntityByColumnName(User.class, "", email);
	}

	@Override
	public Office findOfficeById(String officeId) {

		return (Office) getEntityById(Office.class, officeId);

	}

	@Override
	public User registerUser(User user) {
		return (User) saveEntiy(user);
	}

}
