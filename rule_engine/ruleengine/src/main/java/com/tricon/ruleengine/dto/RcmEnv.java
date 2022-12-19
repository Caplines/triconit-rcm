package com.tricon.ruleengine.dto;

public class RcmEnv {

	String query;
	String queryCount;
	String querySelectcolumns;
	String apiKey;

	public RcmEnv(String query, String queryCount, String querySelectcolumns, String apiKey) {
		super();
		this.query = query;
		this.queryCount = queryCount;
		this.querySelectcolumns = querySelectcolumns;
		this.apiKey = apiKey;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getQuerySelectcolumns() {
		return querySelectcolumns;
	}

	public void setQuerySelectcolumns(String querySelectcolumns) {
		this.querySelectcolumns = querySelectcolumns;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getQueryCount() {
		return queryCount;
	}

	public void setQueryCount(String queryCount) {
		this.queryCount = queryCount;
	}

}
