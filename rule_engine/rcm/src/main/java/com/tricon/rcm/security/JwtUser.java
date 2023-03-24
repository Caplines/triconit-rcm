package com.tricon.rcm.security;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.tricon.rcm.db.entity.RcmCompany;
import com.tricon.rcm.db.entity.RcmTeam;
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
    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;
    private final int active;
    private final Date lastPasswordResetDate;
    private final List<RcmCompany> companies;
    private final List<RcmTeam> teams;
    
    private final boolean isSmilePoint;
    private final boolean isTeamLead;
    private final boolean isAssociate;
    
    private int teamId=-1;
    private RcmCompany company;
    private String companyName;//InHeader
    private String role;//In header
    

    public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public JwtUser(
          String uuid,
          String firstname,
          String lastname,
          String email,
          String password,
          Collection<? extends GrantedAuthority> authorities,
          int enabled,
          Date lastPasswordResetDate,
          List<RcmCompany> companies,
          List<RcmTeam> teams
          
    ) {
        this.uuid = uuid;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.active = enabled;
        this.lastPasswordResetDate = lastPasswordResetDate;
        this.companies =companies;
        this.teams =teams;
        boolean isSmilePoint=false;
        if (companies!=null) {
        	List<RcmCompany> filteredList=companies.stream()
        	.filter(cmp -> cmp.getName().equals(Constants.COMPANY_NAME))
  	      .collect(Collectors.toList());
        	if (filteredList!=null && filteredList.size() ==1 ) {
        		isSmilePoint=true;
        	}
        }
        this.isSmilePoint=isSmilePoint;
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

	public boolean isSmilePoint() {
		return isSmilePoint;
	}
	public String getEmail() {
		return email;
	}
	
	public List<RcmCompany> getCompanies() {
		return companies;
	}
	public List<RcmTeam> getTeams() {
		return teams;
	}
	public boolean isTeamLead() {
		return isTeamLead;
	}
	public boolean isAssociate() {
		return isAssociate;
	}
	public int getTeamId() {
		return teamId;
	}
	public void setTeamId(int teamId) {
		this.teamId = teamId;
	}
	public RcmCompany getCompany() {
		return company;
	}
	public void setCompany(RcmCompany company) {
		this.company = company;
	}

	
    
	
}
