package com.tricon.rcm.api.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tricon.rcm.dto.GenericResponse;
import com.tricon.rcm.dto.PartialHeader;
import com.tricon.rcm.dto.RcmUserClientWithOfficeDto;
import com.tricon.rcm.service.impl.SearchClaimServiceImpl;
import com.tricon.rcm.util.MessageConstants;

@RestController
@CrossOrigin
public class SearchClaimController extends BaseHeaderController {

	private final Logger logger = LoggerFactory.getLogger(SearchClaimController.class);

	@Autowired
	SearchClaimServiceImpl searchClaimServiceImpl;

	@GetMapping(value = "/get-clients-with-offices")
	@PreAuthorize("hasAnyRole('TL','SUPER_ADMIN','ASSO','REPORTING')")
	public ResponseEntity<?> getUserClients(Model model) {
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader == null)
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null));
		List<RcmUserClientWithOfficeDto> response = null;
		try {
			response = searchClaimServiceImpl.getUserClientsWithOffices(partialHeader.getJwtUser());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}

}
