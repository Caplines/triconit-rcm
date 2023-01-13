package com.tricon.rcm.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender emailSender;
	
	@Value("${app.email.from}")
	private String from;

    /**
     * This api is used for send simple text message through email
     * @param to
     * @param subject
     * @param text
     * @return true or false based on condition
     */
	public boolean sendSimpleMessage(String to, String subject, String text) {
		boolean isMailSent = true;
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom(from);
			message.setTo(to);
			message.setSubject(subject);
			message.setText(text);
			emailSender.send(message);
		} catch (Exception e) {
			isMailSent = false;
			e.printStackTrace();
		}
		return isMailSent;
	}
}
