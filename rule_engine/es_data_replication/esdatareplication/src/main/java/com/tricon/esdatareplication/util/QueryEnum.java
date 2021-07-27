package com.tricon.esdatareplication.util;


public enum QueryEnum {

	ES_CHAIRS("chairs", "chair_num,chair_name,sun_temp_id,"
			      + "mon_temp_id,tue_temp_id,wed_temp_id,thu_temp_id,"
			      + "fri_temp_id,sat_temp_id"
			      + " from chairs order by chair_num asc"," top "+Constants.QUERY_TOP_REP
			      + " start at "+Constants.QUERY_START_REP , false),

	ES_CHAIRS_COUNT("chairs", " count(*) from chairs "," ", false);

	private final String tableName;
	private final String query;
	private final String limit;
	private final boolean whereClause;

	private QueryEnum(String tableName, String query,String limit, boolean whereClause) {

		this.tableName = tableName;
		this.query = query;
		this.limit = limit;
		this.whereClause = whereClause;
		
	}

	public String getTableName() {
		return tableName;
	}

	public String getQuery() {
		return query;
	}

	public boolean isWhereClause() {
		return whereClause;
	}

	public String getLimit() {
		return limit;
	}

	
}
