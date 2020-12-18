package com.tricon.ruleengine.dao;

import java.util.List;

import com.tricon.ruleengine.model.db.IVFormType;

public interface IVformTypeDao {
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public IVFormType getIVFormTypeByName(String name);
	
	public List<IVFormType> getAllIVFormType();
	
	public IVFormType getIVFormTypeById(int id);
	
}
