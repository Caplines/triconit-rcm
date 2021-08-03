package com.tricon.esdatareplication.service;

import java.io.BufferedWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

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
import com.tricon.esdatareplication.entity.repdb.Office;
import com.tricon.esdatareplication.entity.repdb.Patient;
import com.tricon.esdatareplication.entity.repdb.PayType;
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
	PrepairESDataFromFromResultSet prepairESDataFromFromResultSet;

	@Autowired
	private ESTableRepository estableRepository;


	@Autowired
	private OfficeRepository officeRepository;

	@Transactional(rollbackFor = NullPointerException.class, transactionManager = "ruleEngineTransactionManager")
	public void startReplication(BufferedWriter bw) {

		// Fetch all tables names to be fetched from ES
		fetchDataFromLocalESToLocalDB(bw);
		// Push data to Cloud
		pushDataFromLocalESToColudDB(bw);

	}

	public void pushDataFromLocalESToColudDB(BufferedWriter bw) {
		Office office = officeRepository.findById(1).get();
		System.out.println("pushDataFromLocalESToColudDB");
		List<ESTable> estables = estableRepository.findByCodeWritten(DataStatus.StatusEnum.CODE_WRITTEN_STATUS.YES);
		estables.forEach(es -> {
			if (es.getTableName().equals(Constants.TABLE_PAYTYPE)) {
				es= payTypeTableService.pushDataFromLocalESToColudDB(bw, office, es);
				estableRepository.save(es);

			}else if (es.getTableName().equals(Constants.TABLE_CHAIRS)) {
				es= chairsTableService.pushDataFromLocalESToColudDB(bw, office, es);
				estableRepository.save(es);

			} else if (es.getTableName().equals(Constants.TABLE_PATIENT)) {
				es= patientTableService.pushDataFromLocalESToColudDB(bw, office, es); 
				estableRepository.save(es);
			} else if (es.getTableName().equals(Constants.TABLE_APPOINTMENT)) {
				es=appointmentTableService.pushDataFromLocalESToColudDB(bw, office, es);
				estableRepository.save(es);
			}
		});

	}

	public void fetchDataFromLocalESToLocalDB(BufferedWriter bw) {

		// Fetch all tables names to be fetched from ES

		Date currentDate = new Date();
		List<ESTable> estables = estableRepository.findByCodeWrittenAndUploadedToLocal(
				DataStatus.StatusEnum.CODE_WRITTEN_STATUS.YES, DataStatus.StatusEnum.LOCAL_DATA_UPLOADED.NO);
		for (ESTable table : estables) {
			int countRecord = 0;
			int totalCount = 0;
			if ((table.getStaticTable() == DataStatus.StatusEnum.STATIC_TABLE.YES
					&& table.getUploadedToLocal() == DataStatus.StatusEnum.LOCAL_DATA_UPLOADED.NO)
					|| (table.getStaticTable() == 0 && table.getUpdatedDate() == null)) {
				QueryTable.QueryEnum tab = QueryTable.QueryEnum.valueOf("ES_" + table.getTableName().toUpperCase());
				QueryTable.QueryEnum tabC = QueryTable.QueryEnum
						.valueOf("ES_" + table.getTableName().toUpperCase() + "_COUNT");
				totalCount = fetchDataCountFromES(tabC, currentDate, table.getUpdatedDate());
				int start = 1;
				int top = batchSize;
				if (totalCount < batchSize)
					top = totalCount;
				while (true) {
					/// SELECT TOP 1 START AT 100 p.* FROM "PPM"."paytype" AS p
					List<?> list = fetchDataFromES(tab, top, start, currentDate, table.getUpdatedDate());
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
					saveDataToLocalDB(list, tab, false);
					start = start + top;
					// patientRepository.saveAll(entities);
				}
			} else if (table.getStaticTable() == DataStatus.StatusEnum.STATIC_TABLE.NO
					&& table.getUpdatedDate() != null) {
				QueryTable.QueryEnum tab = QueryTable.QueryEnum
						.valueOf("ES_" + table.getTableName().toUpperCase() + "_NEXT");
				// Fetch Data From ES and see if that is in the Local DB or not
				// between clause
				QueryTable.QueryEnum tabC = QueryTable.QueryEnum
						.valueOf("ES_" + table.getTableName().toUpperCase() + "_NEXT_COUNT");

				totalCount = fetchDataCountFromES(tabC, currentDate, table.getUpdatedDate());
				int start = 1;
				int top = batchSize;
				if (totalCount < batchSize)
					top = totalCount;
				System.out.println("totalCount totalCount-8888?>" + totalCount);
				if (totalCount == 0) {
					System.out.println("totalCount totalCount-?>" + totalCount);
					if (table.getStaticTable() == DataStatus.StatusEnum.STATIC_TABLE.YES)
						table.setUploadedToLocal(DataStatus.StatusEnum.LOCAL_DATA_UPLOADED.YES);
					table.setIterationCount(table.getIterationCount() + 1);
					table.setRecordsInsertedLastIteration(countRecord);
					estableRepository.save(table);
				} else {

					while (true) {
						/// SELECT TOP 1 START AT 100 p.* FROM "PPM"."paytype" AS p
						List<?> list = fetchDataFromES(tab, top, start, currentDate, table.getUpdatedDate());
						int size = list == null ? 0 : list.size();
						countRecord = countRecord + size;
						System.out.println("SSSSSSSSSSSS-?>" + size);
						if (size == 0) {
							if (table.getStaticTable() == 1)
								table.setUploadedToLocal(1);
							System.out.println("SSSSSSSSSSSS-?>SAVE");
							table.setIterationCount(table.getIterationCount() + 1);
							table.setRecordsInsertedLastIteration(countRecord);
							estableRepository.save(table);
							break;
						}
						// insert data in Local DB
						saveDataToLocalDB(list, tab, true);
						start = start + top;

					}
				}
			}

			// Add logs
			// now //Fetch Data From ES TABLE 100 Records at a time..
			// FETCHED_ROW_COUNT_FROM_DB
			// update estable record for the static table
		}
		//
		// Chairs..
		// Patient

		// Using Date ..

		// PatientReplica pat = new PatientReplica();
		// pat.setId1(1);
		try {
			/*
			 * pat = patientRepositoryre.save(pat); pat = new
			 * com.tricon.esdatareplication.entity.ruleenginedb.Patient(); // pat.setId1(1);
			 * pat = patientRepositoryre.save(pat); pat = new
			 * com.tricon.esdatareplication.entity.ruleenginedb.Patient(); // pat.setId1(1);
			 * pat = patientRepositoryre.save(pat); String n = null; // check for batch
			 * now... if (n.equals("")) System.out.println("ROOO");
			 */
		} catch (NullPointerException e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			// e.printStackTrace();
		}
		// Get Details of Eagle Soft DB Tables.

	}

	private List<?> fetchDataFromES(QueryTable.QueryEnum qnum, int total, int start, Date cDate,
			Date lastDateofCrawling) {

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
			if (qnum.isWhereClause())
				query = query.replace(Constants.QUERY_WHERE_CLAUSE_REP,
						createWhereClause(qnum, cDate, lastDateofCrawling));
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

	private int fetchDataCountFromES(QueryTable.QueryEnum qnum, Date cDate, Date lastDateofCrawling) {

		Connection con = null;
		int totalCount = 0;
		try {
			con = ESConnection.getConnection();
			String query = "select " + qnum.getQuery();
			if (qnum.isWhereClause()) {
				query = query.replace(Constants.QUERY_WHERE_CLAUSE_REP,
						createWhereClause(qnum, cDate, lastDateofCrawling));
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
			// logger.error("Unexpected exception : " + sqe.toString() + ", sqlstate = " +
			// sqe.getSQLState());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ESConnection.closeConnection(con);
		}

		return totalCount;

	}

	private void saveDataToLocalDB(List<?> data, QueryTable.QueryEnum tab, boolean checkExisting) {

		System.out.println("saveDataToLocalDB");
		System.out.println(tab.getTableName());

		// if (tab.getClazz().equals(Chairs.class))
		if ((tab.getClazz().equals(PayType.class))) {
			payTypeTableService.saveDataToLocalDB(data);
		} else if ((tab.getClazz().equals(Chairs.class))) {
			chairsTableService.saveDataToLocalDB(data);
		} else if ((tab.getClazz().equals(Patient.class))) {
			patientTableService.saveDataToLocalDB(data, checkExisting);
		} else if ((tab.getClazz().equals(Appointment.class))) {
			appointmentTableService.saveDataToLocalDB(data, checkExisting);
		}
	}

	private String createWhereClause(QueryTable.QueryEnum tab, Date cDate, Date lastDateofCrawling) {
		if (tab.isWhereClause() && tab.getClazz().equals(Patient.class)) {

			return " date_entered BETWEEN '" + Constants.SimpleDateformatForEsQuery.format(lastDateofCrawling) + "'"
					+ " and '" + Constants.SimpleDateformatForEsQuery.format(cDate) + "'";
		}else if (tab.isWhereClause() && tab.getClazz().equals(Appointment.class)) {
            // TODO
			return " start_time BETWEEN '" + Constants.SimpleDateformatForEsQuery.format(lastDateofCrawling) + "'"
					+ " and '" + Constants.SimpleDateformatForEsQuery.format(cDate) + "'";
		}

		return "";
	}

}
