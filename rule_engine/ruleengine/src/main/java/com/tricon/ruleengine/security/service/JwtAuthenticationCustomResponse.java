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
	
	Collection<? extends GrantedAuthority> authorities;
	
	
    public JwtAuthenticationCustomResponse(String token,
    		String userName ,
    		Collection<? extends GrantedAuthority>  authorities) {
        this.token = token;
        this.userName = userName;
        this.authorities = authorities;
    }
	
	public String getToken() {
		return token;
	}

	public String getUserName() {
		return userName;
	}

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}


}
