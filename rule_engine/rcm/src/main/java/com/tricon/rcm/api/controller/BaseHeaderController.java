package com.tricon.rcm.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;

import com.tricon.rcm.dto.PartialHeader;
import com.tricon.rcm.jpa.repository.RcmCompanyRepo;
import com.tricon.rcm.security.JwtUser;


public abstract class BaseHeaderController  {

	
	@Autowired
	RcmCompanyRepo rcmCompanyRepo;
	
	@Autowired
	@Qualifier("jwtUserDetailsService")
	private UserDetailsService userDetailsService;
	
	
	
	@ModelAttribute("headerInfo")
	public PartialHeader getDataHeaderHeaderInfo(@RequestHeader("c") String clientName,
			@RequestHeader("r") String role,@RequestHeader("t") int team) {
		 
        PartialHeader partialHeader = new PartialHeader();
        partialHeader.setClientUuid(clientName);
        partialHeader.setRole(role);
        partialHeader.setTeamId(team);
        partialHeader.setCompany(rcmCompanyRepo.findByUuid(clientName));
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		((UserDetails) principal).getUsername();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
		JwtUser jwtUser = (JwtUser) userDetails;
		partialHeader.setJwtUser(jwtUser);
        return partialHeader;
    }
}
