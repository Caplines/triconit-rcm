package com.tricon.rcm.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tricon.rcm.db.entity.RcmOffice;
import com.tricon.rcm.db.entity.RcmUser;
import com.tricon.rcm.db.entity.UserAssignOffice;
import com.tricon.rcm.dto.AssignOfficesToBillingUserDto;
import com.tricon.rcm.dto.AssignUserOfficeDto;
import com.tricon.rcm.dto.GenericResponse;
import com.tricon.rcm.jpa.repository.RCMUserRepository;
import com.tricon.rcm.jpa.repository.UserAssignOfficeRepo;
import com.tricon.rcm.util.Constants;
import com.tricon.rcm.util.MessageConstants;

@Service
public class ManageOfficeServiceImpl {

	
	@Autowired
	RCMUserRepository userRepo;
	
	@Autowired
	UserAssignOfficeRepo userAssignRepo;

	/**
	 * This api does assign office to user with the help of teamId
	 * @param dto
	 * @param teamId
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackOn = Exception.class)
	public GenericResponse assignOfficeByAdmin(AssignOfficesToBillingUserDto dto, int teamId) throws Exception {
		List<AssignUserOfficeDto> userOfficeData = dto.getAssignOfficeDetails();
		List<String> listOfUserId = userOfficeData.stream().map(x -> x.getUserId()).collect(Collectors.toList());
		List<String> listOfOfficeId = userOfficeData.stream().map(x -> x.getOfficeId()).collect(Collectors.toList());
		List<RcmUser> listOfUsers = userRepo.findByUuidIn(listOfUserId);
		RcmOffice office = null;
		RcmUser newUser = null;
		UserAssignOffice user = null;

		if (!listOfUsers.isEmpty()) {
			for (RcmUser u : listOfUsers) {
				if (u.getTeam().getId() != teamId || !u.getCompany().getName().equals(Constants.COMPANY_NAME)) {
					return new GenericResponse(HttpStatus.BAD_REQUEST, "", null);
				}
			}
			// now we will check one office is assign to many user or not

			Set<String> getDuplicateOffices = new HashSet<>(listOfOfficeId);
			if (getDuplicateOffices.size() != listOfOfficeId.size()) {
				return new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.DUPLICATE_OFFICE, null);
			}

			for (AssignUserOfficeDto userAndOfficeId : userOfficeData) {
				office = new RcmOffice();
				UserAssignOffice assignUser = userAssignRepo.findByUserUuidAndOfficeUuidAndTeamId(
						userAndOfficeId.getUserId(), userAndOfficeId.getOfficeId(), teamId);
				newUser = listOfUsers.stream().filter(x -> x.getUuid().equals(userAndOfficeId.getUserId())).findFirst()
						.get();
				if (assignUser == null) {
					assignUser = userAssignRepo.findByOfficeUuidAndTeamId(userAndOfficeId.getOfficeId(), teamId);
					if (assignUser != null) {
						assignUser.setUser(userRepo.findByUuid(userAndOfficeId.getUserId()));
						userAssignRepo.save(assignUser);
					} else {
						user = new UserAssignOffice();
						office.setUuid(userAndOfficeId.getOfficeId());
						user.setOffice(office);
						user.setUser(newUser);
						user.setTeam(newUser.getTeam());
						userAssignRepo.save(user);
					}
				} else {
					assignUser = userAssignRepo.findByOfficeUuidAndTeamId(userAndOfficeId.getOfficeId(), teamId);
					if (assignUser != null) {
						assignUser.setUser(userRepo.findByUuid(userAndOfficeId.getUserId()));
						userAssignRepo.save(assignUser);
					} else {
						user = new UserAssignOffice();
						office.setUuid(userAndOfficeId.getOfficeId());
						user.setOffice(office);
						user.setUser(newUser);
						user.setTeam(newUser.getTeam());
						userAssignRepo.save(user);
					}
				}
			}
			return new GenericResponse(HttpStatus.OK, MessageConstants.RECORDS_UPDATE, null);
		}
		return new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.USER_NOT_EXIST, null);
	}
}
