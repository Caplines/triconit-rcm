package com.tricon.ruleengine.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.tricon.ruleengine.dao.TreatmentValidationDao;
import com.tricon.ruleengine.model.db.GoogleSheets;
import com.tricon.ruleengine.model.db.Rules;

/**
 * @author Deepak.Dogra
 *
 */
@Repository
public class TreatmentValidationDaoImpl extends BaseDaoImpl implements TreatmentValidationDao {

	@Override
	public GoogleSheets getSheetByAppSheetId(int id) {
	 return (GoogleSheets) getEntityById(GoogleSheets.class, id);
	}

	@Override
	public List<Rules> getAllActiveRules() {
		
	 List<Rules> list = (List<Rules>)(List<?>)getEntitiesByColumnName(Rules.class, "active", 1);	
	 return list;
	}
}
