package com.tricon.rcm.api.controller;

import java.util.List;

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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tricon.rcm.db.entity.RcmProcessLogger;
import com.tricon.rcm.dto.AssignOfficesToBillingUserDto;
import com.tricon.rcm.dto.GenericResponse;
import com.tricon.rcm.dto.PartialHeader;
import com.tricon.rcm.dto.RcmUserToDto;
import com.tricon.rcm.dto.UsersByTeamsAndCompanyDto;
import com.tricon.rcm.enums.RcmTeamEnum;
import com.tricon.rcm.security.JwtUser;
import com.tricon.rcm.service.impl.ManageOfficeServiceImpl;
import com.tricon.rcm.service.impl.RcmProcessLoggerImpl;
import com.tricon.rcm.service.impl.UserServiceImpl;
import com.tricon.rcm.util.MessageConstants;


@RestController
@CrossOrigin
public class ManageOfficeController extends BaseHeaderController {

	private final Logger logger = LoggerFactory.getLogger(ManageOfficeController.class);
	
	
	@Autowired
	@Qualifier("jwtUserDetailsService")
	private UserDetailsService userDetailsService;
	
	@Autowired
	ManageOfficeServiceImpl officeService;
	
	@Autowired
	private UserServiceImpl userService;
	
	@Autowired
	RcmProcessLoggerImpl rcmProcessLoggerImpl;
	
	@RequestMapping(value = "assignOffice", method = RequestMethod.POST)
	@PreAuthorize("hasAnyRole('SUPER_ADMIN','TL','ADMIN')")
	public ResponseEntity<?> assignOfficesToBillingUser(@RequestBody AssignOfficesToBillingUserDto dto,Model model) {
		if (dto.getAssignOfficeDetails().stream()
				.anyMatch(x -> (x.getOfficeId()==null||x.getOfficeId().trim().equals("")) || (x.getUserId()==null||x.getUserId().trim().equals("")))) {
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.EMPTY_RESOURCE, null));
		}
		GenericResponse response = null;
		RcmProcessLogger processLogger=null;
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if(partialHeader==null)return ResponseEntity
				.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null));
		try {
			logger.info("------------------Start Process (Manage Offce) ---------------------");
			processLogger = rcmProcessLoggerImpl.startProcessLogger("Manage Office", dto.toString(),
					partialHeader.getJwtUser());
			response = officeService.assignOfficeByAdmin(dto, partialHeader.getCompany(), partialHeader.getTeamId(),
					partialHeader.getJwtUser());// why is this in ADMIN
			rcmProcessLoggerImpl.endProcessLogger(processLogger);
			logger.info("------------------End Process---------------------");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			// We need to reset RcmProcessLogger status InCase of any failure
			if (processLogger != null)
				rcmProcessLoggerImpl.resetProcessLoggerStatus(processLogger);
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(response);
	}
	
	@RequestMapping(value = "/users/team", method = RequestMethod.GET)
	@PreAuthorize("hasAnyRole('TL','SUPER_ADMIN','ASSO','ADMIN')")
	public ResponseEntity<?> getUsersByTeamId(Model model) {
		List<UsersByTeamsAndCompanyDto> response = null;
	    PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader ==null) return null;
		try {
			response = userService.getUsersByTeamIdAndCompany(partialHeader.getTeamId(),partialHeader.getJwtUser());
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
}
