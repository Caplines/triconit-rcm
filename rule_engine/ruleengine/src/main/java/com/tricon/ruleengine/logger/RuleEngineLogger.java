package com.tricon.ruleengine.logger;

import java.io.BufferedWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tricon.ruleengine.model.db.Rules;
import com.tricon.ruleengine.utils.Constants;
import com.tricon.ruleengine.utils.WebUtils;

public class RuleEngineLogger {

	public static String generateLogs(Class<?> clazz, Rules rule, String logType) {
		String mess = "";
		Logger log = LoggerFactory.getLogger(clazz);
		if (logType.equals(Constants.rule_log_debug)) {
			log.debug("{} {}", clazz.getName(), rule.getName());
			mess = rule.getName();
		} else if (logType.equals(Constants.rule_log_info)) {
			log.info("{} {}", clazz.getName(), rule.getName());
			mess = rule.getName();
		} else if (logType.equals(Constants.rule_log_warn)) {
			log.warn("{} {}", clazz.getName(), rule.getName());
			mess = rule.getName();
		} else if (logType.equals(Constants.rule_log_error)) {
			log.error("{} {}", clazz.getName(), rule.getName());
			mess = rule.getName();
		}
		return mess;
	}

	public static String generateLogs(Class<?> clazz, String message, String logType, BufferedWriter bw) {
		String mess = "";
		Logger log = LoggerFactory.getLogger(clazz);
		String trimmed = logType != null ? logType.trim() : "";
		if (trimmed.equalsIgnoreCase(Constants.rule_log_debug)) {
			log.debug(message);
			mess = message;
		} else if (trimmed.equalsIgnoreCase(Constants.rule_log_info)) {
			log.info(message);
			mess = message;
		} else if (trimmed.equalsIgnoreCase(Constants.rule_log_warn)) {
			log.warn(message);
			mess = message;
		} else if (trimmed.equalsIgnoreCase(Constants.rule_log_error)) {
			log.error(message);
			mess = message;
		}
		WebUtils.appendStream(message, bw);
		return mess;
	}

}
