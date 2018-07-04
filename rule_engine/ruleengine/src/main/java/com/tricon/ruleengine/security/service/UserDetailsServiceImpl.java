package com.tricon.ruleengine.security.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User.UserBuilder;
//import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tricon.ruleengine.dao.UserDao;
import com.tricon.ruleengine.model.db.User;
import com.tricon.ruleengine.model.db.UserRole;
 
@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {
 
    @Autowired
    private UserDao userDAO;
 
   
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
    	System.out.println("oooooooooooooooooooo");
    	System.out.println(userName);
       User appUser = userDAO.findUserByEmail(userName);
       System.out.println("sdfsfsfsfddff");
       UserBuilder builder = null;
        if (appUser == null) {
            System.out.println("User not found! " + userName);
            throw new UsernameNotFoundException("User " + userName + " was not found in the database");
        }
 
        System.out.println("Found User: " + appUser);
 
        //Set<UserRole> roles = appUser.getRoles();
        builder = org.springframework.security.core.userdetails.User.withUsername(userName);
        //builder.disabled(!appUser.isEnabled());
        builder.password(appUser.getPassword());
        String[] authorities = appUser.getRoles()
            .stream().map(a -> a.getRole()).toArray(String[]::new);

        builder.authorities(authorities);
        return builder.build();
    }
 
}
