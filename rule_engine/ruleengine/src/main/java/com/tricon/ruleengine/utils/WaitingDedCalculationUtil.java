package com.tricon.ruleengine.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.springframework.context.MessageSource;

import com.tricon.ruleengine.dto.CodeValueDto;
import com.tricon.ruleengine.dto.TPValidationResponseDto;
import com.tricon.ruleengine.model.db.Rules;
import com.tricon.ruleengine.model.sheet.CommonDataCheck;
import com.tricon.ruleengine.model.sheet.IVFTableSheet;
public class WaitingDedCalculationUtil {

	public static final int preventive_precentage_type=1;
	public static final int basic_precentage_type=2;
	public static final int major_precentage_type=3;
	public static final int check_type_ded=4;
	public static final int check_type_waiting=5;
	private static Locale locale = new Locale("en");
	
	//List<Object> tpList =null for DED
	public static List<TPValidationResponseDto> isWaitingDedValid(List<Object> tpList,IVFTableSheet ivf,int checkType,
			Rules rule,String errorMessageKey,MessageSource messageSource){
		List<TPValidationResponseDto> list = new ArrayList<>();
		
		try {
		String preventivePercentage=ivf.getPreventivePercentage().replace("$", "");
		String preventiveWaiting=ivf.getWaitingPeriod4();
		String preventiveDed=ivf.getPreventiveSubDed();
		
		String basisPercentage=ivf.getBasicPercentage().replace("$", "");
		String basisWaiting=ivf.getBasicWaitingPeriod();
		String basisDed=ivf.getBasicSubjectDeductible();
		
		String majorPecentage=ivf.getMajorPercentage().replace("$", "");
		String majorWaiting=ivf.getMajorWaitingPeriod();
		String majorDed=ivf.getMajorSubjectDeductible();
		
		
		Map<Integer, List<CodeValueDto>>  map = new HashMap<>();
		if (checkType == check_type_waiting ) {
			if (preventiveWaiting.equalsIgnoreCase("yes")) {
				map.put(preventive_precentage_type,java.util.Arrays.asList(getPreventiveValueFromIv(ivf,tpList)));
			}
			if (basisWaiting.equalsIgnoreCase("yes")) {
				map.put(basic_precentage_type,java.util.Arrays.asList(getBasicValueFromIv(ivf,tpList)));
			}
			if (majorWaiting.equalsIgnoreCase("yes")) {
				map.put(major_precentage_type,java.util.Arrays.asList(getMajorValueFromIv(ivf,tpList)));
			}
			
		}else 	if (checkType == check_type_ded ) {
			if (preventiveDed.equalsIgnoreCase("yes")) {
				map.put(preventive_precentage_type,java.util.Arrays.asList(getPreventiveValueFromIv(ivf,tpList)));
				
			}
			if (basisDed.equalsIgnoreCase("yes")) {
				map.put(basic_precentage_type,java.util.Arrays.asList(getBasicValueFromIv(ivf,tpList)));
			}
			if (majorDed.equalsIgnoreCase("yes")) {
				map.put(major_precentage_type,java.util.Arrays.asList(getMajorValueFromIv(ivf,tpList)));
			}
		}
		map.entrySet().stream()
		      .forEach(e -> {
		    	  e.getValue().forEach(x ->{
		    		if (checkType == check_type_waiting) {
		    			//Look for Codes in TP
		    			Optional<Object> fiter=	tpList.stream().filter( cd ->
		    			((CommonDataCheck) cd).getServiceCode().equals(x.getCode())).findFirst();
		    			if (!fiter.isPresent()) {
		    				return;
		    			}
		    		}
		    		//if Percentage Zero no need to check
		    		if ( x.getValue()==null || ( x.getValue()!=null &&  x.getValue().equals("0"))) {
		    			return;
		    		}
			    	final  TPValidationResponseDto dtoF=  isPrecentageByTypeSame(e.getKey(), preventivePercentage, basisPercentage, majorPecentage, checkType, x.getCode(),
				        		 x.getValue(), messageSource, rule, errorMessageKey);
			    	if (dtoF!=null) {
			    		list.add(dtoF);
			    	}
		      });
	        
	        });
		}catch(Exception ex) {
			list.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { ex.getMessage() }, locale),
					Constants.FAIL, "", "", ""));
			
		}
		return list;
	}
	
	private static TPValidationResponseDto isPrecentageByTypeSame(int percentageType,String preventivePercentage,String basicPercentage,
			                        String majorPecentage,int checkType,String code,String value,
			                        MessageSource messageSource,Rules rule,String errorMessageKey
			                        ) {
		
		TPValidationResponseDto dto = null;
		if( checkType == check_type_ded || checkType == check_type_waiting) {
			
			  if (percentageType == preventive_precentage_type ) {
				  if (!preventivePercentage.equalsIgnoreCase(value)) {
					  //Error build Message
					  if (basicPercentage.equalsIgnoreCase(value)) {
						dto=new TPValidationResponseDto(rule.getId(), rule.getName(), messageSource.getMessage(
								errorMessageKey,
								new Object[] { },
								locale), Constants.FAIL, "", "", "");
					  }else  if (majorPecentage.equalsIgnoreCase(value)) {
						  dto=new TPValidationResponseDto(rule.getId(), rule.getName(), messageSource.getMessage(
									errorMessageKey,
									new Object[] { },
									locale), Constants.FAIL, "", "", "");
					  }else {
						  dto=new TPValidationResponseDto(rule.getId(), rule.getName(), messageSource.getMessage(
									errorMessageKey,
									new Object[] { },
									locale), Constants.FAIL, "", "", "");
					  }
				  }
			}else if (percentageType == major_precentage_type ) {
				if (!majorPecentage.equalsIgnoreCase(value)) {
					  //Error build Message
					  if (basicPercentage.equalsIgnoreCase(value)) {
						dto=new TPValidationResponseDto(rule.getId(), rule.getName(), messageSource.getMessage(
								errorMessageKey,
								new Object[] { },
								locale), Constants.FAIL, "", "", "");
					  }else  if (preventivePercentage.equalsIgnoreCase(value)) {
						  dto=new TPValidationResponseDto(rule.getId(), rule.getName(), messageSource.getMessage(
									errorMessageKey,
									new Object[] { },
									locale), Constants.FAIL, "", "", "");
					  }else {
						  dto=new TPValidationResponseDto(rule.getId(), rule.getName(), messageSource.getMessage(
									errorMessageKey,
									new Object[] { },
									locale), Constants.FAIL, "", "", "");
					  }
				  }
				
			}else if (percentageType == basic_precentage_type ) {
				if (!basicPercentage.equalsIgnoreCase(value)) {
					  //Error build Message
					  if (majorPecentage.equalsIgnoreCase(value)) {
						dto=new TPValidationResponseDto(rule.getId(), rule.getName(), messageSource.getMessage(
								errorMessageKey,
								new Object[] { },
								locale), Constants.FAIL, "", "", "");
					  }else  if (preventivePercentage.equalsIgnoreCase(value)) {
						  dto=new TPValidationResponseDto(rule.getId(), rule.getName(), messageSource.getMessage(
									errorMessageKey,
									new Object[] { },
									locale), Constants.FAIL, "", "", "");
					  }else {
						  dto=new TPValidationResponseDto(rule.getId(), rule.getName(), messageSource.getMessage(
									errorMessageKey,
									new Object[] { },
									locale), Constants.FAIL, "", "", "");
					  }
				  }
			}
		}
		return dto; 
	}
	

	private static CodeValueDto[] getPreventiveValueFromIv(IVFTableSheet ivf,List<Object> tpList) {
		
		return new CodeValueDto[] { 
				        new CodeValueDto("D0120", ivf.getD0120()),
		        		new CodeValueDto("D0140", ivf.getD0140()),
		        		new CodeValueDto("D0150",ivf.getD0150()),
		        		new CodeValueDto("D0220", ivf.getpAXRaysPercentage()),
        				new CodeValueDto("D0230", ivf.getpAXRaysPercentage()),
        				new CodeValueDto("D0210", ivf.getFmxPer()),
        				new CodeValueDto("D0330",ivf.getPano1()),
        				new CodeValueDto("D0272",ivf.getBwx()),
        				new CodeValueDto("D0274",ivf.getBwx()),
        				new CodeValueDto("D1120",ivf.getD1120()),
        				new CodeValueDto("D1110", ivf.getD1110()),
        				new CodeValueDto("D1206",ivf.getD1206()),
        				new CodeValueDto("D1208",ivf.getD1208()),
        				new CodeValueDto("D1330", ivf.getD1330()),
        				new CodeValueDto("D1351", ivf.getSealantsD1351Percentage()),
        				new CodeValueDto("D9310",ivf.getD9310Percentage())
        		
        		};
	}
	
	private static CodeValueDto[] getBasicValueFromIv(IVFTableSheet ivf,List<Object> tpList) {
			
			return new CodeValueDto[] {  
					new CodeValueDto("D4341", ivf.getsRPD4341Percentage()),
					new CodeValueDto("D4346",ivf.getGingivitisD4346Percentage()),
					new CodeValueDto("D4910",ivf.getPerioMaintenanceD4910Percentage()),
					new CodeValueDto("D4381",ivf.getD4381()),
					new CodeValueDto("D4249",ivf.getCrownLengthD4249Percentage()),
					new CodeValueDto("D0431",ivf.getD0431()),
					new CodeValueDto("D4999",ivf.getD4999()),
					new CodeValueDto("D9630",ivf.getD9630()),
					new CodeValueDto("D4921",ivf.getPerioD4921()),
					new CodeValueDto("D4266",ivf.getPerioD4266()),
					new CodeValueDto("D2391",ivf.getPostCompositesD2391Percentage()),
					new CodeValueDto("D7111",ivf.getExtractionsMinorPercentage()),
					new CodeValueDto("D7140",ivf.getExtractionsMinorPercentage()),
					new CodeValueDto("D7210",ivf.getExtractionsMajorPercentage()),
					new CodeValueDto("D7220",ivf.getExtractionsMajorPercentage()),
					new CodeValueDto("D7230",ivf.getExtractionsMajorPercentage()),
					new CodeValueDto("D7240",ivf.getExtractionsMajorPercentage()),
					new CodeValueDto("D7250",ivf.getD7250()),
					new CodeValueDto("D3310",ivf.getD3310()),
					new CodeValueDto("D3320",ivf.getD3310()),
					new CodeValueDto("D3330",ivf.getD3310()),
					new CodeValueDto("D3220",ivf.getD3320()),
					new CodeValueDto("D9944",ivf.getNightGuardsD9940Percentage()),
					new CodeValueDto("D9945",ivf.getNightGuardsD9940Percentage()),
					new CodeValueDto("D9230",ivf.getNitrousD9230Percentage()),
					new CodeValueDto("D9248",ivf.getiVSedationD9248Percentage()),
					new CodeValueDto("D9910",ivf.getPerioD9910())
					};
		}
	
	private static CodeValueDto[] getMajorValueFromIv(IVFTableSheet ivf,List<Object> tpList) {
		
		return new CodeValueDto[] { 
				new CodeValueDto("D2950",ivf.getBuildUpsD2950Covered()),
				new CodeValueDto("D2740",ivf.getCrownsD2750D2740Percentage()),
				new CodeValueDto("D2750",ivf.getD2750()),
				new CodeValueDto("D2954",ivf.getD2954()),
				new CodeValueDto("D6245",ivf.getBridges1()),
				new CodeValueDto("D6740",ivf.getBridges1()),
				new CodeValueDto("D5110",ivf.getD511020Percentage()),
				new CodeValueDto("D5120",ivf.getD511020Percentage()),
				new CodeValueDto("D5130",ivf.getD513040Percentage()),
				new CodeValueDto("D5140",ivf.getD513040Percentage()),
				new CodeValueDto("D5820",ivf.getD5810CPercentage()),
				new CodeValueDto("D5821",ivf.getD5810CPercentage()),
				new CodeValueDto("D5213",ivf.getD5213142625()),
				new CodeValueDto("D5214",ivf.getD5213142625()),
				new CodeValueDto("D525",ivf.getD5213142625()),
				new CodeValueDto("D526",ivf.getD5213142625()),
				new CodeValueDto("D525",ivf.getD5213142625()),
				new CodeValueDto("D6010",ivf.getImplantCoverageD6010Percentage()),
				new CodeValueDto("D6065",ivf.getImplantSupportedPorcCeramicD6065Percentage()),
				new CodeValueDto("D7310",ivf.getD7310()),
				new CodeValueDto("D7311",ivf.getD7311()),
				new CodeValueDto("D7953",ivf.getD7953())
	    		};
}
             
}
