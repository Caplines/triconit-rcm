package com.tricon.ruleengine.logger;

import java.io.BufferedWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tricon.ruleengine.model.db.Rules;
import com.tricon.ruleengine.utils.Constants;
import com.tricon.ruleengine.utils.WebUtils;

public class RuleEngineLogger {
	
	//static Logger log = LogManager. Ma.getLogger(RuleEngineLogger.class.getName());
	
	public static String generateLogs(Class<?> clazz,Rules rule,String logType) {
		
		  String mess="";
		  Logger log= LogManager.getLogger(clazz.getName());
		  if (logType.equals(Constants.rule_log_debug)) {
			  log.debug(clazz.getName());
			  log.debug(rule.getName());
			  mess=rule.getName();
		  }
		
		  return mess;
		
	}
	public static String generateLogs(Class<?> clazz,String message,String logType,BufferedWriter bw) {
		
		  String mess="";
		  Logger log= LogManager.getLogger(clazz.getName());
		  if (logType.equals(Constants.rule_log_debug)) {
			  log.debug(message);
			  mess=message;
		  }
		  WebUtils.appendStream(message, bw);
		  return mess;
		
		
	}

}
