package com.tricon.ruleengine.dao;

import java.io.Serializable;
import java.util.List;

import com.tricon.ruleengine.model.db.GoogleSheets;
import com.tricon.ruleengine.model.db.Mappings;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.ReportDetail;
import com.tricon.ruleengine.model.db.Reports;
import com.tricon.ruleengine.model.db.Rules;

public interface TreatmentValidationDao {

	public GoogleSheets getSheetByAppSheetId(int id);

	public List<GoogleSheets> getAllGoogleSheet();

	public List<GoogleSheets> getAllGoogleSheetByOffice(Office off);
	
	public List<Mappings> getAllMappings();

	public List<Rules> getAllActiveRules();

	public Reports getReportsByTreamentPlanId(String treatmentPlanId);

	public Serializable saveReports(Reports reports);

	public Serializable saveReportDestail(ReportDetail reports);

	public Reports getReportsByName(String name);
	
	public void updateReportDate(Reports reports);

}
