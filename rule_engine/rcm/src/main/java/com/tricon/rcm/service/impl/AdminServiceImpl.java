package com.tricon.rcm.service.impl;

import java.sql.Timestamp;


import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.tricon.rcm.db.entity.RcmCompany;
import com.tricon.rcm.db.entity.RcmMappingTable;
import com.tricon.rcm.db.entity.RcmOffice;
import com.tricon.rcm.db.entity.RcmUser;
import com.tricon.rcm.db.entity.RcmUserCompany;
import com.tricon.rcm.db.entity.RcmUserRole;
import com.tricon.rcm.db.entity.RcmUserTeam;
//import com.tricon.rcm.db.entity.RcmUserTemp;
import com.tricon.rcm.dto.FindUserDto;
import com.tricon.rcm.dto.GenericResponse;
import com.tricon.rcm.dto.PasswordResetDto;
import com.tricon.rcm.dto.RcmClaimDto;
import com.tricon.rcm.dto.RcmClaimResponseDto;
import com.tricon.rcm.dto.RcmClientDto;
import com.tricon.rcm.dto.RcmClientGSheetDto;
import com.tricon.rcm.dto.RcmClientResponseDto;
import com.tricon.rcm.dto.RcmCompanyDto;
import com.tricon.rcm.dto.RcmEditOfficeDto;
import com.tricon.rcm.dto.RcmEditRolesDto;
import com.tricon.rcm.dto.RcmOfficeDto;
import com.tricon.rcm.dto.RcmOfficeResponse;
import com.tricon.rcm.dto.RcmRolesResponseDto;
import com.tricon.rcm.dto.RcmUserDto;
import com.tricon.rcm.dto.RcmUserPaginationDto;
import com.tricon.rcm.dto.RcmUserStatusDto;
import com.tricon.rcm.dto.RcmUserToDto;
import com.tricon.rcm.dto.UserRegistrationDto;
import com.tricon.rcm.dto.UserSearchDto;
import com.tricon.rcm.dto.customquery.RcmClaimAssignmentDto;
import com.tricon.rcm.dto.customquery.RcmCompanyWithGsheetDto;
import com.tricon.rcm.dto.customquery.RcmUserDetails;
import com.tricon.rcm.email.EmailUtil;
import com.tricon.rcm.enums.RcmRoleEnum;
import com.tricon.rcm.enums.RcmTeamEnum;
import com.tricon.rcm.jpa.repository.RCMUserRepository;
import com.tricon.rcm.jpa.repository.RcmClaimAssignmentRepo;
import com.tricon.rcm.jpa.repository.RcmClaimRepository;
import com.tricon.rcm.jpa.repository.RcmClaimStatusTypeRepo;
import com.tricon.rcm.jpa.repository.RcmCompanyRepo;
import com.tricon.rcm.jpa.repository.RcmMappingTableRepo;
import com.tricon.rcm.jpa.repository.RcmOfficeRepository;
import com.tricon.rcm.jpa.repository.RcmTeamRepo;
import com.tricon.rcm.jpa.repository.RcmUserCompanyRepo;
import com.tricon.rcm.jpa.repository.RcmUserRoleRepo;
import com.tricon.rcm.jpa.repository.RcmUserTeamRepo;
//import com.tricon.rcm.jpa.repository.RcmUserTempRepo;
import com.tricon.rcm.security.JwtUser;
import com.tricon.rcm.util.Constants;
import com.tricon.rcm.util.EncrytedKeyUtil;
import com.tricon.rcm.util.MessageConstants;

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
	
	
	@Value("${data.totalRecordperPage}")
	private int totalRecordsperPage;
	
	@Autowired
	EmailUtil emailUtil;
	
	@Autowired
	RcmClaimAssignmentRepo claimAssignmentRepo;
	
	@Autowired
	RcmClaimStatusTypeRepo rcmClaimStatusTypeRepo;
	
	@Autowired
	RcmClaimRepository rcmClaimRepository;
	
	@Autowired
	UserServiceImpl userService;
	
	@Autowired
	RcmMappingTableRepo mappingTableRepo;
	
	@Autowired
	RcmUtilServiceImpl utilService;
	
	@Autowired
	RcmUserCompanyRepo userCompanyRepo;
	
	@Autowired
	RcmUserTeamRepo userTeamRepo;
	

	/**
	 * This Method save Data of New Register User
	 * 
	 * @param dto
	 * @return Generic response
	 */
	@Transactional(rollbackOn = Exception.class)
	public GenericResponse registerUser(UserRegistrationDto dto, String isAdminRole, JwtUser jwtUser) throws Exception {
		RcmUser user = null;
		String responseMessage = "";

		if (isAdminRole.equals(Constants.ADMIN)) {
			if (dto.getUserRole().equals(Constants.SUPER_ADMIN))
				return null;

			if (!commonService.validateUserClients(jwtUser, dto.getCompanyUuid()))
				return null;
		}
		user = userRepo.findByEmail(dto.getEmail());
		if (user == null) {
			user = convertDtotoModel(dto);
			user = userRepo.save(user);
			if (user != null) {
				responseMessage = commonService.saveOrEditUser(user, dto.getUserRole(), dto.getCompanyUuid(),
						dto.getTeamId(),isAdminRole);

				if (responseMessage != null) {
					return new GenericResponse(HttpStatus.BAD_REQUEST, responseMessage, null);
				}

				// send email to register User
				String userEmail = user.getEmail();
				String emailSubject = "New Registration Confirmation";
				String emailText = "Hi " + user.getFirstName()
						+ ",\n\nThanks for signing up to RCM. You can now login into the RCM Account.\n\n"
						+ "Your Password is: " + dto.getPassword() + "\n\n" + "Thanks and Regards\nRCM Team";
				emailUtil.sendEmailForUserRegistration(userEmail, emailSubject, emailText);
			}

			return new GenericResponse(HttpStatus.OK, MessageConstants.USER_CREATION, null);
		}
		return new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.USER_EXIST_WITH_EMAIL, null);
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
		user.setCreatedBy(user);
		return user;
	}

	/**
	 * This Method finds user by email whose status is enable 
	 * This Method can use only ADMIN
	 * @param dto
	 * @return user details
	 */
	public RcmUserDto findUserByEmail(FindUserDto dto, String roleFromHeader, RcmCompany company)
			throws Exception {
		RcmUserDetails userDetails = null;
		RcmUser user = null;
		RcmUserDto data = null;
		if (roleFromHeader.equals(Constants.ADMIN)) {
			userDetails = userRepo.findUserByClientUuid(dto.getEmail(), company.getUuid());
			if (userDetails != null) {
				user = userRepo.findByUuid(userDetails.getUuid());
				if(user!=null) {
				data = new RcmUserDto();
				BeanUtils.copyProperties(userDetails, data);
				data.setActive(user.getActive());
				List<String> rolesData =user.getRoles().stream().map(x -> x.getRole()).collect(Collectors.toList());
				if(rolesData.stream().anyMatch(x->x.equals(Constants.ROLE_PREFIX +Constants.SUPER_ADMIN))) {
					return null;
				}
				for (String roles : rolesData) {
					RcmRolesResponseDto responseDto = new RcmRolesResponseDto();
					responseDto = RcmRoleEnum.getRoles(roles);
					data.setRoles(responseDto);
				}
					
				List<RcmUserCompany> clientName = userCompanyRepo.findByUserUuid(user.getUuid());
				if (clientName != null && !clientName.isEmpty()) {
					data.setClientName(
							clientName.stream().map(x -> x.getCompany().getName()).collect(Collectors.toList()));

				}
				List<RcmUserTeam> teamName = userTeamRepo.findByUserUuid(user.getUuid());
				if (teamName != null && !teamName.isEmpty()) {
					data.setTeamNameId(teamName.stream().map(x -> x.getTeam().getId()).collect(Collectors.toList()));
				}
				return data;
			  }
			}else {
				return null;
			}
		}

		if (roleFromHeader.equals(Constants.SUPER_ADMIN)) {
			user = userRepo.findByEmail(dto.getEmail());
			if(user!=null) {
			data = new RcmUserDto();
			BeanUtils.copyProperties(user, data);
			data.setFullName(String.join(" ", user.getFirstName(), user.getLastName()));
			data.setActive(user.getActive());
			List<String> rolesData =user.getRoles().stream().map(x -> x.getRole()).collect(Collectors.toList());			
			for (String roles : rolesData) {
				RcmRolesResponseDto responseDto = new RcmRolesResponseDto();
				responseDto = RcmRoleEnum.getRoles(roles);
				data.setRoles(responseDto);
			}
			List<RcmUserCompany> clientName = userCompanyRepo.findByUserUuid(user.getUuid());
			if (clientName != null && !clientName.isEmpty()) {
				data.setClientName(clientName.stream().map(x -> x.getCompany().getName()).collect(Collectors.toList()));

			}
			List<RcmUserTeam> teamName = userTeamRepo.findByUserUuid(user.getUuid());
			if (teamName != null && !teamName.isEmpty()) {
				data.setTeamNameId(teamName.stream().map(x -> x.getTeam().getId()).collect(Collectors.toList()));
			}
			return data;
		  }
			else {
				return null;
			}
		}
	     return null;
				
	}

	/**
	 * This Method is used to fetch All user records .
	 * This Method can use only ADMIN and company is capline
	 * @return List of user Details
	 */
	public List<RcmUserPaginationDto> getAllUsers(int pageNumber, String companyUuid, String isAdminOrSuperAdmin,
			JwtUser jwtUser) throws Exception {
		List<RcmUserPaginationDto> listOfUsers = null;
		RcmCompany company = null;
		List<RcmUserCompany> clientsData = userCompanyRepo.findByUserUuid(jwtUser.getUuid());
		List<String> clientUuid = clientsData.stream().map(x -> x.getCompany().getUuid()).collect(Collectors.toList());
		List<String>ignoreUsers=Arrays.asList(jwtUser.getEmail(),Constants.SYSTEM_USER_EMAIL);
		if (isAdminOrSuperAdmin.equals(Constants.ADMIN)) {

			if (companyUuid.equals(Constants.SHOW_ALL_COMPANY_USERS)) {
				Pageable paging = PageRequest.of(pageNumber, totalRecordsperPage, Sort.by("FullName").ascending());
				Page<RcmUserToDto> pageableList = userRepo.getAllUserByPagination(paging, ignoreUsers,
						clientUuid);
				listOfUsers = commonService.setUsersInPaginationDto(pageableList);
				return listOfUsers;
			}

			else {
				company = rcmCompanyRepo.findByUuid(companyUuid);
				if (company != null && clientUuid.stream().anyMatch(x->x.equals(companyUuid))) {
					Pageable paging = PageRequest.of(pageNumber, totalRecordsperPage, Sort.by("FullName").ascending());
					Page<RcmUserToDto> pageableList = userRepo.getUserByCompanyUuidWithPagination(company.getUuid(),
							paging, ignoreUsers);
					listOfUsers = commonService.setUsersInPaginationDto(pageableList);
					return listOfUsers;
				}
			}
		}

		if (isAdminOrSuperAdmin.equals(Constants.SUPER_ADMIN)) {
			if (companyUuid.equals(Constants.SHOW_ALL_COMPANY_USERS)) {
				Pageable paging = PageRequest.of(pageNumber, totalRecordsperPage, Sort.by("FullName").ascending());
				Page<RcmUserToDto> pageableList = userRepo.getAllUserBySuperAdmin(paging, ignoreUsers);
				listOfUsers = commonService.setUsersInPaginationDto(pageableList);
				return listOfUsers;
			}

			else {
				company = rcmCompanyRepo.findByUuid(companyUuid);
				if (company != null && clientUuid.stream().anyMatch(x->x.equals(companyUuid))) {
					Pageable paging = PageRequest.of(pageNumber, totalRecordsperPage, Sort.by("FullName").ascending());
					Page<RcmUserToDto> pageableList = userRepo.getUsersBySuperAdminUsingClicentUuid(paging,
							company.getUuid(),ignoreUsers);
					listOfUsers = commonService.setUsersInPaginationDto(pageableList);
					return listOfUsers;
				}
			}

		}

		return null;
	}

	/**
	 * This Method enable/disable status based on user uuid.
	 * This Method can use only ADMIN and company is capline
	 * @param dto
	 * @param logInUser
	 */
	@Transactional(rollbackOn = Exception.class)
	public GenericResponse resetUserStatus(JwtUser jwtUser, RcmUserStatusDto dto) throws Exception {
		List<String> enables = dto.getUserActiveStatus().stream().filter(x -> x.getStatus() == Constants.ENABLE)
				.map(x -> x.getUserId()).collect(Collectors.toList());
		List<String> disables = dto.getUserActiveStatus().stream().filter(x -> x.getStatus() == Constants.DISABLE)
				.map(x -> x.getUserId()).collect(Collectors.toList());
		RcmUser logInUser = userRepo.findByEmail(jwtUser.getUsername());
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
	 * Password can change only admin and company is capline
	 * 
	 * @param jwtUser
	 * @param dto
	 * @return GenericResponse
	 */
	@Transactional(rollbackOn = Exception.class)
	public GenericResponse passwordUpdation(JwtUser jwtUser, PasswordResetDto dto, String isAdminOrSuperAdmin,RcmCompany companyFromPartialHeader)
			throws Exception {
		String msg = "";
		RcmUser user = null, loginUser = null;
		RcmUserDetails userDetails = null;
		loginUser = userRepo.findByEmail(jwtUser.getUsername());
		if (loginUser != null) {
				user = userRepo.findByUuid(dto.getUuid());
				if(user==null) return new GenericResponse(HttpStatus.OK,MessageConstants.USER_NOT_EXIST, null);				
				if (user != null && isAdminOrSuperAdmin.equals(Constants.ADMIN) && !user.getEmail().equals(Constants.SYSTEM_USER_EMAIL)) {
					userDetails = userRepo.findUserByClientUuid(user.getEmail(),companyFromPartialHeader.getUuid());
					if(userDetails!=null) {
					msg = commonService.resetPassword(user, loginUser, dto.getPassword());}
					else {
						msg=MessageConstants.UPDATION_FAIL;
					}
				}
				if (user != null && isAdminOrSuperAdmin.equals(Constants.SUPER_ADMIN) && !user.getEmail().equals(Constants.SYSTEM_USER_EMAIL)) {
					msg = commonService.resetPassword(user, loginUser, dto.getPassword());
				}
		}
		return new GenericResponse(HttpStatus.OK, msg, null);
	}
    
	/**
	 * This Method find users basis of userName,email,firstName,lastName
	 * This Method can use only admin and company is capline
	 * @param searchQuery
	 * @return List of users
	 */
	public List<UserSearchDto> findUserByDetail(String searchQuery, String roleFromHeader, RcmCompany company)
			throws Exception {
		List<UserSearchDto> user = null;
		if (roleFromHeader.equals(Constants.ADMIN)) {
			user = userRepo.findByUserDetailsByAdmin(searchQuery, company.getUuid());
			return user;
		}
		if (roleFromHeader.equals(Constants.SUPER_ADMIN)) {
			user = userRepo.findByUserDetailsBySuperAdmin(searchQuery);
			return user;
		}
		return null;
	}

	/**
	 * Fetch All company data
	 * If login user is capline then fetch all companies  otherwise login user fetch only own company 
	 * This method is used only admin
	 * @return List of companies
	 */
	public List<RcmCompanyDto> getCompanies(String isAdminOrSuperAdmin, JwtUser jwtUser) throws Exception {

		List<RcmCompany> company = null;
		List<RcmCompanyDto> listOfCompany = new ArrayList<>();
		if (isAdminOrSuperAdmin.equals(Constants.SUPER_ADMIN)) {
			company = rcmCompanyRepo.findAll();
			if (company != null && !company.isEmpty()) {
				listOfCompany = company.stream().map(x -> new RcmCompanyDto(x.getName(), x.getUuid()))
						.collect(Collectors.toList());
			}
			return listOfCompany;
		} else {
			for (RcmCompany client : jwtUser.getCompanies()) {
				RcmCompanyDto dto = null;
				RcmCompany loginUserCompany = rcmCompanyRepo.findByUuid(client.getUuid());
				if (loginUserCompany != null) {					
					dto = new RcmCompanyDto();
					dto.setName(loginUserCompany.getName());
					dto.setCompanyUuid(loginUserCompany.getUuid());	
					listOfCompany.add(dto);	
				}			
			}
			return listOfCompany;
		}

	}

	/**
	 * New office will be added in basis of company name
	 * 
	 * @param dto
	 * @return Generic Response
	 */
	@Transactional(rollbackOn = Exception.class)
	public GenericResponse addNewOfficeByAdmin(RcmCompanyDto dto) throws Exception {
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		Object principal = authentication.getPrincipal();
//		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
//		JwtUser jwtUser = (JwtUser) userDetails;
		RcmOffice office = new RcmOffice();
		RcmCompany company = rcmCompanyRepo.findByUuid(dto.getCompanyUuid());
		RcmOfficeResponse officeResponse=null;

		if (company != null) {

			RcmOffice oldOffice = officeRepo.findByCompanyAndName(company, dto.getName());

			// if company uuid is same as capline or office is alreday exist then return
			if (oldOffice != null ) {
				return new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.OFFICE_EXIST, null);
			}
			
			if(company.getName().equals(Constants.COMPANY_NAME)){
				return new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null);
			}

			// if login user(ADMIN) is capline then login user can add other company new
			// office
			
			//find max id from office table
			int maxId=officeRepo.getMaxKeyFromOffice();
			
//			if (oldOffice == null && jwtUser.getCompany().getName().equals(Constants.COMPANY_NAME)) {
//				officeResponse=new RcmOfficeResponse();
//				office.setName(dto.getName());
//				office.setCompany(company);
//				office.setKey(maxId+1);
//				office=officeRepo.save(office);
//				officeResponse.setOfficeUuid(office.getUuid());
//				officeResponse.setId(maxId);
//				return new GenericResponse(HttpStatus.OK, MessageConstants.NEW_OFFICE_ADDED,officeResponse );
//			} 
			if (oldOffice == null) {
				officeResponse=new RcmOfficeResponse();
				office.setName(dto.getName());
				office.setCompany(company);
				office.setKey(maxId+1);
				office.setActive(true);
				office=officeRepo.save(office);
				officeResponse.setOfficeUuid(office.getUuid());
				officeResponse.setId(maxId);
				return new GenericResponse(HttpStatus.OK, MessageConstants.NEW_OFFICE_ADDED,officeResponse );
			} 
//			else {
//				// if login user(ADMIN) is other than capline then login user can add own new
//				// company offices
//				if (oldOffice == null && jwtUser.getCompany().getName().equals(company.getName())) {
//					officeResponse=new RcmOfficeResponse();
//					office.setName(dto.getName());
//					office.setCompany(company);
//					office.setKey(maxId+1);
//					office=officeRepo.save(office);
//					officeResponse.setOfficeUuid(office.getUuid());
//					officeResponse.setId(maxId);
//					return new GenericResponse(HttpStatus.OK, MessageConstants.NEW_OFFICE_ADDED,officeResponse);
//				}
//			}
		} 
		return new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.COMPANY_NOT_EXIST, null);
	}

	/**
	 * Fetch office data with the help of Company uuid
	 * This method can use only admin
	 * @param uuid
	 * @return List of RcmOfficeDto
	 */
	public List<RcmOfficeDto> getOfficesByCompanyUuid(String uuid) throws Exception {
		return commonService.getOfficesByUuid(uuid);
	}

	/**
	 * Update office details like office name
	 * @param dto
	 * @return GenericResponse
	 */
	@Transactional(rollbackOn = Exception.class)
	public GenericResponse editOfficeDetailsByAdmin(RcmEditOfficeDto dto) throws Exception {
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		Object principal = authentication.getPrincipal();
//		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
//		JwtUser jwtUser = (JwtUser) userDetails;
		RcmOffice office = officeRepo.findByUuid(dto.getOfficeUuid());
		RcmOffice checkExistOfficeName=officeRepo.findByNameAndCompanyUuid(dto.getOfficeName(),dto.getCompanyUuid());

		// if office id is capline then return
		if (office.getCompany().getName().equals(Constants.COMPANY_NAME)) {
			return new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null);
		}
       
		if(checkExistOfficeName!=null &&!checkExistOfficeName.getUuid().equals(dto.getOfficeUuid()))
		{
			return new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.NAME_EXIST, null);
		}
		// if login user(ADMIN) is capline then login user can edit other company office
//		if (office != null && jwtUser.getCompany().getName().equals(Constants.COMPANY_NAME)) {
//			office.setName(dto.getOfficeName());
//			office.setUpdatedDate(Timestamp.from(Instant.now()));
//			office=officeRepo.save(office);
//			return new GenericResponse(HttpStatus.OK, MessageConstants.RECORDS_UPDATE, office.getUuid());
//		}
		if (office != null) {
			office.setName(dto.getOfficeName());
			office.setUpdatedDate(Timestamp.from(Instant.now()));
			office=officeRepo.save(office);
			return new GenericResponse(HttpStatus.OK, MessageConstants.RECORDS_UPDATE, office.getUuid());
		}
//		else {
//			// if login user(ADMIN) is other than capline then login user can edit own
//			// company offices
//			if (office != null && jwtUser.getCompany().getName().equals(office.getCompany().getName())) {
//				office.setName(dto.getOfficeName());
//				office.setUpdatedDate(Timestamp.from(Instant.now()));
//				office=officeRepo.save(office);
//				return new GenericResponse(HttpStatus.OK, MessageConstants.RECORDS_UPDATE, office.getUuid());
//			}
//		}
		return new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null);
	}

	/**
	 * This api does edit roles of any user
	 * This method can use only admin of capline
	 * @param jwtUser
	 * @param dto
	 * @return GenericResponse
	 */

	@Transactional(rollbackOn = Exception.class)
	public GenericResponse editUsers(RcmEditRolesDto dto, String isAdminRole, JwtUser jwtUser) throws Exception {

		String responseMessage = "";
		// Validate ADMIN Functionality
		if (isAdminRole.equals(Constants.ADMIN)) {
			if (dto.getRole().equals(Constants.SUPER_ADMIN))
				return null;

			if (!commonService.validateUserClients(jwtUser, dto.getCompanyUuid()))
				return null;
		}
		// if uuid is match from login user then return
		if (jwtUser.getUuid().equals(dto.getUuid())) {
			return new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null);
		}

		RcmUser existingUser = userRepo.findByUuid(dto.getUuid());
		if (existingUser != null) {
			if(existingUser.getEmail().equals(Constants.SYSTEM_USER_EMAIL)) {
				return new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.USER_NOT_EXIST, null);
			}
			List<RcmUserRole> existingRoles = existingUser.getRoles().stream().collect(Collectors.toList());
			List<RcmUserCompany> existingClients=userCompanyRepo.findByUserUuid(existingUser.getUuid());
			List<RcmUserTeam> existingTeams=userTeamRepo.findByUserUuid(existingUser.getUuid());
			if (existingRoles != null) {
				// remove all existingRoles
				userRole.deleteAll(existingRoles);
				//remove all existing clients
				userCompanyRepo.deleteAll(existingClients);	
				//remove all existing teams
				userTeamRepo.deleteAll(existingTeams);
				
			}
			//edit user Personal details
			
			existingUser.setFirstName(dto.getFirstName());
			existingUser.setLastName(dto.getLastName());
			existingUser=userRepo.save(existingUser);
			
			//edit role,client and teams details
			responseMessage = commonService.saveOrEditUser(existingUser, dto.getRole(), dto.getCompanyUuid(),
					dto.getTeamId(),isAdminRole);

			if (responseMessage != null) {
				return new GenericResponse(HttpStatus.BAD_REQUEST, responseMessage, null);
			}
			return new GenericResponse(HttpStatus.OK, MessageConstants.RECORDS_UPDATE, null);
		}
		return new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.USER_NOT_EXIST, null);
	}

//	public List<RcmUserToDto> getUsersFromClaimAssignmentTable(RcmClaimDto dto,RcmCompany company) throws Exception {
//		// first we will check any claim is assign or not in this uuid
//		// if yes then fetch all billing users data will show in popup window
//
//	int existingUsersClaimCounts = claimAssignmentRepo.findExistingUsersAssignClaimCounts(dto.getUserUuid());
//		if (existingUsersClaimCounts >= 1) {
//			List<RcmUserToDto> rcmUser = userService.getUsersByTeamId(dto.getTeamId(), company);
//			if (rcmUser != null) {
//				rcmUser.removeIf(x -> x.getUuid().equals(dto.getUserUuid()));
//				return rcmUser;
//			} 
//		}
//		return null;
//	}

//	@Transactional(rollbackOn = Exception.class)
//	public String claimAssignmentToUser(ClaimAssignmentDto dto, JwtUser jwtUser) throws Exception {
//		List<RcmClaimAssignment> oldClaimUserData = claimAssignmentRepo
//				.findExistingUserClaimsWithPendingState(dto.getOldClaimUserUuid());
//		RcmUser assigneByUser = null, assigneToUser = null;
//		RcmClaimAssignment assignment = null;
//		RcmClaimStatusType claimStatusType = null;
//		/*
//		if (oldClaimUserData != null && !oldClaimUserData.isEmpty()) {
//
//			assigneToUser = userRepo.findByUuid(dto.getNewClaimUserUuid());
//			if (assigneToUser != null && assigneToUser.getTeam().getId() == RcmTeamEnum.BILLING.getId()) {
//				assigneByUser = userRepo.findByEmail(jwtUser.getEmail());
//
//				for (RcmClaimAssignment assign : oldClaimUserData) {
//					assignment = new RcmClaimAssignment();
//					claimStatusType = assign.getRcmClaimStatus();
//					// change comment and active status of oldClaim user whose claim is pending
//					claimAssignmentRepo.updateClaimUserStatusAndComment(MessageConstants.CLAIM_REMOVE_MESSAGE,
//							assigneByUser, false, dto.getOldClaimUserUuid(), assign.getClaims().getClaimUuid());
//					// now insert old user claims will assign to new user whose claim is pending
//					
//					//if assignTo user already with same claimId then his status will be
//					RcmClaimAssignment checkExistingClaimEntryStatus=claimAssignmentRepo.findByAssignedToUuidAndClaimsClaimUuidAndActive(assigneToUser.getUuid(),assign.getClaims().getClaimUuid(),true);
//					if(checkExistingClaimEntryStatus!=null) {
//					claimAssignmentRepo.updateClaimUserStatusAndComment(MessageConstants.CLAIM_REASSIGN_MESSAGE,
//							assigneByUser, false, checkExistingClaimEntryStatus.getAssignedTo().getUuid(), checkExistingClaimEntryStatus.getClaims().getClaimUuid());
//					}
//					
//					assignment = ClaimUtil.createAssginmentData(assignment, assigneByUser, assigneToUser, null,
//							assign.getClaims(), MessageConstants.CLAIM_REASSIGN_MESSAGE, claimStatusType);
//					claimAssignmentRepo.save(assignment);
//				}
//				return MessageConstants.CLAIM_REASSIGN_SUCCESS_MESSAGE;
//			}
//		}
//		*/
//		return MessageConstants.SOMETHING_WENT_WRONG;
//	}
	
	@Transactional(rollbackOn = Exception.class)
	public String addClient(RcmClientDto dto,String isSuperAdmin) throws Exception {
		RcmCompany company = rcmCompanyRepo.findByName(dto.getClientName());
		RcmMappingTable mappingTable = null;
		if (company == null) {
			company = new RcmCompany();
			company.setName(dto.getClientName());
			company = rcmCompanyRepo.save(company);
			commonService.syncClientsWithSuperAdmin(company, isSuperAdmin);
			for (RcmClientGSheetDto data : dto.getHeader()) {
				mappingTable = new RcmMappingTable();
				mappingTable.setGoogleSheetId(data.getGoogle_sheet_id());
				mappingTable.setGoogleSheetSubId(data.getGoogle_sheet_sub_id());
				mappingTable.setGoogleSheetSubName(data.getGoogle_sheet_sub_name());
				mappingTable.setName(data.getName());
				mappingTable.setCompany(company);
				mappingTableRepo.save(mappingTable);
			}

			// insert constant record of RCM Database in rcm_mapping_table
			mappingTable = new RcmMappingTable();
			mappingTable.setGoogleSheetId(Constants.MAPPING_TABLE_GOOGLE_SHEET_ID);
			mappingTable.setGoogleSheetSubId(Constants.MAPPING_TABLE_GOOGLE_SHEET_SUB_ID);
			mappingTable.setGoogleSheetSubName(Constants.MAPPING_TABLE_GOOGLE_SHEET_SUB_NAME);
			mappingTable.setName(Constants.MAPPING_TABLE_NAME_RCM_DATABASE);
			mappingTable.setCompany(company);
			mappingTableRepo.save(mappingTable);

			return company.getUuid();
		} else
			return MessageConstants.COMPANY_EXIST;
	}

	@Transactional(rollbackOn = Exception.class)
	public String editClient(RcmClientDto dto) throws Exception {
		RcmCompany company = rcmCompanyRepo.findByUuid(dto.getCompanyUuid());
		if (company == null || company.getName().equals(Constants.COMPANY_NAME)) {
			return MessageConstants.SOMETHING_WENT_WRONG;
		}
		// check company name exist or not
		RcmCompany checkcompanyNameExistOrNot = rcmCompanyRepo.findByName(dto.getClientName());
		if (checkcompanyNameExistOrNot != null && !checkcompanyNameExistOrNot.getUuid().equals(dto.getCompanyUuid()))
			return MessageConstants.NAME_EXIST;
		company.setName(dto.getClientName());
		company.setUpdatedDate(Timestamp.from(Instant.now()));
		company = rcmCompanyRepo.save(company);
		for (RcmClientGSheetDto data : dto.getHeader()) {
			RcmMappingTable mappingTable = mappingTableRepo.findByNameAndCompany(data.getName(), company);
			if (mappingTable != null) {
				mappingTable.setGoogleSheetId(data.getGoogle_sheet_id());
				mappingTable.setGoogleSheetSubId(data.getGoogle_sheet_sub_id());
				mappingTable.setGoogleSheetSubName(data.getGoogle_sheet_sub_name());
				mappingTableRepo.save(mappingTable);
			} 
		}
		return company.getUuid();
	}

	public List<RcmClientResponseDto> getClientWithGoogleSheetData() throws Exception{
		List<RcmCompany> company = null;
		List<RcmClientResponseDto> data = null;
		RcmClientResponseDto dto = null;
		company = rcmCompanyRepo.findAll();
		if (company != null && !company.isEmpty()) {
			data = new ArrayList<>();
			for (RcmCompany cmp : company) {
				dto = new RcmClientResponseDto();
				List<RcmCompanyWithGsheetDto> gSheetData = mappingTableRepo.getDataFromRcmMapping(cmp.getUuid(),
						Constants.MAPPING_TABLE_NAME_TFS);
				dto.setClientName(cmp.getName());
				dto.setCompanyUuid(cmp.getUuid());
				dto.setHeader(gSheetData);
				data.add(dto);
			}
			data.removeIf(x->x.getClientName().equals(Constants.COMPANY_NAME));
			return data;
		}
		return null;
	}

	public RcmClaimResponseDto checkExistingUserClaimStatusActiveOrNot(String userUuid) throws Exception {
		int existingUsersClaimCounts = claimAssignmentRepo.findExistingUserAssignClaimCountsAndActiveStatus(userUuid);
		if (existingUsersClaimCounts >= 1) {
			return new RcmClaimResponseDto(MessageConstants.ROLE_CAN_NOT_BE_CHANGE, Constants.DISABLE);
		}
		return new RcmClaimResponseDto("", Constants.ENABLE);
	}
}
