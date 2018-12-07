package com.tricon.ruleengine.utils;


/**
 * 
 * @author Deepak.Dogra
 *
 */
public class EagleSoftQueryObject {
	
	String ids;

	String query;

	int columnCount;

	int prepStCount;

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public int getColumnCount() {
		return columnCount;
	}

	public void setColumnCount(int columnCount) {
		this.columnCount = columnCount;
	}

	public int getPrepStCount() {
		return prepStCount;
	}

	public void setPrepStCount(int prepStCount) {
		this.prepStCount = prepStCount;
	}
	
	
	

}
