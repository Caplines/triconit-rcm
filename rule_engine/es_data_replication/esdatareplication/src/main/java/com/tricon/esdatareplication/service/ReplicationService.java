package com.tricon.esdatareplication.service;

import java.io.BufferedWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.tricon.esdatareplication.dao.repdb.ESTableRepository;
import com.tricon.esdatareplication.dao.repdb.OfficeRepository;
import com.tricon.esdatareplication.eaglesoft.ESConnection;
import com.tricon.esdatareplication.eaglesoft.PrepairESDataFromFromResultSet;
import com.tricon.esdatareplication.entity.repdb.Appointment;
import com.tricon.esdatareplication.entity.repdb.Chairs;
import com.tricon.esdatareplication.entity.repdb.ESTable;
import com.tricon.esdatareplication.entity.repdb.Employer;
import com.tricon.esdatareplication.entity.repdb.Office;
import com.tricon.esdatareplication.entity.repdb.Patient;
import com.tricon.esdatareplication.entity.repdb.PayType;
import com.tricon.esdatareplication.entity.repdb.PaymentProvider;
import com.tricon.esdatareplication.entity.repdb.PlannedServices;
import com.tricon.esdatareplication.entity.repdb.Provider;
import com.tricon.esdatareplication.entity.repdb.Transactions;
import com.tricon.esdatareplication.entity.repdb.TransactionsDetail;
import com.tricon.esdatareplication.entity.repdb.TreatmentPlanItems;
import com.tricon.esdatareplication.entity.repdb.TreatmentPlans;
import com.tricon.esdatareplication.util.Constants;
import com.tricon.esdatareplication.util.DataStatus;
import com.tricon.esdatareplication.util.QueryTable;

@Service
@Transactional("ruleEngineTransactionManager")
public class ReplicationService {

	@Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
	private int batchSize;

	@Autowired
	PayTypeTableService payTypeTableService;

	@Autowired
	PatientTableService patientTableService;

	@Autowired
	AppointmentTableService appointmentTableService;

	@Autowired
	ChairsTableService chairsTableService;

	@Autowired
	TransactionsTableService transactionsTableService;

	@Autowired
	TransactionsDetailTableService transactionsDetailTableService;

	@Autowired
	PaymentProviderTableService paymentProviderTableService;

	@Autowired
	PlannedServicesTableService plannedServicesTableService;

	@Autowired
	TreatmentPlanItemsTableService treatmentPlanItemsTableService;

	@Autowired
	TreatmentPlansTableService treatmentPlansTableService;

	@Autowired
	EmployerTableService employerTableService;

	@Autowired
	ProviderTableService providerTableService;

	@Autowired
	PrepairESDataFromFromResultSet prepairESDataFromFromResultSet;

	@Autowired
	private ESTableRepository estableRepository;

	@Autowired
	private OfficeRepository officeRepository;

	@Autowired
	private CommonTableService commonTableService;

	// Holds the reference of patentIds inserted in DB from the second time
	static Set<String> patientIdsFetchced = new HashSet<>();
	static Set<Integer> employerIdsFetchced = new HashSet<>();
	static Set<Integer> transactionsNumbersFetchced = new HashSet<>();
	static Set<String> providerIdsFetchced = new HashSet<>();
	static Set<Integer> lineNumbersFetchced = new HashSet<>();
	static Set<String> patientIdsFetchcedExtra = new HashSet<>();

	// @Transactional(rollbackFor = NullPointerException.class, transactionManager =
	// "ruleEngineTransactionManager")
	public void startReplication(BufferedWriter bw) {

		// Fetch all tables names to be fetched from ES
		commonTableService.appendLoggerToWriter(ReplicationService.class, bw, new Date().toString(), true);
		commonTableService.appendLoggerToWriter(ReplicationService.class, bw, "Pull From Local ES Started", true);
		fetchDataFromLocalESToLocalDB(bw);
		commonTableService.appendLoggerToWriter(ReplicationService.class, bw, "Pull From Local ES END", true);
		// Push data to Cloud
		pushDataFromLocalESToColudDB(bw);
		commonTableService.appendLoggerToWriter(ReplicationService.class, bw, new Date().toString(), true);

		patientIdsFetchced.clear();
		employerIdsFetchced.clear();
		transactionsNumbersFetchced.clear();
		providerIdsFetchced.clear();
		lineNumbersFetchced.clear();
		patientIdsFetchcedExtra.clear();
	}

	public void pushDataFromLocalESToColudDB(BufferedWriter bw) {
		Office office = officeRepository.findById(1).get();
		System.out.println("pushDataFromLocalESToColudDB");
		List<ESTable> estables = estableRepository.findByCodeWritten(DataStatus.StatusEnum.CODE_WRITTEN_STATUS.YES);
		estables.forEach(es -> {
			commonTableService.appendLoggerToWriter(bw, "*********** START *******************", true);
			commonTableService.appendLoggerToWriter(bw, "Push to could start for " + es.getTableName(), true);
			if (es.getTableName().equals(Constants.TABLE_PAYTYPE)) {
				es = payTypeTableService.pushDataFromLocalESToColudDB(bw, office, es);

			} else if (es.getTableName().equals(Constants.TABLE_CHAIRS)) {
				es = chairsTableService.pushDataFromLocalESToColudDB(bw, office, es);
				estableRepository.save(es);

			} else if (es.getTableName().equals(Constants.TABLE_PATIENT)) {
				es = patientTableService.pushDataFromLocalESToColudDB(bw, office, es);
				estableRepository.save(es);
			} else if (es.getTableName().equals(Constants.TABLE_APPOINTMENT)) {
				es = appointmentTableService.pushDataFromLocalESToColudDB(bw, office, es);
				estableRepository.save(es);
			} else if (es.getTableName().equals(Constants.TABLE_TRANSACTIONS)) {
				es = transactionsTableService.pushDataFromLocalESToColudDB(bw, office, es);
				estableRepository.save(es);
			} else if (es.getTableName().equals(Constants.TABLE_TRANSACTIONS_DETAIL)) {
				es = transactionsDetailTableService.pushDataFromLocalESToColudDB(bw, office, es);
				estableRepository.save(es);
			} else if (es.getTableName().equals(Constants.TABLE_TRANSACTIONS_DETAIL)) {
				es = transactionsDetailTableService.pushDataFromLocalESToColudDB(bw, office, es);
				estableRepository.save(es);
			} else if (es.getTableName().equals(Constants.TABLE_PAYMENT_PROVIDER)) {
				es = paymentProviderTableService.pushDataFromLocalESToColudDB(bw, office, es);
				estableRepository.save(es);
			} else if (es.getTableName().equals(Constants.TABLE_PLANNED_SERVICES)) {
				es = plannedServicesTableService.pushDataFromLocalESToColudDB(bw, office, es);
				estableRepository.save(es);
			} else if (es.getTableName().equals(Constants.TABLE_TREATMENT_PLAN_ITEMS)) {
				es = treatmentPlanItemsTableService.pushDataFromLocalESToColudDB(bw, office, es);
				estableRepository.save(es);
			} else if (es.getTableName().equals(Constants.TABLE_TREATMENT_PLANS)) {
				es = treatmentPlansTableService.pushDataFromLocalESToColudDB(bw, office, es);
				estableRepository.save(es);
			} else if (es.getTableName().equals(Constants.TABLE_EMPLOYER)) {
				es = employerTableService.pushDataFromLocalESToColudDB(bw, office, es);
				estableRepository.save(es);
			} else if (es.getTableName().equals(Constants.TABLE_PROVIDER)) {
				es = providerTableService.pushDataFromLocalESToColudDB(bw, office, es);
				estableRepository.save(es);
			}
			//
			commonTableService.appendLoggerToWriter(bw, "*********** END *******************", true);
		});

	}

	public void fetchDataFromLocalESToLocalDB(BufferedWriter bw) {

		// Fetch all tables names to be fetched from ES

		Date currentDate = new Date();
		ESTable establePat = null;
		List<ESTable> estables = estableRepository.findByCodeWrittenAndUploadedToLocal(
				DataStatus.StatusEnum.CODE_WRITTEN_STATUS.YES, DataStatus.StatusEnum.LOCAL_DATA_UPLOADED.NO);
		for (ESTable table : estables) {
			int countRecord = 0;

			if (table.getTableName().equals(Constants.TABLE_PATIENT))
				establePat = table;
			commonTableService.appendLoggerToWriter(bw, "*********** START *******************" + table.getTableName(),
					true);
			int totalCount = 0;
			commonTableService.appendLoggerToWriter(bw, "Pull from Local ES start for: " + table.getTableName(), true);

			if ((table.getStaticTable() == DataStatus.StatusEnum.STATIC_TABLE.YES
					&& table.getUploadedToLocal() == DataStatus.StatusEnum.LOCAL_DATA_UPLOADED.NO)
					|| (table.getStaticTable() == 0 && table.getUpdatedDate() == null)) {
				QueryTable.QueryEnum tab = QueryTable.QueryEnum.valueOf("ES_" + table.getTableName().toUpperCase());
				QueryTable.QueryEnum tabC = QueryTable.QueryEnum
						.valueOf("ES_" + table.getTableName().toUpperCase() + "_COUNT");
				totalCount = fetchDataCountFromES(tabC, currentDate, table.getUpdatedDate(), bw, false);
				int start = 1;
				int top = batchSize;
				if (totalCount < batchSize)
					top = totalCount;
				while (true) {
					/// SELECT TOP 1 START AT 100 p.* FROM "PPM"."paytype" AS p
					List<?> list = fetchDataFromES(tab, top, start, currentDate, table.getUpdatedDate(), false);
					countRecord = countRecord + list.size();
					if (list.size() == 0) {
						if (table.getStaticTable() == DataStatus.StatusEnum.STATIC_TABLE.YES)
							table.setUploadedToLocal(DataStatus.StatusEnum.LOCAL_DATA_UPLOADED.YES);
						table.setIterationCount(table.getIterationCount() + 1);
						table.setRecordsInsertedLastIteration(countRecord);
						estableRepository.save(table);
						break;
					}
					// insert data in Local DB
					saveDataToLocalDB(bw, list, tab, false);
					start = start + top;
					// patientRepository.saveAll(entities);
				}
			} else if (table.getStaticTable() == DataStatus.StatusEnum.STATIC_TABLE.NO
					&& table.getUpdatedDate() != null) {
				fetchDataFromLocalESToLocalDBNext(table, currentDate, bw, false);
			}
			commonTableService.appendLoggerToWriter(bw, "*********** END *******************" + table.getTableName(),
					true);
		}
		// For Extra Patients
		commonTableService.appendLoggerToWriter(bw, "*********** START *******************" + establePat.getTableName(),
				true);
		//fetchDataFromLocalESToLocalDBNext(establePat, currentDate, bw, true);
		commonTableService.appendLoggerToWriter(bw, "*********** END *******************" + establePat.getTableName(),
				true);

	}

	private void fetchDataFromLocalESToLocalDBNext(ESTable table, Date currentDate, BufferedWriter bw,
			boolean extraPat) {
		int countRecord = 0;
		int totalCount = 0;
		QueryTable.QueryEnum tab = QueryTable.QueryEnum.valueOf("ES_" + table.getTableName().toUpperCase() + "_NEXT");
		// Fetch Data From ES and see if that is in the Local DB or not
		// between clause
		QueryTable.QueryEnum tabC = QueryTable.QueryEnum
				.valueOf("ES_" + table.getTableName().toUpperCase() + "_NEXT_COUNT");

		totalCount = fetchDataCountFromES(tabC, currentDate, table.getUpdatedDate(), bw, extraPat);
		int start = 1;
		int top = batchSize;
		if (totalCount < batchSize)
			top = totalCount;
		// System.out.println("totalCount totalCount-8888?>" + totalCount);
		commonTableService.appendLoggerToWriter(bw, "Count of Total Records Read From ES -->" + totalCount, true);
		if (totalCount == 0) {
			// System.out.println("totalCount totalCount-?>" + totalCount);
			if (table.getStaticTable() == DataStatus.StatusEnum.STATIC_TABLE.YES)
				table.setUploadedToLocal(DataStatus.StatusEnum.LOCAL_DATA_UPLOADED.YES);
			table.setIterationCount(table.getIterationCount() + 1);
			table.setRecordsInsertedLastIteration(countRecord);
			estableRepository.save(table);
		} else {

			while (true) {
				/// SELECT TOP 1 START AT 100 p.* FROM "PPM"."paytype" AS p
				List<?> list = fetchDataFromES(tab, top, start, currentDate, table.getUpdatedDate(), extraPat);
				int size = list == null ? 0 : list.size();
				commonTableService.appendLoggerToWriter(bw,
						"Count of  Records Read From ES in Current Iteration-->" + size, true);
				countRecord = countRecord + size;
				// System.out.println("SSSSSSSSSSSS-?>" + size);
				if (size == 0) {
					if (table.getStaticTable() == DataStatus.StatusEnum.STATIC_TABLE.YES)
						table.setUploadedToLocal(1);
					// System.out.println("SSSSSSSSSSSS-?>SAVE");
					table.setIterationCount(table.getIterationCount() + 1);
					table.setRecordsInsertedLastIteration(countRecord);
					estableRepository.save(table);
					break;
				}
				// insert data in Local DB
				saveDataToLocalDB(bw, list, tab, true);
				start = start + top;

			}
		}
	}

	private List<?> fetchDataFromES(QueryTable.QueryEnum qnum, int total, int start, Date cDate,
			Date lastDateofCrawling, boolean extraPat) {

		List<?> arrayList = null;

		Connection con = null;
		try {
			con = ESConnection.getConnection();
			String query = qnum.getQuery();
			String limit = qnum.getLimit();
			if (limit.contains(Constants.QUERY_TOP_REP)) {
				limit = limit.replace(Constants.QUERY_TOP_REP, total + "");
			}
			if (limit.contains(Constants.QUERY_START_REP)) {
				limit = limit.replace(Constants.QUERY_START_REP, start + "");
			}
			query = limit + " " + query;
			if (qnum.isWhereClause() && !extraPat)
				query = query.replace(Constants.QUERY_WHERE_CLAUSE_REP,
						createWhereClause(qnum, cDate, lastDateofCrawling));
			else if (qnum.isWhereClause() && extraPat) {
				query = query.replace(Constants.QUERY_WHERE_CLAUSE_REP, createWhereClause(qnum));
			}
			PreparedStatement pstmt = con.prepareStatement("select " + query);
			System.out.println("query-" + query);
			ResultSet rs = pstmt.executeQuery();
			arrayList = prepairESDataFromFromResultSet.createTableData(rs, qnum.getClazz());

			rs.close();
			pstmt.close();
		} catch (SQLException sqe) {
			// logger.error("Unexpected exception : " + sqe.toString() + ", sqlstate = " +
			// sqe.getSQLState());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ESConnection.closeConnection(con);
		}

		return arrayList;

	}

	private int fetchDataCountFromES(QueryTable.QueryEnum qnum, Date cDate, Date lastDateofCrawling, BufferedWriter bw,
			boolean extraPat) {

		Connection con = null;
		int totalCount = 0;
		try {
			con = ESConnection.getConnection();
			String query = "select " + qnum.getQuery();
			if (qnum.isWhereClause()) {
				if (!extraPat)
					query = query.replace(Constants.QUERY_WHERE_CLAUSE_REP,
							createWhereClause(qnum, cDate, lastDateofCrawling));
				else {
					//query = "select patient_id in ('" + qnum.getQuery();
					query = query.replace(Constants.QUERY_WHERE_CLAUSE_REP, createWhereClause(qnum));
				}
			}

			PreparedStatement pstmt = con.prepareStatement(query);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				totalCount = rs.getInt(1);
			}
			rs.close();
			pstmt.close();
		} catch (SQLException sqe) {
			sqe.printStackTrace();
			commonTableService.appendLoggerToWriter(bw, "Error in Fetching Connection ES (check credentials)", true);

			// logger.error("Unexpected exception : " + sqe.toString() + ", sqlstate = " +
			// sqe.getSQLState());
		} catch (Exception e) {
			e.printStackTrace();
			commonTableService.appendLoggerToWriter(bw, "Error in Fetching data from ES:" + qnum.getTableName(), true);
			commonTableService.appenErrorToWriter(qnum.getClass(), bw, e);
		} finally {
			ESConnection.closeConnection(con);
		}

		return totalCount;

	}

	private void saveDataToLocalDB(BufferedWriter bw, List<?> data, QueryTable.QueryEnum tab, boolean checkExisting) {

		// System.out.println("saveDataToLocalDB");
		// System.out.println(tab.getTableName());

		if ((tab.getClazz().equals(PayType.class))) {
			payTypeTableService.saveDataToLocalDB(bw, data);
		} else if ((tab.getClazz().equals(Chairs.class))) {
			chairsTableService.saveDataToLocalDB(bw, data);
		} else if ((tab.getClazz().equals(Patient.class))) {
			if (checkExisting) {
				((List<Patient>) data).stream().map(Patient::getPatientId).forEach(patientIdsFetchced::add);
				((List<Patient>) data).stream().map(Patient::getPrimEmployerId).forEach(employerIdsFetchced::add);

			}
			patientTableService.saveDataToLocalDB(bw, data, checkExisting);
		} else if ((tab.getClazz().equals(Appointment.class))) {
			if (checkExisting) {
				((List<Appointment>) data).stream().map(Appointment::getPatientId)
						.forEach(patientIdsFetchcedExtra::add);
			}
			appointmentTableService.saveDataToLocalDB(bw, data, checkExisting);
		} else if ((tab.getClazz().equals(Transactions.class))) {
			if (checkExisting) {
				((List<Transactions>) data).stream().map(Transactions::getTranNum)
						.forEach(transactionsNumbersFetchced::add);
				((List<Transactions>) data).stream().map(Transactions::getProviderId).forEach(providerIdsFetchced::add);
				((List<Transactions>) data).stream().map(Transactions::getPatientId)
						.forEach(patientIdsFetchcedExtra::add);
			}
			transactionsTableService.saveDataToLocalDB(bw, data, checkExisting);
		} else if ((tab.getClazz().equals(TransactionsDetail.class))) {
			transactionsDetailTableService.saveDataToLocalDB(bw, data, checkExisting);
		} else if ((tab.getClazz().equals(PaymentProvider.class))) {
			paymentProviderTableService.saveDataToLocalDB(bw, data, checkExisting);
		} else if ((tab.getClazz().equals(PlannedServices.class))) {
			if (checkExisting) {
				((List<PlannedServices>) data).stream().map(PlannedServices::getLineNumber)
						.forEach(lineNumbersFetchced::add);

			}
			plannedServicesTableService.saveDataToLocalDB(bw, data, checkExisting);
		} else if ((tab.getClazz().equals(TreatmentPlanItems.class))) {
			treatmentPlanItemsTableService.saveDataToLocalDB(bw, data, checkExisting);
		} else if ((tab.getClazz().equals(TreatmentPlans.class))) {
			treatmentPlansTableService.saveDataToLocalDB(bw, data, checkExisting);
		} else if ((tab.getClazz().equals(Employer.class))) {// This will need patient Id info
			employerTableService.saveDataToLocalDB(bw, data, checkExisting);
		} else if ((tab.getClazz().equals(Provider.class))) {
			providerTableService.saveDataToLocalDB(bw, data, checkExisting);
		}
	}

	/*
	 * For getting latest patient id form other dependent table
	 */
	private String createWhereClause(QueryTable.QueryEnum tab) {

		if (tab.isWhereClause() && tab.getClazz().equals(Patient.class)) {

			return " patient_id in  (" + "'" + String.join("','", patientIdsFetchcedExtra) + "')";

		}
		return "";
	}

	private String createWhereClause(QueryTable.QueryEnum tab, Date cDate, Date lastDateofCrawling) {

		if (tab.isWhereClause() && tab.getClazz().equals(Patient.class)) {

			return " date_entered BETWEEN '" + Constants.SimpleDateformatForEsQuery.format(lastDateofCrawling) + "'"
					+ " and '" + Constants.SimpleDateformatForEsQuery.format(cDate) + "'";

		} else if (tab.isWhereClause() && tab.getClazz().equals(Appointment.class)) {
			// TODO
			return " start_time BETWEEN '" + Constants.SimpleDateformatForEsQuery.format(lastDateofCrawling) + "'"
					+ " and '" + Constants.SimpleDateformatForEsQuery.format(cDate) + "'";
		} else if (tab.isWhereClause() && tab.getClazz().equals(Employer.class)) {

			return " employer_id in  ("
					+ String.join(",",
							employerIdsFetchced.stream().map(s -> String.valueOf(s)).collect(Collectors.toList()))
					+ ") ";
		} else if (tab.isWhereClause() && tab.getClazz().equals(Transactions.class)) {

			return " tran_date BETWEEN '" + Constants.SimpleDateformatForEsQuery.format(lastDateofCrawling) + "'"
					+ " and '" + Constants.SimpleDateformatForEsQuery.format(cDate) + "'";
		} else if (tab.isWhereClause() && tab.getClazz().equals(TransactionsDetail.class)) {

			/*
			 * in case logic changes latter; return " tran_num in  ('" + String.join(",",
			 * transactionsNumbersFetchced.stream().map(s ->
			 * String.valueOf(s)).collect(Collectors.toList()));
			 */

			return " date_entered BETWEEN '" + Constants.SimpleDateformatForEsQuery.format(lastDateofCrawling) + "'"
					+ " and '" + Constants.SimpleDateformatForEsQuery.format(cDate) + "'";
		} else if (tab.isWhereClause() && tab.getClazz().equals(PaymentProvider.class)) {
			return " tran_num in  (" + String.join(",",
					transactionsNumbersFetchced.stream().map(s -> String.valueOf(s)).collect(Collectors.toList()))
					+ ")";

		} else if (tab.isWhereClause() && tab.getClazz().equals(Provider.class)) {

			return " provider_id in  (" + String.join(",", providerIdsFetchced) + ")";
		} else if (tab.isWhereClause() && tab.getClazz().equals(PlannedServices.class)) {
			// See latter if we move to date_planned
			return " patient_id in  (" + "'" + String.join("','", patientIdsFetchced) + "')";
			// a b c
			// a,','b,',c
		} else if (tab.isWhereClause() && tab.getClazz().equals(TreatmentPlanItems.class)) {
			return " line_number in  ("
					+ String.join(",",
							lineNumbersFetchced.stream().map(s -> String.valueOf(s)).collect(Collectors.toList()))
					+ ")";
		} else if (tab.isWhereClause() && tab.getClazz().equals(TreatmentPlans.class)) {
			// check for date date_last_updated when we get data
			return " date_entered BETWEEN '" + Constants.SimpleDateformatForEsQuery.format(lastDateofCrawling) + "'"
					+ " and '" + Constants.SimpleDateformatForEsQuery.format(cDate) + "'";
		}

		return "";
	}
	/*
	 * https://drive.google.com/file/d/1PzjrseKjtzoClXaKlOKmwZRuj7bu4YB4/view?usp=
	 * drive_web
	 * https://drive.google.com/file/d/1_LVjry5hadCA-GDk2iBb7UKBSLBvXATP/view?usp=
	 * drive_web
	 * https://drive.google.com/file/d/1ZiCIVo_APWn_oVoubrJe90hwTOwQmfIh/view?usp=
	 * drive_web
	 * https://drive.google.com/file/d/1zAcDNdzisW4yFL9FVjVO2GnmuKonFpNm/view?usp=
	 * drive_web
	 * https://drive.google.com/file/d/1NV5ICivT78Evz5TGFb8H5g3pvBeX0CTa/view?usp=
	 * drive_web
	 * https://drive.google.com/file/d/1eUUYZGogUEj_aq9COva9U7j8MUNsToby/view?usp=
	 * drive_web
	 * https://www.google.com/url?q=https://docs.google.com/spreadsheets/d/
	 * 1Sgf4aJzIDCz84QWRfo6-OfS3YhnYrc9IuAGcMYR2ve0/edit%23gid%3D1576780686&sa=D&
	 * source=hangouts&ust=1628844905374000&usg=AFQjCNHw_FXMawqSZxxzqa9YoD5T97T10w
	 * ES Database https://docs.google.com/spreadsheets/d/1Sgf4aJzIDCz84QWRfo6-
	 * OfS3YhnYrc9IuAGcMYR2ve0/edit#gid=1576780686
	 */
	/*
	 * 1. Patient 1 2. Paytype 1 3. Chairs 1 4. transactions 1 5
	 * .transactions_detail 1 6 .payment_provider 1 7. appointment 1 8.
	 * planned_services 1 (verify in Service if we need Group or PatientId line
	 * number in ) 9. treatment_plan_items 1 10 treatment_plans 1 //check for date
	 * date_last_updated when we get data 11 employer 1 12 provider 1
	 * 
	 */

}
