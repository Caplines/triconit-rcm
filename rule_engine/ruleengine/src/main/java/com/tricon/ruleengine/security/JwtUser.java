package com.tricon.ruleengine.security;

import java.util.Collection;
import java.util.Date;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

//import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Deepak.Dogra
 */
public class JwtUser implements UserDetails {

    /**
	 * 
	 */
	private static final long serialVersionUID = 5610948415563363284L;
	private final String uuid;
    private final String firstname;
    private final String lastname;
    private final String password;
    private final int userType;
    private final String userName;
    private final Collection<? extends GrantedAuthority> authorities;
    private final int active;
    private final Date lastPasswordResetDate;
    

    public JwtUser(
          String uuid,
          String firstname,
          String lastname,
          String userName,
          String password,
          int userType,
          Collection<? extends GrantedAuthority> authorities,
          int enabled,
          Date lastPasswordResetDate
    ) {
        this.uuid = uuid;
        this.firstname = firstname;
        this.lastname = lastname;
        this.userName = userName;
        this.password = password;
        this.userType  = userType;
        this.authorities = authorities;
        this.active = enabled;
        this.lastPasswordResetDate = lastPasswordResetDate;
    }
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return authorities;
	}
	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return password;
	}
	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return userName;
	}
	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		if (this.active ==1) return true;
		return false;
	}
	public String getUuid() {
		return uuid;
	}
	public String getFirstname() {
		return firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public String getUserName() {
		return userName;
	}
	public int getActive() {
		return active;
	}
	public Date getLastPasswordResetDate() {
		return lastPasswordResetDate;
	}
	public int getUserType() {
		return userType;
	}

	
    
}
