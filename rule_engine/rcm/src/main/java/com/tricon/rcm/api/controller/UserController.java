package com.tricon.rcm.api.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import com.tricon.rcm.dto.GenericResponse;
import com.tricon.rcm.dto.PasswordResetDto;
import com.tricon.rcm.dto.RcmOfficeDto;
import com.tricon.rcm.dto.RcmTeamDto;
import com.tricon.rcm.dto.RcmUserToDto;
import com.tricon.rcm.dto.customquery.FreshClaimDataDto;
import com.tricon.rcm.enums.RcmRoleEnum;
import com.tricon.rcm.security.JwtUser;
import com.tricon.rcm.service.impl.RcmCommonServiceImpl;
import com.tricon.rcm.service.impl.UserServiceImpl;
import com.tricon.rcm.util.Constants;
import com.tricon.rcm.util.MessageConstants;

import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin
public class UserController {

	private final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserServiceImpl userService;
	
	@Autowired
	RcmCommonServiceImpl commonService;
	
	@Autowired
	@Qualifier("jwtUserDetailsService")
	private UserDetailsService userDetailsService;

	@ApiOperation(value = "Api For Update Password of Login User")
	@RequestMapping(value = "/updatepassword", method = RequestMethod.POST)
	public ResponseEntity<?> updatePasswordOfUserOrAdmin(@RequestBody PasswordResetDto dto) {
		GenericResponse response = null;
		if ((dto.getPassword()==null||dto.getPassword().trim().equals(""))||dto.getUuid()==null) {
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.PASSWORD_EMPTY, null));
		}
		try {
			response = userService.updatePassword(dto.getPassword());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(response);
	}

	@ApiOperation(value = "Api For Fetching users basis of Login User teamId and his Role TL", response = RcmUserToDto.class, responseContainer = "List")
	@RequestMapping(value = "/user/users_by_role/tl", method = RequestMethod.GET)
	public ResponseEntity<?> getUsersOfParticularTeam() {
		List<RcmUserToDto> response = null;
		try {
			response = userService.getUsersByRole(Constants.TEAMLEAD);
			if(response==null) {
				return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}
	
	@ApiOperation(value = "Api For Fetching offices basis of Login User clientName", response = RcmOfficeDto.class, responseContainer = "List")
	@RequestMapping(value = "getOffices", method = RequestMethod.GET)
	public ResponseEntity<?> officesByUuid() {
		List<RcmOfficeDto> response = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
		JwtUser jwtUser = (JwtUser) userDetails;
		try {
			response = commonService.getOfficesByUuid(jwtUser.getCompany().getUuid());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}
	
	@ApiOperation(value = "Api For Fetching TeamName Details basis of Login User teamId", response = RcmTeamDto.class, responseContainer = "List")
	@RequestMapping(value = "/user/other_teams", method = RequestMethod.GET)
	public ResponseEntity<?> teamNameByUserTeamId() {
		List<RcmTeamDto> response = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
		JwtUser jwtUser = (JwtUser) userDetails;
		try {
			response = userService.getTeamNameByOtherUserTeamId(jwtUser);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}
}
