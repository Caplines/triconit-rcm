package com.tricon.ruleengine.api.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tricon.ruleengine.dto.PasswordResetDto;
import com.tricon.ruleengine.security.JwtTokenUtil;
import com.tricon.ruleengine.security.JwtUser;
import com.tricon.ruleengine.service.UserService;

@RestController
public class UserRestController {

	@Autowired
	private UserService userService;

	
    @Value("${jwt.header}")
    private String tokenHeader;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    @Qualifier("jwtUserDetailsService")
    private UserDetailsService userDetailsService;

    @RequestMapping(value = "user", method = RequestMethod.GET)
    public JwtUser getAuthenticatedUser(HttpServletRequest request) {
        String token = request.getHeader(tokenHeader).substring(7);
        String username = jwtTokenUtil.getUsernameFromToken(token);
        JwtUser user = (JwtUser) userDetailsService.loadUserByUsername(username);
        return user;
    }
    
    
	@CrossOrigin
	@RequestMapping(value = "/resetpassword", method = RequestMethod.POST)
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public ResponseEntity<?> resetpassword(@RequestBody PasswordResetDto dto) {
		return ResponseEntity.ok(userService.resetUserPassword(dto));
	}


}
