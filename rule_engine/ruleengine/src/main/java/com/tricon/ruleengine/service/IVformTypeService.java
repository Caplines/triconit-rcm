package com.tricon.ruleengine.service;

import java.util.List;

import com.tricon.ruleengine.model.db.IVFormType;

public interface IVformTypeService {
	
	/**
	 * Get IV From by Name
	 * @param name
	 * @return
	 */
	public IVFormType getIVFormTypeByName(String name);
	
	/**
	 * Get All IV Forms
	 * @return List<IVFormType>
	 */
	public List<IVFormType> getAllIVFormType();

	public IVFormType getIVFormTypeById(int id);
}
