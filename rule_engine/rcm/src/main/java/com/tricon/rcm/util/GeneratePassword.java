package com.tricon.rcm.util;

import java.security.SecureRandom;

public class GeneratePassword {
	
	public static String generateTempPassword() {
		SecureRandom random = new SecureRandom();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < Constants.LENGTH; i++) {
			int randomIndex = random.nextInt(Constants.PASSWORD_PATTERN.length());
			sb.append(Constants.PASSWORD_PATTERN.charAt(randomIndex));
		}
		return sb.toString();
	}
}
