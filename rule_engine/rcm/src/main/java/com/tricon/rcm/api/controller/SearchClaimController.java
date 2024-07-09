package com.tricon.rcm.api.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tricon.rcm.dto.GenericResponse;
import com.tricon.rcm.dto.PartialHeader;
import com.tricon.rcm.dto.RcmUserClientWithOfficeDto;
import com.tricon.rcm.dto.SearchClaimDto;
import com.tricon.rcm.dto.SearchClaimPaginationDto;
import com.tricon.rcm.dto.SearchClaimResponseDto;
import com.tricon.rcm.service.impl.SearchClaimServiceImpl;
import com.tricon.rcm.util.MessageConstants;

@RestController
@CrossOrigin
public class SearchClaimController extends BaseHeaderController {

	private final Logger logger = LoggerFactory.getLogger(SearchClaimController.class);

	@Autowired
	SearchClaimServiceImpl searchClaimServiceImpl;

	@GetMapping(value = "/get-clients-with-offices")
	@PreAuthorize("hasAnyRole('TL','SUPER_ADMIN','ASSO','REPORTING','ADMIN')")
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
	
	@PostMapping(value = "/api/search-claims")
	@PreAuthorize("hasAnyRole('TL','SUPER_ADMIN','ASSO','REPORTING','ADMIN')")
	public ResponseEntity<?> claimSearch(@RequestBody SearchClaimDto dto, Model model) {
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader == null)
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null));

		//verify null fields
		if (dto.getClientUuid() == null || dto.getOfficeUuid() == null || dto.getAgeCategory() == null
				|| dto.getClaimStatus() == null || dto.getInsuranceName() == null || dto.getInsuranceType() == null
				|| dto.getProviderName() == null || dto.getProviderType() == null || dto.getResponsibleTeam() == null
				|| dto.getShowArchive() == null
				|| dto.getShowArchive().isEmpty()
				|| !dto.getClientUuid().stream().anyMatch(x -> StringUtils.isNoneBlank(x))
				|| dto.getOfficeUuid().stream().anyMatch(x -> x.isEmpty())
				|| (dto.getPageNumber() == 0 || dto.getPageNumber() == -1)
				|| dto.getAgeCategory().stream().anyMatch(x -> x == null)
				|| dto.getClaimStatus().stream().anyMatch(x -> x.isEmpty())
				|| dto.getInsuranceName().stream().anyMatch(x -> x.isEmpty())
				|| dto.getInsuranceType().stream().anyMatch(x -> x.isEmpty())
				|| dto.getProviderName().stream().anyMatch(x -> x.isEmpty())
				|| dto.getProviderType().stream().anyMatch(x -> x.isEmpty())
				|| dto.getResponsibleTeam().stream().anyMatch(x -> x == null)) {
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.EMPTY_RESOURCE, null));
		}
		List<SearchClaimPaginationDto> response = null;
		try {
			response = searchClaimServiceImpl.searchClaims(dto);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}
	
	@PostMapping(value = "/api/search-claims/pdf")
	@PreAuthorize("hasAnyRole('TL','SUPER_ADMIN','ASSO','REPORTING')")
	public ResponseEntity<?> claimSearchWithoutPagination(@RequestBody SearchClaimDto dto, Model model,
			HttpServletResponse response) throws IOException {
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader == null)
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null));

		// verify null fields
		if (dto.getClientUuid() == null || dto.getOfficeUuid() == null || dto.getAgeCategory() == null
				|| dto.getClaimStatus() == null || dto.getInsuranceName() == null || dto.getInsuranceType() == null
				|| dto.getProviderName() == null || dto.getProviderType() == null || dto.getResponsibleTeam() == null
				|| dto.getShowArchive() == null || dto.getShowArchive().isEmpty()
				|| !dto.getClientUuid().stream().anyMatch(x -> StringUtils.isNoneBlank(x))
				|| dto.getOfficeUuid().stream().anyMatch(x -> x.isEmpty())
				|| dto.getAgeCategory().stream().anyMatch(x -> x == null)
				|| dto.getClaimStatus().stream().anyMatch(x -> x.isEmpty())
				|| dto.getInsuranceName().stream().anyMatch(x -> x.isEmpty())
				|| dto.getInsuranceType().stream().anyMatch(x -> x.isEmpty())
				|| dto.getProviderName().stream().anyMatch(x -> x.isEmpty())
				|| dto.getProviderType().stream().anyMatch(x -> x.isEmpty())
				|| dto.getResponsibleTeam().stream().anyMatch(x -> x == null)) {
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.EMPTY_RESOURCE, null));
		}
		Object[] obj = null;
		ByteArrayOutputStream o = null;
		try {
			obj = searchClaimServiceImpl.searchClaimsWithoutPagination(dto,partialHeader.getClientName());
			if (obj != null && obj[1] != null) {
				o = (ByteArrayOutputStream) obj[1];
				response.setContentType("application/octet-stream");
				response.setHeader("Content-Disposition",
						String.format("attachment; filename=" + "Search-claims" + ".pdf"));
				InputStream in = new ByteArrayInputStream(o.toByteArray());
				org.apache.commons.io.IOUtils.copy(in, response.getOutputStream());
				response.flushBuffer();
				o.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			if (o != null) {
				o.close();
			}
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", null));
	}

}
