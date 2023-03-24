package com.tricon.rcm.security;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.tricon.rcm.db.entity.RcmCompany;
import com.tricon.rcm.db.entity.RcmTeam;
import com.tricon.rcm.db.entity.RcmUser;
import com.tricon.rcm.db.entity.RcmUserCompany;
import com.tricon.rcm.db.entity.RcmUserRole;
import com.tricon.rcm.db.entity.RcmUserTeam;



/**
 * @author Deepak.Dogra
 *
 */
public final class JwtUserFactory {

    private JwtUserFactory() {
    }

    public static JwtUser create(RcmUser user) {
    	
        return new JwtUser(
                user.getUuid(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPassword(),
                mapToGrantedAuthorities(user.getRoles()),
                user.getActive(),
                user.getLastPasswordResetDate(),
                mapToRcmCompany(user.getRcmCompanies()),
                mapToRcmTeam(user.getRcmTeams())

        );
    }

    private static List<GrantedAuthority> mapToGrantedAuthorities(Set<RcmUserRole> authorities) {
        return authorities.stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getRole()))
                .collect(Collectors.toList());
    }
    
    private static List<RcmTeam> mapToRcmTeam(Set<RcmUserTeam> teams) {
    	
        return teams.stream()
                .map(team -> new RcmTeam(team.getTeam().getId(),team.getTeam().getName(),team.getTeam().getNameId(), team.getTeam().getDescription(), team.getTeam().getActive()))
                .collect(Collectors.toList());
    }
    
private static List<RcmCompany> mapToRcmCompany(Set<RcmUserCompany> companies) {
    	
        return companies.stream()
                .map(company -> new RcmCompany(company.getCompany().getUuid(),company.getCompany().getName()))
                .collect(Collectors.toList());
    }
}
