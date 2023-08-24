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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tricon.rcm.dto.ClaimAttachmentsResponseDto;
import com.tricon.rcm.dto.FileResponseDto;
import com.tricon.rcm.dto.FinalSubmittionClaimAttachmentDto;
import com.tricon.rcm.dto.GenericResponse;
import com.tricon.rcm.dto.PartialHeader;
import com.tricon.rcm.dto.RcmClaimDeAttachmentDto;
import com.tricon.rcm.service.impl.AttachmentServiceImpl;
import com.tricon.rcm.util.MessageConstants;

@RestController
@CrossOrigin
public class RcmAttachmentController extends BaseHeaderController {

	@Autowired
	private AttachmentServiceImpl attachmentServiceImpl;
	
	private final Logger logger = LoggerFactory.getLogger(RcmAttachmentController.class);
	
	
	@PostMapping("/api/claim-attachment")
	@PreAuthorize("hasAnyRole('TL','SUPER_ADMIN')")
	public ResponseEntity<?> uploadClaimAttachment(@RequestParam String claimUuid, @RequestParam int attachmentTypeId,
			@RequestParam MultipartFile file, Model model) {
		FileResponseDto response = null;
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader == null)
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null));

		if (claimUuid == null || file == null || claimUuid.isEmpty()) {
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.EMPTY_RESOURCE, null));
		}
		try {
			response =attachmentServiceImpl.saveClaimAttachment(file, claimUuid, partialHeader.getJwtUser(),
					partialHeader.getTeamId(), attachmentTypeId);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}

	@GetMapping("/api/get-attachments/{claimUuid}")
	@PreAuthorize("hasAnyRole('TL','SUPER_ADMIN')")
	public ResponseEntity<?> getClaimAttachments(@PathVariable("claimUuid") String claimUuid, Model model) {
		List<ClaimAttachmentsResponseDto> response = null;
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader == null)
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null));
		try {
			response =attachmentServiceImpl.getClaimAttachments(claimUuid);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}
	
	@PostMapping("/api/remove-claim-attachment")
	@PreAuthorize("hasAnyRole('TL','SUPER_ADMIN')")
	public ResponseEntity<?> removeClaimAttachment(@RequestBody RcmClaimDeAttachmentDto dto, Model model) {
		FileResponseDto response = null;
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader == null)
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null));

		if (dto.getClaimAttachmentId() == null || (dto.getClaimUuid()==null || dto.getClaimUuid().isEmpty())) {
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.EMPTY_RESOURCE, null));
		}

		try {
			response = attachmentServiceImpl.removeClaimAttachment(dto, partialHeader.getJwtUser());

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}
	
	
	@PostMapping("/api/final-attachments-data")
	@PreAuthorize("hasAnyRole('TL','SUPER_ADMIN')")
	public ResponseEntity<?> finalSubmittionClaimAttachments(@RequestBody FinalSubmittionClaimAttachmentDto dto,
			Model model) {
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader == null)
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.SOMETHING_WENT_WRONG, null));
		if ((dto.getRemarks() == null || dto.getRemarks().isEmpty()) || (dto.getSubmitButton() == null
				|| dto.getSubmitButton().isEmpty())) {
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.EMPTY_RESOURCE, null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", true));
	}

}
