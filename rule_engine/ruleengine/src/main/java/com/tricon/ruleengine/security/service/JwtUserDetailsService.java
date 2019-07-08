package com.tricon.ruleengine.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import com.tricon.ruleengine.dao.UserDao;
import com.tricon.ruleengine.model.db.User;
import com.tricon.ruleengine.security.JwtUserFactory;

@Service
public class JwtUserDetailsService implements UserDetailsService {
//https://github.com/szerhusenBC/jwt-spring-security-demo
    @Autowired
    private UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //User user = userDao.findUserByEmail(username);
    	User user = userDao.findUserByUsername(username);//Start for here ...next Sprint ...

        if (user == null) {
            throw new UsernameNotFoundException(String.format("No user found with email '%s'.", username));
        } else {
            return JwtUserFactory.create(user);
        }
    }
}
