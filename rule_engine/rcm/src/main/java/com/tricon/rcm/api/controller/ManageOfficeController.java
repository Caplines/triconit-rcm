package com.tricon.rcm.api.controller;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tricon.rcm.dto.AssignOfficesToBillingUserDto;
import com.tricon.rcm.dto.GenericResponse;
import com.tricon.rcm.security.JwtUser;
import com.tricon.rcm.service.impl.ManageOfficeServiceImpl;
import com.tricon.rcm.util.MessageConstants;


@RestController
@CrossOrigin
public class ManageOfficeController {

	private final Logger logger = LoggerFactory.getLogger(ManageOfficeController.class);
	
	
	@Autowired
	@Qualifier("jwtUserDetailsService")
	private UserDetailsService userDetailsService;
	
	@Autowired
	ManageOfficeServiceImpl officeService;
	
	@RequestMapping(value = "assignOffice", method = RequestMethod.POST)
	@PreAuthorize("hasAnyRole('ADMIN','BILLING_TL')")
	public ResponseEntity<?> assignOfficesToBillingUser(@RequestBody AssignOfficesToBillingUserDto dto) {
		if (dto.getAssignOfficeDetails().isEmpty()) {
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.EMPTY_RESOURCE, null));
		}
		GenericResponse response = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
		JwtUser jwtUser = (JwtUser) userDetails;
		if (!jwtUser.isSmilePoint()) {
			return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", null));
		}
		try {
			response = officeService.assignOfficeByAdmin(dto,dto.getTeamId());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.ok(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}
}
