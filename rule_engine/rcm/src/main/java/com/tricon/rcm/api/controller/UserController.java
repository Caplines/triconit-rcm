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

import com.tricon.rcm.dto.GenericResponse;
import com.tricon.rcm.dto.PartialHeader;
import com.tricon.rcm.dto.PasswordResetDto;
import com.tricon.rcm.dto.RcmOfficeDto;
import com.tricon.rcm.dto.RcmRoleDto;
import com.tricon.rcm.dto.RcmTeamDto;
import com.tricon.rcm.dto.RcmUserToDto;
import com.tricon.rcm.dto.UpdatePasswordDto;
import com.tricon.rcm.dto.UploadErrorCountsDto;
import com.tricon.rcm.dto.customquery.FreshClaimDataDto;
import com.tricon.rcm.dto.customquery.RcmCompanyWithGsheetDto;
import com.tricon.rcm.dto.customquery.TreatmentPlanLinkDto;
import com.tricon.rcm.enums.RcmRoleEnum;
import com.tricon.rcm.security.JwtUser;
import com.tricon.rcm.service.impl.RcmCommonServiceImpl;
import com.tricon.rcm.service.impl.UserServiceImpl;
import com.tricon.rcm.util.Constants;
import com.tricon.rcm.util.MessageConstants;

import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin
public class UserController extends BaseHeaderController {

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
	@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','REPORTING','TL','ASSO')")
	public ResponseEntity<?> updatePasswordOfUserOrAdmin(@RequestBody UpdatePasswordDto dto,Model model) {
		
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if(partialHeader==null)return ResponseEntity
				.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null));
		
		GenericResponse response = null;
		if (dto.getOldPassword() == null || dto.getOldPassword().trim().equals("") || dto.getNewPassword() == null
				|| dto.getNewPassword().trim().equals("")) {
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.PASSWORD_EMPTY, null));
		}
		try {
			response = userService.updatePassword(dto.getOldPassword(),dto.getNewPassword(),partialHeader.getJwtUser());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(response);
	}

	@ApiOperation(value = "Api For Fetching users basis of Login User teamId and his Role TL", response = RcmUserToDto.class, responseContainer = "List")
	@RequestMapping(value = "/user/users_by_role/tl", method = RequestMethod.GET)
	public ResponseEntity<?> getUsersOfParticularTeam(Model model) {
		List<RcmUserToDto> response = null;
		
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if(partialHeader==null)return ResponseEntity
				.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null));
		
		try {
			response = userService.getUsersByRole(Constants.TEAMLEAD,partialHeader,false);
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
	
	@ApiOperation(value = "Api For Fetching offices basis of Login User clientUuid", response = RcmOfficeDto.class, responseContainer = "List")
	@RequestMapping(value = "getOffices", method = RequestMethod.GET)
	public ResponseEntity<?> officesByUuid(Model model) {
		List<RcmOfficeDto> response = null;
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if(partialHeader==null)return ResponseEntity
				.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null));
		try {
			response = commonService.getOfficesByUuid(partialHeader.getCompany().getUuid());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}
	
	@ApiOperation(value = "Api For Fetching TeamName Details basis of Login User teamId", response = RcmTeamDto.class, responseContainer = "List")
	@RequestMapping(value = "/user/other_teams", method = RequestMethod.GET)
	public ResponseEntity<?> teamNameByUserTeamId(Model model) {
		List<RcmTeamDto> response = null;

		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader==null) {
			return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", "not Autorized"));
		}
		try {
			response = userService.getTeamNameByOtherUserTeamId(partialHeader);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}
//	
//	@ApiOperation(value = "Api For Fetching UserRoles basis of User's email", response = RcmTeamDto.class, responseContainer = "List")
//	@RequestMapping(value = "/user/roles/{userEmail}", method = RequestMethod.GET)
//	@PreAuthorize("hasRole('ADMIN')")
//	public ResponseEntity<?> rolesByUserEmail(@PathVariable("userEmail")String userEmail) {
//		 List<RcmRoleDto> response = null;
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		Object principal = authentication.getPrincipal();
//		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
//		JwtUser jwtUser = (JwtUser) userDetails;
//		if(!jwtUser.isSmilePoint()) {
//			return ResponseEntity.ok(new GenericResponse(HttpStatus.UNAUTHORIZED, "Not Authorize", null));
//		}
//		try {
//			response = null;//userService.getRolesByUserEmail(userEmail);
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error(e.getMessage());
//			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
//		}
//		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
//	}
	
	@RequestMapping(value = "/tp-link-data/{claimUuid}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyRole('TL','SUPER_ADMIN','ASSO')")
	public ResponseEntity<?> treatmentPlanData(@PathVariable("claimUuid") String claimUuid, Model model) {
		List<TreatmentPlanLinkDto> response = null;
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader == null)
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null));

		try {
			response = userService.getTreatmentPlanLinkData(claimUuid);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}
	
	@RequestMapping(value = "/issue-claim-counts", method = RequestMethod.GET)
	@PreAuthorize("hasAnyRole('TL','SUPER_ADMIN','ASSO','REPORTING','ADMIN')")
	public ResponseEntity<?> uploadErrorsCounts(Model model) {
		UploadErrorCountsDto response = null;
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader == null)
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null));

		try {
			response = userService.getCountsOfUploadErrors(partialHeader.getCompany());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}
	
	@RequestMapping(value = "/gsheet-link", method = RequestMethod.GET)
	@PreAuthorize("hasAnyRole('TL','SUPER_ADMIN')")
	public ResponseEntity<?> gSheetLink(Model model) {
		List<RcmCompanyWithGsheetDto> response = null;
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader == null)
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null));
		try {
			response = userService.getGoogleSheetLink(partialHeader.getCompany());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}
	
	
	@RequestMapping(value = "/archive-claim-counts", method = RequestMethod.GET)
	@PreAuthorize("hasAnyRole('TL','SUPER_ADMIN','ASSO')")
	public ResponseEntity<?> archiveClaimCounts(Model model) {
		int response = 0;
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader == null)
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null));

		try {
			response = userService.getArchiveClaimsCounts(partialHeader.getCompany());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}
	
}
