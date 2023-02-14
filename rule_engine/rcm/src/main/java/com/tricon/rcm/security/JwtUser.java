package com.tricon.rcm.security;

import java.util.Collection;
import java.util.Date;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.tricon.rcm.db.entity.RcmCompany;
import com.tricon.rcm.util.Constants;


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
    private final int teamId;
    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;
    private final int active;
    private final Date lastPasswordResetDate;
    private final RcmCompany company;
    
    private final boolean isSmilePoint;
    private final boolean isTeamLead;
    private final boolean isAssociate;
    

    public JwtUser(
          String uuid,
          String firstname,
          String lastname,
          String email,
          String password,
           int teamId,
          Collection<? extends GrantedAuthority> authorities,
          int enabled,
          Date lastPasswordResetDate,
          RcmCompany company
    ) {
        this.uuid = uuid;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.teamId  = teamId;
        this.authorities = authorities;
        this.active = enabled;
        this.lastPasswordResetDate = lastPasswordResetDate;
        this.company =company;
        this.isSmilePoint=company!=null?(company.getName().equals(Constants.COMPANY_NAME)?true:false):false;
        this.isTeamLead=authorities!=null?(authorities.stream().anyMatch(x->x.getAuthority().endsWith(Constants.HYPHEN+Constants.TEAMLEAD))?true:false):false;
        this.isAssociate=authorities!=null?(authorities.stream().anyMatch(x->x.getAuthority().endsWith(Constants.HYPHEN+Constants.ASSOCIATE))?true:false):false;
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
		return email;
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
	public int getActive() {
		return active;
	}
	public Date getLastPasswordResetDate() {
		return lastPasswordResetDate;
	}
	public int getTeamId() {
		return teamId;
	}
	public RcmCompany getCompany() {
		return company;
	}
	public boolean isSmilePoint() {
		return isSmilePoint;
	}
	public String getEmail() {
		return email;
	}
	
	

	
	public boolean isTeamLead() {
		return isTeamLead;
	}
	public boolean isAssociate() {
		return isAssociate;
	}
    
}
