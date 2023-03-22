package com.tricon.rcm.service.impl;

import java.sql.Timestamp;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tricon.rcm.db.entity.RcmCompany;
import com.tricon.rcm.db.entity.RcmUser;
import com.tricon.rcm.db.entity.RcmUserRoleHistory;
import com.tricon.rcm.dto.RcmOfficeDto;
import com.tricon.rcm.dto.customquery.ClientCustomDto;
import com.tricon.rcm.jpa.repository.RCMUserRepository;
import com.tricon.rcm.jpa.repository.RcmCompanyRepo;
import com.tricon.rcm.jpa.repository.RcmOfficeRepository;
import com.tricon.rcm.jpa.repository.RcmUserRoleHistoryRepo;
import com.tricon.rcm.util.Constants;
import com.tricon.rcm.util.EncrytedKeyUtil;
import com.tricon.rcm.util.MessageConstants;

@Service
public class RcmCommonServiceImpl {
	
	private final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

	@Autowired
	RcmCompanyRepo rcmCompanyRepo;
	
	@Autowired
	RcmOfficeRepository rcmOfficeRepository;
	
	@Autowired
	RCMUserRepository userRepo;
	
	@Autowired
	RcmOfficeRepository officeRepo;
	
	@Autowired
	RcmUserRoleHistoryRepo  userTempRepo;
	
	@Autowired
	RcmUtilServiceImpl utilService;
		
	
	public List<RcmOfficeDto> getAllOffices(){
		
	 RcmCompany	company =rcmCompanyRepo.findByName(Constants.COMPANY_NAME);
	 
	 return rcmOfficeRepository.findByCompany(company);
		
		
		
	}

	/**
	 * This Method provides common functionality for any role to reset password.
	 * @param RecmUser
	 * @param password
	 * @return Generic response
	 */
	public String resetPassword(RcmUser user,RcmUser updatedBy ,String password) {
		String msg = "";
		try {
			user.setPassword(EncrytedKeyUtil.encryptKey(password));
			user.setUpdatedBy(updatedBy);
			user.setLastPasswordResetDate(Timestamp.from(Instant.now()));
			user.setTempPassword("null");
			userRepo.save(user);
			msg = MessageConstants.PASSWORD_UPDATE;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			msg = MessageConstants.UPDATION_FAIL;
			return msg;
		}
		return msg;
	}
	
	
	public List<ClientCustomDto> findAllClients(){
		return rcmCompanyRepo.findAllClients();
	}
	
	/**
	 * Fetchs all offices of Login user's company
	 * @param companyUuid
	 * @return
	 */
	public List<RcmOfficeDto> getOfficesByUuid(String companyUuid) {
		RcmCompany company = rcmCompanyRepo.findByUuid(companyUuid);
		if (company != null) {
			List<RcmOfficeDto> office = officeRepo.findByCompany(company);
			return office;
		}
		return null;
	}
	
	public void dumpDataToRcmUserTemp(RcmUser user, List<String> roles) {
		 RcmUserRoleHistory tempUser = new RcmUserRoleHistory();
		tempUser.setClientName(user.getCompany().getName());
		tempUser.setUser(user);
		tempUser.setEmail(user.getEmail());
		tempUser.setFirstName(user.getFirstName());
		tempUser.setLastName(user.getLastName());
		tempUser.setCreatedDate(Timestamp.from(Instant.now()));
		tempUser.setTeamName(utilService.checkTeamNullOrNot(user.getTeam()) == -1 ? "-1" : user.getTeam().getName());
		tempUser.setRolesDetails(roles.stream().collect(Collectors.joining(",", "[", "]")));
		userTempRepo.save(tempUser);
	}
}
