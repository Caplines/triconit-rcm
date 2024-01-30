package com.tricon.ruleengine.utils;

import java.util.ArrayList;
import java.util.List;

import com.tricon.ruleengine.dto.CodeWithCoverage;
import com.tricon.ruleengine.model.sheet.CommonDataCheck;
import com.tricon.ruleengine.model.sheet.IVFTableSheet;

public class CoverageUtil {

	private static void findZeroCoverageCode(List<CodeWithCoverage> list,String code, String value) {

		CodeWithCoverage codeWithCoverage= null;
		if (code!=null) {
			if (value==null) {
				codeWithCoverage = new CodeWithCoverage();
				codeWithCoverage.setCoverage("0");
			}else if (value.equals("0.0") || value.equals("0")) {
				codeWithCoverage = new CodeWithCoverage();
				codeWithCoverage.setCoverage("0");
			}
		}
		if (codeWithCoverage!=null) {
			if (list==null) list= new ArrayList<>();
			list.add(codeWithCoverage);
		}
       
    }
	
	public static List<CodeWithCoverage> findZeroCoverageCodeList(CommonDataCheck obj, IVFTableSheet ivf) {

		CommonDataCheck tp = (CommonDataCheck) obj;
		List<CodeWithCoverage> list=null;
		String code = tp.getServiceCode();
			if (code!=null  && code.toUpperCase().startsWith("D")) {
				code= code.toUpperCase();
				if (code.equals("XXXX")){
					CoverageUtil.findZeroCoverageCode(list,code, ivf.getAllowAmountD7240());	
				}else if (code.equals("YYY")){
					CoverageUtil.findZeroCoverageCode(list,code, "");
				}
			}
		return list;
		
    }
}
