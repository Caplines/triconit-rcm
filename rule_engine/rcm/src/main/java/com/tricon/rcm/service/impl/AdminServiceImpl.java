package com.tricon.rcm.service.impl;

import java.util.ArrayList;
import java.util.List;
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

import com.tricon.rcm.db.entity.RcmCompany;
import com.tricon.rcm.db.entity.RcmOffice;
import com.tricon.rcm.db.entity.RcmTeam;
import com.tricon.rcm.db.entity.RcmUser;
import com.tricon.rcm.db.entity.RcmUserRole;
import com.tricon.rcm.db.entity.RcmUserRolePk;
import com.tricon.rcm.dto.FindUserDto;
import com.tricon.rcm.dto.GenericResponse;
import com.tricon.rcm.dto.PasswordResetDto;
import com.tricon.rcm.dto.RcmCompanyDto;
import com.tricon.rcm.dto.RcmEditOfficeDto;
import com.tricon.rcm.dto.RcmEditRolesDto;
import com.tricon.rcm.dto.RcmOfficeDto;
import com.tricon.rcm.dto.RcmUserDto;
import com.tricon.rcm.dto.RcmUserPaginationDto;
import com.tricon.rcm.dto.RcmUserToDto;
import com.tricon.rcm.dto.ResetStatusDto;
import com.tricon.rcm.dto.UserRegistrationDto;
import com.tricon.rcm.dto.UserSearchDto;
import com.tricon.rcm.enums.RcmTeamEnum;
import com.tricon.rcm.jpa.repository.RCMUserRepository;
import com.tricon.rcm.jpa.repository.RcmCompanyRepo;
import com.tricon.rcm.jpa.repository.RcmOfficeRepository;
import com.tricon.rcm.jpa.repository.RcmTeamRepo;
import com.tricon.rcm.jpa.repository.RcmUserRoleRepo;
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

	/**
	 * This Method save Data of New Register User
	 * 
	 * @param dto
	 * @return Generic response
	 */
	@Transactional(rollbackOn = Exception.class)
	public GenericResponse registerUser(UserRegistrationDto dto) throws Exception {
		RcmOffice office = officeRepo.findByUuid(dto.getOfficeId());
		RcmUserRole roles = null;
		RcmUserRolePk pk = null;
		RcmUser user = null;
		if (office != null) {
			user = userRepo.findByUserNameOrEmail(dto.getUserName(), dto.getEmail());
			if (user == null) {
				RcmTeam team = teamRepo.findById(dto.getTeamId());
				RcmCompany company = rcmCompanyRepo.findByName(dto.getCompanyName());
				user = convertDtotoModel(dto);
				user.setOffice(office);
				user.setTeam(team);
				user.setCompany(company);
				user = userRepo.save(user);
				for (String role : dto.getUserRole()) {
					roles = new RcmUserRole();
					pk = new RcmUserRolePk();
					pk.setUuid(user.getUuid());
					roles.setId(pk);
					if (role.equals(Constants.ADMIN)) {
						roles.setRole(RcmTeamEnum.generateRole(0, role));
					} else {
						roles.setRole(RcmTeamEnum.generateRole(team.getId(), role));
					}
					userRole.save(roles);
				}

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
			data.setTeamNameId(user.getTeam().getId());
			return new GenericResponse(HttpStatus.OK, MessageConstants.USER_EXIST, data);
		}
		return new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.USER_NOT_EXIST, null);
	}

	/**
	 * This Method is used to fetch All user records .
	 * This Method can use only ADMIN and company is capline
	 * @return List of user Details
	 */
	public GenericResponse getAllUsers(int pageNumber) throws Exception {
		String companyUuid = "";
		RcmCompany company = rcmCompanyRepo.findByName(Constants.COMPANY_NAME);
		RcmUserPaginationDto paginationDto = new RcmUserPaginationDto();
		List<RcmUserPaginationDto> listOfUsers = new ArrayList<>();
		if (company != null) {
			companyUuid = company.getUuid();
			if (pageNumber == -1) { // without pagination if pageNumber<0
				List<RcmUserToDto> data = userRepo.getAllUser(companyUuid);
				return new GenericResponse(HttpStatus.OK, "", data);
			}
			Pageable paging = PageRequest.of(pageNumber, totalRecordsperPage, Sort.by("FullName").ascending());
			Page<RcmUserToDto> pageableList = userRepo.getAllUserByPagination(companyUuid, paging);
			if (pageableList != null && !pageableList.isEmpty()) {
				paginationDto.setData(pageableList.getContent());
				paginationDto.setPageNumber(pageableList.getNumber());
				paginationDto.setTotalElements(pageableList.getTotalElements());
				paginationDto.setPageSize(pageableList.getSize());
				listOfUsers.add(paginationDto);
				return new GenericResponse(HttpStatus.OK, "", listOfUsers);
			}
		}
		return new GenericResponse(HttpStatus.OK, "", null);
	}

	/**
	 * This Method enable/disable status based on user uuid.
	 *  This Method can use only ADMIN and cmpany is capline
	 * @param dto
	 * @param logInUser
	 */
	@Transactional(rollbackOn = Exception.class)
	public GenericResponse resetUserStatus(JwtUser jwtUser, ResetStatusDto dto) throws Exception {
		List<String> enables = dto.getEnable();
		List<String> disables = dto.getDisable();
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
	public GenericResponse getCompany() throws Exception {
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
				RcmCompanyDto dto = new RcmCompanyDto();
				dto.setName(loginUserCompany.getName());
				dto.setCompanyUuid(loginUserCompany.getUuid());
				return new GenericResponse(HttpStatus.OK, "", dto);
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
		RcmCompany company = rcmCompanyRepo.findByUuid(dto.getCompanyUuid());
		RcmOffice office = new RcmOffice();
		RcmOffice oldOffice = officeRepo.findByCompanyAndName(company, dto.getName());

		// if company uuid is same as capline or office is alreday exist then return
		if (oldOffice != null || company.getName().equals(Constants.COMPANY_NAME)) {
			return new GenericResponse(HttpStatus.BAD_REQUEST, "", null);
		}

		// if login user(ADMIN) is capline then login user can add other company new
		// office
		if (oldOffice == null && jwtUser.getCompany().getName().equals(Constants.COMPANY_NAME)) {
			office.setName(dto.getName());
			office.setCompany(company);
			officeRepo.save(office);
			return new GenericResponse(HttpStatus.OK, MessageConstants.RECORDS_UPDATE, null);
		} else {
			// if login user(ADMIN) is other than capline then login user can add own new
			// company offices
			if (oldOffice == null && jwtUser.getCompany().getName().equals(company.getName())) {
				office.setName(dto.getName());
				office.setCompany(company);
				officeRepo.save(office);
				return new GenericResponse(HttpStatus.OK, MessageConstants.RECORDS_UPDATE, null);
			}
		}
		return new GenericResponse(HttpStatus.BAD_REQUEST, "", null);
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
			RcmCompany company = rcmCompanyRepo.findByUuid(uuid);
			if (company != null) {
				List<RcmOfficeDto> office = officeRepo.findByCompany(company);
				return new GenericResponse(HttpStatus.OK, "", office);
			}
		}
		return new GenericResponse(HttpStatus.BAD_REQUEST, "", null);
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

		// if office id is capline then return
		if (office.getCompany().getName().equals(Constants.COMPANY_NAME)) {
			return new GenericResponse(HttpStatus.BAD_REQUEST, "", null);
		}

		// if login user(ADMIN) is capline then login user can edit other company office
		if (office != null && jwtUser.getCompany().getName().equals(Constants.COMPANY_NAME)) {
			office.setName(dto.getOfficeName());
			officeRepo.save(office);
			return new GenericResponse(HttpStatus.OK, MessageConstants.RECORDS_UPDATE, null);
		} else {
			// if login user(ADMIN) is other than capline then login user can edit own
			// company offices
			if (office != null && jwtUser.getCompany().getName().equals(office.getCompany().getName())) {
				office.setName(dto.getOfficeName());
				officeRepo.save(office);
				return new GenericResponse(HttpStatus.OK, MessageConstants.RECORDS_UPDATE, null);
			}
		}
		return new GenericResponse(HttpStatus.BAD_REQUEST, "", null);
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
		// if uuid is match from login user then return
		if (jwtUser.getUuid().equals(dto.getUuid())) {
			return new GenericResponse(HttpStatus.BAD_REQUEST, "", null);
		}

		// if user is not null and company is capline then admin can change user's roles of own company users and other company user's roles
		if (existingUser != null) {
			List<RcmUserRole> existingRoles = existingUser.getRoles().stream().collect(Collectors.toList());
			if (existingRoles != null) {
				// remove all existingRoles include ADMIN
				userRole.deleteAll(existingRoles);
			}

			// add new roles
			for (String role : dto.getRoles()) {
				rcmRole = new RcmUserRole();
				pk = new RcmUserRolePk();
				pk.setUuid(existingUser.getUuid());
				rcmRole.setId(pk);
				rcmRole.setRole(Constants.ROLE_PREFIX.concat(role));
				listOfRoles.add(rcmRole);
			}
			userRole.saveAll(listOfRoles);
			return new GenericResponse(HttpStatus.OK, MessageConstants.RECORDS_UPDATE, null);
		}
		return new GenericResponse(HttpStatus.BAD_REQUEST, "", null);
}
	}
