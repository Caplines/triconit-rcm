package com.tricon.ruleengine.utils;

import java.util.ArrayList;
import java.util.List;

import com.tricon.ruleengine.dto.CodeWithCoverage;
import com.tricon.ruleengine.model.sheet.CommonDataCheck;
import com.tricon.ruleengine.model.sheet.IVFTableSheet;

public class CoverageUtil {

	private static CodeWithCoverage findZeroCoverageCode(String code, String value) {

		CodeWithCoverage codeWithCoverage= null;
		if (code!=null) {
			if (value==null) {
				codeWithCoverage = new CodeWithCoverage();
				codeWithCoverage.setCoverage("0");
				codeWithCoverage.setCode(code);
			}else if (value.equals("0.0") || value.equals("0")) {
				codeWithCoverage = new CodeWithCoverage();
				codeWithCoverage.setCoverage("0");
				codeWithCoverage.setCode(code);
			}
		}
		return codeWithCoverage;
       
    }
	
	public static CodeWithCoverage findZeroCoverageCodeList(CommonDataCheck obj, IVFTableSheet ivf) {

		CommonDataCheck tp = (CommonDataCheck) obj;
		CodeWithCoverage codeWithCoverage= null;
		String code = tp.getServiceCode();
			if (code!=null  && code.toUpperCase().startsWith("D")) {
				code= code.toUpperCase();
				if (code.equals("D0120")){
					codeWithCoverage = CoverageUtil.findZeroCoverageCode(code, ivf.getD0120());	
				}else if (code.equals("D0140")){
					codeWithCoverage = CoverageUtil.findZeroCoverageCode(code, ivf.getD0140());
				}else if (code.equals("D0150")){
					codeWithCoverage = CoverageUtil.findZeroCoverageCode(code, ivf.getD0150());
				}else if (code.equals("D4341")){
					codeWithCoverage = CoverageUtil.findZeroCoverageCode(code, ivf.getsRPD4341Percentage());
				}else if (code.equals("D4346")){
					codeWithCoverage = CoverageUtil.findZeroCoverageCode(code, ivf.getGingivitisD4346Percentage());
				}else if (code.equals("D4910")){
					codeWithCoverage = CoverageUtil.findZeroCoverageCode(code, ivf.getPerioMaintenanceD4910Percentage());
				}else if (code.equals("D2950")){
					codeWithCoverage = CoverageUtil.findZeroCoverageCode(code, ivf.getBuildUpsD2950Covered());
				}else if (code.equals("D2740") || code.equals("D2750")){
					codeWithCoverage = CoverageUtil.findZeroCoverageCode(code, ivf.getCrownsD2750D2740Percentage());
				}else if (code.equals("D0220") || code.equals("D0230")){
					codeWithCoverage = CoverageUtil.findZeroCoverageCode(code, ivf.getpAXRaysPercentage());
				}else if (code.equals("D0272") || code.equals("D0274")){
					codeWithCoverage = CoverageUtil.findZeroCoverageCode(code, ivf.getBwx());
				}else if (code.equals("D0210")){
					codeWithCoverage = CoverageUtil.findZeroCoverageCode(code, ivf.getFmxPer());
				}else if (code.equals("D0330")){
					codeWithCoverage = CoverageUtil.findZeroCoverageCode(code, ivf.getPano1());
				}else if (code.equals("D4381")){
					codeWithCoverage = CoverageUtil.findZeroCoverageCode(code, ivf.getD4381());
				}else if (code.equals("D4249")){
					codeWithCoverage = CoverageUtil.findZeroCoverageCode(code, ivf.getCrownLengthD4249Percentage());
				}else if (code.equals("D0431")){
					codeWithCoverage = CoverageUtil.findZeroCoverageCode(code, ivf.getD0431());
				}else if (code.equals("D4999")){
					codeWithCoverage = CoverageUtil.findZeroCoverageCode(code, ivf.getD4999());
				}else if (code.equals("D2930") || code.equals("D2934")){
					codeWithCoverage = CoverageUtil.findZeroCoverageCode(code, ivf.getD2930());
				}else if (code.equals("D9630")){
					codeWithCoverage = CoverageUtil.findZeroCoverageCode(code, ivf.getD9630());
				}else if (code.equals("D4921")){
					codeWithCoverage = CoverageUtil.findZeroCoverageCode(code, ivf.getPerioD4921());
				}else if (code.equals("D4266")){
					codeWithCoverage = CoverageUtil.findZeroCoverageCode(code, ivf.getPerioD4266());
				}else if (code.equals("D6245") || code.equals("D6740")){
					codeWithCoverage = CoverageUtil.findZeroCoverageCode(code, ivf.getBridges1());
				}else if (code.equals("D2931")){
					codeWithCoverage = CoverageUtil.findZeroCoverageCode(code, ivf.getD2931());
				}else if (code.equals("D5110") || code.equals("D5120")){
					codeWithCoverage = CoverageUtil.findZeroCoverageCode(code, ivf.getD511020Percentage());
				}else if (code.equals("D5130") || code.equals("D5140")){
					codeWithCoverage = CoverageUtil.findZeroCoverageCode(code, ivf.getD513040Percentage());
				}else if (code.equals("D5820") || code.equals("D5821")){
					codeWithCoverage = CoverageUtil.findZeroCoverageCode(code, ivf.getD5810CPercentage());
				}else if (code.equals("D6010")){
					codeWithCoverage = CoverageUtil.findZeroCoverageCode(code, ivf.getImplantCoverageD6010Percentage());
				}else if (code.equals("D7310")){
					codeWithCoverage = CoverageUtil.findZeroCoverageCode(code, ivf.getD7310());
				}else if (code.equals("D7311")){
					codeWithCoverage = CoverageUtil.findZeroCoverageCode(code, ivf.getD7311());
				}else if (code.equals("D7953")){
					codeWithCoverage = CoverageUtil.findZeroCoverageCode(code, ivf.getD7953());
				}else if (code.equals("D2391")){
					codeWithCoverage = CoverageUtil.findZeroCoverageCode(code, ivf.getPostCompositesD2391Percentage());
				}else if (code.equals("D7111") || code.equals("D7140")){
					codeWithCoverage = CoverageUtil.findZeroCoverageCode(code, ivf.getExtractionsMinorPercentage());
				}else if (code.equals("D7210") || code.equals("D7240")){
					codeWithCoverage = CoverageUtil.findZeroCoverageCode(code, ivf.getExtractionsMajorPercentage());
				}else if (code.equals("D7250")){
					codeWithCoverage = CoverageUtil.findZeroCoverageCode(code, ivf.getD7250());
				}else if (code.equals("D3310")){
					codeWithCoverage = CoverageUtil.findZeroCoverageCode(code, ivf.getD3310());
				}else if (code.equals("D3320")){
					codeWithCoverage = CoverageUtil.findZeroCoverageCode(code, ivf.getD3320());
				}else if (code.equals("D9944") || code.equals("D9945")){
					codeWithCoverage = CoverageUtil.findZeroCoverageCode(code, ivf.getNightGuardsD9940Percentage());
				}else if (code.equals("D9310")){
					codeWithCoverage = CoverageUtil.findZeroCoverageCode(code, ivf.getD9310Percentage());
				}else if (code.equals("D1351")){
					codeWithCoverage = CoverageUtil.findZeroCoverageCode(code, ivf.getSealantsD1351Percentage());
				}else if (code.equals("D1330")){
					codeWithCoverage = CoverageUtil.findZeroCoverageCode(code, ivf.getD1330());
				}else if (code.equals("D1120")){
					codeWithCoverage = CoverageUtil.findZeroCoverageCode(code, ivf.getD1120());
				}else if (code.equals("D1110")){
					codeWithCoverage = CoverageUtil.findZeroCoverageCode(code, ivf.getD1110());
				}else if (code.equals("D1206")){
					codeWithCoverage = CoverageUtil.findZeroCoverageCode(code, ivf.getD1206());
				}else if (code.equals("D1208")){
					codeWithCoverage = CoverageUtil.findZeroCoverageCode(code, ivf.getD1208());
				}
			
			}
		return codeWithCoverage;
		
    }
}
