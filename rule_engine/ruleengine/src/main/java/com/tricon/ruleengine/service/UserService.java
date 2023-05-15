package com.tricon.ruleengine.service;

import java.util.List;
import java.util.Optional;

import com.tricon.ruleengine.dto.GenericResponse;
import com.tricon.ruleengine.dto.OfficeDto;
import com.tricon.ruleengine.dto.PasswordResetDto;
import com.tricon.ruleengine.dto.StatusResetDto;
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
	
	public Optional<List<OfficeDto>> getAllOffices(String companyId); 
	
	public GenericResponse resetUserPassword(PasswordResetDto dto);
	
	public GenericResponse resetUserStatus(StatusResetDto dto);
	
	public GenericResponse findUserByUserName(String userName);

	public GenericResponse resetClaimTreatmentRight(StatusResetDto dto);
	
	public GenericResponse getAllUsers(int active);
	
	public List<OfficeDto> getAllOfficesByCompanyName(String companyName);

	public List<OfficeDto> getAllActiveOfficesByCompanyName(String companyName);
	

}
