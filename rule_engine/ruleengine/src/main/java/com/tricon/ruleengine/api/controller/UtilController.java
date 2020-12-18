package com.tricon.ruleengine.api.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tricon.ruleengine.dao.CompanyDao;
import com.tricon.ruleengine.dao.IVformTypeDao;
import com.tricon.ruleengine.dto.GenericResponse;
import com.tricon.ruleengine.dto.OfficeDto;
import com.tricon.ruleengine.dto.OfficesAndIVForms;
import com.tricon.ruleengine.model.db.Company;
import com.tricon.ruleengine.model.db.IVFormType;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.service.IVformTypeService;
import com.tricon.ruleengine.service.UserService;
import com.tricon.ruleengine.utils.Constants;

@Controller
public class UtilController {

	@Autowired
	private UserService userService;
	
	@Autowired
	CompanyDao companyDao;
	
	
	@Autowired
	IVformTypeService iVformTypeService;

	
	@CrossOrigin
	@RequestMapping(value = "/open/getoffices", method = RequestMethod.GET)
	public ResponseEntity<?> getAlloffifceCap() {
		
		
		Company cmp = companyDao.getCompanyByName(Constants.COMPANY_NAME);
		Optional<List<OfficeDto>> offices = userService.getAllOffices(cmp.getUuid());
		if (offices.isPresent() && offices.get() != null) {
			return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", offices.get()));
		}

		else
			return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", ""));

	}

	@CrossOrigin
	@RequestMapping(value = "/open/getofficesandFortypecap", method = RequestMethod.GET)
	public ResponseEntity<?> getAlloffifceCapAndForType() {
		
		
		Company cmp = companyDao.getCompanyByName(Constants.COMPANY_NAME);
		OfficesAndIVForms f= new OfficesAndIVForms();
		f.setIvforms(iVformTypeService.getAllIVFormType());
		Optional<List<OfficeDto>> offices = userService.getAllOffices(cmp.getUuid());
		if (offices.isPresent() && offices.get() != null) {
			f.setOffices(offices.get());
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", f));
		//	return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "", ""));

	}
}
