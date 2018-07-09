package com.tricon.ruleengine.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tricon.ruleengine.dto.UserRegistrationDto;
import com.tricon.ruleengine.service.UserService;

@RestController
@RequestMapping("admin")
public class AdminRestController {

	@Autowired
	private UserService userService;
    /**
     * in @PreAuthorize such as 'hasRole()' to determine if a user has access. Remember that the hasRole expression assumes a
     * 'ROLE_' prefix on all role names. So 'ADMIN' here is actually stored as 'ROLE_ADMIN' in database!
     **/
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getProtectedGreeting() {
        return ResponseEntity.ok("Greetings from admin protected method!");
    }
	@CrossOrigin
	@RequestMapping(value = "/register", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> registerUser(@RequestBody UserRegistrationDto dto) {
		return ResponseEntity.ok(userService.registerUser(dto));
	}

}