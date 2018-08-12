package com.tricon.ruleengine.dao;

import java.util.List;
import java.util.Optional;

import com.tricon.ruleengine.dto.OfficeDto;
import com.tricon.ruleengine.model.db.Office;

public interface OfficeDao {
	
	/**
	 * Get all offices
	 * @return
	 */
	public Optional<List<OfficeDto>> getAllOffices();

	public Office getOfficeByUuid(String uuid);
}
