package com.tricon.esdatareplication.util;

import com.tricon.esdatareplication.entity.repdb.Chairs;
import com.tricon.esdatareplication.entity.repdb.Patient;
import com.tricon.esdatareplication.entity.repdb.PayType;
import com.tricon.esdatareplication.entity.repdb.Appointment;
import com.tricon.esdatareplication.entity.repdb.Transactions;
import com.tricon.esdatareplication.entity.repdb.TransactionsDetail;
import com.tricon.esdatareplication.entity.repdb.PaymentProvider;
import com.tricon.esdatareplication.entity.repdb.PlannedServices;
import com.tricon.esdatareplication.entity.repdb.TreatmentPlanItems;
import com.tricon.esdatareplication.entity.repdb.TreatmentPlans;
import com.tricon.esdatareplication.entity.repdb.Provider;
import com.tricon.esdatareplication.entity.repdb.Employer;



public class QueryTable {

	private QueryEnum query;

	public enum QueryEnum {

		ES_CHAIRS(Constants.TABLE_CHAIRS,
				"chair_num,chair_name,sun_temp_id," + "mon_temp_id,tue_temp_id,wed_temp_id,thu_temp_id,"
						+ "fri_temp_id,sat_temp_id,practice_id" + " from " + Constants.TABLE_CHAIRS
						+ " order by chair_num asc",
				" top " + Constants.QUERY_TOP_REP + " start at " + Constants.QUERY_START_REP, false, true,
				Chairs.class),

		ES_CHAIRS_COUNT(Constants.TABLE_CHAIRS, " count(*) from " + Constants.TABLE_CHAIRS + " ", " ", false, false,
				Chairs.class),

		ES_PAYTYPE(Constants.TABLE_PAYTYPE,
				"paytype_id,sequence,description,prompt,display_on_payment_screen,currency_type,"
						+ "include_on_deposit_yn," + "central_id,system_required" + " from " + Constants.TABLE_PAYTYPE
						+ " order by paytype_id asc",
				" top " + Constants.QUERY_TOP_REP + " start at " + Constants.QUERY_START_REP, false, true,
				PayType.class),

		ES_PAYTYPE_COUNT(Constants.TABLE_PAYTYPE, " count(*) from " + Constants.TABLE_PAYTYPE + " ", " ", false, false,
				PayType.class),

		/*
		 * Patient Start
		 */
		ES_PATIENT(Constants.TABLE_PATIENT,
				Constants.PATIENTS_COLUMNS + " from " + Constants.TABLE_PATIENT + " order by date_entered asc ",
				" top " + Constants.QUERY_TOP_REP + " start at " + Constants.QUERY_START_REP, false, true,
				Patient.class),

		ES_PATIENT_COUNT(Constants.TABLE_PATIENT, " count(*) from " + Constants.TABLE_PATIENT + " ", " ", false, false,
				Patient.class),

		ES_PATIENT_NEXT(Constants.TABLE_PATIENT,
				Constants.PATIENTS_COLUMNS + " from " + Constants.TABLE_PATIENT + " where "
						+ Constants.QUERY_WHERE_CLAUSE_REP + " order by patient_id asc",
				" top " + Constants.QUERY_TOP_REP + " start at " + Constants.QUERY_START_REP, true, true,
				Patient.class),

		ES_PATIENT_NEXT_COUNT(Constants.TABLE_PATIENT,
				" count(*) from " + Constants.TABLE_PATIENT + "  where " + Constants.QUERY_WHERE_CLAUSE_REP, " ", true,
				false, Patient.class),

		/*
		 * Patient End
		 */

		/*
		 * Appointment Start
		 */

		ES_APPOINTMENT(Constants.TABLE_APPOINTMENT,
				Constants.PATIENTS_COLUMNS + " from " + Constants.TABLE_APPOINTMENT + " order by appointment_id asc ",
				" top " + Constants.QUERY_TOP_REP + " start at " + Constants.QUERY_START_REP, false, true,
				Appointment.class),

		ES_APPOINTMENT_COUNT(Constants.TABLE_APPOINTMENT, " count(*) from " + Constants.TABLE_APPOINTMENT + " ", " ",
				false, false, Appointment.class),

		ES_APPOINTMENT_NEXT(Constants.TABLE_APPOINTMENT,
				Constants.APPOINTMENT_COLUMNS + " from " + Constants.TABLE_APPOINTMENT + " where "
						+ Constants.QUERY_WHERE_CLAUSE_REP + " order by start_time asc",
				" top " + Constants.QUERY_TOP_REP + " start at " + Constants.QUERY_START_REP, true, true,
				Appointment.class),

		ES_APPOINTMENT_NEXT_COUNT(Constants.TABLE_APPOINTMENT,
				" count(*) from " + Constants.TABLE_APPOINTMENT + "  where " + Constants.QUERY_WHERE_CLAUSE_REP, " ",
				true, false, Appointment.class),

		/*
		 * Appointment End
		 */

		/*
		 * Transaction Start
		 */

		ES_TRANSACTIONS(Constants.TABLE_TRANSACTIONS,
				Constants.TRANSACTIONS_COLUMNS + " from " + Constants.TABLE_TRANSACTIONS + " order by tran_date asc ",
				" top " + Constants.QUERY_TOP_REP + " start at " + Constants.QUERY_START_REP, false, true,
				Transactions.class),

		ES_TRANSACTIONS_COUNT(Constants.TABLE_TRANSACTIONS, " count(*) from " + Constants.TABLE_TRANSACTIONS + " ", " ",
				false, false, Transactions.class),

		ES_TRANSACTIONS_NEXT(Constants.TABLE_TRANSACTIONS,
				Constants.TRANSACTIONS_COLUMNS + " from " + Constants.TABLE_TRANSACTIONS + " where "
						+ Constants.QUERY_WHERE_CLAUSE_REP + " order by tran_date asc",
				" top " + Constants.QUERY_TOP_REP + " start at " + Constants.QUERY_START_REP, true, true,
				Transactions.class),

		ES_TRANSACTIONS_NEXT_COUNT(Constants.TABLE_TRANSACTIONS,
				" count(*) from " + Constants.TABLE_TRANSACTIONS + "  where " + Constants.QUERY_WHERE_CLAUSE_REP, " ",
				true, false, Transactions.class),

		/*
		 * Transaction End
		 */

		/*
		 * Transactions Detail Start
		 */

		ES_TABLE_TRANSACTIONS_DETAIL(Constants.TABLE_TRANSACTIONS_DETAIL,
				Constants.TRANSACTIONSDETAILS_COLUMNS + " from " + Constants.TABLE_TRANSACTIONS_DETAIL
						+ " order by detail_id asc ",
				" top " + Constants.QUERY_TOP_REP + " start at " + Constants.QUERY_START_REP, false, true,
				TransactionsDetail.class),

		ES_TABLE_TRANSACTIONS_DETAIL_COUNT(Constants.TABLE_TRANSACTIONS_DETAIL,
				" count(*) from " + Constants.TABLE_TRANSACTIONS_DETAIL + " ", " ", false, false,
				TransactionsDetail.class),

		ES_TABLE_TRANSACTIONS_DETAIL_NEXT(Constants.TABLE_TRANSACTIONS_DETAIL,
				Constants.TRANSACTIONSDETAILS_COLUMNS + " from " + Constants.TABLE_TRANSACTIONS_DETAIL + " where "
						+ Constants.QUERY_WHERE_CLAUSE_REP + " order by detail_id asc",
				" top " + Constants.QUERY_TOP_REP + " start at " + Constants.QUERY_START_REP, true, true,
				TransactionsDetail.class),

		ES_TABLE_TRANSACTIONS_DETAIL_NEXT_COUNT(Constants.TABLE_TRANSACTIONS_DETAIL,
				" count(*) from " + Constants.TABLE_TRANSACTIONS_DETAIL + "  where " + Constants.QUERY_WHERE_CLAUSE_REP,
				" ", true, false, TransactionsDetail.class),

		/*
		 * Transactions Detail End
		 */

		/*
		 * Payment Provider Start
		 */

		ES_TABLE_PAYMENT_PROVIDER_DETAIL(Constants.TABLE_PAYMENT_PROVIDER,
				Constants.PAYMENTPROVIDER_COLUMNS + " from " + Constants.TABLE_PAYMENT_PROVIDER
						+ " order by tran_num asc ",
				" top " + Constants.QUERY_TOP_REP + " start at " + Constants.QUERY_START_REP, false, true,
				PaymentProvider.class),

		ES_TABLE_PAYMENT_PROVIDER_COUNT(Constants.TABLE_PAYMENT_PROVIDER,
				" count(*) from " + Constants.TABLE_PAYMENT_PROVIDER + " ", " ", false, false, PaymentProvider.class),

		ES_TABLE_PAYMENT_PROVIDER_NEXT(Constants.TABLE_PAYMENT_PROVIDER,
				Constants.PAYMENTPROVIDER_COLUMNS + " from " + Constants.TABLE_PAYMENT_PROVIDER + " where "
						+ Constants.QUERY_WHERE_CLAUSE_REP + " order by tran_num asc",
				" top " + Constants.QUERY_TOP_REP + " start at " + Constants.QUERY_START_REP, true, true,
				PaymentProvider.class),

		ES_TABLE_PAYMENT_PROVIDER_NEXT_COUNT(Constants.TABLE_PAYMENT_PROVIDER,
				" count(*) from " + Constants.TABLE_PAYMENT_PROVIDER + "  where " + Constants.QUERY_WHERE_CLAUSE_REP,
				" ", true, false, PaymentProvider.class),

		/*
		 * Payment Provider End
		 */

		/*
		 * Planned Services Start
		 */

		ES_TABLE_PLANNED_SERVICES(Constants.TABLE_PLANNED_SERVICES,
				Constants.PLANNEDSERVICE_COLUMNS + " from " + Constants.TABLE_PLANNED_SERVICES
						+ " order by date_planned asc ",
				" top " + Constants.QUERY_TOP_REP + " start at " + Constants.QUERY_START_REP, false, true,
				PlannedServices.class),

		ES_TABLE_PLANNED_SERVICES_COUNT(Constants.TABLE_PLANNED_SERVICES,
				" count(*) from " + Constants.TABLE_PLANNED_SERVICES + " ", " ", false, false, PlannedServices.class),

		ES_TABLE_PLANNED_SERVICES_NEXT(Constants.TABLE_PLANNED_SERVICES,
				Constants.PLANNEDSERVICE_COLUMNS + " from " + Constants.TABLE_PLANNED_SERVICES + " where "
						+ Constants.QUERY_WHERE_CLAUSE_REP + " order by date_planned asc",
				" top " + Constants.QUERY_TOP_REP + " start at " + Constants.QUERY_START_REP, true, true,
				PlannedServices.class),

		ES_TABLE_PLANNED_SERVICES_NEXT_COUNT(Constants.TABLE_PLANNED_SERVICES,
				" count(*) from " + Constants.TABLE_PLANNED_SERVICES + "  where " + Constants.QUERY_WHERE_CLAUSE_REP,
				" ", true, false, PlannedServices.class),

		/*
		 * Planned Services End
		 */

		/*
		 * Treatment Plan Items Start
		 */

		ES_TABLE_TREATMENT_PLAN_ITEMS(Constants.TABLE_TREATMENT_PLAN_ITEMS,
				Constants.PLANNEDSERVICE_COLUMNS + " from " + Constants.TABLE_TREATMENT_PLAN_ITEMS
						+ " order by treatment_plan_id asc ",
				" top " + Constants.QUERY_TOP_REP + " start at " + Constants.QUERY_START_REP, false, true,
				TreatmentPlanItems.class),

		ES_TABLE_TREATMENT_PLAN_ITEMS_COUNT(Constants.TABLE_TREATMENT_PLAN_ITEMS,
				" count(*) from " + Constants.TABLE_TREATMENT_PLAN_ITEMS + " ", " ", false, false,
				TreatmentPlanItems.class),

		ES_TABLE_TREATMENT_PLAN_ITEMS_NEXT(Constants.TABLE_TREATMENT_PLAN_ITEMS,
				Constants.PLANNEDSERVICE_COLUMNS + " from " + Constants.TABLE_PLANNED_SERVICES + " where "
						+ Constants.QUERY_WHERE_CLAUSE_REP + " order by treatment_plan_id asc",
				" top " + Constants.QUERY_TOP_REP + " start at " + Constants.QUERY_START_REP, true, true,
				TreatmentPlanItems.class),

		ES_TABLE_TREATMENT_PLAN_ITEMS_NEXT_COUNT(Constants.TABLE_TREATMENT_PLAN_ITEMS, " count(*) from "
				+ Constants.TABLE_TREATMENT_PLAN_ITEMS + "  where " + Constants.QUERY_WHERE_CLAUSE_REP, " ", true,
				false, TreatmentPlanItems.class),

		/*
		 * Treatment Plan Items End
		 */

		
		/*
		 * Treatment Plans Start
		 */

		ES_TABLE_TREATMENT_PLANS(Constants.TABLE_TREATMENT_PLANS,
				Constants.TREATEMENTPLANS_COLUMNS + " from " + Constants.TABLE_TREATMENT_PLANS
						+ " order by treatment_plan_id asc ",
				" top " + Constants.QUERY_TOP_REP + " start at " + Constants.QUERY_START_REP, false, true,
				TreatmentPlans.class),

		ES_TABLE_TREATMENT_PLANS_COUNT(Constants.TABLE_TREATMENT_PLANS,
				" count(*) from " + Constants.TABLE_TREATMENT_PLANS + " ", " ", false, false,
				TreatmentPlans.class),

		ES_TABLE_TREATMENT_PLANS_NEXT(Constants.TABLE_TREATMENT_PLANS,
				Constants.TREATEMENTPLANS_COLUMNS + " from " + Constants.TABLE_TREATMENT_PLANS + " where "
						+ Constants.QUERY_WHERE_CLAUSE_REP + " order by date_entered asc",
				" top " + Constants.QUERY_TOP_REP + " start at " + Constants.QUERY_START_REP, true, true,
				TreatmentPlans.class),

		ES_TABLE_TREATMENT_PLANS_NEXT_COUNT(Constants.TABLE_TREATMENT_PLANS, " count(*) from "
				+ Constants.TABLE_TREATMENT_PLANS + "  where " + Constants.QUERY_WHERE_CLAUSE_REP, " ", true,
				false, TreatmentPlans.class),

		/*
		 * Treatment Plans End
		 */

		/*
		 * Provider Start
		 */

		ES_TABLE_PROVIDER(Constants.TABLE_PROVIDER,
				Constants.PROVIDER_COLUMNS + " from " + Constants.TABLE_PROVIDER
						+ " order by provider_id asc ",
				" top " + Constants.QUERY_TOP_REP + " start at " + Constants.QUERY_START_REP, false, true,
				Provider.class),

		ES_TABLE_PROVIDER_COUNT(Constants.TABLE_PROVIDER,
				" count(*) from " + Constants.TABLE_PROVIDER + " ", " ", false, false,
				Provider.class),

		ES_TABLE_PROVIDER_NEXT(Constants.TABLE_PROVIDER,
				Constants.PROVIDER_COLUMNS + " from " + Constants.TABLE_PROVIDER + " where "
						+ Constants.QUERY_WHERE_CLAUSE_REP + " order by provider_id asc",
				" top " + Constants.QUERY_TOP_REP + " start at " + Constants.QUERY_START_REP, true, true,
				Provider.class),

		ES_TABLE_PROVIDER_NEXT_COUNT(Constants.TABLE_PROVIDER, " count(*) from "
				+ Constants.TABLE_PROVIDER + "  where " + Constants.QUERY_WHERE_CLAUSE_REP, " ", true,
				false, Provider.class),

		/*
		 * Provider End
		 */


		/*
		 * Employer Start
		 */

		ES_TABLE_EMPLOYER(Constants.TABLE_EMPLOYER,
				Constants.EMPLOYER_COLUMNS + " from " + Constants.TABLE_EMPLOYER
						+ " order by employer_id asc ",
				" top " + Constants.QUERY_TOP_REP + " start at " + Constants.QUERY_START_REP, false, true,
				Employer.class),

		ES_TABLE_EMPLOYER_COUNT(Constants.TABLE_EMPLOYER,
				" count(*) from " + Constants.TABLE_EMPLOYER + " ", " ", false, false,
				Employer.class),

		ES_TABLE_EMPLOYER_NEXT(Constants.TABLE_EMPLOYER,
				Constants.EMPLOYER_COLUMNS + " from " + Constants.TABLE_EMPLOYER + " where "
						+ Constants.QUERY_WHERE_CLAUSE_REP + " order by employer_id asc",
				" top " + Constants.QUERY_TOP_REP + " start at " + Constants.QUERY_START_REP, true, true,
				Employer.class),

		ES_TABLE_EMPLOYER_NEXT_COUNT(Constants.TABLE_EMPLOYER, " count(*) from "
				+ Constants.TABLE_EMPLOYER + "  where " + Constants.QUERY_WHERE_CLAUSE_REP, " ", true,
				false, Employer.class);

		/*
		 * Employer End
		 */
		
		
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
