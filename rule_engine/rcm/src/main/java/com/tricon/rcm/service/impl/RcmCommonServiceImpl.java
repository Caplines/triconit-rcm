package com.tricon.rcm.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tricon.rcm.db.entity.RcmCompany;
import com.tricon.rcm.db.entity.RcmOffice;
import com.tricon.rcm.dto.RcmOfficeDto;
import com.tricon.rcm.jpa.repository.RcmCompanyRepo;
import com.tricon.rcm.jpa.repository.RcmOfficeRepository;
import com.tricon.rcm.util.Constants;

@Service
public class RcmCommonServiceImpl {
	
	@Autowired
	RcmCompanyRepo rcmCompanyRepo;
	
	@Autowired
	RcmOfficeRepository rcmOfficeRepository;
	
	
	public List<RcmOfficeDto> getAllOffices(){
		
	 RcmCompany	company =rcmCompanyRepo.findByName(Constants.COMPANY_NAME);
	 
	 return rcmOfficeRepository.findByCompany(company);
		
		
		
	}

}
