package com.tricon.ruleengine.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.tricon.ruleengine.dao.ScrapingFullDataDoa;
import com.tricon.ruleengine.dao.ScrappingDao;
import com.tricon.ruleengine.logger.RuleEngineLogger;
import com.tricon.ruleengine.utils.Constants;

/**
 * 
 * @author Deepak.Dogra
 *
 */
@Component
public class ApplicationStartupService {

	static Class<?> clazz = ApplicationStartupService.class;
	
	@Autowired ScrappingDao sDao;
	
	@Autowired ScrapingFullDataDoa fDoa;
	
	@Autowired
	Environment env;


	@EventListener
	public void onApplicationEvent(ContextRefreshedEvent event) {
		RuleEngineLogger.generateLogs(clazz, "Application Started .. ", Constants.rule_log_debug, null);
		RuleEngineLogger.generateLogs(clazz, " Update running Status of Scrapping.....", Constants.rule_log_debug, null);
		String[] p=env.getActiveProfiles();
		if (p[0].equalsIgnoreCase("prod")) {
		sDao.updateScrappingSiteRunningStatusAll();//in case we need uncomment this latter...
		fDoa.updateScrappingSiteRunningStatusAll();//in case we need uncomment this latter...
		
		}
		
		
	}
	
}