package com.tricon.ruleengine.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface GoogleReportService {
	
	public LinkedHashMap<String, List<String>> getESDataFromServer(String query,String ids,int columnCount,String office,
			String password);

}
