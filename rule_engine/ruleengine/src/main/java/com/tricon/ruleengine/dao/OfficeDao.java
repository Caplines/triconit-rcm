package com.tricon.ruleengine.dao;

import java.util.List;
import java.util.Optional;

import com.tricon.ruleengine.dto.OfficeDto;

public interface OfficeDao {
	
	/**
	 * Get all offices
	 * @return
	 */
	public Optional<List<OfficeDto>> getAllOffices();

}
