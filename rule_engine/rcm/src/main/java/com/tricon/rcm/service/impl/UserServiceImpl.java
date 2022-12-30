package com.tricon.rcm.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.tricon.rcm.db.entity.RcmTeam;
import com.tricon.rcm.db.entity.RcmUser;
import com.tricon.rcm.dto.GenericResponse;
import com.tricon.rcm.dto.RcmUserToDto;
import com.tricon.rcm.enums.UserRoleEnum;
import com.tricon.rcm.jpa.repository.RCMUserRepository;
import com.tricon.rcm.jpa.repository.RcmTeamRepo;
import com.tricon.rcm.security.JwtUser;
import com.tricon.rcm.util.MessageConstants;
import com.tricon.rcm.util.RoleUtil;

@Service
public class UserServiceImpl {

	@Autowired
	RCMUserRepository userRepo;

	@Autowired
	RcmCommonServiceImpl commonService;

	@Autowired
	RcmTeamRepo teamRepo;

	@Autowired
	@Qualifier("jwtUserDetailsService")
	private UserDetailsService userDetailsService;

	/**
	 * This method does update password of login user or admin
	 * 
	 * @param jwtUser
	 * @param password
	 * @return GenericResponse
	 */
	@Transactional(rollbackOn = Exception.class)
	public GenericResponse updatePassword(String password) throws Exception {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
		JwtUser jwtUser = (JwtUser) userDetails;
		String msg = "";
		RcmUser loginUser = userRepo.findByUserName(jwtUser.getUserName());
		if (loginUser != null) {
			msg = commonService.resetPassword(loginUser, loginUser, password);
		} else {
			msg = MessageConstants.USER_NOT_EXIST;
		}
		return new GenericResponse(HttpStatus.OK, msg, null);
	}

	/**
	 * This Method fetch users by giving role and team id
	 * 
	 * @param jwtUser
	 * @param role
	 * @return List of users
	 */

	public GenericResponse getUsersByRole(String role) throws Exception {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
		JwtUser jwtUser = (JwtUser) userDetails;
		RcmUser loginUser = userRepo.findByUserName(jwtUser.getUserName());
		List<RcmUserToDto> data = null;
		if (loginUser != null) {
			RcmTeam team = loginUser.getTeam();
			if (team != null) {
				UserRoleEnum teamName = UserRoleEnum.valueOf(team.getId());
				role = RoleUtil.generateRole(teamName, role);
				data = userRepo.findUsersByRole(role);
			}
		} else {
			return new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.USER_NOT_EXIST, null);
		}
		return new GenericResponse(HttpStatus.OK, "", data);
	}

}
