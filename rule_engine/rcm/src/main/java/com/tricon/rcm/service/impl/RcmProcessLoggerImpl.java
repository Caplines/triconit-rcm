package com.tricon.rcm.service.impl;

import java.time.LocalTime;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tricon.rcm.db.entity.RcmProcessLogger;
import com.tricon.rcm.jpa.repository.RcmProcessLoggerRepo;
import com.tricon.rcm.security.JwtUser;

@Service
public class RcmProcessLoggerImpl {
	
	private final Logger logger = LoggerFactory.getLogger(RcmProcessLoggerImpl.class);

	@Autowired
	RcmProcessLoggerRepo rcmProcessLoggerRepo;

	@Transactional(rollbackFor = Exception.class)
	public RcmProcessLogger startProcessLogger(String processName, String parametres, JwtUser jwtUser) {
		RcmProcessLogger processLogger = setProcessLoggerInformation(processName, parametres, jwtUser);
		processLogger.setStartTime(LocalTime.now());
		processLogger.setStatus(true);
		processLogger = rcmProcessLoggerRepo.save(processLogger);
		logger.info("Ending Start Prcoess logger!");
		return processLogger;
	}

	@Transactional(rollbackFor = Exception.class)
	public void endProcessLogger(RcmProcessLogger processLogger) {
		if (processLogger != null) {
			processLogger.setEndTime(LocalTime.now());
			processLogger.setStatus(false);
			processLogger = rcmProcessLoggerRepo.save(processLogger);
			logger.info("Ending End Prcoess logger!");
		} else {
			logger.info("No Active Prcoess Exist !");
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void resetProcessLoggerStatus(RcmProcessLogger processLogger) {
		processLogger.setStatus(false);
		rcmProcessLoggerRepo.save(processLogger);
		logger.info("Reset Process logger Done!");
	}

	/*
	 * when application will start then we must to reset all activeTrue status
	 */
	@Transactional(rollbackFor = Exception.class)
	public void resetAllActiveProcesses() {
		rcmProcessLoggerRepo.resetAllRcmProcessLoggerActiveStatus();
		logger.info("Reset All Active Process logger Done!");

	}

	private RcmProcessLogger setProcessLoggerInformation(String processName, String parametres, JwtUser jwtUser) {
		RcmProcessLogger processLogger = new RcmProcessLogger();
		processLogger.setCreatedDate(new Date());
		processLogger.setCreatedBy(jwtUser.getFirstname().concat(" ").concat(jwtUser.getLastname()));
		processLogger.setProcessName(processName);
		processLogger.setParameters(parametres);
		return processLogger;

	}

}
