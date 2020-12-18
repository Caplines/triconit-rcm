package com.tricon.ruleengine.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.tricon.ruleengine.dao.IVformTypeDao;
import com.tricon.ruleengine.model.db.IVFormType;

@Repository
public class IVformTypeDaoImpl extends BaseDaoImpl implements IVformTypeDao {

	@Override
	public IVFormType getIVFormTypeByName(String name) {
		return (IVFormType)getEntityByColumnName(IVFormType.class, "name", name);
	}

	@Override
	public List<IVFormType> getAllIVFormType() {
		return (List<IVFormType>) (List<?>) getWholeEntity(IVFormType.class);
	}

	@Override
	public IVFormType getIVFormTypeById(int id) {
		return (IVFormType)getEntityByColumnName(IVFormType.class, "id", id);
	}

	
	
}
