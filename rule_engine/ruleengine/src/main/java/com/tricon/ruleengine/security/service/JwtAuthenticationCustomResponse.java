package com.tricon.ruleengine.security.service;

import java.io.Serializable;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;


/**
 * @author Deepak.Dogra
 *
 */
public class JwtAuthenticationCustomResponse implements Serializable {


	
	private static final long serialVersionUID = 7359399371708955717L;

	private String token;

	private String userName;
	
	private int userType;
	
	Collection<? extends GrantedAuthority> authorities;
	
	
    public JwtAuthenticationCustomResponse(String token,
    		String userName ,
    		Collection<? extends GrantedAuthority>  authorities,
    		int userType) {
        this.token = token;
        this.userName = userName;
        this.authorities = authorities;
        this.userType = userType;
        
    }
	
	public String getToken() {
		return token;
	}

	public String getUserName() {
		return userName;
	}

	public int getUserType() {
		return userType;
	}

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}


	
}
