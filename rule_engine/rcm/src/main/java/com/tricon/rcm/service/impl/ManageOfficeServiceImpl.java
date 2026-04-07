package com.tricon.rcm.service.impl;

import java.util.HashSet;
import java.util.List;
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
	public GenericResponse assignOfficeByAdmin(AssignOfficesToBillingUserDto dto, RcmCompany logedIncompany, int teamId,
			JwtUser jwtUser) throws Exception {
		List<AssignUserOfficeDto> userOfficeData = dto.getAssignOfficeDetails();
		List<String> listOfUserId = userOfficeData.stream().map(x -> x.getUserId()).collect(Collectors.toList());
		List<String> listOfOfficeId = userOfficeData.stream().map(x -> x.getOfficeId()).collect(Collectors.toList());
		List<RcmUser> listOfUsers = userRepo.findByUuidIn(listOfUserId);

		if (listOfUsers != null && !listOfUsers.isEmpty()) {
			// Prevent assigning the same office to multiple users in one request
			Set<String> getDuplicateOffices = new HashSet<>(listOfOfficeId);
			if (getDuplicateOffices.size() != listOfOfficeId.size()) {
				return new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.DUPLICATE_OFFICE, null);
			}

			for (AssignUserOfficeDto userAndOfficeId : userOfficeData) {
				// Reuse the already-fetched user — no extra DB call
				RcmUser freshUserAssignment = listOfUsers.stream()
						.filter(x -> x.getUuid().equals(userAndOfficeId.getUserId()))
						.findFirst().get();
				RcmUserTeam teams = freshUserAssignment.getRcmTeams().stream()
						.filter(x -> x.getTeam().getId() == teamId).findAny().orElse(null);
				if (teams == null)
					return new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.TEAM_NOT_EXIT, null);

				// Single lookup: does this office already have an assignment for this team?
				UserAssignOffice assignUser = userAssignRepo.findByOfficeUuidAndTeamId(
						userAndOfficeId.getOfficeId(), dto.getTeamId());
				if (assignUser != null) {
					// Reassign existing record to the new user — no extra DB call for the user
					assignUser.setUser(freshUserAssignment);
					userAssignRepo.save(assignUser);
				} else {
					// Create a fresh assignment
					UserAssignOffice newAssignment = new UserAssignOffice();
					RcmOffice office = new RcmOffice();
					office.setUuid(userAndOfficeId.getOfficeId());
					newAssignment.setOffice(office);
					newAssignment.setUser(freshUserAssignment);
					newAssignment.setTeam(teams.getTeam());
					userAssignRepo.save(newAssignment);
				}
			}

			RcmUser user = userRepo.findByEmail(Constants.SYSTEM_USER_EMAIL);
			if (!userOfficeData.isEmpty()) {
				ruleEngineService.reAssignClaimToUserByOffices(logedIncompany, teamId, jwtUser, userOfficeData);
				ruleEngineService.assignedClaimsByTeamWithNoActiveInClaimAssigments(user, teamId, logedIncompany.getUuid());
			}
			return new GenericResponse(HttpStatus.OK, MessageConstants.RECORDS_UPDATE, null);
		}
		return new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.USER_NOT_EXIST, null);
	}
}
