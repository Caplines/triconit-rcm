package com.tricon.ruleengine.service;

import java.util.List;
import java.util.Optional;

import com.tricon.ruleengine.dto.GenericResponse;
import com.tricon.ruleengine.dto.OfficeDto;
import com.tricon.ruleengine.dto.PasswordResetDto;
import com.tricon.ruleengine.dto.UserRegistrationDto;

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
	public GenericResponse registerUser(UserRegistrationDto dto); 
	
	public Optional<List<OfficeDto>> getAllOffices(); 
	
	public GenericResponse resetUserPassword(PasswordResetDto dto);

}
