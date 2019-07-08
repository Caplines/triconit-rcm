package com.tricon.ruleengine.dao;

import java.io.Serializable;
import java.util.List;

import com.tricon.ruleengine.model.db.EagleSoftDBDetails;
import com.tricon.ruleengine.model.db.GoogleSheets;
import com.tricon.ruleengine.model.db.MVPandVAP;
import com.tricon.ruleengine.model.db.Mappings;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.ReportDetail;
import com.tricon.ruleengine.model.db.Reports;
import com.tricon.ruleengine.model.db.ReportsClaim;
import com.tricon.ruleengine.model.db.Rules;

public interface TreatmentValidationDao {

	public GoogleSheets getSheetByAppSheetId(int id);

	public List<GoogleSheets> getAllGoogleSheet();

	public List<GoogleSheets> getAllGoogleSheetByOffice(Office off);
	
	public List<Mappings> getAllMappings();

	public List<Rules> getAllActiveRules();

	public Reports getReportsByTreamentPlanIdAndOffice(String treatmentPlanId,Office office);
	
	public Reports getReportsByIVFIdAndOffice(String ivfId,Office off);

	public Reports getReportsByTPIdIVFIDAndOffice(String id,String ivfId,Office office);
	
	public ReportsClaim getReportsClaimByTPIdIVFIDAndOffice(String id,String ivfId,Office office);


	public Serializable saveReports(Object reports);

	public Serializable saveReportDestail(Object reports);

	public Reports getReportsByName(String name);
	
	public void updateReportDate(Object reports);
	
	public EagleSoftDBDetails getESDBDetailsByOffice(Office off);
	
	public List<MVPandVAP> getAllMVPVAP();
	

}
