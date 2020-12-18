package com.tricon.ruleengine.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tricon.ruleengine.dao.IVformTypeDao;
import com.tricon.ruleengine.model.db.IVFormType;
import com.tricon.ruleengine.service.IVformTypeService;

@Service
public class IVformTypeServiceImpl implements IVformTypeService {

	@Autowired
	IVformTypeDao iVformTypeDao;

	@Override
	public IVFormType getIVFormTypeByName(String name) {
		// TODO Auto-generated method stub
		return iVformTypeDao.getIVFormTypeByName(name);
	}

	@Override
	public List<IVFormType> getAllIVFormType() {
		// TODO Auto-generated method stub
		return iVformTypeDao.getAllIVFormType();
	}
	
	@Override
	public IVFormType getIVFormTypeById(int id) {
		// TODO Auto-generated method stub
		return iVformTypeDao.getIVFormTypeById(id);
	}

}
