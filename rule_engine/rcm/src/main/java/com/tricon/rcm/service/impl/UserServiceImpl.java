package com.tricon.rcm.service.impl;

import java.util.ArrayList;
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

import com.tricon.rcm.db.entity.RcmCompany;
import com.tricon.rcm.db.entity.RcmTeam;
import com.tricon.rcm.db.entity.RcmUser;
import com.tricon.rcm.dto.GenericResponse;
import com.tricon.rcm.dto.RcmRoleDto;
import com.tricon.rcm.dto.RcmTeamDto;
import com.tricon.rcm.dto.RcmUserToDto;
import com.tricon.rcm.enums.RcmTeamEnum;
import com.tricon.rcm.jpa.repository.RCMUserRepository;
import com.tricon.rcm.jpa.repository.RcmCompanyRepo;
import com.tricon.rcm.jpa.repository.RcmTeamRepo;
import com.tricon.rcm.security.JwtUser;
import com.tricon.rcm.util.Constants;
import com.tricon.rcm.util.MessageConstants;

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
	
	@Autowired
	RcmCompanyRepo rcmCompanyRepo;
	
	@Autowired
	MasterServiceImpl masterService;

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
		RcmUser loginUser = userRepo.findByEmail(jwtUser.getUsername());
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

	public List<RcmUserToDto> getUsersByRole(String role) throws Exception {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
		JwtUser jwtUser = (JwtUser) userDetails;
		List<RcmUserToDto> data = null;
		if (jwtUser.getTeamId() == -1) {
			return null;
		}
		data = userRepo.findUsersByRole(RcmTeamEnum.generateRole(jwtUser.getTeamId(), role),
				jwtUser.getCompany().getUuid());
		data.removeIf(x -> x.getUuid().equals(jwtUser.getUuid()));
		return data;

	}

	public List<RcmUserToDto> getUsersByTeamId(int teamId,RcmCompany company) throws Exception {
		    List<RcmUserToDto> data = null;
			teamId=RcmTeamEnum.validateTeamId(teamId);
			if (teamId != 0) {
				data = userRepo.findUsersByTeamId(teamId, company.getUuid());
				return data;
			}
		
		return null;
	}

	/**
	 * This api fetches all teamName of loginUser's teamId only exclude loginUser TeamId
	 * @param jwtUser
	 * @return
	 */
	public List<RcmTeamDto> getTeamNameByOtherUserTeamId(JwtUser jwtUser) {
		int teamId = RcmTeamEnum.validateTeamId(jwtUser.getTeamId());
		if (teamId != 0) {
			List<RcmTeamDto> teamName = masterService.getTeams(jwtUser.getCompany().getName());
			teamName.removeIf(x -> x.getTeamId() == jwtUser.getTeamId());
			return teamName;
		}
		return null;
	}

	public List<RcmRoleDto> getRolesByUserEmail(String userEmail)throws Exception {
		RcmUser user = userRepo.findByEmail(userEmail);
		List<RcmRoleDto> roles = null;
		if (user != null && !user.getEmail().equals(Constants.SYSTEM_USER_EMAIL)) {
			roles = masterService.getRoles(user.getCompany().getName());
			roles.removeIf(x->x.getRoleId().equals(Constants.ADMIN));
			return roles;
		}
		return null;
	}

}
