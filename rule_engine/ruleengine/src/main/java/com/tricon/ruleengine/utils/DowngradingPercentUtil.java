package com.tricon.ruleengine.utils;

import java.io.BufferedWriter;


import com.tricon.ruleengine.logger.RuleEngineLogger;
import com.tricon.ruleengine.model.sheet.IVFTableSheet;

public class DowngradingPercentUtil {
	
	static Class<?> clazz = DowngradingPercentUtil.class;
	
	public static Double getIVFColumnforServiceCode(IVFTableSheet ivf, String serviceCode,String fee,BufferedWriter bw) {
		String actualFeeS="";
		double actualFee=0;
		try {
			
		double fees= Double.parseDouble(fee);
		double perc= 0;
		
		if (serviceCode.equalsIgnoreCase("D2391") || serviceCode.equalsIgnoreCase("D2392") || serviceCode.equalsIgnoreCase("D2393") ||
				serviceCode.equalsIgnoreCase("D2394")) {
			RuleEngineLogger.generateLogs(clazz, "PostCompositesD2391Percentage  for Code -"+serviceCode+" is " + ivf.getPostCompositesD2391Percentage(),
					Constants.rule_log_debug, bw);
			perc= Double.parseDouble(ivf.getPostCompositesD2391Percentage());
		}else if (serviceCode.equalsIgnoreCase("D2740") || serviceCode.equalsIgnoreCase("D2750") || serviceCode.equalsIgnoreCase("D2751") 
				) {
			RuleEngineLogger.generateLogs(clazz, "CrownsD2750D2740Percentage  for Code -"+serviceCode+" is " + ivf.getCrownsD2750D2740Percentage(),
					Constants.rule_log_debug, bw);
			perc= Double.parseDouble(ivf.getCrownsD2750D2740Percentage());
		}else {
			RuleEngineLogger.generateLogs(clazz, "Percentage NOT FOUND for code-" + serviceCode,
					Constants.rule_log_debug, bw);
		}
		 actualFee=(fees*perc)/100;
		RuleEngineLogger.generateLogs(clazz, "Calulate Fee  -" + actualFee,
				Constants.rule_log_debug, bw);
	
		actualFeeS = Constants.formatter.format(actualFee);
		RuleEngineLogger.generateLogs(clazz, "Calulated Fee  after formating -" + actualFeeS,
				Constants.rule_log_debug, bw);
		}catch(Exception x) {
			RuleEngineLogger.generateLogs(clazz, "Calulated Fee issue -" + x.getMessage(),
					Constants.rule_log_debug, bw);
		}
		return actualFee;
	}

}
