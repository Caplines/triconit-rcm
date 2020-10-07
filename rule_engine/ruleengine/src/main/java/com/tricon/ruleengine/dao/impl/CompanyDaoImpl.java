package com.tricon.ruleengine.dao.impl;

import org.springframework.stereotype.Repository;

import com.tricon.ruleengine.dao.CompanyDao;
import com.tricon.ruleengine.model.db.Company;

@Repository
public class CompanyDaoImpl extends BaseDaoImpl implements CompanyDao{

	@Override
	public Company getCompanyByName(String name) {
		// TODO Auto-generated method stub
		return (Company)getEntityByColumnName(Company.class, "name", name);
	}

	@Override
	public Company getCompanyByUUId(String uuid) {
		// TODO Auto-generated method stub
		return (Company)getEntityByColumnName(Company.class, "uuid", uuid);
	}

}
