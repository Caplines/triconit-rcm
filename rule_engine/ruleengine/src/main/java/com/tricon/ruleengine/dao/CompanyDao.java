package com.tricon.ruleengine.dao;

import com.tricon.ruleengine.model.db.Company;

public interface CompanyDao {

	public Company getCompanyByName(String name);
	
	public Company getCompanyByUUId(String uuid);
}
