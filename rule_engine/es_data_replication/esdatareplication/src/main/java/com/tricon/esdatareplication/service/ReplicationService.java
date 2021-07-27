package com.tricon.esdatareplication.service;

import java.io.BufferedWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.tricon.esdatareplication.eaglesoft.ESConnection;
import com.tricon.esdatareplication.eaglesoft.PrepairESDataFromFromResultSet;
import com.tricon.esdatareplication.entity.repdb.ESTable;
import com.tricon.esdatareplication.entity.repdb.PayType;
import com.tricon.esdatareplication.util.Constants;
import com.tricon.esdatareplication.util.QueryEnum;
import com.tricon.esdatareplication.util.QueryTable;
import com.tricon.esdatareplication.dao.repdb.ESTableRepository;
import com.tricon.esdatareplication.dao.repdb.PatientRepository;
import com.tricon.esdatareplication.dao.repdb.PayTypeRepository;
import com.tricon.esdatareplication.dao.ruleenginedb.PatientRepositoryRe;

@Service
@Transactional("ruleEngineTransactionManager")
public class ReplicationService {

	
	@Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
	private int batchSize;
	
	@Autowired
	private PatientRepository patientRepository;
	
	@Autowired
	private PayTypeRepository payTypeRepository;
	
	@Autowired
	PrepairESDataFromFromResultSet prepairESDataFromFromResultSet;

	@Autowired
	private ESTableRepository estableRepository;

	@Autowired
	private PatientRepositoryRe patientRepositoryre;

	public static void main(String d[]) {

		QueryTable.QueryEnum w = QueryTable.QueryEnum.valueOf("ES_CHAIRS");
		System.out.println(w.getTableName());
	}

	@Transactional(rollbackFor = NullPointerException.class, transactionManager = "ruleEngineTransactionManager")
	public void startReplication(BufferedWriter bw) {

		// Fetch all tables names to be fetched from ES
		List<ESTable> estables = estableRepository.findByCodeWrittenAndUploadedToLocal(1, 0);
		for (ESTable table : estables) {
			int countRecord=0;
			QueryTable.QueryEnum tab = QueryTable.QueryEnum.valueOf("ES_" + table.getTableName().toUpperCase());
			int totalCount=0 ;
			if (tab.isTotalCountNeeded() && table.getStaticTable()==1 && table.getUploadedToLocal()==0) {
				QueryTable.QueryEnum tabC = QueryTable.QueryEnum.valueOf("ES_" + table.getTableName().toUpperCase()+"_COUNT");
				totalCount = fetchDataCountFromES(tabC);
				///
				int start=1;
				int top=batchSize;
				if (totalCount<batchSize)
					top=totalCount;
				  while (true) {
					  ///SELECT TOP 1 START AT 100 p.* FROM "PPM"."paytype" AS  p
				   List <?> list =fetchDataFromES(tab, top, start);
				   countRecord=countRecord+list.size();
				   if (list.size()==0) {
					   table.setUploadedToLocal(1);
					   table.setRecordsInsertedLastIteration(countRecord);
					   estableRepository.save(table);
					   break;
				   }
				   //insert data in Local DB
				   saveDataToLocalDB(list, tab);   
				   start=start+top;
				   //patientRepository.saveAll(entities);
				}
			}else {
				
			}
			
			// Add logs
			// now //Fetch Data From ES TABLE 100 Records at a time..
			// FETCHED_ROW_COUNT_FROM_DB
          //update estable record for the static table
		}
         //
		// Chairs..
		// Patient

		// Using Date ..

		com.tricon.esdatareplication.entity.ruleenginedb.Patient pat = new com.tricon.esdatareplication.entity.ruleenginedb.Patient();
		// pat.setId1(1);
		try {
			/*
			pat = patientRepositoryre.save(pat);
			pat = new com.tricon.esdatareplication.entity.ruleenginedb.Patient();
			// pat.setId1(1);
			pat = patientRepositoryre.save(pat);
			pat = new com.tricon.esdatareplication.entity.ruleenginedb.Patient();
			// pat.setId1(1);
			pat = patientRepositoryre.save(pat);
			String n = null;
			// check for batch now...
			if (n.equals(""))
				System.out.println("ROOO");
			*/
		} catch (NullPointerException e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			// e.printStackTrace();
		}
		// Get Details of Eagle Soft DB Tables.

	}

	private List<?> fetchDataFromES(QueryTable.QueryEnum qnum, int total,int start) {

		List<?> arrayList = null;

		Connection con = null;
		try {
			con = ESConnection.getConnection();
			String query=qnum.getQuery();
			String limit=qnum.getLimit();
			if (limit.contains(Constants.QUERY_TOP_REP)) {
				limit=limit.replace(Constants.QUERY_TOP_REP, total+"");
			}
			if (limit.contains(Constants.QUERY_START_REP)) {
				limit=limit.replace(Constants.QUERY_START_REP, start+"");
			}
			query=limit+" "+ query;
			PreparedStatement pstmt = con.prepareStatement("select " + query);
	        System.out.println("query-"+query);		
			
			ResultSet rs = pstmt.executeQuery();
			arrayList = prepairESDataFromFromResultSet.createTableData(rs, qnum.getClazz());
			
			rs.close();
			pstmt.close();
		} catch (SQLException sqe) {
			//logger.error("Unexpected exception : " + sqe.toString() + ", sqlstate = " + sqe.getSQLState());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ESConnection.closeConnection(con);
		}

		return arrayList;

	}

	private int fetchDataCountFromES(QueryTable.QueryEnum qnum) {

		Connection con = null;
		int totalCount=0;
		try {
			con = ESConnection.getConnection();
			PreparedStatement pstmt = con.prepareStatement("select" + qnum.getQuery());
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				totalCount =rs.getInt(1);	
			}
			rs.close();
			pstmt.close();
		} catch (SQLException sqe) {
			sqe.printStackTrace();
			//logger.error("Unexpected exception : " + sqe.toString() + ", sqlstate = " + sqe.getSQLState());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ESConnection.closeConnection(con);
		}

		return totalCount;

	}
	
	private void saveDataToLocalDB(List<?> data,QueryTable.QueryEnum tab) {
		
	System.out.println("saveDataToLocalDB");
	System.out.println(tab.getTableName());
	
	if (tab.getTableName().equals("paytype")) {
		payTypeRepository.saveAll((List<PayType>) data);	  
	}
	}

}
