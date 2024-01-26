package com.tricon.rcm.api.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tricon.rcm.dto.ClientSectionMappingDto;
import com.tricon.rcm.dto.GenericResponse;
import com.tricon.rcm.dto.PartialHeader;
import com.tricon.rcm.service.impl.ClaimSectionImpl;
import com.tricon.rcm.util.MessageConstants;

@RestController
@CrossOrigin
public class RcmClaimSectionController extends BaseHeaderController {

	private final Logger logger = LoggerFactory.getLogger(RcmClaimSectionController.class);

	@Autowired
	ClaimSectionImpl claimSection;

	/**
	 * To manage various sections of claims client wise
	 * 
	 * @param dto
	 * @param model
	 * @return
	 */
	@PostMapping(value = "api/manage-claim-client-section")
	@PreAuthorize("hasAnyRole('SUPER_ADMIN')")
	public ResponseEntity<?> clientSectionDetails(@RequestBody List<ClientSectionMappingDto> listOfSectionsDto,
			Model model) {
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader == null)
			return ResponseEntity.badRequest()
					.body(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null));
		if (listOfSectionsDto.isEmpty()
				|| listOfSectionsDto.stream().anyMatch(x -> !StringUtils.isNoneBlank(x.getClientUuid()))
				|| listOfSectionsDto.stream().anyMatch(x1 -> x1.getTeamsWithSections().isEmpty())
				|| listOfSectionsDto.stream().flatMap(x2 -> x2.getTeamsWithSections().stream())
						.anyMatch(x3 -> x3.getTeamId() == null || x3.getTeamId() == 0 || x3.getSectionData().isEmpty())
				|| listOfSectionsDto.stream().flatMap(x4 -> x4.getTeamsWithSections().stream())
						.flatMap(x5 -> x5.getSectionData().stream()).anyMatch(y -> y.getEditAccess() == null
								|| y.getViewAccess() == null || y.getSectionId() == null || y.getSectionId() == 0)) {
			return ResponseEntity.badRequest()
					.body(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.EMPTY_RESOURCE, null));
		}

		String response = null;
		try {
			response = claimSection.manageClientSectionDetails(listOfSectionsDto);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}

	@GetMapping(value = "api/claim-client-with-section")
	@PreAuthorize("hasAnyRole('SUPER_ADMIN')")
	public ResponseEntity<?> getSectionDetails(Model model) {
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader == null)
			return ResponseEntity.badRequest()
					.body(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null));
		List<ClientSectionMappingDto> response = null;
		try {
			response = claimSection.getClientsWithAllSectionsAndTeam();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}
	
	@GetMapping(value = "api/claim-user-with-section/{uuid}")
	@PreAuthorize("hasAnyRole('SUPER_ADMIN')")
	public ResponseEntity<?> getSectionDetailsOfUser(@PathVariable("uuid")String userUuid,Model model) {
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader == null)
			return ResponseEntity.badRequest()
					.body(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null));
		List<ClientSectionMappingDto> response = null;
		try {
			response = claimSection.sectionsPermissionOfUser(userUuid,null,0);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}
	
	@PostMapping(value = "api/manage-claim-user-section")
	@PreAuthorize("hasAnyRole('SUPER_ADMIN')")
	public ResponseEntity<?> manageSectionDetailsOfUser(@RequestBody List<ClientSectionMappingDto> listOfSectionsDto,
			Model model) {
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader == null)
			return ResponseEntity.badRequest()
					.body(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null));
		if (listOfSectionsDto.isEmpty()
				|| listOfSectionsDto.stream().anyMatch(x -> !StringUtils.isNoneBlank(x.getClientUuid()))
				|| listOfSectionsDto.stream().anyMatch(x -> !StringUtils.isNoneBlank(x.getUserUuid()))
				|| listOfSectionsDto.stream().anyMatch(x1 -> x1.getTeamsWithSections().isEmpty())
				|| listOfSectionsDto.stream().flatMap(x2 -> x2.getTeamsWithSections().stream())
						.anyMatch(x3 -> x3.getTeamId() == null || x3.getTeamId() == 0 || x3.getSectionData().isEmpty())
				|| listOfSectionsDto.stream().flatMap(x4 -> x4.getTeamsWithSections().stream())
						.flatMap(x5 -> x5.getSectionData().stream()).anyMatch(y -> y.getEditAccess() == null
								|| y.getViewAccess() == null || y.getSectionId() == null || y.getSectionId() == 0)) {
			return ResponseEntity.badRequest()
					.body(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.EMPTY_RESOURCE, null));
		}
		String userUuid = listOfSectionsDto.stream().map(x -> x.getUserUuid()).findFirst().orElse(null);
		String response = null;
		try {
			response = claimSection.manageSectionsOfUsers(listOfSectionsDto, userUuid);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}

}
