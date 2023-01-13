package com.tricon.rcm.service.impl;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.tricon.rcm.db.entity.RcmUser;
import com.tricon.rcm.dto.ForgotPasswordDto;
import com.tricon.rcm.dto.GenericResponse;
import com.tricon.rcm.email.EmailService;
import com.tricon.rcm.jpa.repository.RCMUserRepository;
import com.tricon.rcm.util.Constants;
import com.tricon.rcm.util.EncrytedKeyUtil;
import com.tricon.rcm.util.GeneratePassword;
import com.tricon.rcm.util.MessageConstants;

@Service
public class RcmUtilServiceImpl {

	@Autowired
	RCMUserRepository userRepo;
	
	@Autowired
	EmailService emailService;

	/**
	 * This Api does reset password for a user.Reset Password procedure completes through Email
	 * @param dto
	 * @return Generic response
	 */
	@Transactional(rollbackOn = Exception.class)
	public GenericResponse forgotPassword(ForgotPasswordDto dto) throws Exception {
		RcmUser existingUser = userRepo.findByEmail(dto.getEmail());
		RcmUser system=userRepo.findByUserName(Constants.SYSTEM);
		if (existingUser != null) {
			String newPassword = GeneratePassword.generateTempPassword();
			String emailSubject = "Reset Password RCM Tool";
			String emailText = "Hi " + existingUser.getFirstName()
					+ ",\n\nYou recently requested to reset your password for your RCM account.\n\n"
					+ "Your New Password is: " + newPassword + "\n\n" + "Thanks and Regards\nRCM Team";
			ExecutorService emailExecutor = Executors.newCachedThreadPool();
		      emailExecutor.execute(new Runnable() {
		 	    @Override
				public void run() {
		 	    	emailService.sendSimpleMessage(dto.getEmail(), emailSubject, emailText);
			}
		});
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

}
