package com.tricon.rcm.security;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.tricon.rcm.db.entity.RcmUser;
import com.tricon.rcm.db.entity.RcmUserRole;



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
                user.getTeam().getId(),
                mapToGrantedAuthorities(user.getRoles()),
                user.getActive(),
                user.getLastPasswordResetDate(),
                user.getCompany()
        );
    }

    private static List<GrantedAuthority> mapToGrantedAuthorities(Set<RcmUserRole> authorities) {
        return authorities.stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getRole()))
                .collect(Collectors.toList());
    }
}
