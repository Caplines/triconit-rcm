package com.tricon.esdatareplication.scheduler;

import java.io.BufferedWriter;
import java.io.FileWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tricon.esdatareplication.logger.AppLogger;
import com.tricon.esdatareplication.service.ReplicationService;

@Component
public class ReplicationDBTask {

	@Autowired
	AppLogger appLogger;

	@Autowired
	ReplicationService replicationService;

	private static final Logger log = LoggerFactory.getLogger(ReplicationDBTask.class);

	@Scheduled(cron = "${scheduler.startcron}")
	public void runReplication() {

		Object[] o = appLogger.createNewLogFile();
		try {
			appLogger.appendStream("Logger Started", (BufferedWriter) o[0], true);
			log.info("Replication", "Started");
			replicationService.startReplication((BufferedWriter) o[0]);
		} finally {
			appLogger.closeBuffer((BufferedWriter) o[0], (FileWriter) o[1]);

		}

	}
}
