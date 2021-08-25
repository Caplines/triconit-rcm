package com.tricon.esdatareplication.service;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tricon.esdatareplication.entity.repdb.Patient;
import com.tricon.esdatareplication.logger.AppLogger;
import com.tricon.esdatareplication.util.Constants;

@Service
public class CommonTableService {

	@Autowired
	private AppLogger logger;

	public void appendLoggerToWriter(Class<?> clazz, BufferedWriter bw, String message,
			boolean newLine) {

		Logger log = LogManager.getLogger(clazz.getName());
		log.debug(message);
		logger.appendStream(message, bw,newLine);
	}

	public void appendLoggerToWriter(BufferedWriter bw, String message,
			boolean newLine) {

		logger.appendStream(message, bw,newLine);
	}

	public void logFirstTimeDataEntryToTable(Class<?> clazz, BufferedWriter bw, int dataCount) {

		appendLoggerToWriter(clazz, bw, Constants.FIRST_TIME_TABLE_DATA_PULL,true);
		appendLoggerToWriter(clazz, bw, Constants.RECORDS_INSERTED_IN_TABLE + "-" + dataCount,true);

	}

	public void logAfterFirstTimeDataEntryToTable(Class<?> clazz, BufferedWriter bw, 
			int newDataCount,int updatedDataCount,String newDataIds,String updatedDataIds) {

		appendLoggerToWriter(clazz, bw, Constants.SECOND_TIME_TABLE_DATA_PULL,true);
		appendLoggerToWriter(clazz, bw, Constants.RECORDS_UPDATED_IN_TABLE+"-"+updatedDataCount,true);
		appendLoggerToWriter(clazz, bw, "Data Ids->"+updatedDataIds,true);
		appendLoggerToWriter(clazz, bw, Constants.RECORDS_INSERTED_IN_TABLE+"-"+newDataCount,true);
		appendLoggerToWriter(clazz, bw, "Data Ids->"+newDataIds,true);
	}

	public void logDeletedFromTable(Class<?> clazz, BufferedWriter bw, 
			int dataCount,String oldDataIds) {

		appendLoggerToWriter(clazz, bw, Constants.TABLE_DATA_DELETION_PROCESS,true);
		appendLoggerToWriter(clazz, bw, Constants.RECORDS_UPDATED_IN_TABLE+"-"+dataCount,true);
		appendLoggerToWriter(clazz, bw, "Data Ids->"+oldDataIds,true);
	}

	public void logPushToCloud(Class<?> clazz, BufferedWriter bw, 
			int newDataCount,int updatedDataCount,String newDataIds,String updatedDataIds) {

		appendLoggerToWriter(clazz, bw, Constants.SECOND_TIME_TABLE_DATA_PULL,true);
		appendLoggerToWriter(clazz, bw, Constants.RECORDS_UPDATED_IN_TABLE+"-"+updatedDataCount,true);
		appendLoggerToWriter(clazz, bw, "Count Of Data-"+updatedDataIds,true);
		appendLoggerToWriter(clazz, bw, Constants.RECORDS_INSERTED_IN_TABLE+"-"+newDataCount,true);
		appendLoggerToWriter(clazz, bw, "Count Of Data-"+newDataIds,true);
	}

	public void appenErrorToWriter(Class<?> clazz, BufferedWriter bw, Exception ex) {

		StringWriter errors = new StringWriter();
		ex.printStackTrace(new PrintWriter(errors));
		appendLoggerToWriter(clazz, bw, Constants.PROCESS_EXCEPTION,true);
		appendLoggerToWriter(clazz, bw, errors.toString(),true);
		
	}

	public void closeWriter(BufferedWriter bw) {

		logger.closeBuffer(bw, null);
		
	}

}
