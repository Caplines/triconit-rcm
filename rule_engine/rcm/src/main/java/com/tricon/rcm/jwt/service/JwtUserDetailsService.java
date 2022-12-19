package com.tricon.rcm.jwt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tricon.rcm.db.entity.RcmUser;
import com.tricon.rcm.jpa.repository.RCMUserRepository;
import com.tricon.rcm.security.JwtUserFactory;

@Service
public class JwtUserDetailsService implements UserDetailsService {
	// https://github.com/szerhusenBC/jwt-spring-security-demo
	@Autowired
	RCMUserRepository rcmUserRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		RcmUser user = rcmUserRepository.findByUserName(username);

		if (user == null) {
			throw new UsernameNotFoundException(String.format("No user found with email '%s'.", username));
		} else {
			return JwtUserFactory.create(user);
		}
	}
}
