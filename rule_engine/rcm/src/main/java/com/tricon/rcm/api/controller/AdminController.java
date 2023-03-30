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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tricon.rcm.dto.ClaimAssignmentDto;
import com.tricon.rcm.dto.FindUserDto;
import com.tricon.rcm.dto.GenericResponse;
import com.tricon.rcm.dto.PartialHeader;
import com.tricon.rcm.dto.PasswordResetDto;
import com.tricon.rcm.dto.RcmClaimDto;
import com.tricon.rcm.dto.RcmClaimResponseDto;
import com.tricon.rcm.dto.RcmClientDto;
import com.tricon.rcm.dto.RcmClientResponseDto;
import com.tricon.rcm.dto.RcmCompanyDto;
import com.tricon.rcm.dto.RcmEditOfficeDto;
import com.tricon.rcm.dto.RcmEditRolesDto;
import com.tricon.rcm.dto.RcmOfficeDto;
import com.tricon.rcm.dto.RcmUserDto;
import com.tricon.rcm.dto.RcmUserStatusDto;
import com.tricon.rcm.dto.RcmUserToDto;
import com.tricon.rcm.dto.UserRegistrationDto;
import com.tricon.rcm.enums.RcmRoleEnum;
import com.tricon.rcm.security.JwtUser;
import com.tricon.rcm.service.impl.AdminServiceImpl;
import com.tricon.rcm.util.Constants;
import com.tricon.rcm.util.MessageConstants;

@RestController
@CrossOrigin
public class AdminController extends BaseHeaderController{

	private final Logger logger = LoggerFactory.getLogger(AdminController.class);

	@Autowired
	private AdminServiceImpl serviceImpl;
	
	@Autowired
	@Qualifier("jwtUserDetailsService")
	private UserDetailsService userDetailsService;

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
	public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationDto dto,Model model) {
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		GenericResponse response = null;
		if(partialHeader==null)return ResponseEntity
				.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null));
		
		if (dto.getUserRole().equals(Constants.SYSTEM)) {
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", null));
		}
		
		String checkValidation=checkUserDetailsRelatedToTeamOrClient(dto.getUserRole(),dto.getCompanyUuid(),dto.getTeamId());
		if(checkValidation!=null) {			
		   return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, checkValidation, null));
		}
		try {
			response = serviceImpl.registerUser(dto,partialHeader.getRole(),partialHeader.getJwtUser());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(response);
	}

	@RequestMapping(value = "/resetpassword", method = RequestMethod.POST)
	@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
	public ResponseEntity<?> resetpasswordByAdmin(@RequestBody PasswordResetDto dto,Model model) {
		GenericResponse response = null;
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if(partialHeader==null)return ResponseEntity
				.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null));
		
		if ((dto.getPassword()==null||dto.getPassword().trim().equals("")) || (dto.getUuid()==null||dto.getUuid().trim().equals(""))) {
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.EMPTY_RESOURCE, null));
		}
		
		try {
			response = serviceImpl.passwordUpdation(partialHeader.getJwtUser(),dto,partialHeader.getRole());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(response);
	}

	@RequestMapping(value = "/finduser", method = RequestMethod.POST)
	@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
	public ResponseEntity<?> findUserByEmail(@RequestBody FindUserDto dto,Model model) {
		RcmUserDto response = null;
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if(partialHeader==null)return ResponseEntity
				.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null));

		if (dto.getEmail()==null||dto.getEmail().trim().equals("")) {
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.EMPTY_RESOURCE, null));
		}
		try {
			response = serviceImpl.findUserByEmail(dto,partialHeader.getRole(),partialHeader.getCompany());
			if(response==null) {
				return ResponseEntity.ok(new GenericResponse(HttpStatus.OK,MessageConstants.USER_NOT_EXIST,null));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK,MessageConstants.USER_EXIST,response));
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
	@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
	public ResponseEntity<?> resetstatus(@RequestBody RcmUserStatusDto dto,Model model) {
		GenericResponse response = null;
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if(partialHeader==null)return ResponseEntity
				.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null));

		if (dto.getUserActiveStatus().stream()
				.anyMatch(x -> (x.getUserId()==null||x.getUserId().trim().equals("")) ||x.getStatus()==null)) {
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.EMPTY_RESOURCE, null));
		}
		if(dto.getUserActiveStatus().stream().anyMatch(x->x.getUserId().equals(partialHeader.getJwtUser().getUuid())))
		{
			return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", null));
		}
		try {
			response = serviceImpl.resetUserStatus(partialHeader.getJwtUser(),dto);
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
	@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
	public ResponseEntity<?> getCompanyDetails(Model model) {
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if(partialHeader==null)return ResponseEntity
				.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null));
		GenericResponse response = null;
		try {
			response = serviceImpl.getCompanies(partialHeader.getRole(),partialHeader.getJwtUser());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}
	
	@RequestMapping(value = "/getClientDetails", method = RequestMethod.GET)
	@PreAuthorize("hasRole('SUPER_ADMIN')")
	public ResponseEntity<?> getClientDetails(Model model) {
		List<RcmClientResponseDto> response = null;
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if(partialHeader==null)return ResponseEntity
				.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null));
		
		if(!partialHeader.getJwtUser().isSmilePoint()) {
			return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.UNAUTHORIZED_USER, null));
		}
		try {
			response = serviceImpl.getClientWithGoogleSheetData();
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

	@RequestMapping(value = "addOffice", method = RequestMethod.POST)
	@PreAuthorize("hasRole('SUPER_ADMIN')")
	public ResponseEntity<?> addOffice(@RequestBody RcmCompanyDto dto,Model model) {
		if ((dto.getName()==null||dto.getName().trim().equals("")) || (dto.getCompanyUuid()==null||dto.getCompanyUuid().trim().equals(""))) {
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.EMPTY_RESOURCE, null));
		}
		GenericResponse response = null;
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if(partialHeader==null)return ResponseEntity
				.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null));
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
	@PreAuthorize("hasRole('SUPER_ADMIN')")
	public ResponseEntity<?> officesByUuid(@PathVariable("uuid") String uuid,Model model) {
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if(partialHeader==null)return ResponseEntity
				.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null));
		List<RcmOfficeDto> response = null;
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
	@PreAuthorize("hasRole('SUPER_ADMIN')")
	public ResponseEntity<?> editOffice(@RequestBody RcmEditOfficeDto dto,Model model) {
		if ((dto.getOfficeName()==null||dto.getOfficeName().trim().equals("")) || (dto.getOfficeUuid()==null||dto.getOfficeUuid().trim().equals(""))) {
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.EMPTY_RESOURCE, null));
		}
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if(partialHeader==null)return ResponseEntity
				.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null));

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
	@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
	public ResponseEntity<?> editRoles(@RequestBody RcmEditRolesDto dto,Model model) {
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if(partialHeader==null)return ResponseEntity
				.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null));
		
		
		if ((dto.getUuid()==null||dto.getUuid().trim().equals(""))|| dto.getRole().trim().equals("")
				||dto.getFirstName().trim().equals("")|| dto.getLastName().trim().equals("")){
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.EMPTY_RESOURCE, null));
		}
		
		if (dto.getRole().equals(Constants.SYSTEM)) {
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", null));
		}
		
		String checkValidation=checkUserDetailsRelatedToTeamOrClient(dto.getRole(),dto.getCompanyUuid(),dto.getTeamId());
		if(checkValidation!=null) {			
		   return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, checkValidation, null));
		}
				
		GenericResponse response = null;	
		try {
			response = serviceImpl.editRolesByAdmin(dto,partialHeader.getRole(),partialHeader.getJwtUser());
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
//	@RequestMapping(value = "assignmentclaimUsers", method = RequestMethod.POST)
//	@PreAuthorize("hasRole('ADMIN')")
//	public ResponseEntity<?> claimsFromAssignmentTable(@RequestBody RcmClaimDto dto) {
//		if (dto.getTeamId()==null||dto.getUserUuid().trim().equals("")) {
//			return ResponseEntity
//					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.EMPTY_RESOURCE, null));
//		}
//		List<RcmUserToDto> response = null;
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		Object principal = authentication.getPrincipal();
//		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
//		JwtUser jwtUser = (JwtUser) userDetails;
//		if(!jwtUser.isSmilePoint()) {
//			return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", null));
//		}
//		try {
//			response = serviceImpl.getUsersFromClaimAssignmentTable(dto, jwtUser.getCompany());
//			if(response==null) {
//				return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", null));
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error(e.getMessage());
//			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
//		}
//		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
//	}
//	
//	@RequestMapping(value = "assignclaimToUser", method = RequestMethod.POST)
//	@PreAuthorize("hasRole('ADMIN')")
//	public ResponseEntity<?> claimAssignmentToUser(@RequestBody ClaimAssignmentDto dto) {
//		if ((dto.getNewClaimUserUuid()==null||dto.getNewClaimUserUuid().trim().equals(""))||(dto.getOldClaimUserUuid()==null||dto.getOldClaimUserUuid().trim().equals(""))) {
//			return ResponseEntity
//					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.EMPTY_RESOURCE, null));
//		}
//		String response = null;
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		Object principal = authentication.getPrincipal();
//		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
//		JwtUser jwtUser = (JwtUser) userDetails;
//		if(!jwtUser.isSmilePoint()) {
//			return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", null));
//		}
//		try {
//			response = serviceImpl.claimAssignmentToUser(dto,jwtUser);
//			if(response==null) {
//				return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", null));
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error(e.getMessage());
//			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
//		}
//		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
//	}
	
	@RequestMapping(value = "addClient", method = RequestMethod.POST)
	@PreAuthorize("hasRole('SUPER_ADMIN')")
	public ResponseEntity<?> addCompany(@RequestBody RcmClientDto dto,Model model) {
		if ((dto.getClientName()==null||dto.getClientName().trim().equals(""))|| dto.getHeader().stream().anyMatch(
				x ->(x.getGoogle_sheet_id()==null|| x.getGoogle_sheet_id().trim().equals("")) || (x.getGoogle_sheet_sub_id()==null||x.getGoogle_sheet_sub_id().trim().equals(""))
				|| (x.getGoogle_sheet_sub_name()==null||x.getGoogle_sheet_sub_name().trim().equals(""))
				|| (x.getName()==null||x.getName().trim().equals("")))) {
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.EMPTY_RESOURCE, null));
		}
		String response = null;
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if(partialHeader==null)return ResponseEntity
				.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null));

		try {
			response = serviceImpl.addClient(dto);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}
	
	@RequestMapping(value = "editClient", method = RequestMethod.POST)
	@PreAuthorize("hasRole('SUPER_ADMIN')")
	public ResponseEntity<?> editCompany(@RequestBody RcmClientDto dto,Model model) {
		if ((dto.getCompanyUuid()==null||dto.getCompanyUuid().trim().equals("")) || (dto.getClientName()==null||dto.getClientName().trim().equals(""))
				|| dto.getHeader().stream()
						.anyMatch(x -> (x.getGoogle_sheet_id()==null||x.getGoogle_sheet_id().trim().equals(""))
								|| (x.getGoogle_sheet_sub_id()==null||x.getGoogle_sheet_sub_id().trim().equals(""))
								|| (x.getGoogle_sheet_sub_name()==null||x.getGoogle_sheet_sub_name().trim().equals("")) || (x.getName()==null||x.getName().trim().equals("")))) {
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.EMPTY_RESOURCE, null));
		}
		String response = null;
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if(partialHeader==null)return ResponseEntity
				.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null));

		try {
			response = serviceImpl.editClient(dto);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}
	
	@RequestMapping(value = "claim_assign_to_user/{uuid}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
	public ResponseEntity<?> claimStatusOfUser(@PathVariable("uuid") String uuid,Model model) {
		RcmClaimResponseDto response = null;
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if(partialHeader==null)return ResponseEntity
				.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null));
		try {
			response = serviceImpl.checkExistingUserClaimStatusActiveOrNot(uuid);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}
	
	public String checkUserDetailsRelatedToTeamOrClient(String role,List<String>companyUuid,List<Integer>teamId) {

		// check role exit or not
		if (RcmRoleEnum.validateRoles(role) == null) {
			return MessageConstants.ROLE_NOT_MATCH;
		}

		// check team is mandatory
		if (!(role.equals(Constants.ADMIN) || role.equals(Constants.SUPER_ADMIN))) {

			if (teamId==null || teamId.isEmpty() || teamId.stream().anyMatch(x -> x == 0)) {
				return MessageConstants.TEAM_REQUIRED;
			}

		} else {
			if (!teamId.isEmpty()) {
				return MessageConstants.TEAM_NOT_REQUIRED;
			}
		}

		// check client is mandatory

		if (!role.equals(Constants.SUPER_ADMIN) && (companyUuid==null ||companyUuid.isEmpty()
				|| companyUuid.stream().anyMatch(x -> x == null || x.trim().equals("")))) {
			return MessageConstants.CLINET_REQUIRED;
		}

		// Client is not required for SUPER_ADMIN
		if (role.equals(Constants.SUPER_ADMIN) && !companyUuid.isEmpty()) {
			return  MessageConstants.CLINET_NOT_REQUIRED;
		}
		
		return null;
	}

}
