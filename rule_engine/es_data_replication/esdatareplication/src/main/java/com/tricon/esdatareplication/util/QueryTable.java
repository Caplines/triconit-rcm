package com.tricon.esdatareplication.util;

import com.tricon.esdatareplication.entity.repdb.Chairs;
import com.tricon.esdatareplication.entity.repdb.Patient;
import com.tricon.esdatareplication.entity.repdb.PayType;

public class QueryTable {

	private QueryEnum query;

	public enum QueryEnum {

		ES_CHAIRS("chairs",
				"chair_num,chair_name,sun_temp_id," + "mon_temp_id,tue_temp_id,wed_temp_id,thu_temp_id,"
						+ "fri_temp_id,sat_temp_id,practice_id" + " from chairs order by chair_num asc",
				" top " + Constants.QUERY_TOP_REP + " start at " + Constants.QUERY_START_REP, false, true,
				Chairs.class),

		ES_CHAIRS_COUNT("chairs", " count(*) from chairs ", " ", false, false, Chairs.class),

		ES_PAYTYPE("paytype", "paytype_id,sequence,description,prompt,display_on_payment_screen,currency_type,"
				+ "include_on_deposit_yn," + "central_id,system_required" + " from paytype order by paytype_id asc",
				" top " + Constants.QUERY_TOP_REP + " start at " + Constants.QUERY_START_REP, false, true,
				PayType.class),

		ES_PAYTYPE_COUNT("paytype", " count(*) from paytype ", " ", false, false, PayType.class),

		ES_PATIENT("patient", Constants.PATIENTS_COLUMNS + " from patient order by date_entered asc ",
				" top " + Constants.QUERY_TOP_REP + " start at " + Constants.QUERY_START_REP, false, true,
				Patient.class),

		ES_PATIENT_COUNT("patient", " count(*) from patient ", " ", false, false, Patient.class),

		ES_PATIENT_NEXT("patient",
				Constants.PATIENTS_COLUMNS + " from patient where " + Constants.QUERY_WHERE_CLAUSE_REP
						+ " order by patient_id asc",
				" top " + Constants.QUERY_TOP_REP + " start at " + Constants.QUERY_START_REP, true, true,
				Patient.class),

		ES_PATIENT_NEXT_COUNT("patient", " count(*) from patient  where " + Constants.QUERY_WHERE_CLAUSE_REP, " ",
				true, false, Patient.class);

		private final String tableName;
		private final String query;
		private final String limit;
		private final boolean whereClause;
		private final boolean totalCountNeeded;
		private final Class<?> clazz;

		private QueryEnum(String tableName, String query, String limit, boolean whereClause, boolean totalCountNeeded,
				Class<?> clazz) {

			this.tableName = tableName;
			this.query = query;
			this.limit = limit;
			this.whereClause = whereClause;
			this.totalCountNeeded = totalCountNeeded;
			this.clazz = clazz;
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

		public boolean isTotalCountNeeded() {
			return totalCountNeeded;
		}

		public Class<?> getClazz() {
			return clazz;
		}

	}

	public QueryEnum getQuery() {
		return query;
	}

	public void setQuery(QueryEnum query) {
		this.query = query;
	}

}
