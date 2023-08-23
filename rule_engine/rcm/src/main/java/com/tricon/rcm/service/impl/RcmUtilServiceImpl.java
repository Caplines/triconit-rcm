package com.tricon.rcm.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.Instant;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.tricon.rcm.db.entity.RcmTeam;
import com.tricon.rcm.db.entity.RcmUser;
import com.tricon.rcm.dto.ForgotPasswordDto;
import com.tricon.rcm.dto.GenericResponse;
import com.tricon.rcm.dto.RecaptchaResponseDto;
import com.tricon.rcm.email.EmailUtil;
import com.tricon.rcm.jpa.repository.RCMUserRepository;
import com.tricon.rcm.security.api.controller.AuthenticationRestController;
import com.tricon.rcm.util.Constants;
import com.tricon.rcm.util.EncrytedKeyUtil;
import com.tricon.rcm.util.GeneratePassword;
import com.tricon.rcm.util.MessageConstants;

@Service
public class RcmUtilServiceImpl {

	@Autowired
	RCMUserRepository userRepo;
	
	@Autowired
	EmailUtil emailUtil;
	
	@Value("${captcha.url}")
	private String captchaUrl;

	@Value("${captcha.secret.key}")
	private String captchaSecretKey;
	
	
	@Autowired
	RestTemplate restTemplate;

	private final Logger logger = LoggerFactory.getLogger(RcmUtilServiceImpl.class);

	/**
	 * This Api does reset password for a user.Reset Password procedure completes through Email
	 * @param dto
	 * @return Generic response
	 */
	@Transactional(rollbackOn = Exception.class)
	public GenericResponse forgotPassword(ForgotPasswordDto dto) throws Exception {
		RcmUser existingUser = userRepo.findByEmail(dto.getEmail());
		RcmUser system = null;
		if (existingUser != null) {
			system = userRepo.findByEmail(Constants.SYSTEM_USER_EMAIL);
			String newPassword = GeneratePassword.generateTempPassword();
			String emailSubject = "Reset Password RCM Tool";
			String emailText = "Hi " + existingUser.getFirstName()
					+ ",\n\nYou recently requested to reset your password for your RCM account.\n\n"
					+ "Your New Password is: " + newPassword + "\n\n" + "Thanks and Regards\nRCM Team";
			emailUtil.sendEmailForForgotPassword(dto.getEmail(), emailSubject, emailText);
			if (!newPassword.trim().equals("")) {
				String newEncryptPassword=EncrytedKeyUtil.encryptKey(newPassword);
				existingUser.setPassword(newEncryptPassword);
				existingUser.setTempPassword(newEncryptPassword);
				existingUser.setLastPasswordResetDate(Timestamp.from(Instant.now()));
				existingUser.setUpdatedBy(system);
				userRepo.save(existingUser);
				return new GenericResponse(HttpStatus.OK, MessageConstants.PASSWORD_UPDATE, null);
			}
		} else {
			return new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.USER_NOT_EXIST, null);
		}
		return new GenericResponse(HttpStatus.OK, "", null);
	}
	
	public int checkTeamNullOrNot(RcmTeam team) {
		
		return team==null?-1:team.getId();
	}

	
	public boolean googleCaptchaVerificationStatus(String tokenForRecaptcha) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(captchaUrl)
					.queryParam("secret", captchaSecretKey).queryParam("response", tokenForRecaptcha);

			HttpEntity<String> entity = new HttpEntity<>(headers);
			ResponseEntity<RecaptchaResponseDto> response = restTemplate.exchange(builder.build().encode().toUri(),
					HttpMethod.GET, entity, RecaptchaResponseDto.class);
			RecaptchaResponseDto rs = response.getBody();
			logger.info("Captcha getHostName-->"+rs.getHostName());
			logger.info("Captcha Status-->"+rs.isSuccess());
			if (rs.isSuccess())
				return true;
			else
				return false;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	
	public String getFileAbsolutePath(String path) {
		Path fullPath = null;
		try {
			fullPath = Files.createDirectories(Paths.get(path));
			return fullPath.toAbsolutePath().toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}
}
