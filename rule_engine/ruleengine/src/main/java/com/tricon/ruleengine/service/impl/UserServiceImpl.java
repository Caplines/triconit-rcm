package com.tricon.ruleengine.service.impl;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tricon.ruleengine.dao.OfficeDao;
import com.tricon.ruleengine.dao.UserDao;
import com.tricon.ruleengine.dto.OfficeDto;
import com.tricon.ruleengine.dto.UserRegistrationDto;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.User;
import com.tricon.ruleengine.service.UserService;

/**
 * @author Deepak.Dogra
 *
 */
@Transactional
@Service
public class UserServiceImpl implements UserService{

	
	@Autowired UserDao userDao;
	@Autowired OfficeDao officeDao;
	
	@Override
	public User registerUser(UserRegistrationDto dto) {
		// TODO Auto-generated method stub
		
		Office office = userDao.findOfficeById(dto.getOfficeId());
		if (office!=null) {
		  User user = userDao.findUserByUsername(dto.getName());
		  if (user==null) {
			  userDao.registerUser(user);
		  }
		}
		
		return null;
	}

	@Override
	public  Optional<List<OfficeDto>> getAllOffices() {
		return officeDao.getAllOffices();
	}
	
	
	
  
	
}
