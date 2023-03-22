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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.tricon.rcm.db.entity.RcmClaimAssignment;
import com.tricon.rcm.db.entity.RcmClaimStatusType;
import com.tricon.rcm.db.entity.RcmClaims;
import com.tricon.rcm.db.entity.RcmCompany;
import com.tricon.rcm.db.entity.RcmMappingTable;
import com.tricon.rcm.db.entity.RcmOffice;
import com.tricon.rcm.db.entity.RcmTeam;
import com.tricon.rcm.db.entity.RcmUser;
import com.tricon.rcm.db.entity.RcmUserRole;
import com.tricon.rcm.db.entity.RcmUserRolePk;
import com.tricon.rcm.db.entity.RcmUserTemp;
import com.tricon.rcm.dto.ClaimAssignmentDto;
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
import com.tricon.rcm.jpa.repository.RcmUserRoleRepo;
import com.tricon.rcm.jpa.repository.RcmUserTempRepo;
import com.tricon.rcm.jpa.repository.UserAssignOfficeRepo;
import com.tricon.rcm.security.JwtUser;
import com.tricon.rcm.util.ClaimUtil;
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
	
	@Autowired
	UserAssignOfficeRepo userAssignRepo;
	
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
	RcmUserTempRepo  userTempRepo;

	/**
	 * This Method save Data of New Register User
	 * 
	 * @param dto
	 * @return Generic response
	 */
	@Transactional(rollbackOn = Exception.class)
	public GenericResponse registerUser(UserRegistrationDto dto) throws Exception {
		RcmUserRole roles = null;
		RcmUserRolePk pk = null;
		RcmUser user = null;
//		UserAssignOffice userAssignOffice = null;
		user = userRepo.findByEmail(dto.getEmail());
		if (user == null) {
			RcmCompany company = rcmCompanyRepo.findByName(dto.getCompanyName());
			RcmTeam team = teamRepo.findById(dto.getTeamId());
			user = convertDtotoModel(dto);
			if (team == null) {
				
				//FOR SINGLE ADMIN ROLE whose TEAM IS NULL
				if (dto.getUserRole().stream()
						.anyMatch(x -> x.equals(Constants.ADMIN) && dto.getUserRole().size() == 1)) {} 	
				
				//FOR SINGLE UPLOAD_CLAIMS ROLE whose TEAM IS NULL			
				else if (dto.getUserRole().stream()
						.anyMatch(x -> x.equals(Constants.UPLOAD_CLAIMS) && dto.getUserRole().size() == 1)) {}
				
				//FOR SINGLE ACCOUNT_MANAGER ROLE whose TEAM IS NULL	
				else if (dto.getUserRole().stream()
						.anyMatch(x -> x.equals(Constants.ACCOUNT_MANAGER)&& dto.getUserRole().size() == 1)) {}
				
				//FOR ADMIN ROLE WITH UPLOAD_CLAIMS AND ACCOUNT_MANAGER ROLE whose TEAM IS NULL
				else if (dto.getUserRole().size() == 3) {
					for (String r : dto.getUserRole()) {
						if (r.equals(Constants.UPLOAD_CLAIMS) || r.equals(Constants.ADMIN) || r.equals(Constants.ACCOUNT_MANAGER))
							continue;
						else
							return new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.TEAM_MANDATORY, null);
					}
				} else 
					return new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.TEAM_MANDATORY, null);				
			} else {
				//ADMIN ROLE CAN NOT ASSOCIATED WITH OTHER ROLES
				if(dto.getUserRole().stream()
						.anyMatch(x -> x.equals(Constants.ADMIN) && dto.getUserRole().size()>1)){
					return new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.ADMIN_NOT_ASSOCIATED_WITH_ROLES, null);
				}
				//FOR SINGLE ADMIN ROLE TEAM IS NOT REQUIRED 
				else if (dto.getUserRole().stream()
						.anyMatch(x -> x.equals(Constants.ADMIN) && dto.getUserRole().size() == 1 && team!=null)) {
					return new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.TEAM_NOT_REQUIRED, null);
				} 
				//FOR SINGLE UPLOAD_CLAIMS ROLE TEAM IS NOT REQUIRED 	
				else if (dto.getUserRole().stream()
						.anyMatch(x -> x.equals(Constants.UPLOAD_CLAIMS) && dto.getUserRole().size() == 1 && team!=null)) {
					//return new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.TEAM_NOT_REQUIRED, null);
				}
				
				//FOR SINGLE ACCOUNT_MANAGER ROLE TEAM IS NOT REQUIRED 	
				else if (dto.getUserRole().stream()
						.anyMatch(x -> x.equals(Constants.ACCOUNT_MANAGER) && dto.getUserRole().size() == 1 && team!=null)) {
					//return new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.TEAM_NOT_REQUIRED, null);
				}
				else
				user.setTeam(team);
			}
			if (company == null) {
				return new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.COMPANY_NOT_EXIST,null);
			} else {
				user.setCompany(company);
			}
			user = userRepo.save(user);

			// if role is TL then we will assign TL+ASSO to registerUser
			if (dto.getUserRole().stream().anyMatch(x -> x.equals(Constants.TEAMLEAD))) {
				roles = new RcmUserRole();
				pk = new RcmUserRolePk();
				pk.setUuid(user.getUuid());
				roles.setId(pk);
				roles.setRole(RcmTeamEnum.generateRole(team.getId(), Constants.ASSOCIATE));
				userRole.save(roles);
			}

			for (String role : dto.getUserRole()) {
				roles = new RcmUserRole();
				pk = new RcmUserRolePk();
				pk.setUuid(user.getUuid());
				roles.setId(pk);
				if (role.equals(Constants.ADMIN)) {
					roles.setRole(RcmTeamEnum.generateRole(0, role)); // If role is admin then by default teamId will
																		// consider 0
				} 
				else if (role.equals(Constants.UPLOAD_CLAIMS)) {
					roles.setRole(RcmTeamEnum.generateRole(0, role)); // If role is upload_claims then by default teamId will
																		// consider 0
				}
				else if (role.equals(Constants.ACCOUNT_MANAGER)) {
					roles.setRole(RcmTeamEnum.generateRole(0, role)); // If role is ACCOUNT_MANAGER then by default teamId will
																		// consider 0
				}
				else {
					roles.setRole(RcmTeamEnum.generateRole(team.getId(), role));
				}
				userRole.save(roles);
			}

			// save user data into user_assign_office table
//				if (user.getCompany().getName().equals(Constants.COMPANY_NAME) && dto.getUserRole().stream().anyMatch(x->x.equals(RcmRoleEnum.ASSO.getName())||x.equals(RcmRoleEnum.TL.getName()))) {
//
//					// check office is already exist or not in given team id
//					if (user.getOffice()!=null) {
//					userAssignOffice = userAssignRepo.findByOfficeUuidAndTeamId(user.getOffice().getUuid(),user.getTeam().getId());
//
//					if (userAssignOffice==null) {
//						userAssignOffice=new UserAssignOffice();
//						userAssignOffice.setUser(user);
//						userAssignOffice.setOffice(user.getOffice());
//						userAssignOffice.setTeam(user.getTeam());
//						userAssignRepo.save(userAssignOffice);
//					 }
//					}
//				}

			// send email to register User
			if (user != null) {
				String userEmail = user.getEmail();
				String emailSubject = "New Registration Confirmation";
				String emailText = "Hi " + user.getFirstName()
						+ ",\n\nThanks for signing up to RCM. You can now login into the RCM Account.\n\n"
						+ "Your Password is: " + dto.getPassword() + "\n\n" + "Thanks and Regards\nRCM Team";
				emailUtil.sendEmailForUserRegistration(userEmail, emailSubject, emailText);
			}
			
			//dump new userData into rcm_user_temp table
			
            if(user!=null) {
            	RcmUserTemp tempUser=new RcmUserTemp();
            	tempUser.setClientName(user.getCompany().getName());
            	tempUser.setUser(user);
            	tempUser.setEmail(user.getEmail());    
            	tempUser.setFirstName(user.getFirstName());            	
            	tempUser.setLastName(user.getLastName());
            	tempUser.setCreatedDate(Timestamp.from(Instant.now()));
            	tempUser.setTeamName(user.getTeam().getNameId()); 	
            	tempUser.setRolesDetails(dto.getUserRole().stream().collect(Collectors.joining(",", "[", "]")));
            	userTempRepo.save(tempUser);            
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
	public GenericResponse findUserByEmail(FindUserDto dto) throws Exception {
		RcmUser user = userRepo.findByEmail(dto.getEmail());
		if (user != null && !user.getEmail().equals(Constants.SYSTEM_USER_EMAIL)) {
			RcmUserDto data = new RcmUserDto();
			BeanUtils.copyProperties(user, data);
			data.setFullName(String.join(" ", user.getFirstName(), user.getLastName()));
			data.setClientName(user.getCompany().getName());
			data.setTeamNameId(utilService.checkTeamNullOrNot(user.getTeam()));
//			List<String> rolesData = user.getRoles().stream().map(x -> x.getRole()).collect(Collectors.toList());
//			List<RcmRolesResponseDto> rolesByTeamEnum = RcmTeamEnum
//					.getRolesByTeamId(utilService.checkTeamNullOrNot(user.getTeam()));
//			List<RcmRolesResponseDto> rolesResponse = new ArrayList<>();
//			for (String roles : rolesData) {
//				RcmRolesResponseDto responseDto = new RcmRolesResponseDto();
//				for (RcmRolesResponseDto respDto : rolesByTeamEnum) {
//					if (respDto.getFullRoleName().equals(roles)) {
//						responseDto.setRoleId(respDto.getRoleId());
//						responseDto.setRoleName(respDto.getRoleName());
//						responseDto.setFullRoleName(respDto.getFullRoleName());
//					}
//					// if UPLOAD_CLAIMS Roles Not match with teamId then we will add explicity
//					if (roles.equals(Constants.ROLE_PREFIX + Constants.UPLOAD_CLAIMS)) {
//						responseDto.setRoleId(RcmRoleEnum.UPLOAD_CLAIMS.getName());
//						responseDto.setRoleName(RcmRoleEnum.UPLOAD_CLAIMS.getFullName());
//						responseDto.setFullRoleName(roles);
//					}
			// if ACCOUNT_MANAGER Roles Not match with teamId then we will add explicity
//			        if (roles.equals(Constants.ROLE_PREFIX + Constants.ACCOUNT_MANAGER)) {
//				        responseDto.setRoleId(RcmRoleEnum.ACCOUNT_MANAGER.getName());
//				        responseDto.setRoleName(RcmRoleEnum.ACCOUNT_MANAGER.getFullName());
//				        responseDto.setFullRoleName(roles);
//			        }
			// if ADMIN Roles Not match with teamId then we will add explicity
//	        if (roles.equals(Constants.ROLE_PREFIX + Constants.ADMIN)) {
//		        responseDto.setRoleId(RcmRoleEnum.ADMIN.getName());
//		        responseDto.setRoleName(RcmRoleEnum.ADMIN.getFullName());
//		        responseDto.setFullRoleName(roles);
//	        }
//				}
//				rolesResponse.add(responseDto);
//			}
//			data.setRoles(rolesResponse);
			Object [] rolesData=user.getRoles().stream().map(x->x.getRole().split("_", 4)).toArray();
			List<String>roles=new ArrayList<>();
			for (Object o : rolesData) {
				String s[] = (String[])o;
				roles.add(s[s.length-1]);
			}
			data.setRoles(roles);		
			return new GenericResponse(HttpStatus.OK, MessageConstants.USER_EXIST, data);
		}
		return new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.USER_NOT_EXIST, null);
	}

	/**
	 * This Method is used to fetch All user records .
	 * This Method can use only ADMIN and company is capline
	 * @return List of user Details
	 */
	public GenericResponse getAllUsers(int pageNumber, String cmpny) throws Exception {
		RcmUserPaginationDto paginationDto = new RcmUserPaginationDto();
		List<RcmUserPaginationDto> listOfUsers = new ArrayList<>();
		RcmCompany company = null;
		String companyUuid = "";
		String ignoreUser=Constants.SYSTEM_USER_EMAIL;
		// get all users without company name
		if (cmpny.equals(Constants.SHOW_ALL_COMPANY_USERS)) {
			if (pageNumber == -1) { // without pagination if pageNumber<0
				List<RcmUserToDto> data = userRepo.getAllUser(ignoreUser);
				return new GenericResponse(HttpStatus.OK, "", data);
			}
			Pageable paging = PageRequest.of(pageNumber, totalRecordsperPage, Sort.by("FullName").ascending());
			Page<RcmUserToDto> pageableList = userRepo.getAllUserByPagination(paging,ignoreUser);
			if (pageableList != null && !pageableList.isEmpty()) {
				paginationDto.setData(pageableList.getContent());
				paginationDto.setPageNumber(pageableList.getNumber());
				paginationDto.setTotalElements(pageableList.getTotalElements());
				paginationDto.setPageSize(pageableList.getSize());
				paginationDto.setHasNextElement(pageableList.hasNext());
				listOfUsers.add(paginationDto);
				return new GenericResponse(HttpStatus.OK, "", listOfUsers);
			}
		} else {
			// get all users by company name
			company = rcmCompanyRepo.findByName(cmpny);
			if (company != null) {
				companyUuid = company.getUuid();
				if (pageNumber == -1) { // without pagination if pageNumber<0
					List<RcmUserToDto> data = userRepo.getAllUserByCompanyUuid(companyUuid,ignoreUser);
					return new GenericResponse(HttpStatus.OK, "", data);
				}
				Pageable paging = PageRequest.of(pageNumber, totalRecordsperPage, Sort.by("FullName").ascending());
				Page<RcmUserToDto> pageableList = userRepo.getAllUserByCompanyUuidWithPagination(companyUuid, paging,ignoreUser);
				if (pageableList != null && !pageableList.isEmpty()) {
					paginationDto.setData(pageableList.getContent());
					paginationDto.setPageNumber(pageableList.getNumber());
					paginationDto.setTotalElements(pageableList.getTotalElements());
					paginationDto.setPageSize(pageableList.getSize());
					paginationDto.setHasNextElement(pageableList.hasNext());
					listOfUsers.add(paginationDto);
					return new GenericResponse(HttpStatus.OK, "", listOfUsers);
				}
			} else
				return new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.COMPANY_NOT_EXIST, null);
		}
		return new GenericResponse(HttpStatus.OK, "", null);
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
	public GenericResponse passwordUpdation(JwtUser jwtUser, PasswordResetDto dto) throws Exception {
		String msg = "";
		RcmUser user = null, loginUser = null;
		loginUser = userRepo.findByEmail(jwtUser.getUsername());
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
    
	/**
	 * This Method find users basis of userName,email,firstName,lastName
	 * This Method can use only admin and company is capline
	 * @param searchQuery
	 * @return List of users
	 */
	public GenericResponse findUserByDetail(String searchQuery) throws Exception {
		List<UserSearchDto> user = userRepo.findByUserDetails(searchQuery);
		if (user != null && !user.isEmpty()) {
			return new GenericResponse(HttpStatus.OK, MessageConstants.USER_EXIST, user);
		}
		return new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.USER_NOT_EXIST, null);
	}

	/**
	 * Fetch All company data
	 * If login user is capline then fetch all companies  otherwise login user fetch only own company 
	 * This method is used only admin
	 * @return List of companies
	 */
	public GenericResponse getCompanies() throws Exception {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
		JwtUser jwtUser = (JwtUser) userDetails;
		List<RcmCompany> company = null;
		List<RcmCompanyDto> listOfCompany = null;
		// if login user is capline then return all companies details like company name and company uuid otherwise login user
		// company details
		if (jwtUser.getCompany().getName().equals(Constants.COMPANY_NAME)) {
			company = rcmCompanyRepo.findAll();
			if (company != null && !company.isEmpty()) {
				listOfCompany = company.stream().map(x -> new RcmCompanyDto(x.getName(), x.getUuid()))
						.collect(Collectors.toList());
			}
			return new GenericResponse(HttpStatus.OK, "", listOfCompany);
		} else {
			RcmCompany loginUserCompany = rcmCompanyRepo.findByUuid(jwtUser.getCompany().getUuid());
			if (loginUserCompany != null) {
				listOfCompany=new ArrayList<>();
			RcmCompanyDto dto = new RcmCompanyDto();
			dto.setName(loginUserCompany.getName());
			dto.setCompanyUuid(loginUserCompany.getUuid());
			listOfCompany.add(dto);
			return new GenericResponse(HttpStatus.OK, "", listOfCompany);
			}
		}
		return new GenericResponse(HttpStatus.OK, "", null);
	}

	/**
	 * New office will be added in basis of company name
	 * 
	 * @param dto
	 * @return Generic Response
	 */
	@Transactional(rollbackOn = Exception.class)
	public GenericResponse addNewOfficeByAdmin(RcmCompanyDto dto) throws Exception {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
		JwtUser jwtUser = (JwtUser) userDetails;
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
			
			if (oldOffice == null && jwtUser.getCompany().getName().equals(Constants.COMPANY_NAME)) {
				officeResponse=new RcmOfficeResponse();
				office.setName(dto.getName());
				office.setCompany(company);
				office.setKey(maxId+1);
				office=officeRepo.save(office);
				officeResponse.setOfficeUuid(office.getUuid());
				officeResponse.setId(maxId);
				return new GenericResponse(HttpStatus.OK, MessageConstants.NEW_OFFICE_ADDED,officeResponse );
			} else {
				// if login user(ADMIN) is other than capline then login user can add own new
				// company offices
				if (oldOffice == null && jwtUser.getCompany().getName().equals(company.getName())) {
					officeResponse=new RcmOfficeResponse();
					office.setName(dto.getName());
					office.setCompany(company);
					office.setKey(maxId+1);
					office=officeRepo.save(office);
					officeResponse.setOfficeUuid(office.getUuid());
					officeResponse.setId(maxId);
					return new GenericResponse(HttpStatus.OK, MessageConstants.NEW_OFFICE_ADDED,officeResponse);
				}
			}
		} 
		return new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.COMPANY_NOT_EXIST, null);
	}

	/**
	 * Fetch office data with the help of Company uuid
	 * This method can use only admin
	 * @param uuid
	 * @return List of RcmOfficeDto
	 */
	public GenericResponse getOfficesByCompanyUuid(String uuid) throws Exception {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
		JwtUser jwtUser = (JwtUser) userDetails;
		//if login user is capline then fetch all companies offices otherwise login user can fetch own company offices by uuid
		if (jwtUser.getCompany().getUuid().equals(uuid)
				|| jwtUser.getCompany().getName().equals(Constants.COMPANY_NAME)) {
			return new GenericResponse(HttpStatus.OK, "",commonService.getOfficesByUuid(uuid));
		}
		return new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null);
	}

	/**
	 * Update office details like office name
	 * @param dto
	 * @return GenericResponse
	 */
	@Transactional(rollbackOn = Exception.class)
	public GenericResponse editOfficeDetailsByAdmin(RcmEditOfficeDto dto) throws Exception {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
		JwtUser jwtUser = (JwtUser) userDetails;
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
		if (office != null && jwtUser.getCompany().getName().equals(Constants.COMPANY_NAME)) {
			office.setName(dto.getOfficeName());
			office.setUpdatedDate(Timestamp.from(Instant.now()));
			office=officeRepo.save(office);
			return new GenericResponse(HttpStatus.OK, MessageConstants.RECORDS_UPDATE, office.getUuid());
		} else {
			// if login user(ADMIN) is other than capline then login user can edit own
			// company offices
			if (office != null && jwtUser.getCompany().getName().equals(office.getCompany().getName())) {
				office.setName(dto.getOfficeName());
				office.setUpdatedDate(Timestamp.from(Instant.now()));
				office=officeRepo.save(office);
				return new GenericResponse(HttpStatus.OK, MessageConstants.RECORDS_UPDATE, office.getUuid());
			}
		}
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
	public GenericResponse editRolesByAdmin(JwtUser jwtUser, RcmEditRolesDto dto) throws Exception {
		RcmUser existingUser = userRepo.findByUuid(dto.getUuid());
		RcmUserRole rcmRole = null;
		RcmUserRolePk pk = null;
		List<RcmUserRole> listOfRoles = new ArrayList<>();
		boolean isAccountManager=false;
		boolean isUploadClaims=false;
		
		// if uuid is match from login user then return
		if (jwtUser.getUuid().equals(dto.getUuid())) {
			return new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null);
		}

		// if user is not null and company is capline then admin can change user's roles of own company users and other company user's roles
		if (existingUser != null) {
			int teamId=utilService.checkTeamNullOrNot(existingUser.getTeam());
			
			//IF teamId=-1 then this role can't be exist in roles array
			if (teamId == -1 && dto.getRoles().stream()
					.anyMatch(x -> x.equals(Constants.TEAMLEAD) || x.equals(Constants.ASSOCIATE)
							|| x.equals(Constants.CLIENT_MANAGER) || x.equals(Constants.CLIENT_VIEW_ONLY))) {
				return new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null);
			}
			
			//IF teamId=9 then this role can't be exist in roles array
			if (teamId == 9 && dto.getRoles().stream()
					.anyMatch(x -> x.equals(Constants.TEAMLEAD) || x.equals(Constants.ASSOCIATE)
							|| x.equals(Constants.UPLOAD_CLAIMS) || x.equals(Constants.ACCOUNT_MANAGER))) {
				return new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null);
			}
			
			//ADMIN Role can not associate with other roles
			if (dto.getRoles().stream().anyMatch(x -> x.equals(Constants.ADMIN) && teamId!=-1)) {
				return new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.ADMIN_NOT_ASSOCIATED_WITH_ROLES, null);
			}
			
			//UPLOAD Claim Role can associate with other roles but TL or ASSO is required for this role
			if (dto.getRoles().stream().anyMatch(x -> x.equals(Constants.UPLOAD_CLAIMS) && dto.getRoles().size()==1) && teamId!=-1) {
				return new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.TL_ASSO_REQUIRED, null);
			}
			
			//ACCOUNT_MANAGER Role can associate with other roles but TL or ASSO is required for this role
			if (dto.getRoles().stream().anyMatch(x -> x.equals(Constants.ACCOUNT_MANAGER)&& dto.getRoles().size()==1) && teamId!=-1) {
				return new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.TL_ASSO_REQUIRED, null);
			}
			
			if (dto.getRoles().size() == 2 && teamId!=-1) {
				for (String r : dto.getRoles()) {
					if (r.equals(Constants.UPLOAD_CLAIMS))
						isUploadClaims = true;
					if (r.equals(Constants.ACCOUNT_MANAGER))
						isAccountManager = true;
				}
				if (isAccountManager && isUploadClaims)
					return new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.TL_ASSO_REQUIRED, null);
			}
			
			List<RcmUserRole> existingRoles = existingUser.getRoles().stream().collect(Collectors.toList());
			if (existingRoles != null) {
				// remove all existingRoles include ADMIN
				userRole.deleteAll(existingRoles);
			}
			
			// if role is TL then we will assign TL+ASSO to editUser
			if (dto.getRoles().stream().anyMatch(x -> x.equals(Constants.TEAMLEAD) && teamId!=-1)) {
				rcmRole = new RcmUserRole();
				pk = new RcmUserRolePk();
				pk.setUuid(existingUser.getUuid());
				rcmRole.setId(pk);
				rcmRole.setRole(RcmTeamEnum.generateRole(teamId,Constants.ASSOCIATE));
				userRole.save(rcmRole);
			}
			// add new roles
			for (String role : dto.getRoles()) {
				rcmRole = new RcmUserRole();
				pk = new RcmUserRolePk();
				pk.setUuid(existingUser.getUuid());
				rcmRole.setId(pk);
				if (role.equals(Constants.UPLOAD_CLAIMS)) {
					rcmRole.setRole(RcmTeamEnum.generateRole(0, role)); 
				}
				else if (role.equals(Constants.ACCOUNT_MANAGER)) {
					rcmRole.setRole(RcmTeamEnum.generateRole(0, role));
				}
				else if (role.equals(Constants.ADMIN)) {
					rcmRole.setRole(RcmTeamEnum.generateRole(0, role));
				}
				else {
					rcmRole.setRole(RcmTeamEnum.generateRole(teamId, role));
				}
				listOfRoles.add(rcmRole);
			}
			userRole.saveAll(listOfRoles);
			return new GenericResponse(HttpStatus.OK, MessageConstants.RECORDS_UPDATE, null);
		}
		return new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null);
   }

	public List<RcmUserToDto> getUsersFromClaimAssignmentTable(RcmClaimDto dto,RcmCompany company) throws Exception {
		// first we will check any claim is assign or not in this uuid
		// if yes then fetch all billing users data will show in popup window

	int existingUsersClaimCounts = claimAssignmentRepo.findExistingUsersAssignClaimCounts(dto.getUserUuid());
		if (existingUsersClaimCounts >= 1) {
			List<RcmUserToDto> rcmUser = userService.getUsersByTeamId(dto.getTeamId(), company);
			if (rcmUser != null) {
				rcmUser.removeIf(x -> x.getUuid().equals(dto.getUserUuid()));
				return rcmUser;
			} 
		}
		return null;
	}

	@Transactional(rollbackOn = Exception.class)
	public String claimAssignmentToUser(ClaimAssignmentDto dto, JwtUser jwtUser) throws Exception {
		List<RcmClaimAssignment> oldClaimUserData = claimAssignmentRepo
				.findExistingUserClaimsWithPendingState(dto.getOldClaimUserUuid());
		RcmUser assigneByUser = null, assigneToUser = null;
		RcmClaimAssignment assignment = null;
		RcmClaimStatusType claimStatusType = null;
		if (oldClaimUserData != null && !oldClaimUserData.isEmpty()) {

			assigneToUser = userRepo.findByUuid(dto.getNewClaimUserUuid());
			if (assigneToUser != null && assigneToUser.getTeam().getId() == RcmTeamEnum.BILLING.getId()) {
				assigneByUser = userRepo.findByEmail(jwtUser.getEmail());

				for (RcmClaimAssignment assign : oldClaimUserData) {
					assignment = new RcmClaimAssignment();
					claimStatusType = assign.getRcmClaimStatus();
					// change comment and active status of oldClaim user whose claim is pending
					claimAssignmentRepo.updateClaimUserStatusAndComment(MessageConstants.CLAIM_REMOVE_MESSAGE,
							assigneByUser, false, dto.getOldClaimUserUuid(), assign.getClaims().getClaimUuid());
					// now insert old user claims will assign to new user whose claim is pending
					
					//if assignTo user already with same claimId then his status will be
					RcmClaimAssignment checkExistingClaimEntryStatus=claimAssignmentRepo.findByAssignedToUuidAndClaimsClaimUuidAndActive(assigneToUser.getUuid(),assign.getClaims().getClaimUuid(),true);
					if(checkExistingClaimEntryStatus!=null) {
					claimAssignmentRepo.updateClaimUserStatusAndComment(MessageConstants.CLAIM_REASSIGN_MESSAGE,
							assigneByUser, false, checkExistingClaimEntryStatus.getAssignedTo().getUuid(), checkExistingClaimEntryStatus.getClaims().getClaimUuid()); }
					
					assignment = ClaimUtil.createAssginmentData(assignment, assigneByUser, assigneToUser, null,
							assign.getClaims(), MessageConstants.CLAIM_REASSIGN_MESSAGE, claimStatusType);
					claimAssignmentRepo.save(assignment);
				}
				return MessageConstants.CLAIM_REASSIGN_SUCCESS_MESSAGE;
			}
		}
		return MessageConstants.SOMETHING_WENT_WRONG;
	}
	
	@Transactional(rollbackOn = Exception.class)
	public String addClient(RcmClientDto dto) throws Exception {
		RcmCompany company = rcmCompanyRepo.findByName(dto.getClientName());
		RcmMappingTable mappingTable = null;
		if (company == null) {
			company = new RcmCompany();
			company.setName(dto.getClientName());
			company = rcmCompanyRepo.save(company);
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
			} else
				return MessageConstants.DB_INSERSTION_ERROR;
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
