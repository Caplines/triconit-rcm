package com.tricon.rcm.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.tricon.rcm.db.entity.RcmCompany;
import com.tricon.rcm.db.entity.RcmOffice;
import com.tricon.rcm.db.entity.RcmTeam;
import com.tricon.rcm.db.entity.RcmUser;
import com.tricon.rcm.db.entity.RcmUserRole;
import com.tricon.rcm.db.entity.RcmUserRolePk;
import com.tricon.rcm.dto.FindUserDto;
import com.tricon.rcm.dto.GenericResponse;
import com.tricon.rcm.dto.PasswordResetDto;
import com.tricon.rcm.dto.RcmUserDto;
import com.tricon.rcm.dto.RcmUserToDto;
import com.tricon.rcm.dto.ResetStatusDto;
import com.tricon.rcm.dto.UserRegistrationDto;
import com.tricon.rcm.enums.UserRoleEnum;
import com.tricon.rcm.jpa.repository.RCMUserRepository;
import com.tricon.rcm.jpa.repository.RcmCompanyRepo;
import com.tricon.rcm.jpa.repository.RcmOfficeRepository;
import com.tricon.rcm.jpa.repository.RcmTeamRepo;
import com.tricon.rcm.jpa.repository.RcmUserRoleRepo;
import com.tricon.rcm.security.JwtUser;
import com.tricon.rcm.util.Constants;
import com.tricon.rcm.util.EncrytedKeyUtil;
import com.tricon.rcm.util.MessageConstants;
import com.tricon.rcm.util.RoleUtil;

@Service
public class AdminServiceImpl {

	private final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

	@Autowired
	RcmOfficeRepository officeRepo;

	@Autowired
	RCMUserRepository userRepo;

	@Autowired
	RcmTeamRepo teamRepo;

	@Autowired
	RcmCompanyRepo rcmCompanyRepo;

	@Autowired
	@Qualifier("jwtUserDetailsService")
	private UserDetailsService userDetailsService;

	@Autowired
	RcmUserRoleRepo userRole;

	@Autowired
	RcmCommonServiceImpl commonService;

	/**
	 * This Method save Data of New Register User
	 * 
	 * @param dto
	 * @return Generic response
	 */
	@Transactional(rollbackOn = Exception.class)
	public GenericResponse registerUser(UserRegistrationDto dto) throws Exception {
		RcmOffice office = officeRepo.findByUuid(dto.getOfficeId());
		RcmUserRole roles = new RcmUserRole();
		RcmUserRolePk pk = new RcmUserRolePk();
		RcmUser user = null;
		if (office != null) {
			user = userRepo.findByUserNameOrEmail(dto.getUserName(), dto.getEmail());
			if (user == null) {
				RcmTeam team = teamRepo.findById(dto.getTeamId());
				RcmCompany company = rcmCompanyRepo.findByName(Constants.COMPANY_NAME);
				user = convertDtotoModel(dto);
				user.setOffice(office);
				user.setTeam(team);
				user.setCompany(company);
				user = userRepo.save(user);
				pk.setUuid(user.getUuid());
				roles.setId(pk);
				UserRoleEnum rolesEnum = UserRoleEnum.valueOf(team.getId());
				roles.setRole(RoleUtil.generateRole(rolesEnum, dto.getUserRole()));
				System.out.println(roles.getRole());
				userRole.save(roles);
				return new GenericResponse(HttpStatus.OK, MessageConstants.USER_CREATION, null);
			}
			return new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.USER_EXIST, null);
		}

		return new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.INCORRECT_OFFICE_NAME, null);
	}

	/**
	 * This Method convert DTO to RcmUser Model
	 * 
	 * @param dto
	 * @return RcmUser
	 */
	private RcmUser convertDtotoModel(UserRegistrationDto dto) {
		RcmUser user = new RcmUser();
		user.setFirstName(dto.getFirstName());
		user.setLastName(dto.getLastName());
		user.setPassword(EncrytedKeyUtil.encryptKey(dto.getPassword()));
		user.setActive(1);
		user.setEmail(dto.getEmail());
		user.setUserName(dto.getUserName());
		user.setCreatedBy(user);
		return user;
	}

	/**
	 * This Method finds user by userName whose status is enable This Method can use
	 * only ADMIN
	 * 
	 * @param dto
	 * @return user details
	 */
	public GenericResponse findUserByUserName(FindUserDto dto) throws Exception {
		RcmUser user = userRepo.findByUserNameAndActive(dto.getUsername(), Constants.ENABLE);
		if (user != null) {
			RcmUserDto data = new RcmUserDto();
			BeanUtils.copyProperties(user, data);
			data.setFullName(String.join(" ", user.getFirstName(), user.getLastName()));
			return new GenericResponse(HttpStatus.OK, MessageConstants.USER_EXIST, data);
		}
		return new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.USER_NOT_EXIST, null);
	}

	/**
	 * This Method is used to fetch All user records This Method can use only ADMIN
	 * 
	 * @return List of user Details
	 */
	public GenericResponse getAllUsers() throws Exception {
		String companyUuid = "";
		RcmCompany company = rcmCompanyRepo.findByName(Constants.COMPANY_NAME);
		if (company != null) {
			companyUuid = company.getUuid();
			List<RcmUserToDto> listOfUsers = userRepo.getAllUser(companyUuid);
			if (listOfUsers != null && !listOfUsers.isEmpty()) {
				return new GenericResponse(HttpStatus.OK, "", listOfUsers);
			}
		}
		return new GenericResponse(HttpStatus.OK, "", null);
	}

	/**
	 * This Method enable/disable status based on user uuid. This Method can use
	 * only ADMIN
	 * 
	 * @param dto
	 * @param logInUser
	 */
	@Transactional(rollbackOn = Exception.class)
	public GenericResponse resetUserStatus(ResetStatusDto dto)  throws Exception {
		List<String> enables = dto.getEnable();
		List<String> disables = dto.getDisable();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
		JwtUser jwtUser = (JwtUser) userDetails;
		RcmUser logInUser = userRepo.findByUserName(jwtUser.getUserName());
		String updatedBy = logInUser.getUuid();
		try {
			if (enables != null && !enables.isEmpty()) {
				userRepo.enableOrDisableStatus(Constants.ENABLE, updatedBy, enables);
			}
			if (disables != null && !disables.isEmpty()) {
				userRepo.enableOrDisableStatus(Constants.DISABLE, updatedBy, disables);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.UPDATION_FAIL, null);
		}
		return new GenericResponse(HttpStatus.OK, MessageConstants.RECORDS_UPDATE, null);

	}

	/**
	 * This method does change user's password basis of user's uuid and password
	 * Password can change only admin
	 * 
	 * @param jwtUser
	 * @param dto
	 * @return GenericResponse
	 */
	@Transactional(rollbackOn = Exception.class)
	public GenericResponse passwordUpdation(PasswordResetDto dto) throws Exception {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
		JwtUser jwtUser = (JwtUser) userDetails;
		String msg = "";
		RcmUser user = null, loginUser = null;
		loginUser = userRepo.findByUserName(jwtUser.getUserName());
		if (loginUser != null) {
			if (!dto.getUuid().equals(loginUser.getUuid())) {
				user = userRepo.findByUuid(dto.getUuid());
				msg = commonService.resetPassword(user, loginUser, dto.getPassword());
			} else {
				msg = MessageConstants.UPDATION_FAIL;
			}
		} else {
			msg = MessageConstants.USER_NOT_EXIST;
		}
		return new GenericResponse(HttpStatus.OK, msg, null);
	}
}
