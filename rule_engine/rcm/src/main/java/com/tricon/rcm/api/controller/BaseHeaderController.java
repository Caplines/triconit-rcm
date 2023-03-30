package com.tricon.rcm.api.controller;

import org.apache.tomcat.util.bcel.classfile.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;

import com.tricon.rcm.db.entity.RcmCompany;
import com.tricon.rcm.dto.PartialHeader;
import com.tricon.rcm.enums.RcmTeamEnum;
import com.tricon.rcm.jpa.repository.RcmCompanyRepo;
import com.tricon.rcm.security.JwtUser;
import com.tricon.rcm.service.impl.RcmCommonServiceImpl;
import com.tricon.rcm.util.Constants;

public abstract class BaseHeaderController  {

	
	@Autowired
	RcmCompanyRepo rcmCompanyRepo;
	
	@Autowired
	RcmCommonServiceImpl commonService;
	
	@Autowired
	@Qualifier("jwtUserDetailsService")
	private UserDetailsService userDetailsService;
	
	
	
	@ModelAttribute("headerInfo")
	public PartialHeader getDataHeaderHeaderInfo(@RequestHeader("c") String clientName,
			@RequestHeader("r") String role,@RequestHeader("t") int team) {
		 
        PartialHeader partialHeader = new PartialHeader();
        RcmCompany company=rcmCompanyRepo.findByName(clientName);
        partialHeader.setClientUuid(clientName);
        partialHeader.setRole(role);
        partialHeader.setTeamId(team);
        partialHeader.setCompany(company);
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		((UserDetails) principal).getUsername();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
		JwtUser jwtUser = (JwtUser) userDetails;
		partialHeader.setJwtUser(jwtUser);
		if (commonService.isSuperAdmin(role,jwtUser))
			return partialHeader;
		if (company == null)
			return null;
		if (company != null) {		
			if(!commonService.checkIfSessionClientValid(company.getUuid(), jwtUser.getCompanies())) return null;
			if (!commonService.checkIfRolesValidFromJWT(role, jwtUser))return null;
			if (!commonService.checkIfSessionTeamValid(team, jwtUser.getTeams()) && (!role.equals(Constants.ADMIN)
					||!role.equals(Constants.REPORTING)))
				return null;
		}
		return partialHeader;
	}
}
