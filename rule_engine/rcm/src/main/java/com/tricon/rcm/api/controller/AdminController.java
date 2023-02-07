package com.tricon.rcm.api.controller;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tricon.rcm.dto.ClaimAssignmentDto;
import com.tricon.rcm.dto.FindUserDto;
import com.tricon.rcm.dto.GenericResponse;
import com.tricon.rcm.dto.PasswordResetDto;
import com.tricon.rcm.dto.RcmClaimDto;
import com.tricon.rcm.dto.RcmCompanyDto;
import com.tricon.rcm.dto.RcmEditOfficeDto;
import com.tricon.rcm.dto.RcmEditRolesDto;
import com.tricon.rcm.dto.RcmUserStatusDto;
import com.tricon.rcm.dto.RcmUserToDto;
import com.tricon.rcm.dto.UserRegistrationDto;
import com.tricon.rcm.security.JwtUser;
import com.tricon.rcm.service.impl.AdminServiceImpl;
import com.tricon.rcm.util.Constants;
import com.tricon.rcm.util.MessageConstants;

@RestController
@CrossOrigin
public class AdminController {

	private final Logger logger = LoggerFactory.getLogger(AdminController.class);

	@Autowired
	private AdminServiceImpl serviceImpl;
	
	@Autowired
	@Qualifier("jwtUserDetailsService")
	private UserDetailsService userDetailsService;

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationDto dto) {
		if (dto.getUserRole().stream().anyMatch(x->x.equals(Constants.SYSTEM))) {
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", null));
		}
		GenericResponse response = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
		JwtUser jwtUser = (JwtUser) userDetails;
		if(!jwtUser.isSmilePoint()) {
			return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", null));
		}
		try {
			response = serviceImpl.registerUser(dto);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(response);
	}

	@RequestMapping(value = "/resetpassword", method = RequestMethod.POST)
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> resetpasswordByAdmin(@RequestBody PasswordResetDto dto) {
		GenericResponse response = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
		JwtUser jwtUser = (JwtUser) userDetails;
		if(!jwtUser.isSmilePoint()) {
			return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", null));
		}
		if (dto.getPassword().trim().equals("") || dto.getUuid().trim().equals("")) {
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.EMPTY_RESOURCE, null));
		}
		try {
			response = serviceImpl.passwordUpdation(jwtUser,dto);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(response);
	}

	@RequestMapping(value = "/finduser", method = RequestMethod.POST)
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> findUserByEmail(@RequestBody FindUserDto dto) {
		GenericResponse response = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
		JwtUser jwtUser = (JwtUser) userDetails;
		if(!jwtUser.isSmilePoint()) {
			return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", null));
		}
		if (dto.getEmail().trim().equals("")) {
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.EMPTY_RESOURCE, null));
		}
		try {
			response = serviceImpl.findUserByEmail(dto);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(response);
	}

	@RequestMapping(value = "/getAllUsers/{companyName}/{pageNumber}", method = RequestMethod.GET)
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> getAllUsers(@PathVariable("companyName")String companyName,@PathVariable("pageNumber") int pageNumber) {
		GenericResponse response = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
		JwtUser jwtUser = (JwtUser) userDetails;
		if(!jwtUser.isSmilePoint()) {
			return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", null));
		}
		try {
			response = serviceImpl.getAllUsers(pageNumber,companyName);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(response);
	}

	@RequestMapping(value = "/resetstatus", method = RequestMethod.POST)
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> resetstatus(@RequestBody RcmUserStatusDto dto) {
		GenericResponse response = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
		JwtUser jwtUser = (JwtUser) userDetails;
		if(!jwtUser.isSmilePoint()) {
			return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", null));
		}
		if (dto.getUserActiveStatus().isEmpty()) {
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.EMPTY_RESOURCE, null));
		}
		if(dto.getUserActiveStatus().stream().anyMatch(x->x.getUserId().equals(jwtUser.getUuid())))
		{
			return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", null));
		}
		try {
			response = serviceImpl.resetUserStatus(jwtUser,dto);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(response);
	}
	
	@RequestMapping(value = "/finduserbydetail/{query}", method = RequestMethod.GET)
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> findUserByDetail(@PathVariable("query")String searchQuery) {
		GenericResponse response = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
		JwtUser jwtUser = (JwtUser) userDetails;
		if(!jwtUser.isSmilePoint()) {
			return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", null));
		}
		try {
			response = serviceImpl.findUserByDetail(searchQuery);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(response);
	}
	
	@RequestMapping(value = "/getOrganization", method = RequestMethod.GET)
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> getCompanyDetails() {
		GenericResponse response = null;
		try {
			response = serviceImpl.getCompanies();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}

	@RequestMapping(value = "addOffice", method = RequestMethod.POST)
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> addOffice(@RequestBody RcmCompanyDto dto) {
		if (dto.getName().trim().equals("") || dto.getCompanyUuid().trim().equals("")) {
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.EMPTY_RESOURCE, null));
		}
		GenericResponse response = null;
		try {
			response = serviceImpl.addNewOfficeByAdmin(dto);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(response);
	}

	@RequestMapping(value = "officeByCompany/{uuid}", method = RequestMethod.GET)
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> officesByUuid(@PathVariable("uuid") String uuid) {
		GenericResponse response = null;
		try {
			response = serviceImpl.getOfficesByCompanyUuid(uuid);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}

	@RequestMapping(value = "editOffice", method = RequestMethod.POST)
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> editOffice(@RequestBody RcmEditOfficeDto dto) {
		if (dto.getOfficeName().trim().equals("") || dto.getOfficeUuid().trim().equals("")) {
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.EMPTY_RESOURCE, null));
		}
		GenericResponse response = null;
		try {
			response = serviceImpl.editOfficeDetailsByAdmin(dto);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(response);
	}
	
	@RequestMapping(value = "editRole", method = RequestMethod.POST)
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> editRoles(@RequestBody RcmEditRolesDto dto) {
		if (dto.getUuid().trim().equals("")||dto.getRoles().isEmpty()) {
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.EMPTY_RESOURCE, null));
		}
		GenericResponse response = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
		JwtUser jwtUser = (JwtUser) userDetails;
		if(!jwtUser.isSmilePoint()) {
			return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", null));
		}
		try {
			response = serviceImpl.editRolesByAdmin(jwtUser,dto);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(response);
	}
	/**
	 * This Api fetches all users from Claim assignment table according to teamid
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "assignmentclaimUsers", method = RequestMethod.POST)
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> claimsFromAssignmentTable(@RequestBody RcmClaimDto dto) {
		if (dto.getUserUuid().trim().equals("")||dto.getTeamId()==null) {
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.EMPTY_RESOURCE, null));
		}
		List<RcmUserToDto> response = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
		JwtUser jwtUser = (JwtUser) userDetails;
		if(!jwtUser.isSmilePoint()) {
			return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", null));
		}
		try {
			response = serviceImpl.getUsersFromClaimAssignmentTable(dto, jwtUser.getCompany());
			if(response==null) {
				return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", null));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}
	
	@RequestMapping(value = "assignclaimToUser", method = RequestMethod.POST)
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> claimAssignmentToUser(@RequestBody ClaimAssignmentDto dto) {
		if (dto.getNewClaimUserUuid().trim().equals("")||dto.getOldClaimUserUuid().trim().equals("")) {
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.EMPTY_RESOURCE, null));
		}
		String response = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
		JwtUser jwtUser = (JwtUser) userDetails;
		if(!jwtUser.isSmilePoint()) {
			return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", null));
		}
		try {
			response = serviceImpl.claimAssignmentToUser(dto,jwtUser);
			if(response==null) {
				return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", null));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}
}
