package com.tricon.rcm.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Deepak.Dogra
 *
 */

@Component
public class ApplicationStartupService {

	
	@Autowired
	Environment env;
	
	@Autowired
	RcmProcessLoggerImpl rcmProcessLoggerImpl;
	
	private final Logger logger = LoggerFactory.getLogger(ApplicationStartupService.class);
	
	@EventListener
	public void onApplicationEvent(ContextRefreshedEvent event) {
		logger.info("Application Started ..do any start up task if needed ");
		String[] p=env.getActiveProfiles();
		if (p[0].equalsIgnoreCase("prod")) {
			// when application will start then we need to reset all active processes
			rcmProcessLoggerImpl.resetAllActiveProcesses();
		}
		
		
	}
}
