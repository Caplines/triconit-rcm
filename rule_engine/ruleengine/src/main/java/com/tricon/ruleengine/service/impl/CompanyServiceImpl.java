package com.tricon.ruleengine.service.impl;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tricon.ruleengine.dao.CompanyDao;
import com.tricon.ruleengine.model.db.Company;
import com.tricon.ruleengine.service.CompanyService;

@Transactional
@Service
public class CompanyServiceImpl implements CompanyService{

	@Autowired
	CompanyDao companyDao;
	
	@Override
	public Company getCompanyByName(String name) {
		// TODO Auto-generated method stub
		return  companyDao.getCompanyByName(name);
	}

}
