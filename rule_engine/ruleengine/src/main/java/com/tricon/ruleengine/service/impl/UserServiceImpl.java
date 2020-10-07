package com.tricon.ruleengine.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.tricon.ruleengine.dao.CompanyDao;
import com.tricon.ruleengine.dao.OfficeDao;
import com.tricon.ruleengine.dao.UserDao;
import com.tricon.ruleengine.dto.GenericResponse;
import com.tricon.ruleengine.dto.OfficeDto;
import com.tricon.ruleengine.dto.PasswordResetDto;
import com.tricon.ruleengine.dto.StatusResetDto;
import com.tricon.ruleengine.dto.UserEmailIdDto;
import com.tricon.ruleengine.dto.UserRegistrationDto;
import com.tricon.ruleengine.dto.UserSettingsDto;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.User;
import com.tricon.ruleengine.model.db.UserRole;
import com.tricon.ruleengine.security.JwtUser;
import com.tricon.ruleengine.service.UserService;
import com.tricon.ruleengine.utils.Constants;
import com.tricon.ruleengine.utils.DtoToModel;
import com.tricon.ruleengine.utils.EncrytedKeyUtil;

/**
 * @author Deepak.Dogra
 *
 */
@Transactional
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserDao userDao;
	@Autowired
	OfficeDao officeDao;
	@Autowired
	CompanyDao companyDao;
	
	
	@Autowired
	@Qualifier("jwtUserDetailsService")
	private UserDetailsService userDetailsService;

	@Override
	public GenericResponse registerUser(UserRegistrationDto dto) {
		Office office = userDao.findOfficeById(dto.getOfficeId());
		if (office != null) {
			User user = userDao.findUserByUsername(dto.getUserName());
			if (user == null) {
				user = DtoToModel.convertRegistrationDto(dto, office);
				user.setCompany(companyDao.getCompanyByUUId(dto.getCuuid()));
				user.setUuid((String) userDao.registerUser(user));
				UserRole role = new UserRole();
				role.setRole(Constants.APP_ROLE_USER);
				role.setUser(user);
				
				userDao.generateUserRole(role);
				return new GenericResponse(HttpStatus.OK, "User Created Successfully", null);
			}
			return new GenericResponse(HttpStatus.BAD_REQUEST, "User Already Exists", null);
		}

		return new GenericResponse(HttpStatus.BAD_REQUEST, "In correct Office name", null);
	}

	@Override
	public GenericResponse resetUserStatus(StatusResetDto dto) {
		User user = userDao.findUserByUUid(dto.getUuid());
			if (user != null) {
				user.setActive(dto.getStatus());
				userDao.updateUser(user);
				return new GenericResponse(HttpStatus.OK, "User status updated Successfully", null);
			}
			return new GenericResponse(HttpStatus.BAD_REQUEST, "User Does not Exists", null);

		
	}

	@Override
	public GenericResponse resetUserPassword(PasswordResetDto dto) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails)principal).getUsername());
		JwtUser userJ = (JwtUser) userDetails;
		List<SimpleGrantedAuthority> list=(List<SimpleGrantedAuthority>) userJ.getAuthorities();
		
		boolean isAdmin=false;
		for (SimpleGrantedAuthority s:list) {
			if (s.getAuthority().equals("ROLE_ADMIN")) {
				isAdmin=true;
			}
		}
		if (dto.getPassword().trim().equals("")) {
			return new GenericResponse(HttpStatus.BAD_REQUEST, "Password is empty", null);
		}
		//make sure that only admin can change other users password
		if (!isAdmin) dto.setUuid(userJ.getUuid());

			User user = userDao.findUserByUUid(dto.getUuid());
			if (user != null) {
				user.setPassword(EncrytedKeyUtil.encryptKey(dto.getPassword()));
				user.setLastPasswordResetDate(new Date());
				userDao.updateUser(user);
				//userDao.
				return new GenericResponse(HttpStatus.OK, "User password updated Successfully", null);
			}
			return new GenericResponse(HttpStatus.BAD_REQUEST, "User Does not Exists", null);

		
	}

	@Override
	public GenericResponse findUserByUserName(String userName) {
			User user = userDao.findUserByUsername(userName);
			if (user != null) {
				UserSettingsDto d= new UserSettingsDto();
				d.setStatus(user.getActive());
				d.setUuid(user.getUuid());
				d.setUserName(user.getUserName());
				d.setType(user.getUserType());
				return new GenericResponse(HttpStatus.OK, "User Exists", d);
			}
			return new GenericResponse(HttpStatus.BAD_REQUEST, "User Does not Exists", null);

		
	}

	@Override
	public GenericResponse resetClaimTreatmentRight(StatusResetDto dto) {
		User user = userDao.findUserByUUid(dto.getUuid());
		int type=dto.getStatus();
		if ( type == Constants.userType_CL || type == Constants.userType_TR) {
			
		
		if (user != null) {
			user.setUserType(type);
			userDao.updateUser(user);
			return new GenericResponse(HttpStatus.OK, "User status updated Successfully", null);
		}
		return new GenericResponse(HttpStatus.BAD_REQUEST, "User Does not Exists", null);
		}else {
			return new GenericResponse(HttpStatus.BAD_REQUEST, "Bad status", null);
				
		}
	}

	@Override
	public Optional<List<OfficeDto>> getAllOffices(String companyId) {
		return officeDao.getAllOffices(companyId);
	}

	@Override
	public GenericResponse getAllUsers(int active) {
		 List<UserEmailIdDto>  li= userDao.getAllUsers(active);
			return new GenericResponse(HttpStatus.OK, "Users list", li);
	}

}
