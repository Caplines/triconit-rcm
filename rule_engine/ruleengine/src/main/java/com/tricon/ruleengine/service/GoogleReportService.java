package com.tricon.ruleengine.service;

import java.util.List;
import java.util.Map;

public interface GoogleReportService {
	
	public Map<String, List<String>> getESDataFromServer(String query,String ids,int columnCount,String office,
			String password);

}
