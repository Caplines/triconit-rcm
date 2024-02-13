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
				if (code.equals("D0120")){
					CoverageUtil.findZeroCoverageCode(list,code, ivf.getD0120());	
				}else if (code.equals("D0140")){
					CoverageUtil.findZeroCoverageCode(list,code, ivf.getD0140());
				}else if (code.equals("D0150")){
					CoverageUtil.findZeroCoverageCode(list,code, ivf.getD0150());
				}else if (code.equals("D4341")){
					CoverageUtil.findZeroCoverageCode(list,code, ivf.getsRPD4341Percentage());
				}else if (code.equals("D4346")){
					CoverageUtil.findZeroCoverageCode(list,code, ivf.getGingivitisD4346Percentage());
				}else if (code.equals("D4910")){
					CoverageUtil.findZeroCoverageCode(list,code, ivf.getPerioMaintenanceD4910Percentage());
				}else if (code.equals("D2950")){
					CoverageUtil.findZeroCoverageCode(list,code, ivf.getBuildUpsD2950Covered());
				}else if (code.equals("D2740") || code.equals("D2750")){
					CoverageUtil.findZeroCoverageCode(list,code, ivf.getCrownsD2750D2740Percentage());
				}else if (code.equals("D0220") || code.equals("D0230")){
					CoverageUtil.findZeroCoverageCode(list,code, ivf.getpAXRaysPercentage());
				}else if (code.equals("D0272") || code.equals("D0274")){
					CoverageUtil.findZeroCoverageCode(list,code, ivf.getBwx());
				}else if (code.equals("D0210")){
					CoverageUtil.findZeroCoverageCode(list,code, ivf.getFmxPer());
				}else if (code.equals("D0330")){
					CoverageUtil.findZeroCoverageCode(list,code, ivf.getPano1());
				}else if (code.equals("D4381")){
					CoverageUtil.findZeroCoverageCode(list,code, ivf.getD4381());
				}else if (code.equals("D4249")){
					CoverageUtil.findZeroCoverageCode(list,code, ivf.getCrownLengthD4249Percentage());
				}else if (code.equals("D0431")){
					CoverageUtil.findZeroCoverageCode(list,code, ivf.getD0431());
				}else if (code.equals("D4999")){
					CoverageUtil.findZeroCoverageCode(list,code, ivf.getD4999());
				}else if (code.equals("D2930") || code.equals("D2934")){
					CoverageUtil.findZeroCoverageCode(list,code, ivf.getD2930());
				}else if (code.equals("D9630")){
					CoverageUtil.findZeroCoverageCode(list,code, ivf.getD9630());
				}else if (code.equals("D4921")){
					CoverageUtil.findZeroCoverageCode(list,code, ivf.getPerioD4921());
				}else if (code.equals("D4266")){
					CoverageUtil.findZeroCoverageCode(list,code, ivf.getPerioD4266());
				}else if (code.equals("D6245") || code.equals("D6740")){
					CoverageUtil.findZeroCoverageCode(list,code, ivf.getBridges1());
				}else if (code.equals("D2931")){
					CoverageUtil.findZeroCoverageCode(list,code, ivf.getD2931());
				}else if (code.equals("D5110") || code.equals("D5120")){
					CoverageUtil.findZeroCoverageCode(list,code, ivf.getD511020Percentage());
				}else if (code.equals("D5130") || code.equals("D5140")){
					CoverageUtil.findZeroCoverageCode(list,code, ivf.getD513040Percentage());
				}else if (code.equals("D5820") || code.equals("D5821")){
					CoverageUtil.findZeroCoverageCode(list,code, ivf.getD5810CPercentage());
				}else if (code.equals("D6010")){
					CoverageUtil.findZeroCoverageCode(list,code, ivf.getImplantCoverageD6010Percentage());
				}else if (code.equals("D7310")){
					CoverageUtil.findZeroCoverageCode(list,code, ivf.getD7310());
				}else if (code.equals("D7311")){
					CoverageUtil.findZeroCoverageCode(list,code, ivf.getD7311());
				}else if (code.equals("D7953")){
					CoverageUtil.findZeroCoverageCode(list,code, ivf.getD7953());
				}else if (code.equals("D2391")){
					CoverageUtil.findZeroCoverageCode(list,code, ivf.getPostCompositesD2391Percentage());
				}else if (code.equals("D7111") || code.equals("D7140")){
					CoverageUtil.findZeroCoverageCode(list,code, ivf.getExtractionsMinorPercentage());
				}else if (code.equals("D7210") || code.equals("D7240")){
					CoverageUtil.findZeroCoverageCode(list,code, ivf.getExtractionsMajorPercentage());
				}else if (code.equals("D7250")){
					CoverageUtil.findZeroCoverageCode(list,code, ivf.getD7250());
				}else if (code.equals("D3310")){
					CoverageUtil.findZeroCoverageCode(list,code, ivf.getD3310());
				}else if (code.equals("D3320")){
					CoverageUtil.findZeroCoverageCode(list,code, ivf.getD3320());
				}else if (code.equals("D9944") || code.equals("D9945")){
					CoverageUtil.findZeroCoverageCode(list,code, ivf.getNightGuardsD9940Percentage());
				}else if (code.equals("D9310")){
					CoverageUtil.findZeroCoverageCode(list,code, ivf.getD9310Percentage());
				}else if (code.equals("D1351")){
					CoverageUtil.findZeroCoverageCode(list,code, ivf.getSealantsD1351Percentage());
				}else if (code.equals("D1330")){
					CoverageUtil.findZeroCoverageCode(list,code, ivf.getD1330());
				}else if (code.equals("D1120")){
					CoverageUtil.findZeroCoverageCode(list,code, ivf.getD1120());
				}else if (code.equals("D1110")){
					CoverageUtil.findZeroCoverageCode(list,code, ivf.getD1110());
				}else if (code.equals("D1206")){
					CoverageUtil.findZeroCoverageCode(list,code, ivf.getD1206());
				}else if (code.equals("D1208")){
					CoverageUtil.findZeroCoverageCode(list,code, ivf.getD1208());
				}
			
			}
		return list;
		
    }
}
