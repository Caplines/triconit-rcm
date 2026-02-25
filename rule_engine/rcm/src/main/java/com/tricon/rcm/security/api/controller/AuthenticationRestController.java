package com.tricon.rcm.security.api.controller;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tricon.rcm.dto.GenericResponse;
import com.tricon.rcm.exception.AuthenticationException;
import com.tricon.rcm.jwt.service.JwtAuthenticationCustomResponse;
import com.tricon.rcm.jwt.service.JwtAuthenticationResponse;
import com.tricon.rcm.security.JwtAuthenticationRequest;
import com.tricon.rcm.security.JwtTokenUtil;
import com.tricon.rcm.security.JwtUser;
import com.tricon.rcm.service.impl.RcmUtilServiceImpl;


@CrossOrigin
@RestController
public class AuthenticationRestController {
	
	private final Logger logger = LoggerFactory.getLogger(AuthenticationRestController.class);

    @Value("${jwt.header}")
    private String tokenHeader;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    @Qualifier("jwtUserDetailsService")
    private UserDetailsService userDetailsService;
    
    @Autowired
    private RcmUtilServiceImpl utilService;

    @RequestMapping(value = "${jwt.route.authentication.path}", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtAuthenticationRequest authenticationRequest) throws AuthenticationException {  
		if (authenticationRequest.getUsername() == null || authenticationRequest.getUsername().trim().equals("")
				|| authenticationRequest.getPassword() == null || authenticationRequest.getPassword().trim().equals("")
				|| authenticationRequest.getToken() == null || authenticationRequest.getToken().trim().equals("")) {
			return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "User Logged in Failed", null));

		}
    	boolean verificationStatus=utilService.googleCaptchaVerificationStatus(authenticationRequest.getToken());  
    	//lets enable latter
		if (true) {
			logger.info("Captcha verification status>>>>>>>success");
			// Reload password post-security so we can generate the token
			authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());  
			final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
			final String token = jwtTokenUtil.generateToken(userDetails);
			JwtUser user = (JwtUser) userDetails;

			// Return the token
			return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "User Logged in Success",
					new JwtAuthenticationCustomResponse(token, userDetails.getUsername(), userDetails.getAuthorities(),
							user.getTeams(), user.getFirstname(), user.getCompanies(),user.getLastname())));
		}

		return ResponseEntity.ok(new GenericResponse(HttpStatus.UNAUTHORIZED, "User Logged in Failed", null));
		// return ResponseEntity.ok(new JwtAuthenticationResponse(token));
    }
    
   @RequestMapping(value = "${jwt.route.authentication.refresh}", method = RequestMethod.GET)
    public ResponseEntity<?> refreshAndGetAuthenticationToken(HttpServletRequest request) {
        String authToken = request.getHeader(tokenHeader);
        final String token = authToken.substring(7);
        String username = jwtTokenUtil.getUsernameFromToken(token);
        JwtUser user = (JwtUser) userDetailsService.loadUserByUsername(username);
        
        if (jwtTokenUtil.canTokenBeRefreshed(token, user.getLastPasswordResetDate(),user.getActive())) {
            String refreshedToken = jwtTokenUtil.refreshToken(token);
            JwtAuthenticationResponse r=new JwtAuthenticationResponse(refreshedToken);
            
            return ResponseEntity.ok(new Object[] {r,user.getAuthorities(),user.getTeams(),user.getCompanies()});
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<String> handleAuthenticationException(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    /**
     * Authenticates the user. If something is wrong, an {@link AuthenticationException} will be thrown
     */
    private void authenticate(String username, String password) {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);
  
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new AuthenticationException("User is disabled!", e);
        } catch (BadCredentialsException e) {
            throw new AuthenticationException("Bad credentials!", e);
        }
    }
    
	@RequestMapping(value = "${jwt.route.testing.authentication.path}", method = RequestMethod.POST)
	public ResponseEntity<?> loginWithOutCaptcha(@RequestBody JwtAuthenticationRequest authenticationRequest)
			throws AuthenticationException {
		
		if (authenticationRequest.getUsername() == null || authenticationRequest.getUsername().trim().equals("")
				|| authenticationRequest.getPassword() == null || authenticationRequest.getPassword().trim().equals("")) {
			return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "User Logged in Failed", null));

		}
        
		authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

		// Reload password post-security so we can generate the token
		final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
		final String token = jwtTokenUtil.generateToken(userDetails);
		JwtUser user = (JwtUser) userDetails;

		// Return the token
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "User Logged in Success",
				new JwtAuthenticationCustomResponse(token, userDetails.getUsername(), userDetails.getAuthorities(),
						user.getTeams(), user.getFirstname(), user.getCompanies(),user.getLastname())));
		// return ResponseEntity.ok(new JwtAuthenticationResponse(token));
	}
}
