package com.tricon.rcm.email;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailUtil {

	private final Logger logger = LoggerFactory.getLogger(EmailUtil.class);
	
	@Autowired
	EmailService emailService;
	
	@Value("${app.email.userRegistration}")
	private boolean userRegistrationStatus;
	
	@Value("${app.email.forgotPassword}")
	private boolean forgotPasswordStatus;
	
	public void sendEmailForUserRegistration(String userEmail, String emailSubject, String emailText) {
		if (userRegistrationStatus) {
			logger.info("Sending Email for UserRegistration status:enable");
			try {
				ExecutorService emailExecutor = Executors.newCachedThreadPool();
				emailExecutor.execute(new Runnable() {
					@Override
					public void run() {
						boolean status=emailService.sendSimpleMessage(userEmail, emailSubject, emailText);
						logger.info("Email success:"+status);
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			logger.info("Sending Email for UserRegistration status:disable");
		}
	}
	
	public void sendEmailForForgotPassword(String userEmail, String emailSubject, String emailText) {
		if (forgotPasswordStatus) {
			logger.info("Sending Email for ForgotPassword status:enable");
			try {
				ExecutorService emailExecutor = Executors.newCachedThreadPool();
				emailExecutor.execute(new Runnable() {
					@Override
					public void run() {
						boolean status=emailService.sendSimpleMessage(userEmail, emailSubject, emailText);
						logger.info("Email success:"+status);
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			logger.info("Sending Email for ForgotPassword status:disable");
		}
	}
}
