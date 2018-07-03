package com.tricon.ruleengine.service;

import java.util.List;
import java.util.Optional;

import com.tricon.ruleengine.dto.OfficeDto;
import com.tricon.ruleengine.dto.UserRegistrationDto;
import com.tricon.ruleengine.model.db.User;

/**
 * @author Deepak.Dogra
 *
 */
public interface UserService {
	
	/**
	 * Register User To the Application but will not able it. 
	 * @param dto
	 * @return
	 */
	public User registerUser(UserRegistrationDto dto); 
	
	public Optional<List<OfficeDto>> getAllOffices(); 

}
