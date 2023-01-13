package com.tricon.rcm.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tricon.rcm.dto.ForgotPasswordDto;
import com.tricon.rcm.dto.GenericResponse;
import com.tricon.rcm.service.impl.RcmUtilServiceImpl;
import com.tricon.rcm.util.MessageConstants;

@RestController
@CrossOrigin
public class RcmUtillController {

	private final Logger logger = LoggerFactory.getLogger(RcmUtillController.class);

	@Autowired
	RcmUtilServiceImpl utilService;

	@RequestMapping(value = "/forgotPassword", method = RequestMethod.POST)
	public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordDto dto) {
		if (dto.getEmail().trim().equals("")) {
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.PASSWORD_EMPTY, null));
		}
		GenericResponse response = null;
		try {
			response = utilService.forgotPassword(dto);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.ok(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(response);
	}
}
