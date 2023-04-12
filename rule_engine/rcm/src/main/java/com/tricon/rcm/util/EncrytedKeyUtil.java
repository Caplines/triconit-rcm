package com.tricon.rcm.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class EncrytedKeyUtil {

	// Encrypt Password with BCryptPasswordEncoder
    public static String encryptKey(String key) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(key);
    }
    
	public static boolean verifyPassword(String oldPassword, String encryptedPassword) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		if (encoder.matches(oldPassword, encryptedPassword))
			return true;
		else
			return false;
	}
 
    public static void main(String[] args) {
        String password = "Smilepoint01";
        String encrytedPassword = encryptKey(password);
 
        System.out.println("Encryted Password: " + encrytedPassword);
    }
    
}
