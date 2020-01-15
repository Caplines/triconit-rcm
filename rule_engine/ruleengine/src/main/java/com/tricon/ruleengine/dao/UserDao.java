package com.tricon.ruleengine.dao;

import java.util.List;

import com.tricon.ruleengine.dto.UserEmailIdDto;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.User;
import com.tricon.ruleengine.model.db.UserRole;

/**
 * @author Deepak.Dogra
 *
 */
public interface UserDao {

	public User findUserByUsername(String username);
	public User findUserByUUid(String uuid);
	public User findUserByEmail(String email);
	public Office findOfficeById(String officeId);
	public String registerUser(User user);
	public void generateUserRole(UserRole role);
	public User findUserAndOfficeByEmail(String email);
	public void updateUser(User user);
	public List<UserEmailIdDto> getAllUsers(int active);
	
}
