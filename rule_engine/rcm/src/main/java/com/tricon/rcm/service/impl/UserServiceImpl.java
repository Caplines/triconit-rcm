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
import com.tricon.rcm.dto.PartialHeader;
import com.tricon.rcm.dto.RcmRoleDto;
import com.tricon.rcm.dto.RcmTeamDto;
import com.tricon.rcm.dto.RcmUserToDto;
import com.tricon.rcm.dto.customquery.RcmCompanyWithGsheetDto;
import com.tricon.rcm.dto.customquery.TreatmentPlanLinkDto;
import com.tricon.rcm.enums.RcmTeamEnum;
import com.tricon.rcm.jpa.repository.RCMUserRepository;
import com.tricon.rcm.jpa.repository.RcmCompanyRepo;
import com.tricon.rcm.jpa.repository.RcmMappingTableRepo;
import com.tricon.rcm.jpa.repository.RcmTeamRepo;
import com.tricon.rcm.security.JwtUser;
import com.tricon.rcm.util.Constants;
import com.tricon.rcm.util.EncrytedKeyUtil;
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

	@Autowired
	RcmMappingTableRepo mappingTableRepo;
	
	/**
	 * This method does update password of login user or admin
	 * 
	 * @param jwtUser
	 * @param password
	 * @return GenericResponse
	 */
	@Transactional(rollbackOn = Exception.class)
	public GenericResponse updatePassword(String password, String newPassword, JwtUser jwtUser) throws Exception {
		String msg = "";
		RcmUser loginUser = userRepo.findByEmail(jwtUser.getUsername());
		if (loginUser != null) {
			String encodedPassword = loginUser.getPassword();
			if (EncrytedKeyUtil.verifyPassword(password, encodedPassword)) {
				msg = commonService.resetPassword(loginUser, loginUser, newPassword);
				if (msg.equals(MessageConstants.UPDATION_FAIL)) {
					msg = MessageConstants.UPDATION_FAIL;
				} else
					return new GenericResponse(HttpStatus.OK, msg, null);
			} else
				msg = MessageConstants.PASSWORD_NOT_MATCH;
		}
		return new GenericResponse(HttpStatus.BAD_REQUEST, msg, null);
	}

	/**
	 * This Method fetch users by giving role and team id
	 * 
	 * @param jwtUser
	 * @param role
	 * @return List of users
	 */

	public List<RcmUserToDto> getUsersByRole(String role,PartialHeader partialHeader,boolean remove) throws Exception {
		List<RcmUserToDto> data = null;
		data = userRepo.findUsersByRoleAndTeamId(RcmTeamEnum.generateRole(partialHeader.getTeamId(), role),
				partialHeader.getCompany().getUuid(),partialHeader.getTeamId());
		if (remove)data.removeIf(x -> x.getUuid().equals(partialHeader.getJwtUser().getUuid()));
		return data;

	}

	public List<RcmUserToDto> getUsersByTeamIdAndCompany(int teamId,RcmCompany company) throws Exception {
		    List<RcmUserToDto> data = null;
			teamId=RcmTeamEnum.validateTeamId(teamId);
			if (teamId != 0) {
		data = userRepo.findUsersByTeamIdAndCompanyId(teamId, company.getUuid());
				return data;
			}
		
		return null;
	}

	/**
	 * This api fetches all teamName of loginUser's teamId only exclude loginUser TeamId
	 * @param partialHeader
	 * @return
	 */
	
	public List<RcmTeamDto> getTeamNameByOtherUserTeamId(PartialHeader partialHeader) {
		int teamId = RcmTeamEnum.validateTeamId(partialHeader.getTeamId());
		if (teamId != 0) {
			List<RcmTeamDto> teams =null;
           //in case if internal audit only pass Billing Team
           /*if (partialHeader.getTeamId()==RcmTeamEnum.INTERNAL_AUDIT.getId() && handleInterAudit) {
        	   teams= masterService.getTeamById(RcmTeamEnum.BILLING.getId());
				
			}else {
				teams = masterService.getTeams();
				teams.removeIf(x -> x.getTeamId() == partialHeader.getTeamId());
			}*/
       	teams = masterService.getTeams();
		teams.removeIf(x -> x.getTeamId() == partialHeader.getTeamId());
			return teams;
		}
		
		return null;
	}
   /*
	public List<RcmRoleDto> getRolesByUserEmail(String userEmail)throws Exception {
		RcmUser user = userRepo.findByEmail(userEmail);
		List<RcmRoleDto> roles = null;
		if (user != null && !user.getEmail().equals(Constants.SYSTEM_USER_EMAIL)) {
			roles = masterService.getRoles(user.getCompany().getName());
			return roles;
		}
		return null;
	}
    */

	public List<TreatmentPlanLinkDto> getTreatmentPlanLinkData(String claimUuid) throws Exception {
		if (claimUuid != null && !claimUuid.trim().equals("")) {
			List<TreatmentPlanLinkDto> data = userRepo.findTreatmentPlanLinkData(claimUuid);
			return data;
		}
		return null;
	}

	public int getIssueClaimsCounts(RcmCompany company,boolean isArchive) throws Exception {
		int counts = 0;
		if (company != null) {
			counts = userRepo.findCountsOfIssueClaims(company.getUuid(),isArchive);
			if (counts > 0)
				return counts;
			else
				return 0;
		}
		return 0;
	}

	public List<RcmCompanyWithGsheetDto> getGoogleSheetLink(RcmCompany company) throws Exception {
		if (company != null) {
			return mappingTableRepo.findGsheetLinkByClientuuid(company.getUuid());
		}
		return null;
	}
}
