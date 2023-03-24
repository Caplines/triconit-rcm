package com.tricon.rcm.jwt.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import com.tricon.rcm.db.entity.RcmCompany;
import com.tricon.rcm.db.entity.RcmTeam;


/**
 * @author Deepak.Dogra
 *
 */
public class JwtAuthenticationCustomResponse implements Serializable {


	
	private static final long serialVersionUID = 7359399371708955717L;

	private String token;

	private String userName;
	
	//private int teamId;
	
	private String firstName;
	
	//private String clientName;
	
	
	private List<RcmTeam> teams;
	
	private List<RcmCompany> companies;
	
	
	Collection<? extends GrantedAuthority> authorities;
	
	
    public JwtAuthenticationCustomResponse(String token,
    		String userName ,
    		Collection<? extends GrantedAuthority>  authorities,
    		List<RcmTeam> teams,String firstName,List<RcmCompany> companies) {
        this.token = token;
        this.userName = userName;
        this.authorities = authorities;
       // this.teamId = teamId;
        this.firstName=firstName;
        //this.clientName=clientName;
        this.teams=teams;
        this.companies=companies;
        
    }
	
	public String getToken() {
		return token;
	}

	public String getUserName() {
		return userName;
	}

	/*public int getTeamId() {
		return teamId;
	}*/

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public String getFirstName() {
		return firstName;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public List<RcmTeam> getTeams() {
		return teams;
	}

	public List<RcmCompany> getCompanies() {
		return companies;
	}
	
	/*public String getClientName() {
		return clientName;
	}*/
	

	
	
}
