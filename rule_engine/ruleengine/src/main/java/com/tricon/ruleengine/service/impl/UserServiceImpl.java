package com.tricon.ruleengine.service.impl;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tricon.ruleengine.dao.OfficeDao;
import com.tricon.ruleengine.dao.UserDao;
import com.tricon.ruleengine.dto.GenericResponse;
import com.tricon.ruleengine.dto.OfficeDto;
import com.tricon.ruleengine.dto.UserRegistrationDto;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.User;
import com.tricon.ruleengine.model.db.UserRole;
import com.tricon.ruleengine.service.UserService;
import com.tricon.ruleengine.utils.Constants;
import com.tricon.ruleengine.utils.DtoToModel;

/**
 * @author Deepak.Dogra
 *
 */
@Transactional
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserDao userDao;
	@Autowired
	OfficeDao officeDao;

	@Override
	public GenericResponse registerUser(UserRegistrationDto dto) {
		Office office = userDao.findOfficeById(dto.getOfficeId());
		if (office != null) {
			User user = userDao.findUserByEmail(dto.getEmail());
			if (user == null) {
				user = DtoToModel.convertRegistrationDto(dto, office);
				user.setUuid((String) userDao.registerUser(user));
				UserRole role = new UserRole();
				role.setRole(Constants.APP_ROLE_USER);
				role.setUser(user);
				userDao.generateUserRole(role);
				return new GenericResponse(HttpStatus.OK, "User Created Successfully", null);
			}
			return new GenericResponse(HttpStatus.BAD_REQUEST, "User Already Exists", null);
		}

		return new GenericResponse(HttpStatus.BAD_REQUEST, "In correct Office name", null);
	}

	@Override
	public Optional<List<OfficeDto>> getAllOffices() {
		return officeDao.getAllOffices();
	}

}
