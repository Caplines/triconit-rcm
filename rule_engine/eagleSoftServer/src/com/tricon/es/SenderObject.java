package com.tricon.es;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class serves as outgoing JSON response to Rule Engine 
 * @author Deepak.Dogra
 *
 */
public class SenderObject {

	
	private Map<String,List<String>> dataMap= new  HashMap<>();

	public Map<String, List<String>> getDataMap() {
		return dataMap;
	}

	public void setDataMap(Map<String, List<String>> dataMap) {
		this.dataMap = dataMap;
	}


	

	
	
	
}
