package com.tricon.ruleengine.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tricon.ruleengine.dao.OfficeDao;
import com.tricon.ruleengine.dao.TreatmentValidationDao;
import com.tricon.ruleengine.model.db.EagleSoftDBDetails;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.service.EagleSoftDBAccessService;
import com.tricon.ruleengine.service.GoogleReportService;

@Service
public class GoogleReportServiceImpl implements GoogleReportService{

	@Autowired
	OfficeDao od;

	@Autowired
	TreatmentValidationDao tvd;
	
	@Autowired
	EagleSoftDBAccessService es;
	
	@Override
    public Map<String, List<String>> getESDataFromServer(String query, String ids,int columnCount, String officeName,
    		String password) {
		// TODO Auto-generated method stub
		
		Office office= od.getOfficeByName(officeName);
		Map<String, List<String>> data=null;
		EagleSoftDBDetails esDB = tvd.getESDBDetailsByOffice(office);
		
		if (esDB!=null && esDB.getPassword().equals(password)) {
			data= es.getGoogleReportData(query, ids, columnCount, esDB, null);
			
		}

		return data;
	 //return null;
	}
   
}
