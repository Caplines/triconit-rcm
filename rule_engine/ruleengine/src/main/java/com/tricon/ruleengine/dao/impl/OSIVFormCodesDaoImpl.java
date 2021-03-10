package com.tricon.ruleengine.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.tricon.ruleengine.dao.OSIVFormCodesDao;
import com.tricon.ruleengine.model.db.OSIVFormCodes;



@Repository
public class OSIVFormCodesDaoImpl extends BaseDaoImpl implements OSIVFormCodesDao{

	@Override
	public List<OSIVFormCodes> getAllActiveOSIVCodes() {
		// TODO Auto-generated method stub
		
		getEntitiesByColumnName(OSIVFormCodes.class, "active", 1);
		return null;
	}

}
