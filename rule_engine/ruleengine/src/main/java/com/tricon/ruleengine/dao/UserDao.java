package com.tricon.ruleengine.dao;

import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.User;

/**
 * @author Deepak.Dogra
 *
 */
public interface UserDao {

	User findUserByUsername(String username);
	User findUserByEmail(String email);
	Office findOfficeById(String officeId);
	User registerUser(User user);
	
}
