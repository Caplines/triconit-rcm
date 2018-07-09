package com.tricon.ruleengine.utils;

import com.tricon.ruleengine.dto.UserRegistrationDto;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.User;

public class DtoToModel {
	
	public static User convertRegistrationDto(UserRegistrationDto dto,Office off) {
		
		User us =new User();
		us.setEmail(dto.getEmail());
		us.setName(dto.getFirstName());
		us.setLastName(dto.getLastName());
		us.setOffice(off);
		us.setActive(1);
		us.setCreatedBy(us);
		us.setPassword(EncrytedKeyUtil.encryptKey(dto.getPassword()));
		return us;
		
	}

}
