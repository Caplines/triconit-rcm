package com.tricon.es;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * This class serves as incoming JSON request from Eagle Soft 
 * @author Deepak.Dogra
 *
 */
@JsonPropertyOrder({ "ids", "query", "columncount","prepstcount" })
public class RuleEngineQuery {

	@JsonProperty("ids")
	String ids;

	@JsonProperty("query")
	String query;

	@JsonProperty("columncount")
	int columnCount;


	@JsonProperty("prepstcount")
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
