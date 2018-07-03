package com.tricon.ruleengine.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class EncrytedKeyUtil {

	// Encryte Password with BCryptPasswordEncoder
    public static String encryteKey(String key) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(key);
    }
 
    public static void main(String[] args) {
        String password = "123";
        String encrytedPassword = encryteKey(password);
 
        System.out.println("Encryted Password: " + encrytedPassword);
    }
    
}
