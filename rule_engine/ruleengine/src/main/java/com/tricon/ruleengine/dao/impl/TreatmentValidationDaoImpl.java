package com.tricon.ruleengine.dao.impl;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.tricon.ruleengine.dao.TreatmentValidationDao;
import com.tricon.ruleengine.model.db.GoogleSheets;
import com.tricon.ruleengine.model.db.ReportDetail;
import com.tricon.ruleengine.model.db.Reports;
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

	@Override
	public Reports getReportsByTreamentPlanId(String treatmentPlanId) {

		return (Reports) getEntityByColumnName(Reports.class, "treatementPlanId", treatmentPlanId);
	}

	@Override
	public Serializable saveReports(Reports reports) {
		// TODO Auto-generated method stub
		return saveEntiy(reports);
	}

	@Override
	public Reports getReportsByName(String name) {
	 return (Reports) getEntityById(GoogleSheets.class, name);
	}

	@Override
	public Serializable saveReportDestail(ReportDetail reports) {
		// TODO Auto-generated method stub
		return saveEntiy(reports);
	}

	@Override
	public void updateReportDate(Reports reports) {
		
		updateEntiyDate(reports);
		
		
	}

	@Override
	public List<GoogleSheets> getAllGoogleSheet() {
		List<GoogleSheets> list = (List<GoogleSheets>)(List<?>) getWholeEntity(GoogleSheets.class);
		return list;
	}
}
