package com.tricon.rcm.service.impl;

import java.util.HashSet;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tricon.rcm.db.entity.RcmCompany;
import com.tricon.rcm.db.entity.RcmOffice;
import com.tricon.rcm.db.entity.RcmUser;
import com.tricon.rcm.db.entity.RcmUserTeam;
import com.tricon.rcm.db.entity.UserAssignOffice;
import com.tricon.rcm.dto.AssignOfficesToBillingUserDto;
import com.tricon.rcm.dto.AssignUserOfficeDto;
import com.tricon.rcm.dto.GenericResponse;
import com.tricon.rcm.enums.RcmTeamEnum;
import com.tricon.rcm.jpa.repository.RCMUserRepository;
import com.tricon.rcm.jpa.repository.UserAssignOfficeRepo;
import com.tricon.rcm.security.JwtUser;
import com.tricon.rcm.util.Constants;
import com.tricon.rcm.util.MessageConstants;

@Service
public class ManageOfficeServiceImpl {

	
	@Autowired
	RCMUserRepository userRepo;
	
	@Autowired
	UserAssignOfficeRepo userAssignRepo;
	
	@Autowired
	RuleEngineService ruleEngineService;

	/**
	 * This api does assign office to user with the help of teamId
	 * @param dto
	 * @param teamId
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackOn = Exception.class)
	public GenericResponse assignOfficeByAdmin(AssignOfficesToBillingUserDto dto,RcmCompany logedIncompany,int teamId,
			JwtUser jwtUser) throws Exception {
		List<AssignUserOfficeDto> userOfficeData = dto.getAssignOfficeDetails();
		List<String> listOfUserId = userOfficeData.stream().map(x -> x.getUserId()).collect(Collectors.toList());
		List<String> listOfOfficeId = userOfficeData.stream().map(x -> x.getOfficeId()).collect(Collectors.toList());
		List<RcmUser> listOfUsers = userRepo.findByUuidIn(listOfUserId);
		RcmOffice office = null;
		RcmUser freshUserAssignment = null;
		UserAssignOffice userAssignedOffice = null;
		

		if (listOfUsers!=null && !listOfUsers.isEmpty()) {
//			for (RcmUser u : listOfUsers) {
//				if (u.getTeam().getId() != dto.getTeamId() || !u.getCompany().getUuid().equals(logedIncompany.getUuid())) {
//					return new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null);
//				}
//			}
			// now we will check one office is assign to many user or not

			Set<String> getDuplicateOffices = new HashSet<>(listOfOfficeId);
			if (getDuplicateOffices.size() != listOfOfficeId.size()) {
				return new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.DUPLICATE_OFFICE, null);
			}

			for (AssignUserOfficeDto userAndOfficeId : userOfficeData) {
				office = new RcmOffice();
				UserAssignOffice assignUser = userAssignRepo.findByUserUuidAndOfficeUuidAndTeamId(
						userAndOfficeId.getUserId(), userAndOfficeId.getOfficeId(),dto.getTeamId());
				freshUserAssignment = listOfUsers.stream().filter(x -> x.getUuid().equals(userAndOfficeId.getUserId())).findFirst()
						.get();
				RcmUserTeam teams=freshUserAssignment.getRcmTeams().stream().filter(x->x.getTeam().getId()==teamId).findAny().orElse(null);
				if(teams==null) return new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.TEAM_NOT_EXIT, null);
				if (assignUser == null) {
					assignUser = userAssignRepo.findByOfficeUuidAndTeamId(userAndOfficeId.getOfficeId(), dto.getTeamId());
					if (assignUser != null) {
						assignUser.setUser(userRepo.findByUuid(userAndOfficeId.getUserId()));
						userAssignRepo.save(assignUser);
					} else {
						userAssignedOffice = new UserAssignOffice();
						office.setUuid(userAndOfficeId.getOfficeId());
						userAssignedOffice.setOffice(office);
						userAssignedOffice.setUser(freshUserAssignment);
						userAssignedOffice.setTeam(teams.getTeam());
						userAssignRepo.save(userAssignedOffice);
					}
				} else {
					assignUser = userAssignRepo.findByOfficeUuidAndTeamId(userAndOfficeId.getOfficeId(),dto.getTeamId());
					if (assignUser != null) {
						assignUser.setUser(userRepo.findByUuid(userAndOfficeId.getUserId()));
						userAssignRepo.save(assignUser);
					} else {
						userAssignedOffice = new UserAssignOffice();
						office.setUuid(userAndOfficeId.getOfficeId());
						userAssignedOffice.setOffice(office);
						userAssignedOffice.setUser(freshUserAssignment);
						userAssignedOffice.setTeam(teams.getTeam());
						userAssignRepo.save(userAssignedOffice);
					}
				}
			}
			RcmUser user= userRepo.findByEmail(Constants.SYSTEM_USER_EMAIL);
			ruleEngineService.reAssignClaimToUserByOffices( logedIncompany, teamId, jwtUser);  
			//ruleEngineService.assignedUnsAssignedClaimsByTeam(logedIncompany.getUuid(),user,teamId);
			ruleEngineService.assignedClaimsByTeamWithNoActiveInClaimAssigments(user, teamId, logedIncompany.getUuid());
			return new GenericResponse(HttpStatus.OK, MessageConstants.RECORDS_UPDATE, null);
		}
		return new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.USER_NOT_EXIST, null);
	}
}
